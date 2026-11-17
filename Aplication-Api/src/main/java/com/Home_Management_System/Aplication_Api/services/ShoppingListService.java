package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.Product;
import com.Home_Management_System.Aplication_Api.models.ShoppingList;
import com.Home_Management_System.Aplication_Api.models.ShoppingListItem;
import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import com.Home_Management_System.Aplication_Api.repositories.ShoppingListRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShoppingListService {

    public static final String TITULO_LISTA_REPOSICAO = "Reposição - Estoque baixo";

    @Autowired private ShoppingListRepository shoppingListRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ActivityLogService activityLogService;

    public List<ShoppingList> listarTodas() { return shoppingListRepository.findAll(); }

    public Optional<ShoppingList> buscarPorId(String id) { return shoppingListRepository.findById(id); }

    public ShoppingList salvar(ShoppingList list) {
        validar(list);
        list.setUser(buscarUsuario(list.getUser()));
        list.setId(null);
        if (list.getDate() == null) list.setDate(LocalDateTime.now());
        ShoppingList salva = shoppingListRepository.save(list);
        activityLogService.registrar("Criou a lista de compras '" + list.getTitle()
                + "' com " + list.getItems().size() + " item(ns)", list.getUser().getId());
        return salva;
    }

    public ShoppingList atualizar(String id, ShoppingList dados) {
        ShoppingList existente = shoppingListRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lista de compras não encontrada!"));
        validar(dados);

        String tituloAnterior = existente.getTitle();
        Map<String, ShoppingListItem> itensAntigos = itensPorProduto(existente.getItems());

        existente.setTitle(dados.getTitle());
        if (dados.getDate() != null) existente.setDate(dados.getDate());
        existente.setItems(dados.getItems());
        ShoppingList salva = shoppingListRepository.save(existente);

        registrarMudancas(tituloAnterior, itensAntigos, existente, idDoCriador(existente));
        return salva;
    }

    /**
     * Coloca produtos na lista "Reposição - Estoque baixo" (criada se ainda não existir).
     * - produto que ainda não está na lista: é adicionado;
     * - produto que já está e não foi comprado: fica como está (sem duplicar);
     * - produto que já foi marcado como comprado: volta para "não comprado".
     */
    public ShoppingList adicionarItensDeReposicao(List<ShoppingListItem> novosItens, String userId) {
        User criador = new User();
        criador.setId(userId);
        buscarUsuario(criador); // 400 se faltar o id, 404 se o usuário não existir

        if (novosItens == null || novosItens.isEmpty()) return null;

        ShoppingList existente = shoppingListRepository.findAll().stream()
                .filter(l -> TITULO_LISTA_REPOSICAO.equalsIgnoreCase(l.getTitle()))
                .findFirst().orElse(null);

        if (existente == null) {
            ShoppingList nova = new ShoppingList();
            nova.setUser(criador);
            nova.setTitle(TITULO_LISTA_REPOSICAO);
            nova.setItems(new ArrayList<>(novosItens));
            return salvar(nova);
        }

        List<ShoppingListItem> mesclados = new ArrayList<>();
        if (existente.getItems() != null) {
            for (ShoppingListItem i : existente.getItems()) mesclados.add(copia(i));
        }
        for (ShoppingListItem novo : novosItens) {
            ShoppingListItem atual = mesclados.stream()
                    .filter(m -> novo.getProductId().equals(m.getProductId()))
                    .findFirst().orElse(null);
            if (atual == null) {
                mesclados.add(copia(novo));
            } else if (Boolean.TRUE.equals(atual.getIsPurchased())) {
                atual.setIsPurchased(false);
                atual.setQuantityNeeded(novo.getQuantityNeeded());
            }
        }
        ShoppingList dados = new ShoppingList();
        dados.setTitle(existente.getTitle());
        dados.setItems(mesclados);
        return atualizar(existente.getId(), dados);
    }

    private ShoppingListItem copia(ShoppingListItem i) {
        ShoppingListItem c = new ShoppingListItem();
        c.setProductId(i.getProductId());
        c.setQuantityNeeded(i.getQuantityNeeded());
        c.setIsPurchased(i.getIsPurchased());
        return c;
    }

    public void deletar(String id) {
        if (!shoppingListRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lista de compras não encontrada!");
        }
        ShoppingList existente = shoppingListRepository.findById(id).orElse(null);
        String titulo = existente != null ? existente.getTitle() : "sem título";
        String criador = existente != null ? idDoCriador(existente) : null;
        shoppingListRepository.deleteById(id);
        activityLogService.registrar("Excluiu a lista de compras '" + titulo + "'", criador);
    }

    /** Compara a lista antiga com a nova e registra cada mudança com uma frase própria. */
    private void registrarMudancas(String tituloAnterior,
                                   Map<String, ShoppingListItem> antigos,
                                   ShoppingList atual,
                                   String usuarioPadrao) {
        String titulo = atual.getTitle();
        Map<String, ShoppingListItem> novos = itensPorProduto(atual.getItems());
        List<String> mensagens = new ArrayList<>();

        if (!Objects.equals(tituloAnterior, titulo)) {
            mensagens.add("Renomeou a lista de compras '" + tituloAnterior + "' para '" + titulo + "'");
        }

        novos.forEach((productId, novo) -> {
            ShoppingListItem antigo = antigos.get(productId);
            String produto = nomeProduto(productId);
            if (antigo == null) {
                mensagens.add("Adicionou o produto '" + produto + "' à lista de compras '" + titulo + "'");
                return;
            }
            if (!Objects.equals(antigo.getQuantityNeeded(), novo.getQuantityNeeded())) {
                mensagens.add("Alterou a quantidade de '" + produto + "' na lista de compras '" + titulo
                        + "' de " + antigo.getQuantityNeeded() + " para " + novo.getQuantityNeeded());
            }
            boolean eraComprado = Boolean.TRUE.equals(antigo.getIsPurchased());
            boolean ficouComprado = Boolean.TRUE.equals(novo.getIsPurchased());
            if (!eraComprado && ficouComprado) {
                mensagens.add("Marcou '" + produto + "' como comprado na lista de compras '" + titulo + "'");
            } else if (eraComprado && !ficouComprado) {
                mensagens.add("Desmarcou '" + produto + "' como comprado na lista de compras '" + titulo + "'");
            }
        });

        antigos.keySet().stream()
                .filter(productId -> !novos.containsKey(productId))
                .forEach(productId -> mensagens.add("Removeu o produto '" + nomeProduto(productId)
                        + "' da lista de compras '" + titulo + "'"));

        if (mensagens.isEmpty()) {
            mensagens.add("Atualizou a lista de compras '" + titulo + "'");
        }
        mensagens.forEach(m -> activityLogService.registrar(m, usuarioPadrao));
    }

    private String idDoCriador(ShoppingList l) {
        return l.getUser() != null ? l.getUser().getId() : null;
    }

    /** O usuário (com id) é obrigatório na criação; devolve a entidade completa do banco. */
    private User buscarUsuario(User informado) {
        if (informado == null || informado.getId() == null || informado.getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário (com id) é obrigatório!");
        }
        return userRepository.findById(informado.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!"));
    }

    private Map<String, ShoppingListItem> itensPorProduto(List<ShoppingListItem> itens) {
        Map<String, ShoppingListItem> mapa = new LinkedHashMap<>();
        if (itens != null) {
            for (ShoppingListItem i : itens) {
                if (i != null && i.getProductId() != null) mapa.putIfAbsent(i.getProductId(), i);
            }
        }
        return mapa;
    }

    private String nomeProduto(String productId) {
        return productRepository.findById(productId).map(Product::getName).orElse(productId);
    }

    private void validar(ShoppingList l) {
        if (l.getTitle() == null || l.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O título é obrigatório!");
        }
        if (l.getItems() == null || l.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A lista deve ter pelo menos 1 item!");
        }
        for (ShoppingListItem i : l.getItems()) {
            if (i.getProductId() == null || i.getProductId().isBlank()
                    || i.getQuantityNeeded() == null || i.getQuantityNeeded() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cada item precisa de productId e quantityNeeded > 0!");
            }
            if (i.getIsPurchased() == null) i.setIsPurchased(false);
        }
        for (ShoppingListItem i : l.getItems()) {
            if (!productRepository.existsById(i.getProductId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Produto não encontrado: " + i.getProductId());
            }
        }
    }
}
