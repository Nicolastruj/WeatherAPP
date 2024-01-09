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

    public void execute() {
        createAndShowInitialGUI();
    }

    private void createAndShowInitialGUI() {
        JFrame initialFrame = new JFrame("Islas Canarias");
        initialFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initialFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        initialFrame.setLayout(new BorderLayout());
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
        backButton.addActionListener(e -> {
            islandFrame.setVisible(false);
            createAndShowInitialGUI();
        });
        islandFrame.getContentPane().add(backButton, BorderLayout.SOUTH);
        JPanel weatherPanelContainer = new JPanel(new BorderLayout());
        JPanel hotelPanelContainer = new JPanel(new BorderLayout());
        CardLayout weatherCardLayout = new CardLayout();
        CardLayout hotelCardLayout = new CardLayout();

        JPanel weatherLegendPanel = new JPanel(weatherCardLayout);
        JPanel hotelLegendPanel = new JPanel(hotelCardLayout);

        List<JPanel> weatherPanels = showWeatherData(islandName);
        List<JPanel> hotelEventPanels = showHotelEventPanels(islandName);

        for (JPanel weatherPanel : weatherPanels) {
            weatherLegendPanel.add(weatherPanel, "Weather");
        }

        for (JPanel hotelPanel : hotelEventPanels) {
            hotelLegendPanel.add(hotelPanel, "Hotel");
        }

        weatherPanelContainer.add(weatherLegendPanel, BorderLayout.CENTER);
        hotelPanelContainer.add(hotelLegendPanel, BorderLayout.CENTER);

        islandFrame.add(weatherPanelContainer, BorderLayout.WEST);
        islandFrame.add(hotelPanelContainer, BorderLayout.EAST);
        JButton prevWeatherButton = new JButton("<");
        JButton nextWeatherButton = new JButton(">");

        prevWeatherButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) weatherLegendPanel.getLayout();
            cardLayout.previous(weatherLegendPanel);
        });

        nextWeatherButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) weatherLegendPanel.getLayout();
            cardLayout.next(weatherLegendPanel);
        });

        JPanel weatherButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        weatherButtonPanel.add(prevWeatherButton);
        weatherButtonPanel.add(nextWeatherButton);
        JButton prevHotelButton = new JButton("<");
        JButton nextHotelButton = new JButton(">");

        prevHotelButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) hotelLegendPanel.getLayout();
            cardLayout.previous(hotelLegendPanel);
        });

        nextHotelButton.addActionListener(e -> {
            CardLayout cardLayout = (CardLayout) hotelLegendPanel.getLayout();
            cardLayout.next(hotelLegendPanel);
        });

        JPanel hotelButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        hotelButtonPanel.add(prevHotelButton);
        hotelButtonPanel.add(nextHotelButton);
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.add(weatherButtonPanel, BorderLayout.WEST);
        navigationPanel.add(hotelButtonPanel, BorderLayout.EAST);

        islandFrame.add(navigationPanel, BorderLayout.NORTH);

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
                weatherPanel.add(new JLabel("Prediction time: " + rs.getString("predictionTime")));
                weatherPanel.add(new JLabel("Temperature: " + rs.getDouble("temperature") + " °C"));
                weatherPanel.add(new JLabel("Precipitation: " + rs.getDouble("precipitation") + " mm"));
                weatherPanel.add(new JLabel("Humidity: " + rs.getInt("humidity") + "%"));
                weatherPanel.add(new JLabel("Cloudisness: " + rs.getInt("clouds") + "%"));
                weatherPanel.add(new JLabel("Wind speed: " + rs.getDouble("windSpeed") + " m/s"));
                weatherPanels.add(weatherPanel);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error in obtaining data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return weatherPanels;
    }

    private List<JPanel> showHotelEventPanels(String islandName) {
        List<JPanel> hotelEventPanels = new ArrayList<>();
        List<String> recentTables = getRecentTables();

        try {
            String databasePath = "Business-Unit/src/main/resources/databases/HotelEventsMart.db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);

            for (String tableName : recentTables) {
                if (tableName.startsWith(islandName)) {
                    String query = "SELECT * FROM " + tableName;
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        JPanel hotelEventPanel = new JPanel(new GridLayout(0, 1));
                        hotelEventPanel.add(new JLabel("Name: " + rs.getString("name")));
                        hotelEventPanel.add(new JLabel("Location: " + rs.getString("location")));
                        hotelEventPanel.add(new JLabel("Price: " + rs.getDouble("price") + " €"));
                        hotelEventPanel.add(new JLabel("Review note: " + rs.getString("review")));
                        hotelEventPanel.add(new JLabel("Number of review: " + rs.getString("reviewNumber")));
                        hotelEventPanel.add(new JLabel("Distance to the center: " + rs.getString("distanceToCenter") + "km"));
                        hotelEventPanels.add(hotelEventPanel);
                    }

                    rs.close();
                    stmt.close();
                }
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error in obtaining data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return hotelEventPanels;
    }


    private List<String> getRecentTables() {
        List<String> recentTables = new ArrayList<>();
        try {
            String databasePath = "Business-Unit/src/main/resources/databases/HotelEventsMart.db";
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});

            LocalDate tomorrow = LocalDate.now().plusDays(1);
            String tomorrowDate = tomorrow.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            Map<String, LocalDateTime> tableDateTimes = new HashMap<>();

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (tableName.contains(tomorrowDate)) {
                    int startIndex = tableName.lastIndexOf("_") + 1;
                    int endIndex = tableName.lastIndexOf("_", startIndex) + 3;
                    String timeString = tableName.substring(startIndex, endIndex);
                    LocalDateTime dateTime = LocalDateTime.parse(tomorrowDate + "_" + timeString, DateTimeFormatter.ofPattern("yyyyMMdd_HH"));
                    tableDateTimes.put(tableName, dateTime);
                }
            }

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
