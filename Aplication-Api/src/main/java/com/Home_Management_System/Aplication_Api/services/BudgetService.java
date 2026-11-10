package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Budget;
import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.repositories.BudgetRepository;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
    }

    public Budget criar(Budget budget) {
        validarDadosOrcamento(budget);

        Boolean jaExiste = budgetRepository.existsByCategoryIdAndMesReferenciaAndAnoReferencia(
                budget.getCategory().getId(),
                budget.getMesReferencia(),
                budget.getAnoReferencia()
        );
        if (Boolean.TRUE.equals(jaExiste)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um orçamento cadastrado para esta categoria neste mês e ano.");
        }

        if (budget.getValorGastoAtual() == null) {
            budget.setValorGastoAtual(BigDecimal.ZERO);
        }

        return budgetRepository.save(budget);
    }

    public List<Budget> listarTodos() {
        return budgetRepository.findAll();
    }

    public Budget buscarPorId(String id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado."));
    }

    public List<Budget> buscarPorPeriodo(Integer mes, Integer ano) {
        return budgetRepository.findByMesReferenciaAndAnoReferencia(mes, ano);
    }

    public Budget atualizar(String id, Budget dadosAtualizados) {
        Budget existente = buscarPorId(id);
        validarDadosOrcamento(dadosAtualizados);

        existente.setMesReferencia(dadosAtualizados.getMesReferencia());
        existente.setAnoReferencia(dadosAtualizados.getAnoReferencia());
        existente.setValorLimite(dadosAtualizados.getValorLimite());
        if (dadosAtualizados.getValorGastoAtual() != null) {
            existente.setValorGastoAtual(dadosAtualizados.getValorGastoAtual());
        }
        existente.setCategory(dadosAtualizados.getCategory());

        return budgetRepository.save(existente);
    }

    public void excluir(String id) {
        Budget existente = buscarPorId(id);
        budgetRepository.delete(existente);
    }

    private void validarDadosOrcamento(Budget budget) {
        if (budget.getMesReferencia() == null || budget.getMesReferencia() < 1 || budget.getMesReferencia() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O mês de referência deve estar entre 1 e 12.");
        }
        if (budget.getAnoReferencia() == null || budget.getAnoReferencia() < 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um ano de referência válido.");
        }
        if (budget.getValorLimite() == null || budget.getValorLimite().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O valor limite do orçamento deve ser maior que zero.");
        }
        if (budget.getCategory() == null || budget.getCategory().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A categoria do orçamento é obrigatória.");
        }
        Category categoria = categoryRepository.findById(budget.getCategory().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria informada não existe."));
        budget.setCategory(categoria);
    }
}