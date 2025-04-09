package end3r.apielectric.energy;

public interface IHoneyCharge {
    int getHoneyCharge();             // current energy
    int getMaxHoneyCharge();          // max capacity
    boolean canExtract();             // can energy be extracted?
    boolean canReceive();             // can energy be received?

    /**
     * Add charge. Returns amount accepted.
     */
    int addHoneyCharge(int amount);

    /**
     * Remove charge. Returns amount extracted.
     */
    int extractHoneyCharge(int amount);
}
