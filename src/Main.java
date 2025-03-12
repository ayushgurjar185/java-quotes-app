import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static List<String> quotes;
    private static JTextArea quoteArea;
    
    public static void main(String[] args) throws IOException {
        // Check if running in Docker (headless mode, no GUI)
        if (System.getenv("DOCKERIZED") != null) {
            runServerOnly();
            return;
        }
        
        JFrame frame = new JFrame("Quote Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        quoteArea = new JTextArea(4, 30);
        quoteArea.setLineWrap(true);
        quoteArea.setWrapStyleWord(true);
        quoteArea.setEditable(false);
        
        JScrollPane scrollPane = new JScrollPane(quoteArea);
        panel.add(scrollPane, gbc);
        
        JButton button = new JButton("New Quote >>");
        gbc.gridy = 1;
        panel.add(button, gbc);
        
        quotes = loadQuotesFromFile("quotes.txt");
        
        if (quotes.isEmpty()) {
            System.err.println("No quotes found in the file. Please ensure 'quotes.txt' has content.");
            return;
        }
        
        quoteArea.setText(getRandomQuote());
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quoteArea.setText(getRandomQuote());
            }
        });
        
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        runServerOnly();
    }
    
    private static void runServerOnly() throws IOException {
        quotes = loadQuotesFromFile("quotes.txt");
        if (quotes.isEmpty()) {
            System.err.println("No quotes found in the file. Please ensure 'quotes.txt' has content.");
            return;
        }
        
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", exchange -> {
            String quote = getRandomQuote();
            String jsonResponse = String.format("{\"quote\": \"%s\"}", quote);
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });
        
        server.start();
        System.out.println("Server is running on port 8000...");
    }
    
    private static String getRandomQuote() {
        Random random = new Random();
        return quotes.get(random.nextInt(quotes.size()));
    }
    
    private static List<String> loadQuotesFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading quotes file: " + e.getMessage());
            return List.of();
        }
    }
}
