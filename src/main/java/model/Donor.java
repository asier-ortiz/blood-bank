package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;

@XStreamAlias("Donor")
public class Donor implements Serializable {
    @XStreamAsAttribute
    private final Integer id;
    private final String firstName;
    private final String lastName;
    @XStreamAlias("Address")
    private final Address address;
    private final String email;
    private final String phone;
    private char sex;
    private BloodGroup bloodGroup;

    public Donor(Integer id, String firstName, String lastName, Address address, String email, String phone, char sex, BloodGroup bloodGroup) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.sex = sex;
        this.bloodGroup = bloodGroup;
    }

    public Donor(Integer id, String firstName, String lastName, Address address, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public char getSex() {
        return sex;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    @Override
    public String toString() {
        return "Donor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address=" + address +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", sex=" + sex +
                ", bloodGroup=" + bloodGroup +
                '}';
    }
}
