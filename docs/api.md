# API REST: Home Management System

Base URL: `http://localhost:8080/api`. Formato: JSON.

## Convenções de status

| Status | Uso |
|---|---|
| 200 | Consulta ou atualização com sucesso |
| 201 | Registro criado |
| 204 | Registro excluído |
| 400 | Dados inválidos ou obrigatórios ausentes |
| 404 | Registro ou referência inexistente |
| 409 | Conflito de negócio (duplicidade, exclusão com dependência, limite de orçamento) |

## Endpoints

| Recurso | Rotas |
|---|---|
| Categorias | `POST/GET /categories`, `GET/PUT/DELETE /categories/{id}` |
| Produtos | `POST/GET /products`, `POST /products/batch`, `GET /products/category/{categoryId}`, `GET/PUT/DELETE /products/{id}` |
| Usuários | `POST/GET /users`, `GET/PUT/DELETE /users/{id}` |
| Estoque | `POST/GET /inventories`, `GET /inventories/low-stock?max=N`, `GET/PUT/DELETE /inventories/{id}` |
| Logs de atividade | `POST/GET /activity-logs`, `GET/DELETE /activity-logs/{id}` |
| Mercados | `POST/GET /markets`, `GET/PUT/DELETE /markets/{id}` |
| Listas de compras | `POST/GET /shopping-lists`, `GET/PUT/DELETE /shopping-lists/{id}` |
| Compras | `POST/GET /purchases`, `GET/PUT/DELETE /purchases/{id}` |
| Orçamentos | `POST/GET /budgets`, `GET /budgets/month?month=Novembro/2026`, `GET/PUT/DELETE /budgets/{id}` |
| Transações | `POST/GET /transactions`, `GET /transactions/purchase/{purchaseId}`, `GET/PUT/DELETE /transactions/{id}` |

## Exemplos

### Criar categoria
`POST /api/categories`
```json
{ "name": "Alimentos" }
```
Resposta **201**: `{ "id": "...", "name": "Alimentos" }`

### Criar produtos em lote
`POST /api/products/batch`
```json
[
  { "name": "Arroz Integral 1kg", "category": { "id": "ID_CATEGORIA" } },
  { "name": "Feijão Carioca 1kg", "category": { "id": "ID_CATEGORIA" } }
]
```
Resposta **201** com a lista criada, se um item for inválido nada é gravado.

### Efetivar compra
`POST /api/purchases`
```json
{
  "user": { "id": "ID_USUARIO" },
  "market": { "id": "ID_MERCADO" },
  "date": "2026-11-14T10:00:00",
  "purchasedItems": [
    { "productId": "ID_PRODUTO_A", "unitPrice": 5.50, "quantity": 2 },
    { "productId": "ID_PRODUTO_B", "unitPrice": 7.00, "quantity": 3 }
  ]
}
```
Resposta **201**: inclui `"totalValue": 32.00`, calculado pelo servidor.

Efeitos automáticos:
1. Valida o limite do orçamento do mês da compra (409 se estourar).
2. Grava a compra.
3. Cria a `Transaction` com `amount = totalValue`.
4. Soma a quantidade no `Inventory` de cada produto.

Erros: **400** (sem itens, quantidade ou preço inválidos), **404** (usuário, mercado ou produto inexistente), **409** (orçamento estourado).

### Criar orçamento
`POST /api/budgets`
```json
{ "month": "Novembro/2026", "limitAmount": 800.00 }
```
Erros: **400** (formato do mês ou limite ≤ 0), **409** (mês já cadastrado).

### Criar transação
`POST /api/transactions`
```json
{ "purchase": { "id": "ID_COMPRA" }, "date": "2026-11-14T10:00:00" }
```
Se `amount` não for enviado, usa o `totalValue` da compra. Erros: **404** (compra inexistente), **409** (compra já tem transação, ou limite do mês estourado).

## Observações

- Em `PUT`, envie o objeto completo.
- A senha do usuário é aceita na criação, mas não é devolvida nas respostas.
- A coleção do Insomnia com todos os testes está em `insomnia-collection/`.
