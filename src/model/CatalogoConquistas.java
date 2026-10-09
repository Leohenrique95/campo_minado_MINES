package model;

import java.util.ArrayList;
import java.util.List;

public class CatalogoConquistas {
    public static List<Conquista> todas() {
        List<Conquista> lista = new ArrayList<>();
        lista.add(new Conquista("PRIMEIRA_VITORIA", "Primeira Vitória", "Vença sua primeira partida"));
        lista.add(new Conquista("SEM_BANDEIRA", "Sem Bandeira", "Vença sem usar nenhuma bandeira"));
        lista.add(new Conquista("VELOCISTA", "Velocista", "Vença em menos de 30 segundos"));
        lista.add(new Conquista("MESTRE_HARD", "Mestre da Matrix", "Vença no modo Hard"));
        lista.add(new Conquista("SEM_AJUDA", "Por Conta Própria", "Vença sem usar Dica ou Resolver"));
        lista.add(new Conquista("COLECIONADOR", "Colecionador", "Vença nas três dificuldades"));
        return lista;
    }
}