package br.com.poc.pedidocomum.grpc;

import io.grpc.*;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrpcClientInterceptor implements ClientInterceptor {

    private final OpenTelemetry openTelemetry;

    private static final Metadata.Key<String> REQUEST_ID_KEY =
            Metadata.Key.of("x-request-id", Metadata.ASCII_STRING_MARSHALLER);

    private static final TextMapSetter<Metadata> SETTER =
            (carrier, key, value) -> carrier.put(
                    Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value);

    @Override
    public <Q, R> ClientCall<Q, R> interceptCall(MethodDescriptor<Q, R> method,
                                                 CallOptions callOptions,
                                                 Channel next) {
        Metadata headers = new Metadata();

        // Injeta contexto OTel atual no Metadata usando o bean injetado
        openTelemetry.getPropagators()
                .getTextMapPropagator()
                .inject(Context.current(), headers, SETTER);

        // Propaga X-Request-ID do MDC
        String requestId = MDC.get("X-Request-ID");
        if (requestId != null) {
            headers.put(REQUEST_ID_KEY, requestId);
        }

        return new ForwardingClientCall.SimpleForwardingClientCall<>(
                next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<R> responseListener, Metadata originalHeaders) {
                originalHeaders.merge(headers);
                super.start(responseListener, originalHeaders);
            }
        };
    }
}