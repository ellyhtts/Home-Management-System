# Documentação de Modelagem NoSQL - Sistema de Gerenciamento do Lar

Este documento detalha a arquitetura de dados adotada para o banco de dados MongoDB do Sistema de Gerenciamento do Lar. Ele explica as decisões de modelagem, os tipos de dados escolhidos e os relacionamentos entre as coleções, baseando-se nos princípios de design para bancos de dados orientados a documentos.

---

## 1. Estratégia de Identificadores Únicos (`_id`)

Para garantir a unicidade e a alta performance nas operações, o sistema utiliza o tipo nativo **`ObjectId`** para a chave primária (`_id`) de todos os documentos nas coleções principais. 

**Justificativa Técnica:**
* O `ObjectId` é gerado automaticamente pelo driver do MongoDB ou pelo servidor.
* Ele possui 12 bytes de tamanho, contendo informações de timestamp (data/hora de criação), o que permite ordenar documentos cronologicamente sem precisar criar um índice extra de data de criação na maioria dos casos.
* Garante escalabilidade horizontal, pois a probabilidade de colisão de IDs em sistemas distribuídos é praticamente nula.

---

## 2. Tipos de Dados Utilizados

O MongoDB utiliza BSON (Binary JSON), o que nos permite trabalhar com tipos de dados mais ricos e precisos do que o JSON tradicional. No projeto, utilizamos:

* **`ObjectId`**: Para chaves primárias (`_id`) e referências estrangeiras (ex: `productId`, `categoryId`, `userId`, `marketId`).
* **`String`**: Para textos, nomes, descrições e e-mails (ex: `name`, `title`, `email`).
* **`Int32`**: Para números inteiros, como a quantidade de itens no estoque ou o número da casa (ex: `quantity`, `houseNumber`).
* **`Decimal128`**: Utilizado estritamente para **valores financeiros** (ex: `unitPrice`, `amount`, `limitAmount`, `spentAmount`). Evita os erros de arredondamento comuns ao usar ponto flutuante (*Double*) em cálculos monetários.
* **`Date` (`ISODate`)**: Para armazenar datas exatas de validade, criação de listas, compras e logs (ex: `expirationDate`, `timestamp`, `date`).
* **`Boolean`**: Para status binários (ex: `isPurchased` nos itens da lista de compras).
* **`Array`**: Para armazenar listas de subdocumentos embutidos (ex: `items` na lista de compras e `purchasedItems` na compra).
* **`Object`**: Para organizar atributos agrupados dentro de um documento (ex: `addressUser`).

---

## 3. Entidades, Atributos e Cardinalidades

Abaixo está o detalhamento do schema flexível mapeado para as coleções do banco de dados:

### Coleções Independentes (Collections)

* **Usuário (`users`)**
  * `_id`: ObjectId
  * `name`: String
  * `email`: String
  * `password`: String
  * `addressUser`: Object (Embutido: `city` [String], `district` [String], `street` [String], `houseNumber` [Int32])
* **Produto (`products`)**
  * `_id`: ObjectId
  * `name`: String
  * `categoryId`: ObjectId (Referência 1:N)
* **Categoria (`categories`)**
  * `_id`: ObjectId
  * `name`: String
* **Inventário (`inventory`)**
  * `_id`: ObjectId
  * `productId`: ObjectId (Referência 1:N)
  * `quantity`: Int32
  * `expirationDate`: Date
* **Mercado (`markets`)**
  * `_id`: ObjectId
  * `name`: String
  * `location`: String
* **Orçamento (`budgets`)**
  * `_id`: ObjectId
  * `month`: String
  * `limitAmount`: Decimal128
  * `spentAmount`: Decimal128 (Calculado e atualizado pelo sistema)
* **Transação (`transactions`)**
  * `_id`: ObjectId
  * `purchaseId`: ObjectId (Referência 1:1)
  * `amount`: Decimal128
  * `date`: Date
* **Log de Atividade (`activity_logs`)**
  * `_id`: ObjectId
  * `userId`: ObjectId (Referência 1:N)
  * `action`: String
  * `timestamp`: Date

### Coleções com Subdocumentos Embutidos (Embedded)

* **Lista de Compras (`shopping_lists`)**
  * `_id`: ObjectId
  * `userId`: ObjectId (Referência ao usuário criador/gestor)
  * `title`: String
  * `date`: Date
  * `items`: Array of Objects (Embutido: `productId` [ObjectId], `quantityNeeded` [Int32], `isPurchased` [Boolean])
* **Compra (`purchases`)**
  * `_id`: ObjectId
  * `marketId`: ObjectId (Referência 1:N)
  * `userId`: ObjectId (Referência ao usuário responsável pela compra)
  * `date`: Date
  * `totalValue`: Decimal128 (Calculado automaticamente pela API)
  * `purchasedItems`: Array of Objects (Embutido: `productId` [ObjectId], `unitPrice` [Decimal128], `quantity` [Int32])

---

## 4. Estratégia de Relacionamentos: Embedded vs. Reference

No MongoDB, a regra de ouro é: *"Dados que são acessados juntos, devem ser armazenados juntos"*, mas sem ultrapassar o limite de 16MB por documento. A tabela a seguir justifica nossas escolhas arquiteturais:

| Relacionamento | Tipo de Modelagem | Cardinalidade | Justificativa Técnica |
| :--- | :--- | :---: | :--- |
| **Usuário ➔ Endereço** | **Embedded** (Embutido) | 1:1 | O endereço pertence exclusivamente ao usuário e é sempre consultado junto com o perfil. Sendo uma relação de 1 para 1, não há risco de crescimento infinito (*unbounded growth*). |
| **ShoppingList ➔ ShoppingListItem** | **Embedded** (Embutido) | 1:N (Limitado) | Os itens da lista (`ShoppingListItem`) são estruturados como subdocumentos embutidos para otimizar a leitura direta da lista. Eles não fazem sentido fora do contexto da própria lista e seu tamanho é previsível. |
| **Compra ➔ PurchaseItem** | **Embedded** (Embutido) | 1:N (Limitado) | Os itens comprados (`PurchaseItem`) representam o "recibo" físico e são embutidos para garantir performance e congelar o histórico financeiro da compra. |
| **ShoppingList ➔ Usuário** | **Reference** (Referência) | 1:N | Vincula a lista de compras ao usuário responsável por sua criação ou gestão através do campo `userId`. |
| **Compra ➔ Usuário** | **Reference** (Referência) | 1:N | Associa o registro da compra ao usuário responsável pelo lançamento através do campo `userId`. |
| **Produto ➔ Categoria** | **Reference** (Referência) | 1:N | Categorias são reutilizadas por milhares de produtos. Atualizações em categorias propagam-se de forma consistente sem redundância. |
| **Inventário ➔ Produto** | **Reference** (Referência) | 1:N | O produto é um catálogo fixo, enquanto o estoque é dinâmico (vários lotes com diferentes validades). |
| **Compra ➔ Mercado** | **Reference** (Referência) | 1:N | Relaciona o local onde a compra foi efetuada (`marketId`) com a coleção de mercados cadastrados. |
| **Transação ➔ Compra** | **Reference** (Referência) | 1:1 | Associa cada transação financeira de pagamento à sua respectiva compra geradora (`purchaseId`). |
| **Budget ➔ Transações** | **Sem Vínculo Físico Direto** | Dinâmico | O `Budget` **não** possui uma referência física direta gravada no banco para as transações. O gasto mensal (`spentAmount`) é calculado dinamicamente em tempo de execução com base no intervalo de datas das transações daquele mês (`date` / `findByDateBetween`). |
| **ActivityLog ➔ Usuário** | **Reference** (Referência) | 1:N (Infinito) | Logs de atividade crescem indefinidamente, exigindo coleção separada para evitar o estouro do limite de 16MB do BSON. |

---

## 5. Resposta Fundamentada: Por que os dados foram modelados dessa forma?

A modelagem de dados neste projeto foi guiada por dois pilares fundamentais do paradigma NoSQL orientado a documentos: **Otimização de Leitura (Read Performance)** e o **Ciclo de Vida dos Dados (Data Lifecycle)**.

1. **Evitando Junções Desnecessárias (Otimização de Leitura):** 
   Em bancos relacionais (SQL), normalizamos tudo. No nosso sistema, decidimos **desnormalizar** (usar *Embedded Documents*) entidades como `Endereço`, `Itens da Lista de Compras` (`ShoppingListItem`) e `Itens Comprados` (`PurchaseItem`). Isso foi feito porque a aplicação precisará exibir a tela de "Recibo de Compra" ou "Lista de Supermercado" de uma só vez. Ao embutir os itens no documento pai, o MongoDB busca todas as informações em uma única operação de disco (I/O), tornando as respostas da API extremamente rápidas.

2. **Respeitando o Ciclo de Vida e Limites Físicos:**
   Entidades com ciclos de vida independentes foram separadas utilizando **Referências** (Reference/Normalized). O `Produto` precisa existir mesmo se o estoque estiver zerado. Da mesma forma, utilizamos referências para `Usuários` (`userId` em listas e compras), `Transações` e `ActivityLogs` para manter a integridade relacional onde há múltiplos vínculos ou crescimento contínuo. 

**Conclusão:** O esquema proposto atinge um equilíbrio perfeito: é flexível para lidar com as variabilidades do controle doméstico, denormalizado nas camadas de visualização direta (listas/compras) para máxima performance, e normalizado nos dados categóricos e de auditoria para garantir a consistência ao longo do tempo.
