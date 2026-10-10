use("home_inventory");

// 1. CONSULTAS COM PROJEÇÃO (3 consultas)


db.shopping_lists.find(
  {},
  { title: 1, date: 1, _id: 0 }
);


db.inventory.find(
  {},
  { productId: 1, quantity: 1, _id: 0 }
);


db.transactions.find(
  {},
  { amount: 1, date: 1, _id: 0 }
);


// 2. MÚLTIPLAS CONDIÇÕES (2 consultas)

db.inventory.find({
  $and: [
    { quantity: { $lte: 3 } },
    { expirationDate: { $lte: ISODate("2027-12-31T23:59:59Z") } }
  ]
});


db.transactions.find({
  $and: [
    { amount: { $gt: 30.00 } },
    { date: { $gte: ISODate("2026-10-01T00:00:00Z") } }
  ]
});

// 3. ARRAYS, EMBEDDED DOCUMENTS E ORDENAÇÃO


db.shopping_lists.find({
  "items.quantityNeeded": { $gte: 5 }
});


db.purchases.find({
  purchasedItems: {
    $elemMatch: { unitPrice: { $gt: 8.00 } }
  }
});


db.transactions.find().sort({ amount: -1 });


db.shopping_lists.find(
  {},
  { title: 1, items: 1, date: 1, _id: 0 }
).sort({ date: -1 });
