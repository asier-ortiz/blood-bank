package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("BloodDonations")
public class BloodDonationList {

    @XStreamImplicit
    private final List<BloodDonation> bloodDonations;

    public BloodDonationList() {
        bloodDonations = new ArrayList<>();
    }

    public void add(BloodDonation b) {
        bloodDonations.add(b);
    }

    public List<BloodDonation> getBloodDonations() {
        return bloodDonations;
    }
}