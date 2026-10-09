package model;

public class RegistroRanking {

    private final String nomeJogador;
    private final int tempoSegundos;
    private final String dificuldade;

    public RegistroRanking(String nomeJogador, int tempoSegundos, String dificuldade) {
        this.nomeJogador = nomeJogador;
        this.tempoSegundos = tempoSegundos;
        this.dificuldade = dificuldade;
    }

    public String getNomeJogador() {
        return nomeJogador;
    }

    public int getTempoSegundos() {
        return tempoSegundos;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public String getTempoFormatado() {
        return String.format("%02d:%02d", tempoSegundos / 60, tempoSegundos % 60);
    }
}