package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Ranking {

    private static final String ARQUIVO = "ranking.txt";
    private final List<RegistroRanking> registros = new ArrayList<>();

    public Ranking() {
        carregarDeArquivo();
    }

    public void registrarTempo(String nomeJogador, int tempoSegundos, String dificuldade) {
        registros.add(new RegistroRanking(nomeJogador, tempoSegundos, dificuldade));
        ordenarPorTempo();
        salvarEmArquivo();
    }

    private void ordenarPorTempo() {
        for (int i = 1; i < registros.size(); i++) {
            RegistroRanking atual = registros.get(i);
            int j = i - 1;
            while (j >= 0 && registros.get(j).getTempoSegundos() > atual.getTempoSegundos()) {
                registros.set(j + 1, registros.get(j));
                j--;
            }
            registros.set(j + 1, atual);
        }
    }

    public List<RegistroRanking> getMelhoresPorDificuldade(String dificuldade, int quantidade) {
        List<RegistroRanking> filtrados = new ArrayList<>();
        for (RegistroRanking registro : registros) {
            if (registro.getDificuldade().equals(dificuldade)) {
                filtrados.add(registro);
                if (filtrados.size() >= quantidade) {
                    break;
                }
            }
        }
        return filtrados;
    }

    private void carregarDeArquivo() {
        File arquivo = new File(ARQUIVO);
        if (!arquivo.exists()) {
            return;
        }
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 3) {
                    registros.add(new RegistroRanking(partes[1], Integer.parseInt(partes[2]), partes[0]));
                }
            }
            ordenarPorTempo();
        } catch (IOException e) {
            // arquivo inacessível: segue com ranking vazio
        }
    }

    private void salvarEmArquivo() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARQUIVO))) {
            for (RegistroRanking r : registros) {
                escritor.write(r.getDificuldade() + ";" + r.getNomeJogador() + ";" + r.getTempoSegundos());
                escritor.newLine();
            }
        } catch (IOException e) {
            // não foi possível salvar; ranking segue funcionando só em memória
        }
    }
}