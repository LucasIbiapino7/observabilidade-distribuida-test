package br.com.poc.notificacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"br.com.poc.notificacao",
		"br.com.poc.pedidocomum"
})
public class NotificacaoServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(NotificacaoServiceApplication.class, args);
	}
}
