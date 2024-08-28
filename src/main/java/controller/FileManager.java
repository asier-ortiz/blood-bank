package controller;

import com.thoughtworks.xstream.XStream;

import java.io.*;

import model.*;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;
import util.DBConnection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FileManager {
    private static FileManager fileManager;
    private final DonorDAO donorDAO = new DonorDAO();
    private final BloodDonationDAO bloodDonationDAO = new BloodDonationDAO();
    private final HospitalDAO hospitalDAO = new HospitalDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    private synchronized static void createInstance() {
        if (fileManager == null) fileManager = new FileManager();
    }

    public synchronized static FileManager getInstance() {
        createInstance();
        return fileManager;
    }

    public DonorDAO getDonorDAO() {
        return donorDAO;
    }

    public BloodDonationDAO getBloodDonationDAO() {
        return bloodDonationDAO;
    }

    public HospitalDAO getHospitalDAO() {
        return hospitalDAO;
    }

    public OrderDAO getOrderDAO() {
        return orderDAO;
    }

    public void createDonorsXMLFile() {
        try {
            XStream xStream = new XStream();
            xStream.processAnnotations(Donor.class);
            xStream.processAnnotations(DonorList.class);
            DonorList donorList = new DonorList();
            donorList.add(
                    new Donor(1,
                            "Asier",
                            "Ortiz",
                            new Address(
                                    "Av. Reina Sofía",
                                    "150",
                                    "01015",
                                    "4º",
                                    "D",
                                    "Vitoria-Gasteiz",
                                    "España"
                            ),
                            "asier@gmail.com",
                            "658 45 96 32",
                            'H',
                            BloodGroup.O_NEG
                    )
            );
            donorList.add(
                    new Donor(2,
                            "Miguen Angel",
                            "Correa",
                            new Address(
                                    "Gorbea",
                                    "15",
                                    "01008",
                                    "2º",
                                    "D",
                                    "Vitoria-Gasteiz",
                                    "España"
                            ),
                            "miguel@gmail.com",
                            "658 45 96 32",
                            'H',
                            BloodGroup.A_NEG
                    )
            );
            donorList.add(
                    new Donor(3,
                            "Nerea",
                            "Alonso",
                            new Address(
                                    "Voluntaria Entrega",
                                    "40",
                                    "01010",
                                    "10º",
                                    "E",
                                    "Vitoria-Gasteiz",
                                    "España"
                            ),
                            "nerea@gmail.com",
                            "658 45 96 32",
                            'M',
                            BloodGroup.AB_POS
                    )
            );
            File targetDir = new File(".//XMLFiles");
            File targetFile = new File(targetDir, "Donors.xml");
            xStream.toXML(donorList, new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createBloodDonationsXMLFile() {
        try {
            XStream xStream = new XStream();
            xStream.processAnnotations(BloodDonation.class);
            xStream.processAnnotations(BloodDonationList.class);
            BloodDonationList bloodDonationList = new BloodDonationList();
            bloodDonationList.add(new BloodDonation(1, 1, BloodGroup.O_NEG, LocalDate.now(), false));
            bloodDonationList.add(new BloodDonation(2, 2, BloodGroup.A_NEG, LocalDate.now()));
            bloodDonationList.add(new BloodDonation(3, 3, BloodGroup.AB_POS, LocalDate.now()));
            File targetDir = new File(".//XMLFiles");
            File targetFile = new File(targetDir, "BloodDonations.xml");
            xStream.toXML(bloodDonationList, new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createHospitalsXMLFile() {
        try {
            XStream xStream = new XStream();
            xStream.processAnnotations(Hospital.class);
            xStream.processAnnotations(HospitalList.class);
            HospitalList hospitalList = new HospitalList();
            hospitalList.add(
                    new Hospital(
                            1,
                            "Txagorritxu",
                            new Address("Jose Atxotegi Kalea",
                                    "s/n", // Mirar si hay una anotación por defecto para nulos
                                    "01009",
                                    "Vitoria-Gasteiz",
                                    "España"
                            ),
                            "945 00 70 00"
                    )
            );
            hospitalList.add(
                    new Hospital(
                            2,
                            "Santiago Apóstol",
                            new Address("Olagibel",
                                    "29",
                                    "01004",
                                    "Vitoria-Gasteiz",
                                    "España"
                            ),
                            "945 00 76 00"
                    )
            );
            File targetDir = new File(".//XMLFiles");
            File targetFile = new File(targetDir, "Hospitals.xml");
            xStream.toXML(hospitalList, new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void createOrdersXMLFile() {
        try {
            XStream xStream = new XStream();
            xStream.processAnnotations(Order.class);
            xStream.processAnnotations(OrderList.class);
            OrderList orderList = new OrderList();
            Order order = new Order(1, 1, LocalDate.now());
            List<BloodBag> bloodBags = new ArrayList<>(Collections.singletonList(new BloodBag(BloodGroup.O_NEG, LocalDate.now())));
            order.setBloodBags(bloodBags);
            orderList.add(order);
            File targetDir = new File(".//XMLFiles");
            File targetFile = new File(targetDir, "Orders.xml");
            xStream.toXML(orderList, new FileOutputStream(targetFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void uploadDonorsXMLFile() {
        File XMLFile = new File(".//XMLFiles//Donors.xml");
        String XMLFileName = "Donors.xml";
        uploadXMLFileToCollection(XMLFile, XMLFileName);
    }

    public void uploadBloodDonationsXMLFile() {
        File XMLFile = new File(".//XMLFiles//BloodDonations.xml");
        String XMLFileName = "BloodDonations.xml";
        uploadXMLFileToCollection(XMLFile, XMLFileName);
    }

    public void uploadHospitalsXMLFile() {
        File XMLFile = new File(".//XMLFiles//Hospitals.xml");
        String XMLFileName = "Hospitals.xml";
        uploadXMLFileToCollection(XMLFile, XMLFileName);
    }

    public void uploadOrdersXMLFile() {
        File XMLFile = new File(".//XMLFiles//Orders.xml");
        String XMLFileName = "Orders.xml";
        uploadXMLFileToCollection(XMLFile, XMLFileName);
    }

    private void uploadXMLFileToCollection(File XMLFile, String XMLFileName) {
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresentOrElse(collection -> {
            try {
                XMLResource res;
                res = (XMLResource) collection.createResource(XMLFileName, "XMLResource");
                res.setContent(XMLFile);
                collection.storeResource(res);
                collection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, () -> System.err.println("Error : Imposible cargar el fichero XML"));
    }

    public void logQuery(String queryName, String query) {
        OutputStream os;
        try {
            os = new FileOutputStream(".//queriesLog//log.txt", true);
            os.write("\n".getBytes());
            os.write(queryName.getBytes());
            os.write("\n".getBytes());
            os.write(query.getBytes());
            os.write("\n".getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}