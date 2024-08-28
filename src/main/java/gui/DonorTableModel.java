package gui;

import model.Donor;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class DonorTableModel extends AbstractTableModel {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_FIRSTSURNAME = 2;
    private static final int COLUMN_BLOODGROUP = 3;
    private final String[] columnNames = {"Id #", "Nombre", "Primer apellido", "Grupo Sanguíneo"};
    private final Class<?>[] collumnDataType = new Class[]{Integer.class, String.class, String.class, String.class};
    private List<Donor> donors;

    public DonorTableModel(List<Donor> donors) {
        this.donors = donors;
    }

    public void setData(List<Donor> donors) {
        this.donors = donors;
    }

    @Override
    public int getRowCount() {
        return donors.size();
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
        Donor donor = donors.get(rowIndex);
        Object returnValue;
        switch (columnIndex) {
            case COLUMN_ID -> returnValue = donor.getId();
            case COLUMN_NAME -> returnValue = donor.getFirstName();
            case COLUMN_FIRSTSURNAME -> returnValue = donor.getLastName();
            case COLUMN_BLOODGROUP -> returnValue = donor.getBloodGroup().getGroup();
            default -> throw new IllegalArgumentException("Invalid column index");
        }
        return returnValue;
    }

    public Donor getDonor(int rowIndex) {
        return donors.get(rowIndex);
    }
}
