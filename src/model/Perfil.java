package model;

import java.util.HashSet;
import java.util.Set;

public class Perfil {

    private final String nome;
    private int vitorias;
    private int derrotas;
    private int melhorTempoEasy;
    private int melhorTempoMedium;
    private int melhorTempoHard;

    public Perfil(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public int getVitorias() { return vitorias; }
    public int getDerrotas() { return derrotas; }
    public int getMelhorTempoEasy() { return melhorTempoEasy; }
    public int getMelhorTempoMedium() { return melhorTempoMedium; }
    public int getMelhorTempoHard() { return melhorTempoHard; }

    public void registrarDerrota() {
        derrotas++;
    }

    public void registrarVitoria(String dificuldade, int tempoSegundos, boolean contaParaRecorde) {
        vitorias++;
        if (!contaParaRecorde) {
            return;
        }
        switch (dificuldade) {
            case "EASY":
                if (melhorTempoEasy == 0 || tempoSegundos < melhorTempoEasy) melhorTempoEasy = tempoSegundos;
                break;
            case "MEDIUM":
                if (melhorTempoMedium == 0 || tempoSegundos < melhorTempoMedium) melhorTempoMedium = tempoSegundos;
                break;
            case "HARD":
                if (melhorTempoHard == 0 || tempoSegundos < melhorTempoHard) melhorTempoHard = tempoSegundos;
                break;
        }
    }

    public void definirEstatisticas(int vitorias, int derrotas, int easy, int medium, int hard) {
        this.vitorias = vitorias;
        this.derrotas = derrotas;
        this.melhorTempoEasy = easy;
        this.melhorTempoMedium = medium;
        this.melhorTempoHard = hard;
    }

    private final Set<String> conquistas = new HashSet<>();

        public boolean desbloquear(String id) {
            return conquistas.add(id);
        }

        public Set<String> getConquistas() {
            return conquistas;
        }

        public void definirConquistas(Set<String> ids) {
            conquistas.clear();
            conquistas.addAll(ids);
        }

        public double getTaxaAcerto() {
        int total = vitorias + derrotas;
        if (total == 0) {
            return 0;
        }
        return (double) vitorias / total * 100;
    }
}