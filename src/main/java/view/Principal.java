package view;

import gui.CustomCardLayout;
import util.UtilityFunctions;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Principal extends JFrame {
    private JPanel principalWindow;
    private JButton donorsButton;
    private JButton hospitalsButton;
    private JButton ordersButton;
    private JPanel cards;
    private final CardLayout cardLayout;
    public static final String DONORS = "Donors";
    public static final String HOSPITALS = "Hospitals";
    public static final String ORDERS = "Orders";
    private final ArrayList<JButton> buttons;

    public Principal() {
        super("Banco de Sangre");
        buttons = new ArrayList<>(Arrays.asList(donorsButton, hospitalsButton, ordersButton));
        cardLayout = new CustomCardLayout();
        cards.setLayout(cardLayout);
        cards.add(createDonorsPanel(), DONORS);
        cards.add(createHospitalsPanel(), HOSPITALS);
        cards.add(createOrdersPanel(), ORDERS);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setListeners();
        setIcons();
    }

    public JPanel getPrincipalWindow() {
        return principalWindow;
    }

    private void setListeners() {
        donorsButton.addActionListener(e -> swapView("Donors"));
        hospitalsButton.addActionListener(e -> swapView("Hospitals"));
        ordersButton.addActionListener(e -> swapView("Orders"));
    }

    private void setIcons() {
        var icons = new ArrayList<ImageIcon>();
        var donors = "./assets/donors.png";
        var hospitals = "./assets/hospitals.png";
        var orders = "./assets/orders.png";
        if (UtilityFunctions.isWindows()) {
            donors = donors.replaceAll("/", "\\");
            hospitals = hospitals.replaceAll("/", "\\");
            orders = orders.replaceAll("/", "\\");
        }
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(donors), 50, 50));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(hospitals), 50, 50));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(orders), 50, 50));
        for (int i = 0; i < icons.size(); i++) {
            ImageIcon icon = icons.get(i);
            JButton button = buttons.get(i);
            button.setIcon(icon);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
        }
    }

    private JPanel createDonorsPanel() {
        return new Donors().getDonorsWindow();
    }

    private JPanel createHospitalsPanel() {
        return new Hospitals().getHospitalsWindow();
    }

    private JPanel createOrdersPanel() {
        return new Orders().getOrdersWindow();
    }

    private void swapView(String key) {
        SwingUtilities.invokeLater(() -> {
            cards.removeAll();
            switch (key) {
                case "Donors" -> cards.add(createDonorsPanel(), DONORS);
                case "Hospitals" -> cards.add(createHospitalsPanel(), HOSPITALS);
                case "Orders" -> cards.add(createOrdersPanel(), ORDERS);
            }
            cardLayout.show(cards, key);
            validate();
            repaint();
        });
    }
}