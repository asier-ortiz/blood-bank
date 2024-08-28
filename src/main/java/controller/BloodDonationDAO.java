package controller;

import com.thoughtworks.xstream.XStream;
import model.BloodDonation;
import model.BloodDonationList;
import model.BloodGroup;
import org.exist.xmldb.EXistResource;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XQueryService;
import util.DBConnection;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BloodDonationDAO {

    public Integer getLastId() {
        AtomicReference<String> ref = new AtomicReference<>();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "BloodDonationDAO getLastId()";
                String query = "string(/BloodDonations/BloodDonation[@id = max(/BloodDonations/BloodDonation/@id)]/@id)";
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

    public boolean save(final BloodDonation bloodDonation) {
        AtomicBoolean ok = new AtomicBoolean(false);
        String node = String.format("""
                        <BloodDonation id=%s%s%s>
                            <donorId>%s</donorId>
                            <dateDonated>%s</dateDonated>
                            <bloodGroup>%s</bloodGroup>
                            <isAvailable>%s</isAvailable>
                          </BloodDonation>""",
                "\"", bloodDonation.getId(), "\"", bloodDonation.getDonorId(), bloodDonation.getDateDonated(),
                bloodDonation.getBloodGroup().name(), bloodDonation.isAvailable()
        );
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "BloodDonationDAO save()";
                String query = "update insert " + node + " into /BloodDonations";
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

    public List<BloodDonation> getAllByDonorId(final Integer donorId) {
        BloodDonationList bloodDonationList = new BloodDonationList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "BloodDonationDAO getAllByDonorId()";
                String query = "/BloodDonations/BloodDonation[donorId=" + donorId + "]";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(BloodDonation.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        BloodDonation bloodDonation = (BloodDonation) xStream.fromXML(String.valueOf(res.getContent()));
                        bloodDonationList.add(bloodDonation);
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
        return bloodDonationList.getBloodDonations();
    }

    public List<BloodDonation> getAllAvailableCompatibleWithGroup(BloodGroup bloodGroup) {
        ArrayList<String> complatibleBloodGroupsNames = new ArrayList<>();
        BloodGroup.compatibleWith(bloodGroup).forEach(bg -> complatibleBloodGroupsNames.add(bg.name()));
        StringBuilder st = new StringBuilder();
        for (int i = 0; i < complatibleBloodGroupsNames.size(); i++) {
            if (i == 0) {
                st.append("and contains($bg, ").append("\"").append(complatibleBloodGroupsNames.get(i)).append("\"").append(")");
            } else {
                st.append(" or contains($bg, ").append("\"").append(complatibleBloodGroupsNames.get(i)).append("\"").append(")");
            }
        }
        BloodDonationList bloodDonationList = new BloodDonationList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                LocalDate aMonthAgo = LocalDate.now().minus(1, ChronoUnit.MONTHS);
                String query = String.format("""
                                for $bd in /BloodDonations/BloodDonation
                                let $dd := $bd/dateDonated
                                let $bg := $bd/bloodGroup
                                where $bd/dateDonated gt %s%s%s
                                %s
                                where $bd/isAvailable = 'true'
                                order by $bd/dateDonated descending
                                return $bd
                                """,
                        "\"", aMonthAgo, "\"", st.toString());
                String queryName = "BloodDonationDAO getAllAvailableCompatibleWithGroup()";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(BloodDonation.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        BloodDonation bloodDonation = (BloodDonation) xStream.fromXML(String.valueOf(res.getContent()));
                        bloodDonationList.add(bloodDonation);
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
        return bloodDonationList.getBloodDonations();
    }

    public boolean update(final Integer bloodDonationId) {
        AtomicBoolean ok = new AtomicBoolean(false);
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "BloodDonationDAO update()";
                String query = "update value /BloodDonations/BloodDonation[@id=" + bloodDonationId + "]" +
                        "/isAvailable with data ('" + false + "')";
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