package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Hospitals")
public class HospitalList implements Serializable {

    @XStreamImplicit
    private final List<Hospital> hospitals;

    public HospitalList() {
        hospitals = new ArrayList<>();
    }

    public void add(Hospital h) {
        hospitals.add(h);
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }
}