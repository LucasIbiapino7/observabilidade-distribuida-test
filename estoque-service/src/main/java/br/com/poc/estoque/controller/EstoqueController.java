package br.com.poc.estoque.controller;

import br.com.poc.estoque.entity.Produto;
import br.com.poc.estoque.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("/{produtoId}")
    public ResponseEntity<Produto> consultar(@PathVariable String produtoId) {
        log.debug("REST GET /estoque/{}", produtoId);
        return ResponseEntity.ok(estoqueService.consultarEstoque(produtoId));
    }
}
