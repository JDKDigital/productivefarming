package cy.jdkdigital.productivefarming.client.render.entity;

import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;

public class WalleyeRenderer extends CodRenderer
{
    private static final Identifier WALLEYE_LOCATION = Identifier.withDefaultNamespace("textures/entity/fish/cod.png");

    public WalleyeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return WALLEYE_LOCATION;
    }
}
