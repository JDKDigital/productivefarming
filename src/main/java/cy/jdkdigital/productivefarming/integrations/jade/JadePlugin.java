package cy.jdkdigital.productivefarming.integrations.jade;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock;
import cy.jdkdigital.productivefarming.common.block.entity.CropBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = ProductiveFarming.MODID)
public class JadePlugin implements IWailaPlugin
{
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(CropProvider.INSTANCE, CropBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CropProvider.INSTANCE, ProductiveCropBlock.class);
    }
}
