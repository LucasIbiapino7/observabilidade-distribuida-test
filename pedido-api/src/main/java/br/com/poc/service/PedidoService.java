package br.com.poc.service;

import br.com.poc.dto.CriarPedidoRequest;
import br.com.poc.dto.PedidoDTO;

import java.util.List;

public interface PedidoService {
    PedidoDTO criarPedido(CriarPedidoRequest request);
    PedidoDTO buscarPedido(Long id);
    List<PedidoDTO> listarPedidos();
}