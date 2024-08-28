package view;

import controller.FileManager;
import gui.HospitalTableModel;
import gui.OrderBloodBagsChart;
import model.*;
import util.UtilityFunctions;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class Hospitals extends JFrame {
    private JPanel hospitalsWindow;
    private JButton newHospitalButton;
    private JButton filterButton;
    private JButton reloadButton;
    private JPanel filterPanel;
    private JTextField idTextField;
    private JTextField nameTextField;
    private JTextField phoneTextField;
    private JTextField cityTextField;
    private JTextField countryTextField;
    private JButton searchButton;
    private JButton clearButton;
    private JTable hospitalsTable;
    private JTextField selectedHospitalIdTextField;
    private JTextField selectedHospitalNameTextField;
    private JTextField selectedHospitalPhoneTextField;
    private JTextField selectedHospitalStreetTextField;
    private JTextField selectedHospitalNumberTextField;
    private JTextField selectedHospitalZipCodeTextField;
    private JTextField selectedHospitalCityTextField;
    private JTextField selectedHospitalCountryTextField;
    private JList<String> historicJlist;
    private JButton deleteButton;
    private JButton updateButton;
    private JPanel chartPanel;
    private Hospital selectedHospital;
    private List<Hospital> hospitalsList;
    private List<Order> orders;
    private List<List<BloodBag>> orderBloodBags = new ArrayList<>();
    public static HospitalTableModel hospitalTableModel;
    protected String[] searchFields = {"id", "name", "phone", "city", "country"};
    private OrderBloodBagsChart chart;

    public Hospitals() {
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        filterPanel.setVisible(false);
        chartPanel.setFocusable(false);
        loadTableData(FileManager.getInstance().getHospitalDAO().getAll());
        setListeners();
    }

    private void setListeners() {
        newHospitalButton.addActionListener(e -> {
            NewHospital newHospital = new NewHospital();
            newHospital.setContentPane(newHospital.getNewHospitalWindow());
            newHospital.pack();
            newHospital.setResizable(false);
            newHospital.setLocationRelativeTo(null);
            newHospital.setVisible(true);
        });

        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));

        reloadButton.addActionListener(e -> {
            orderBloodBags = new ArrayList<>();
            loadTableData(FileManager.getInstance().getHospitalDAO().getAll());
        });

        searchButton.addActionListener(e -> {
            orderBloodBags = new ArrayList<>();
            Integer id;
            try {
                id = Integer.parseInt(idTextField.getText().trim());
            } catch (NumberFormatException ex) {
                id = null;
            }
            List<String> searchFieldsValues = Arrays.asList(
                    id == null ? "" : String.valueOf(id),
                    nameTextField.getText().trim(),
                    phoneTextField.getText().trim(),
                    cityTextField.getText().trim(),
                    countryTextField.getText().trim()
            );
            Map<String, String> searchFieldValueMap = new HashMap<>();
            for (int i = 0; i < searchFields.length; i++) {
                searchFieldValueMap.put(searchFields[i], searchFieldsValues.get(i));
            }
            List<Hospital> result = FileManager.getInstance().getHospitalDAO().search(searchFieldValueMap);
            if (!result.isEmpty()) {
                loadTableData(result);
            } else {
                JOptionPane.showMessageDialog(this, "Sin resultados...", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            orderBloodBags = new ArrayList<>();
            clearInputsData();
            loadTableData(FileManager.getInstance().getHospitalDAO().getAll());
            selectedHospital = null;
            deleteButton.setEnabled(false);
            updateButton.setEnabled(false);
            clearSelectedHospitalInfoData();
            historicJlist.setModel(new DefaultListModel<>());
            removeChart();
        });

        hospitalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (hospitalsTable.getSelectedRow() > -1) {
                deleteButton.setEnabled(true);
                updateButton.setEnabled(true);
                selectedHospital = hospitalsList.get(hospitalsTable.getSelectedRow());
                selectedHospitalIdTextField.setText(String.valueOf(selectedHospital.getId()));
                selectedHospitalNameTextField.setText(selectedHospital.getName());
                selectedHospitalPhoneTextField.setText(selectedHospital.getPhone());
                selectedHospitalStreetTextField.setText(selectedHospital.getAddress().getStreet());
                selectedHospitalNumberTextField.setText(selectedHospital.getAddress().getNumber());
                selectedHospitalZipCodeTextField.setText(selectedHospital.getAddress().getZipCode());
                selectedHospitalCityTextField.setText(selectedHospital.getAddress().getCity());
                selectedHospitalCountryTextField.setText(selectedHospital.getAddress().getCountry());
                orders = FileManager.getInstance().getOrderDAO().getAllByHospital(selectedHospital.getId());
                historicJlist.setModel(defaultListModelHospitalOrders());
                removeChart();
            } else {
                selectedHospital = null;
                deleteButton.setEnabled(false);
                updateButton.setEnabled(false);
                clearSelectedHospitalInfoData();
                historicJlist.setModel(new DefaultListModel<>());
                removeChart();
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedHospital != null) {
                Object[] options = {"Si", "No"};
                int reply = JOptionPane.showOptionDialog(
                        this,
                        "¿Desea eliminar este hospital?",
                        "¡Atención!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        1);
                if (reply == JOptionPane.YES_OPTION) {
                    if (FileManager.getInstance().getHospitalDAO().delete(selectedHospital.getId())) {
                        JOptionPane.showMessageDialog(this, "Hospital eliminado correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                        loadTableData(FileManager.getInstance().getHospitalDAO().getAll());
                    } else {
                        JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedHospital != null) {
                if (validateForm()) {
                    if (FileManager.getInstance().getHospitalDAO().update(new Hospital(
                            Integer.parseInt(selectedHospitalIdTextField.getText().trim()),
                            selectedHospitalNameTextField.getText().trim(),
                            new Address(
                                    selectedHospitalStreetTextField.getText().trim(),
                                    selectedHospitalNumberTextField.getText().trim(),
                                    selectedHospitalZipCodeTextField.getText().trim(),
                                    selectedHospitalCityTextField.getText().trim(),
                                    selectedHospitalCountryTextField.getText().trim()
                            ),
                            selectedHospitalPhoneTextField.getText().trim()
                    ))) {
                        JOptionPane.showMessageDialog(this, "Datos hospital actualizados correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                        int index = hospitalsTable.getSelectedRow();
                        loadTableData(FileManager.getInstance().getHospitalDAO().getAll());
                        hospitalsTable.clearSelection();
                        hospitalsTable.setRowSelectionInterval(index, index);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor rellena todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        historicJlist.addListSelectionListener(e -> {
            if (!orderBloodBags.isEmpty()) {
                if (historicJlist.getSelectedIndex() != -1) {
                    List<BloodBag> bloodBags = orderBloodBags.get(historicJlist.getSelectedIndex());
                    createChart(bloodBags);
                }
            }
        });
    }

    private void loadTableData(final List<Hospital> hospitals) {
        hospitalsList = hospitals;
        hospitalTableModel = new HospitalTableModel(hospitalsList);
        hospitalsTable.setModel(hospitalTableModel);
        UtilityFunctions.setTableLookAndFeel(hospitalsTable, true);
    }

    private void createChart(final List<BloodBag> bloodBags) {
        SwingUtilities.invokeLater(() -> {
            Map<BloodGroup, Long> count = bloodBags
                    .stream()
                    .collect(Collectors.groupingBy(BloodBag::getBloodGroup, Collectors.counting()));
            List<String> bloodGroupNames = new ArrayList<>();
            List<Integer> times = new ArrayList<>();
            count.forEach((key, value) -> {
                bloodGroupNames.add(String.valueOf(key));
                times.add(Math.toIntExact(value));
            });
            Collections.sort(bloodGroupNames);
            Collections.sort(times);
            chart = new OrderBloodBagsChart(bloodGroupNames, times);
            chartPanel.removeAll();
            chartPanel.add(chart.getChartPanel());
            chartPanel.validate();
            chartPanel.repaint();
        });
    }

    private void removeChart() {
        SwingUtilities.invokeLater(() -> {
            chartPanel.removeAll();
            chartPanel.validate();
            chartPanel.repaint();
        });
    }

    private void clearInputsData() {
        idTextField.setText("");
        nameTextField.setText("");
        phoneTextField.setText("");
        cityTextField.setText("");
        countryTextField.setText("");
    }

    private void clearSelectedHospitalInfoData() {
        selectedHospitalIdTextField.setText("");
        selectedHospitalNameTextField.setText("");
        selectedHospitalPhoneTextField.setText("");
        selectedHospitalStreetTextField.setText("");
        selectedHospitalNumberTextField.setText("");
        selectedHospitalZipCodeTextField.setText("");
        selectedHospitalCityTextField.setText("");
        selectedHospitalCountryTextField.setText("");
    }

    private boolean validateForm() {
        if (selectedHospitalNameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedHospitalPhoneTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedHospitalStreetTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedHospitalNumberTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedHospitalZipCodeTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedHospitalCityTextField.getText().trim().isEmpty()) {
            return false;
        } else return !selectedHospitalCountryTextField.getText().trim().isEmpty();
    }

    public JPanel getHospitalsWindow() {
        return hospitalsWindow;
    }

    private DefaultListModel<String> defaultListModelHospitalOrders() {
        orderBloodBags = new ArrayList<>();
        DefaultListModel<String> defaultListModelHospitalOrders = new DefaultListModel<>();
        orders.forEach(order -> {
                    orderBloodBags.add(order.getBloodBags());
                    defaultListModelHospitalOrders
                            .addElement(
                                    "ID " + "( " + order.getId() + " )" + " || " +
                                            "ID Hospital " + "( " + order.getHospitalId() + " )" + " || " +
                                            "Fecha pedido " + "( " + order.getOrderDate() + " )");
                }
        );
        return defaultListModelHospitalOrders;
    }
}