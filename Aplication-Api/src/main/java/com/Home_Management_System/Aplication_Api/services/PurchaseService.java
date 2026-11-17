package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Inventory;
import com.Home_Management_System.Aplication_Api.models.Product;
import com.Home_Management_System.Aplication_Api.models.Purchase;
import com.Home_Management_System.Aplication_Api.models.PurchaseItem;
import com.Home_Management_System.Aplication_Api.models.Transaction;
import com.Home_Management_System.Aplication_Api.repositories.InventoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.MarketRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import com.Home_Management_System.Aplication_Api.repositories.PurchaseRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PurchaseService {

    @Autowired private PurchaseRepository purchaseRepository;
    @Autowired private MarketRepository marketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private TransactionService transactionService;
    @Autowired private ActivityLogService activityLogService;

    public List<Purchase> listarTodas() { return purchaseRepository.findAll(); }

    public Optional<Purchase> buscarPorId(String id) { return purchaseRepository.findById(id); }

    public Purchase salvar(Purchase p) {
        validarPreencherECalcular(p);
        p.setId(null);

        // 1) recusa a compra se estourar o orçamento do mês
        transactionService.validarLimiteDoMes(p.getDate(), p.getTotalValue(), null);

        Purchase salva = purchaseRepository.save(p);
        try {
            // 2) cria a transação (isso debita o orçamento do mês)
            transactionService.registrarParaCompra(salva);
            // 3) soma os itens no estoque
            ajustarEstoque(salva.getPurchasedItems(), 1);
        } catch (RuntimeException e) {
            // desfaz para não deixar compra órfã
            transactionService.excluirPorCompra(salva.getId());
            purchaseRepository.deleteById(salva.getId());
            throw e;
        }

        activityLogService.registrar("Registrou a compra física no supermercado " + nomeMercado(salva)
                        + " no valor de " + ActivityLogService.moeda(salva.getTotalValue())
                        + ", com " + salva.getPurchasedItems().size() + " item(ns) lançado(s) no estoque",
                idUsuario(salva));
        return salva;
    }

    public Purchase atualizar(String id, Purchase dados) {
        Purchase existente = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compra não encontrada!"));
        List<PurchaseItem> itensAntigos = existente.getPurchasedItems();

        validarPreencherECalcular(dados);

        String transacaoId = transactionService.buscarPorCompra(id).stream()
                .findFirst().map(Transaction::getId).orElse(null);
        transactionService.validarLimiteDoMes(dados.getDate(), dados.getTotalValue(), transacaoId);

        existente.setMarket(dados.getMarket());
        existente.setUser(dados.getUser());
        existente.setDate(dados.getDate());
        existente.setPurchasedItems(dados.getPurchasedItems());
        existente.setTotalValue(dados.getTotalValue());
        Purchase salva = purchaseRepository.save(existente);

        transactionService.sincronizarParaCompra(salva);      // ajusta valor/data e o orçamento
        ajustarEstoque(itensAntigos, -1);                      // estorna o estoque antigo
        ajustarEstoque(salva.getPurchasedItems(), 1);          // aplica o novo

        activityLogService.registrar("Atualizou a compra do supermercado " + nomeMercado(salva)
                + " (novo total: " + ActivityLogService.moeda(salva.getTotalValue())
                + "), reajustando o estoque e o orçamento do mês", idUsuario(salva));
        return salva;
    }

    public void deletar(String id) {
        Purchase existente = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compra não encontrada!"));
        transactionService.excluirPorCompra(id);               // devolve o valor ao orçamento
        ajustarEstoque(existente.getPurchasedItems(), -1);     // retira os itens do estoque
        purchaseRepository.deleteById(id);

        activityLogService.registrar("Excluiu a compra do supermercado " + nomeMercado(existente)
                + " no valor de " + ActivityLogService.moeda(existente.getTotalValue())
                + ", devolvendo o valor ao orçamento e retirando os itens do estoque", idUsuario(existente));
    }

    private String nomeMercado(Purchase p) {
        return p.getMarket() != null && p.getMarket().getName() != null
                ? p.getMarket().getName() : "não informado";
    }

    private String idUsuario(Purchase p) {
        return p.getUser() != null ? p.getUser().getId() : null;
    }

    /**
     * Soma (sinal = 1) ou subtrai (sinal = -1) as quantidades dos itens no estoque.
     * Se o produto ainda não existe no estoque, cria o registro (apenas na soma).
     * A quantidade nunca fica negativa.
     */
    private void ajustarEstoque(List<PurchaseItem> itens, int sinal) {
        if (itens == null || itens.isEmpty()) return;

        Map<String, Integer> porProduto = new LinkedHashMap<>();
        for (PurchaseItem i : itens) {
            porProduto.merge(i.getProductId(), i.getQuantity(), Integer::sum);
        }
        List<Inventory> estoque = inventoryRepository.findAll();
        porProduto.forEach((productId, qtd) -> {
            Inventory inv = estoque.stream()
                    .filter(x -> x.getProduct() != null && productId.equals(x.getProduct().getId()))
                    .findFirst()
                    .orElse(null);
            if (inv == null) {
                if (sinal < 0) return;
                Product produto = productRepository.findById(productId).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado: " + productId));
                inv = new Inventory();
                inv.setProduct(produto);
                inv.setQuantity(qtd);
            } else {
                int atual = inv.getQuantity() != null ? inv.getQuantity() : 0;
                inv.setQuantity(Math.max(0, atual + sinal * qtd));
            }
            inventoryRepository.save(inv);
        });
    }

    private void validarPreencherECalcular(Purchase p) {
        if (p.getPurchasedItems() == null || p.getPurchasedItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A compra deve possuir pelo menos 1 item!");
        }
        if (p.getUser() == null || vazio(p.getUser().getId())
                || p.getMarket() == null || vazio(p.getMarket().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "user e market (com id) são obrigatórios!");
        }
        for (PurchaseItem i : p.getPurchasedItems()) {
            if (vazio(i.getProductId()) || i.getUnitPrice() == null || i.getQuantity() == null
                    || i.getQuantity() <= 0 || i.getUnitPrice().signum() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada item precisa de productId, quantity > 0 e unitPrice >= 0!");
            }
        }
        p.setUser(userRepository.findById(p.getUser().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado!")));
        p.setMarket(marketRepository.findById(p.getMarket().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Supermercado não encontrado!")));
        for (PurchaseItem i : p.getPurchasedItems()) {
            if (!productRepository.existsById(i.getProductId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado: " + i.getProductId());
            }
        }
        if (p.getDate() == null) p.setDate(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem i : p.getPurchasedItems()) {
            total = total.add(i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
        }
        p.setTotalValue(total.setScale(2, RoundingMode.HALF_UP));
    }

    private boolean vazio(String s) { return s == null || s.isBlank(); }
}
