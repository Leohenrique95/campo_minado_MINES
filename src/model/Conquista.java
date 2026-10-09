package model;

public class Conquista {
    private final String id;
    private final String nome;
    private final String descricao;

    public Conquista(String id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
}