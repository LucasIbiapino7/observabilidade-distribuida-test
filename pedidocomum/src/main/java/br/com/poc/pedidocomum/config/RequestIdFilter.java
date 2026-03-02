package br.com.poc.pedidocomum.config;

import io.opentelemetry.api.trace.Span;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro que garante que toda requisição HTTP tenha um X-Request-ID.
 *
 * Com OTel implementado em todos os serviços, o traceId já é o identificador
 * principal de correlação. O papel deste filtro se resume a devolver esse
 * traceId no header de resposta para que o cliente externo possa referenciá-lo
 * em caso de erro — unificando os dois identificadores em um único valor.
 *
 * O MDC não precisa mais ser alimentado manualmente aqui — o
 * opentelemetry-logback-mdc-1.0 injeta traceId e spanId automaticamente
 * em cada log via contexto OTel.
 *
 * VERSÃO ANTERIOR (sem OTel):
 * Gerava um UUID novo ou reutilizava o header recebido, colocava no MDC
 * manualmente e devolvia no header de resposta. Era a única forma de
 * correlacionar logs entre serviços antes do OTel existir como padrão estável.
 */
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Com OTel: usa o traceId já gerado pelo SDK como X-Request-ID.
        // O cliente recebe o mesmo ID que aparece no Jaeger — busca direta.
        String traceId = Span.current().getSpanContext().getTraceId();
        String requestId = isValidTraceId(traceId)
                ? traceId
                : UUID.randomUUID().toString(); // fallback se OTel não estiver ativo

        response.setHeader(REQUEST_ID_HEADER, requestId);

        // MDC para o X-Request-ID ainda é útil nos logs do terminal
        // enquanto o Jaeger não é a ferramenta principal de diagnóstico
        MDC.put(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Sempre limpa o MDC ao fim da requisição
            // para não vazar contexto entre threads (pool de threads reutiliza)
            MDC.remove(REQUEST_ID_HEADER);
        }

        /*
         * VERSÃO ANTERIOR — mantida como referência histórica
         *
         * String requestId = request.getHeader(REQUEST_ID_HEADER);
         * if (requestId == null || requestId.isBlank()) {
         *     requestId = UUID.randomUUID().toString();
         * }
         *
         * MDC.put(REQUEST_ID_HEADER, requestId);
         * response.setHeader(REQUEST_ID_HEADER, requestId);
         *
         * try {
         *     filterChain.doFilter(request, response);
         * } finally {
         *     MDC.remove(REQUEST_ID_HEADER);
         * }
         */
    }

    /**
     * Valida se o traceId retornado pelo OTel é real.
     * Quando nenhum span está ativo, o OTel retorna
     * "00000000000000000000000000000000" (32 zeros).
     */
    private boolean isValidTraceId(String traceId) {
        return traceId != null
                && !traceId.isBlank()
                && !traceId.equals("00000000000000000000000000000000");
    }
}