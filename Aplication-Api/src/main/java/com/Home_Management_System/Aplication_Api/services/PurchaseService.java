package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Market;
import com.Home_Management_System.Aplication_Api.models.Purchase;
import com.Home_Management_System.Aplication_Api.models.PurchaseItem;
import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.MarketRepository;
import com.Home_Management_System.Aplication_Api.repositories.PurchaseRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketRepository marketRepository;

    public List<Purchase> listarTodas() {
        return purchaseRepository.findAll();
    }

    public Optional<Purchase> buscarPorId(String id) {
        return purchaseRepository.findById(id);
    }

    public Purchase salvar(Purchase purchase) {
        validarPreencherECalcular(purchase);
        return purchaseRepository.save(purchase);
    }

    public Purchase atualizar(String id, Purchase dados) {
        Purchase existente = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compra não encontrada!"));

        validarPreencherECalcular(dados);

        existente.setComprador(dados.getComprador());
        existente.setMarket(dados.getMarket());
        existente.setFormaPagamento(dados.getFormaPagamento());
        existente.setItens(dados.getItens());
        existente.setValorTotal(dados.getValorTotal());

        return purchaseRepository.save(existente);
    }

    public void deletar(String id) {
        if (!purchaseRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Compra não encontrada!");
        }
        purchaseRepository.deleteById(id);
    }

    private void validarPreencherECalcular(Purchase purchase) {


        if (purchase.getItens() == null || purchase.getItens().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A compra deve possuir pelo menos 1 item!");
        }


        if (purchase.getComprador() == null || purchase.getComprador().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O comprador e o ID do comprador são obrigatórios!");
        }
        User comprador = userRepository.findById(purchase.getComprador().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário comprador não encontrado!"));
        purchase.setComprador(comprador);


        if (purchase.getMarket() == null || purchase.getMarket().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O supermercado e o ID do supermercado são obrigatórios!");
        }
        Market market = marketRepository.findById(purchase.getMarket().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supermercado não encontrado!"));
        purchase.setMarket(market);


        BigDecimal valorTotalGeral = BigDecimal.ZERO;
        for (PurchaseItem item : purchase.getItens()) {
            if (item.getPrecoUnitario() == null || item.getQuantidade() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Preço unitário e quantidade são obrigatórios em cada item!");
            }
            if (item.getQuantidade() <= 0 || item.getPrecoUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Quantidade deve ser maior que zero e preço não pode ser negativo!");
            }
            BigDecimal subtotal = item.getPrecoUnitario()
                    .multiply(BigDecimal.valueOf(item.getQuantidade()));
            item.setSubtotal(subtotal);
            valorTotalGeral = valorTotalGeral.add(subtotal);
        }
        purchase.setValorTotal(valorTotalGeral);
    }
}