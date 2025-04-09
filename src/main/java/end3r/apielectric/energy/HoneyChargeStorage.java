package end3r.apielectric.energy;

public class HoneyChargeStorage implements IHoneyCharge {
    private int charge;
    private final int capacity;
    private final boolean canExtract;
    private final boolean canReceive;

    public HoneyChargeStorage(int capacity, boolean canExtract, boolean canReceive) {
        this.capacity = capacity;
        this.canExtract = canExtract;
        this.canReceive = canReceive;
        this.charge = 0;
    }

    @Override
    public int getHoneyCharge() {
        return charge;
    }

    @Override
    public int getMaxHoneyCharge() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return canExtract;
    }

    @Override
    public boolean canReceive() {
        return canReceive;
    }

    @Override
    public int addHoneyCharge(int amount) {
        if (!canReceive) return 0;
        int accepted = Math.min(amount, capacity - charge);
        charge += accepted;
        return accepted;
    }

    @Override
    public int extractHoneyCharge(int amount) {
        if (!canExtract) return 0;
        int extracted = Math.min(amount, charge);
        charge -= extracted;
        return extracted;
    }
}
