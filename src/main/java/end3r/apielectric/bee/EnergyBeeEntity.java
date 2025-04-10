package end3r.apielectric.bee;

import end3r.apielectric.block.entity.EnergyApiaryBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyBeeEntity extends MobEntity {
    private int honeyChargeGenerated = 100; // How much charge this bee generates per tick or interval

    public EnergyBeeEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        // Check if the bee is near an Energy Apiary or similar energy-generating block
        if (isNearEnergyApiary()) {
            storeHoneyChargeInApiary();
        }
    }

    // Method to check if the bee is near an EnergyApiaryBlockEntity
    private boolean isNearEnergyApiary() {
        // This is a placeholder. In an actual implementation, you would:
        // 1. Get blocks in a radius around the bee
        // 2. Check if any of them are energy apiaries
        // 3. Return true if found, false otherwise

        // For example (simplified):
        BlockPos beePos = this.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = beePos.add(x, y, z);
                    if (this.world.getBlockEntity(checkPos) instanceof EnergyApiaryBlockEntity) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Store HoneyCharge in the EnergyApiary
    private void storeHoneyChargeInApiary() {
        // Get the actual apiary block entity from the world
        EnergyApiaryBlockEntity apiary = getNearbyEnergyApiary();
        if (apiary != null) {
            apiary.addHoneyCharge(honeyChargeGenerated);
        }
    }

    // Get the nearby apiary block entity
    private EnergyApiaryBlockEntity getNearbyEnergyApiary() {
        // This is a placeholder. In an actual implementation, you would:
        // 1. Get blocks in a radius around the bee
        // 2. Find the first energy apiary block entity
        // 3. Return it, or null if none found

        BlockPos beePos = this.getBlockPos();
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = beePos.add(x, y, z);
                    if (this.world.getBlockEntity(checkPos) instanceof EnergyApiaryBlockEntity) {
                        return (EnergyApiaryBlockEntity) this.world.getBlockEntity(checkPos);
                    }
                }
            }
        }
        return null;
    }
}