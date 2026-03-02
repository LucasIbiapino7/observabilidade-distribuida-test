package br.com.poc.pedidocomum.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long id) {
        super("Pedido não encontrado com id: " + id);
    }
}
