package com.giovannyenes.estruturadados.service;

import java.util.*;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;


/**
 * Serviço responsável por realizar análises sobre os dados de desmatamento.
 *
 * 🧠 Este código aplica diferentes algoritmos clássicos:
 * - QuickSort (manual) → para ordenar listas (anos, biomas, municípios)
 * - Busca Linear → para contagens e agregações
 * - Regressão Linear → para prever tendências
 */
@Service
public class AnaliseService {

    private final DadosDesmatamentoRepository repository;

    public AnaliseService(DadosDesmatamentoRepository repository) {
        this.repository = repository;
    }

    // ============================================================
    // 🔹 ORDENAÇÃO COM QUICKSORT
    // ============================================================

    /**
     * Lista todos os anos distintos em ordem crescente.
     *
     * 🔸 Algoritmo: QuickSort (manual)
     * 🔸 Complexidade média: O(n log n)
     * 🔸 Etapas:
     *    1. Coleta os anos únicos da base de dados.
     *    2. Converte o Set em lista.
     *    3. Ordena usando QuickSort manual.
     */
    public List<Integer> listarAnosOrdenados() {
        List<DadosDesmatamento> lista = repository.findAll();
        Set<Integer> anosSet = new HashSet<>();

        // 🔍 Busca Linear — percorre toda a lista para coletar os anos.
        for (DadosDesmatamento d : lista) {
            LocalDate data = d.getData();
            if (data != null) anosSet.add(data.getYear());
        }

        // 🔢 Ordenação com QuickSort manual
        List<Integer> anosOrdenados = new ArrayList<>(anosSet);
        quickSortIntegers(anosOrdenados, 0, anosOrdenados.size() - 1);
        return anosOrdenados;
    }

    // ============================================================
    // 🔹 CONTAGEM (BUSCA LINEAR)
    // ============================================================

    /**
     * Conta o total de focos por ano.
     *
     * 🔸 Algoritmo: Busca Linear
     * 🔸 Complexidade: O(n)
     *
     * Percorre toda a lista e acumula a contagem por ano.
     */
    public Map<Integer, Long> totalFocosPorAno() {
        List<DadosDesmatamento> lista = repository.findAll();
        Map<Integer, Long> mapa = new HashMap<>();

        for (DadosDesmatamento d : lista) {
            LocalDate data = d.getData();
            if (data != null) {
                int ano = data.getYear();
                mapa.put(ano, mapa.getOrDefault(ano, 0L) + 1);
            }
        }
        return mapa;
    }

    /**
     * Lista de biomas distintos em ordem alfabética.
     *
     * 🔸 Algoritmo: QuickSort (manual)
     * 🔸 Passos:
     *    1. Coleta biomas únicos.
     *    2. Converte em lista.
     *    3. Ordena alfabeticamente com QuickSort.
     */
    public List<String> listarBiomasOrdenados() {
        List<DadosDesmatamento> lista = repository.findAll();
        Set<String> biomasSet = new HashSet<>();

        // Busca Linear — percorre todos os dados coletando biomas não nulos
        for (DadosDesmatamento d : lista) {
            String bioma = d.getBioma();
            if (bioma != null) biomasSet.add(bioma);
        }

        // QuickSort aplicado à lista de Strings
        List<String> biomasOrdenados = new ArrayList<>(biomasSet);
        quickSortStrings(biomasOrdenados, 0, biomasOrdenados.size() - 1);
        return biomasOrdenados;
    }

    /**
     * Conta o total de focos por bioma.
     *
     * 🔸 Algoritmo: Busca Linear
     * 🔸 Cada registro é percorrido uma vez e somado no mapa.
     */
    public Map<String, Long> totalFocosPorBioma() {
        List<DadosDesmatamento> lista = repository.findAll();
        Map<String, Long> mapa = new HashMap<>();

        for (DadosDesmatamento d : lista) {
            String bioma = d.getBioma();
            if (bioma != null) mapa.put(bioma, mapa.getOrDefault(bioma, 0L) + 1);
        }
        return mapa;
    }

    // ============================================================
    // 🔹 CRESCIMENTO E TENDÊNCIA
    // ============================================================

    /**
     * Calcula o crescimento percentual de queimadas entre anos consecutivos.
     *
     * 🔸 QuickSort — usado para ordenar os anos.
     * 🔸 Busca Linear — compara cada ano com o anterior.
     */
    public Map<Integer, Double> crescimentoPercentualPorAno() {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = new ArrayList<>(totalPorAno.keySet());
        quickSortIntegers(anos, 0, anos.size() - 1); // ordena anos

        Map<Integer, Double> crescimento = new LinkedHashMap<>();

        // Percorre os anos consecutivos (busca linear)
        for (int i = 1; i < anos.size(); i++) {
            int anoAnterior = anos.get(i - 1);
            int anoAtual = anos.get(i);
            long anterior = totalPorAno.get(anoAnterior);
            long atual = totalPorAno.get(anoAtual);
            double percentual = anterior > 0 ? ((double) (atual - anterior) / anterior) * 100 : 0;
            crescimento.put(anoAtual, Math.round(percentual * 100.0) / 100.0);
        }
        return crescimento;
    }

    /**
     * Conta focos por mês (1 a 12).
     *
     * 🔸 Algoritmo: Busca Linear
     * 🔸 Estrutura: TreeMap (mantém ordem crescente automaticamente)
     */
    public Map<Integer, Long> contagemPorMes() {
        List<DadosDesmatamento> lista = repository.findAll();
        Map<Integer, Long> mapa = new TreeMap<>();
        for (DadosDesmatamento d : lista) {
            LocalDate data = d.getData();
            if (data != null) {
                int mes = data.getMonthValue();
                mapa.put(mes, mapa.getOrDefault(mes, 0L) + 1);
            }
        }
        return mapa;
    }

    /**
     * Conta focos por estação do ano (Verão, Outono, Inverno, Primavera).
     *
     * 🔸 Algoritmo: Busca Linear
     */
    public Map<String, Long> contagemPorEstacao() {
        List<DadosDesmatamento> lista = repository.findAll();
        Map<String, Long> mapa = new HashMap<>();
        for (DadosDesmatamento d : lista) {
            LocalDate data = d.getData();
            if (data != null) {
                String estacao = getEstacao(data.getMonthValue());
                mapa.put(estacao, mapa.getOrDefault(estacao, 0L) + 1);
            }
        }
        return mapa;
    }

    // ============================================================
    // 🔹 RANKING DE MUNICÍPIOS
    // ============================================================

    /**
     * Retorna os top N municípios com mais queimadas.
     *
     * 🔸 Algoritmo: QuickSort (manual)
     * 🔸 Ordenação: Decrescente pelo valor (quantidade de queimadas)
     */
    public Map<String, Long> rankingMunicipios(int top) {
        List<DadosDesmatamento> lista = repository.findAll();
        Map<String, Long> contagem = new HashMap<>();

        // Busca Linear — soma as ocorrências por município
        for (DadosDesmatamento d : lista) {
            String municipio = d.getMunicipio();
            if (municipio != null && !municipio.isBlank()) {
                contagem.put(municipio, contagem.getOrDefault(municipio, 0L) + 1);
            }
        }

        // QuickSort aplicado em pares (chave, valor)
        List<Map.Entry<String, Long>> ordenada = new ArrayList<>(contagem.entrySet());
        quickSortEntries(ordenada, 0, ordenada.size() - 1);

        // Retorna apenas os top N
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(top, ordenada.size()); i++) {
            Map.Entry<String, Long> e = ordenada.get(i);
            resultado.put(e.getKey(), e.getValue());
        }
        return resultado;
    }

    // ============================================================
    // 🔹 OUTRAS ANÁLISES
    // ============================================================

    /**
     * Retorna a estação com maior número de queimadas.
     *
     * 🔸 Algoritmo: Busca Linear
     * Percorre o mapa e identifica o maior valor.
     */
    public Map<String, Long> estacaoComMaisQueimadas() {
        Map<String, Long> mapa = contagemPorEstacao();
        String maiorEstacao = null;
        long maiorValor = 0;

        for (Map.Entry<String, Long> e : mapa.entrySet()) {
            if (e.getValue() > maiorValor) {
                maiorValor = e.getValue();
                maiorEstacao = e.getKey();
            }
        }

        if (maiorEstacao == null) return Map.of();
        return Map.of(maiorEstacao, maiorValor);
    }

    /**
     * Calcula a tendência geral (regressão linear).
     *
     * 🔸 QuickSort — organiza os anos antes da regressão.
     * 🔸 Algoritmo estatístico: Regressão Linear
     *    y = a + b*x, onde:
     *      - b indica a direção (positiva = crescente, negativa = decrescente)
     *      - r² mede a precisão do modelo
     */
    public Map<String, Object> tendenciaGeral() {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = new ArrayList<>(totalPorAno.keySet());
        quickSortIntegers(anos, 0, anos.size() - 1);

        if (anos.size() < 2) {
            return Map.of("erro", "Dados insuficientes para calcular tendência");
        }

        double[] resultado = calcularRegressaoLinear(totalPorAno);
        double a = resultado[0], b = resultado[1], r2 = resultado[2];

        int proximoAno = anos.get(anos.size() - 1) + 1;
        int x = proximoAno - anos.get(0);
        long previsao = Math.max(0, Math.round(a + b * x));

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("proximoAno", proximoAno);
        resposta.put("previsao", previsao);
        resposta.put("precisao", String.format("%.2f%%", r2 * 100));
        resposta.put("tendencia", b > 0 ? "CRESCENTE" : (b < 0 ? "DECRESCENTE" : "ESTÁVEL"));
        return resposta;
    }

    /**
     * Calcula previsões para vários anos à frente.
     *
     * 🔸 QuickSort — organiza anos antes da regressão.
     * 🔸 Regressão Linear — usada para prever valores futuros.
     */
    public Map<Integer, Map<String, Object>> tendenciaIntervalo(int anosParaFrente) {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = new ArrayList<>(totalPorAno.keySet());
        quickSortIntegers(anos, 0, anos.size() - 1);

        if (anos.size() < 2) return Map.of();

        double[] resultado = calcularRegressaoLinear(totalPorAno);
        double a = resultado[0], b = resultado[1], r2 = resultado[2];
        int anoBase = anos.get(0);
        int ultimoAno = anos.get(anos.size() - 1);

        Map<Integer, Map<String, Object>> previsoes = new LinkedHashMap<>();

        // Loop gera previsões futuras (busca linear pelos próximos anos)
        for (int i = 1; i <= anosParaFrente; i++) {
            int anoFuturo = ultimoAno + i;
            int x = anoFuturo - anoBase;
            long previsao = Math.max(0, Math.round(a + b * x));
            double margemErro = (1 - r2) * i * 10;

            Map<String, Object> detalhes = new LinkedHashMap<>();
            detalhes.put("previsao", previsao);
            detalhes.put("precisao", String.format("%.2f%%", Math.max(0, (r2 * 100) - (i * 5))));
            detalhes.put("margemErro", String.format("%.1f%%", margemErro));
            detalhes.put("aviso", i > 5 ? "Previsão muito distante, baixa confiabilidade" : "");
            previsoes.put(anoFuturo, detalhes);
        }
        return previsoes;
    }

    // ============================================================
    // 🔹 MÉTODOS AUXILIARES
    // ============================================================

    /** Retorna a estação do ano com base no mês (Hemisfério Sul). */
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
     * Calcula a regressão linear sobre os dados.
     *
     * 🔸 Algoritmo: Regressão Linear Simples
     * 🔸 Retorna: [a (intercepto), b (inclinação), r² (coeficiente de determinação)]
     */
    private double[] calcularRegressaoLinear(Map<Integer, Long> dados) {
        List<Integer> anos = new ArrayList<>(dados.keySet());
        quickSortIntegers(anos, 0, anos.size() - 1);
        int n = anos.size();
        int anoBase = anos.get(0);

        // Cálculos de soma (busca linear)
        double somaX = 0, somaY = 0, somaXY = 0, somaX2 = 0;
        for (int ano : anos) {
            double x = ano - anoBase;
            double y = dados.get(ano);
            somaX += x;
            somaY += y;
            somaXY += x * y;
            somaX2 += x * x;
        }

        // Equações da reta: y = a + b*x
        double mediaX = somaX / n;
        double mediaY = somaY / n;
        double b = (n * somaXY - somaX * somaY) / (n * somaX2 - somaX * somaX);
        double a = mediaY - b * mediaX;

        // Cálculo do coeficiente de determinação (r²)
        double ssRes = 0, ssTot = 0;
        for (int ano : anos) {
            double x = ano - anoBase;
            double y = dados.get(ano);
            double yPred = a + b * x;
            ssRes += Math.pow(y - yPred, 2);
            ssTot += Math.pow(y - mediaY, 2);
        }

        double r2 = ssTot != 0 ? 1 - (ssRes / ssTot) : 0;
        return new double[]{a, b, r2};
    }

    // ============================================================
    // 🔹 IMPLEMENTAÇÕES DO QUICKSORT
    // ============================================================

    /** QuickSort para inteiros (anos). */
    private void quickSortIntegers(List<Integer> list, int low, int high) {
        if (low < high) {
            int pi = partitionIntegers(list, low, high);
            quickSortIntegers(list, low, pi - 1);
            quickSortIntegers(list, pi + 1, high);
        }
    }

    private int partitionIntegers(List<Integer> list, int low, int high) {
        int pivot = list.get(high); // pivô = último elemento
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j) < pivot) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    /** QuickSort para Strings (ordem alfabética). */
    private void quickSortStrings(List<String> list, int low, int high) {
        if (low < high) {
            int pi = partitionStrings(list, low, high);
            quickSortStrings(list, low, pi - 1);
            quickSortStrings(list, pi + 1, high);
        }
    }

    private int partitionStrings(List<String> list, int low, int high) {
        String pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) < 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    /** QuickSort para Map.Entry — ordena pelo valor de forma decrescente. */
    private void quickSortEntries(List<Map.Entry<String, Long>> list, int low, int high) {
        if (low < high) {
            int pi = partitionEntries(list, low, high);
            quickSortEntries(list, low, pi - 1);
            quickSortEntries(list, pi + 1, high);
        }
    }

    private int partitionEntries(List<Map.Entry<String, Long>> list, int low, int high) {
        long pivot = list.get(high).getValue();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).getValue() > pivot) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }
}
