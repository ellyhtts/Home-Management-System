# Decisões de implementação

## Pergunta principal

Como transformar a modelagem em uma aplicação funcional sem perder a coerência? A modelagem (`Modeling/entities.md` e `relationships.md`) define nomes de campos, collections e relacionamentos. Quando foi preciso algo a mais, a mudança foi documentada abaixo.

## Alterações em relação à modelagem

| Alteração | Motivo |
|---|---|
| `PurchaseItem.quantity` | Necessário para calcular `quantity × unitPrice`. |
| `Purchase.totalValue` | Total calculado no servidor, sem confiar no cliente. |
| `Purchase.user` (reference) | Rastreabilidade e validação de User (404). |
| `ShoppingListItem.isPurchased` | Marcar itens já comprados. |
| `ShoppingList.user` (reference) | Registrar quem criou a lista. |
| `Budget.spentAmount` | Guarda o gasto do mês, atualizado automaticamente a cada transação. |
| `Budget.remainingAmount` | Saldo calculado (`limitAmount - spentAmount`), não persistido. |


## Escolhas

- **Idioma:** campos em inglês, como na modelagem; nomes de métodos e mensagens ao usuário em português.
- **Tipos:** apenas wrappers (`Integer`, `Boolean`, `BigDecimal`, `LocalDate`, `LocalDateTime`), nunca `int`. Valores monetários como `Decimal128`.
- **Market:** `location` é String.
- **Lote de produtos:** `POST /products/batch` valida todos os itens antes de gravar.
- **Orçamento:** `spentAmount` é somente leitura na API (o cliente não consegue falsificar o gasto).


## Regras de negócio

As regras são verificações que o sistema faz antes de gravar algo, para evitar dados errados, cada resposta de erro tem um número: **400** (dado inválido), **404** (algo não existe) e **409** (conflito com o que já existe).

1. **Não repetir cadastros (409).** O sistema recusa o que já existe: usuário com e-mail repetido, categoria ou produto com nome repetido, dois orçamentos para o mesmo mês e mais de uma transação para a mesma compra.
2. **Compra precisa ter itens (400).** Não é possível salvar uma compra vazia. O total é calculado pelo sistema (quantidade × preço de cada item), então o cliente não consegue informar um valor errado.
3. **Só usar o que existe (404).** Antes de gravar, o sistema confere se tudo o que foi citado existe: usuário, mercado e produto da compra, produto do estoque e da lista,  usuário do log e  compra da transação, se algo não existir, a operação é recusada.
4. **Estoque válido (400).** A quantidade é obrigatória e não pode ser negativa.
5. **Respeitar o limite do orçamento (409).** Se uma compra ou transação fizer o gasto do mês passar do limite (`limitAmount`), ela é recusada.
6. **Não apagar o que está em uso (409).** Uma categoria que ainda tem produtos não pode ser excluída.
7. **A compra faz o resto sozinha.** Ao registrar uma compra, o sistema: cria a transação, atualiza o gasto do orçamento (`spentAmount`), soma os itens no estoque, marca como comprados os itens das listas e registra a ação no log.
8. **Estoque baixo vai para a lista.** Com `POST /shopping-lists/from-low-stock`, os produtos com pouco estoque entram na lista automática, sem aparecer duas vezes.
9. **Editar ou apagar uma compra também ajusta o resto.** No `PUT` (editar), a transação e o estoque são recalculados. No `DELETE` (apagar), o estoque volta ao que era e a transação some.

## Como a aplicação garante a integridade

- **Dados inválidos:** os services validam campos obrigatórios, formatos (e-mail, mês) e limites (quantidade > 0, preço ≥ 0) e respondem 400.
- **Referências inexistentes:** o MongoDB não confere referências, antes de gravar, cada service consulta o repository da entidade referenciada e responde 404.
- **Duplicidades:** consultas `existsBy...` e `findBy...` antes de gravar, com resposta 409.
- **Atualizações inconsistentes:** o `PUT` revalida tudo e recalcula o total; a compra valida o orçamento antes de gravar.


## Testes

- Unitários (Mockito): rodar com `.\mvnw clean test`. Ao acrescentar dependências nos services (`ActivityLogService` e `BudgetService`), é preciso acrescentar o respectivo `@Mock` nos testes.
- API: coleção do Insomnia em `Insomnia1.json/`, com casos 200, 201, 204, 400, 404 e 409.

