package br.com.poc.pedidoapi.repository;

import br.com.poc.pedidoapi.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}