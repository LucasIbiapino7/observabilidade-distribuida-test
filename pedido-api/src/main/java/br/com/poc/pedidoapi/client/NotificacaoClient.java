package br.com.poc.pedidoapi.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente REST para o notificacao-service.
 *
 * O OTel instrumenta o RestClient automaticamente —
 * ele injeta os headers W3C Trace Context (traceparent)
 * em toda chamada REST de saída, propagando o trace.
 */
@Slf4j
@Component
public class NotificacaoClient {

    private final RestClient restClient;

    public NotificacaoClient(
            RestClient.Builder builder,
            @Value("${notificacao.service.url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public void enviarNotificacao(Long pedidoId, String clienteNome, String mensagem) {
        log.info("Enviando notificação para cliente {} — pedido {}", clienteNome, pedidoId);

        restClient.post()
                .uri("/notificacoes")
                .body(Map.of(
                        "pedidoId", pedidoId,
                        "clienteNome", clienteNome,
                        "mensagem", mensagem
                ))
                .retrieve()
                .toBodilessEntity();
    }
}
