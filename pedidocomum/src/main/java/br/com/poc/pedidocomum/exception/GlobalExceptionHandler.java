package br.com.poc.pedidocomum.exception;

import br.com.poc.pedidocomum.error.ErrorReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handler global de exceções.
 *
 * Regras que este handler segue intencionalmente:
 * 1. Um único log.error() por exceção — sem duplicação
 * 2. Helpers de formatação não chamam log.*() — apenas formatam string
 * 3. O requestId vem do MDC (já foi colocado lá pelo RequestIdFilter)
 * 4. A stacktrace só é logada para exceções inesperadas (500)
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorReportService errorReportService;

    @ExceptionHandler(PedidoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePedidoNotFound(PedidoNotFoundException ex) {
        log.warn("[requestId={}] Pedido não encontrado: {}", getRequestId(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildBody(ex.getMessage(), 404));
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleEstoqueInsuficiente(EstoqueInsuficienteException ex) {
        log.warn("[requestId={}] Estoque insuficiente: {}", getRequestId(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(buildBody(ex.getMessage(), 422));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("[requestId={}] Erro inesperado: {}", getRequestId(), ex.getMessage(), ex);
        errorReportService.salvar(ex, request);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildBody("Erro interno. Contate o administrador.", 500));
    }

    // --- helpers de formatação — nenhum deles chama log.*() ---

    private String getRequestId() {
        String id = MDC.get("X-Request-ID");
        return id != null ? id : "N/A";
    }

    private Map<String, Object> buildBody(String mensagem, int status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("mensagem", mensagem);
        body.put("requestId", getRequestId());
        return body;
    }
}
