package br.com.poc.pedidoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"br.com.poc.pedidoapi",
		"br.com.poc.pedidocomum"
})
public class PedidoApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(PedidoApiApplication.class, args);
	}
}
