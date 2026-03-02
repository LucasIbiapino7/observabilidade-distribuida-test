package br.com.poc.pedidoapi.client;

import br.com.poc.pedidocomum.grpc.GrpcClientInterceptor;
import br.com.poc.pedidocomum.grpc.proto.EstoqueServiceGrpc;
import br.com.poc.pedidocomum.grpc.proto.ReservaRequest;
import br.com.poc.pedidocomum.grpc.proto.ReservaResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cliente gRPC para o estoque-service.
 *
 * O GrpcClientInterceptor do pedido-comum é registrado aqui —
 * ele injeta o contexto OTel no Metadata antes de cada chamada,
 * garantindo que o trace continue no estoque-service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EstoqueGrpcClient {

    private final GrpcClientInterceptor grpcClientInterceptor;

    @Value("${estoque.grpc.host:localhost}")
    private String host;

    @Value("${estoque.grpc.port:9001}")
    private int port;

    private ManagedChannel channel;
    private EstoqueServiceGrpc.EstoqueServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .intercept(grpcClientInterceptor)  // <-- propaga o contexto OTel
                .build();

        stub = EstoqueServiceGrpc.newBlockingStub(channel);
        log.info("Cliente gRPC do estoque-service conectado em {}:{}", host, port);
    }

    public ReservaResponse reservarEstoque(String produtoId, int quantidade, String pedidoId) {
        log.info("Chamando gRPC ReservarEstoque — produto: {}, quantidade: {}, pedido: {}",
                produtoId, quantidade, pedidoId);

        ReservaRequest request = ReservaRequest.newBuilder()
                .setProdutoId(produtoId)
                .setQuantidade(quantidade)
                .setPedidoId(pedidoId)
                .build();

        return stub.reservarEstoque(request);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }
}
