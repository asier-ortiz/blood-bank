package controller;

import com.thoughtworks.xstream.XStream;
import model.Donor;
import model.DonorList;
import org.exist.xmldb.EXistResource;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XQueryService;
import util.DBConnection;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class DonorDAO {

    public Integer getLastId() {
        AtomicReference<String> ref = new AtomicReference<>();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "DonorDAO getLastId()";
                String query = "string(/Donors/Donor[@id = max(/Donors/Donor/@id)]/@id)";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                if (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        ref.set((String) res.getContent());
                    } finally {
                        if (res != null) {
                            try {
                                ((EXistResource) res).freeResources();
                            } catch (XMLDBException xe) {
                                xe.printStackTrace();
                            }
                        }
                    }
                }
            } catch (XMLDBException e) {
                e.printStackTrace();
            } finally {
                try {
                    collection.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        });
        try {
            return Integer.parseInt(String.valueOf(ref.get())) + 1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public boolean save(final Donor donor) {
        AtomicBoolean ok = new AtomicBoolean(false);
        String node = String.format("""
                        <Donor id=%s%s%s>
                            <firstName>%s</firstName>
                            <lastName>%s</lastName>
                            <Address>
                              <street>%s</street>
                              <number>%s</number>
                              <zipCode>%s</zipCode>
                              <floor>%s</floor>
                              <houseNumber>%s</houseNumber>
                              <city>%s</city>
                              <country>%s</country>
                            </Address>
                            <email>%s</email>
                            <phone>%s</phone>
                            <sex>%s</sex>
                            <bloodGroup>%s</bloodGroup>
                          </Donor>""",
                "\"", donor.getId(), "\"", donor.getFirstName(), donor.getLastName(),
                donor.getAddress().getStreet(), donor.getAddress().getNumber(), donor.getAddress().getZipCode(),
                donor.getAddress().getFloor(), donor.getAddress().getHouseNumber(), donor.getAddress().getCity(),
                donor.getAddress().getCountry(),
                donor.getEmail(), donor.getPhone(), donor.getSex(), donor.getBloodGroup().name()
        );
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "DonorDAO save()";
                String query = "update insert " + node + " into /Donors";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                xqs.execute(compiled);
                ok.set(true);
            } catch (XMLDBException e) {
                e.printStackTrace();
            } finally {
                try {
                    collection.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        });
        return ok.get();
    }

    public List<Donor> getAll() {
        DonorList donorList = new DonorList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "DonorDAO getAll()";
                String query = "for $donor in /Donors/Donor return $donor";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(Donor.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        Donor donor = (Donor) xStream.fromXML(String.valueOf(res.getContent()));
                        donorList.add(donor);
                    } finally {
                        if (res != null) {
                            try {
                                ((EXistResource) res).freeResources();
                            } catch (XMLDBException xe) {
                                xe.printStackTrace();
                            }
                        }
                    }
                }
            } catch (XMLDBException e) {
                e.printStackTrace();
            } finally {
                try {
                    collection.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        });
        return donorList.getDonors();
    }

    public List<Donor> search(final Map<String, String> map) {
        DonorList donorList = new DonorList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                StringBuilder sb = new StringBuilder("for $donor in /Donors/Donor");
                if (!map.get("id").equals("")) sb.append("[@id=").append(map.get("id")).append("]");
                int sbInitialLength = sb.length();
                String query = null;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (!entry.getValue().equals("")) {
                        switch (entry.getKey()) {
                            case "firstName", "lastName", "sex", "bloodGroup" -> sb.append("\nwhere $donor/").append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
                            case "city", "country" -> sb.append("\nwhere $donor/Address/").append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
                        }
                    }
                }
                if (sb.length() > sbInitialLength) {
                    sb.append("\nreturn $donor");
                    query = sb.toString();
                    String queryName = "DonorDAO search()";
                    FileManager.getInstance().logQuery(queryName, query);
                }
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(Donor.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        Donor donor = (Donor) xStream.fromXML(String.valueOf(res.getContent()));
                        donorList.add(donor);
                    } finally {
                        if (res != null) {
                            try {
                                ((EXistResource) res).freeResources();
                            } catch (XMLDBException xe) {
                                xe.printStackTrace();
                            }
                        }
                    }
                }
            } catch (XMLDBException e) {
                e.printStackTrace();
            } finally {
                try {
                    collection.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        });
        return donorList.getDonors();
    }

    public boolean update(final Donor donor) {
        AtomicBoolean ok = new AtomicBoolean(false);
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                CompiledExpression compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/firstName with data ('" + donor.getFirstName() + "')"
                );
                xqs.execute(compiled);
                String queryName = "DonorDAO update()";
                String query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/lastName with data ('" + donor.getLastName() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/street with data ('" + donor.getAddress().getStreet() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/number with data ('" + donor.getAddress().getNumber() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/zipCodde with data ('" + donor.getAddress().getZipCode() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/floor with data ('" + donor.getAddress().getFloor() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/houseNumber with data ('" + donor.getAddress().getHouseNumber() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/city with data ('" + donor.getAddress().getCity() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/Address" +
                                "/country with data ('" + donor.getAddress().getCountry() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/email with data ('" + donor.getEmail() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Donors/Donor[@id=" + donor.getId() + "]" +
                                "/phone with data ('" + donor.getPhone() + "')"
                );
                xqs.execute(compiled);
                queryName = "DonorDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                ok.set(true);
            } catch (XMLDBException e) {
                e.printStackTrace();
            } finally {
                try {
                    collection.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        });
        return ok.get();
    }

    public boolean delete(final Integer donorId) {
        AtomicBoolean ok = new AtomicBoolean(false);
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "DonorDAO delete()";
                String query = "update delete /Donors/Donor[@id=" + donorId + "]";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                xqs.execute(compiled);
                ok.set(true);
            } catch (XMLDBException e) {
                e.printStackTrace();
            } finally {
                try {
                    collection.close();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        });
        return ok.get();
    }
}