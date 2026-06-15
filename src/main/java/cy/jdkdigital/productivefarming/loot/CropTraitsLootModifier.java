package cy.jdkdigital.productivefarming.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivefarming.util.ExternalCropStats;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class CropTraitsLootModifier extends LootModifier
{
    public static final MapCodec<CropTraitsLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, CropTraitsLootModifier::new));

    public CropTraitsLootModifier(LootItemCondition[] conditions, int priority) {
        super(conditions, priority);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> loot, LootContext context) {
        if (!ExternalCropStats.isEnabled()) {
            return loot;
        }
        BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        Vec3 origin = context.getOptionalParameter(LootContextParams.ORIGIN);
        if (state == null || origin == null || !ExternalCropStats.isEligible(state)) {
            return loot;
        }
        ExternalCropStats.applyToLoot(context.getLevel(), BlockPos.containing(origin), state, loot);
        return loot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
