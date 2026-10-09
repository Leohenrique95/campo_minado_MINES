package model;

public class Jogada {

    public enum Tipo { REVELAR, MARCAR }

    private final Tipo tipo;
    private final int linha;
    private final int coluna;

    public Jogada(Tipo tipo, int linha, int coluna) {
        this.tipo = tipo;
        this.linha = linha;
        this.coluna = coluna;
    }

    public Tipo getTipo() { return tipo; }
    public int getLinha() { return linha; }
    public int getColuna() { return coluna; }
}