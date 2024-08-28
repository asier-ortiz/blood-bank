package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.time.LocalDate;

@XStreamAlias("BloodBag")
public class BloodBag {
    private final BloodGroup bloodGroup;
    private final LocalDate dateDonated;

    public BloodBag(BloodGroup bloodGroup, LocalDate dateDonated) {
        this.bloodGroup = bloodGroup;
        this.dateDonated = dateDonated;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public LocalDate getDateDonated() {
        return dateDonated;
    }

    @Override
    public String toString() {
        return "BloodBag{" +
                "bloodGroup=" + bloodGroup +
                ", dateDonated=" + dateDonated +
                '}';
    }
}