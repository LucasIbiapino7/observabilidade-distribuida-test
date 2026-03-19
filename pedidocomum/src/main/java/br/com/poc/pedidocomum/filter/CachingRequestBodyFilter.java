package br.com.poc.pedidocomum.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class CachingRequestBodyFilter extends OncePerRequestFilter {

    private static final int CACHE_LIMIT = 10 * 1024; // 10KB

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrapper =
                new ContentCachingRequestWrapper(request, CACHE_LIMIT);

        filterChain.doFilter(wrapper, response);
    }
}