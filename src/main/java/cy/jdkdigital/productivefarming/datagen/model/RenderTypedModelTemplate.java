package cy.jdkdigital.productivefarming.datagen.model;

import com.google.gson.JsonObject;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class RenderTypedModelTemplate extends ModelTemplate
{
    public static final ModelTemplate ATTACHED_STEM = create("stem_fruit", "cutout", TextureSlot.STEM, TextureSlot.UPPER_STEM);
    public static final ModelTemplate[] STEMS = IntStream.range(0, 8)
            .mapToObj(p_125729_ -> create("stem_growth" + p_125729_, "cutout", "_stage" + p_125729_, TextureSlot.STEM))
            .toArray(ModelTemplate[]::new);

    private final ResourceLocation model;
    private final String renderType;

    public RenderTypedModelTemplate(ResourceLocation model, String renderType, String prefix, TextureSlot... requiredSlots) {
        super(Optional.of(model), prefix.isEmpty() ? Optional.empty() : Optional.of(prefix), requiredSlots);
        this.model = model;
        this.renderType = renderType;
    }

    @Override
    public JsonObject createBaseTemplate(ResourceLocation modelLocation, Map<TextureSlot, ResourceLocation> modelGetter) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("parent", this.model.toString());
        if (!modelGetter.isEmpty()) {
            JsonObject jsonobject1 = new JsonObject();
            modelGetter.forEach((p_176457_, p_176458_) -> jsonobject1.addProperty(p_176457_.getId(), p_176458_.toString()));
            jsonobject.add("textures", jsonobject1);
        }
        jsonobject.addProperty("render_type", renderType);

        return jsonobject;
    }

    private static ModelTemplate create(String blockModelLocation, String renderType, TextureSlot... requiredSlots) {
        return create(blockModelLocation, renderType, "", requiredSlots);
    }

    private static ModelTemplate create(String blockModelLocation, String renderType, String prefix, TextureSlot... requiredSlots) {
        return new RenderTypedModelTemplate(ResourceLocation.withDefaultNamespace("block/" + blockModelLocation), renderType, prefix, requiredSlots);
    }
}
