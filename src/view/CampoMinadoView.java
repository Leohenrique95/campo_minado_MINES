package view;

import controller.AcoesJogador;
import model.LeituraTabuleiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.RegistroRanking;
import java.util.List;
import model.Perfil;
import model.Conquista;
import model.Perfil;

/**
 * VIEW da arquitetura MVC: cuida só de desenhar a tela e capturar
 * interações do usuário. Nunca decide o que um clique "significa" em
 * termos de regra de jogo — ela apenas repassa o clique para quem
 * implementa {@link AcoesJogador} (o Controller) e espera ser chamada de
 * volta para atualizar o que aparece na tela.
 */
public class CampoMinadoView extends JFrame {

    private static final Color COR_FUNDO = new Color(30, 30, 35);
    private static final Color COR_FUNDO_CLARO = new Color(45, 45, 52);
    private static final Color COR_DESTAQUE = new Color(70, 130, 180);

    // Célula OCULTA: escura e "elevada" — ainda não foi clicada.
    private static final Color COR_CELULA_OCULTA = new Color(72, 78, 96);
    private static final Color COR_CELULA_OCULTA_HOVER = new Color(90, 97, 118);
    private static final Color COR_BORDA_OCULTA = new Color(100, 107, 128);

    // Célula REVELADA: clara e "afundada" — contraste forte e
    // inconfundível com a célula oculta, como no Campo Minado clássico.
    private static final Color COR_CELULA_REVELADA = new Color(228, 228, 233);
    private static final Color COR_BORDA_REVELADA = new Color(195, 195, 202);
    private static final Color COR_TEXTO_SOBRE_REVELADA = new Color(40, 40, 45);

    private static final Color COR_MINA = new Color(220, 60, 60);
    private static final Color COR_MINA_FUNDO = new Color(60, 20, 20);
    private static final Color COR_VITORIA = new Color(50, 180, 80);
    private static final Color COR_TEXTO_PRINCIPAL = new Color(230, 230, 235);
    private static final Color COR_TEXTO_SECUNDARIO = new Color(150, 150, 160);
    private static final Color COR_BORDA = new Color(80, 80, 90);
    private static final Color COR_CARD = new Color(50, 50, 58);
    private static final Color COR_CARD_HOVER = new Color(65, 65, 78);
    private static final Color COR_BANDEIRA = new Color(230, 180, 50);

    private static final String[] TEMAS_CYBERPUNK = {"Escuro", "Claro"};
    private static final String[] TEMPOS_JOGO = {"Sem tempo", "1 minuto", "2 minutos", "3 minutos", "5 minutos"};

    private static final Font FONTE_CELULA = new Font("Segoe UI Emoji", Font.BOLD, 20);
    private static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONTE_NUMERO = new Font("Consolas", Font.BOLD, 18);
    private static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 12);

    private static final String EMOJI_BOMBA = "\uD83D\uDCA3";
    private static final String EMOJI_BANDEIRA = "\uD83D\uDEA9";
    private static final String EMOJI_TROFEU = "\uD83C\uDFC6";
    private static final String EMOJI_EXPLOSAO = "\uD83D\uDCA5";
    private static final String EMOJI_RELOGIO = "\u23F1";
    private static final String EMOJI_JOGADA = "\uD83D\uDC46";
    // Antes havia um "quadradinho" (\u25A0) usado como ícone de estatística.
    // Trocado pelo emoji de bomba, como pedido.
    private static final String EMOJI_ICONE_ESTATISTICA = EMOJI_BOMBA;
    private static final Font FONTE_EMOJI_TITULO = new Font("Segoe UI Emoji", Font.PLAIN, 28);

    // Esquema clássico do Campo Minado, pensado para boa leitura sobre o
    // fundo claro (COR_CELULA_REVELADA) da célula já revelada.
    private static final Color[] CORES_NUMEROS = {
            null,
            new Color(25, 118, 210),   // 1 - azul
            new Color(56, 142, 60),    // 2 - verde
            new Color(211, 47, 47),    // 3 - vermelho
            new Color(13, 71, 161),    // 4 - azul-marinho
            new Color(136, 14, 14),    // 5 - vinho
            new Color(0, 131, 143),    // 6 - teal
            new Color(33, 33, 33),     // 7 - preto
            new Color(97, 97, 97)      // 8 - cinza-escuro
    };

    private AcoesJogador ouvinte;
    private JButton[][] botoes;
    private JLabel labelStatus;

    private JLabel lblTempo;
    private JLabel lblMinasRestantes;
    private JLabel lblCelulasReveladas;
    private JLabel lblJogadas;
    private JProgressBar barraProgresso;
    private JPanel painelJogadorDuelo;
    private JLabel lblJogadorDuelo;

    private JComboBox<String> comboModo;
    private JComboBox<String> comboTempo;
    private JButton btnResolver;

    private Color corFundo = COR_FUNDO;
    private Color corFundoClaro = COR_FUNDO_CLARO;
    private Color corDestaque = COR_DESTAQUE;
    private Color corTextoPrincipal = COR_TEXTO_PRINCIPAL;
    private Color corTextoSecundario = COR_TEXTO_SECUNDARIO;
    private Color corCard = COR_CARD;
    private Color corCardHover = COR_CARD_HOVER;
    private Color corBorda = COR_BORDA;
    private Color corCelulaOculta = COR_CELULA_OCULTA;
    private Color corCelulaOcultaHover = COR_CELULA_OCULTA_HOVER;
    private Color corBordaOculta = COR_BORDA_OCULTA;
    private Color corCelulaRevelada = COR_CELULA_REVELADA;
    private Color corBordaRevelada = COR_BORDA_REVELADA;
    private Color corTextoSobreRevelada = COR_TEXTO_SOBRE_REVELADA;
    private Color corMinaFundo = COR_MINA_FUNDO;
    private String temaAtual = "Escuro";
    private PainelConfete painelConfete;
    private boolean partidaSalvaDisponivel;
    private final GerenciadorSom som = new GerenciadorSom();
    private static final Font FONTE_SECAO = new Font("Segoe UI", Font.BOLD, 20);

    private JButton criarBotaoMenu(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_NORMAL);
        botao.setForeground(corTextoPrincipal);
        botao.setBackground(corFundoClaro);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.addActionListener(e -> acao.run());
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botao.isEnabled()) botao.setBackground(corCardHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (botao.isEnabled()) botao.setBackground(corFundoClaro);
            }
        });
        return botao;
    }
        
    public CampoMinadoView() {
        super("M.I.N.E.S.");
        temaAtual = DetectorTemaSistema.detectarTemaPreferido();
        aplicarTemaSelecionado();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(corFundo);
        setLocationRelativeTo(null);
        setResizable(false);

        painelConfete = new PainelConfete();
        setGlassPane(painelConfete);
        painelConfete.setVisible(false);
    }

    public void tocarSom(String nomeArquivo) {
        som.tocar("sons/" + nomeArquivo);
    }

    /** Define quem recebe os eventos de clique/escolha (o Controller). */
    public void setOuvinte(AcoesJogador ouvinte) {
        this.ouvinte = ouvinte;
    }

    // ================================================================
    // TELA INICIAL
    // ================================================================

    public void mostrarTelaInicial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painelTopo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        painelTopo.setBackground(corFundo);
        painelTopo.add(criarSeletorTemaCompacto(this::mostrarTelaInicial));
        add(painelTopo, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(corFundo);
        painelCentral.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(corFundo);
        painelConteudo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        painelTitulo.setBackground(corFundo);
        painelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emoji = new JLabel(EMOJI_BOMBA);
        emoji.setFont(FONTE_EMOJI_TITULO);
        emoji.setForeground(corTextoPrincipal);

        JLabel texto = new JLabel("M.I.N.E.S.");
        texto.setFont(FONTE_TITULO);
        texto.setForeground(corTextoPrincipal);

        painelTitulo.add(emoji);
        painelTitulo.add(texto);
        painelConteudo.add(painelTitulo);

        JLabel tagline = new JLabel("Matrix Inspection & Neural Exclusion System");
        tagline.setFont(FONTE_PEQUENA);
        tagline.setForeground(corTextoSecundario);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(tagline);
        painelConteudo.add(Box.createVerticalStrut(25));

        JPanel colunaEsquerda = new JPanel();
        colunaEsquerda.setLayout(new BoxLayout(colunaEsquerda, BoxLayout.Y_AXIS));
        colunaEsquerda.setBackground(corFundo);

        JLabel lblDificuldade = new JLabel("Dificuldade");
        lblDificuldade.setFont(FONTE_SECAO);
        lblDificuldade.setForeground(corTextoPrincipal);
        lblDificuldade.setAlignmentX(Component.CENTER_ALIGNMENT);
        colunaEsquerda.add(lblDificuldade);
        colunaEsquerda.add(Box.createVerticalStrut(12));

        colunaEsquerda.add(criarCardDificuldade("EASY", "9 × 9", "10 minas", 9, 9, 10));
        colunaEsquerda.add(Box.createVerticalStrut(10));
        colunaEsquerda.add(criarCardDificuldade("MEDIUM", "16 × 16", "40 minas", 16, 16, 40));
        colunaEsquerda.add(Box.createVerticalStrut(10));
        colunaEsquerda.add(criarCardDificuldade("HARD", "16 × 30", "99 minas", 16, 30, 99));


        JPanel colunaDireita = new JPanel();
        colunaDireita.setLayout(new BoxLayout(colunaDireita, BoxLayout.Y_AXIS));
        colunaDireita.setBackground(corFundo);
        colunaDireita.add(Box.createVerticalGlue());
        JLabel lblTempo = new JLabel("Tempo");
        lblTempo.setFont(FONTE_SECAO);
        lblTempo.setForeground(corTextoPrincipal);
        lblTempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        colunaDireita.add(lblTempo);
        colunaDireita.add(Box.createVerticalStrut(12));

        comboTempo = new JComboBox<>(TEMPOS_JOGO);
        comboTempo.setFont(FONTE_NORMAL);
        comboTempo.setBackground(corFundoClaro);
        comboTempo.setForeground(corTextoPrincipal);
        comboTempo.setBorder(BorderFactory.createLineBorder(corBorda));
        comboTempo.setMaximumSize(new Dimension(190, 36));
        comboTempo.setAlignmentX(Component.CENTER_ALIGNMENT);
        colunaDireita.add(comboTempo);
        colunaDireita.add(Box.createVerticalStrut(20));

        JButton btnComoJogar = new JButton("Como Jogar");
        btnComoJogar.setFont(FONTE_NORMAL);
        btnComoJogar.setForeground(corTextoPrincipal);
        btnComoJogar.setBackground(corFundoClaro);
        btnComoJogar.setFocusPainted(false);
        btnComoJogar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        btnComoJogar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComoJogar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnComoJogar.addActionListener(e -> mostrarTutorial());
        btnComoJogar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnComoJogar.setBackground(corCardHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnComoJogar.setBackground(corFundoClaro);
            }
        });
        colunaDireita.add(btnComoJogar);

        colunaDireita.add(Box.createVerticalGlue());

        JPanel painelDuasColunas = new JPanel(new GridLayout(1, 2, 50, 0));
        painelDuasColunas.setBackground(corFundo);
        painelDuasColunas.add(colunaEsquerda);
        painelDuasColunas.add(colunaDireita);
        painelDuasColunas.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelConteudo.add(painelDuasColunas);

        if (partidaSalvaDisponivel) {
            JButton btnContinuar = new JButton("<html><font face='Segoe UI Emoji'>▶</font> Continuar Partida</html>");
            btnContinuar.setFont(FONTE_NORMAL);
            btnContinuar.setForeground(corTextoPrincipal);
            btnContinuar.setBackground(corDestaque);
            btnContinuar.setFocusPainted(false);
            btnContinuar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBorda),
                    BorderFactory.createEmptyBorder(8, 20, 8, 20)
            ));
            btnContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnContinuar.addActionListener(e -> {
                if (ouvinte != null) {
                    ouvinte.aoPedirContinuarPartida();
                }
            });
            painelConteudo.add(Box.createVerticalStrut(20));
            painelConteudo.add(btnContinuar);
        }

        painelConteudo.add(Box.createVerticalStrut(20));

        JPanel painelBotoesInferior = new JPanel(new GridLayout(1, 4, 15, 0));
        painelBotoesInferior.setBackground(corFundo);
        painelBotoesInferior.add(criarBotaoMenu("<html><font face='Segoe UI Emoji'>🏆</font> Ranking</html>", () -> {
            if (ouvinte != null) ouvinte.aoPedirVerRanking();
        }));
        painelBotoesInferior.add(criarBotaoMenu("<html><font face='Segoe UI Emoji'>🏅</font> Conquistas</html>", () -> {
            if (ouvinte != null) ouvinte.aoPedirVerConquistas();
        }));
        painelBotoesInferior.add(criarBotaoMenu("<html><font face='Segoe UI Emoji'>📊</font> Histórico</html>", () -> {
            if (ouvinte != null) ouvinte.aoPedirVerHistorico();
        }));
        JButton btnDuelo = criarBotaoMenu("<html><font face='Segoe UI Emoji'>⚔</font> Duelo</html>", this::mostrarTelaConfigDuelo);
        painelBotoesInferior.add(btnDuelo);
        painelConteudo.add(painelBotoesInferior);

        painelCentral.add(painelConteudo);
        add(painelCentral, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public void definirPartidaSalvaDisponivel(boolean disponivel) {
        this.partidaSalvaDisponivel = disponivel;
    }

    public void mostrarTelaConfigDuelo() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        painelTitulo.setBackground(corFundo);
        painelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel iconeTitulo = new JLabel("⚔");
        iconeTitulo.setFont(FONTE_EMOJI_TITULO);
        iconeTitulo.setForeground(corTextoPrincipal);
        iconeTitulo.setPreferredSize(new Dimension(40, 40));
        iconeTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel textoTitulo = new JLabel("Duelo");
        textoTitulo.setFont(FONTE_TITULO);
        textoTitulo.setForeground(corTextoPrincipal);
        painelTitulo.add(iconeTitulo);
        painelTitulo.add(textoTitulo);
        painel.add(painelTitulo);
        painel.add(Box.createVerticalStrut(10));

        JLabel subtitulo = new JLabel("Mesmo tabuleiro para os dois — quem for mais rápido vence");
        subtitulo.setFont(FONTE_PEQUENA);
        subtitulo.setForeground(corTextoSecundario);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(subtitulo);
        painel.add(Box.createVerticalStrut(25));

        JTextField campoNome1 = new JTextField();
        campoNome1.setFont(FONTE_NORMAL);
        campoNome1.setMaximumSize(new Dimension(260, 36));
        campoNome1.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoNome1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        JLabel lblNome1 = new JLabel("Nome do Jogador 1");
        lblNome1.setFont(FONTE_PEQUENA);
        lblNome1.setForeground(corTextoSecundario);
        lblNome1.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblNome1);
        painel.add(Box.createVerticalStrut(5));
        painel.add(campoNome1);
        painel.add(Box.createVerticalStrut(15));

        JTextField campoNome2 = new JTextField();
        campoNome2.setFont(FONTE_NORMAL);
        campoNome2.setMaximumSize(new Dimension(260, 36));
        campoNome2.setAlignmentX(Component.CENTER_ALIGNMENT);
        campoNome2.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        JLabel lblNome2 = new JLabel("Nome do Jogador 2");
        lblNome2.setFont(FONTE_PEQUENA);
        lblNome2.setForeground(corTextoSecundario);
        lblNome2.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblNome2);
        painel.add(Box.createVerticalStrut(5));
        painel.add(campoNome2);
        painel.add(Box.createVerticalStrut(25));

        JLabel lblDificuldade = new JLabel("Escolha a dificuldade");
        lblDificuldade.setFont(FONTE_SECAO);
        lblDificuldade.setForeground(corTextoPrincipal);
        lblDificuldade.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDificuldade);
        painel.add(Box.createVerticalStrut(15));

        JPanel painelBotoesDificuldade = new JPanel(new GridLayout(1, 3, 15, 0));
        painelBotoesDificuldade.setBackground(corFundo);
        painelBotoesDificuldade.setMaximumSize(new Dimension(420, 44));
        painelBotoesDificuldade.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelBotoesDificuldade.add(criarBotaoDuelo("EASY", campoNome1, campoNome2, 9, 9, 10));
        painelBotoesDificuldade.add(criarBotaoDuelo("MEDIUM", campoNome1, campoNome2, 16, 16, 40));
        painelBotoesDificuldade.add(criarBotaoDuelo("HARD", campoNome1, campoNome2, 16, 30, 99));
        painel.add(painelBotoesDificuldade);
        painel.add(Box.createVerticalStrut(20));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(corTextoPrincipal);
        voltar.setBackground(corFundoClaro);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda), BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        voltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JButton criarBotaoDuelo(String texto, JTextField campoNome1, JTextField campoNome2,
                                    int linhas, int colunas, int minas) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_NORMAL);
        botao.setForeground(corTextoPrincipal);
        botao.setBackground(corFundoClaro);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda), BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botao.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoIniciarDuelo(campoNome1.getText(), campoNome2.getText(), linhas, colunas, minas);
            }
        });
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { botao.setBackground(corCardHover); }
            @Override
            public void mouseExited(MouseEvent e) { botao.setBackground(corFundoClaro); }
        });
        return botao;
    }

    public void mostrarTelaResultadoDuelo(String nome1, int tempo1, boolean venceu1,
                                        String nome2, int tempo2, boolean venceu2, String vencedor) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        painelTitulo.setBackground(corFundo);
        painelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel iconeTitulo = new JLabel("⚔");
        iconeTitulo.setFont(FONTE_EMOJI_TITULO);
        iconeTitulo.setForeground(corTextoPrincipal);
        iconeTitulo.setPreferredSize(new Dimension(40, 40));
        iconeTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel textoTitulo = new JLabel("Resultado do Duelo");
        textoTitulo.setFont(FONTE_TITULO);
        textoTitulo.setForeground(corTextoPrincipal);
        painelTitulo.add(iconeTitulo);
        painelTitulo.add(textoTitulo);
        painel.add(painelTitulo);
        painel.add(Box.createVerticalStrut(10));

        String mensagem = vencedor != null
        ? ("<html><font face='Segoe UI Emoji'>🏆</font> " + vencedor + " venceu!</html>")
        : "Empate — os dois perderam";
        JLabel lblVencedor = new JLabel(mensagem);
        lblVencedor.setFont(FONTE_SUBTITULO);
        lblVencedor.setForeground(corDestaque);
        lblVencedor.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblVencedor);
        painel.add(Box.createVerticalStrut(25));

        JPanel painelColunas = new JPanel(new GridLayout(1, 2, 20, 0));
        painelColunas.setBackground(corFundo);
        painelColunas.add(criarCartaoResultadoDuelo(nome1, tempo1, venceu1));
        painelColunas.add(criarCartaoResultadoDuelo(nome2, tempo2, venceu2));
        painel.add(painelColunas);
        painel.add(Box.createVerticalStrut(25));

        JButton voltar = new JButton("Voltar ao Menu");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(corTextoPrincipal);
        voltar.setBackground(corFundoClaro);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda), BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        voltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarCartaoResultadoDuelo(String nome, int tempoSegundos, boolean venceu) {
        JPanel cartao = new JPanel();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBackground(corCard);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda), BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(FONTE_SUBTITULO);
        lblNome.setForeground(corTextoPrincipal);
        lblNome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTempo = new JLabel(String.format("%02d:%02d", tempoSegundos / 60, tempoSegundos % 60));
        lblTempo.setFont(FONTE_NUMERO);
        lblTempo.setForeground(corTextoPrincipal);
        lblTempo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel painelStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelStatus.setBackground(corCard);
        painelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconeStatus = new JLabel(venceu ? "✅" : "💥");
        iconeStatus.setFont(FONTE_CELULA);
        iconeStatus.setForeground(corTextoSecundario);
        iconeStatus.setPreferredSize(new Dimension(30, 30));
        iconeStatus.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textoStatus = new JLabel(venceu ? "Venceu" : "Perdeu");
        textoStatus.setFont(FONTE_PEQUENA);
        textoStatus.setForeground(corTextoSecundario);

        painelStatus.add(iconeStatus);
        painelStatus.add(textoStatus);

        cartao.add(lblNome);
        cartao.add(Box.createVerticalStrut(8));
        cartao.add(lblTempo);
        cartao.add(Box.createVerticalStrut(4));
        cartao.add(painelStatus);
        return cartao;
    }


    private JPanel criarSeletorTemaCompacto(Runnable aoTrocarTema) { 
        JPanel painelSeletor = criarLinhaSelecao("Tema:", TEMAS_CYBERPUNK);
        painelSeletor.setMaximumSize(new Dimension(160, 32)); comboModo = (JComboBox<String>)
        painelSeletor.getClientProperty("combo"); comboModo.setSelectedItem(temaAtual);
        comboModo.addActionListener(e -> { 
            aplicarTemaSelecionado(); 
            aoTrocarTema.run(); 
            });
        return painelSeletor; 
        }


    private JPanel criarLinhaSelecao(String texto, String[] opcoes) {
        JPanel painel = new JPanel(new BorderLayout(10, 0));
        painel.setBackground(corFundo);
        painel.setMaximumSize(new Dimension(320, 40));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_PEQUENA);
        lbl.setForeground(corTextoSecundario);
        painel.add(lbl, BorderLayout.WEST);

        JComboBox<String> combo = new JComboBox<>(opcoes);
        combo.setFont(FONTE_PEQUENA);
        combo.setBackground(corFundoClaro);
        combo.setForeground(corTextoPrincipal);
        combo.setBorder(BorderFactory.createLineBorder(corBorda));
        painel.add(combo, BorderLayout.EAST);
        painel.putClientProperty("combo", combo);

        return painel;
    }

    private void mostrarTutorial() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Instruções M.I.N.E.S.");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(corTextoPrincipal);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        String texto = "1. INÍCIO: Escolha a dificuldade e o tempo de conexão.\n"
                + "2. ESQUERDO: Revela um nó. O número mostra quantas ameaças o cercam.\n"
                + "3. DIREITO: Use para colocar uma trava (bandeira) no perigo.\n"
                + "4. VITÓRIA: Revele todos os nós seguros para limpar a Matrix.\n"
                + "5. COLAPSO: Clicar em uma ameaça causa falha crítica (Derrota).\n"
                + "6. TEMPO: Se o relógio chegar a zero, sua conexão é encerrada.\n";

        JTextArea area = new JTextArea(texto);
        area.setFont(FONTE_NORMAL);
        area.setForeground(corTextoPrincipal);
        area.setBackground(corFundoClaro);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.add(area);
        painel.add(Box.createVerticalStrut(15));

        JLabel dicas = new JLabel("Dica: A lógica é sua arma. Use os números para deduzir onde estão as ameaças.");
        dicas.setFont(FONTE_PEQUENA);
        dicas.setForeground(corTextoSecundario);
        dicas.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(dicas);
        painel.add(Box.createVerticalStrut(25));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(corTextoPrincipal);
        voltar.setBackground(corFundoClaro);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public int getTempoLimiteSegundosSelecionado() {
        if (comboTempo == null) {
            return 0;
        }
        String selecionado = (String) comboTempo.getSelectedItem();
        if (selecionado == null || selecionado.startsWith("Sem")) {
            return 0;
        }
        if (selecionado.contains("1 minuto")) {
            return 60;
        }
        if (selecionado.contains("2 minutos")) {
            return 120;
        }
        if (selecionado.contains("3 minutos")) {
            return 180;
        }
        if (selecionado.contains("5 minutos")) {
            return 300;
        }
        return 0;
    }

        public void aplicarTemaSelecionado() {
            if (comboModo != null) {
                temaAtual = (String) comboModo.getSelectedItem();
            }
            if ("Claro".equals(temaAtual)) {
                corFundo = new Color(250, 248, 255);
                corFundoClaro = new Color(235, 222, 245);
                corTextoPrincipal = new Color(30, 10, 45);
                corTextoSecundario = new Color(120, 90, 150);
                corCard = new Color(255, 255, 255);
                corCardHover = new Color(248, 220, 240);
                corDestaque = new Color(219, 10, 130);
                corBorda = new Color(0, 150, 150);

                corCelulaOculta = new Color(225, 210, 240);
                corCelulaOcultaHover = new Color(210, 190, 230);
                corBordaOculta = new Color(0, 150, 150);
                corCelulaRevelada = new Color(240, 235, 250);
                corBordaRevelada = new Color(219, 10, 130);
                corTextoSobreRevelada = new Color(40, 10, 60);
                corMinaFundo = new Color(225, 20, 90);
            } else {
                corFundo = new Color(18, 8, 28);
                corFundoClaro = new Color(35, 15, 50);
                corTextoPrincipal = new Color(230, 245, 255);
                corTextoSecundario = new Color(160, 120, 200);
                corCard = new Color(30, 12, 45);
                corCardHover = new Color(55, 20, 80);
                corDestaque = new Color(255, 20, 147);
                corBorda = new Color(0, 220, 220);

                corCelulaOculta = new Color(35, 15, 55);
                corCelulaOcultaHover = new Color(60, 25, 90);
                corBordaOculta = new Color(0, 200, 200);
                corCelulaRevelada = new Color(20, 10, 35);
                corBordaRevelada = new Color(255, 20, 147);
                corTextoSobreRevelada = new Color(0, 255, 255);
                corMinaFundo = new Color(255, 0, 90);
            }
            getContentPane().setBackground(corFundo);
    }

    private JPanel criarCardDificuldade(String titulo, String dimensao, String minasTexto,
                                         int linhas, int colunas, int minas) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(corCard);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(corDestaque);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);

        JLabel lblDim = new JLabel(dimensao);
        lblDim.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDim.setForeground(corTextoPrincipal);
        lblDim.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDim.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        card.add(lblDim);

        JPanel painelMinas = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelMinas.setOpaque(false);
        painelMinas.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconeMinas = new JLabel(EMOJI_BOMBA);
        iconeMinas.setFont(FONTE_CELULA);
        iconeMinas.setForeground(corTextoSecundario);
        iconeMinas.setPreferredSize(new Dimension(30, 30));
        iconeMinas.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textoMinas = new JLabel(minasTexto);
        textoMinas.setFont(FONTE_NORMAL);
        textoMinas.setForeground(corTextoSecundario);

        painelMinas.add(iconeMinas);
        painelMinas.add(textoMinas);
        card.add(painelMinas);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(corCardHover);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(corDestaque, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(corCard);
                    card.setBackground(corCard);
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(corBorda, 1),
                            BorderFactory.createEmptyBorder(8, 12, 8, 12)
                    ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (ouvinte != null) {
                    ouvinte.aoEscolherDificuldade(linhas, colunas, minas);
                }
            }
        });

        Dimension tamanhoCard = new Dimension(190, 120);
            card.setPreferredSize(tamanhoCard);
            card.setMinimumSize(tamanhoCard);
            card.setMaximumSize(tamanhoCard);

        return card;
    }

    // ================================================================
    // TELA DE JOGO
    // ================================================================

    /**
     * Monta a tela de jogo do zero para um tabuleiro de {@code linhas} x
     * {@code colunas}. Não recebe o {@link Tabuleiro}, apenas as
     * dimensões — quem decide o que cada célula mostra depois é sempre
     * o Controller, chamando {@link #atualizarCelula}.
     */
    public void iniciarTelaDeJogo(int linhas, int colunas, int totalMinas, int totalCelulas, int tempoLimiteSegundos) {
        getContentPane().removeAll();
        setLayout(new BorderLayout(0, 0));

        add(criarPainelSuperior(), BorderLayout.NORTH);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 0));
        painelPrincipal.setBackground(corFundo);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        painelPrincipal.add(criarPainelTabuleiro(linhas, colunas), BorderLayout.CENTER);
        painelPrincipal.add(criarPainelEstatisticas(totalMinas, totalCelulas), BorderLayout.EAST);

        add(painelPrincipal, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarPainelSuperior() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JButton btnNovo = new JButton("← Menu");
        btnNovo.setFont(FONTE_NORMAL);
        btnNovo.setForeground(corTextoPrincipal);
        btnNovo.setBackground(corFundoClaro);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovo.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirNovoJogo();
            }
        });
        btnNovo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnNovo.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnNovo.setBackground(corFundoClaro);
            }
        });

        labelStatus = new JLabel("Bom Jogo!", SwingConstants.CENTER);
        labelStatus.setFont(FONTE_SUBTITULO);
        labelStatus.setForeground(corTextoSecundario);

        JButton btnRepetir = new JButton("<html><font face='Segoe UI Emoji'>🔁</font> Repetir</html>");
        btnRepetir.setFont(FONTE_NORMAL);
        btnRepetir.setForeground(corTextoPrincipal);
        btnRepetir.setBackground(corFundoClaro);
        btnRepetir.setFocusPainted(false);
        btnRepetir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnRepetir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRepetir.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoRepetirPartida();
            }
        });
        btnRepetir.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRepetir.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRepetir.setBackground(corFundoClaro);
            }
        });

        JPanel painelBotoesEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBotoesEsquerda.setBackground(corFundo);
        painelBotoesEsquerda.add(btnNovo);
        painelBotoesEsquerda.add(btnRepetir);

        painel.add(painelBotoesEsquerda, BorderLayout.WEST);
        painel.add(labelStatus, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("<html><font face='Segoe UI Emoji'>💾</font> Salvar</html>");
        btnSalvar.setFont(FONTE_NORMAL);
        btnSalvar.setForeground(corTextoPrincipal);
        btnSalvar.setBackground(corFundoClaro);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirSalvarPartida();
            }
        });
        btnSalvar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnSalvar.setBackground(corCardHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnSalvar.setBackground(corFundoClaro);
            }
        });
        painelBotoesEsquerda.add(btnSalvar);

        JButton btnDica = new JButton("<html><font face='Segoe UI Emoji'>💡</font> Dica (+40s)</html>");
        btnDica.setFont(FONTE_NORMAL);
        btnDica.setForeground(corTextoPrincipal);
        btnDica.setBackground(corFundoClaro);
        btnDica.setFocusPainted(false);
        btnDica.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnDica.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDica.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirDica();
            }
        });
        btnDica.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnDica.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnDica.setBackground(corFundoClaro);
            }
        });

        btnResolver = new JButton("<html><font face='Segoe UI Emoji'>🤖</font> Resolver</html>");
        btnResolver.setFont(FONTE_NORMAL);
        btnResolver.setForeground(corTextoPrincipal);
        btnResolver.setBackground(corFundoClaro);
        btnResolver.setFocusPainted(false);
        btnResolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnResolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResolver.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirResolverAutomatico();
            }
        });
        btnResolver.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnResolver.setBackground(corCardHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnResolver.setBackground(corFundoClaro);
            }
        });

        JPanel painelBotoesDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        painelBotoesDireita.setBackground(corFundo);
        painelBotoesDireita.add(btnDica);
        painelBotoesDireita.add(btnResolver);

        painel.add(painelBotoesDireita, BorderLayout.EAST);

        JButton btnReplay = new JButton("<html><font face='Segoe UI Emoji'>▶</font> Replay</html>");
        btnReplay.setFont(FONTE_NORMAL);
        btnReplay.setForeground(corTextoPrincipal);
        btnReplay.setBackground(corFundoClaro);
        btnReplay.setFocusPainted(false);
        btnReplay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnReplay.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReplay.addActionListener(e -> {
            if (ouvinte != null) {
                ouvinte.aoPedirReplay();
            }
        });
        btnReplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnReplay.setBackground(corCardHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnReplay.setBackground(corFundoClaro);
            }
        });
        painelBotoesEsquerda.add(btnReplay);

        JButton btnSom = new JButton("<html><font face='Segoe UI Emoji'>🔊</font> Som</html>");
        btnSom.setFont(FONTE_NORMAL);
        btnSom.setForeground(corTextoPrincipal);
        btnSom.setBackground(corFundoClaro);
        btnSom.setFocusPainted(false);
        btnSom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btnSom.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSom.addActionListener(e -> {
            som.alternarAtivado();
            btnSom.setText(som.isAtivado()
                ? "<html><font face='Segoe UI Emoji'>🔊</font> Som</html>"
                : "<html><font face='Segoe UI Emoji'>🔇</font> Mudo</html>");
        });
        painelBotoesEsquerda.add(btnSom);

        return painel;
    }

    private JPanel criarPainelEstatisticas(int totalMinas, int totalCelulas) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundoClaro);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        int largura = 180 + Math.min(100, totalMinas * 2);
        painel.setPreferredSize(new Dimension(largura, 0));

        JLabel lblTitulo = new JLabel("Monitoramento");
        lblTitulo.setFont(FONTE_SUBTITULO);
        lblTitulo.setForeground(COR_DESTAQUE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTitulo);
        painel.add(Box.createVerticalStrut(20));

       JPanel pnlTempo = criarItemEstatistica("<html><font face='Segoe UI Emoji'>" + EMOJI_RELOGIO + "</font> Tempo de Conexão</html>", "00:00");
        lblTempo = (JLabel) pnlTempo.getClientProperty("valor");
        painel.add(pnlTempo);
        painel.add(Box.createVerticalStrut(15));

        JPanel pnlMinas = criarItemEstatistica("<html><font face='Segoe UI Emoji'>" + EMOJI_BOMBA + "</font> Minas</html>", String.valueOf(totalMinas));
        lblMinasRestantes = (JLabel) pnlMinas.getClientProperty("valor");
        painel.add(pnlMinas);
        painel.add(Box.createVerticalStrut(15));
        JPanel pnlReveladas = criarItemEstatistica("<html><font face='Segoe UI Emoji'>" + EMOJI_BANDEIRA + "</font> Ameaças Mapeadas</html>", "0 / " + totalCelulas);
        lblCelulasReveladas = (JLabel) pnlReveladas.getClientProperty("valor");
        painel.add(pnlReveladas);
        painel.add(Box.createVerticalStrut(15));


        JPanel pnlJogadas = criarItemEstatistica("<html><font face='Segoe UI Emoji'>" + EMOJI_JOGADA + "</font> Jogadas</html>", "0");
        lblJogadas = (JLabel) pnlJogadas.getClientProperty("valor");
        painel.add(pnlJogadas);
        painel.add(Box.createVerticalStrut(20));

        JLabel lblProgTitulo = new JLabel("Domínio do Mapa");
        lblProgTitulo.setFont(FONTE_NORMAL);
        lblProgTitulo.setForeground(COR_TEXTO_SECUNDARIO);
        lblProgTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblProgTitulo);

        barraProgresso = new JProgressBar(0, Math.max(totalCelulas, 1));
        barraProgresso.setValue(0);
        barraProgresso.setStringPainted(true);
        barraProgresso.setString("0%");
        barraProgresso.setForeground(corDestaque);
        barraProgresso.setBackground(corFundo);
        barraProgresso.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        barraProgresso.setPreferredSize(new Dimension(150, 20));
        barraProgresso.setMaximumSize(new Dimension(150, 20));
        barraProgresso.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(barraProgresso);
        painel.add(Box.createVerticalStrut(15));

        painelJogadorDuelo = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        painelJogadorDuelo.setBackground(corFundoClaro);
        painelJogadorDuelo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelJogadorDuelo.setVisible(false);

        JLabel iconeJogadorDuelo = new JLabel("🎮");
        iconeJogadorDuelo.setFont(FONTE_CELULA);
        iconeJogadorDuelo.setForeground(corDestaque);
        iconeJogadorDuelo.setPreferredSize(new Dimension(30, 30));
        iconeJogadorDuelo.setHorizontalAlignment(SwingConstants.CENTER);

        lblJogadorDuelo = new JLabel("");
        lblJogadorDuelo.setFont(FONTE_SUBTITULO);
        lblJogadorDuelo.setForeground(corDestaque);

        painelJogadorDuelo.add(iconeJogadorDuelo);
        painelJogadorDuelo.add(lblJogadorDuelo);
        painel.add(painelJogadorDuelo);
        painel.add(Box.createVerticalStrut(10));

        painel.add(Box.createVerticalGlue());

        JLabel lblDica = new JLabel("<html><center>\uD83D\uDDB1\uFE0F Esquerdo: revelar<br>\uD83D\uDDB1\uFE0F Direito: bandeira</center></html>");
        lblDica.setFont(FONTE_PEQUENA);
        lblDica.setForeground(corTextoSecundario);
        lblDica.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDica);

                return painel;
    }

    public void definirJogadorAtualDuelo(String nome) {
        if (painelJogadorDuelo == null) {
            return;
        }
        if (nome == null) {
            painelJogadorDuelo.setVisible(false);
        } else {
            lblJogadorDuelo.setText(nome);
            painelJogadorDuelo.setVisible(true);
        }
    }



    /**
     * Cria um item de estatística (título + valor) como um único painel,
     * guardando a referência ao label de valor via putClientProperty para
     * que possa ser atualizado depois. (Antes o valor era retornado
     * "solto", sem o painel-pai ser adicionado à tela — corrigido aqui.)
     */
    private JPanel criarItemEstatistica(String titulo, String valorInicial) {
        JPanel painelItem = new JPanel();
        painelItem.setLayout(new BoxLayout(painelItem, BoxLayout.Y_AXIS));
        painelItem.setBackground(corFundoClaro);
        painelItem.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(FONTE_PEQUENA);
        lblTitulo.setForeground(corTextoSecundario);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValor = new JLabel(valorInicial);
        lblValor.setFont(FONTE_NUMERO);
        lblValor.setForeground(corTextoPrincipal);
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelItem.add(lblTitulo);
        painelItem.add(lblValor);
        painelItem.putClientProperty("valor", lblValor);

        return painelItem;
    }

    private JPanel criarPainelTabuleiro(int linhas, int colunas) {
        JPanel grade = new JPanel(new GridLayout(linhas, colunas, 2, 2));
        grade.setBackground(corFundo);

        int tamanhoCelula = (linhas == 9 && colunas == 9) ? 50 : 36;

        botoes = new JButton[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                JButton botao = criarBotaoCelula(i, j, tamanhoCelula);
                botoes[i][j] = botao;
                grade.add(botao);
            }
        }
        return grade;
    }

    /**
     * Cria o botão de uma célula. AQUI ESTAVA O BUG DA BANDEIRA: o código
     * original detectava o clique direito em mousePressed. Em trackpads
     * (Mac, e alguns drivers de notebook Windows/Linux) o clique direito
     * simulado por toque com dois dedos nem sempre reporta corretamente
     * qual botão foi pressionado no evento de "pressed" — só fica
     * confiável no evento de "released". Por isso o primeiro clique
     * direito costumava funcionar e os seguintes eram ignorados ou
     * tratados como clique esquerdo. A correção é ouvir mouseReleased.
     */
    private JButton criarBotaoCelula(int linha, int coluna, int tamanhoCelula) {
        JButton botao = new JButton();
        botao.setPreferredSize(new Dimension(tamanhoCelula, tamanhoCelula));
        botao.setFont(FONTE_CELULA);
        botao.setFocusPainted(false);
        botao.setBackground(corCelulaOculta);
        botao.setForeground(corTextoPrincipal);
        botao.setMargin(new Insets(0, 0, 0, 0));
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.putClientProperty("revelada", Boolean.FALSE);

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Só aplica o realce de "hover" em células ainda ocultas;
                // caso contrário isso sobrescreveria a cor clara da
                // célula já revelada sempre que o mouse passasse por cima.
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOcultaHover);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.FALSE.equals(botao.getClientProperty("revelada"))) {
                    botao.setBackground(corCelulaOculta);
                }
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                if (ouvinte == null) {
                    return;
                }
                // e.getButton() é checado explicitamente além de
                // SwingUtilities.isRightMouseButton para cobrir cliques
                // direitos simulados por trackpad de forma confiável.
                boolean botaoDireito = SwingUtilities.isRightMouseButton(evento)
                        || evento.getButton() == MouseEvent.BUTTON3;
                if (botaoDireito) {
                    ouvinte.aoMarcarCelula(linha, coluna);
                } else if (SwingUtilities.isLeftMouseButton(evento)) {
                    ouvinte.aoRevelarCelula(linha, coluna);
                }
            }
        });
        return botao;
    }

    // ================================================================
    // ATUALIZAÇÕES CHAMADAS PELO CONTROLLER
    // ================================================================

    /** Redesenha uma célula com base no estado atual do tabuleiro. */
    public void atualizarCelula(int linha, int coluna, LeituraTabuleiro leitura) {
        JButton botao = botoes[linha][coluna];
        botao.putClientProperty("revelada", leitura.isRevelada(linha, coluna));

        if (leitura.isMarcada(linha, coluna)) {
            botao.setText(EMOJI_BANDEIRA);
            botao.setForeground(COR_BANDEIRA);
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_BANDEIRA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (!leitura.isRevelada(linha, coluna)) {
            botao.setText("");
            botao.setBackground(corCelulaOculta);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaOculta, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            return;
        }

        if (leitura.isMinada(linha, coluna)) {
            botao.setText(EMOJI_BOMBA);
            botao.setBackground(corMinaFundo);
            botao.setForeground(COR_MINA);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COR_MINA, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        } else {
            // Célula revelada e segura: fundo claro e "afundado",
            // nitidamente diferente do fundo escuro da célula oculta.
            botao.setBackground(corCelulaRevelada);
            botao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBordaRevelada, 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
            int vizinhas = leitura.getMinasVizinhas(linha, coluna);
            if (vizinhas == 0) {
                botao.setText("");
                botao.setForeground(corTextoSobreRevelada);
            } else {
                botao.setText(String.valueOf(vizinhas));
                botao.setForeground(CORES_NUMEROS[vizinhas]);
            }
        }
    }

    public void atualizarTempo(String texto) {
        if (lblTempo != null) {
            lblTempo.setText(texto);
        }
    }

    public void atualizarEstatisticas(int minasRestantes, int celulasReveladas, int totalCelulas, int jogadas) {
        if (lblMinasRestantes != null) {
            lblMinasRestantes.setText(String.valueOf(minasRestantes));
        }
        if (lblCelulasReveladas != null) {
            lblCelulasReveladas.setText(celulasReveladas + " / " + totalCelulas);
        }
        if (lblJogadas != null) {
            lblJogadas.setText(String.valueOf(jogadas));
        }

        int progresso = totalCelulas > 0 ? (int) ((celulasReveladas * 100.0) / totalCelulas) : 0;
        if (barraProgresso != null) {
            barraProgresso.setValue(celulasReveladas);
            barraProgresso.setString(progresso + "%");
            if (progresso < 30) {
                barraProgresso.setForeground(new Color(220, 80, 80));
            } else if (progresso < 70) {
                barraProgresso.setForeground(new Color(220, 180, 60));
            } else {
                barraProgresso.setForeground(COR_VITORIA);
            }
        }
    }

    public void mostrarDerrota() {
        labelStatus.setText("<html><font face='Segoe UI Emoji'>" + EMOJI_EXPLOSAO + "</font> Derrota!</html>");
        labelStatus.setForeground(COR_MINA);
    }

    public void mostrarVitoria() {
        labelStatus.setText("<html><font face='Segoe UI Emoji'>" + EMOJI_TROFEU + "</font> Vitória!</html>");
        labelStatus.setForeground(COR_VITORIA);
    }
    

    public void celebrarVitoria() {
        Color[] coresConfete = { corDestaque, corBorda, corTextoPrincipal, corCardHover };
        painelConfete.setVisible(true);
        painelConfete.iniciar(getWidth(), getHeight(), coresConfete);
    }

    public void piscarFundoDeExplosao(boolean explodindo) {
        getContentPane().setBackground(explodindo ? COR_MINA_FUNDO : COR_FUNDO);
    }

    public void chacoalharJanela() {
    Point posicaoOriginal = getLocation();
    int[] deslocamentos = {12, -12, 9, -9, 6, -6, 3, -3, 0};
    int[] indice = {0};

    Timer timer = new Timer(30, null);
    timer.addActionListener(e -> {
        if (indice[0] >= deslocamentos.length) {
            timer.stop();
            setLocation(posicaoOriginal);
            return;
        }
        setLocation(posicaoOriginal.x + deslocamentos[indice[0]], posicaoOriginal.y);
        indice[0]++;
    });
    timer.start();
}

    public void marcarMinaExplodida(int linha, int coluna) {
        JButton botao = botoes[linha][coluna];
        botao.setText(EMOJI_BOMBA);
        botao.setForeground(COR_MINA);
        botao.setBackground(COR_MINA_FUNDO);
        botao.setBorder(BorderFactory.createLineBorder(COR_MINA, 1));
    }

    public void destacarCelulaVencedora(int linha, int coluna) {
        botoes[linha][coluna].setBackground(new Color(40, 100, 60));
    }

    public void mostrarMensagemStatus(String texto) {
    labelStatus.setText(texto);
    }

    public int perguntarContinuarPartidaSalva() {
        return JOptionPane.showConfirmDialog(this,
                "Você tem uma partida salva. Deseja continuar de onde parou?",
                "Partida salva encontrada", JOptionPane.YES_NO_OPTION);
    }


    public void mostrarTelaRanking(List<RegistroRanking> iniciante, List<RegistroRanking> intermediario,
        List<RegistroRanking> avancado) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        painelTitulo.setBackground(corFundo);
        painelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconeTitulo = new JLabel("🏆");
        iconeTitulo.setFont(FONTE_EMOJI_TITULO);
        iconeTitulo.setForeground(corTextoPrincipal);
        iconeTitulo.setPreferredSize(new Dimension(40, 40));
        iconeTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textoTitulo = new JLabel("Ranking");
        textoTitulo.setFont(FONTE_TITULO);
        textoTitulo.setForeground(corTextoPrincipal);

        painelTitulo.add(iconeTitulo);
        painelTitulo.add(textoTitulo);
        painel.add(painelTitulo);
        painel.add(Box.createVerticalStrut(20));

        JPanel painelColunas = new JPanel(new GridLayout(1, 3, 20, 0));
        painelColunas.setBackground(corFundo);
        painelColunas.add(criarColunaRanking("EASY", iniciante));
        painelColunas.add(criarColunaRanking("MEDIUM", intermediario));
        painelColunas.add(criarColunaRanking("HARD", avancado));
        painel.add(painelColunas);
        painel.add(Box.createVerticalStrut(20));

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(corTextoPrincipal);
        voltar.setBackground(corFundoClaro);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        voltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(voltar);

        add(painel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    private JPanel criarColunaRanking(String tituloDificuldade, List<RegistroRanking> registros) {
        JPanel coluna = new JPanel();
        coluna.setLayout(new BoxLayout(coluna, BoxLayout.Y_AXIS));
        coluna.setBackground(corCard);
        coluna.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel nomeDificuldade = new JLabel(tituloDificuldade);
        nomeDificuldade.setFont(FONTE_SUBTITULO);
        nomeDificuldade.setForeground(corDestaque);
        nomeDificuldade.setAlignmentX(Component.CENTER_ALIGNMENT);
        coluna.add(nomeDificuldade);
        coluna.add(Box.createVerticalStrut(10));

        if (registros.isEmpty()) {
            JLabel vazio = new JLabel("Sem registros ainda");
            vazio.setFont(FONTE_PEQUENA);
            vazio.setForeground(corTextoSecundario);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            coluna.add(vazio);
        } else {
            int posicao = 1;
            for (RegistroRanking registro : registros) {
                JLabel linha = new JLabel(posicao + ". " + registro.getNomeJogador() + " — " + registro.getTempoFormatado());
                linha.setFont(FONTE_NORMAL);
                linha.setForeground(corTextoPrincipal);
                linha.setAlignmentX(Component.CENTER_ALIGNMENT);
                coluna.add(linha);
                coluna.add(Box.createVerticalStrut(4));
                posicao++;
            }
        }

        return coluna;
    }


    public void mostrarTelaLogin(List<String> perfisExistentes) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        JLabel titulo = new JLabel("M.I.N.E.S.");
        titulo.setFont(FONTE_TITULO);
        titulo.setForeground(corTextoPrincipal);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(10));

        JLabel subtitulo = new JLabel("Escolha ou crie seu perfil");
        subtitulo.setFont(FONTE_PEQUENA);
        subtitulo.setForeground(corTextoSecundario);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(subtitulo);
        painel.add(Box.createVerticalStrut(20));

        JComboBox<String> comboPerfil = new JComboBox<>(perfisExistentes.toArray(new String[0]));
        comboPerfil.setEditable(true);
        comboPerfil.setFont(FONTE_NORMAL);
        comboPerfil.setMaximumSize(new Dimension(260, 36));
        comboPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(comboPerfil);
        painel.add(Box.createVerticalStrut(15));

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(FONTE_NORMAL);
        btnEntrar.setForeground(corTextoPrincipal);
        btnEntrar.setBackground(corFundoClaro);
        btnEntrar.setFocusPainted(false);
        btnEntrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.addActionListener(e -> {
            if (ouvinte != null) {
                Object selecionado = comboPerfil.getEditor().getItem();
                ouvinte.aoFazerLogin(selecionado == null ? null : selecionado.toString());
            }
        });
        painel.add(btnEntrar);

        add(painel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public void mostrarConquistasDesbloqueadas(List<String> nomes) {
        StringBuilder texto = new StringBuilder("🏅 Nova conquista desbloqueada!\n\n");
        for (String nome : nomes) {
            texto.append("• ").append(nome).append("\n");
        }
        JOptionPane.showMessageDialog(this, texto.toString(), "Conquista!", JOptionPane.PLAIN_MESSAGE);
    }

    public void mostrarTelaConquistas(List<Conquista> todas, java.util.Set<String> desbloqueadas) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(corFundo);
        painel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        painelTitulo.setBackground(corFundo);
        painelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconeTitulo = new JLabel("🏅");
        iconeTitulo.setFont(FONTE_EMOJI_TITULO);
        iconeTitulo.setForeground(corTextoPrincipal);
        iconeTitulo.setPreferredSize(new Dimension(40, 40));
        iconeTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel textoTitulo = new JLabel("Conquistas");
        textoTitulo.setFont(FONTE_TITULO);
        textoTitulo.setForeground(corTextoPrincipal);

        painelTitulo.add(iconeTitulo);
        painelTitulo.add(textoTitulo);
        painel.add(painelTitulo);
        painel.add(Box.createVerticalStrut(20));

        for (Conquista conquista : todas) {
            boolean obtida = desbloqueadas.contains(conquista.getId());
            JPanel linha = new JPanel();
            linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
            linha.setBackground(corCard);
            linha.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(corBorda),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
            linha.setMaximumSize(new Dimension(500, 70));
            linha.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nome = new JLabel("<html><font face='Segoe UI Emoji'>" + (obtida ? "✅" : "🔒") + "</font> " + conquista.getNome() + "</html>");
            nome.setFont(FONTE_SUBTITULO);
            nome.setForeground(obtida ? corDestaque : corTextoSecundario);

            JLabel descricao = new JLabel(conquista.getDescricao());
            descricao.setFont(FONTE_PEQUENA);
            descricao.setForeground(corTextoSecundario);

            linha.add(nome);
            linha.add(descricao);
            painel.add(linha);
            painel.add(Box.createVerticalStrut(10));
        }

        JButton voltar = new JButton("Voltar");
        voltar.setFont(FONTE_NORMAL);
        voltar.setForeground(corTextoPrincipal);
        voltar.setBackground(corFundoClaro);
        voltar.setFocusPainted(false);
        voltar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(corBorda),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        voltar.addActionListener(e -> mostrarTelaInicial());
        voltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(Box.createVerticalStrut(10));
        painel.add(voltar);

        JScrollPane scroll = new JScrollPane(painel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(corFundo);
        add(scroll, BorderLayout.CENTER);

        setSize(600, 650);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }

    public void mostrarTelaHistorico(Perfil perfil) {
    getContentPane().removeAll();
    setLayout(new BorderLayout());

    JPanel painel = new JPanel();
    painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
    painel.setBackground(corFundo);
    painel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

    JPanel painelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
    painelTitulo.setBackground(corFundo);
    painelTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel iconeTitulo = new JLabel("📊");
    iconeTitulo.setFont(FONTE_EMOJI_TITULO);
    iconeTitulo.setForeground(corTextoPrincipal);
    iconeTitulo.setPreferredSize(new Dimension(40, 40));
iconeTitulo.setHorizontalAlignment(SwingConstants.CENTER);

    JLabel textoTitulo = new JLabel("Histórico — " + perfil.getNome());
    textoTitulo.setFont(FONTE_TITULO);
    textoTitulo.setForeground(corTextoPrincipal);

    painelTitulo.add(iconeTitulo);
    painelTitulo.add(textoTitulo);
    painel.add(painelTitulo);
    painel.add(Box.createVerticalStrut(25));

    painel.add(criarLinhaHistorico("Vitórias", String.valueOf(perfil.getVitorias())));
    painel.add(criarLinhaHistorico("Derrotas", String.valueOf(perfil.getDerrotas())));
    painel.add(criarLinhaHistorico("Taxa de acerto", String.format("%.1f%%", perfil.getTaxaAcerto())));
    painel.add(Box.createVerticalStrut(15));
    painel.add(criarLinhaHistorico("Melhor tempo — Easy",
            perfil.getMelhorTempoEasy() > 0 ? formatarTempo(perfil.getMelhorTempoEasy()) : "—"));
    painel.add(criarLinhaHistorico("Melhor tempo — Medium",
            perfil.getMelhorTempoMedium() > 0 ? formatarTempo(perfil.getMelhorTempoMedium()) : "—"));
    painel.add(criarLinhaHistorico("Melhor tempo — Hard",
            perfil.getMelhorTempoHard() > 0 ? formatarTempo(perfil.getMelhorTempoHard()) : "—"));

    painel.add(Box.createVerticalStrut(25));

    JButton voltar = new JButton("Voltar");
    voltar.setFont(FONTE_NORMAL);
    voltar.setForeground(corTextoPrincipal);
    voltar.setBackground(corFundoClaro);
    voltar.setFocusPainted(false);
    voltar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corBorda),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
    ));
    voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
    voltar.addActionListener(e -> mostrarTelaInicial());
    voltar.setAlignmentX(Component.CENTER_ALIGNMENT);
    painel.add(voltar);

    add(painel, BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(null);
    revalidate();
    repaint();
}

    private JPanel criarLinhaHistorico(String rotulo, String valor) {
        JPanel linha = new JPanel(new BorderLayout());
        linha.setBackground(corFundo);
        linha.setMaximumSize(new Dimension(320, 30));
        linha.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(FONTE_NORMAL);
        lbl.setForeground(corTextoSecundario);

        JLabel val = new JLabel(valor);
        val.setFont(FONTE_NORMAL);
        val.setForeground(corTextoPrincipal);

        linha.add(lbl, BorderLayout.WEST);
        linha.add(val, BorderLayout.EAST);
        return linha;
    }

    private String formatarTempo(int segundos) {
        return String.format("%02d:%02d", segundos / 60, segundos % 60);
    }

    public void definirResolverDisponivel(boolean disponivel) {
        if (btnResolver != null) {
            btnResolver.setVisible(disponivel);
        }
    }

}
