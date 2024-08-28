package view;

import controller.FileManager;
import model.Address;
import model.Hospital;

import javax.swing.*;

public class NewHospital extends JFrame {
    private JPanel newHospitalWindow;
    private JTextField nameTextField;
    private JTextField phoneTextField;
    private JTextField streetTextField;
    private JTextField numberTextField;
    private JTextField zipCodeTextField;
    private JTextField cityTextField;
    private JTextField countryTextField;
    private JButton addButton;

    public NewHospital() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setListeners();
    }

    private void setListeners() {
        addButton.addActionListener(e -> {
            if (validateForm()) {
                int lastId = FileManager.getInstance().getHospitalDAO().getLastId();
                Hospital newHospital = new Hospital(lastId,
                        nameTextField.getText().trim(),
                        new Address(
                                streetTextField.getText().trim(),
                                numberTextField.getText().trim(),
                                zipCodeTextField.getText().trim(),
                                cityTextField.getText().trim(),
                                countryTextField.getText().trim()
                        ),
                        phoneTextField.getText().trim()
                );
                if (FileManager.getInstance().getHospitalDAO().save(newHospital)) {
                    JOptionPane.showMessageDialog(this, "Nuevo hospital registrado correctamente", "Info", JOptionPane.INFORMATION_MESSAGE);
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
        } else if (phoneTextField.getText().trim().isEmpty()) {
            return false;
        } else if (streetTextField.getText().trim().isEmpty()) {
            return false;
        } else if (numberTextField.getText().trim().isEmpty()) {
            return false;
        } else if (zipCodeTextField.getText().trim().isEmpty()) {
            return false;
        } else if (cityTextField.getText().trim().isEmpty()) {
            return false;
        } else return !countryTextField.getText().trim().isEmpty();
    }

    private void clearInputsData() {
        nameTextField.setText("");
        phoneTextField.setText("");
        streetTextField.setText("");
        numberTextField.setText("");
        zipCodeTextField.setText("");
        cityTextField.setText("");
        countryTextField.setText("");
    }

    public JPanel getNewHospitalWindow() {
        return newHospitalWindow;
    }
}