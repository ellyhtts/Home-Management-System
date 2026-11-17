package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Budget;
import com.Home_Management_System.Aplication_Api.models.Transaction;
import com.Home_Management_System.Aplication_Api.repositories.BudgetRepository;
import com.Home_Management_System.Aplication_Api.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class BudgetService {

    private static final Locale PT_BR = Locale.of("pt", "BR");

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final ActivityLogService activityLogService;

    public BudgetService(BudgetRepository budgetRepository,
                         TransactionRepository transactionRepository,
                         ActivityLogService activityLogService) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.activityLogService = activityLogService;
    }

    public Budget criar(Budget b) {
        validar(b);
        if (budgetRepository.existsByMonthIgnoreCase(b.getMonth())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um orçamento para este mês!");
        }

        b.setLimitAmount(b.getLimitAmount().setScale(2, RoundingMode.HALF_UP));
        b.setSpentAmount(calcularGasto(b.getMonth()));
        Budget salvo = budgetRepository.save(b);
        activityLogService.registrar("Definiu o orçamento de " + b.getMonth()
                + " em " + ActivityLogService.moeda(b.getLimitAmount()));
        return salvo;
    }

    public List<Budget> listarTodos() { return budgetRepository.findAll(); }

    public Budget buscarPorId(String id) {
        return budgetRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado!"));
    }

    public Budget buscarPorMes(String month) {
        Budget b = budgetRepository.findByMonthIgnoreCase(month).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado para o mês informado!"));
        activityLogService.registrar("Verificou o orçamento e o saldo restante de " + b.getMonth()
                + " (saldo: " + ActivityLogService.moeda(b.getRemainingAmount()) + ")");
        return b;
    }

    public Budget atualizar(String id, Budget dados) {
        Budget existente = buscarPorId(id);
        validar(dados);
        if (!existente.getMonth().equalsIgnoreCase(dados.getMonth())
                && budgetRepository.existsByMonthIgnoreCase(dados.getMonth())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um orçamento para este mês!");
        }
        existente.setMonth(dados.getMonth());
        existente.setLimitAmount(dados.getLimitAmount().setScale(2, RoundingMode.HALF_UP));
        existente.setSpentAmount(calcularGasto(existente.getMonth()));
        Budget salvo = budgetRepository.save(existente);
        activityLogService.registrar("Atualizou o orçamento de " + existente.getMonth()
                + " para o limite de " + ActivityLogService.moeda(existente.getLimitAmount()));
        return salvo;
    }

    public void excluir(String id) {
        Budget existente = buscarPorId(id);
        budgetRepository.delete(existente);
        activityLogService.registrar("Excluiu o orçamento de " + existente.getMonth());
    }


    public static String nomeMes(LocalDateTime date) {
        String nome = date.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + "/" + date.getYear();
    }

    public BigDecimal calcularGasto(String month) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getDate() != null && t.getAmount() != null
                        && nomeMes(t.getDate()).equalsIgnoreCase(month))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public void recalcularGastoPorData(LocalDateTime date) {
        if (date == null) return;
        budgetRepository.findByMonthIgnoreCase(nomeMes(date)).ifPresent(budget -> {
            budget.setSpentAmount(calcularGasto(budget.getMonth()));
            budgetRepository.save(budget);
        });
    }

    private void validar(Budget b) {
        if (b.getMonth() == null || !b.getMonth().matches("\\p{L}+/\\d{4}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O mês deve estar no formato 'Outubro/2026'!");
        }
        if (b.getLimitAmount() == null || b.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O limite do orçamento deve ser maior que zero!");
        }
    }
}
