# APS – Sistema para Análise de Performance de Algoritmos de Ordenação de Dados

## 🎓 Contexto Acadêmico
Este projeto foi desenvolvido como parte das **Atividades Práticas Supervisionadas (APS)** da disciplina de **Estrutura de Dados**, do curso de **Ciência da Computação – UNIP (Universidade Paulista)**, referente ao **4º semestre – 2025**.

O trabalho tem como objetivo aplicar conceitos de **estruturas de dados, algoritmos de ordenação e análise de desempenho**, em um cenário prático de **geoprocessamento de dados ambientais**.

---

## 🧠 Tema
**“Desenvolvimento de um Sistema para Análise de Performance de Algoritmos de Ordenação de Dados”**

O sistema realiza a análise de dados referentes a **focos de incêndio detectados por satélite** em diferentes estados do Brasil, utilizando informações públicas disponibilizadas pelo **INPE (Instituto Nacional de Pesquisas Espaciais)**.

---

## 🎯 Objetivo
O objetivo central do projeto é **avaliar a performance de diferentes algoritmos de ordenação** aplicados a grandes volumes de dados geográficos, simulando um cenário real de análise ambiental.

O sistema:
- Lê dados de queimadas em formato CSV (extraídos do INPE).
- Permite a ordenação dos registros por **data**, **bioma**, **município** e **precipitação**.
- Calcula e exibe o **número de operações** (comparações e trocas) realizadas durante o processo de ordenação.
- Fornece uma interface de visualização de dados e resultados de performance.

---

## 🧩 Metodologia
O projeto foi desenvolvido utilizando a **linguagem Java**, estruturada no padrão **MVC (Model–View–Controller)** e gerenciada por **Maven**.  
A aplicação foi construída com **Spring Boot**, empregando as seguintes etapas:

1. **Coleta de Dados:** download dos arquivos CSV do INPE.  
2. **Leitura e Tratamento:** conversão dos dados em objetos Java.  
3. **Implementação de Algoritmos:** comparação entre métodos como Bubble Sort, Insertion Sort, Selection Sort e Quick Sort.  
4. **Análise de Performance:** contagem de comparações e substituições.  
5. **Visualização:** exibição de resultados via interface web e gráficos.  

---

## 🧰 Tecnologias Utilizadas
- **Java 17**
- **Spring Boot**
- **H2 Database** (banco de dados em memória)
- **Thymeleaf / HTML / CSS**
- **Maven**
- **OpenCSV** (leitura de arquivos CSV)

---

## 📈 Resultados Esperados
- Avaliação comparativa entre algoritmos de ordenação.  
- Relatórios de desempenho com base no volume de dados.  
- Visualização gráfica dos resultados e dos dados de queimadas.  

---

## 📚 Referências
- Instituto Nacional de Pesquisas Espaciais (INPE): https://dataserver-coids.inpe.br  
- Cormen, T. H. et al. *Introduction to Algorithms.* MIT Press.  
- Sedgewick, R. *Algorithms in Java.* Addison Wesley.  
- Documentação oficial do Spring Boot: https://spring.io/projects/spring-boot
