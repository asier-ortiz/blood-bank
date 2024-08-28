package util;

import gui.TableCellRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class UtilityFunctions {

    public static ImageIcon resizeImageIcon(ImageIcon imageIcon, int width, int height) {
        Image img = imageIcon.getImage();
        Image newimg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }

    public static void setApplicationLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public static boolean isWindows() {
        String OS = System.getProperty("os.name");
        return OS.startsWith("Windows");
    }

    public static void setTableLookAndFeel(JTable table, boolean sortable) {
        TableColumnModel tableColumnModel = table.getColumnModel();
        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new TableCellRenderer());
        }
        table.setRowHeight(25);
        table.setSelectionBackground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Verdana", Font.BOLD, 12));
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(true);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setEnabled(sortable);
    }
}