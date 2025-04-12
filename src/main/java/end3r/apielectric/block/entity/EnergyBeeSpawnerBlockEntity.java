package end3r.apielectric.block.entity;

import end3r.apielectric.bee.EnergyBeeEntity;
import end3r.apielectric.registry.ModBlockEntities;
import end3r.apielectric.registry.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class EnergyBeeSpawnerBlockEntity extends BlockEntity {
    private int cooldown = 0;
    private static final int MAX_COOLDOWN = 1200; // 60 seconds (20 ticks per second)
    private static final int SPAWN_RADIUS = 10;
    private static final int MAX_BEES = 10;

    public EnergyBeeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENERGY_BEE_SPAWNER_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, EnergyBeeSpawnerBlockEntity entity) {
        if (world.isClient) {
            return; // Don't run on client side
        }

        // Decrease cooldown
        if (entity.cooldown > 0) {
            entity.cooldown--;
            return;
        }

        // Check the number of energy bees in the area
        int beeCount = entity.countNearbyBees();

        // If there are fewer than MAX_BEES, spawn a new one
        if (beeCount < MAX_BEES) {
            entity.spawnEnergyBee((ServerWorld) world, pos);
            // Reset cooldown
            entity.cooldown = MAX_COOLDOWN;
            entity.markDirty();
        }
    }

    private void spawnEnergyBee(ServerWorld world, BlockPos pos) {
        // Generate random position within spawn radius
        double x = pos.getX() + (world.getRandom().nextDouble() * SPAWN_RADIUS * 2) - SPAWN_RADIUS;
        double y = pos.getY() + world.getRandom().nextDouble() * 2; // Spawn slightly above or at the spawner
        double z = pos.getZ() + (world.getRandom().nextDouble() * SPAWN_RADIUS * 2) - SPAWN_RADIUS;

        // Create the bee
        EnergyBeeEntity bee = ModEntities.ENERGY_BEE.create(world);
        if (bee != null) {
            bee.refreshPositionAndAngles(x, y, z, world.getRandom().nextFloat() * 360f, 0);
            // Set the bee as not having a hive, so it's "wild"
            // Add particle effects at spawn location
            world.spawnParticles(
                    net.minecraft.particle.ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    10,  // number of particles
                    0.5, 0.5, 0.5,  // spread
                    0.1  // speed
            );

            world.spawnEntity(bee);
        }
    }

    public int countNearbyBees() {
        if (world == null || world.isClient) {
            return 0;
        }

        // Create a bounding box centered on this block, with a radius of SPAWN_RADIUS
        Box box = new Box(
                pos.getX() - SPAWN_RADIUS, pos.getY() - SPAWN_RADIUS, pos.getZ() - SPAWN_RADIUS,
                pos.getX() + SPAWN_RADIUS, pos.getY() + SPAWN_RADIUS, pos.getZ() + SPAWN_RADIUS
        );

        // Get a list of all energy bees within the bounding box
        List<EnergyBeeEntity> bees = world.getEntitiesByType(
                ModEntities.ENERGY_BEE,
                box,
                bee -> true // No additional filtering
        );

        return bees.size();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.cooldown = nbt.getInt("Cooldown");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("Cooldown", this.cooldown);
    }
}