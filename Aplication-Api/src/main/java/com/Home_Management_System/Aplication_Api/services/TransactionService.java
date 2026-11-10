package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Budget;
import com.Home_Management_System.Aplication_Api.models.Purchase;
import com.Home_Management_System.Aplication_Api.models.Transaction;
import com.Home_Management_System.Aplication_Api.repositories.BudgetRepository;
import com.Home_Management_System.Aplication_Api.repositories.PurchaseRepository;
import com.Home_Management_System.Aplication_Api.repositories.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final PurchaseRepository purchaseRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              BudgetRepository budgetRepository,
                              PurchaseRepository purchaseRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public Transaction criar(Transaction transaction) {
        validarCamposBasicos(transaction);

        if (transaction.getPurchase() != null && transaction.getPurchase().getId() != null) {
            Purchase purchase = purchaseRepository.findById(transaction.getPurchase().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra referenciada não encontrada."));
            transaction.setPurchase(purchase);
        }

        if (transaction.getBudget() != null && transaction.getBudget().getId() != null) {
            Budget budget = budgetRepository.findById(transaction.getBudget().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento referenciado não encontrado."));

            if ("DESPESA".equalsIgnoreCase(transaction.getTipo())) {
                BigDecimal gastoAtual = budget.getValorGastoAtual() != null ? budget.getValorGastoAtual() : BigDecimal.ZERO;
                BigDecimal novoGastoTotal = gastoAtual.add(transaction.getValor());

                if (novoGastoTotal.compareTo(budget.getValorLimite()) > 0) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Transação recusada: o valor ultrapassa o limite disponível no orçamento mensal.");
                }

                budget.setValorGastoAtual(novoGastoTotal);
                budgetRepository.save(budget);
            }
            transaction.setBudget(budget);
        }

        if (transaction.getDataTransacao() == null) {
            transaction.setDataTransacao(LocalDateTime.now());
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> listarTodas() {
        return transactionRepository.findAll();
    }

    public Transaction buscarPorId(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação financeira não encontrada."));
    }

    public List<Transaction> buscarPorTipo(String tipo) {
        return transactionRepository.findByTipoIgnoreCase(tipo);
    }

    public List<Transaction> buscarPorOrcamento(String budgetId) {
        return transactionRepository.findByBudgetId(budgetId);
    }

    public Transaction atualizar(String id, Transaction dadosAtualizados) {
        Transaction existente = buscarPorId(id);
        validarCamposBasicos(dadosAtualizados);

        existente.setDescricao(dadosAtualizados.getDescricao());
        existente.setValor(dadosAtualizados.getValor());
        existente.setTipo(dadosAtualizados.getTipo().toUpperCase());

        return transactionRepository.save(existente);
    }

    public void excluir(String id) {
        Transaction existente = buscarPorId(id);

        if ("DESPESA".equalsIgnoreCase(existente.getTipo()) && existente.getBudget() != null) {
            budgetRepository.findById(existente.getBudget().getId()).ifPresent(budget -> {
                BigDecimal gastoAtual = budget.getValorGastoAtual() != null ? budget.getValorGastoAtual() : BigDecimal.ZERO;
                BigDecimal novoGasto = gastoAtual.subtract(existente.getValor()).max(BigDecimal.ZERO);
                budget.setValorGastoAtual(novoGasto);
                budgetRepository.save(budget);
            });
        }

        transactionRepository.delete(existente);
    }

    private void validarCamposBasicos(Transaction transaction) {
        if (transaction.getDescricao() == null || transaction.getDescricao().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A descrição da transação é obrigatória.");
        }
        if (transaction.getValor() == null || transaction.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor da transação deve ser maior que zero.");
        }
        if (transaction.getTipo() == null ||
                (!transaction.getTipo().equalsIgnoreCase("DESPESA") && !transaction.getTipo().equalsIgnoreCase("RECEITA"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O tipo da transação deve ser 'DESPESA' ou 'RECEITA'.");
        }
        transaction.setTipo(transaction.getTipo().toUpperCase());
    }
}