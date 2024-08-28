package controller;

import com.thoughtworks.xstream.XStream;
import model.Order;
import model.OrderList;
import org.exist.xmldb.EXistResource;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.XQueryService;
import util.DBConnection;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OrderDAO {

    public Integer getLastId() {
        AtomicReference<String> ref = new AtomicReference<>();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "OrderDAO getLastId()";
                String query = "string(/Orders/Order[@id = max(/Orders/Order/@id)]/@id)";
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

    public boolean save(final Order order) {
        AtomicBoolean ok = new AtomicBoolean(false);
        StringBuilder st = new StringBuilder();
        order.getBloodBags().forEach(bloodBag -> {
            String bloodBagNode = String.format("""
                                  <BloodBag>
                                    <bloodGroup>%s</bloodGroup>
                                    <dateDonated>%s</dateDonated>
                                  </BloodBag>       
                            """,
                    bloodBag.getBloodGroup().name(), bloodBag.getDateDonated()
            );
            st.append(bloodBagNode);
        });
        String node = String.format("""
                                  <Order id=%s%s%s>
                                    <hospitalId>%s</hospitalId>
                                    <orderDate>%s</orderDate>
                                    <BloodBags>
                                      %s
                                    </BloodBags>
                                  </Order>   
                        """,
                "\"", order.getId(), "\"", order.getHospitalId(), LocalDate.now(), st.toString()
        );
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String queryName = "OrderDAO save()";
                String query = "update insert " + node + " into /Orders";
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

    public List<Order> getAllByHospital(final Integer hospitalId) {
        OrderList orderList = new OrderList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String query = String.format("""
                                    for $order in /Orders/Order
                                        where $order/hospitalId = %s%s%s
                                    return $order            
                                """,
                        "\"", hospitalId, "\""
                );
                String queryName = "OrderDAO getAllByHospital()";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(Order.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        Order order = (Order) xStream.fromXML(String.valueOf(res.getContent()));
                        orderList.add(order);
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
        return orderList.getOrders();
    }

    public List<Order> getAllByHospitalInDate(final Integer hospitalId, String date) {
        OrderList orderList = new OrderList();
        Optional<Collection> optionalCollection = DBConnection.getCollection("BloodBankCollection");
        optionalCollection.ifPresent(collection -> {
            try {
                XQueryService xqs = (XQueryService) collection.getService("XQueryService", "1.0");
                xqs.setProperty("indent", "yes");
                String query = String.format("""
                                    for $order in /Orders/Order
                                        where $order/hospitalId = %s%s%s
                                        and $order/orderDate = %s%s%s
                                    return $order            
                                """,
                        "\"", hospitalId, "\"", "\"", date, "\""
                );
                String queryName = "OrderDAO getAllByHospitalInDate()";
                FileManager.getInstance().logQuery(queryName, query);
                CompiledExpression compiled = xqs.compile(query);
                ResourceSet result = xqs.execute(compiled);
                ResourceIterator i = result.getIterator();
                Resource res = null;
                XStream xStream = new XStream();
                xStream.processAnnotations(Order.class);
                while (i.hasMoreResources()) {
                    try {
                        res = i.nextResource();
                        Order order = (Order) xStream.fromXML(String.valueOf(res.getContent()));
                        orderList.add(order);
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
        return orderList.getOrders();
    }
}