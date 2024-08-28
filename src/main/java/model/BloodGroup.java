package model;

import java.util.*;

public enum BloodGroup {

    AB_POS("AB+"), AB_NEG("AB-"), A_POS("A+"), A_NEG("A-"), B_POS("B+"), B_NEG("B-"), O_POS("O+"), O_NEG("O-");

    private final String group;

    BloodGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }

    public static List<BloodGroup> compatibleWith(final BloodGroup bloodGroup) {

        Map<BloodGroup, List<BloodGroup>> bloodCompatibilities = new HashMap<>();

        bloodCompatibilities.put(BloodGroup.AB_POS, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.B_NEG, BloodGroup.B_POS, BloodGroup.A_NEG, BloodGroup.A_POS, BloodGroup.AB_NEG, BloodGroup.AB_POS)
        ));

        bloodCompatibilities.put(BloodGroup.AB_NEG, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.B_NEG, BloodGroup.A_NEG, BloodGroup.AB_NEG)
        ));

        bloodCompatibilities.put(BloodGroup.A_POS, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.A_NEG, BloodGroup.A_POS)
        ));

        bloodCompatibilities.put(BloodGroup.A_NEG, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.A_NEG)
        ));

        bloodCompatibilities.put(BloodGroup.B_POS, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.B_NEG, BloodGroup.B_POS)
        ));

        bloodCompatibilities.put(BloodGroup.B_NEG, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.B_NEG)
        ));

        bloodCompatibilities.put(BloodGroup.O_POS, new ArrayList<>(
                Arrays.asList(BloodGroup.O_NEG, BloodGroup.O_POS)
        ));

        bloodCompatibilities.put(BloodGroup.O_NEG, new ArrayList<>(
                Collections.singletonList(BloodGroup.O_NEG)
        ));

        return bloodCompatibilities.get(bloodGroup);
    }

    @Override
    public String toString() {
        return group;
    }
}
