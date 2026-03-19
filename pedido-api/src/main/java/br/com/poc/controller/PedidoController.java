package br.com.poc.controller;

import br.com.poc.dto.CriarPedidoRequest;
import br.com.poc.dto.PedidoDTO;
import br.com.poc.service.PedidoService;
import br.com.poc.pedidocomum.exception.EstoqueInsuficienteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDTO> criarPedido(@RequestBody CriarPedidoRequest request) {
        PedidoDTO pedido = pedidoService.criarPedido(request);

        if ("ERRO".equals(pedido.getStatus())) {
            throw new EstoqueInsuficienteException(pedido.getProdutoId(), request.getQuantidade(), pedido.getQuantidade());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> buscar(@PathVariable Long id) {
        log.debug("GET /pedidos/{}", id);
        return ResponseEntity.ok(pedidoService.buscarPedido(id));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listar() {
        return ResponseEntity.ok(pedidoService.listarPedidos());
    }
}
