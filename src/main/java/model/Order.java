package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Order")
public class Order implements Serializable {
    @XStreamAsAttribute
    private final Integer id;
    private final Integer hospitalId;
    private final LocalDate orderDate;
    @XStreamAlias("BloodBags")
    private List<BloodBag> bloodBags;

    public Order(Integer id, Integer hospitalId, LocalDate orderDate, List<BloodBag> bloodBags) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.orderDate = orderDate;
        this.bloodBags = bloodBags;
    }

    public Order(Integer id, Integer hospitalId, LocalDate orderDate) {
        this.id = id;
        this.hospitalId = hospitalId;
        this.orderDate = orderDate;
        this.bloodBags = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public Integer getHospitalId() {
        return hospitalId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public List<BloodBag> getBloodBags() {
        return bloodBags;
    }

    public void setBloodBags(List<BloodBag> bloodBags) {
        this.bloodBags = bloodBags;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", hospitalId=" + hospitalId +
                ", orderDate=" + orderDate +
                ", bloodBags=" + bloodBags +
                '}';
    }
}