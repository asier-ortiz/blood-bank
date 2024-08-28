package controller;

import com.thoughtworks.xstream.XStream;
import model.Hospital;
import model.HospitalList;
import org.exist.xmldb.EXistResource;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XQueryService;
import util.DBConnection;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class HospitalDAO {

    public Integer getLastId() {
        AtomicReference<String> ref = new AtomicReference<>();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "HospitalDAO getLastId()";
                String query = "string(/Hospitals/Hospital[@id = max(/Hospitals/Hospital/@id)]/@id)";
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

    public boolean save(final Hospital hospital) {
        AtomicBoolean ok = new AtomicBoolean(false);
        String node = String.format("""
                        <Hospital id=%s%s%s>
                            <name>%s</name>
                            <Address>
                              <street>%s</street>
                              <number>%s</number>
                              <zipCode>%s</zipCode>
                              <city>%s</city>
                              <country>%s</country>
                            </Address>
                            <phone>%s</phone>
                          </Hospital>
                        """,
                "\"", hospital.getId(), "\"", hospital.getName(),
                hospital.getAddress().getStreet(), hospital.getAddress().getNumber(), hospital.getAddress().getZipCode(),
                hospital.getAddress().getCity(), hospital.getAddress().getCountry(),
                hospital.getPhone()
        );
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "HospitalDAO save()";
                String query = "update insert " + node + " into /Hospitals";
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

    public List<Hospital> getAll() {
        HospitalList hospitalList = new HospitalList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "HospitalDAO getAll()";
                String query = "for $hospital in /Hospitals/Hospital return $hospital";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(Hospital.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        Hospital hospital = (Hospital) xStream.fromXML(String.valueOf(res.getContent()));
                        hospitalList.add(hospital);
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
        return hospitalList.getHospitals();
    }

    public List<Hospital> search(final Map<String, String> map) {
        HospitalList hospitalList = new HospitalList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                StringBuilder sb = new StringBuilder("for $hospital in /Hospitals/Hospital");
                int sbInitialLength = sb.length();
                if (!map.get("id").equals("")) sb.append("[@id=").append(map.get("id")).append("]");
                String query = null;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    if (!entry.getValue().equals("")) {
                        switch (entry.getKey()) {
                            case "name", "phone" -> sb.append("\nwhere $hospital/").append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
                            case "city", "country" -> sb.append("\nwhere $hospital/Address/").append(entry.getKey()).append("=").append("'").append(entry.getValue()).append("'");
                        }
                    }
                }
                if (sb.length() > sbInitialLength) {
                    sb.append("\nreturn $hospital");
                    query = sb.toString();
                    String queryName = "HospitalDAO search()";
                    FileManager.getInstance().logQuery(queryName, query);
                }
                if (query != null) {
                    CompiledExpression compiled = xqs.compile(query);
                    ResourceSet result = xqs.execute(compiled);
                    ResourceIterator i = result.getIterator();
                    Resource res = null;
                    XStream xStream = new XStream();
                    xStream.processAnnotations(Hospital.class);
                    while (i.hasMoreResources()) {
                        try {
                            res = i.nextResource();
                            Hospital hospital = (Hospital) xStream.fromXML(String.valueOf(res.getContent()));
                            hospitalList.add(hospital);
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
        return hospitalList.getHospitals();
    }


    public boolean update(final Hospital hospital) {
        AtomicBoolean ok = new AtomicBoolean(false);
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                CompiledExpression compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/name with data ('" + hospital.getName() + "')"
                );
                xqs.execute(compiled);
                String queryName = "HospitalDAO update()";
                String query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/Address" +
                                "/street with data ('" + hospital.getAddress().getStreet() + "')"
                );
                xqs.execute(compiled);
                queryName = "HospitalDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/Address" +
                                "/number with data ('" + hospital.getAddress().getNumber() + "')"
                );
                xqs.execute(compiled);
                queryName = "HospitalDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/Address" +
                                "/zipCodde with data ('" + hospital.getAddress().getZipCode() + "')"
                );
                xqs.execute(compiled);
                queryName = "HospitalDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/Address" +
                                "/city with data ('" + hospital.getAddress().getCity() + "')"
                );
                xqs.execute(compiled);
                queryName = "HospitalDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/Address" +
                                "/country with data ('" + hospital.getAddress().getCountry() + "')"
                );
                xqs.execute(compiled);
                queryName = "HospitalDAO update()";
                query = compiled.toString();
                FileManager.getInstance().logQuery(queryName, query);
                compiled = xqs.compile(
                        "update value /Hospitals/Hospital[@id=" + hospital.getId() + "]" +
                                "/phone with data ('" + hospital.getPhone() + "')"
                );
                xqs.execute(compiled);
                queryName = "HospitalDAO update()";
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

    public boolean delete(final Integer hospitalId) {
        AtomicBoolean ok = new AtomicBoolean(false);
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "HospitalDAO delete()";
                String query = "update delete /Hospitals/Hospital[@id=" + hospitalId + "]";
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