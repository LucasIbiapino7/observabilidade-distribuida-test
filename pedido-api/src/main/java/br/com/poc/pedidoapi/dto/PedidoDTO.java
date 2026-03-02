package br.com.poc.pedidoapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PedidoDTO {
    private Long id;
    private String produtoId;
    private int quantidade;
    private String clienteNome;
    private String status;
    private LocalDateTime dataCriacao;
}
