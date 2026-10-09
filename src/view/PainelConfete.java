package view;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PainelConfete extends JPanel {

    private final List<Particula> particulas = new ArrayList<>();
    private Timer timer;

    public PainelConfete() {
        setOpaque(false);
    }

    public void iniciar(int largura, int altura, Color[] cores) {
        particulas.clear();
        Random sorteio = new Random();

        for (int i = 0; i < 150; i++) {
            double x = sorteio.nextInt(Math.max(largura, 1));
            double y = -sorteio.nextInt(Math.max(altura / 2, 1));
            double velocidadeX = sorteio.nextDouble() * 4 - 2;
            double velocidadeY = sorteio.nextDouble() * 2 + 2;
            double velocidadeRotacao = sorteio.nextDouble() * 10 - 5;
            Color cor = cores[sorteio.nextInt(cores.length)];
            int tamanho = 6 + sorteio.nextInt(6);
            particulas.add(new Particula(x, y, velocidadeX, velocidadeY, velocidadeRotacao, cor, tamanho));
        }

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(16, e -> {
            for (Particula p : particulas) {
                p.atualizar();
            }
            particulas.removeIf(p -> p.saiuDaTela(altura + 50));
            repaint();
            if (particulas.isEmpty()) {
                timer.stop();
                setVisible(false);
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (Particula p : particulas) {
            AffineTransform transformOriginal = g2d.getTransform();
            g2d.translate(p.getX(), p.getY());
            g2d.rotate(Math.toRadians(p.getRotacao()));
            g2d.setColor(p.getCor());
            g2d.fillRect(-p.getTamanho() / 2, -p.getTamanho() / 2, p.getTamanho(), p.getTamanho());
            g2d.setTransform(transformOriginal);
        }
    }
}