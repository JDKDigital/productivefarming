package cy.jdkdigital.productivefarming.common.entity;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.util.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.IntFunction;

public class Crab extends Animal implements ProductiveFish
{
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Crab.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(Crab.class, EntityDataSerializers.BOOLEAN);

    public Crab(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    @Override
    public String getFishName() {
        return "crab";
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, 0);
        builder.define(FROM_BUCKET, false);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean pFromBucket) {
        this.entityData.set(FROM_BUCKET, pFromBucket);
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        Bucketable.saveDefaultDataToBucketTag(this, stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> {
            tag.putInt("Variant", this.getVariant().getId());
            tag.putInt("Age", this.getAge());
            Brain<?> brain = this.getBrain();
            if (brain.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
                tag.putLong("HuntingCooldown", brain.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
            }
        });
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        Bucketable.loadDefaultDataFromBucketTag(this, tag);
        this.setVariant(Crab.Variant.byId(tag.getIntOr("Variant", 0)));
        if (tag.contains("Age")) {
            this.setAge(tag.getIntOr("Age", 0));
        }

        if (tag.contains("HuntingCooldown")) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, tag.getLongOr("HuntingCooldown", 0L));
        }
    }

    @Override
    public ItemStack getBucketItemStack() {
        return getBucket();
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_AXOLOTL;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        var entity = this.getType().create(pLevel, EntitySpawnReason.BREEDING);
        if (entity instanceof Crab crab) {
            Crab.Variant variant;
            if (this.getRandom().nextInt(1200) == 0) {
                variant = Crab.Variant.getRareSpawnVariant(this.getRandom());
            } else {
                variant = this.getRandom().nextBoolean() ? this.getVariant() : ((Crab)pOtherParent).getVariant();
            }

            crab.setVariant(variant);
            crab.setPersistenceRequired();

            return crab;
        }
        return null;
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        return false;
    }

    public void setVariant(Variant pVariant) {
        this.entityData.set(DATA_VARIANT, pVariant.getId());
    }

    public Variant getVariant() {
        return Crab.Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.getVariant().getId());
        output.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setVariant(Crab.Variant.byId(input.getIntOr("Variant", 0)));
        this.setFromBucket(input.getBooleanOr("FromBucket", false));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ModTags.Items.CRAB_FOOD);
    }

    public static enum Variant implements StringRepresentable
    {
        BLUE(0, "blue", true),
        BROWN(1, "brown", true),
        GOLD(2, "gold", false);

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private final int id;
        private final String name;
        private final boolean common;

        Variant(int pId, String pName, boolean pCommon) {
            this.id = pId;
            this.name = pName;
            this.common = pCommon;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }

        public static Variant byId(int id) {
            return BY_ID.apply(id);
        }

        public static Variant getCommonSpawnVariant(RandomSource pRandom) {
            return getSpawnVariant(pRandom, true);
        }

        public static Variant getRareSpawnVariant(RandomSource pRandom) {
            return getSpawnVariant(pRandom, false);
        }

        private static Variant getSpawnVariant(RandomSource pRandom, boolean pCommon) {
            Variant[] variants = Arrays.stream(values()).filter((variant) -> variant.common == pCommon).toArray(Variant[]::new);
            return Util.getRandom(variants, pRandom);
        }
    }
}
