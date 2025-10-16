package com.giovannyenes.estruturadados.controller;

import com.giovannyenes.estruturadados.service.PredictionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/previsoes")
@CrossOrigin(origins = "*")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    /**
     * Retorna a projeção de focos de queimadas para os próximos N anos.
     */
    @GetMapping("/{anos}")
    public Map<Integer, Double> projetarFocos(@PathVariable int anos) {
        return predictionService.projetarFocosFuturos(anos);
    }

    /**
     * Retorna a tendência percentual geral (subida ou queda ao longo do tempo).
     */
    @GetMapping("/tendencia")
    public double tendenciaGeral() {
        return predictionService.tendenciaGeral();
    }
}