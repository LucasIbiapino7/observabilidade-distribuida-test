package br.com.poc.estoque.service;

import br.com.poc.estoque.entity.Produto;
import br.com.poc.estoque.repository.ProdutoRepository;
import br.com.poc.pedidocomum.exception.EstoqueInsuficienteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstoqueServiceImpl implements EstoqueService {

    private final ProdutoRepository produtoRepository;

    @Override
    public Produto consultarEstoque(String produtoId) {
        log.debug("Consultando estoque do produto {}", produtoId);
        return produtoRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));
    }

    @Override
    @Transactional
    public Produto reservarEstoque(String produtoId, int quantidade, String pedidoId) {
        log.info("Reservando {} unidade(s) do produto {} para o pedido {}",
                quantidade, produtoId, pedidoId);

        Produto produto = produtoRepository.findByProdutoId(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produtoId));

        if (produto.getQuantidadeDisponivel() < quantidade) {
            throw new EstoqueInsuficienteException(produtoId, quantidade, produto.getQuantidadeDisponivel());
        }

        produto.setQuantidadeDisponivel(produto.getQuantidadeDisponivel() - quantidade);
        produtoRepository.save(produto);

        log.info("Reserva concluída. Estoque restante do produto {}: {}",
                produtoId, produto.getQuantidadeDisponivel());

        return produto;
    }
}
