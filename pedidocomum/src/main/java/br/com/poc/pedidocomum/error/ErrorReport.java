package br.com.poc.pedidocomum.error;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "error_reports")
public class ErrorReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    private String traceId;
    private String requestId;
    private String metodo;
    private String uri;
    private String queryString;
    private String ip;
    private String userId;
    private String userAgent;
    private String exceptionType;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    private LocalDateTime criadoEm;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @PrePersist
    void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}