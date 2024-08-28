package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@XStreamAlias("BloodDonation")
public class BloodDonation implements Serializable, Comparable<BloodDonation> {
    @XStreamAsAttribute
    private final Integer id;
    private final Integer donorId;
    private final BloodGroup bloodGroup;
    private final LocalDate dateDonated;
    private boolean isAvailable;

    public BloodDonation(Integer id, Integer donorId, BloodGroup bloodGroup, LocalDate dateDonated, boolean isAvailable) {
        this.id = id;
        this.donorId = donorId;
        this.bloodGroup = bloodGroup;
        this.dateDonated = dateDonated;
        this.isAvailable = isAvailable;
    }

    public BloodDonation(Integer id, Integer donorId, BloodGroup bloodGroup, LocalDate dateDonated) {
        this.id = id;
        this.donorId = donorId;
        this.bloodGroup = bloodGroup;
        this.dateDonated = dateDonated;
        this.isAvailable = true;
    }

    public Integer getId() {
        return id;
    }

    public Integer getDonorId() {
        return donorId;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public LocalDate getDateDonated() {
        return dateDonated;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Integer getYearDonated() {
        return dateDonated.getYear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodDonation that = (BloodDonation) o;
        return bloodGroup == that.bloodGroup;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bloodGroup);
    }

    @Override
    public int compareTo(BloodDonation bd) {
        return bd.getDateDonated().compareTo(this.getDateDonated());
    }

    @Override
    public String toString() {
        return "BloodDonation{" +
                "id=" + id +
                ", donorId=" + donorId +
                ", bloodGroup=" + bloodGroup +
                ", dateDonated=" + dateDonated +
                ", isAvailable=" + isAvailable +
                '}';
    }
}