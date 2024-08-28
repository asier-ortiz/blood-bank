package view;

import controller.FileManager;
import gui.BloodDonationsChart;
import gui.DonorTableModel;
import model.Address;
import model.BloodDonation;
import model.BloodGroup;
import model.Donor;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import util.UtilityFunctions;

public class Donors extends JFrame {
    private JPanel donorsWindow;
    private JButton newDonorButton;
    private JButton filterButton;
    private JButton reloadButton;
    private JPanel filterPanel;
    private JTextField idTextField;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JComboBox<String> sexComboBox;
    private JComboBox<BloodGroup> bloodGroupComboBox;
    private JTextField cityTextField;
    private JTextField countryTextField;
    private JButton searchButton;
    private JButton clearButton;
    private JTable donorsTable;
    private JButton newDonation;
    private JTextField selectedDonorIdTextField;
    private JTextField selectedDonorFirstNameTextField;
    private JTextField selectedDonorLastNameTextField;
    private JTextField selectedDonorEmailTextField;
    private JTextField selectedDonorPhoneTextField;
    private JComboBox<String> selectedDonorSexComoBox;
    private JComboBox<BloodGroup> selectedDonorBloodGroupComboBox;
    private JTextField selectedDonorStreetTextField;
    private JTextField selectedDonorNumberTextField;
    private JTextField selectedDonorZipCodeTextField;
    private JTextField selectedDonorFloorTextField;
    private JTextField selectedDonorHouseNumberTextField;
    private JTextField selectedDonorCityTextField;
    private JTextField selectedDonorCountryTextField;
    private JList<String> historicJlist;
    private JButton deleteButton;
    private JButton updateButton;
    private JPanel chartPanel;
    private Donor selectedDonor;
    private List<Donor> donorsList;
    private List<BloodDonation> bloodDonations;
    public static DonorTableModel donorTableModel;
    protected String[] searchFields = {"id", "firstName", "lastName", "sex", "bloodGroup", "city", "country"};
    private BloodDonationsChart chart;

    public Donors() {
        newDonation.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);
        sexComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"H", "M"}));
        selectedDonorSexComoBox.setModel(new DefaultComboBoxModel<>(new String[]{"H", "M"}));
        bloodGroupComboBox.setModel(new DefaultComboBoxModel<>(BloodGroup.values()));
        selectedDonorBloodGroupComboBox.setModel(new DefaultComboBoxModel<>(BloodGroup.values()));
        filterPanel.setVisible(false);
        chartPanel.setFocusable(false);
        loadTableData(FileManager.getInstance().getDonorDAO().getAll());
        setListeners();
    }

    private void setListeners() {
        newDonorButton.addActionListener(e -> {
            NewDonor newDonor = new NewDonor();
            newDonor.setContentPane(newDonor.getNewDonorWindow());
            newDonor.pack();
            newDonor.setResizable(false);
            newDonor.setLocationRelativeTo(null);
            newDonor.setVisible(true);

        });

        filterButton.addActionListener(e -> filterPanel.setVisible(!filterPanel.isVisible()));

        reloadButton.addActionListener(e -> loadTableData(FileManager.getInstance().getDonorDAO().getAll()));

        searchButton.addActionListener(e -> {
            Integer id;
            try {
                id = Integer.parseInt(idTextField.getText().trim());
            } catch (NumberFormatException ex) {
                id = null;
            }
            List<String> searchFieldsValues = Arrays.asList(
                    id == null ? "" : String.valueOf(id),
                    firstNameTextField.getText().trim(),
                    lastNameTextField.getText().trim(),
                    sexComboBox.getSelectedIndex() == 0 ? "H" : "M",
                    bloodGroupComboBox.getItemAt(bloodGroupComboBox.getSelectedIndex()).name(),
                    cityTextField.getText().trim(),
                    countryTextField.getText().trim()
            );
            Map<String, String> searchFieldValueMap = new HashMap<>();
            for (int i = 0; i < searchFields.length; i++) {
                searchFieldValueMap.put(searchFields[i], searchFieldsValues.get(i));
            }
            List<Donor> result = FileManager.getInstance().getDonorDAO().search(searchFieldValueMap);
            if (!result.isEmpty()) {
                loadTableData(result);
            } else {
                JOptionPane.showMessageDialog(this, "Sin resultados...", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            clearInputsData();
            loadTableData(FileManager.getInstance().getDonorDAO().getAll());
            selectedDonor = null;
            newDonation.setEnabled(false);
            deleteButton.setEnabled(false);
            updateButton.setEnabled(false);
            clearSelectedDonorInfoData();
            historicJlist.setModel(new DefaultListModel<>());
            removeChart();
        });

        donorsTable.getSelectionModel().addListSelectionListener(e -> {
            if (donorsTable.getSelectedRow() > -1) {
                newDonation.setEnabled(true);
                deleteButton.setEnabled(true);
                updateButton.setEnabled(true);
                selectedDonor = donorsList.get(donorsTable.getSelectedRow());
                selectedDonorIdTextField.setText(String.valueOf(selectedDonor.getId()));
                selectedDonorFirstNameTextField.setText(selectedDonor.getFirstName());
                selectedDonorLastNameTextField.setText(selectedDonor.getLastName());
                selectedDonorEmailTextField.setText(selectedDonor.getEmail());
                selectedDonorPhoneTextField.setText(selectedDonor.getPhone());
                selectedDonorSexComoBox.setSelectedIndex(selectedDonor.getSex() == 'H' ? 0 : 1);
                selectedDonorBloodGroupComboBox.setSelectedItem(selectedDonor.getBloodGroup());
                selectedDonorStreetTextField.setText(selectedDonor.getAddress().getStreet());
                selectedDonorNumberTextField.setText(selectedDonor.getAddress().getNumber());
                selectedDonorZipCodeTextField.setText(selectedDonor.getAddress().getZipCode());
                selectedDonorFloorTextField.setText(selectedDonor.getAddress().getFloor());
                selectedDonorHouseNumberTextField.setText(selectedDonor.getAddress().getHouseNumber());
                selectedDonorCityTextField.setText(selectedDonor.getAddress().getCity());
                selectedDonorCountryTextField.setText(selectedDonor.getAddress().getCountry());
                bloodDonations = FileManager.getInstance().getBloodDonationDAO().getAllByDonorId(selectedDonor.getId());
                historicJlist.setModel(defaultListModelDonorBloodDonations());
                if (bloodDonations.size() > 0) createChart();
            } else {
                selectedDonor = null;
                newDonation.setEnabled(false);
                deleteButton.setEnabled(false);
                updateButton.setEnabled(false);
                clearSelectedDonorInfoData();
                historicJlist.setModel(new DefaultListModel<>());
                removeChart();
            }
        });

        newDonation.addActionListener(e -> {
            if (selectedDonor != null) {
                Integer lastId = FileManager.getInstance().getBloodDonationDAO().getLastId();
                if (FileManager.getInstance().getBloodDonationDAO().save(new BloodDonation(
                        lastId, selectedDonor.getId(), selectedDonor.getBloodGroup(), LocalDate.now()
                ))) {
                    JOptionPane.showMessageDialog(this, "Donación registrada correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                    int index = donorsTable.getSelectedRow();
                    loadTableData(FileManager.getInstance().getDonorDAO().getAll());
                    donorsTable.clearSelection();
                    donorsTable.setRowSelectionInterval(index, index);
                } else {
                    JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            if (selectedDonor != null) {
                Object[] options = {"Si", "No"};
                int reply = JOptionPane.showOptionDialog(
                        this,
                        "¿Desea eliminar a este donante?",
                        "¡Atención!",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        1);
                if (reply == JOptionPane.YES_OPTION) {
                    if (FileManager.getInstance().getDonorDAO().delete(selectedDonor.getId())) {
                        JOptionPane.showMessageDialog(this, "Donante eliminado correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                        loadTableData(FileManager.getInstance().getDonorDAO().getAll());
                    } else {
                        JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updateButton.addActionListener(e -> {
            if (selectedDonor != null) {
                if (validateForm()) {
                    if (FileManager.getInstance().getDonorDAO().update(new Donor(
                            Integer.parseInt(selectedDonorIdTextField.getText().trim()),
                            selectedDonorFirstNameTextField.getText().trim(),
                            selectedDonorLastNameTextField.getText().trim(),
                            new Address(
                                    selectedDonorStreetTextField.getText().trim(),
                                    selectedDonorNumberTextField.getText().trim(),
                                    selectedDonorZipCodeTextField.getText().trim(),
                                    selectedDonorFloorTextField.getText().trim(),
                                    selectedDonorHouseNumberTextField.getText().trim(),
                                    selectedDonorCityTextField.getText().trim(),
                                    selectedDonorCountryTextField.getText().trim()
                            ),
                            selectedDonorEmailTextField.getText().trim(),
                            selectedDonorPhoneTextField.getText().trim()
                    ))) {
                        JOptionPane.showMessageDialog(this, "Datos donante actualizados correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                        int index = donorsTable.getSelectedRow();
                        loadTableData(FileManager.getInstance().getDonorDAO().getAll());
                        donorsTable.clearSelection();
                        donorsTable.setRowSelectionInterval(index, index);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor rellena todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadTableData(final List<Donor> donors) {
        donorsList = donors;
        donorTableModel = new DonorTableModel(donorsList);
        donorsTable.setModel(donorTableModel);
        UtilityFunctions.setTableLookAndFeel(donorsTable, true);
    }

    private void createChart() {
        SwingUtilities.invokeLater(() -> {
            Map<Integer, Long> count = bloodDonations
                    .stream()
                    .collect(Collectors.groupingBy(BloodDonation::getYearDonated, Collectors.counting()));
            List<String> years = new ArrayList<>();
            List<Integer> times = new ArrayList<>();
            count.forEach((key, value) -> {
                years.add(String.valueOf(key));
                times.add(Math.toIntExact(value));
            });
            Collections.sort(years);
            Collections.sort(times);
            chart = new BloodDonationsChart(years, times);
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
        firstNameTextField.setText("");
        lastNameTextField.setText("");
        sexComboBox.setSelectedIndex(0);
        cityTextField.setText("");
        countryTextField.setText("");
        bloodGroupComboBox.setSelectedIndex(0);
    }

    private void clearSelectedDonorInfoData() {
        selectedDonorIdTextField.setText("");
        selectedDonorFirstNameTextField.setText("");
        selectedDonorLastNameTextField.setText("");
        selectedDonorEmailTextField.setText("");
        selectedDonorPhoneTextField.setText("");
        selectedDonorSexComoBox.setSelectedIndex(0);
        selectedDonorBloodGroupComboBox.setSelectedIndex(0);
        selectedDonorStreetTextField.setText("");
        selectedDonorNumberTextField.setText("");
        selectedDonorZipCodeTextField.setText("");
        selectedDonorFloorTextField.setText("");
        selectedDonorHouseNumberTextField.setText("");
        selectedDonorCityTextField.setText("");
        selectedDonorCountryTextField.setText("");
    }

    private boolean validateForm() {
        if (selectedDonorFirstNameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorLastNameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorEmailTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorPhoneTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorStreetTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorNumberTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorZipCodeTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorFloorTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorHouseNumberTextField.getText().trim().isEmpty()) {
            return false;
        } else if (selectedDonorCityTextField.getText().trim().isEmpty()) {
            return false;
        } else return !selectedDonorCountryTextField.getText().trim().isEmpty();
    }

    public JPanel getDonorsWindow() {
        return donorsWindow;
    }


    private DefaultListModel<String> defaultListModelDonorBloodDonations() {
        DefaultListModel<String> defaultListModelDonorBloodDonations = new DefaultListModel<>();
        bloodDonations.forEach(bloodDonation -> defaultListModelDonorBloodDonations
                .addElement(
                        "ID " + "( " + bloodDonation.getId() + " )" + " || " +
                                "Grupo sanguíneo " + "( " + bloodDonation.getBloodGroup().getGroup() + " )" + " || " +
                                "Fecha donación " + "( " + bloodDonation.getDateDonated() + " )")
        );
        return defaultListModelDonorBloodDonations;
    }
}