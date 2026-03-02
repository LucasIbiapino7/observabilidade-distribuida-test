package br.com.poc.estoque.repository;

import br.com.poc.estoque.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByProdutoId(String produtoId);
}