package model;

import java.io.*;
import java.util.*;

public class GerenciadorPerfis {

    private static final String PASTA = "perfis";

    public GerenciadorPerfis() {
        new File(PASTA).mkdirs();
    }

    public List<String> listarPerfis() {
        File pasta = new File(PASTA);
        String[] arquivos = pasta.list((dir, nome) -> nome.endsWith(".txt"));
        List<String> nomes = new ArrayList<>();
        if (arquivos != null) {
            for (String arquivo : arquivos) {
                nomes.add(arquivo.substring(0, arquivo.length() - 4));
            }
        }
        return nomes;
    }

        public Perfil carregarOuCriar(String nome) {
            Perfil perfil = new Perfil(nome);
            File arquivo = new File(PASTA, nome + ".txt");
            if (arquivo.exists()) {
                try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
                    String linha = leitor.readLine();
                    if (linha != null) {
                        String[] p = linha.split(";");
                        perfil.definirEstatisticas(Integer.parseInt(p[0]), Integer.parseInt(p[1]),
                                Integer.parseInt(p[2]), Integer.parseInt(p[3]), Integer.parseInt(p[4]));
                    }

                    String linha2 = leitor.readLine();
                    if (linha2 != null && !linha2.isBlank()) {
                        Set<String> ids = new HashSet<>(Arrays.asList(linha2.split(",")));
                        perfil.definirConquistas(ids);
                    }
                } catch (IOException e) {
                    // segue com perfil zerado
                }
            }
            return perfil;
        }

    public void salvar(Perfil perfil) {
        File arquivo = new File(PASTA, perfil.getNome() + ".txt");
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {
            escritor.write(perfil.getVitorias() + ";" + perfil.getDerrotas() + ";"
                    + perfil.getMelhorTempoEasy() + ";" + perfil.getMelhorTempoMedium() + ";"
                    + perfil.getMelhorTempoHard());
            escritor.newLine();
            escritor.write(String.join(",", perfil.getConquistas()));
        } catch (IOException e) {
            // não foi possível salvar
        }
    }
}