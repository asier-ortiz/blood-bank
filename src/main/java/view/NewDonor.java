package view;

import controller.FileManager;
import model.Address;
import model.BloodGroup;
import model.Donor;

import javax.swing.*;

public class NewDonor extends JFrame {
    private JPanel newDonorWindow;
    private JTextField nameTextField;
    private JTextField lastNameTextField;
    private JTextField emailTextField;
    private JTextField phoneTextField;
    private JComboBox<String> sexComboBox;
    private JComboBox<BloodGroup> bloodGroupComboBox;
    private JTextField streetTextField;
    private JTextField numberTextField;
    private JTextField zipCodeTextField;
    private JTextField floorTextField;
    private JTextField doorHouseTextField;
    private JTextField cityTextField;
    private JTextField countryTextField;
    private JButton addButton;

    public NewDonor() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        sexComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"H", "M"}));
        bloodGroupComboBox.setModel(new DefaultComboBoxModel<>(BloodGroup.values()));
        setListeners();
    }

    private void setListeners() {
        addButton.addActionListener(e -> {
            if (validateForm()) {
                int lastId = FileManager.getInstance().getDonorDAO().getLastId();
                Donor newDonor = new Donor(lastId,
                        nameTextField.getText().trim(),
                        lastNameTextField.getText().trim(),
                        new Address(
                                streetTextField.getText().trim(),
                                numberTextField.getText().trim(),
                                zipCodeTextField.getText().trim(),
                                floorTextField.getText().trim(),
                                doorHouseTextField.getText().trim(),
                                cityTextField.getText().trim(),
                                countryTextField.getText().trim()
                        ),
                        emailTextField.getText().trim(),
                        phoneTextField.getText().trim(),
                        sexComboBox.getItemAt(sexComboBox.getSelectedIndex()).charAt(0),
                        bloodGroupComboBox.getItemAt(bloodGroupComboBox.getSelectedIndex())
                );
                if (FileManager.getInstance().getDonorDAO().save(newDonor)) {
                    JOptionPane.showMessageDialog(this, "Nuevo donante registrado correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
                    clearInputsData();
                } else {
                    JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor rellene todos los campos correctamente", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private boolean validateForm() {
        if (nameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (lastNameTextField.getText().trim().isEmpty()) {
            return false;
        } else if (emailTextField.getText().trim().isEmpty()) {
            return false;
        } else if (phoneTextField.getText().trim().isEmpty()) {
            return false;
        } else if (streetTextField.getText().trim().isEmpty()) {
            return false;
        } else if (numberTextField.getText().trim().isEmpty()) {
            return false;
        } else if (zipCodeTextField.getText().trim().isEmpty()) {
            return false;
        } else if (floorTextField.getText().trim().isEmpty()) {
            return false;
        } else if (doorHouseTextField.getText().trim().isEmpty()) {
            return false;
        } else if (cityTextField.getText().trim().isEmpty()) {
            return false;
        } else return !countryTextField.getText().trim().isEmpty();
    }

    private void clearInputsData() {
        nameTextField.setText("");
        lastNameTextField.setText("");
        emailTextField.setText("");
        phoneTextField.setText("");
        sexComboBox.setSelectedIndex(0);
        bloodGroupComboBox.setSelectedIndex(0);
        streetTextField.setText("");
        numberTextField.setText("");
        zipCodeTextField.setText("");
        floorTextField.setText("");
        doorHouseTextField.setText("");
        cityTextField.setText("");
        countryTextField.setText("");
    }

    public JPanel getNewDonorWindow() {
        return newDonorWindow;
    }
}