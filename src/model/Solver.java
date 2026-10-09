package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Aplica regras de dedução lógica sobre um tabuleiro, sem nunca consultar
 * onde as minas realmente estão (isMinada() nunca é chamado aqui, de
 * propósito). Enxerga o jogo pela mesma "janela" que um jogador humano
 * teria: células reveladas, seus números, e quais vizinhas estão
 * marcadas com bandeira.
 */
public class Solver {

    private final LeituraTabuleiro tabuleiro;

    public Solver(LeituraTabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
    }

    /**
     * Regra 1: para cada célula revelada cujo número de minas vizinhas já
     * bate com o número de bandeiras ao redor dela, todas as vizinhas
     * ocultas e não marcadas restantes são garantidamente seguras.
     */
    public List<int[]> encontrarCelulasSeguras() {
        List<int[]> seguras = new ArrayList<>();

        for (int linha = 0; linha < tabuleiro.getLinhas(); linha++) {
            for (int coluna = 0; coluna < tabuleiro.getColunas(); coluna++) {
                if (!tabuleiro.isRevelada(linha, coluna)) {
                    continue; // só células reveladas têm número pra analisar
                }

                int minasVizinhas = tabuleiro.getMinasVizinhas(linha, coluna);
                if (minasVizinhas == 0) {
                    continue; // célula "0" já teve tudo ao redor revelado pela cascata
                }

                int marcadas = contarVizinhosMarcados(linha, coluna);

                if (marcadas == minasVizinhas) {
                    seguras.addAll(vizinhosOcultosNaoMarcados(linha, coluna));
                }
            }
        }

        return seguras;
    }

    /**
     * Regra 2: para cada célula revelada onde o número de vizinhas
     * ocultas restantes é exatamente igual ao número de minas que ainda
     * faltam ser encontradas, todas essas vizinhas só podem ser minas.
     */
    public List<int[]> encontrarCelulasMinadas() {
        List<int[]> minadas = new ArrayList<>();

        for (int linha = 0; linha < tabuleiro.getLinhas(); linha++) {
            for (int coluna = 0; coluna < tabuleiro.getColunas(); coluna++) {
                if (!tabuleiro.isRevelada(linha, coluna)) {
                    continue;
                }

                int minasVizinhas = tabuleiro.getMinasVizinhas(linha, coluna);
                if (minasVizinhas == 0) {
                    continue;
                }

                int marcadas = contarVizinhosMarcados(linha, coluna);
                List<int[]> ocultosNaoMarcados = vizinhosOcultosNaoMarcados(linha, coluna);
                int minasFaltando = minasVizinhas - marcadas;

                if (!ocultosNaoMarcados.isEmpty() && minasFaltando == ocultosNaoMarcados.size()) {
                    minadas.addAll(ocultosNaoMarcados);
                }
            }
        }

        return minadas;
    }

    private int contarVizinhosMarcados(int linha, int coluna) {
        int total = 0;
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vizinhoLinha = linha + deltaLinha;
                int vizinhoColuna = coluna + deltaColuna;
                if (dentroDosLimites(vizinhoLinha, vizinhoColuna)
                        && tabuleiro.isMarcada(vizinhoLinha, vizinhoColuna)) {
                    total++;
                }
            }
        }
        return total;
    }

    private List<int[]> vizinhosOcultosNaoMarcados(int linha, int coluna) {
        List<int[]> vizinhos = new ArrayList<>();
        for (int deltaLinha = -1; deltaLinha <= 1; deltaLinha++) {
            for (int deltaColuna = -1; deltaColuna <= 1; deltaColuna++) {
                if (deltaLinha == 0 && deltaColuna == 0) {
                    continue;
                }
                int vizinhoLinha = linha + deltaLinha;
                int vizinhoColuna = coluna + deltaColuna;
                if (dentroDosLimites(vizinhoLinha, vizinhoColuna)
                        && !tabuleiro.isRevelada(vizinhoLinha, vizinhoColuna)
                        && !tabuleiro.isMarcada(vizinhoLinha, vizinhoColuna)) {
                    vizinhos.add(new int[] { vizinhoLinha, vizinhoColuna });
                }
            }
        }
        return vizinhos;
    }

    private boolean dentroDosLimites(int linha, int coluna) {
        return linha >= 0 && linha < tabuleiro.getLinhas()
                && coluna >= 0 && coluna < tabuleiro.getColunas();
    }
}