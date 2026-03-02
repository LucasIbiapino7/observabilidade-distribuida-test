package br.com.poc.estoque.service;

import br.com.poc.estoque.entity.Produto;

public interface EstoqueService {
    Produto consultarEstoque(String produtoId);
    Produto reservarEstoque(String produtoId, int quantidade, String pedidoId);
}
