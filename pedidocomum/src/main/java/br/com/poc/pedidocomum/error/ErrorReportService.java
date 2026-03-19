package br.com.poc.pedidocomum.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorReportService {

    private final ErrorReportRepository repository;

    @Async
    public void salvar(Exception ex, HttpServletRequest request) {
        try {
            ErrorReport report = new ErrorReport();
            report.setSource("BACKEND");
            report.setTraceId(MDC.get("trace_id"));
            report.setRequestId(MDC.get("X-Request-ID"));
            report.setMetodo(request.getMethod());
            report.setUri(request.getRequestURI());
            report.setQueryString(request.getQueryString());
            report.setIp(Optional.ofNullable(
                            request.getHeader("X-Forwarded-For"))
                    .orElse(request.getRemoteAddr()));
            report.setUserId("anonimo"); // no ITCD: SecurityContextHolder...getName()
            report.setUserAgent(request.getHeader("User-Agent"));
            report.setExceptionType(ex.getClass().getName());
            report.setRequestBody(extrairBody(request));
            report.setMensagem(ex.getMessage());
            report.setStackTrace(printStackTrace(ex));
            repository.save(report);
        } catch (Exception e) {
            log.error("Erro ao salvar ErrorReport", e);
        }
    }

    private String printStackTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private String extrairBody(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, StandardCharsets.UTF_8);
            }
        }
        return "[body indisponível]";
    }

}