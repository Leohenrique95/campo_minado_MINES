package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Tenta descobrir se o Windows está configurado no modo claro ou escuro,
 * lendo a chave de registro correspondente. Se qualquer coisa der errado
 * (sistema operacional diferente, comando indisponível, etc.), cai no
 * padrão "Escuro" sem lançar exceção pro resto do programa.
 */
public class DetectorTemaSistema {

    public static String detectarTemaPreferido() {
        try {
            Process processo = Runtime.getRuntime().exec(new String[] {
                    "reg", "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme"
            });

            try (BufferedReader leitor = new BufferedReader(
                    new InputStreamReader(processo.getInputStream()))) {
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    if (linha.contains("AppsUseLightTheme")) {
                        boolean claro = linha.trim().endsWith("0x1");
                        return claro ? "Claro" : "Escuro";
                    }
                }
            }
        } catch (Exception e) {
            // SO diferente do Windows, comando indisponível, etc. — ignora e usa o padrão.
        }
        return "Escuro";
    }
}