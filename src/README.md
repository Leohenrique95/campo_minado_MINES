# 💣 M.I.N.E.S.
### Matrix Inspection & Neural Exclusion System

Campo Minado clássico, reconstruído em Java com arquitetura MVC, tema cyberpunk e um bom punhado de funcionalidades extras além do que foi pedido no enunciado original — solver com dedução lógica, perfis de jogador, ranking, replay, conquistas, save/load e modo duelo.

Projeto desenvolvido para a disciplina de Programação Orientada a Objetos (Java) — UniCesumar/UNIPA Curitiba.

---

## 📋 Requisitos

- **JDK 17 ou superior** (desenvolvido e testado com Java 25.0.4.1 LTS — Eclipse Temurin)
- **PowerShell** (Windows) ou terminal equivalente
- Não usa Maven/Gradle — compilação feita direto via `javac`/`java`

**Editor recomendado:** [Visual Studio Code](https://code.visualstudio.com/) com o pacote **Extension Pack for Java** (Microsoft), que inclui:
- Language Support for Java™ by Red Hat
- Debugger for Java
- Test Runner for Java
- Project Manager for Java

Não é obrigatório usar o VS Code — qualquer editor de texto + o JDK instalado é suficiente, já que a compilação é feita pelo terminal.

---

## 📁 Estrutura de pastas

```
Mines/
├── lib/
│   └── junit-platform-console-standalone-1.12.1.jar
├── src/
│   ├── model/          → Celula, Tabuleiro, LeituraTabuleiro, Solver,
│   │                      Perfil, GerenciadorPerfis, Ranking, RegistroRanking,
│   │                      Jogada, PartidaCarregada, GerenciadorPartidaSalva,
│   │                      Conquista, CatalogoConquistas
│   ├── view/            → CampoMinadoView, GerenciadorSom, DetectorTemaSistema
│   ├── controller/      → CampoMinadoController, AcoesJogador
│   ├── main/            → JogoCampoMinado (console), JogoCampoMinadoGUI (GUI)
│   ├── test/            → CampoMinadoTest (JUnit)
│   ├── sons/            → clique.wav, bandeira.wav, explosao.wav, vitoria.wav
│   └── out/              → gerado pelo javac (não versionar)
├── perfis/               → um .txt por jogador (gerado em tempo de execução)
├── saves/                → partidas salvas, um .txt por jogador
└── ranking.txt           → ranking global (gerado em tempo de execução)
```

`perfis/`, `saves/` e `ranking.txt` são criados automaticamente na primeira vez que o jogo salva alguma coisa — não precisam existir antes.

---

## ⚙️ Baixando o JUnit (necessário só para rodar os testes)

O projeto usa JUnit 5 (Jupiter) para os testes unitários. Baixe o JAR "console standalone" (já vem com tudo incluso — biblioteca + executor):

```
https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.12.1/junit-platform-console-standalone-1.12.1.jar
```

Salva o arquivo dentro de uma pasta `lib/` na raiz do projeto (ao lado de `src/`).

---

## 🔨 Como compilar

Abra o PowerShell **dentro da pasta `src`** e rode:

```powershell
javac -d out -cp "out;..\lib\junit-platform-console-standalone-1.12.1.jar" (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

Isso compila todos os `.java` do projeto (incluindo os testes) para dentro da pasta `out/`. Se aparecer só uma nota sobre `unchecked or unsafe operations`, é normal — não é erro.

---

## ▶️ Como executar

**Versão gráfica (recomendada):**
```powershell
java -cp out main.JogoCampoMinadoGUI
```

**Versão console (texto, sem interface gráfica):**
```powershell
java -cp out main.JogoCampoMinado
```

---

## ✅ Como rodar os testes

```powershell
java -jar ..\lib\junit-platform-console-standalone-1.12.1.jar execute -cp out --scan-classpath
```

(Se aparecer erro sobre o argumento `execute`, tenta sem essa palavra: `java -jar ..\lib\junit-platform-console-standalone-1.12.1.jar -cp out --scan-classpath`)

O relatório mostra cada teste com ✔ (passou) ou ✘ (falhou), e um resumo no final.

---

## 🎮 Funcionalidades

### Base (requisitos do enunciado)
- Tabuleiro configurável, posicionamento aleatório de minas, efeito cascata
- Marcar/desmarcar bandeira, detecção de vitória e derrota
- Testes unitários (JUnit) cobrindo contagem de vizinhança, cascata e vitória/derrota

### Identidade visual
- Tema **Cyberpunk** (roxo/rosa/ciano), com variante **Claro** e **Escura**
- Detecção automática do modo claro/escuro do sistema operacional
- Seletor de tema discreto, no canto superior direito, com preview ao vivo
- Confete animado na vitória · efeito de "shake" da janela ao clicar em mina
- Menu com layout em duas colunas (Dificuldade / Tempo), tabuleiro Iniciante com células maiores para melhor aproveitamento de tela

### Algoritmos
- **Solver** com dedução lógica (regras de vizinhança, sem nunca "espiar" onde as minas estão de verdade)
- Botão de **Dica** (marca uma mina deduzida, com custo de +40s no cronômetro)
- **Resolver automático** (aplica o solver em loop até travar ou vencer)
- **Ranking** dos melhores tempos por dificuldade, com ordenação manual (insertion sort) e persistência em arquivo
- **Replay**: grava a sequência de jogadas e reproduz a partida do zero

### Persistência
- **Perfis de jogador** com tela de login, estatísticas e recordes salvos por perfil
- **Salvar/retomar partida** em andamento (serializa a grade inteira)
- **Conquistas** (6 badges, com regras que ignoram vitórias obtidas via Resolver automático)
- **Histórico** de partidas: vitórias, derrotas e taxa de acerto por perfil

### Som
- Efeitos sonoros (clique, bandeira, explosão, vitória) via `javax.sound.sampled`
- Botão de mudo

### Modo Duelo
- Dois jogadores, mesmo tabuleiro (mesmas minas), jogando em turnos sequenciais
- Tela de configuração (nomes + dificuldade) e tela de resultado comparando tempos

---

## 🏗️ Arquitetura

MVC clássico:
- **Model** (`model/`) não conhece Swing nem a View — só regras de jogo e dados
- **View** (`view/`) nunca altera o estado do jogo diretamente — só desenha o que o Model expõe via `LeituraTabuleiro`
- **Controller** (`controller/`) é o único ponto que conhece Model e View, e decide o que cada ação do jogador significa

O `Solver` nunca consulta `isMinada()` durante a dedução lógica — enxerga o tabuleiro pela mesma "janela" que um jogador humano teria (células reveladas, números, bandeiras).
