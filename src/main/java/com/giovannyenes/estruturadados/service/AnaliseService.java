package com.giovannyenes.estruturadados.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;


@Service
public class AnaliseService {

    private final DadosDesmatamentoRepository repository;

    public AnaliseService(DadosDesmatamentoRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todos os anos distintos, em ordem crescente.
     */
    public List<Integer> listarAnosOrdenados() {
        return repository.findAll().stream()
                .map(d -> d.getData() != null ? d.getData().getYear() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Total de focos por ano.
     */
    public Map<Integer, Long> totalFocosPorAno() {
        return repository.findAll().stream()
                .filter(d -> d.getData() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getData().getYear(),
                        Collectors.counting()
                ));
    }

    /**
     * Lista de biomas em ordem alfabética.
     */
    public List<String> listarBiomasOrdenados() {
        return repository.findAll().stream()
                .map(DadosDesmatamento::getBioma)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * Total de focos por bioma.
     */
    public Map<String, Long> totalFocosPorBioma() {
        return repository.findAll().stream()
                .filter(d -> d.getBioma() != null)
                .collect(Collectors.groupingBy(
                        DadosDesmatamento::getBioma,
                        Collectors.counting()
                ));
    }

    /**
     * Crescimento percentual entre anos consecutivos.
     * Não inclui o primeiro ano (pois não tem ano anterior para comparar).
     * Valores truncados para 2 casas decimais.
     */
    public Map<Integer, Double> crescimentoPercentualPorAno() {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = totalPorAno.keySet().stream().sorted().toList();

        Map<Integer, Double> crescimento = new LinkedHashMap<>();

        // Começa do segundo ano (i=1) para ter comparação com ano anterior
        for (int i = 1; i < anos.size(); i++) {
            int anoAnterior = anos.get(i - 1);
            int anoAtual = anos.get(i);

            long anterior = totalPorAno.get(anoAnterior);
            long atual = totalPorAno.get(anoAtual);

            double percentual = anterior > 0
                    ? ((double) (atual - anterior) / anterior) * 100
                    : 0;

            // Truncar para 2 casas decimais
            percentual = Math.round(percentual * 100.0) / 100.0;

            crescimento.put(anoAtual, percentual);
        }

        return crescimento;
    }

    /**
     * ✅ Contagem de focos por mês (1-12)
     * Retorna Map<Mês, Total>
     */
    public Map<Integer, Long> contagemPorMes() {
        return repository.findAll().stream()
                .filter(d -> d.getData() != null)
                .collect(Collectors.groupingBy(
                        d -> d.getData().getMonthValue(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * ✅ Contagem de focos por estação do ano
     * Verão (DEZ-JAN-FEV), Outono (MAR-ABR-MAI), Inverno (JUN-JUL-AGO), Primavera (SET-OUT-NOV)
     */
    public Map<String, Long> contagemPorEstacao() {
        return repository.findAll().stream()
                .filter(d -> d.getData() != null)
                .collect(Collectors.groupingBy(
                        d -> getEstacao(d.getData().getMonthValue()),
                        Collectors.counting()
                ));
    }

    /**
     * ✅ Ranking dos top N municípios com mais queimadas
     */
    public Map<String, Long> rankingMunicipios(int top) {
        return repository.findAll().stream()
                .filter(d -> d.getMunicipio() != null && !d.getMunicipio().isBlank())
                .collect(Collectors.groupingBy(
                        DadosDesmatamento::getMunicipio,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(top)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * ✅ Estação com mais queimadas
     * Retorna o nome da estação e a contagem
     */
    public Map<String, Long> estacaoComMaisQueimadas() {
        Map<String, Long> porEstacao = contagemPorEstacao();
        
        return porEstacao.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> Map.of(entry.getKey(), entry.getValue()))
                .orElse(Map.of());
    }

    /**
     * ✅ Tendência geral - Previsão para o próximo ano usando regressão linear
     * Retorna o ano previsto e a quantidade estimada
     */
    public Map<String, Object> tendenciaGeral() {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = totalPorAno.keySet().stream().sorted().toList();
        
        if (anos.size() < 2) {
            return Map.of("erro", "Dados insuficientes para calcular tendência");
        }

        // Regressão linear: y = a + bx
        double[] resultado = calcularRegressaoLinear(totalPorAno);
        double a = resultado[0]; // intercepto
        double b = resultado[1]; // inclinação
        double r2 = resultado[2]; // coeficiente de determinação

        int proximoAno = anos.get(anos.size() - 1) + 1;
        int x = proximoAno - anos.get(0); // normalizar x
        long previsao = Math.round(a + b * x);
        previsao = Math.max(0, previsao); // não pode ser negativo

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("proximoAno", proximoAno);
        resposta.put("previsao", previsao);
        resposta.put("precisao", String.format("%.2f%%", r2 * 100));
        resposta.put("tendencia", b > 0 ? "CRESCENTE" : (b < 0 ? "DECRESCENTE" : "ESTÁVEL"));
        
        return resposta;
    }

    /**
     * ✅ Tendência para intervalo - Previsão para múltiplos anos futuros
     * Quanto maior o range, mais impreciso
     */
    public Map<Integer, Map<String, Object>> tendenciaIntervalo(int anosParaFrente) {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = totalPorAno.keySet().stream().sorted().toList();
        
        if (anos.size() < 2) {
            return Map.of();
        }

        double[] resultado = calcularRegressaoLinear(totalPorAno);
        double a = resultado[0];
        double b = resultado[1];
        double r2 = resultado[2];

        int anoBase = anos.get(0);
        int ultimoAno = anos.get(anos.size() - 1);

        Map<Integer, Map<String, Object>> previsoes = new LinkedHashMap<>();

        for (int i = 1; i <= anosParaFrente; i++) {
            int anoFuturo = ultimoAno + i;
            int x = anoFuturo - anoBase;
            long previsao = Math.round(a + b * x);
            previsao = Math.max(0, previsao);

            // Calcular margem de erro (aumenta com a distância)
            double margemErro = (1 - r2) * i * 10; // simplificado
            
            Map<String, Object> detalhes = new LinkedHashMap<>();
            detalhes.put("previsao", previsao);
            detalhes.put("precisao", String.format("%.2f%%", Math.max(0, (r2 * 100) - (i * 5))));
            detalhes.put("margemErro", String.format("%.1f%%", margemErro));
            detalhes.put("aviso", i > 5 ? "Previsão muito distante, baixa confiabilidade" : "");

            previsoes.put(anoFuturo, detalhes);
        }

        return previsoes;
    }

    /**
     * Método auxiliar: determina a estação com base no mês (Hemisfério Sul - Brasil)
     */
    private String getEstacao(int mes) {
        return switch (mes) {
            case 12, 1, 2 -> "Verão";
            case 3, 4, 5 -> "Outono";
            case 6, 7, 8 -> "Inverno";
            case 9, 10, 11 -> "Primavera";
            default -> "Desconhecida";
        };
    }

    /**
     * Método auxiliar: Calcula regressão linear simples
     * Retorna [intercepto, inclinação, R²]
     */
    private double[] calcularRegressaoLinear(Map<Integer, Long> dados) {
        List<Integer> anos = dados.keySet().stream().sorted().toList();
        int n = anos.size();
        int anoBase = anos.get(0);

        double somaX = 0, somaY = 0, somaXY = 0, somaX2 = 0;

        for (int i = 0; i < n; i++) {
            int ano = anos.get(i);
            double x = ano - anoBase; // normalizar
            double y = dados.get(ano);

            somaX += x;
            somaY += y;
            somaXY += x * y;
            somaX2 += x * x;
        }

        double mediaX = somaX / n;
        double mediaY = somaY / n;

        // Calcular inclinação (b) e intercepto (a)
        double b = (n * somaXY - somaX * somaY) / (n * somaX2 - somaX * somaX);
        double a = mediaY - b * mediaX;

        // Calcular R² (coeficiente de determinação)
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < n; i++) {
            int ano = anos.get(i);
            double x = ano - anoBase;
            double y = dados.get(ano);
            double yPred = a + b * x;
            
            ssRes += Math.pow(y - yPred, 2);
            ssTot += Math.pow(y - mediaY, 2);
        }
        
        double r2 = ssTot != 0 ? 1 - (ssRes / ssTot) : 0;

        return new double[]{a, b, r2};
    }
}
