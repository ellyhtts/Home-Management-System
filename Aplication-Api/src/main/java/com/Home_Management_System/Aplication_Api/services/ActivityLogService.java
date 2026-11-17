package com.Home_Management_System.Aplication_Api.services;

import com.Home_Management_System.Aplication_Api.models.ActivityLog;
import com.Home_Management_System.Aplication_Api.models.User;
import com.Home_Management_System.Aplication_Api.repositories.ActivityLogRepository;
import com.Home_Management_System.Aplication_Api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ActivityLogService {

    public static final String PARAM_USUARIO = "userId";

    private static final Logger LOG = LoggerFactory.getLogger(ActivityLogService.class);
    private static final Locale PT_BR = Locale.of("pt", "BR");

    @Autowired private ActivityLogRepository activityLogRepository;
    @Autowired private UserRepository userRepository;

    public List<ActivityLog> listarTodos() { return activityLogRepository.findAll(); }

    public Optional<ActivityLog> buscarPorId(String id) { return activityLogRepository.findById(id); }

    public ActivityLog salvar(ActivityLog log) {
        if (log.getAction() == null || log.getAction().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A ação é obrigatória!");
        }
        if (log.getUser() == null || log.getUser().getId() == null || log.getUser().getId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O usuário (com id) é obrigatório!");
        }
        log.setUser(userRepository.findById(log.getUser().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado!")));
        log.setId(null);
        if (log.getTimestamp() == null) log.setTimestamp(LocalDateTime.now());
        return activityLogRepository.save(log);
    }

    public void deletar(String id) {
        if (!activityLogRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log não encontrado!");
        }
        activityLogRepository.deleteById(id);
    }

    public void registrar(String acao) {
        registrar(acao, null);
    }

    public void registrar(String acao, String usuarioPadrao) {
        try {
            String userId = usuarioDaRequisicao();
            if (userId == null || userId.isBlank()) userId = usuarioPadrao;
            if (userId == null || userId.isBlank()) {
                LOG.warn("Atividade não registrada (informe ?{}=<id do usuário>): {}", PARAM_USUARIO, acao);
                return;
            }
            User usuario = new User();
            usuario.setId(userId.trim());
            ActivityLog log = new ActivityLog();
            log.setUser(usuario);
            log.setAction(acao);
            salvar(log);
        } catch (RuntimeException e) {
            LOG.warn("Não foi possível registrar a atividade '{}': {}", acao, e.getMessage());
        }
    }

    public static String moeda(BigDecimal valor) {
        BigDecimal v = valor != null ? valor : BigDecimal.ZERO;
        return "R$ " + String.format(PT_BR, "%,.2f", v);
    }

    private String usuarioDaRequisicao() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest().getParameter(PARAM_USUARIO);
        }
        return null;
    }
}
