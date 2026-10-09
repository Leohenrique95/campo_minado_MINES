package model;

public class PartidaCarregada {
    private final Tabuleiro tabuleiro;
    private final int totalMinas;
    private final int totalCelulas;
    private final int celulasReveladas;
    private final int jogadas;
    private final int segundosDecorridos;
    private final int limiteSegundos;
    private final boolean usouResolver;

    public PartidaCarregada(Tabuleiro tabuleiro, int totalMinas, int totalCelulas, int celulasReveladas,
                             int jogadas, int segundosDecorridos, int limiteSegundos, boolean usouResolver) {
        this.tabuleiro = tabuleiro;
        this.totalMinas = totalMinas;
        this.totalCelulas = totalCelulas;
        this.celulasReveladas = celulasReveladas;
        this.jogadas = jogadas;
        this.segundosDecorridos = segundosDecorridos;
        this.limiteSegundos = limiteSegundos;
        this.usouResolver = usouResolver;
    }

    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public int getTotalMinas() { return totalMinas; }
    public int getTotalCelulas() { return totalCelulas; }
    public int getCelulasReveladas() { return celulasReveladas; }
    public int getJogadas() { return jogadas; }
    public int getSegundosDecorridos() { return segundosDecorridos; }
    public int getLimiteSegundos() { return limiteSegundos; }
    public boolean isUsouResolver() { return usouResolver; }
}