package com.alurachallenge.literalura;

import com.alurachallenge.literalura.principal.Principal;
import com.alurachallenge.literalura.repository.Repository;
import com.alurachallenge.literalura.repository.RepositoryAutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	private Repository repository;
	@Autowired
	private RepositoryAutor repositoryAutor;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository, repositoryAutor);
		principal.iniciaTodo();
	}

}
