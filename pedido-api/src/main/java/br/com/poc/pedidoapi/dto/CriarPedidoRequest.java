package br.com.poc.pedidoapi.dto;

import lombok.Data;

@Data
public class CriarPedidoRequest {
    private String produtoId;
    private int quantidade;
    private String clienteNome;
}