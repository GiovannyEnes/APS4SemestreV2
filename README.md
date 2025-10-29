## 🔥 Sistema de Análise de Performance de Algoritmos de Ordenação

### 📊 Projeto acadêmico em Java voltado à análise de desempenho e processamento de dados geográficos

---

## 🚀 Visão Geral
Este projeto tem como objetivo **analisar e comparar o desempenho de diferentes algoritmos de ordenação**, aplicados a **dados geográficos reais** sobre **focos de incêndio no Brasil**, disponibilizados pelo **INPE (Instituto Nacional de Pesquisas Espaciais)**.

O sistema foi desenvolvido como parte das **Atividades Práticas Supervisionadas (APS)** da disciplina de **Estrutura de Dados** do curso de **Ciência da Computação – UNIP (Universidade Paulista)**.

A aplicação combina conceitos de **estrutura de dados, análise de complexidade e desempenho de algoritmos**, utilizando **Java e Spring Boot** para processamento e visualização dos resultados.

---

## 🧩 Funcionalidades
- 📂 Importação de dados em formato **CSV** (extraídos do portal do INPE).  
- 🔄 Ordenação dos registros por **data**, **bioma**, **município** e **precipitação**.  
- ⚙️ Implementação e comparação de múltiplos **algoritmos de ordenação** (Bubble Sort, Insertion Sort, Selection Sort e Quick Sort).  
- 📊 Exibição do número de **comparações** e **trocas** realizadas em cada execução.  
- 🌎 Interface web para **visualização dos dados e da eficiência dos algoritmos**.  

---

## 🛠️ Tecnologias Utilizadas
| Categoria | Tecnologia |
|------------|-------------|
| Linguagem | **Java 17** |
| Framework | **Spring Boot** |
| Banco de Dados | **H2 (em memória)** |
| Frontend | **Thymeleaf / HTML / CSS** |
| Gerenciador de Dependências | **Maven** |
| Leitura de CSV | **OpenCSV** |

---

## ⚡ Arquitetura do Sistema
O projeto segue o padrão **MVC (Model–View–Controller)**, garantindo uma boa separação entre regras de negócio, controle e apresentação dos dados.

```
📁 src/
 ┣ 📂 main/java/com/giovannyenes/estruturadados
 ┃ ┣ 📂 controller/
 ┃ ┣ 📂 service/
 ┃ ┣ 📂 repository/
 ┃ ┗ 📂 model/
 ┣ 📂 resources/
 ┃ ┣ 📂 templates/
 ┃ ┗ 📂 static/
 ┗ 📄 application.properties
```

---

## ▶️ Como Executar o Projeto

### 1️⃣ Clonar o repositório
```bash
git clone https://github.com/GiovannyEnes/APS4SemestreV2.git
```

### 2️⃣ Acessar a pasta do projeto
```bash
cd APS4SemestreV2
```

### 3️⃣ Executar a aplicação
```bash
mvn spring-boot:run
```

### 4️⃣ Acessar no navegador
```
http://localhost:8080
```

---

## 📈 Objetivos de Aprendizado
- Compreender a **análise de complexidade e eficiência** de algoritmos de ordenação.  
- Aplicar **estruturas de dados** na manipulação de grandes volumes de informação.  
- Desenvolver uma aplicação **Java com Spring Boot** integrando backend e frontend.  
- Realizar **benchmarking** entre diferentes abordagens de ordenação.  

---

## 🧠 Conceitos Aplicados
- Estruturas de Dados (listas, vetores e coleções)  
- Algoritmos de Ordenação (Bubble Sort, Insertion Sort, Selection Sort, Quick Sort)  
- Leitura e manipulação de arquivos CSV  
- Programação orientada a objetos  
- Padrão MVC  
- Persistência de dados em memória com H2  
- Visualização de dados e análise de performance  

---

## 👤 Autores
**Giovanny Enes**  
🎓 Estudante de Ciência da Computação – UNIP  
🌐 [LinkedIn](https://www.linkedin.com/in/giovanny-enes) • [GitHub](https://github.com/GiovannyEnes)

**Lauan Amorim**  
🎓 Estudante de Ciência da Computação – UNIP  
🌐 [LinkedIn](https://www.linkedin.com/in/lauanamorim) • [GitHub](https://github.com/LauanAmorim)

**Maria Clara**  
🎓 Estudante de Ciência da Computação – UNIP  
🌐 [LinkedIn](https://www.linkedin.com/in/maria-borelli) 

**Miguel Teixeira**  
🎓 Estudante de Ciência da Computação – UNIP  
🌐 [LinkedIn](https://www.linkedin.com/in/miguelsilvateixeira) • [GitHub](https://github.com/MiguelSilvaTeixeira)

---

> “Algoritmos eficientes transformam dados em conhecimento.”
