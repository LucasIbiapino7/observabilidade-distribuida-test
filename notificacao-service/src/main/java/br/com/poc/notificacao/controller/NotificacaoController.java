package br.com.poc.notificacao.controller;

import br.com.poc.notificacao.dto.NotificacaoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    @PostMapping
    public ResponseEntity<Void> receberNotificacao(@RequestBody NotificacaoRequest request) {
        log.info("Notificação recebida — pedido: {}, cliente: {}, mensagem: {}",
                request.getPedidoId(),
                request.getClienteNome(),
                request.getMensagem());

        return ResponseEntity.ok().build();
    }
}
