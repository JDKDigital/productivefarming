package cy.jdkdigital.productivefarming.integrations.ponder;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.registry.ModTags;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class FarmingPonderTags
{
    public static final Identifier FARM_CONTROLLER_BLOCKS = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_controller_blocks");
    public static final Identifier FARM_BUILDING_BLOCKS = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_building_blocks");
    public static final Identifier FARM_FARMLAND = Identifier.fromNamespaceAndPath(ProductiveFarming.MODID, "farm_farmland");

    public static void register(PonderTagRegistrationHelper<Identifier> helper) {
        PonderTagRegistrationHelper<ItemLike> HELPER = helper.withKeyFunction(item -> BuiltInRegistries.ITEM.getKey(item.asItem()));

        helper.registerTag(FARM_CONTROLLER_BLOCKS)
                .addToIndex()
                .item(FarmingRegistrator.FARM_CONTROLLER.get().asItem(), true, false)
                .title("Farm Controllers")
                .description("Controller blocks of the multiblock farm")
                .register();

        helper.registerTag(FARM_BUILDING_BLOCKS)
                .addToIndex()
                .item(Items.STONE_BRICKS, true, false)
                .title("Farm Building Blocks")
                .description("Base blocks used to build the multiblock farm")
                .register();

        helper.registerTag(FARM_FARMLAND)
                .addToIndex()
                .item(Items.FARMLAND, true, false)
                .title("Farmland")
                .description("Farmland used in the multiblock farm")
                .register();

        BuiltInRegistries.ITEM.getTag(ModTags.Items.FARM_WALL_BLOCKS).ifPresent(holders -> {
            holders.forEach(holder -> {
                HELPER.addToTag(FARM_BUILDING_BLOCKS).add(holder.value());
            });
        });
    }
}
