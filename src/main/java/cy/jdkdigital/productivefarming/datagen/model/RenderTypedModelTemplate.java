package cy.jdkdigital.productivefarming.datagen.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class RenderTypedModelTemplate extends ModelTemplate
{
    // TODO move to lib
    public static final ModelTemplate ATTACHED_STEM = create("stem_fruit", "cutout", TextureSlot.STEM, TextureSlot.UPPER_STEM);
    public static final ModelTemplate CROSS = create("cross", "cutout", TextureSlot.CROSS);
    public static final ModelTemplate CROP = create("crop", "cutout", TextureSlot.CROP);
    public static final ModelTemplate[] STEMS = IntStream.range(0, 8)
            .mapToObj(p -> create("stem_growth" + p, "cutout", "_stage" + p, TextureSlot.STEM))
            .toArray(ModelTemplate[]::new);

    private final Identifier model;
    private final String renderType;

    public RenderTypedModelTemplate(Identifier model, String renderType, String suffix, TextureSlot... requiredSlots) {
        super(Optional.of(model), suffix.isEmpty() ? Optional.empty() : Optional.of(suffix), requiredSlots);
        this.model = model;
        this.renderType = renderType;
    }

    @Override
    public JsonObject createBaseTemplate(Identifier target, Map<TextureSlot, Material> slots) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("parent", this.model.toString());
        if (!slots.isEmpty()) {
            JsonObject textures = new JsonObject();
            slots.forEach((slot, value) -> {
                JsonElement valueJson = Material.CODEC.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
                textures.add(slot.getId(), valueJson);
            });
            jsonobject.add("textures", textures);
        }
        jsonobject.addProperty("render_type", renderType);
        return jsonobject;
    }

    private static ModelTemplate create(String blockModelLocation, String renderType, TextureSlot... requiredSlots) {
        return create(blockModelLocation, renderType, "", requiredSlots);
    }

    private static ModelTemplate create(String blockModelLocation, String renderType, String suffix, TextureSlot... requiredSlots) {
        return new RenderTypedModelTemplate(Identifier.withDefaultNamespace("block/" + blockModelLocation), renderType, suffix, requiredSlots);
    }
}
