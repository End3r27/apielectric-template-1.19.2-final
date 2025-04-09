package end3r.apielectric.bee;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import net.minecraft.entity.ai.goal.Goal;

public class EnergyBeeEntity extends BeeEntity {

    // This variable will store whether the bee is pollinating
    private boolean isPollinating;

    public EnergyBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        this.isPollinating = false;  // Default value when not pollinating
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        // Example of adding a goal for pollination behavior
        this.goalSelector.add(1, new PollinateGoal(this));
    }

    // Define isPollinating method to access the pollinating state
    public boolean isPollinating() {
        return isPollinating;
    }

    // Set the pollinating state (useful for goal handling)
    public void setPollinating(boolean pollinating) {
        this.isPollinating = pollinating;
    }

    // Custom goal for pollination (basic example)
    public static class PollinateGoal extends Goal {
        private final EnergyBeeEntity bee;

        public PollinateGoal(EnergyBeeEntity bee) {
            this.bee = bee;
        }

        @Override
        public boolean canStart() {
            return !this.bee.isPollinating() && this.bee.getTarget() == null; // Example check
        }

        @Override
        public void start() {
            // Example of changing state to pollinating
            this.bee.setPollinating(true);
        }

        @Override
        public void stop() {
            // Example of stopping the pollination
            this.bee.setPollinating(false);
        }

        @Override
        public void tick() {
            // Example logic for pollination behavior
            if (this.bee.isPollinating()) {
                // Pollination logic goes here (e.g., check if near flowers)
            }
        }
    }
}
