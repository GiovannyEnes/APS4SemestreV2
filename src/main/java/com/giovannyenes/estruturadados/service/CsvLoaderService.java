package com.giovannyenes.estruturadados.service;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;



@Service
public class CsvLoaderService {

    private final DadosDesmatamentoRepository repository;

    public CsvLoaderService(DadosDesmatamentoRepository repository) {
        this.repository = repository;
    }

    public void carregarCSV(String pasta) {
        File dir = new File(pasta);
        File[] arquivos = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".csv"));
        if (arquivos == null || arquivos.length == 0) {
            System.err.println("❌ Nenhum arquivo CSV encontrado na pasta: " + pasta);
            return;
        }

        // formatos comuns que o CSV pode trazer
        DateTimeFormatter fmtFull = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter fmtDate1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter fmtDate2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtDate3 = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (File arquivo : arquivos) {
            try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
                List<String[]> linhas = reader.readAll();
                List<DadosDesmatamento> lista = new ArrayList<>();

                // se tiver só cabeçalho ou estiver vazio, pula
                if (linhas.size() <= 1) {
                    System.out.println("⚠️ Arquivo vazio ou sem dados: " + arquivo.getName());
                    continue;
                }

                // pula o cabeçalho (assumindo primeira linha cabeçalho)
                for (String[] l : linhas.subList(1, linhas.size())) {
                    try {
                        if (l == null || l.length < 9) continue;

                        // trim em todas as colunas
                        for (int i = 0; i < l.length; i++) {
                            if (l[i] != null) l[i] = l[i].trim();
                        }

                        // Colunas esperadas:
                        // 0 = ID_BDQ, 1 = FOCO_ID, 2 = LATITUDE, 3 = LONGITUDE, 4 = DATA, 5 = PAIS, 6 = ESTADO, 7 = MUNICIPIO, 8 = BIOMA
                        String idBdq = l[0];
                        String focoId = l[1];
                        double latitude = Double.parseDouble(l[2].replace(",", "."));
                        double longitude = Double.parseDouble(l[3].replace(",", "."));
                        String dataStr = l[4] != null ? l[4].trim() : "";
                        String pais = l[5];
                        String estado = l[6];
                        String municipio = l[7];
                        String bioma = l[8];

                        Integer ano = parseYear(dataStr, fmtFull, fmtDate1, fmtDate2, fmtDate3);
                        if (ano == null) {
                            // se não conseguir extrair, ignora esta linha (ou definir fallback)
                            System.err.println("⚠️ Não foi possível extrair ano da data: '" + dataStr + "' (linha ignorada)");
                            continue;
                        }

                        DadosDesmatamento area = new DadosDesmatamento(
                                idBdq,
                                focoId,
                                latitude,
                                longitude,
                                pais,
                                estado,
                                municipio,
                                bioma,
                                ano
                        );

                        lista.add(area);

                    } catch (NumberFormatException nf) {
                        System.err.println("⚠️ Formato numérico inválido na linha: " + String.join(",", l));
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

        System.out.println("🎉 Todos os arquivos foram processados!");
    }

    /**
     * Tenta extrair o ano a partir de diferentes formatos de data.
     * Retorna null se não for possível extrair.
     */
    private Integer parseYear(String dataStr, DateTimeFormatter... formatters) {
        if (dataStr == null || dataStr.isBlank()) return null;

        dataStr = dataStr.trim();

        // se for apenas ano (ex: "2003")
        if (dataStr.matches("^\\d{4}$")) {
            return Integer.parseInt(dataStr);
        }

        // tenta com os formatters passados
        for (DateTimeFormatter f : formatters) {
            try {
                LocalDate d = LocalDate.parse(dataStr, f);
                return d.getYear();
            } catch (DateTimeParseException ignored) {}
        }

        // alguns CSVs têm "2003-05-15 00:00:00" — DateTimeFormatter usado pode não aceitar hora; vamos tentar cortar
        String possibleDateOnly = dataStr.split(" ")[0];
        if (possibleDateOnly.matches("\\d{4}-\\d{2}-\\d{2}")) {
            try {
                LocalDate d = LocalDate.parse(possibleDateOnly, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return d.getYear();
            } catch (DateTimeParseException ignored) {}
        }

        // fallback: tenta extrair os quatro primeiros dígitos se parecem ano
        if (dataStr.length() >= 4 && dataStr.substring(0,4).matches("\\d{4}")) {
            return Integer.parseInt(dataStr.substring(0,4));
        }

        return null;
    }
}