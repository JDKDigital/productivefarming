package cy.jdkdigital.productivefarming.common.block.entity;

import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

// TODO move into lib
public abstract class TickingBlockEntity extends CapabilityBlockEntity
{
    int tickCounter = 0;

    public TickingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    abstract int tickRate();

    public void tickClient(Level level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {};

    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {};

    boolean shouldTick() {
        return tickCounter%tickRate() == 0;
    }

    public void tickHandler(Level level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        tickCounter++;
        if (shouldTick()) {
            tickCounter = 0;
            if (level.isClientSide()) {
                tickClient(level, blockPos, blockState, blockEntity);
            } else {
                tickServer((ServerLevel) level, blockPos, blockState, blockEntity);
            }
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        blockEntity.tickHandler(level, blockPos, blockState, blockEntity);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        output.putInt("tickCounter", tickCounter);
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        tickCounter = input.getIntOr("tickCounter", 0);
    }
}
