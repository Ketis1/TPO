import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientGui extends JFrame {
    private JTextField wordField;
    private JTextField languageField;
    private JButton translateButton;
    private JTextArea responseArea;

    private TranslatorClient translatorClient;

    public ClientGui() {
        setTitle("Translator Client");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Word to translate:"));
        wordField = new JTextField();
        inputPanel.add(wordField);
        inputPanel.add(new JLabel("Language code:"));
        languageField = new JTextField();
        inputPanel.add(languageField);
        translateButton = new JButton("Translate");
        translateButton.addActionListener(new TranslateButtonListener());
        inputPanel.add(translateButton);

        responseArea = new JTextArea();
        responseArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(responseArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        translatorClient = new TranslatorClient();
    }

    private class TranslateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String wordToTranslate = wordField.getText().trim();
            String languageCode = languageField.getText().trim().toLowerCase();

            String response = translatorClient.translate(wordToTranslate, languageCode);
            responseArea.append("Response from server: " + response + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGui clientGUI = new ClientGui();
            clientGUI.setVisible(true);
        });
    }
}