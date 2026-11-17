package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.*;
import com.Home_Management_System.Aplication_Api.repositories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock TransactionRepository transactionRepository;
    @Mock BudgetRepository budgetRepository;
    @Mock PurchaseRepository purchaseRepository;
    @InjectMocks TransactionService service;

    private Transaction transacao(String purchaseId, String valor) {
        Purchase ref = new Purchase(); ref.setId(purchaseId);
        Transaction t = new Transaction();
        t.setPurchase(ref);
        if (valor != null) t.setAmount(new BigDecimal(valor));
        t.setDate(LocalDateTime.of(2026, 10, 2, 10, 0));
        return t;
    }

    private Purchase compra(String id, String total) {
        Purchase p = new Purchase(); p.setId(id);
        if (total != null) p.setTotalValue(new BigDecimal(total));
        return p;
    }

    @Test
    void compraInexistente_retorna404() {
        Mockito.when(purchaseRepository.findById("p1")).thenReturn(Optional.empty());
        ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.criar(transacao("p1", "10.00")));
        Assertions.assertEquals(404, e.getStatusCode().value());
    }

    @Test
    void compraJaTemTransacao_retorna409() {
        Mockito.when(purchaseRepository.findById("p1")).thenReturn(Optional.of(compra("p1", "10.00")));
        Transaction existente = transacao("p1", "10.00"); existente.setId("t1");
        Mockito.when(transactionRepository.findAll()).thenReturn(List.of(existente));
        ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.criar(transacao("p1", "10.00")));
        Assertions.assertEquals(409, e.getStatusCode().value());
    }

    @Test
    void ultrapassaLimiteDoOrcamento_retorna409() {
        Mockito.when(purchaseRepository.findById("p1")).thenReturn(Optional.of(compra("p1", "20.00")));
        Mockito.when(transactionRepository.findAll()).thenReturn(List.of());
        Budget b = new Budget(); b.setMonth("Outubro/2026"); b.setLimitAmount(new BigDecimal("100.00"));
        Mockito.when(budgetRepository.findByMonthIgnoreCase(ArgumentMatchers.any())).thenReturn(Optional.of(b));
        Transaction gasto = transacao("p9", "90.00"); gasto.setId("t9");
        Mockito.when(transactionRepository.findByDateBetween(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(List.of(gasto));
        ResponseStatusException e = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.criar(transacao("p1", "20.00")));
        Assertions.assertEquals(409, e.getStatusCode().value());
    }

    @Test
    void semValor_usaTotalDaCompra() {
        Mockito.when(purchaseRepository.findById("p1")).thenReturn(Optional.of(compra("p1", "32.00")));
        Mockito.when(transactionRepository.findAll()).thenReturn(List.of());
        Mockito.when(budgetRepository.findByMonthIgnoreCase(ArgumentMatchers.any())).thenReturn(Optional.empty());
        Mockito.when(transactionRepository.save(ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));
        Transaction salva = service.criar(transacao("p1", null));
        Assertions.assertEquals(new BigDecimal("32.00"), salva.getAmount());
    }
}