# Arquitetura

## Visão geral

API REST em camadas, com Java, Spring Boot e MongoDB.

```
Cliente (Insomnia)
      │ HTTP/JSON
      ▼
Controllers   → recebem a requisição e devolvem ResponseEntity com o status
      ▼
Services      → regras de negócio e integridade referencial
      ▼
Repositories  → MongoRepository (Spring Data MongoDB)
      ▼
MongoDB (banco home_inventory)
```

## Estrutura de pacotes

```
src/main/java/com/Home_Management_System/Aplication_Api/
├── controllers/   (endpoints REST)
├── services/      (regras de negócio)
├── repositories/  (acesso a dados)
├── models/        (documentos e objetos embutidos)
└── AplicationApiApplication.java
```

## Tecnologias

Java 25, Spring Boot 4.1, Spring Data MongoDB, MongoDB local, Maven (wrapper), JUnit 5 e Mockito.

## Banco de dados

Conexão em `application.properties`: `spring.data.mongodb.uri=mongodb://localhost:27017/home_inventory`.

| Collection | Entidade |
|---|---|
| `users`, `categories`, `products`, `inventory`, `markets`, `shopping_lists`, `purchases`, `budgets`, `transactions`, `activity_logs` | Documentos principais |

## Embedded vs. References

**Embedded** (sem `@Document`): `AddressUser` em `User`, `ShoppingListItem` em `ShoppingList.items` e `PurchaseItem` em `Purchase.purchasedItems`. Só fazem sentido dentro do documento pai, são lidos junto com ele e têm crescimento limitado.

**References** (`@DocumentReference`): Product→Category, Inventory→Product, Purchase→Market, Purchase→User, ShoppingList→User, Transaction→Purchase, ActivityLog→User. São entidades reutilizadas por muitos registros, e embutir duplicaria dados.

## Fluxo da compra

```
POST /purchases
├── valida itens, usuário, mercado e produtos (400/404)
├── calcula totalValue = Σ quantity × unitPrice
├── valida o limite do orçamento do mês (409)
├── grava a Purchase
├── cria a Transaction (1:1) e recalcula o spentAmount do Budget
├── soma a quantity no Inventory do produto
├── marca como comprados os itens das listas
└── registra a ação no ActivityLog
```

## Fluxo do estoque baixo

```
GET  /inventories/low-stock?max=N        → lista itens com quantity <= N
POST /shopping-lists/from-low-stock?max=N → adiciona esses produtos à
                                             "Lista Automática - Estoque Baixo"
                                             (sem duplicar itens pendentes)
Compra do produto → item da lista vira isPurchased = true e o estoque sobe
```

## Orçamento

O `Budget` guarda `month` e `limitAmount`, e agora também `spentAmount`, atualizado pelo sistema a partir das `Transaction` do mês. O `remainingAmount` (`limitAmount - spentAmount`) é calculado na leitura e não é persistido. A relação lógica Budget → Transaction continua sendo por período (mês), sem id fixo.

## Log de atividades

Os services de usuário e compra registram suas ações no `ActivityLogService`, formando um histórico de auditoria (quem fez o quê e quando).



## Testes

Testes unitários com Mockito em `src/test/.../services`: `PurchaseServiceTest` (6) e `TransactionServiceTest` (4). Os testes estão na coleção do Insomnia.
