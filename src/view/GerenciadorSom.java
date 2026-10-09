package view;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorSom {

    private final Map<String, Clip> clipesCarregados = new HashMap<>();
    private boolean ativado = true;

    public void tocar(String caminhoArquivo) {
        if (!ativado) {
            return;
        }
        try {
            Clip clip = clipesCarregados.get(caminhoArquivo);
            if (clip == null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(caminhoArquivo));
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clipesCarregados.put(caminhoArquivo, clip);
            }
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        }  catch (Exception e) { System.out.println("Erro ao tocar som: " + e.getMessage()); }
    }

    public void alternarAtivado() {
        ativado = !ativado;
    }

    public boolean isAtivado() {
        return ativado;
    }
}