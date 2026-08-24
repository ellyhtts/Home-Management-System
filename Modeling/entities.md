## 1. Levantamento de entidades.

- **User** (Usuário)
- **Product** (Produto)
- **Category** (Categoria)
- **Inventory** (Estoque)
- **ShoppingList** (Lista de Compras)
- **ShoppingListItem** (Item da Lista)
- **Market** (Mercado / Estabelecimento)
- **Purchase** (Compra)
- **PurchaseItem** (Item da Compra)
- **Budget** (Orçamento)
- **Transaction** (Transação Financeira)
- **ActivityLog** (Log de Atividade)

## 2.Descrição das entidades.
| Entidade | Descrição | Responsabilidade no sistema |
|---|---|---|
| **User** (Usuário) | Indivíduo da casa com acesso à plataforma. | Autenticar o acesso e registrar os responsáveis pelas despesas ou alterações no sistema. |
| **Product** (Produto) | Catálogo base com dados fixos dos itens do mercado. | Padronizar as informações dos itens. |
| **Category** (Categoria) | Classificação macro do produto. | Organizar o catálogo e facilitar filtros (ex: Laticínios, Limpeza). |
| **Inventory** (Estoque) | Representação física dos produtos guardados em casa. | Controlar o volume atual dos itens e disparar alertas de escassez. |
| **ShoppingList** (Lista de Compras) | Agrupamento de itens que estão em falta. | Planejar a ida ao supermercado e organizar as necessidades de reposição. |
| **ShoppingListItem** (Item da Lista) | Produto específico vinculado à lista de compras. | Informar a quantidade necessária de um produto específico para a reposição. |
| **Market** (Mercado) | Estabelecimento físico ou online de compras. | Manter o cadastro de locais frequentes para comparar o histórico de preços. |
| **Purchase** (Compra) | Registro oficial da visita ao supermercado. | Consolidar a compra, registrando a data, o local e o valor total gasto. |
| **PurchaseItem** (Item da Compra) | Detalhe individual do que foi colocado no carrinho. | Gravar o preço unitário pago e a quantidade real adquirida no momento da compra. |
| **Budget** (Orçamento) | Meta financeira estabelecida para a residência. | Limitar o teto de gastos disponível mensalmente para compras e despensa. |
| **Transaction** (Transação) | O pagamento gerado por uma compra. | Registrar a saída do dinheiro e o método de pagamento utilizado. |
| **ActivityLog** (Log de Atividade) | Histórico de ações realizadas dentro da plataforma. | Rastrear ações importantes (quem deletou, editou ou comprou algo) para auditoria e segurança. |

## 3.Levantamento dos atributos

## 4. Levantamento dos relacinamentos.

## 5. Documentos incorporados.

## 6. Justificativa das decisões tomadas.
