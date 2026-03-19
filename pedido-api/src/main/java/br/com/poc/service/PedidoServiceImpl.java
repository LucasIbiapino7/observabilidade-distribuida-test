package br.com.poc.service;

import br.com.poc.client.EstoqueGrpcClient;
import br.com.poc.client.NotificacaoClient;
import br.com.poc.dto.CriarPedidoRequest;
import br.com.poc.dto.PedidoDTO;
import br.com.poc.entity.Pedido;
import br.com.poc.repository.PedidoRepository;
import br.com.poc.pedidocomum.exception.PedidoNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstoqueGrpcClient estoqueGrpcClient;
    private final NotificacaoClient notificacaoClient;

    @Override
    @Transactional
    public PedidoDTO criarPedido(CriarPedidoRequest request) {
        log.info("Criando pedido — produto: {}, quantidade: {}, cliente: {}",
                request.getProdutoId(), request.getQuantidade(), request.getClienteNome());

        // 1. Persiste o pedido com status inicial
        Pedido pedido = pedidoRepository.save(Pedido.builder()
                .produtoId(request.getProdutoId())
                .quantidade(request.getQuantidade())
                .clienteNome(request.getClienteNome())
                .status("CRIADO")
                .dataCriacao(LocalDateTime.now())
                .build());

        log.debug("Pedido {} persistido com status CRIADO", pedido.getId());

        // 2. Chama estoque-service via gRPC para reservar
        var reserva = estoqueGrpcClient.reservarEstoque(
                request.getProdutoId(),
                request.getQuantidade(),
                pedido.getId().toString()
        );

        // 3. Atualiza status conforme resposta do estoque
        if (reserva.getSucesso()) {
            pedido.setStatus("RESERVADO");
            log.info("Pedido {} com reserva confirmada. Estoque restante: {}",
                    pedido.getId(), reserva.getQuantidadeRestante());
        } else {
            pedido.setStatus("ERRO");
            log.warn("Pedido {} com reserva negada: {}", pedido.getId(), reserva.getMensagem());
        }

        pedidoRepository.save(pedido);

        // 4. Notifica via REST independente do resultado
        notificacaoClient.enviarNotificacao(
                pedido.getId(),
                request.getClienteNome(),
                reserva.getSucesso()
                        ? "Seu pedido foi confirmado!"
                        : "Não foi possível confirmar seu pedido: " + reserva.getMensagem()
        );

        return toDTO(pedido);
    }

    @Override
    public PedidoDTO buscarPedido(Long id) {
        log.debug("Buscando pedido {}", id);
        return pedidoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new PedidoNotFoundException(id));
    }

    @Override
    public List<PedidoDTO> listarPedidos() {
        return pedidoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private PedidoDTO toDTO(Pedido pedido) {
        return PedidoDTO.builder()
                .id(pedido.getId())
                .produtoId(pedido.getProdutoId())
                .quantidade(pedido.getQuantidade())
                .clienteNome(pedido.getClienteNome())
                .status(pedido.getStatus())
                .dataCriacao(pedido.getDataCriacao())
                .build();
    }
}
