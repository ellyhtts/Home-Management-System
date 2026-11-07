package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.ActivityLog;
import com.Home_Management_System.Aplication_Api.models.Inventory;
import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.ActivityLogRepository;
import com.Home_Management_System.Aplication_Api.repositories.InventoryRepository;
import com.Home_Management_System.Aplication_Api.repositories.ProductRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository,
                            ActivityLogRepository activityLogRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.activityLogRepository = activityLogRepository;
    }

    public List<Inventory> listarTodos() {
        return inventoryRepository.findAll();
    }

    public Inventory buscarPorId(String id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de estoque não encontrado com o ID: " + id));
    }

    public List<Inventory> buscarEstoqueBaixo() {
        return inventoryRepository.findLowStock();
    }

    public List<Inventory> buscarPorLocal(String local) {
        return inventoryRepository.findByStorageLocationIgnoreCase(local);
    }

    public Inventory salvar(Inventory inventory) {

        if (inventory.getQuantity() == null || inventory.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade em estoque não pode ser negativa.");
        }
        if (inventory.getMinimumQuantity() != null && inventory.getMinimumQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A quantidade mínima não pode ser negativa.");
        }


        if (inventory.getProduct() == null || inventory.getProduct().getId() == null ||
                !productRepository.existsById(inventory.getProduct().getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto vinculado não foi encontrado no banco de dados.");
        }

        if (inventory.getResponsibleUser() == null || inventory.getResponsibleUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário responsável pela operação é obrigatório.");
        }
        User usuario = userRepository.findById(inventory.getResponsibleUser().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário responsável não encontrado."));

        inventory.setLastUpdated(LocalDateTime.now());
        boolean isNovo = (inventory.getId() == null);
        Inventory itemSalvo = inventoryRepository.save(inventory);


        String acao = isNovo ? "CADASTRO_ESTOQUE" : "ATUALIZACAO_ESTOQUE";
        String detalhes = "Estoque do produto [" + itemSalvo.getProduct().getId() + "] ajustado para " + itemSalvo.getQuantity() + " unid.";
        activityLogRepository.save(new ActivityLog(acao, detalhes, usuario));

        return itemSalvo;
    }

    public Inventory atualizar(String id, Inventory inventoryAtualizado) {
        buscarPorId(id); // Garante que o item existe antes de atualizar
        inventoryAtualizado.setId(id);
        return salvar(inventoryAtualizado);
    }

    public void deletar(String id) {
        Inventory item = buscarPorId(id);
        inventoryRepository.deleteById(id);

        if (item.getResponsibleUser() != null) {
            activityLogRepository.save(new ActivityLog(
                    "REMOCAO_ESTOQUE",
                    "Item do estoque ID [" + id + "] foi removido.",
                    item.getResponsibleUser()
            ));
        }
    }
}