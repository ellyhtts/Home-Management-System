package com.Home_Management_System.Aplication_Api;

import com.Home_Management_System.Aplication_Api.models.Budget;
import com.Home_Management_System.Aplication_Api.models.Category;
import com.Home_Management_System.Aplication_Api.models.Transaction;
import com.Home_Management_System.Aplication_Api.repositories.BudgetRepository;
import com.Home_Management_System.Aplication_Api.repositories.CategoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.PurchaseRepository;
import com.Home_Management_System.Aplication_Api.repositories.TransactionRepository;
import com.Home_Management_System.Aplication_Api.services.BudgetService;
import com.Home_Management_System.Aplication_Api.services.TransactionService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AplicationApiApplicationTests {

	@Mock
	private BudgetRepository budgetRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private PurchaseRepository purchaseRepository;

	@InjectMocks
	private BudgetService budgetService;

	@InjectMocks
	private TransactionService transactionService;

	@Test
	void deveImpedirOrcamentoDuplicadoParaMesmaCategoriaEMes() {
		Category cat = new Category();
		cat.setId("cat123");

		Budget budget = new Budget();
		budget.setMesReferencia(11);
		budget.setAnoReferencia(2026);
		budget.setValorLimite(new BigDecimal("1500.00"));
		budget.setCategory(cat);

		Mockito.when(categoryRepository.findById("cat123")).thenReturn(Optional.of(cat));
		Mockito.when(budgetRepository.existsByCategoryIdAndMesReferenciaAndAnoReferencia("cat123", 11, 2026))
				.thenReturn(true);

		ResponseStatusException ex = Assertions.assertThrows(
				ResponseStatusException.class,
				() -> budgetService.criar(budget)
		);
		Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
	}

	@Test
	void deveImpedirTransacaoQueUltrapassaLimiteDoOrcamento() {
		Budget budget = new Budget();
		budget.setId("bud123");
		budget.setValorLimite(new BigDecimal("500.00"));
		budget.setValorGastoAtual(new BigDecimal("450.00"));

		Transaction tx = new Transaction();
		tx.setDescricao("Compra Extra no Mercado");
		tx.setTipo("DESPESA");
		tx.setValor(new BigDecimal("100.00"));
		tx.setBudget(budget);

		Mockito.when(budgetRepository.findById("bud123")).thenReturn(Optional.of(budget));

		ResponseStatusException ex = Assertions.assertThrows(
				ResponseStatusException.class,
				() -> transactionService.criar(tx)
		);
		Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
	}

	@Test
	void deveAtualizarValorGastoDoOrcamentoAoCriarDespesaValida() {
		Budget budget = new Budget();
		budget.setId("bud123");
		budget.setValorLimite(new BigDecimal("1000.00"));
		budget.setValorGastoAtual(new BigDecimal("200.00"));

		Transaction tx = new Transaction();
		tx.setDescricao("Feira da Semana");
		tx.setTipo("DESPESA");
		tx.setValor(new BigDecimal("150.00"));
		tx.setBudget(budget);

		Mockito.when(budgetRepository.findById("bud123")).thenReturn(Optional.of(budget));
		Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

		Transaction salva = transactionService.criar(tx);

		Assertions.assertNotNull(salva.getDataTransacao());
		Assertions.assertEquals(new BigDecimal("350.00"), budget.getValorGastoAtual());
	}
}