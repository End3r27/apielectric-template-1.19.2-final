package end3r.apielectric.energy;

public class HoneyChargeUtils {
    public static boolean transfer(IHoneyCharge from, IHoneyCharge to, int maxTransfer) {
        if (!from.canExtract() || !to.canReceive()) return false;

        int extractable = Math.min(from.getHoneyCharge(), maxTransfer);
        int accepted = to.addHoneyCharge(extractable);
        from.extractHoneyCharge(accepted);
        return accepted > 0;
    }
}
