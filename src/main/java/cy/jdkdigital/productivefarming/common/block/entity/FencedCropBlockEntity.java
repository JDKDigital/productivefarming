package cy.jdkdigital.productivefarming.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        input.getString("fence").ifPresent(fenceId -> {
            var fence = BuiltInRegistries.BLOCK.getValue(Identifier.parse(fenceId)).defaultBlockState();
            fence = fence
                    .setValue(FenceBlock.NORTH, input.getBooleanOr("north", false))
                    .setValue(FenceBlock.SOUTH, input.getBooleanOr("south", false))
                    .setValue(FenceBlock.EAST, input.getBooleanOr("east", false))
                    .setValue(FenceBlock.WEST, input.getBooleanOr("west", false));
            setFence(fence);
        });
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        if (this.fence != null) {
            output.putString("fence", BuiltInRegistries.BLOCK.getKey(this.fence.getBlock()).toString());
            output.putBoolean("north", this.fence.getValue(FenceBlock.NORTH));
            output.putBoolean("south", this.fence.getValue(FenceBlock.SOUTH));
            output.putBoolean("east", this.fence.getValue(FenceBlock.EAST));
            output.putBoolean("west", this.fence.getValue(FenceBlock.WEST));
        }
    }
}
