package org.ulpgc.dacd.control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SwingGUIExecutor implements GUIExecutor {

    @Override
    public void execute() {
        createAndShowInitialGUI();
    }

    private void createAndShowInitialGUI() {
        JFrame initialFrame = new JFrame("Islas Canarias");
        initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        initialFrame.setLayout(new BorderLayout());

        // Panel para los botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 200));
        String[] islands = {"Tenerife", "GranCanaria", "Lanzarote", "Fuerteventura", "LaPalma", "LaGomera", "ElHierro", "LaGraciosa"};

        for (String island : islands) {
            JButton islandButton = new JButton(island);
            islandButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    createAndShowIslandGUI(island);
                    initialFrame.setVisible(false);
                }
            });
            buttonPanel.add(islandButton);
        }

        // Panel para la imagen
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon imageIcon = new ImageIcon(getClass().getResource("/archipielago_canario.jpg"));
                    Image image = imageIcon.getImage();
                    int x = (getWidth() - image.getWidth(this)) / 2;
                    int y = (getHeight() - image.getHeight(this)) / 2;
                    g.drawImage(image, x, y, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        initialFrame.add(imagePanel, BorderLayout.CENTER);
        initialFrame.add(buttonPanel, BorderLayout.SOUTH);

        initialFrame.setVisible(true);
    }

    private void createAndShowIslandGUI(String islandName) {
        JFrame islandFrame = new JFrame(islandName);
        islandFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        islandFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        islandFrame.setLayout(new BorderLayout());

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon imageIcon = new ImageIcon(getClass().getResource("/" + islandName.toLowerCase() + ".jpg"));
                    Image image = imageIcon.getImage();
                    int x = (getWidth() - image.getWidth(this)) / 2;
                    int y = (getHeight() - image.getHeight(this)) / 2;
                    g.drawImage(image, x, y, this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        islandFrame.add(imagePanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Reversa");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                islandFrame.setVisible(false);
                createAndShowInitialGUI();
            }
        });
        islandFrame.getContentPane().add(backButton, BorderLayout.SOUTH);

        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BorderLayout());
        List<JPanel> weatherPanels = showWeatherData(islandName);
        final int[] currentPanelIndex = {0};
        legendPanel.add(weatherPanels.get(currentPanelIndex[0]), BorderLayout.CENTER);

        JButton prevButton = new JButton("<");
        JButton nextButton = new JButton(">");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPanelIndex[0] > 0) {
                    currentPanelIndex[0]--;
                    legendPanel.removeAll();
                    legendPanel.add(weatherPanels.get(currentPanelIndex[0]), BorderLayout.CENTER);
                    legendPanel.revalidate();
                    legendPanel.repaint();
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPanelIndex[0] < weatherPanels.size() - 1) {
                    currentPanelIndex[0]++;
                    legendPanel.removeAll();
                    legendPanel.add(weatherPanels.get(currentPanelIndex[0]), BorderLayout.CENTER);
                    legendPanel.revalidate();
                    legendPanel.repaint();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); // Usamos FlowLayout para centrar los botones y darles un pequeño margen horizontal
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        islandFrame.add(buttonPanel, BorderLayout.NORTH);
        islandFrame.add(legendPanel, BorderLayout.EAST);

        islandFrame.setVisible(true);
    }

    private List<JPanel> showWeatherData(String islandName) {
        List<JPanel> weatherPanels = new ArrayList<>();
        try {
            String databasePath = "Business-Unit/src/main/resources/databases/WeatherEventsMart.db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            String query = "SELECT * FROM " + islandName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                JPanel weatherPanel = new JPanel(new GridLayout(0, 1));
                weatherPanel.add(new JLabel("Hora de predicción: " + rs.getString("predictionTime")));
                weatherPanel.add(new JLabel("Temperatura: " + rs.getDouble("temperature") + " °C"));
                weatherPanel.add(new JLabel("Precipitación: " + rs.getDouble("precipitation") + " mm"));
                weatherPanel.add(new JLabel("Humedad: " + rs.getInt("humidity") + "%"));
                weatherPanel.add(new JLabel("Nubosidad: " + rs.getInt("clouds") + "%"));
                weatherPanel.add(new JLabel("Velocidad del viento: " + rs.getDouble("windSpeed") + " m/s"));
                weatherPanels.add(weatherPanel);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener datos meteorológicos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return weatherPanels;
    }
    private List<String> getRecentTables() {
        List<String> recentTables = new ArrayList<>();
        try {
            String databasePath = "Business-Unit/src/main/resources/databases/WeatherEventsMart.db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});

            // Determinar la fecha de mañana
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String tomorrowDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // Map para mantener un seguimiento de las tablas y sus fechas y horas
            Map<String, LocalDateTime> tableDateTimes = new HashMap<>();

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (tableName.contains(tomorrowDate)) {
                    // Extraer la hora del nombre de la tabla
                    int startIndex = tableName.lastIndexOf("_") + 1;
                    int endIndex = tableName.lastIndexOf(".");
                    String timeString = tableName.substring(startIndex, endIndex);
                    LocalDateTime dateTime = LocalDateTime.parse(tomorrowDate + "_" + timeString, DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    tableDateTimes.put(tableName, dateTime);
                }
            }

            // Ordenar las tablas por fecha y hora
            recentTables = tableDateTimes.entrySet().stream()
                    .sorted(Map.Entry.<String, LocalDateTime>comparingByValue().reversed())
                    .limit(8)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recentTables;
    }
}