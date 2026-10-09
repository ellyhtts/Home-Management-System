use("home_inventory");

function mostrar(titulo, dados) {
  print("\n=== " + titulo + " ===");
  printjson(dados);
}

const num = (v) => Number(v.toString());


// 1. SCHEMA FLEXÍVEL

const catAlimentos = db.categories.findOne({ name: "Alimentos" })._id;

db.products.updateOne(
  { name: "Café Torrado 500g" },
  { $setOnInsert: { categoryId: catAlimentos } },
  { upsert: true }
);

db.products.updateOne(
  { name: "Óleo de Soja 900ml" },
  { $setOnInsert: { categoryId: catAlimentos, discount: NumberInt(10) } },
  { upsert: true }
);

mostrar(
  "1.1 Produtos com e sem discount",
  db.products.find({ name: { $in: ["Café Torrado 500g", "Óleo de Soja 900ml"] } }).toArray()
);

mostrar(
  "1.2 Produtos com discount",
  db.products.find({ discount: { $exists: true } }, { name: 1, discount: 1, _id: 0 }).toArray()
);

mostrar(
  "1.3 Produtos sem discount",
  db.products.find({ discount: { $exists: false } }, { name: 1, _id: 0 }).toArray()
);

mostrar(
  "1.4 Desconto efetivo (0 quando ausente)",
  db.products.aggregate([
    { $project: { _id: 0, name: 1, descontoPercentual: { $ifNull: ["$discount", 0] } } }
  ]).toArray()
);

// Análise do schema flexível:
// - Necessária? Sim: desconto é eventual, não vale gravar "discount: 0" em todos os produtos.
// - Ajuda ou prejudica? Ajuda a evoluir o schema sem migração, mas exige tratar a ausência do campo.
// - Mais consistência? Para campos essenciais (name, categoryId) sim; para opcionais, a flexibilidade basta.


// 2. REFERÊNCIAS

// 2.1 Junção manual: Product - Category
const arroz = db.products.findOne({ name: "Arroz Integral 1kg" });
const catDoArroz = db.categories.findOne({ _id: arroz.categoryId });
mostrar("2.1 Junção manual: produto e categoria", {
  produto: arroz.name,
  categoria: catDoArroz.name
});

// 2.2 Product - Category

mostrar("2.2 Produtos com categoria",
  db.products.aggregate([
    { $lookup: { from: "categories", localField: "categoryId", foreignField: "_id", as: "categoria" } },
    { $unwind: "$categoria" },
    { $project: { _id: 0, produto: "$name", categoria: "$categoria.name" } },
    { $sort: { categoria: 1, produto: 1 } }
  ]).toArray()
);

// 2.3 Inventory - Product - Category

mostrar("2.3 Estoque com produto e categoria",
  db.inventory.aggregate([
    { $lookup: { from: "products", localField: "productId", foreignField: "_id", as: "produto" } },
    { $unwind: "$produto" },
    { $lookup: { from: "categories", localField: "produto.categoryId", foreignField: "_id", as: "categoria" } },
    { $unwind: "$categoria" },
    { $project: {
        _id: 0,
        produto: "$produto.name",
        categoria: "$categoria.name",
        quantidade: "$quantity",
        validade: "$expirationDate"
    } },
    { $sort: { validade: 1 } }
  ]).toArray()
);

// 2.4 Purchase - Market

mostrar("2.4 Compras com mercado",
  db.purchases.aggregate([
    { $lookup: { from: "markets", localField: "marketId", foreignField: "_id", as: "mercado" } },
    { $unwind: "$mercado" },
    { $project: { _id: 0, data: "$date", mercado: "$mercado.name", local: "$mercado.location" } }
  ]).toArray()
);

// 2.5 Transaction - Purchase - Market

mostrar("2.5 Transações com compra e mercado",
  db.transactions.aggregate([
    { $lookup: { from: "purchases", localField: "purchaseId", foreignField: "_id", as: "compra" } },
    { $unwind: "$compra" },
    { $lookup: { from: "markets", localField: "compra.marketId", foreignField: "_id", as: "mercado" } },
    { $unwind: "$mercado" },
    { $project: { _id: 0, valorPago: "$amount", dataPagamento: "$date", mercado: "$mercado.name" } }
  ]).toArray()
);

// 2.6 ActivityLog - User 

if (db.activity_logs.countDocuments() === 0) {
  const usuario = db.users.findOne({ email: "maria@email.com" });
  db.activity_logs.insertMany([
    { userId: usuario._id, action: "Criou a lista de compras 'Reposição Mensal de Supermercado'", timestamp: ISODate("2026-10-01T09:15:00Z") },
    { userId: usuario._id, action: "Registrou a compra no Atacadão",                              timestamp: ISODate("2026-10-02T14:30:00Z") },
    { userId: usuario._id, action: "Atualizou o estoque de Arroz Integral 1kg",                   timestamp: ISODate("2026-10-03T08:00:00Z") }
  ]);
}

mostrar("2.6 Logs com nome do usuário",
  db.activity_logs.aggregate([
    { $lookup: { from: "users", localField: "userId", foreignField: "_id", as: "usuario" } },
    { $unwind: "$usuario" },
    { $project: { _id: 0, quem: "$usuario.name", acao: "$action", quando: "$timestamp" } },
    { $sort: { quando: -1 } }
  ]).toArray()
);


// 3. EMBEDDED + REFERÊNCIA

// 3.1 Itens da compra com produto e subtotal 

mostrar("3.1 Itens da compra",
  db.purchases.aggregate([
    { $unwind: "$purchasedItems" },
    { $lookup: { from: "products", localField: "purchasedItems.productId", foreignField: "_id", as: "produto" } },
    { $unwind: "$produto" },
    { $project: {
        _id: 0,
        compraId: "$_id",
        data: "$date",
        produto: "$produto.name",
        precoUnitario: "$purchasedItems.unitPrice",
        quantidade: { $ifNull: ["$purchasedItems.quantity", 1] },
        subtotal: { $multiply: ["$purchasedItems.unitPrice", { $ifNull: ["$purchasedItems.quantity", 1] }] }
    } }
  ]).toArray()
);

// 3.2 Total dos itens x transações da compra

mostrar("3.2 Total dos itens x transação",
  db.purchases.aggregate([
    { $addFields: {
        totalItens: { $sum: { $map: {
          input: "$purchasedItems", as: "i",
          in: { $multiply: ["$$i.unitPrice", { $ifNull: ["$$i.quantity", 1] }] }
        } } }
    } },
    { $lookup: { from: "transactions", localField: "_id", foreignField: "purchaseId", as: "transacoes" } },
    { $project: {
        _id: 0,
        data: "$date",
        totalItens: 1,
        qtdTransacoes: { $size: "$transacoes" },
        valorPagoPrimeira: { $arrayElemAt: ["$transacoes.amount", 0] }
    } }
  ]).toArray()
);

// 3.3 Lista de compras x estoque

mostrar("3.3 Lista de compras x estoque",
  db.shopping_lists.aggregate([
    { $unwind: "$items" },
    { $lookup: { from: "products", localField: "items.productId", foreignField: "_id", as: "produto" } },
    { $unwind: "$produto" },
    { $lookup: { from: "inventory", localField: "items.productId", foreignField: "productId", as: "estoque" } },
    { $project: {
        _id: 0,
        lista: "$title",
        produto: "$produto.name",
        precisaComprar: "$items.quantityNeeded",
        emEstoque: { $sum: "$estoque.quantity" },
        lotes: { $size: "$estoque" }
    } }
  ]).toArray()
);

// 3.4 Histórico de preços por produto

mostrar("3.4 Preço médio, mínimo e máximo por produto",
  db.purchases.aggregate([
    { $unwind: "$purchasedItems" },
    { $group: {
        _id: "$purchasedItems.productId",
        precoMedio: { $avg: "$purchasedItems.unitPrice" },
        precoMin: { $min: "$purchasedItems.unitPrice" },
        precoMax: { $max: "$purchasedItems.unitPrice" },
        vezesComprado: { $sum: 1 }
    } },
    { $lookup: { from: "products", localField: "_id", foreignField: "_id", as: "produto" } },
    { $unwind: "$produto" },
    { $project: { _id: 0, produto: "$produto.name", precoMedio: 1, precoMin: 1, precoMax: 1, vezesComprado: 1 } }
  ]).toArray()
);

// 4. BUDGET - TRANSACTION 

const orcamento = db.budgets.findOne({ month: "Outubro/2026" });
const inicio = ISODate("2026-10-01T00:00:00Z");
const fim    = ISODate("2026-11-01T00:00:00Z");

const resumo = db.transactions.aggregate([
  { $match: { date: { $gte: inicio, $lt: fim } } },
  { $group: { _id: null, totalGasto: { $sum: "$amount" }, qtdTransacoes: { $sum: 1 } } }
]).toArray()[0] || { totalGasto: 0, qtdTransacoes: 0 };

const limite = num(orcamento.limitAmount);
const gasto  = num(resumo.totalGasto);

mostrar("4.1 Orçamento x gastos do mês", {
  mes: orcamento.month,
  limite: limite,
  gasto: gasto,
  saldoRestante: Math.round((limite - gasto) * 100) / 100,
  percentualUsado: Math.round((gasto / limite) * 10000) / 100 + "%",
  qtdTransacoes: resumo.qtdTransacoes
});


// 5. INTEGRIDADE 

mostrar(
  "5.1 Produtos sem categoria válida",
  db.products.aggregate([
    { $lookup: { from: "categories", localField: "categoryId", foreignField: "_id", as: "cat" } },
    { $match: { cat: { $size: 0 } } },
    { $project: { _id: 0, name: 1 } }
  ]).toArray()
);

mostrar(
  "5.2 Estoque sem produto válido",
  db.inventory.aggregate([
    { $lookup: { from: "products", localField: "productId", foreignField: "_id", as: "p" } },
    { $match: { p: { $size: 0 } } },
    { $project: { _id: 1, productId: 1 } }
  ]).toArray()
);

mostrar(
  "5.3 Compras sem mercado válido",
  db.purchases.aggregate([
    { $lookup: { from: "markets", localField: "marketId", foreignField: "_id", as: "m" } },
    { $match: { m: { $size: 0 } } },
    { $project: { _id: 1 } }
  ]).toArray()
);

// 5.4 Compras com mais de uma transação

const duplicadas = db.transactions.aggregate([
  { $group: { _id: "$purchaseId", qtd: { $sum: 1 }, ids: { $push: "$_id" } } },
  { $match: { qtd: { $gt: 1 } } }
]).toArray();
mostrar("5.4 Compras com transação duplicada", duplicadas);

mostrar("5.5 Logs sem usuário válido",
  db.activity_logs.aggregate([
    { $lookup: { from: "users", localField: "userId", foreignField: "_id", as: "u" } },
    { $match: { u: { $size: 0 } } },
    { $project: { _id: 1 } }
  ]).toArray()
);