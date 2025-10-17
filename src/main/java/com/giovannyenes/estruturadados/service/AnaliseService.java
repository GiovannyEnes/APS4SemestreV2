package com.giovannyenes.estruturadados.service;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import com.giovannyenes.estruturadados.repository.DadosDesmatamentoRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


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
     */
    public Map<Integer, Double> crescimentoPercentualPorAno() {
        Map<Integer, Long> totalPorAno = totalFocosPorAno();
        List<Integer> anos = totalPorAno.keySet().stream().sorted().toList();

        Map<Integer, Double> crescimento = new LinkedHashMap<>();

        for (int i = 1; i < anos.size(); i++) {
            int anoAnterior = anos.get(i - 1);
            int anoAtual = anos.get(i);

            long anterior = totalPorAno.get(anoAnterior);
            long atual = totalPorAno.get(anoAtual);

            double percentual = anterior > 0
                    ? ((double) (atual - anterior) / anterior) * 100
                    : 0;

            crescimento.put(anoAtual, percentual);
        }

        return crescimento;
    }
}
