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
### **User** (Usuário da casa)
 
├── `_id` (Identificador único)
 
├── `name` (Nome do usuário)
 
├── `email` (E-mail para login)
 
├── `password` (Senha de acesso)
 
└── `addressUser` (Endereço do usuario)  	
 
        	├── `city` (Cidade do usuário)
 
        	├── `district` (Bairro do usuário)
 
        	├── `street` (Rua do usuário)
 
        	└── `houseNumber` (Número da casa do usuário)
 

### **Product** (Produto - Catálogo Base)
 
├── `_id` (Identificador único)
 
├── `name` (Nome do produto, ex: Leite Integral)
 
└── `categoryId` (Referência à Categoria do produto)
 
 
 
### **Category** (Categoria para organização)
 
├── `_id` (Identificador único)
 
└── `name` (Nome da categoria, ex: Laticínios, Limpeza)
 
 
 
### **Inventory** (Estoque Físico armazenado em casa)
 
├── `_id` (Identificador único)
 
├── `productId` (Referência ao Produto cadastrado)
 
├── `quantity` (Quantidade atual disponível na despensa)
 
└── `expirationDate` (Data de validade do lote atual)
 
 
 
### **ShoppingList** (Lista de Compras para ir ao mercado)
 
├── `_id` (Identificador único)
 
├── `title` (Nome da lista, ex: Compras de Agosto)
 
├── `date` (Data em que a lista foi criada)
 
└── `items` (Array contendo a entidade 'ShoppingListItem')
 
 	├── `productId` (Referência ao Produto que precisa ser comprado)
 
	└── `quantityNeeded` (Quantidade necessária)
 
 
 
### **Market** (Mercado ou Estabelecimento)
 
├── `_id` (Identificador único)
 
├── `name` (Nome do mercado, ex: Assaí, Atacadão)
 
└── `location` (Endereço ou bairro do estabelecimento)
 
 
 
### **Purchase** (Registro da Compra Realizada)
 
├── `_id` (Identificador único)
 
├── `marketId` (Referência ao Mercado onde a compra foi feita)
 
├── `date` (Data exata da ida ao mercado)
 
└── `purchasedItems` (Array contendo a entidade 'PurchaseItem')
 
	 ├── `productId` (Referência ao Produto que foi efetivamente comprado)
 
	 └── `unitPrice` (Preço unitário de cada produto)
 
 
 
 
### **Budget** (Orçamento ou Teto de Gastos)
 
├── `_id` (Identificador único)
 
├── `month` (Mês de referência, ex: Agosto)
 
└── `limitAmount` (Valor máximo que a família decidiu gastar)
 
 
 
### **Transaction** (Transação Financeira / Pagamento)
 
├── `_id` (Identificador único)
 
├── `purchaseId` (Referência à Compra que gerou este gasto)
 
├── `amount` (Valor total efetivamente pago)
 
└── `date` (Data em que o pagamento foi registrado)
 
 
 
### **ActivityLog** (Log de Atividades do Sistema)
 
├── `_id` (Identificador único)
 
├── `userId` (Referência ao Usuário que fez a ação)
 
├── `action` (Descrição da ação, ex: "Marcou item como comprado")
 
└── `timestamp` (Data e hora exata em que a ação ocorreu)

## 4. Levantamento dos relacinamentos.
### **A. Relacionamentos por Embutimento (Embedded Documents)**
**ShoppingList** ➔ `items` (Array de ShoppingListItem): Os itens necessários para ir ao mercado ficam aninhados diretamente na lista de compras.
 
**Purchase** ➔ `purchasedItems` (Array de PurchaseItem): Os produtos e preços unitários efetivamente adquiridos ficam guardados dentro do registro da compra.

 **User** ➔ `adressUser` (Endereço): O endereço pertence exclusivamente ao perfil do usuário (relação 1 para 1) e é um objeto interno do documento.

### **B. Relacionamentos por Referência (References / Normalized)**
 **Product** ➔ **Category** (`categoryId`): O produto armazena apenas o ID da categoria a que pertence.

 **Inventory** ➔ **Product** (`productId`): O estoque físico da despensa aponta para o produto base do catálogo via ID, evitando duplicar dados a cada lote novo.

**Purchase** ➔ **Market** (`marketId`): O registro de compra aponta para o estabelecimento onde a transação ocorreu usando o ID.

**Transaction** ➔ **Purchase** (`purchaseId`): A transação financeira de pagamento aponta diretamente para a compra que a originou.
 
**ActivityLog** ➔ **User** (`userId`): O log de atividade guarda o ID do usuário que executou a ação, mantendo uma coleção isolada.

### **C. Relacionamentos Lógicos / Dinâmicos**
 **Budget** ➔ **Transaction** (Por Período/Mês): O orçamento define um limite (`limitAmount`) para um determinado `month`. A aplicação faz o cruzamento dinâmico somando as transações (Transaction) cuja data corresponda àquele mês de referência, sem restrição de ID físico fixo.

## 5. Documentos incorporados.
**Documentos Incorporados (Embedded):**  Utilizados quando os dados secundários pertencem exclusivamente ao documento pai, são consultados ao mesmo tempo  na maioria dos casos de uso e possuem crescimento previsível e limitado (1 para 1  ou 1 para poucos), ideal para informações que nascem e morrem com o registro principal, como os itens de uma mesma compra.

**Coleções Separadas (collections):**  Utilizadas para entidades independentes que são armazenadas de forma separada e conectadas apenas por um código de identificação. É ideal para informações que crescem sem parar ou que precisam ser reutilizadas em várias partes do sistema, como o cadastro de usuários ou produtos.


## 6. Justificativa das decisões tomadas.
| Entidade | Decisão | Justificativa  |
|---|---|---|
| **User** (Usuário) |Collections separadas | O usuário é o centro do sistema,  o ID do usuário precisa ser apontado por varias  partes do banco. Se estivesse  dentro de uma tarefa, o restante do sistema não conseguiria acessá-lo.  |
| **Product** (Produto) | Collections separadas | O produto precisa existir no sistema mesmo que ele não esteja em nenhuma lista de compras e  precisa de uma tabela central para gerenciar e controlar  os itens. |
| **Category** (Categoria) | Collections separadas | mantém uma base padronizada e organizada no sistema, evitando que os produtos sejam cadastrados com divergências de nomes(ex: limpeza e produtos de limpeza). |
| **Inventory** (Estoque) | Collections separadas  | Ele lida com o controle de diversos lotes com diferentes datas de validade (expirationDate) para o mesmo produto, separar a coleção permite que ele faça buscas rápidas no catálogo de produtos. |
| **ShoppingList** (Lista de Compras) | Collections separadas | Manter uma coleção independente permite acumular o histórico de compras da casa ao longo dos meses sem lotar outros documentos, evitando estourar o limite de tamanho do banco de dados.  |
| **ShoppingListItem** (Item da Lista) | Embedded | O item não existe sozinho, ele só faz sentido enquanto estiver dentro de uma lista específica. Se a lista for apagada, os itens somem com ela. |
| **Market** (Mercado) | Collections separadas | O mercado é uma entidade que existe sem depender de compras ativa ou não, e  precisa ser cadastrado apenas uma vez no sistema (ex: Supermercado Félix ou Atacadão) para ficar disponível para toda a família. |
| **Purchase** (Compra) | Collections separadas | Uma compra é um registro financeiro e logístico único (ex: "Compra do Mês de Abril"). E  precisa ser armazenada de forma isolada para não poluir outros documentos do sistema. |
| **PurchaseItem** (Item da Compra) | Embedded | O item comprado não possui independência fora daquela transação específica. Se o registro daquela compra for excluído, os itens vinculados a ela perdem o sentido e deixam de existir automaticamente. |
| **Budget** (Orçamento) | Collections separadas | Mantém metas de gastos mensais/anuais para análises financeiras e gestão do lar, mantendo em uma coleção própria para controle em tempo real. |
| **Transaction** (Transação) | Collections separadas | Registro de fluxo de entrada e saída, dinheiro. Mantê-la em uma coleção própria permite que o sistema filtre e gere relatórios de gastos do lar. |
| **ActivityLog** (Log de Atividade) | Collections separadas | Logs são gerados a cada clique ou alteração no sistema. Mantê-los em uma coleção própria garante que esse fluxo não atrapalhe a performance das tabelas principais. |
