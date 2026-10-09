package controller;

import model.Tabuleiro;
import view.CampoMinadoView;
import javax.swing.Timer;

import java.util.ArrayList;
import java.util.List;
import model.Solver;
import model.Ranking;
import model.Jogada;
import model.Perfil;
import model.GerenciadorPerfis;
import model.PartidaCarregada;
import model.GerenciadorPartidaSalva;

    /**
     * CONTROLLER da arquitetura MVC: é o único ponto que conhece tanto o
     * {@link Tabuleiro} (Model) quanto a {@link CampoMinadoView} (View).
     * Recebe notificações de clique da View através de {@link AcoesJogador},
     * aplica a jogada no Model e manda a View se redesenhar. A View nunca
     * toca no Model diretamente, e o Model nunca conhece a View.
     */
    public class CampoMinadoController implements AcoesJogador {

    private final CampoMinadoView view;

    private Tabuleiro tabuleiro;
    private int totalMinas;
    private int totalCelulas;
    private int celulasReveladas;
    private int jogadas;
    private boolean jogoIniciado;
    private long tempoInicio;
    private int limiteSegundos;
    private Timer timerJogo;
    private Solver solver;
    private final Ranking ranking = new Ranking();
    private boolean usouResolverNestaPartida;
    private final List<Jogada> historicoJogadas = new ArrayList<>();
    private int[][] posicoesMinasPartidaAtual;
    private boolean emReplay;
    private final GerenciadorPerfis gerenciadorPerfis = new GerenciadorPerfis();
    private Perfil perfilAtual;
    private final GerenciadorPartidaSalva gerenciadorSalvamento = new GerenciadorPartidaSalva();
    private boolean usouBandeiraNestaPartida;
    private boolean usouDicaNestaPartida;
    private boolean emDuelo;
    private int etapaDuelo;
    private String nomeJogador1Duelo;
    private String nomeJogador2Duelo;
    private int[][] posicoesMinasDuelo;
    private int linhasDuelo, colunasDuelo, minasDuelo;
    private int tempoJogador1Duelo, tempoJogador2Duelo;
    private boolean venceuJogador1Duelo, venceuJogador2Duelo;

    public CampoMinadoController(CampoMinadoView view) {
        this.view = view;
        this.view.setOuvinte(this);
    }

    public void iniciar() {
        view.mostrarTelaLogin(gerenciadorPerfis.listarPerfis());
        view.setVisible(true);
    }
    // ================================================================
    // AcoesJogador — chamado pela View
    // ================================================================

    @Override
    public void aoEscolherDificuldade(int linhas, int colunas, int minas) {
        this.tabuleiro = new Tabuleiro(linhas, colunas, minas);
        this.posicoesMinasPartidaAtual = tabuleiro.getPosicoesMinas();
        this.historicoJogadas.clear();
        this.emReplay = false;
        this.solver = new Solver(tabuleiro);
        this.totalMinas = minas;
        this.totalCelulas = linhas * colunas - minas;
        this.celulasReveladas = 0;
        this.jogadas = 0;
        this.jogoIniciado = false;
        this.usouResolverNestaPartida = false;
        this.usouBandeiraNestaPartida = false;
        this.usouDicaNestaPartida = false;

        pararTimer();
        view.aplicarTemaSelecionado();

        this.limiteSegundos = view.getTempoLimiteSegundosSelecionado();
        view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
        view.atualizarEstatisticas(totalMinas, 0, totalCelulas, 0);
        view.definirJogadorAtualDuelo(null);
        view.definirResolverDisponivel(true);
    }

    @Override
    public void aoIniciarDuelo(String nome1, String nome2, int linhas, int colunas, int minas) {
        if (nome1 == null || nome1.isBlank() || nome2 == null || nome2.isBlank()) {
            return;
        }
        this.nomeJogador1Duelo = nome1.trim();
        this.nomeJogador2Duelo = nome2.trim();
        this.linhasDuelo = linhas;
        this.colunasDuelo = colunas;
        this.minasDuelo = minas;
        this.limiteSegundos = 0;

        Tabuleiro tabuleiroBase = new Tabuleiro(linhas, colunas, minas);
        this.posicoesMinasDuelo = tabuleiroBase.getPosicoesMinas();

        this.emDuelo = true;
        this.etapaDuelo = 1;
        iniciarRodadaDuelo();
    }

    private void iniciarRodadaDuelo() {
        pararTimer();
        this.tabuleiro = new Tabuleiro(linhasDuelo, colunasDuelo, posicoesMinasDuelo);
        this.solver = new Solver(tabuleiro);
        this.totalMinas = minasDuelo;
        this.totalCelulas = linhasDuelo * colunasDuelo - minasDuelo;
        this.celulasReveladas = 0;
        this.jogadas = 0;
        this.jogoIniciado = false;
        this.usouResolverNestaPartida = false;
        this.usouBandeiraNestaPartida = false;
        this.usouDicaNestaPartida = false;
        this.historicoJogadas.clear();
        this.emReplay = false;
        this.posicoesMinasPartidaAtual = tabuleiro.getPosicoesMinas();

        String nomeAtual = (etapaDuelo == 1) ? nomeJogador1Duelo : nomeJogador2Duelo;
        view.iniciarTelaDeJogo(linhasDuelo, colunasDuelo, totalMinas, totalCelulas, limiteSegundos);
        view.definirResolverDisponivel(false);
        view.definirJogadorAtualDuelo(nomeAtual);
    }

    private void finalizarRodadaDuelo() {
        int tempoFinal = (int) obterSegundosPassados();
        boolean venceu = !tabuleiro.isDerrota();

        if (tabuleiro.isDerrota()) {
            view.mostrarDerrota();
            view.chacoalharJanela();
            view.tocarSom("explosao.wav");
        } else {
            view.mostrarVitoria();
            view.tocarSom("vitoria.wav");
        }

        if (etapaDuelo == 1) {
            tempoJogador1Duelo = tempoFinal;
            venceuJogador1Duelo = venceu;
            etapaDuelo = 2;
            Timer proximo = new Timer(2500, e -> iniciarRodadaDuelo());
            proximo.setRepeats(false);
            proximo.start();
        } else {
            tempoJogador2Duelo = tempoFinal;
            venceuJogador2Duelo = venceu;
            emDuelo = false;
            Timer proximo = new Timer(2500, e -> mostrarResultadoDuelo());
            proximo.setRepeats(false);
            proximo.start();
        }
    }

    private void mostrarResultadoDuelo() {
        view.mostrarTelaResultadoDuelo(nomeJogador1Duelo, tempoJogador1Duelo, venceuJogador1Duelo,
                nomeJogador2Duelo, tempoJogador2Duelo, venceuJogador2Duelo, calcularVencedorDuelo());
    }

    private String calcularVencedorDuelo() {
        if (venceuJogador1Duelo && !venceuJogador2Duelo) return nomeJogador1Duelo;
        if (venceuJogador2Duelo && !venceuJogador1Duelo) return nomeJogador2Duelo;
        if (venceuJogador1Duelo && venceuJogador2Duelo) {
            return tempoJogador1Duelo <= tempoJogador2Duelo ? nomeJogador1Duelo : nomeJogador2Duelo;
        }
        return null;
    }

    @Override
    public void aoPedirSalvarPartida() {
        if (tabuleiro == null || tabuleiro.isJogoEncerrado()) {
            return;
        }
        int segundosDecorridos = jogoIniciado ? (int) obterSegundosPassados() : 0;
        gerenciadorSalvamento.salvar(perfilAtual.getNome(), tabuleiro, totalMinas, totalCelulas,
                celulasReveladas, jogadas, segundosDecorridos, limiteSegundos, usouResolverNestaPartida);
        view.definirPartidaSalvaDisponivel(true);
        view.mostrarMensagemStatus("💾 Partida salva!");
    }

    @Override
    public void aoFazerLogin(String nomePerfil) {
        if (nomePerfil == null || nomePerfil.isBlank()) {
            return;
        }
        perfilAtual = gerenciadorPerfis.carregarOuCriar(nomePerfil.trim());
        gerenciadorPerfis.salvar(perfilAtual);
        view.definirPartidaSalvaDisponivel(gerenciadorSalvamento.existePartidaSalva(perfilAtual.getNome()));
        view.mostrarTelaInicial();
    }

    @Override
    public void aoPedirContinuarPartida() {
        retomarPartidaSalva();
    }

    @Override
    public void aoPedirNovoJogo() {
        pararTimer();
        emDuelo = false;
        view.mostrarTelaInicial();
    }

    @Override
    public void aoMarcarCelula(int linha, int coluna) {
        if (tabuleiro.isJogoEncerrado()) {
            return;
        }
        usouBandeiraNestaPartida = true;
        historicoJogadas.add(new Jogada(Jogada.Tipo.MARCAR, linha, coluna));
        tabuleiro.alternarMarcacao(linha, coluna);
        view.tocarSom("bandeira.wav");
        view.atualizarCelula(linha, coluna, tabuleiro);
        atualizarEstatisticasNaView();
    }

    @Override
    public void aoRevelarCelula(int linha, int coluna) {
        if (tabuleiro.isJogoEncerrado()) {
            return;
        }

        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }

        if (limiteSegundos > 0 && obterSegundosPassados() >= limiteSegundos) {
            encerrarPorTempo();
            return;
        }

        jogadas++;
        historicoJogadas.add(new Jogada(Jogada.Tipo.REVELAR, linha, coluna));
        List<int[]> reveladas = tabuleiro.revelar(linha, coluna);
        celulasReveladas = contarCelulasReveladas();

        view.tocarSom("clique.wav");

        int atraso = reveladas.size() > 80 ? 3 : (reveladas.size() > 25 ? 8 : 18);
        animarRevelacao(reveladas, 0, atraso);
    }

    @Override
    public void aoRepetirPartida() {
        if (tabuleiro == null) {
            return;
        }

        int linhas = tabuleiro.getLinhas();
        int colunas = tabuleiro.getColunas();

        pararTimer();
        this.tabuleiro = new Tabuleiro(linhas, colunas, totalMinas);
        this.posicoesMinasPartidaAtual = tabuleiro.getPosicoesMinas();
        this.historicoJogadas.clear();
        this.emReplay = false;
        this.solver = new Solver(tabuleiro);
        this.celulasReveladas = 0;
        this.jogadas = 0;
        this.jogoIniciado = false;
        this.usouResolverNestaPartida = false;
        this.usouBandeiraNestaPartida = false;
        this.usouDicaNestaPartida = false;

        view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
        view.atualizarEstatisticas(totalMinas, 0, totalCelulas, 0);
        view.definirJogadorAtualDuelo(null);
        view.definirResolverDisponivel(true);
    }

    @Override
    public void aoPedirResolverAutomatico() {
        if (emDuelo) {
            return;
        }
        usouResolverNestaPartida = true;
        if (tabuleiro.isJogoEncerrado()) {
            return;
        }

        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }

        executarPassoDoResolver();
    }

    private void executarPassoDoResolver() {
    if (tabuleiro.isJogoEncerrado()) {
        return;
    }

    List<int[]> minadas = solver.encontrarCelulasMinadas();
    for (int[] pos : minadas) {
        if (!tabuleiro.isMarcada(pos[0], pos[1])) {
            tabuleiro.alternarMarcacao(pos[0], pos[1]);
            historicoJogadas.add(new Jogada(Jogada.Tipo.MARCAR, pos[0], pos[1]));
            view.atualizarCelula(pos[0], pos[1], tabuleiro);
        }
    }

    List<int[]> seguras = solver.encontrarCelulasSeguras();
    for (int[] pos : seguras) {
        historicoJogadas.add(new Jogada(Jogada.Tipo.REVELAR, pos[0], pos[1]));
        List<int[]> afetadas = tabuleiro.revelar(pos[0], pos[1]);
        for (int[] afetada : afetadas) {
            view.atualizarCelula(afetada[0], afetada[1], tabuleiro);
        }
    }

    celulasReveladas = contarCelulasReveladas();
    atualizarEstatisticasNaView();

    if (tabuleiro.isJogoEncerrado()) {
        finalizarJogada();
        return;
    }

    boolean progrediu = !minadas.isEmpty() || !seguras.isEmpty();

    if (!progrediu) {
        int[] palpite = encontrarCelulaInicialAleatoria();

        if (palpite != null) {
            historicoJogadas.add(new Jogada(Jogada.Tipo.REVELAR, palpite[0], palpite[1]));
            List<int[]> afetadas = tabuleiro.revelar(palpite[0], palpite[1]);
            for (int[] afetada : afetadas) {
                view.atualizarCelula(afetada[0], afetada[1], tabuleiro);
            }
            celulasReveladas = contarCelulasReveladas();
            atualizarEstatisticasNaView();

            if (tabuleiro.isJogoEncerrado()) {
                finalizarJogada();
                return;
            }
            agendarProximoPassoDoResolver();
        }
        return;
    }

    agendarProximoPassoDoResolver();
}

    private void agendarProximoPassoDoResolver() {
        Timer proximoPasso = new Timer(500, e -> executarPassoDoResolver());
        proximoPasso.setRepeats(false);
        proximoPasso.start();
    }

    private int[] encontrarCelulaInicialAleatoria() {
        List<int[]> candidatas = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (!tabuleiro.isRevelada(i, j) && !tabuleiro.isMarcada(i, j)) {
                    candidatas.add(new int[] { i, j });
                }
            }
        }
        if (candidatas.isEmpty()) {
            return null;
        }
        return candidatas.get(new java.util.Random().nextInt(candidatas.size()));
    }

    private int[] encontrarPalpiteArriscado() {
        List<int[]> candidatas = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (!tabuleiro.isRevelada(i, j) && !tabuleiro.isMarcada(i, j)) {
                    candidatas.add(new int[] { i, j });
                }
            }
        }
        if (candidatas.isEmpty()) {
            return null;
        }
        return candidatas.get(new java.util.Random().nextInt(candidatas.size()));
    }

    @Override
    public void aoPedirDica() {
        if (tabuleiro.isJogoEncerrado()) {
            return;
        }
        usouDicaNestaPartida = true;

        if (!jogoIniciado) {
            jogoIniciado = true;
            tempoInicio = System.currentTimeMillis();
            iniciarTimer();
        }

        List<int[]> minasDeduzidas = solver.encontrarCelulasMinadas();
        int[] celulaEscolhida;

        if (!minasDeduzidas.isEmpty()) {
            celulaEscolhida = minasDeduzidas.get(new java.util.Random().nextInt(minasDeduzidas.size()));
        } else {
            celulaEscolhida = encontrarMinaQualquerNaoMarcada();
        }

        if (celulaEscolhida == null) {
            return;
        }

        tempoInicio -= 40_000L;
        atualizarTempo();

        tabuleiro.alternarMarcacao(celulaEscolhida[0], celulaEscolhida[1]);
        view.atualizarCelula(celulaEscolhida[0], celulaEscolhida[1], tabuleiro);
        atualizarEstatisticasNaView();
    }

    private int[] encontrarMinaQualquerNaoMarcada() {
    List<int[]> candidatas = new java.util.ArrayList<>();
    for (int i = 0; i < tabuleiro.getLinhas(); i++) {
        for (int j = 0; j < tabuleiro.getColunas(); j++) {
            if (tabuleiro.isMinada(i, j) && !tabuleiro.isMarcada(i, j) && !tabuleiro.isRevelada(i, j)) {
                candidatas.add(new int[] { i, j });
            }
        }
    }
    if (candidatas.isEmpty()) {
        return null;
    }
    return candidatas.get(new java.util.Random().nextInt(candidatas.size()));
}
    // ================================================================
    // Contagens e sincronização com a View
    // ================================================================

    private int contarCelulasReveladas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int contarMarcadas() {
        int count = 0;
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMarcada(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private void atualizarEstatisticasNaView() {
        int restantes = totalMinas - contarMarcadas();
        view.atualizarEstatisticas(restantes, celulasReveladas, totalCelulas, jogadas);
    }

    // ================================================================
    // Timer do cronômetro
    // ================================================================

    private void iniciarTimer() {
        timerJogo = new Timer(1000, e -> atualizarTempo());
        timerJogo.start();
    }

    private void pararTimer() {
        if (timerJogo != null) {
            timerJogo.stop();
        }
    }

    private long obterSegundosPassados() {
        return (System.currentTimeMillis() - tempoInicio) / 1000;
    }

    private void atualizarTempo() {
        long segundosPassados = obterSegundosPassados();
        if (limiteSegundos > 0) {
            long restantes = Math.max(0, limiteSegundos - segundosPassados);
            view.atualizarTempo(String.format("-%02d:%02d", restantes / 60, restantes % 60));
            if (restantes <= 0) {
                encerrarPorTempo();
                return;
            }
        } else {
            view.atualizarTempo(String.format("%02d:%02d", segundosPassados / 60, segundosPassados % 60));
        }
    }

    private void encerrarPorTempo() {
        pararTimer();
        if (tabuleiro != null && !tabuleiro.isJogoEncerrado()) {
            tabuleiro = new Tabuleiro(tabuleiro.getLinhas(), tabuleiro.getColunas(), tabuleiro.getNumMinas());
            // Não reiniciamos o tabuleiro; apenas exibimos derrota devido ao tempo.
        }
        view.mostrarDerrota();
        labelStatusTempoEsgotado();
    }

    private void labelStatusTempoEsgotado() {
        view.mostrarDerrota();
    }

    // ================================================================
    // Animações (o Controller decide o ritmo; a View só desenha um passo)
    // ================================================================

    private void animarRevelacao(List<int[]> celulas, int indice, int atraso) {
        if (indice >= celulas.size()) {
            finalizarJogada();
            return;
        }
        int[] posicao = celulas.get(indice);
        view.atualizarCelula(posicao[0], posicao[1], tabuleiro);

        Timer timer = new Timer(atraso, e -> animarRevelacao(celulas, indice + 1, atraso));
        timer.setRepeats(false);
        timer.start();
    }

    private void finalizarJogada() {
        atualizarEstatisticasNaView();

        if (!tabuleiro.isJogoEncerrado()) {
            return;
        }

        pararTimer();

        if (emDuelo) {
            finalizarRodadaDuelo();
            return;
        }

        if (!emReplay) {
            if (tabuleiro.isDerrota()) {
                perfilAtual.registrarDerrota();
            }
            gerenciadorPerfis.salvar(perfilAtual);
        }

        if (tabuleiro.isDerrota()) {
            view.mostrarDerrota();
            view.chacoalharJanela();
            view.tocarSom("explosao.wav");
            animarExplosao();
        } else {
            view.mostrarVitoria();
            view.celebrarVitoria();
            view.tocarSom("vitoria.wav");
            animarVitoria();

        if (!emReplay) {
            boolean contaParaRecorde = !usouResolverNestaPartida && limiteSegundos > 0;
            int tempoFinal = (int) obterSegundosPassados();
            perfilAtual.registrarVitoria(nomeDificuldadeAtual(), tempoFinal, contaParaRecorde);
            verificarConquistas(tempoFinal);
            gerenciadorPerfis.salvar(perfilAtual);

                if (contaParaRecorde) {
                    ranking.registrarTempo(perfilAtual.getNome(), tempoFinal, nomeDificuldadeAtual());
                }
            }
        }
    }

    private void verificarConquistas(int tempoFinal) {
            if (usouResolverNestaPartida) {
                return;
            }
        List<String> novas = new java.util.ArrayList<>();

        if (perfilAtual.getVitorias() == 1 && perfilAtual.desbloquear("PRIMEIRA_VITORIA")) {
            novas.add("Primeira Vitória");
        }
        if (!usouBandeiraNestaPartida && perfilAtual.desbloquear("SEM_BANDEIRA")) {
            novas.add("Sem Bandeira");
        }
        if (tempoFinal < 30 && perfilAtual.desbloquear("VELOCISTA")) {
            novas.add("Velocista");
        }
        if ("HARD".equals(nomeDificuldadeAtual()) && perfilAtual.desbloquear("MESTRE_HARD")) {
            novas.add("Mestre da Matrix");
        }
        if (!usouResolverNestaPartida && !usouDicaNestaPartida && perfilAtual.desbloquear("SEM_AJUDA")) {
            novas.add("Por Conta Própria");
        }
        if (perfilAtual.getMelhorTempoEasy() > 0 && perfilAtual.getMelhorTempoMedium() > 0
                && perfilAtual.getMelhorTempoHard() > 0 && perfilAtual.desbloquear("COLECIONADOR")) {
            novas.add("Colecionador");
        }

        if (!novas.isEmpty()) {
            view.mostrarConquistasDesbloqueadas(novas);
        }
    }

    private void animarExplosao() {
        Timer piscar = new Timer(100, null);
        int[] contador = {0};
        piscar.addActionListener(e -> {
            contador[0]++;
            view.piscarFundoDeExplosao(contador[0] % 2 == 1);
            if (contador[0] >= 6) {
                piscar.stop();
                view.piscarFundoDeExplosao(false);
                revelarMinasComAnimacao();
            }
        });
        piscar.start();
    }

    private void revelarMinasComAnimacao() {
        List<int[]> minasNaoReveladas = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isMinada(i, j) && !tabuleiro.isRevelada(i, j)) {
                    minasNaoReveladas.add(new int[]{i, j});
                }
            }
        }
        revelarMinasPasso(minasNaoReveladas, 0);
    }

    private void revelarMinasPasso(List<int[]> minas, int indice) {
        if (indice >= minas.size()) {
            return;
        }
        int[] posicao = minas.get(indice);
        view.marcarMinaExplodida(posicao[0], posicao[1]);

        Timer timer = new Timer(80, e -> revelarMinasPasso(minas, indice + 1));
        timer.setRepeats(false);
        timer.start();
    }

    private void animarVitoria() {
        List<int[]> celulasSeguras = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                if (tabuleiro.isRevelada(i, j) && !tabuleiro.isMinada(i, j)) {
                    celulasSeguras.add(new int[]{i, j});
                }
            }
        }
        vitoriaPasso(celulasSeguras, 0);
    }

    private void vitoriaPasso(List<int[]> celulas, int indice) {
        if (indice >= celulas.size()) {
            return;
        }
        int[] atual = celulas.get(indice);
        view.destacarCelulaVencedora(atual[0], atual[1]);

        Timer timer = new Timer(8, e -> vitoriaPasso(celulas, indice + 1));
        timer.setRepeats(false);
        timer.start();
    }

    private String nomeDificuldadeAtual() {
        int linhas = tabuleiro.getLinhas();
        int colunas = tabuleiro.getColunas();
        if (linhas == 9 && colunas == 9) {
            return "EASY";
        }
        if (linhas == 16 && colunas == 16) {
            return "MEDIUM";
        }
        if (linhas == 16 && colunas == 30) {
            return "HARD";
        }
        return "Personalizado";
    }
    @Override
    public void aoPedirVerRanking() {
        view.mostrarTelaRanking(
                ranking.getMelhoresPorDificuldade("EASY", 5),
                ranking.getMelhoresPorDificuldade("MEDIUM", 5),
                ranking.getMelhoresPorDificuldade("HARD", 5)
        );
    }

    @Override
    public void aoPedirVerConquistas() {
        view.mostrarTelaConquistas(model.CatalogoConquistas.todas(), perfilAtual.getConquistas());
    }

    @Override
        public void aoPedirReplay() {
            if (posicoesMinasPartidaAtual == null || historicoJogadas.isEmpty()) {
                return;
            }
            pararTimer();
            emReplay = true;
            int linhas = tabuleiro.getLinhas();
            int colunas = tabuleiro.getColunas();
            this.tabuleiro = new Tabuleiro(linhas, colunas, posicoesMinasPartidaAtual);
            this.solver = new Solver(tabuleiro);
            view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
            view.atualizarEstatisticas(totalMinas, 0, totalCelulas, 0);
            reproduzirJogada(0);
        }

        private void reproduzirJogada(int indice) {
            if (indice >= historicoJogadas.size() || tabuleiro.isJogoEncerrado()) {
                emReplay = false;
                if (tabuleiro.isJogoEncerrado()) {
                    finalizarJogada();
                }
                return;
            }

            Jogada jogada = historicoJogadas.get(indice);
            if (jogada.getTipo() == Jogada.Tipo.REVELAR) {
                List<int[]> afetadas = tabuleiro.revelar(jogada.getLinha(), jogada.getColuna());
                for (int[] pos : afetadas) {
                    view.atualizarCelula(pos[0], pos[1], tabuleiro);
                }
            } else {
                tabuleiro.alternarMarcacao(jogada.getLinha(), jogada.getColuna());
                view.atualizarCelula(jogada.getLinha(), jogada.getColuna(), tabuleiro);
            }

            celulasReveladas = contarCelulasReveladas();
            atualizarEstatisticasNaView();

            Timer proximo = new Timer(400, e -> reproduzirJogada(indice + 1));
            proximo.setRepeats(false);
            proximo.start();
        }

        private void retomarPartidaSalva() {
        PartidaCarregada carregada = gerenciadorSalvamento.carregar(perfilAtual.getNome());
        if (carregada == null) {
            view.mostrarTelaInicial();
            return;
        }

        this.tabuleiro = carregada.getTabuleiro();
        this.solver = new Solver(tabuleiro);
        this.totalMinas = carregada.getTotalMinas();
        this.totalCelulas = carregada.getTotalCelulas();
        this.celulasReveladas = carregada.getCelulasReveladas();
        this.jogadas = carregada.getJogadas();
        this.limiteSegundos = carregada.getLimiteSegundos();
        this.usouResolverNestaPartida = carregada.isUsouResolver();
        this.posicoesMinasPartidaAtual = tabuleiro.getPosicoesMinas();
        this.historicoJogadas.clear();
        this.emReplay = false;
        this.jogoIniciado = true;
        this.tempoInicio = System.currentTimeMillis() - (carregada.getSegundosDecorridos() * 1000L);
        iniciarTimer();

        int linhas = tabuleiro.getLinhas();
        int colunas = tabuleiro.getColunas();
        view.iniciarTelaDeJogo(linhas, colunas, totalMinas, totalCelulas, limiteSegundos);
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                view.atualizarCelula(i, j, tabuleiro);
            }
        }
        atualizarEstatisticasNaView();
        gerenciadorSalvamento.apagar(perfilAtual.getNome());
        view.definirPartidaSalvaDisponivel(false);
        view.definirJogadorAtualDuelo(null);
        view.definirResolverDisponivel(true);
    }

    @Override
    public void aoPedirVerHistorico() {
        view.mostrarTelaHistorico(perfilAtual);
    }

}
