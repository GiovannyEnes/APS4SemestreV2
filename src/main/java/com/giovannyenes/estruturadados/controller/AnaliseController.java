package com.giovannyenes.estruturadados.controller;

import com.giovannyenes.estruturadados.service.AnaliseService;
import com.giovannyenes.estruturadados.service.CsvLoaderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analise")
@CrossOrigin(origins = "*")
public class AnaliseController {

    private final AnaliseService analiseService;

    public AnaliseController(AnaliseService analiseService) {
        this.analiseService = analiseService;
    }

    @GetMapping("/anos")
    public List<Integer> listarAnosOrdenados() {
        return analiseService.listarAnosOrdenados();
    }

    @GetMapping("/total-por-ano")
    public Map<Integer, Long> totalFocosPorAno() {
        return analiseService.totalFocosPorAno();
    }

    @GetMapping("/biomas")
    public List<String> listarBiomasOrdenados() {
        return analiseService.listarBiomasOrdenados();
    }

    @GetMapping("/total-por-bioma")
    public Map<String, Long> totalFocosPorBioma() {
        return analiseService.totalFocosPorBioma();
    }

    @GetMapping("/crescimento-por-ano")
    public Map<Integer, Double> crescimentoPercentualPorAno() {
        return analiseService.crescimentoPercentualPorAno();
    }
}