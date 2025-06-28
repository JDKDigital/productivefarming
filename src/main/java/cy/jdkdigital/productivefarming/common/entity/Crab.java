package cy.jdkdigital.productivefarming.common.entity;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.minecraft.Util;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.IntFunction;

public class Crab extends Animal implements VariantHolder<Crab.Variant>, ProductiveFish
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
        this.setVariant(Crab.Variant.byId(tag.getInt("Variant")));
        if (tag.contains("Age")) {
            this.setAge(tag.getInt("Age"));
        }

        if (tag.contains("HuntingCooldown")) {
            this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, tag.getLong("HuntingCooldown"));
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
        var entity = this.getType().create(pLevel);
        if (entity instanceof Crab crab) {
            Crab.Variant variant;
            if (this.random.nextInt(1200) == 0) {
                variant = Crab.Variant.getRareSpawnVariant(this.random);
            } else {
                variant = this.random.nextBoolean() ? this.getVariant() : ((Crab)pOtherParent).getVariant();
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

    @Override
    public void setVariant(Variant pVariant) {
        this.entityData.set(DATA_VARIANT, pVariant.getId());
    }

    @Override
    public Variant getVariant() {
        return Crab.Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant().getId());
        pCompound.putBoolean("FromBucket", this.fromBucket());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(Crab.Variant.byId(pCompound.getInt("Variant")));
        this.setFromBucket(pCompound.getBoolean("FromBucket"));
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
