package br.com.poc.estoque.config;

import br.com.poc.estoque.entity.Produto;
import br.com.poc.estoque.repository.ProdutoRepository;
import br.com.poc.pedidocomum.grpc.GrpcServerInterceptor;
import br.com.poc.estoque.grpc.EstoqueGrpcService;
import br.com.poc.estoque.service.EstoqueService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Popula o H2 com produtos de exemplo e sobe o servidor gRPC.
 * Tudo em um único lugar para manter o POC simples.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EstoqueDataLoader {

    private final ProdutoRepository produtoRepository;
    private final EstoqueService estoqueService;
    private final GrpcServerInterceptor grpcServerInterceptor;

    @Value("${grpc.server.port:9001}")
    private int grpcPort;

    private Server grpcServer;

    @PostConstruct
    public void init() throws IOException {
        carregarProdutos();
        subirServidorGrpc();
    }

    private void carregarProdutos() {
        produtoRepository.save(Produto.builder()
                .produtoId("PROD-001").nome("Notebook").quantidadeDisponivel(10).build());
        produtoRepository.save(Produto.builder()
                .produtoId("PROD-002").nome("Mouse").quantidadeDisponivel(50).build());
        produtoRepository.save(Produto.builder()
                .produtoId("PROD-003").nome("Teclado").quantidadeDisponivel(30).build());

        log.info("Produtos carregados no H2: PROD-001 (10), PROD-002 (50), PROD-003 (30)");
    }

    private void subirServidorGrpc() throws IOException {
        grpcServer = ServerBuilder.forPort(grpcPort)
                .addService(new EstoqueGrpcService(estoqueService))
                .intercept(grpcServerInterceptor)  // <-- aqui entra a propagação OTel
                .build()
                .start();

        log.info("Servidor gRPC do estoque-service iniciado na porta {}", grpcPort);
    }

    @PreDestroy
    public void shutdown() {
        if (grpcServer != null) {
            grpcServer.shutdown();
            log.info("Servidor gRPC encerrado");
        }
    }
}