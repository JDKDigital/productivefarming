package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FencedCropBlockEntity extends CropBlockEntity
{
    private BlockState fence;

    public FencedCropBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public BlockState getFence() {
        return this.fence;
    }

    public void setFence(BlockState fence) {
        this.fence = fence;
        this.setChanged();
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("fence")) {
            var fence = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("fence"))).defaultBlockState(); // TODO update state based on neighbors
            fence = fence
                    .setValue(FenceBlock.NORTH, tag.getBoolean("north"))
                    .setValue(FenceBlock.SOUTH, tag.getBoolean("south"))
                    .setValue(FenceBlock.EAST, tag.getBoolean("east"))
                    .setValue(FenceBlock.WEST, tag.getBoolean("west"));
            setFence(fence);
        }
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        if (this.fence != null) {
            tag.putString("fence", BuiltInRegistries.BLOCK.getKey(this.fence.getBlock()).toString());
            tag.putBoolean("north", this.fence.getValue(FenceBlock.NORTH));
            tag.putBoolean("south", this.fence.getValue(FenceBlock.SOUTH));
            tag.putBoolean("east", this.fence.getValue(FenceBlock.EAST));
            tag.putBoolean("west", this.fence.getValue(FenceBlock.WEST));
        }
    }
}
