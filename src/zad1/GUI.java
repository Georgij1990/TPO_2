package zad1;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GUI {
    private static Double temp;
    private static Long humidity;
    private static Long pressure;
    JFXPanel jfxPanel;
    Service service;
    String weather;
    Double currencyRate;
    Double currencyRatePLN;

    GridBagConstraints gbc;
    JPanel mainRightPanel;

    public GUI(Service service, String weather, Double currencyRate, Double currencyRatePLN) {
        this.service = service;
        this.weather = weather;
        this.currencyRate = currencyRate;
        this.currencyRatePLN = currencyRatePLN;
    }

    public void createAndShowGUI() {
        processWeatherJson();
        JFrame frame = new JFrame("TPO Zadanie 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        GridBagLayout gbl = new GridBagLayout();
        frame.setLayout(gbl);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        jfxPanel = new JFXPanel();
        JPanel mainLeftPanel = new JPanel(gbl);
        mainLeftPanel.add(jfxPanel, gbc);
        gbc.weightx = 0.7;
        frame.add(mainLeftPanel, gbc);

        mainRightPanel = new JPanel(new GridLayout(0, 1, 10, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(mainRightPanel, gbc);

        generateWeatherLabel();
        generateCurrencyLabel();
        generateCurrencyPLNPanel();
        generateButton(frame);

        frame.setVisible(true);

        Platform.runLater(() -> {
            WebView browser = new WebView();
            Scene scene = new Scene(browser);
            jfxPanel.setScene(scene);

            String wikiUrl = String.format("https://en.wikipedia.org/wiki/%s", service.city);
            browser.getEngine().load(wikiUrl);
        });
    }

    private void generateButton(JFrame frame) {
        JButton jButton = new JButton("Input Other Parameters");
        jButton.setFont(new Font("Arial", Font.PLAIN, 24));
        mainRightPanel.add((jButton));
        jButton.addActionListener(generateFormFrame(frame));
    }

    public void generateWeatherLabel() {
        JLabel weatherLabel = new JLabel("<html>Location: " + service.city + ", " + service.country + "<br>" +
                "Temperature: " + (temp - 273.15) + "<br>" +
                "Humidity: " + humidity + "<br>" +
                "Pressure: " + pressure + "</html>");
        weatherLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        weatherLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainRightPanel.add(createPanel(weatherLabel, "Weather"));
    }

    private void generateCurrencyPLNPanel() {
        JLabel currencyRatePLNLabel = new JLabel();
        currencyRatePLNLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        currencyRatePLNLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currencyRatePLNLabel.setVerticalAlignment(SwingConstants.CENTER);
        currencyRatePLNLabel.setText(String.valueOf(currencyRatePLN));
        mainRightPanel.add(createPanel(currencyRatePLNLabel, "Currency Rate PLN"));
    }

    private void generateCurrencyLabel() {
        JLabel currencyRateLabel = new JLabel();
        currencyRateLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        currencyRateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currencyRateLabel.setVerticalAlignment(SwingConstants.CENTER);
        currencyRateLabel.setText(String.valueOf(currencyRate));
        mainRightPanel.add(createPanel(currencyRateLabel, "Currency Rate"));
    }

    private JPanel createPanel(Component component, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setBorder(BorderFactory.createTitledBorder(title));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        titlePanel.add(component, gbc);

        panel.add(titlePanel, BorderLayout.CENTER);
        return panel;
    }

    public void createForm(JFrame frame) {
        JFrame formFrame = new JFrame();
        formFrame.setSize(400, 200);
        formFrame.setTitle("Input Parameters");

        JPanel panel = new JPanel(new GridBagLayout());
        formFrame.add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel countryLabel = new JLabel("Country:");
        JLabel cityLabel = new JLabel("City:");
        JLabel currencyLabel = new JLabel("Currency:");

        JTextField countryField = new JTextField();
        countryField.setPreferredSize(new Dimension(200, 25));
        JTextField cityField = new JTextField();
        cityField.setPreferredSize(new Dimension(200, 25));
        JTextField currencyField = new JTextField();
        currencyField.setPreferredSize(new Dimension(200, 25));

        JPanel buttonsPanel = new JPanel(new FlowLayout());

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(generateNewFrame(frame, countryField, cityField, currencyField, formFrame));
        cancelButton.addActionListener(closeFrame(formFrame));

        buttonsPanel.add(submitButton);
        buttonsPanel.add(cancelButton);

        panel.add(countryLabel, gbc);
        gbc.gridx++;
        panel.add(countryField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(cityLabel, gbc);
        gbc.gridx++;
        panel.add(cityField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(currencyLabel, gbc);
        gbc.gridx++;
        panel.add(currencyField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(buttonsPanel, gbc);

        formFrame.setVisible(true);
        formFrame.setLocationRelativeTo(null);
    }


    public ActionListener generateFormFrame(JFrame frame) {
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createForm(frame);
            }
        };
        return al;
    }
    public ActionListener closeFrame(JFrame jFrame) {
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        };
        return al;
    }

    public ActionListener generateNewFrame(JFrame mainFrame,  JTextField countryField,  JTextField cityField, JTextField currencyField, JFrame formFrame) {
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                formFrame.dispose();
                String country = countryField.getText();
                String city = cityField.getText();
                String currency = currencyField.getText();
                service = new Service(country);
                weather = service.getWeather(city);
                currencyRate = service.getRateFor(currency);
                currencyRatePLN = service.getNBPRate();

                processWeatherJson();

                mainRightPanel.removeAll();
                generateWeatherLabel();
                generateCurrencyLabel();
                generateCurrencyPLNPanel();
                generateButton(mainFrame);


                String wikiUrl = String.format("https://en.wikipedia.org/wiki/%s", city);
                Platform.runLater(() -> {
                    WebView browser = new WebView();
                    Scene scene = new Scene(browser);
                    jfxPanel.setScene(scene);
                    browser.getEngine().load(wikiUrl);
                });
                mainFrame.revalidate();
                mainFrame.repaint();
            }
        };
        return al;
    }

    private void processWeatherJson() {
        JSONParser parser = new JSONParser();
        ContainerFactory containerFactory = new ContainerFactory() {
            @Override
            public Map createObjectContainer() {
                return new LinkedHashMap<>();
            }
            @Override
            public List creatArrayContainer() {
                return new LinkedList<>();
            }
        };
        try {
            Map map = (Map) parser.parse(this.weather, containerFactory);
            map.forEach((k,v)-> {
                String key = (String) k;
                if (key.equals("temp")) {
                    temp = (Double) v;
                } else if (key.equals("humidity")) {
                    humidity = (Long) v;
                } else if (key.equals("pressure")) {
                    pressure = (Long) v;
                }
            });
        } catch(ParseException pe) {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
    }

//    private WebView getWebView(JFrame mainFrame) {
//        for (Component component : mainFrame.getComponents()) {
//            if (component instanceof JFXPanel) {
//                JFXPanel jfxPanel = (JFXPanel) component;
//                Scene scene = jfxPanel.getScene();
//                if (scene != null) {
//                    Parent root = scene.getRoot();
//                    if (root instanceof WebView) {
//                        return (WebView) root;
//                    }
//                }
//            }
//        }
//        return null;
//    }
}
