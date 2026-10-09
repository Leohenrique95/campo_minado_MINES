package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorPartidaSalva {

    private static final String PASTA = "saves";

    public GerenciadorPartidaSalva() {
        new File(PASTA).mkdirs();
    }

    public boolean existePartidaSalva(String nomePerfil) {
        return new File(PASTA, nomePerfil + ".txt").exists();
    }

    public void apagar(String nomePerfil) {
        new File(PASTA, nomePerfil + ".txt").delete();
    }

    public void salvar(String nomePerfil, Tabuleiro tabuleiro, int totalMinas, int totalCelulas,
                        int celulasReveladas, int jogadas, int segundosDecorridos, int limiteSegundos,
                        boolean usouResolver) {
        File arquivo = new File(PASTA, nomePerfil + ".txt");
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {
            escritor.write(tabuleiro.getLinhas() + ";" + tabuleiro.getColunas() + ";" + totalMinas + ";"
                    + totalCelulas + ";" + celulasReveladas + ";" + jogadas + ";" + segundosDecorridos + ";"
                    + limiteSegundos + ";" + (usouResolver ? 1 : 0));
            escritor.newLine();
            for (int i = 0; i < tabuleiro.getLinhas(); i++) {
                for (int j = 0; j < tabuleiro.getColunas(); j++) {
                    Celula celula = tabuleiro.getCelula(i, j);
                    escritor.write((celula.isMinada() ? 1 : 0) + ";" + (celula.isRevelada() ? 1 : 0) + ";"
                            + (celula.isMarcada() ? 1 : 0));
                    escritor.newLine();
                }
            }
        } catch (IOException e) {
            // não foi possível salvar
        }
    }

    public PartidaCarregada carregar(String nomePerfil) {
        File arquivo = new File(PASTA, nomePerfil + ".txt");
        if (!arquivo.exists()) {
            return null;
        }
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String[] meta = leitor.readLine().split(";");
            int linhas = Integer.parseInt(meta[0]);
            int colunas = Integer.parseInt(meta[1]);
            int totalMinas = Integer.parseInt(meta[2]);
            int totalCelulas = Integer.parseInt(meta[3]);
            int celulasReveladas = Integer.parseInt(meta[4]);
            int jogadas = Integer.parseInt(meta[5]);
            int segundosDecorridos = Integer.parseInt(meta[6]);
            int limiteSegundos = Integer.parseInt(meta[7]);
            boolean usouResolver = meta[8].equals("1");

            List<int[]> posicoesMinas = new ArrayList<>();
            boolean[][] reveladas = new boolean[linhas][colunas];
            boolean[][] marcadas = new boolean[linhas][colunas];

            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    String[] partes = leitor.readLine().split(";");
                    if (partes[0].equals("1")) {
                        posicoesMinas.add(new int[] { i, j });
                    }
                    reveladas[i][j] = partes[1].equals("1");
                    marcadas[i][j] = partes[2].equals("1");
                }
            }

            Tabuleiro tabuleiro = new Tabuleiro(linhas, colunas, posicoesMinas.toArray(new int[0][]));
            for (int i = 0; i < linhas; i++) {
                for (int j = 0; j < colunas; j++) {
                    if (marcadas[i][j]) {
                        tabuleiro.getCelula(i, j).alternarMarcacao();
                    }
                    if (reveladas[i][j]) {
                        tabuleiro.getCelula(i, j).revelar();
                    }
                }
            }

            return new PartidaCarregada(tabuleiro, totalMinas, totalCelulas, celulasReveladas, jogadas,
                    segundosDecorridos, limiteSegundos, usouResolver);
        } catch (IOException e) {
            return null;
        }
    }
}