package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Donors")
public class DonorList implements Serializable {

    @XStreamImplicit
    private final List<Donor> donors;

    public DonorList() {
        donors = new ArrayList<>();
    }

    public void add(Donor d) {
        donors.add(d);
    }

    public List<Donor> getDonors() {
        return donors;
    }
}