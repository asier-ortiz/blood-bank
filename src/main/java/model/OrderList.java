package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Orders")
public class OrderList implements Serializable {

    @XStreamImplicit
    private final List<Order> orders;

    public OrderList() {
        orders = new ArrayList<>();
    }

    public void add(Order o) {
        orders.add(o);
    }

    public List<Order> getOrders() {
        return orders;
    }
}