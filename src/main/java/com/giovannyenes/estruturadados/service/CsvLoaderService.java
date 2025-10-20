package com.giovannyenes.estruturadados.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

@Service
public class CsvLoaderService {

    private final DadosDesmatamentoRepository repository;
    private static final String MERGED_CSV_PATH = "src/main/resources/data/merged_data.csv";
    private static final Pattern YEAR_PATTERN = Pattern.compile("(\\d{4})");

    public CsvLoaderService(DadosDesmatamentoRepository repository) {
        this.repository = repository;
    }

    public void carregarCSV(String pasta) {
        System.out.println("🔍 Iniciando processo de carga de dados...");
        
        File dir = new File(pasta);
        File[] arquivos = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".csv") && !name.equals("merged_data.csv"));
        
        if (arquivos == null || arquivos.length == 0) {
            System.err.println("❌ Nenhum arquivo CSV encontrado na pasta: " + pasta);
            return;
        }

        // Ordenar arquivos por nome (garante ordem cronológica: 2003, 2004, etc.)
        Arrays.sort(arquivos, Comparator.comparing(File::getName));

        // 1. Determinar o ano mais recente dos arquivos CSV
        int anoMaisRecenteCSV = obterAnoMaisRecente(arquivos);
        System.out.println("📅 Ano mais recente encontrado nos CSVs: " + anoMaisRecenteCSV);

        // 2. Verificar se já existe arquivo mergeado e qual é o último ano nele
        File arquivoMergeado = new File(MERGED_CSV_PATH);
        boolean precisaMergear = true;
        
        if (arquivoMergeado.exists()) {
            System.out.println("📂 Arquivo mergeado encontrado. Verificando última data...");
            int ultimoAnoMergeado = obterUltimoAnoDoArquivoMergeado(arquivoMergeado);
            System.out.println("📅 Último ano no arquivo mergeado: " + ultimoAnoMergeado);
            
            if (ultimoAnoMergeado == anoMaisRecenteCSV) {
                System.out.println("✅ Arquivo mergeado já está atualizado. Pulando etapa de merge.");
                precisaMergear = false;
            } else {
                System.out.println("⚠️ Arquivo mergeado desatualizado. Será recriado.");
            }
        } else {
            System.out.println("📝 Arquivo mergeado não existe. Será criado.");
        }

        // 3. Mergear CSVs se necessário
        if (precisaMergear) {
            System.out.println("🔄 Iniciando processo de merge dos arquivos CSV...");
            mergearArquivosCSV(arquivos, arquivoMergeado);
        }

        // 4. Carregar dados do arquivo mergeado no H2
        System.out.println("💾 Carregando dados no banco H2...");
        carregarDadosNoH2(arquivoMergeado);

        System.out.println("🎉 Processo de carga concluído com sucesso!");
    }

    /**
     * Extrai o ano mais recente dos nomes dos arquivos CSV
     */
    private int obterAnoMaisRecente(File[] arquivos) {
        int anoMaisRecente = 0;
        for (File arquivo : arquivos) {
            Matcher matcher = YEAR_PATTERN.matcher(arquivo.getName());
            while (matcher.find()) {
                int ano = Integer.parseInt(matcher.group(1));
                if (ano > anoMaisRecente && ano >= 2000 && ano <= 2100) {
                    anoMaisRecente = ano;
                }
            }
        }
        return anoMaisRecente;
    }

    /**
     * Lê a última linha do arquivo mergeado e retorna o ano
     */
    private int obterUltimoAnoDoArquivoMergeado(File arquivo) {
        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> todasLinhas = reader.readAll();
            if (todasLinhas.size() <= 1) {
                return 0; // Arquivo vazio ou só tem header
            }
            
            String[] ultimaLinha = todasLinhas.get(todasLinhas.size() - 1);
            if (ultimaLinha.length >= 5) {
                String dataStr = ultimaLinha[4].trim(); // coluna data_pas
                LocalDate data = parseData(dataStr);
                if (data != null) {
                    return data.getYear();
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Erro ao ler última data do arquivo mergeado: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Mergeia todos os arquivos CSV em um único arquivo
     */
    private void mergearArquivosCSV(File[] arquivos, File arquivoDestino) {
        try {
            // Criar diretório se não existir
            arquivoDestino.getParentFile().mkdirs();
            
            try (CSVWriter writer = new CSVWriter(new FileWriter(arquivoDestino))) {
                boolean headerEscrito = false;
                int totalRegistros = 0;

                for (File arquivo : arquivos) {
                    System.out.println("📥 Processando: " + arquivo.getName());
                    
                    try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
                        List<String[]> linhas = reader.readAll();
                        
                        if (linhas.isEmpty()) {
                            System.out.println("⚠️ Arquivo vazio: " + arquivo.getName());
                            continue;
                        }

                        // Escrever header apenas uma vez
                        if (!headerEscrito) {
                            writer.writeNext(linhas.get(0)); // Header
                            headerEscrito = true;
                        }

                        // Escrever todas as linhas de dados (pular header)
                        for (int i = 1; i < linhas.size(); i++) {
                            writer.writeNext(linhas.get(i));
                            totalRegistros++;
                        }
                        
                        System.out.println("   ✓ " + (linhas.size() - 1) + " registros adicionados");
                        
                    } catch (Exception e) {
                        System.err.println("❌ Erro ao ler " + arquivo.getName() + ": " + e.getMessage());
                    }
                }

                System.out.println("✅ Arquivo mergeado criado com sucesso!");
                System.out.println("📊 Total de registros no arquivo mergeado: " + totalRegistros);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro crítico ao mergear arquivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carrega os dados do arquivo mergeado no banco H2 (VERSÃO OTIMIZADA)
     */
    private void carregarDadosNoH2(File arquivo) {
        if (!arquivo.exists()) {
            System.err.println("❌ Arquivo mergeado não encontrado: " + arquivo.getPath());
            return;
        }

        // Verificar se já existem dados no H2 - se sim, pular carga
        long registrosExistentes = repository.count();
        if (registrosExistentes > 0) {
            System.out.println("⚡ Banco já possui " + registrosExistentes + " registros. Pulando carga para acelerar.");
            return;
        }

        System.out.println("📊 Banco vazio. Iniciando carga rápida...");

        // Formatter mais simples e rápido
        DateTimeFormatter formatoPrincipal = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (CSVReader reader = new CSVReader(new FileReader(arquivo))) {
            List<String[]> linhas = reader.readAll();
            
            if (linhas.size() <= 1) {
                System.out.println("⚠️ Arquivo mergeado vazio ou sem dados");
                return;
            }

            int totalLinhas = linhas.size() - 1;
            List<DadosDesmatamento> lista = new ArrayList<>(totalLinhas); // Pré-alocar TUDO
            int linhasComErro = 0;

            System.out.println("📦 Processando " + totalLinhas + " linhas...");

            // Processar TODAS as linhas de uma vez
            for (int idx = 1; idx < linhas.size(); idx++) {
                String[] l = linhas.get(idx);
                
                try {
                    if (l == null || l.length < 9) {
                        linhasComErro++;
                        continue;
                    }

                    // Parse direto sem validação
                    String idBdq = l[0].trim();
                    String focoId = l[1].trim();
                    double latitude = Double.parseDouble(l[2].trim().replace(",", "."));
                    double longitude = Double.parseDouble(l[3].trim().replace(",", "."));
                    String dataStr = l[4].trim();
                    String pais = l[5].trim();
                    String estado = l[6].trim();
                    String municipio = l[7].trim();
                    String bioma = l[8].trim();

                    // Parse de data simplificado
                    LocalDate data;
                    try {
                        data = LocalDate.parse(dataStr, formatoPrincipal);
                    } catch (DateTimeParseException e) {
                        data = LocalDate.parse(dataStr.split(" ")[0]);
                    }

                    lista.add(new DadosDesmatamento(
                            idBdq, focoId, latitude, longitude,
                            pais, estado, municipio, bioma, data
                    ));

                } catch (Exception ex) {
                    linhasComErro++;
                }
            }

            System.out.println("💾 Salvando " + lista.size() + " registros de uma vez...");
            
            // SALVAR TUDO DE UMA VEZ SÓ!
            repository.saveAll(lista);

            System.out.println("✅ Carga concluída!");
            System.out.println("   📊 Registros salvos: " + lista.size());
            System.out.println("   ⚠️ Erros: " + linhasComErro);

        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar dados: " + e.getMessage());
        }
    }

    /**
     * Faz o parse da data em vários formatos possíveis
     */
    private LocalDate parseData(String dataStr, DateTimeFormatter... formatters) {
        if (dataStr == null || dataStr.isBlank()) return null;

        dataStr = dataStr.trim();

        for (DateTimeFormatter f : formatters) {
            try {
                return LocalDate.parse(dataStr, f);
            } catch (DateTimeParseException ignored) {}
        }

        // Fallback para casos tipo "2003-05-15 00:00:00"
        String possibleDateOnly = dataStr.split(" ")[0];
        try {
            return LocalDate.parse(possibleDateOnly, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException ignored) {}

        return null;
    }

    /**
     * Faz o parse robusto de coordenadas geográficas (latitude/longitude)
     * Trata múltiplos formatos: vírgulas, pontos, espaços extras
     * 
     * @param coordStr String contendo a coordenada
     * @param tipo Tipo da coordenada ("latitude" ou "longitude") para validação
     * @return Double com a coordenada parseada ou null se inválida
     */
    private Double parseCoordenada(String coordStr, String tipo) {
        if (coordStr == null || coordStr.isBlank()) {
            return null;
        }

        try {
            // Limpar a string: remover espaços extras e substituir vírgula por ponto
            String coordLimpa = coordStr.trim()
                    .replace(" ", "")
                    .replace(",", ".");

            // Parse para double
            double valor = Double.parseDouble(coordLimpa);

            // Validar se não é NaN ou Infinity
            if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                return null;
            }

            // Validação básica de range por tipo
            if (tipo.equals("latitude")) {
                // Latitude válida: -90 a 90
                if (valor < -90.0 || valor > 90.0) {
                    return null;
                }
            } else if (tipo.equals("longitude")) {
                // Longitude válida: -180 a 180
                if (valor < -180.0 || valor > 180.0) {
                    return null;
                }
            }

            return valor;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Valida se as coordenadas estão dentro de ranges esperados
     * Para Brasil, aplica validação mais específica
     * 
     * @param latitude Latitude a validar
     * @param longitude Longitude a validar
     * @param pais País de referência
     * @return true se coordenadas são válidas, false caso contrário
     */
    private boolean isCoordenadasValidas(Double latitude, Double longitude, String pais) {
        if (latitude == null || longitude == null) {
            return false;
        }

        // Validação geral (todo o mundo)
        if (latitude < -90.0 || latitude > 90.0) {
            return false;
        }
        if (longitude < -180.0 || longitude > 180.0) {
            return false;
        }

        // Validação específica para Brasil
        // Limites aproximados do Brasil:
        // Latitude: -33.75° (sul) a 5.27° (norte)
        // Longitude: -73.99° (oeste) a -28.84° (leste)
        if (pais != null && pais.trim().equalsIgnoreCase("Brasil")) {
            // Adicionar margem de tolerância de 2 graus para pontos de fronteira
            if (latitude < -35.0 || latitude > 7.0) {
                return false;
            }
            if (longitude < -76.0 || longitude > -26.0) {
                return false;
            }
        }

        // Verificar se coordenadas não são (0, 0) - geralmente indica erro
        if (Math.abs(latitude) < 0.0001 && Math.abs(longitude) < 0.0001) {
            return false;
        }

        return true;
    }
}