use("home_inventory");

// 1. CONSULTAS COM PROJEÇÃO (3 consultas)
// Mostra só os campos que precisamos na tela

// 1.1 Exibe só o título e a data das listas de compras
db.shopping_lists.find(
  {},
  { title: 1, date: 1, _id: 0 }
);

// 1.2  Retorna só a data e o valor total gasto nas compras
db.inventory.find(
  {},
  { productId: 1, quantity: 1, _id: 0 }
);

// 1.3  Lista só o mercado, usuário e total pago
db.transactions.find(
  {},
  { amount: 1, date: 1, _id: 0 }
);


// 2. MÚLTIPLAS CONDIÇÕES (2 consultas)

// 2.1 Filtra o que tá com estoque baixo e  que vence até o fim de 2027
db.inventory.find({
  $and: [
    { quantity: { $lte: 3 } },
    { expirationDate: { $lte: ISODate("2027-12-31T23:59:59Z") } }
  ]
});

// 2.2 Busca transações maiores que 30 reais E feitas de outubro de 2026 em diante
db.transactions.find({
  $and: [
    { amount: { $gt: 30.00 } },
    { date: { $gte: ISODate("2026-10-01T00:00:00Z") } }
  ]
});

// 3. ARRAYS, EMBEDDED DOCUMENTS E ORDENAÇÃO


// 3.1 Procura listas que tenham algum item com quantidade maior ou igual a 5
db.shopping_lists.find({
  "items.quantityNeeded": { $gte: 5 }
});

// 3.2 Procura compras com pelo menos um item que custou mais de 8 reais
db.purchases.find({
  purchasedItems: {
    $elemMatch: { unitPrice: { $gt: 8.00 } }
  }
});

// 3.3 Ordena todas as transações da mais cara para a mais barata
db.transactions.find().sort({ amount: -1 });

// 3.4 Pega o título e os itens das listas, ordenando pelas mais recentes
db.shopping_lists.find(
  {},
  { title: 1, items: 1, date: 1, _id: 0 }
).sort({ date: -1 });
