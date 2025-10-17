package com.giovannyenes.estruturadados.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.giovannyenes.estruturadados.service.AnaliseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/analise")
@CrossOrigin(origins = "*")
@Tag(name = "Análise de Queimadas", description = "Endpoints para análise estatística de focos de queimadas")
public class AnaliseController {

    private final AnaliseService analiseService;

    public AnaliseController(AnaliseService analiseService) {
        this.analiseService = analiseService;
    }

    // ========== ENDPOINTS DE DADOS BÁSICOS ==========

    @Operation(summary = "Lista todos os anos disponíveis", 
               description = "Retorna lista de anos distintos presentes na base de dados, em ordem crescente")
    @GetMapping("/anos")
    public List<Integer> listarAnosOrdenados() {
        return analiseService.listarAnosOrdenados();
    }

    @Operation(summary = "Total de focos por ano", 
               description = "Retorna a contagem total de focos de queimadas agrupados por ano")
    @GetMapping("/total-por-ano")
    public Map<Integer, Long> totalFocosPorAno() {
        return analiseService.totalFocosPorAno();
    }

    @Operation(summary = "Lista todos os biomas disponíveis", 
               description = "Retorna lista de biomas distintos presentes na base de dados, em ordem alfabética")
    @GetMapping("/biomas")
    public List<String> listarBiomasOrdenados() {
        return analiseService.listarBiomasOrdenados();
    }

    @Operation(summary = "Total de focos por bioma", 
               description = "Retorna a contagem total de focos de queimadas agrupados por bioma")
    @GetMapping("/total-por-bioma")
    public Map<String, Long> totalFocosPorBioma() {
        return analiseService.totalFocosPorBioma();
    }

    @Operation(summary = "Crescimento percentual por ano", 
               description = "Retorna a variação percentual de queimadas entre anos consecutivos. Não inclui o primeiro ano. Valores com 2 casas decimais.")
    @GetMapping("/crescimento-por-ano")
    public Map<Integer, Double> crescimentoPercentualPorAno() {
        return analiseService.crescimentoPercentualPorAno();
    }

    // ========== ENDPOINTS DE ANÁLISE TEMPORAL ==========

    @Operation(summary = "Contagem de focos por mês", 
               description = "Retorna a quantidade de focos agrupados por mês (1-12), considerando todos os anos")
    @GetMapping("/contagem-por-mes")
    public Map<Integer, Long> contagemPorMes() {
        return analiseService.contagemPorMes();
    }

    @Operation(summary = "Contagem de focos por estação", 
               description = "Retorna a quantidade de focos agrupados por estação do ano (Verão, Outono, Inverno, Primavera). Baseado no Hemisfério Sul (Brasil)")
    @GetMapping("/contagem-por-estacao")
    public Map<String, Long> contagemPorEstacao() {
        return analiseService.contagemPorEstacao();
    }

    @Operation(summary = "Estação com mais queimadas", 
               description = "Retorna a estação do ano que possui o maior número de focos de queimadas")
    @GetMapping("/estacao-mais-queimadas")
    public Map<String, Long> estacaoComMaisQueimadas() {
        return analiseService.estacaoComMaisQueimadas();
    }

    // ========== ENDPOINTS DE RANKING ==========

    @Operation(summary = "Ranking de municípios com mais queimadas", 
               description = "Retorna os municípios com maior número de focos de queimadas, ordenados de forma decrescente")
    @GetMapping("/ranking-municipios")
    public Map<String, Long> rankingMunicipios(
            @Parameter(description = "Número de municípios a retornar no ranking", example = "10")
            @RequestParam(defaultValue = "10") int top) {
        return analiseService.rankingMunicipios(top);
    }

    // ========== ENDPOINTS DE PREVISÃO (MACHINE LEARNING) ==========

    @Operation(summary = "Tendência geral - Previsão para próximo ano", 
               description = "Utiliza regressão linear para prever o número de queimadas para o próximo ano. Retorna também a precisão (R²) e a tendência (CRESCENTE/DECRESCENTE/ESTÁVEL)")
    @GetMapping("/tendencia-geral")
    public Map<String, Object> tendenciaGeral() {
        return analiseService.tendenciaGeral();
    }

    @Operation(summary = "Tendência por intervalo - Previsão para múltiplos anos", 
               description = "Utiliza regressão linear para prever queimadas para os próximos N anos. ATENÇÃO: Quanto maior o intervalo, menor a confiabilidade da previsão")
    @GetMapping("/tendencia-intervalo")
    public Map<Integer, Map<String, Object>> tendenciaIntervalo(
            @Parameter(description = "Número de anos para frente a prever", example = "5")
            @RequestParam(defaultValue = "5") int anos) {
        return analiseService.tendenciaIntervalo(anos);
    }
}