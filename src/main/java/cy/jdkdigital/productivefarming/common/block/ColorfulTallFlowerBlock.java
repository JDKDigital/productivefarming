package cy.jdkdigital.productivefarming.common.block;

import cy.jdkdigital.productivefarming.common.block.entity.ColorfulFlowerBlockEntity;
import cy.jdkdigital.productivefarming.registry.FarmingDataComponents;
import cy.jdkdigital.productivefarming.util.FarmUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ColorfulTallFlowerBlock extends TallFlowerBlock implements EntityBlock, IColorfulFlowerBlock
{
    private final int defaultColor;

    public ColorfulTallFlowerBlock(Properties properties, int defaultColor) {
        super(properties);
        this.defaultColor = defaultColor;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ColorfulFlowerBlockEntity(pos, state, this.defaultColor);
    }

    @Override
    public int getDefaultColor() {
        return this.defaultColor;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos blockpos = pos.above();
        level.setBlock(blockpos, copyWaterloggedFrom(level, blockpos, this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)), Block.UPDATE_ALL);
        if (level.getBlockEntity(blockpos) instanceof ColorfulFlowerBlockEntity topFlowerBlockEntity && level.getBlockEntity(pos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
            topFlowerBlockEntity.setColor(flowerBlockEntity.getColor());
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                preventDropFromBottomPart(level, pos, state, player);
            } else {
                popResource(level, pos, getDrop(level, pos));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return getDrop(level, pos);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        popResource(level, pos, getDrop(level, pos));
    }

    private ItemStack getDrop(LevelReader level, BlockPos pos) {
        var stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof ColorfulFlowerBlockEntity flowerBlockEntity) {
            stack.set(FarmingDataComponents.COLOR, flowerBlockEntity.getColor());
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable(FarmUtil.getDyeFromColor(stack.getOrDefault(FarmingDataComponents.COLOR, this.defaultColor)).getDescriptionId()).withColor(stack.getOrDefault(FarmingDataComponents.COLOR, this.defaultColor)).withStyle(ChatFormatting.ITALIC));
    }
}
