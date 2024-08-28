package util;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XPathQueryService;

import javax.swing.*;
import java.util.Optional;

public class DBConnection {
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8083/exist/xmlrpc/db/";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";
    private static Collection collection;

    public static Optional<Collection> getCollection(final String collectionName) {
        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            collection = DatabaseManager.getCollection(URI + collectionName, USER, PASSWORD);
        } catch (IllegalAccessException | InstantiationException | XMLDBException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Imposible conectarse a la base de datos...", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return Optional.ofNullable(collection);
    }

    public static void createCollection(final String collectionName) {
        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            collection = DatabaseManager.getCollection(URI, USER, PASSWORD);
            XPathQueryService service = (XPathQueryService) collection.getService("XPathQueryService", "1.0");
            String query = "declare namespace rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\";\n" +
                    "\n" +
                    "import module namespace xmldb=\"http://exist-db.org/xquery/xmldb\";\n" +
                    "\n" +
                    "let $log-in := xmldb:login(\"/db\", \"" + USER + "\", \"" + PASSWORD + "\")\n" +
                    "let $create-collection := xmldb:create-collection(\"/db\", \"" + collectionName + "\")\n" +
                    "for $record in doc('/db/" + collectionName + ".rdf')/rdf:RDF/*\n" +
                    "let $split-record :=\n" +
                    "    <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                    "        {$record}\n" +
                    "    </rdf:RDF>\n" +
                    "let $about := $record/@rdf:about\n" +
                    "let $filename := util:hash($record/@rdf:about/string(), \"md5\") || \".xml\"\n" +
                    "return\n" +
                    "    xmldb:store(\"/db/" + collectionName + "\", $filename, $split-record)";
            service.query(query);
            collection.close();
        } catch (IllegalAccessException | InstantiationException | XMLDBException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Imposible conectarse a la base de datos...", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}