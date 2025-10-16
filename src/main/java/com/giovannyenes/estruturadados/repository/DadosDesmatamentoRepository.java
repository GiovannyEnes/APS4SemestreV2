package com.giovannyenes.estruturadados.repository;

import com.giovannyenes.estruturadados.model.DadosDesmatamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DadosDesmatamentoRepository extends JpaRepository<DadosDesmatamento, Long> {}
