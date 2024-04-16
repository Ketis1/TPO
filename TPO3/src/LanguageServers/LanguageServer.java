package LanguageServers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public abstract class LanguageServer {
    private ServerSocket serverSocket;
    private Map<String,String> translations;
    private int port;
    private String languageCode;



    private void loadTranslations(){
        translations = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("en.properties"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Sprawdzamy, czy linia nie jest pusta i czy zawiera znak równości
                if (!line.trim().isEmpty() && line.contains("=")) {
                    // Dzielimy linię na klucz i wartość
                    String[] parts = line.split("=", 2);
                    // Dodajemy do mapy
                    translations.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String translate(String key) {
        return translations.get(key);
    }

}
