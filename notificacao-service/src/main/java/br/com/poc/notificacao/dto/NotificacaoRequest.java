package br.com.poc.notificacao.dto;

import lombok.Data;

@Data
public class NotificacaoRequest {
    private Long pedidoId;
    private String clienteNome;
    private String mensagem;
}
