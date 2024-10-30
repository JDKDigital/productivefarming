package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.util.CropConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import java.util.function.Supplier;

public class ProductiveCropBlock extends CropBlock implements IAgeableCropBlock
{
    protected final Supplier<Item> itemSupplier;
    private final CropConfig crop;

    public ProductiveCropBlock(CropConfig crop, Properties pProperties) {
        super(pProperties);
        this.itemSupplier = () -> BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(this).withPath(p -> crop.hasSeed() ? p + "_seeds" : p));
        this.crop = crop;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.itemSupplier.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(getAgeProperty());
    }

    @Override
    protected int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.random, getMaxAge() > 4 ? 2 : 1, getMaxAge() < 4 ? 2 : 5);
    }
}
