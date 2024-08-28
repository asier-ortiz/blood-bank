import controller.FileManager;
import util.DBConnection;
import util.UtilityFunctions;
import view.Principal;

public class Main {

    public static void main(String[] args) {

        // PARA CAMBIAR EL PUERTO Y LAS CREDENCIALES IR AL PAQUETE util y modificarlos en la clase DBConnection.java

        // DESCOMENTAR PARA CREAR LA COLECCIÓN Y SUBIR LOS FICHEROS XML

        /*
        DBConnection.createCollection("BloodBankCollection");
        FileManager.getInstance().uploadDonorsXMLFile();
        FileManager.getInstance().uploadBloodDonationsXMLFile();
        FileManager.getInstance().uploadHospitalsXMLFile();
        FileManager.getInstance().uploadOrdersXMLFile();
         */

        System.out.println("\nPARA CAMBIAR EL PUERTO Y LAS CREDENCIALES IR AL PAQUETE util y modificarlos en la clase DBConnection.java");
        System.out.println("PARA CREAR LA COLECCIÓN Y SUBIR LOS FICHEROS XML DESCOMENTAR EL BLOQUE EN LA CLASE Main.java\n");

        UtilityFunctions.setApplicationLookAndFeel();
        Principal principal = new Principal();
        principal.setContentPane(principal.getPrincipalWindow());
        principal.pack();
        principal.setLocationRelativeTo(null);
        principal.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        principal.setSize(1920, 1080);
        principal.setResizable(true);
        principal.setVisible(true);
    }
}