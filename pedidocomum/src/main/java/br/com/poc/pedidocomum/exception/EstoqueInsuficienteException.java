package br.com.poc.pedidocomum.exception;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String produtoId, int solicitado, int disponivel) {
        super(String.format(
                "Estoque insuficiente para produto %s. Solicitado: %d, Disponível: %d",
                produtoId, solicitado, disponivel
        ));
    }
}
