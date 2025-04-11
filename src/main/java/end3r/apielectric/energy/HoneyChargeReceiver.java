package end3r.apielectric.energy;

/**
 * Interface for blocks that can receive HoneyCharge energy
 */
public interface HoneyChargeReceiver {
    /**
     * Called when this receiver should receive honey charge energy
     *
     * @param amount The amount of honey charge to receive
     */
    void receiveHoneyCharge(int amount);
}