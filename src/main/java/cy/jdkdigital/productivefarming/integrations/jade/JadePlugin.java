package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = ProductiveFarming.MODID)
public class JadePlugin implements IWailaPlugin
{
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CropServerProvider.INSTANCE, CropBlockEntity.class);
        registration.registerBlockDataProvider(ExternalCropServerProvider.INSTANCE, CropBlock.class);
        registration.registerBlockDataProvider(AgriTechPlanterServerProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CropClientProvider.INSTANCE, ProductiveCropBlock.class);
        registration.registerBlockComponent(ExternalCropClientProvider.INSTANCE, CropBlock.class);
        registration.registerBlockComponent(AgriTechPlanterClientProvider.INSTANCE, Block.class);
    }
}
