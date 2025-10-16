package com.giovannyenes.estruturadados.service;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final DadosDesmatamentoRepository repository;

    public PredictionService(DadosDesmatamentoRepository repository) {
        this.repository = repository;
    }

    /**
     * Retorna um mapa com o total de focos por ano.
     */
    private Map<Integer, Long> totalFocosPorAno() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        DadosDesmatamento::getAno,
                        Collectors.counting()
                ));
    }

    /**
     * Faz uma projeção linear simples (y = a*x + b)
     * com base nos focos de anos anteriores.
     */
    public Map<Integer, Double> projetarFocosFuturos(int anosFuturos) {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = totalPorAno.keySet().stream().sorted().toList();
        List<Long> focos = anos.stream().map(totalPorAno::get).toList();

        if (anos.size() < 2) {
            throw new IllegalStateException("É necessário pelo menos 2 anos de dados para realizar a projeção.");
        }

        // cálculo da regressão linear
        double n = anos.size();
        double somaX = anos.stream().mapToDouble(a -> a).sum();
        double somaY = focos.stream().mapToDouble(f -> f).sum();
        double somaXY = 0, somaX2 = 0;

        for (int i = 0; i < anos.size(); i++) {
            somaXY += anos.get(i) * focos.get(i);
            somaX2 += Math.pow(anos.get(i), 2);
        }

        double a = (n * somaXY - somaX * somaY) / (n * somaX2 - Math.pow(somaX, 2));
        double b = (somaY - a * somaX) / n;

        // Gera projeções futuras
        int ultimoAno = anos.get(anos.size() - 1);
        Map<Integer, Double> projecoes = new LinkedHashMap<>();

        for (int i = 1; i <= anosFuturos; i++) {
            int anoFuturo = ultimoAno + i;
            double previsao = a * anoFuturo + b;
            projecoes.put(anoFuturo, previsao);
        }

        return projecoes;
    }

    /**
     * Calcula também uma tendência geral (subida ou queda média percentual).
     */
    public double tendenciaGeral() {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = totalPorAno.keySet().stream().sorted().toList();
        if (anos.size() < 2) return 0;

        long primeiro = totalPorAno.get(anos.get(0));
        long ultimo = totalPorAno.get(anos.get(anos.size() - 1));

        return ((double) (ultimo - primeiro) / primeiro) * 100.0;
    }
}
