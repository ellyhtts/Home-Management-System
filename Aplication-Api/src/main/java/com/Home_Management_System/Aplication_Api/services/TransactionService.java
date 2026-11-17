package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Purchase;
import com.Home_Management_System.Aplication_Api.models.Transaction;
import com.Home_Management_System.Aplication_Api.repositories.BudgetRepository;
import com.Home_Management_System.Aplication_Api.repositories.PurchaseRepository;
import com.Home_Management_System.Aplication_Api.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final PurchaseRepository purchaseRepository;
    private final BudgetService budgetService;

    public TransactionService(TransactionRepository transactionRepository,
                              BudgetRepository budgetRepository,
                              PurchaseRepository purchaseRepository,
                              BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.purchaseRepository = purchaseRepository;
        this.budgetService = budgetService;
    }

    public Transaction criar(Transaction t) {
        Transaction nova = new Transaction();
        preencher(nova, t, null);
        Transaction salva = transactionRepository.save(nova);
        budgetService.recalcularGastoPorData(salva.getDate());
        return salva;
    }

    public List<Transaction> listarTodas() { return transactionRepository.findAll(); }

    public Transaction buscarPorId(String id) {
        return transactionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada!"));
    }

    public List<Transaction> buscarPorCompra(String purchaseId) {
        return transactionRepository.findAll().stream()
                .filter(x -> x.getPurchase() != null && purchaseId.equals(x.getPurchase().getId()))
                .toList();
    }

    public Transaction atualizar(String id, Transaction dados) {
        Transaction existente = buscarPorId(id);
        LocalDateTime dataAntiga = existente.getDate();
        preencher(existente, dados, id);
        Transaction salva = transactionRepository.save(existente);
        budgetService.recalcularGastoPorData(dataAntiga);   // mês antigo (caso a data tenha mudado)
        budgetService.recalcularGastoPorData(salva.getDate());
        return salva;
    }

    public void excluir(String id) {
        Transaction t = buscarPorId(id);
        transactionRepository.delete(t);
        budgetService.recalcularGastoPorData(t.getDate());  // devolve o valor ao orçamento
    }

    public void validarLimiteDoMes(LocalDateTime date, BigDecimal valor, String idAtual) {
        validarOrcamento(date, valor, idAtual);
    }

    public Transaction registrarParaCompra(Purchase compra) {
        Transaction t = new Transaction();
        t.setPurchase(compra);
        t.setAmount(compra.getTotalValue());
        t.setDate(compra.getDate());
        return criar(t);
    }


    public Transaction sincronizarParaCompra(Purchase compra) {
        List<Transaction> existentes = buscarPorCompra(compra.getId());
        Transaction dados = new Transaction();
        dados.setPurchase(compra);
        dados.setAmount(compra.getTotalValue());
        dados.setDate(compra.getDate());
        if (existentes.isEmpty()) return criar(dados);
        return atualizar(existentes.get(0).getId(), dados);
    }

    public void excluirPorCompra(String purchaseId) {
        List<Transaction> lista = buscarPorCompra(purchaseId);
        lista.forEach(transactionRepository::delete);
        lista.forEach(t -> budgetService.recalcularGastoPorData(t.getDate()));
    }

    private void preencher(Transaction destino, Transaction dados, String idAtual) {

        if (dados.getPurchase() == null || dados.getPurchase().getId() == null
                || dados.getPurchase().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "purchase (com id) é obrigatória!");
        }
        if (dados.getAmount() != null && dados.getAmount().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor deve ser maior que zero!");
        }

        Purchase compra = purchaseRepository.findById(dados.getPurchase().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada!"));

        boolean duplicada = transactionRepository.findAll().stream()
                .anyMatch(x -> x.getPurchase() != null
                        && compra.getId().equals(x.getPurchase().getId())
                        && !Objects.equals(x.getId(), idAtual));
        if (duplicada) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma transação para esta compra!");
        }

        BigDecimal amount = dados.getAmount() != null ? dados.getAmount() : compra.getTotalValue();
        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor da transação inválido!");
        }
        LocalDateTime date = dados.getDate() != null ? dados.getDate() : LocalDateTime.now();

        validarOrcamento(date, amount, idAtual);

        destino.setPurchase(compra);
        destino.setAmount(amount.setScale(2, RoundingMode.HALF_UP));
        destino.setDate(date);
    }

    private void validarOrcamento(LocalDateTime date, BigDecimal novoValor, String idAtual) {
        budgetRepository.findByMonthIgnoreCase(BudgetService.nomeMes(date)).ifPresent(budget -> {
            LocalDateTime inicio = date.toLocalDate().withDayOfMonth(1).atStartOfDay().minusNanos(1);
            LocalDateTime fim = inicio.plusNanos(1).plusMonths(1);
            BigDecimal gasto = transactionRepository
                    .findByDateBetween(inicio, fim).stream()
                    .filter(x -> !Objects.equals(x.getId(), idAtual))
                    .map(Transaction::getAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (gasto.add(novoValor).compareTo(budget.getLimitAmount()) > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Transação recusada: ultrapassa o limite do orçamento do mês!");
            }
        });
    }
}
