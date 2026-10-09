package view;

import java.awt.Color;

public class Particula {

    private double x;
    private double y;
    private double velocidadeX;
    private double velocidadeY;
    private double rotacao;
    private final double velocidadeRotacao;
    private final Color cor;
    private final int tamanho;

    public Particula(double x, double y, double velocidadeX, double velocidadeY,
                      double velocidadeRotacao, Color cor, int tamanho) {
        this.x = x;
        this.y = y;
        this.velocidadeX = velocidadeX;
        this.velocidadeY = velocidadeY;
        this.velocidadeRotacao = velocidadeRotacao;
        this.cor = cor;
        this.tamanho = tamanho;
    }

    public void atualizar() {
        x += velocidadeX;
        y += velocidadeY;
        velocidadeY += 0.15;   // gravidade — vai acelerando a queda
        rotacao += velocidadeRotacao;
    }

    public boolean saiuDaTela(int alturaLimite) {
        return y > alturaLimite;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRotacao() {
        return rotacao;
    }

    public Color getCor() {
        return cor;
    }

    public int getTamanho() {
        return tamanho;
    }
}