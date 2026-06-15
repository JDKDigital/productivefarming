package cy.jdkdigital.productivefarming.registry;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.common.attachment.CropTraitStore;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class FarmingAttachments
{
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ProductiveFarming.MODID);

    public static final Supplier<AttachmentType<CropTraitStore>> CROP_TRAITS = ATTACHMENT_TYPES.register("crop_traits",
            () -> AttachmentType.builder(CropTraitStore::new).serialize(CropTraitStore.CODEC, store -> !store.isEmpty()).build());
}
