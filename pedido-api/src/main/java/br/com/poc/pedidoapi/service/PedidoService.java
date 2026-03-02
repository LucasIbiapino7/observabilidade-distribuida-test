package br.com.poc.pedidoapi.service;

import br.com.poc.pedidoapi.dto.CriarPedidoRequest;
import br.com.poc.pedidoapi.dto.PedidoDTO;

import java.util.List;

public interface PedidoService {
    PedidoDTO criarPedido(CriarPedidoRequest request);
    PedidoDTO buscarPedido(Long id);
    List<PedidoDTO> listarPedidos();
}