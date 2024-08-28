package gui;

import model.Hospital;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class HospitalTableModel extends AbstractTableModel {

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_HOSPITAL_NAME = 1;
    private static final int COLUMN_PHONE = 2;
    private static final int COLUMN_CITY = 3;
    private static final int COLUMN_COUNTRY = 4;
    private final String[] columnNames = {"Id #", "Nombre hospital", "Teléfono", "Ciudad", "País"};
    private final Class<?>[] collumnDataType = new Class[]{Integer.class, String.class, String.class, String.class, String.class};
    private List<Hospital> hospitals;

    public HospitalTableModel(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    public void setData(List<Hospital> hospitals) {
        this.hospitals = hospitals;
    }

    @Override
    public int getRowCount() {
        return hospitals.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return collumnDataType[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Hospital hospital = hospitals.get(rowIndex);
        Object returnValue;
        switch (columnIndex) {
            case COLUMN_ID -> returnValue = hospital.getId();
            case COLUMN_HOSPITAL_NAME -> returnValue = hospital.getName();
            case COLUMN_PHONE -> returnValue = hospital.getPhone();
            case COLUMN_CITY -> returnValue = hospital.getAddress().getCity();
            case COLUMN_COUNTRY -> returnValue = hospital.getAddress().getCountry();
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    public Hospital getHospital(int rowIndex) {
        return hospitals.get(rowIndex);
    }
}