package cy.jdkdigital.productivefarming.client.render.entity;

import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cod;

public class WalleyeRenderer extends CodRenderer
{
    public WalleyeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Cod pEntity) {
        return super.getTextureLocation(pEntity); // TODO
    }
}
