package slotmachine;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.InputStreamReader;

public class SlotMachineGUI {

    private static final int IMAGE_WIDTH = 64;
    private static final int IMAGE_HEIGHT = 64;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static JSONArray readSymbolsJSON() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(SlotMachineGUI.class.getResourceAsStream("/ressources/symbols.json")))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(content.toString());
        return jsonObject.getJSONArray("symbols");
    }

    private static ImageIcon[] loadImages(int width, int height, JSONArray symbols) {
        ImageIcon[] images = new ImageIcon[symbols.length()];

        for (int i = 0; i < symbols.length(); i++) {
            String imageUrl = symbols.getJSONObject(i).getString("image_url");
            ImageIcon imageIcon = new ImageIcon(imageUrl);
            Image image = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            images[i] = new ImageIcon(image);
        }

        return images;
    }

    private static void createAndShowGUI() {
        // Charger les symboles à partir du fichier symbols.json
        JSONArray symbols = readSymbolsJSON();
        ImageIcon[] images = loadImages(IMAGE_WIDTH, IMAGE_HEIGHT, symbols);

        // Création de la fenêtre principale
        JFrame frame = new JFrame("Slot Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);

        // Ajout du panneau principal avec un fond d'image
        JPanel mainPanel = new JPanel() {
            ImageIcon imageIcon = new ImageIcon(SlotMachineGUI.class.getResource("/ressources/assets/images/slotMachine.png"));
            Image image = imageIcon.getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        frame.add(mainPanel);

        // Créez un tableau 2D de JLabel pour stocker les images
        JLabel[][] imageLabels = new JLabel[5][3];
        GridBagConstraints constraints = new GridBagConstraints();
        Random random = new Random();

        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 3; row++) {
                ImageIcon imageIcon = images[random.nextInt(images.length)];
                imageLabels[col][row] = new JLabel(imageIcon);

                constraints.gridx = col;
                constraints.gridy = row;

                if (row == 0) {
                    constraints.anchor = GridBagConstraints.LINE_START;
                    constraints.insets = new Insets(60, 30, 30, 10);
                } else if (row == 1) {
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.insets = new Insets(20, 20, 20, 10);
                } else {
                    constraints.anchor = GridBagConstraints.LINE_END;
                    constraints.insets = new Insets(0, 10, 10, 10);
                }
                constraints.gridy += 1;

                if (col == 4) {
                    constraints.insets.right = 15;
                } else {
                    constraints.insets.right = 0;
                }

                if (col == 0) {
                    constraints.insets.left = 30;
                } else if (col == 1) {
                    constraints.insets.left = 20;
                } else if (col == 2) {
                    constraints.insets.left = 10;
                } else if (col == 3) {
                    constraints.insets.left = 20;
                } else {
                    constraints.insets.left = 30;
                }

                mainPanel.add(imageLabels[col][row], constraints);
            }
        }

        // Créer les boutons avec des images
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridwidth = 2;
        buttonPanel.setOpaque(false);
        
        // Bouton Spin
        JButton spinButton = createButtonWithImage("/ressources/assets/images/spin.png");
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 2;
        buttonConstraints.weightx = 0.0;
        buttonConstraints.insets = new Insets(340, 16, 0, 0);
        buttonConstraints.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(spinButton, buttonConstraints);
        
        // Bouton Auto Spin
        /*JButton autoSpinButton = createButtonWithImage("/ressources/assets/images/autoSpin.png");
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 2;
        buttonConstraints.weightx = 0.0;
        buttonConstraints.insets = new Insets(600, 200, 0, 0);
        buttonConstraints.anchor = GridBagConstraints.LINE_END;
        buttonPanel.add(autoSpinButton, buttonConstraints);
        
        // Bouton Max Bet
        JButton maxBetButton = createButtonWithImage("/ressources/assets/images/maxBet.png");
        buttonConstraints.gridx = 2;
        buttonConstraints.gridy = 2;
        buttonConstraints.weightx = 0.0;
        buttonConstraints.insets = new Insets(600, 100, 0, 0);
        buttonConstraints.anchor = GridBagConstraints.LINE_END;
        buttonPanel.add(maxBetButton, buttonConstraints);*/
        
        // Ajout du panneau des boutons au panneau principal
        constraints.gridx = 2;
        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.PAGE_END;
        constraints.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(buttonPanel, constraints);

        // Ajout de l'action au bouton Spin
spinButton.addActionListener(e -> {
    // Création de la transition d'animation
    int initialWidth = mainPanel.getWidth();
    int initialHeight = mainPanel.getHeight();
    int finalWidth = (int) (initialWidth * 0.8);
    int finalHeight = (int) (initialHeight * 0.8);
    int deltaX = (initialWidth - finalWidth) / 2;
    int deltaY = (initialHeight - finalHeight) / 2;
    int duration = 2000; // Durée de la transition en millisecondes
    int delay = 20; // Délai entre chaque image de la transition en millisecondes

    Timer timer = new Timer(delay, null);
    timer.addActionListener(new ActionListener() {
        long startTime = -1;
        @Override
        public void actionPerformed(ActionEvent e) {
            if (startTime == -1) {
                startTime = System.currentTimeMillis();
            }

            float progress = Math.min(1f, (System.currentTimeMillis() - startTime) / (float) duration);
            int newWidth = (int) (initialWidth - (initialWidth - finalWidth) * progress);
            int newHeight = (int) (initialHeight - (initialHeight - finalHeight) * progress);
            mainPanel.setPreferredSize(new Dimension(newWidth, newHeight));
            mainPanel.revalidate();
            mainPanel.repaint();

            if (progress == 1f) {
                timer.stop();
                // Modifier les autres composants de l'interface utilisateur selon la transition
            }
        }
    });

    // Démarrer la transition d'animation
    timer.start();
});


        // Affichage de la fenêtre
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createButtonWithImage(String imagePath) {
        ImageIcon imageIcon = new ImageIcon(SlotMachineGUI.class.getResource(imagePath));
        JButton button = new JButton(imageIcon);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        return button;
    }
}

