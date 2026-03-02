package br.com.poc.estoque;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"br.com.poc.estoque",
		"br.com.poc.pedidocomum"  // necessário para o Spring encontrar os beans do comum
})
public class EstoqueApplication {
	public static void main(String[] args) {
		SpringApplication.run(EstoqueApplication.class, args);
	}
}
