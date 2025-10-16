package com.giovannyenes.estruturadados;

import com.giovannyenes.estruturadados.service.CsvLoaderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EstruturaDadosApplication implements CommandLineRunner {

    private final CsvLoaderService loaderService;

    public EstruturaDadosApplication(CsvLoaderService loaderService) {
        this.loaderService = loaderService;
    }

    public static void main(String[] args) {
        SpringApplication.run(EstruturaDadosApplication.class, args);
    }

    @Override
    public void run(String... args) {
        loaderService.carregarCSV("src/main/resources/data");
    }
}
