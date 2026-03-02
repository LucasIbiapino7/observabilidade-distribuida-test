package br.com.poc.pedidocomum.grpc;

import io.grpc.*;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcServerInterceptor implements ServerInterceptor {

    private final OpenTelemetry openTelemetry;

    private static final Metadata.Key<String> REQUEST_ID_KEY =
            Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);

    private static final TextMapGetter<Metadata> GETTER = new TextMapGetter<>() {
        @Override
        public Iterable<String> keys(Metadata carrier) {
            return Collections.emptyList();
        }

        @Override
        public String get(Metadata carrier, String key) {
            Metadata.Key<String> metaKey =
                    Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER);
            return carrier.get(metaKey);
        }
    };

    @Override
    public <Q, R> ServerCall.Listener<Q> interceptCall(ServerCall<Q, R> call,
                                                       Metadata headers,
                                                       ServerCallHandler<Q, R> next) {
        // Extrai contexto OTel do Metadata
        io.opentelemetry.context.Context otelContext = openTelemetry.getPropagators()
                .getTextMapPropagator()
                .extract(io.opentelemetry.context.Context.current(), headers, GETTER);

        // Propaga X-Request-ID para o MDC
        String requestId = headers.get(REQUEST_ID_KEY);
        if (requestId != null) {
            MDC.put("X-Request-ID", requestId);
        }

        // Ativa o contexto OTel e mantém ativo durante todo o processamento
        // NÃO usa try-with-resources — o scope precisa sobreviver ao return
        io.opentelemetry.context.Scope scope = otelContext.makeCurrent();
        try {
            return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
                    next.startCall(call, headers)) {
                @Override
                public void onComplete() {
                    try {
                        super.onComplete();
                    } finally {
                        scope.close();
                        MDC.remove("X-Request-ID");
                    }
                }

                @Override
                public void onCancel() {
                    try {
                        super.onCancel();
                    } finally {
                        scope.close();
                        MDC.remove("X-Request-ID");
                    }
                }
            };
        } catch (Exception e) {
            scope.close();
            MDC.remove("X-Request-ID");
            throw e;
        }
    }
}
