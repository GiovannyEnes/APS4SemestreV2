package com.giovannyenes.estruturadados.service;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;
import com.opencsv.CSVReader;



@Service
public class CsvLoaderService {

    private final DadosDesmatamentoRepository repository;

    public CsvLoaderService(DadosDesmatamentoRepository repository) {
        this.repository = repository;
    }

    public void carregarCSV(String pasta) {
        // LIMPAR O BANCO ANTES DE CARREGAR
        long countAntes = repository.count();
        if (countAntes > 0) {
            System.out.println("⚠️ Banco já contém " + countAntes + " registros. Limpando...");
            repository.deleteAll();
            System.out.println("✅ Banco limpo!");
        }
        
        File dir = new File(pasta);
        // CARREGAR APENAS O merged_data.csv para evitar duplicação
        File[] arquivos = dir.listFiles((d, name) -> 
            name.equalsIgnoreCase("merged_data.csv"));
        
        if (arquivos == null || arquivos.length == 0) {
            System.err.println("❌ Arquivo merged_data.csv não encontrado na pasta: " + pasta);
            System.out.println("ℹ️ Tentando carregar todos os CSVs individuais...");
            // Fallback: carregar arquivos individuais se merged não existir
            arquivos = dir.listFiles((d, name) -> 
                name.toLowerCase().endsWith(".csv") && !name.equalsIgnoreCase("merged_data.csv"));
        }
        
        if (arquivos == null || arquivos.length == 0) {
            System.err.println("❌ Nenhum arquivo CSV encontrado na pasta: " + pasta);
            return;
        }

        // possíveis formatos de data
        DateTimeFormatter[] formatos = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
        };

        for (File arquivo : arquivos) {
            try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
                List<String[]> linhas = reader.readAll();
                List<DadosDesmatamento> lista = new ArrayList<>();

                if (linhas.size() <= 1) {
                    System.out.println("⚠️ Arquivo vazio ou sem dados: " + arquivo.getName());
                    continue;
                }

                for (String[] l : linhas.subList(1, linhas.size())) {
                    try {
                        if (l == null || l.length < 9) continue;
                        for (int i = 0; i < l.length; i++) {
                            if (l[i] != null) l[i] = l[i].trim();
                        }

                        String idBdq = l[0];
                        String focoId = l[1];
                        double latitude = Double.parseDouble(l[2].replace(",", "."));
                        double longitude = Double.parseDouble(l[3].replace(",", "."));
                        String dataStr = l[4];
                        String pais = l[5];
                        String estado = l[6];
                        String municipio = l[7];
                        String bioma = l[8];

                        LocalDate data = parseData(dataStr, formatos);
                        if (data == null) {
                            System.err.println("⚠️ Não foi possível converter a data: " + dataStr);
                            continue;
                        }

                        DadosDesmatamento area = new DadosDesmatamento(
                                idBdq, focoId, latitude, longitude,
                                pais, estado, municipio, bioma, data
                        );

                        lista.add(area);

                    } catch (Exception ex) {
                        System.err.println("⚠️ Erro ao processar linha: " + String.join(",", l) + " -> " + ex.getMessage());
                    }
                }

                if (!lista.isEmpty()) {
                    repository.saveAll(lista);
                    System.out.println("✅ " + arquivo.getName() + " importado (" + lista.size() + " registros)");
                } else {
                    System.out.println("⚠️ Nenhum registro válido no arquivo " + arquivo.getName());
                }

            } catch (Exception e) {
                System.err.println("❌ Erro ao processar " + arquivo.getName() + ": " + e.getMessage());
            }
        }

        long totalFinal = repository.count();
        System.out.println("🎉 Todos os arquivos foram processados!");
        System.out.println("📊 Total de registros no banco: " + totalFinal);
    }

    private LocalDate parseData(String dataStr, DateTimeFormatter... formatters) {
        if (dataStr == null || dataStr.isBlank()) return null;

        dataStr = dataStr.trim();

        for (DateTimeFormatter f : formatters) {
            try {
                return LocalDate.parse(dataStr, f);
            } catch (DateTimeParseException ignored) {}
        }

        // fallback para casos tipo "2003-05-15 00:00:00"
        String possibleDateOnly = dataStr.split(" ")[0];
        try {
            return LocalDate.parse(possibleDateOnly, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ignored) {}

        return null;
    }
}