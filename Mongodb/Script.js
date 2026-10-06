use("home_inventory_db")
// VALIDAÇÃO DA MÁQUINA:
// switched to db home_inventory_db

db.categories.insertMany([
  { name: "Laticínios" },
  { name: "Limpeza" }
])
// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedIds: {
//     '0': ObjectId('6ac3f936eaf906fd1e23a317'),
//     '1': ObjectId('6ac3f936eaf906fd1e23a318')
//   }
// }

db.products.insertMany([
  { name: "Leite Integral 1L", categoryId: db.categories.findOne({ name: "Laticínios" })._id },
  { name: "Sabão em Pó 1kg", categoryId: db.categories.findOne({ name: "Limpeza" })._id }
])

// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedIds: {
//     '0': ObjectId('6ac3f944eaf906fd1e23a319'),
//     '1': ObjectId('6ac3f944eaf906fd1e23a31a')
//   }
// }

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
// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedId: ObjectId('6ac3f94eeaf906fd1e23a31b')
// }

db.inventory.insertMany([
  { productId: db.products.findOne({ name: "Leite Integral 1L" })._id, quantity: 4, expirationDate: ISODate("2026-11-10") },
  { productId: db.products.findOne({ name: "Sabão em Pó 1kg" })._id, quantity: 2, expirationDate: ISODate("2028-05-15") }
])
// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedIds: {
//     '0': ObjectId('6ac3f959eaf906fd1e23a31c'),
//     '1': ObjectId('6ac3f959eaf906fd1e23a31d')
//   }
// }

db.markets.insertOne({
  name: "Atacadão",
  location: "Av. Contorno, Feira de Santana"
})

db.shopping_lists.insertOne({
  title: "Reposição Mensal de Supermercado",
  date: ISODate("2026-10-01"),
  items: [
    { productId: db.products.findOne({ name: "Leite Integral 1L" })._id, quantityNeeded: 6 },
    { productId: db.products.findOne({ name: "Sabão em Pó 1kg" })._id, quantityNeeded: 2 }
  ]
})

// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedId: ObjectId('6ac3f962eaf906fd1e23a31f')
// }

db.purchases.insertOne({
  marketId: db.markets.findOne({ name: "Atacadão" })._id,
  date: ISODate("2026-10-02"),
  purchasedItems: [
    { productId: db.products.findOne({ name: "Leite Integral 1L" })._id, unitPrice: 5.49 },
    { productId: db.products.findOne({ name: "Sabão em Pó 1kg" })._id, unitPrice: 12.90 }
  ]
})
// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedId: ObjectId('6ac3f96deaf906fd1e23a320')
// }

db.budgets.insertOne({
  month: "Outubro/2026",
  limitAmount: 800.00
})

// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedId: ObjectId('6ac3f973eaf906fd1e23a321')
// }

db.transactions.insertOne({
  purchaseId: db.purchases.findOne()._id,
  amount: 45.84,
  date: ISODate("2026-10-02")
})

// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedId: ObjectId('6ac3f97aeaf906fd1e23a322')
// }


db.activity_logs.insertOne({
  userId: db.users.findOne({ name: "Maria Fabiana" })._id,
  action: "Criou a lista de compras de Outubro",
  timestamp: ISODate("2026-10-01T10:30:00Z")
})

// VALIDAÇÃO DA MÁQUINA:
// {
//   acknowledged: true,
//   insertedId: ObjectId('6ac3f980eaf906fd1e23a323')
// }
