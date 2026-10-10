use("home_inventory")

db.categories.insertMany([
  { name: "Alimentos" },
  { name: "Limpeza" },
  { name: "Higiene Pessoal" }
])

db.products.insertMany([
  { name: "Arroz Integral 1kg", categoryId: db.categories.findOne({ name: "Alimentos" })._id },
  { name: "Feijão Carioca 1kg", categoryId: db.categories.findOne({ name: "Alimentos" })._id },
  { name: "Farinha de Mandioca 500g", categoryId: db.categories.findOne({ name: "Alimentos" })._id },
  { name: "Macarrão Espaguete 500g", categoryId: db.categories.findOne({ name: "Alimentos" })._id },
  { name: "Detergente Líquido 500ml", categoryId: db.categories.findOne({ name: "Limpeza" })._id },
  { name: "Sabão em Pó 1kg", categoryId: db.categories.findOne({ name: "Limpeza" })._id },
  { name: "Papel Higiênico 4 Rolos", categoryId: db.categories.findOne({ name: "Higiene Pessoal" })._id },
  { name: "Sabonete em Barra 90g", categoryId: db.categories.findOne({ name: "Higiene Pessoal" })._id }
])

db.users.insertOne({
  name: "Maria Fabiana",
  email: "maria@email.com",
  password: "hashed_password_segura",
  addressUser: {
    city: "Feira de Santana",
    district: "Centro",
    street: "Rua Exemplo",
    houseNumber: 123
  }
})

db.inventory.insertMany([
  { productId: db.products.findOne({ name: "Arroz Integral 1kg" })._id, quantity: 3, expirationDate: ISODate("2027-05-10") },
  { productId: db.products.findOne({ name: "Feijão Carioca 1kg" })._id, quantity: 2, expirationDate: ISODate("2027-02-15") },
  { productId: db.products.findOne({ name: "Detergente Líquido 500ml" })._id, quantity: 5, expirationDate: ISODate("2028-10-01") },
  { productId: db.products.findOne({ name: "Papel Higiênico 4 Rolos" })._id, quantity: 4, expirationDate: ISODate("2030-01-01") }
])

db.markets.insertOne({
  name: "Atacadão",
  location: "Av. Contorno, Feira de Santana"
})

db.shopping_lists.insertOne({
  title: "Reposição Mensal de Supermercado",
  date: ISODate("2026-10-01"),
  items: [
    { productId: db.products.findOne({ name: "Arroz Integral 1kg" })._id, quantityNeeded: 5 },
    { productId: db.products.findOne({ name: "Feijão Carioca 1kg" })._id, quantityNeeded: 3 },
    { productId: db.products.findOne({ name: "Farinha de Mandioca 500g" })._id, quantityNeeded: 2 },
    { productId: db.products.findOne({ name: "Macarrão Espaguete 500g" })._id, quantityNeeded: 4 },
    { productId: db.products.findOne({ name: "Sabonete em Barra 90g" })._id, quantityNeeded: 6 }
  ]
})


db.purchases.insertOne({
  marketId: db.markets.findOne({ name: "Atacadão" })._id,
  date: ISODate("2026-10-02"),
  purchasedItems: [
    { productId: db.products.findOne({ name: "Arroz Integral 1kg" })._id, unitPrice: 6.50 },
    { productId: db.products.findOne({ name: "Feijão Carioca 1kg" })._id, unitPrice: 8.90 },
    { productId: db.products.findOne({ name: "Detergente Líquido 500ml" })._id, unitPrice: 2.49 },
    { productId: db.products.findOne({ name: "Sabão em Pó 1kg" })._id, unitPrice: 12.90 }
  ]
})


db.budgets.insertOne({
  month: "Outubro/2026",
  limitAmount: 800.00
})


db.transactions.insertOne({
  purchaseId: db.purchases.findOne()._id,
  amount: 30.79,
  date: ISODate("2026-10-02")
})

