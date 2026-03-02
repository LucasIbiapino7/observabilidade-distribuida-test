package br.com.poc.estoque.grpc;

import br.com.poc.estoque.service.EstoqueService;
import br.com.poc.pedidocomum.exception.EstoqueInsuficienteException;
import br.com.poc.pedidocomum.grpc.proto.EstoqueServiceGrpc;
import br.com.poc.pedidocomum.grpc.proto.ReservaRequest;
import br.com.poc.pedidocomum.grpc.proto.ReservaResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do servidor gRPC.
 * Recebe chamadas de ReservarEstoque vindas do pedido-api.
 *
 * Nota: o @Component não é necessário aqui — o servidor gRPC
 * é registrado manualmente no GrpcServerConfig.
 */
@Slf4j
@RequiredArgsConstructor
public class EstoqueGrpcService extends EstoqueServiceGrpc.EstoqueServiceImplBase {

    private final EstoqueService estoqueService;

    @Override
    public void reservarEstoque(ReservaRequest request,
                                StreamObserver<ReservaResponse> responseObserver) {

        log.info("gRPC ReservarEstoque recebido — produto: {}, quantidade: {}, pedido: {}",
                request.getProdutoId(), request.getQuantidade(), request.getPedidoId());

        try {
            var produto = estoqueService.reservarEstoque(
                    request.getProdutoId(),
                    request.getQuantidade(),
                    request.getPedidoId()
            );

            var response = ReservaResponse.newBuilder()
                    .setSucesso(true)
                    .setMensagem("Reserva realizada com sucesso")
                    .setQuantidadeRestante(produto.getQuantidadeDisponivel())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (EstoqueInsuficienteException ex) {
            log.warn("Reserva negada — {}", ex.getMessage());

            var response = ReservaResponse.newBuilder()
                    .setSucesso(false)
                    .setMensagem(ex.getMessage())
                    .setQuantidadeRestante(0)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
