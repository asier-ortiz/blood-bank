package view;

import controller.FileManager;
import gui.HospitalTableModel;
import model.*;
import util.UtilityFunctions;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Orders extends JFrame {
    private JPanel ordersWindow;
    private JButton filterButton;
    private JButton reloadButton;
    private JPanel filterPanel;
    private JComboBox<BloodGroup> bloodGroupComboBox;
    private JCheckBox allCompatibleGroupsCheckBox;
    private JLabel AB_POSQuantityLabel;
    private JLabel AB_NEGQuantityLabel;
    private JLabel A_POSQuantityLabel;
    private JLabel A_NEGQuantityLabel;
    private JLabel B_POSQuantityLabel;
    private JLabel B_NEGQuantityLabel;
    private JLabel O_POSQuantityLabel;
    private JLabel O_NEGQuantityLabel;
    private JButton AB_POSButton;
    private JButton AB_NEGButton;
    private JButton A_POSButton;
    private JButton A_NEGButton;
    private JButton B_POSButton;
    private JButton B_NEGButton;
    private JButton O_POSButton;
    private JButton O_NEGButton;
    private JSpinner AB_POSSpinner;
    private JSpinner AB_NEGSpinner;
    private JSpinner A_POSSpinner;
    private JSpinner A_NEGSpinner;
    private JSpinner B_POSSpinner;
    private JSpinner B_NEGSpinner;
    private JSpinner O_POSSpinner;
    private JSpinner O_NEGSpinner;
    private JTable hospitalsTable;
    private JButton orderButton;
    private final ArrayList<JButton> buttons;
    private Hospital selectedHospital;
    private List<Hospital> hospitalsList;
    public static HospitalTableModel hospitalTableModel;
    private List<BloodDonation> bloodDonations = new ArrayList<>();
    private int[] orderBloodBagsAmounts = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

    public Orders() {
        buttons = new ArrayList<>(Arrays.asList(O_NEGButton, O_POSButton, B_NEGButton, B_POSButton, A_NEGButton, A_POSButton, AB_NEGButton, AB_POSButton));
        filterPanel.setVisible(false);
        bloodGroupComboBox.setModel(new DefaultComboBoxModel<>(BloodGroup.values()));
        allCompatibleGroupsCheckBox.setSelected(true);
        loadTableData(FileManager.getInstance().getHospitalDAO().getAll());
        resetSpinners();
        loadBloodBagsStockData(BloodGroup.AB_POS);
        setIcons();
        setListeners();
    }

    private void setListeners() {
        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));

        orderButton.addActionListener(e -> {
            if (selectedHospital == null) {
                JOptionPane.showMessageDialog(this, "Por favor selecciona un hospital", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (orderIsEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor añade bolsas al pedido", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                List<BloodBag> bloodBags = new ArrayList<>();
                List<Integer> blooDonationsIds = new ArrayList<>();
                for (int i = 0; i < orderBloodBagsAmounts.length; i++) {
                    List<BloodDonation> temp = new ArrayList<>();
                    switch (i) {
                        case 0 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.O_NEG))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 1 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.O_POS))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 2 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.B_NEG))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 3 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.B_POS))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 4 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.A_NEG))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 5 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.A_POS))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 6 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.AB_NEG))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                        case 7 -> {
                            temp = bloodDonations
                                    .stream()
                                    .filter(bloodDonation -> bloodDonation.getBloodGroup().equals(BloodGroup.AB_POS))
                                    .collect(Collectors.toList());
                            if (!temp.isEmpty()) {
                                Collections.sort(temp);
                                for (int j = 0; j < orderBloodBagsAmounts[i]; j++) {
                                    bloodBags.add(new BloodBag(temp.get(j).getBloodGroup(), temp.get(j).getDateDonated()));
                                    blooDonationsIds.add(temp.get(j).getId());
                                }
                            }
                        }
                    }
                }
                int lastId = FileManager.getInstance().getOrderDAO().getLastId();
                Order newOrder = new Order(lastId, selectedHospital.getId(), LocalDate.now(), bloodBags);
                if (FileManager.getInstance().getOrderDAO().save(newOrder)) {
                    blooDonationsIds.forEach(id -> FileManager.getInstance().getBloodDonationDAO().update(id));
                    JOptionPane.showMessageDialog(this, "Pedido registrado correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                    reload();
                } else {
                    JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        hospitalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (hospitalsTable.getSelectedRow() > -1) {
                selectedHospital = hospitalsList.get(hospitalsTable.getSelectedRow());
            } else {
                selectedHospital = null;
            }
        });

        bloodGroupComboBox.addActionListener(e -> setUIforCompatibleBloodGroups());

        allCompatibleGroupsCheckBox.addActionListener(e -> setUIforCompatibleBloodGroups());

        reloadButton.addActionListener(e -> reload());
    }

    private void reload() {
        bloodDonations = new ArrayList<>();
        selectedHospital = null;
        hospitalsTable.clearSelection();
        resetSpinners();
        bloodGroupComboBox.setSelectedIndex(0);
        loadBloodBagsStockData(BloodGroup.AB_POS);
    }

    private boolean orderIsEmpty() {
        orderBloodBagsAmounts = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        orderBloodBagsAmounts[0] = (Integer) O_NEGSpinner.getValue();
        orderBloodBagsAmounts[1] = (Integer) O_POSSpinner.getValue();
        orderBloodBagsAmounts[2] = (Integer) B_NEGSpinner.getValue();
        orderBloodBagsAmounts[3] = (Integer) B_POSSpinner.getValue();
        orderBloodBagsAmounts[4] = (Integer) A_NEGSpinner.getValue();
        orderBloodBagsAmounts[5] = (Integer) A_POSSpinner.getValue();
        orderBloodBagsAmounts[6] = (Integer) AB_NEGSpinner.getValue();
        orderBloodBagsAmounts[7] = (Integer) AB_POSSpinner.getValue();
        return Arrays.stream(orderBloodBagsAmounts).sum() == 0;
    }

    private void loadTableData(final List<Hospital> hospitals) {
        hospitalsList = hospitals;
        hospitalTableModel = new HospitalTableModel(hospitalsList);
        hospitalsTable.setModel(hospitalTableModel);
        UtilityFunctions.setTableLookAndFeel(hospitalsTable, true);
    }

    private void setIcons() {
        var icons = new ArrayList<ImageIcon>();
        var o_neg = "./assets/O_NEG.png";
        var o_pos = "./assets/O_POS.png";
        var b_neg = "./assets/B_NEG.png";
        var b_pos = "./assets/B_POS.png";
        var a_neg = "./assets/A_NEG.png";
        var a_pos = "./assets/A_POS.png";
        var ab_neg = "./assets/AB_NEG.png";
        var ab_pos = "./assets/AB_POS.png";
        if (UtilityFunctions.isWindows()) {
            o_neg = o_neg.replaceAll("/", "\\");
            o_pos = o_pos.replaceAll("/", "\\");
            b_neg = b_neg.replaceAll("/", "\\");
            b_pos = b_pos.replaceAll("/", "\\");
            a_neg = a_neg.replaceAll("/", "\\");
            a_pos = a_pos.replaceAll("/", "\\");
            ab_neg = ab_neg.replaceAll("/", "\\");
            ab_pos = ab_pos.replaceAll("/", "\\");
        }
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(o_neg), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(o_pos), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(b_neg), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(b_pos), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(a_neg), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(a_pos), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(ab_neg), 100, 100));
        icons.add(UtilityFunctions.resizeImageIcon(new ImageIcon(ab_pos), 100, 100));
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

    private void resetSpinners() {
        AB_POSSpinner.setValue(0);
        AB_NEGSpinner.setValue(0);
        A_POSSpinner.setValue(0);
        A_NEGSpinner.setValue(0);
        B_POSSpinner.setValue(0);
        B_NEGSpinner.setValue(0);
        O_POSSpinner.setValue(0);
        O_NEGSpinner.setValue(0);
        AB_POSSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        AB_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        A_POSSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        A_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        B_POSSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        B_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        O_POSSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        O_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, 0, 0));
        O_NEGQuantityLabel.setText("Cantidad : 0");
        O_POSQuantityLabel.setText("Cantidad : 0");
        B_NEGQuantityLabel.setText("Cantidad : 0");
        B_POSQuantityLabel.setText("Cantidad : 0");
        A_NEGQuantityLabel.setText("Cantidad : 0");
        A_POSQuantityLabel.setText("Cantidad : 0");
        AB_NEGQuantityLabel.setText("Cantidad : 0");
        AB_POSQuantityLabel.setText("Cantidad : 0");
    }

    private void loadBloodBagsStockData(BloodGroup bloodGroup) {
        bloodDonations = FileManager.getInstance().getBloodDonationDAO().getAllAvailableCompatibleWithGroup(bloodGroup);
        Map<BloodGroup, Long> count = bloodDonations
                .stream()
                .collect(Collectors.groupingBy(BloodDonation::getBloodGroup, Collectors.counting()));
        count.forEach((key, value) -> {
            switch (key) {
                case O_NEG -> {
                    O_NEGQuantityLabel.setText("Cantidad : " + value);
                    O_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case O_POS -> {
                    O_POSQuantityLabel.setText("Cantidad : " + value);
                    O_POSSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case B_NEG -> {
                    B_NEGQuantityLabel.setText("Cantidad : " + value);
                    B_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case B_POS -> {
                    B_POSQuantityLabel.setText("Cantidad : " + value);
                    B_POSSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case A_NEG -> {
                    A_NEGQuantityLabel.setText("Cantidad : " + value);
                    A_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case A_POS -> {
                    A_POSQuantityLabel.setText("Cantidad : " + value);
                    A_POSSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case AB_NEG -> {
                    AB_NEGQuantityLabel.setText("Cantidad : " + value);
                    AB_NEGSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
                case AB_POS -> {
                    AB_POSQuantityLabel.setText("Cantidad : " + value);
                    AB_POSSpinner.setModel(new SpinnerNumberModel(0, 0, Math.toIntExact(value), 1));
                }
            }
        });
    }

    private void setUIforCompatibleBloodGroups() {
        resetSpinners();
        switch (bloodGroupComboBox.getItemAt(bloodGroupComboBox.getSelectedIndex())) {
            case AB_POS -> {
                loadBloodBagsStockData(BloodGroup.AB_POS);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(true);
                    AB_NEGButton.setEnabled(true);
                    A_POSButton.setEnabled(true);
                    A_NEGButton.setEnabled(true);
                    B_POSButton.setEnabled(true);
                    B_NEGButton.setEnabled(true);
                    O_POSButton.setEnabled(true);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(true);
                    AB_NEGSpinner.setEnabled(true);
                    A_POSSpinner.setEnabled(true);
                    A_NEGSpinner.setEnabled(true);
                    B_POSSpinner.setEnabled(true);
                    B_NEGSpinner.setEnabled(true);
                    O_POSSpinner.setEnabled(true);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(true);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(true);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case AB_NEG -> {
                loadBloodBagsStockData(BloodGroup.AB_NEG);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(true);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(true);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(true);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(true);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(true);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(true);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(true);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(true);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case A_POS -> {
                loadBloodBagsStockData(BloodGroup.A_POS);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(true);
                    A_NEGButton.setEnabled(true);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(true);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(true);
                    A_NEGSpinner.setEnabled(true);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(true);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(true);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(true);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case A_NEG -> {
                loadBloodBagsStockData(BloodGroup.A_NEG);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(true);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(true);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(true);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(true);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case B_POS -> {
                loadBloodBagsStockData(BloodGroup.B_POS);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(true);
                    B_NEGButton.setEnabled(true);
                    O_POSButton.setEnabled(true);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(true);
                    B_NEGSpinner.setEnabled(true);
                    O_POSSpinner.setEnabled(true);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(true);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(true);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case B_NEG -> {
                loadBloodBagsStockData(BloodGroup.B_NEG);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(true);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(true);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(true);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(true);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case O_POS -> {
                loadBloodBagsStockData(BloodGroup.O_POS);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(true);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(true);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(true);
                    O_NEGButton.setEnabled(false);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(true);
                    O_NEGSpinner.setEnabled(false);
                }
            }
            case O_NEG -> {
                loadBloodBagsStockData(BloodGroup.O_NEG);
                if (allCompatibleGroupsCheckBox.isSelected()) {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(true);
                } else {
                    AB_POSButton.setEnabled(false);
                    AB_NEGButton.setEnabled(false);
                    A_POSButton.setEnabled(false);
                    A_NEGButton.setEnabled(false);
                    B_POSButton.setEnabled(false);
                    B_NEGButton.setEnabled(false);
                    O_POSButton.setEnabled(false);
                    O_NEGButton.setEnabled(true);
                    AB_POSSpinner.setEnabled(false);
                    AB_NEGSpinner.setEnabled(false);
                    A_POSSpinner.setEnabled(false);
                    A_NEGSpinner.setEnabled(false);
                    B_POSSpinner.setEnabled(false);
                    B_NEGSpinner.setEnabled(false);
                    O_POSSpinner.setEnabled(false);
                    O_NEGSpinner.setEnabled(true);
                }
            }
        }
    }

    public JPanel getOrdersWindow() {
        return ordersWindow;
    }
}