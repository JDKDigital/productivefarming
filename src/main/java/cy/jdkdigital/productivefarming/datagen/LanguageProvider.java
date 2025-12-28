package cy.jdkdigital.productivefarming.datagen;

import cy.jdkdigital.productivefarming.ProductiveFarming;
import cy.jdkdigital.productivefarming.registry.FarmingRegistrator;
import cy.jdkdigital.productivefarming.util.TraitsHelper;
import cy.jdkdigital.productivelib.util.LangUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.MobBucketItem;

import java.util.HashMap;
import java.util.Map;

public class LanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider
{
    public LanguageProvider(PackOutput output) {
        super(output, ProductiveFarming.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + ProductiveFarming.MODID, "Productive Farming");
        add("jei." + ProductiveFarming.MODID + ".crop_fruiting", "Crop Fruiting");
        add("jei." + ProductiveFarming.MODID + ".crop_mutation", "Crop Mutation");
        add("jade." + ProductiveFarming.MODID + ".mutation", "Mutation: %s");
        add("jade." + ProductiveFarming.MODID + ".mutation_harvest", "Right click to safely harvest the mutated crop");
        add("config.jade.plugin_" + ProductiveFarming.MODID + ".crop", "Crops");
        add(ProductiveFarming.MODID + ".screen.progress", "Progress: %s");
        add(ProductiveFarming.MODID + ".message.farm_formed", "Farm structure assembled with height %s");
        add(ProductiveFarming.MODID + ".message.farm_invalid", "Farm structure invalid. %s");

        add(ProductiveFarming.MODID + ".devices.farm_controller", "Multiblock Farm");
        add(ProductiveFarming.MODID + ".information.upgrade.upgrade_stability.farm_controller", "Strips stats from collected crops.");
        add(ProductiveFarming.MODID + ".information.upgrade.upgrade_time.farm_controller", "Decreases time between harvesting attempts, use with care.\n   Multiple upgrades can be installed.");
        add(ProductiveFarming.MODID + ".information.upgrade.upgrade_time_2.farm_controller", "It's twice as good as the other one.");

        add("block." + ProductiveFarming.MODID + ".watering_trough.tooltip", "Increases growth speed of nearby animals");
        add("block." + ProductiveFarming.MODID + ".feeding_trough.tooltip", "Makes nearby animals breed");
        add(ProductiveFarming.MODID + ".pollen.name", "%s");
        add(ProductiveFarming.MODID + ".information.pollen", "Use on a fully grown crop to manually pollinate it.");
        add(ProductiveFarming.MODID + ".tooltip.extend", "Hold [SHIFT] for more info");
        add(ProductiveFarming.MODID + ".tooltip.fencepost_crop", "Must be planted on a fence post");
        add(ProductiveFarming.MODID + ".tooltip.double_crop", "Requires two blocks of space to grow.");
        add(ProductiveFarming.MODID + ".tooltip.mushroom_crop", "Composting this has a chance of growing new shrooms on the side of the composter.");
        add(ProductiveFarming.MODID + ".trait." + TraitsHelper.GROWTH, "Growth speed: %s");
        add(ProductiveFarming.MODID + ".trait." + TraitsHelper.YIELD, "Yield: %s");
        add(ProductiveFarming.MODID + ".trait." + TraitsHelper.RESISTANCE, "Resistance: %s");
        add(ProductiveFarming.MODID + ".trait." + TraitsHelper.MUTABILITY, "Mutability: %s");
        add(ProductiveFarming.MODID + ".trait_value.none", "None");
        add(ProductiveFarming.MODID + ".trait_value.low", "Low");
        add(ProductiveFarming.MODID + ".trait_value.medium", "Medium");
        add(ProductiveFarming.MODID + ".trait_value.high", "High");
        add(ProductiveFarming.MODID + ".trait_value.very_high", "Very High");

        ProductiveFarming.BLOCKS.getEntries().forEach(holder -> {
            var regName = BuiltInRegistries.BLOCK.getKey(holder.get()).getPath();
            if (regName.equals("ferula_crate")) {
                add(holder.get(), "Asafoetida Crate");
            } else {
                add(holder.get(), LangUtil.capName(BuiltInRegistries.BLOCK.getKey(holder.get()).getPath()));
            }
        });
        ProductiveFarming.ITEMS.getEntries().forEach(holder -> {
            if (holder.get() instanceof MobBucketItem) {
                add(holder.get(), "Bucket of " + capName(BuiltInRegistries.ITEM.getKey(holder.get()).getPath().replace("_bucket", "")));
            } else if (!(holder.get() instanceof BlockItem) || holder.get() instanceof ItemNameBlockItem) {
                var regName = BuiltInRegistries.ITEM.getKey(holder.get()).getPath();
                if (regName.contains("grape_seeds")) {
                    add(holder.get(), LangUtil.capName(regName.replace("seeds", "propagule")));
                } else if (regName.contains("sarsaparilla_vine_seeds")) {
                    add(holder.get(), LangUtil.capName(regName.replace("seeds", "roots")));
                } else if (regName.equals("ferula")) {
                    add(holder.get(), "Asafoetida");
                }  else {
                    add(holder.get(), LangUtil.capName(regName));
                }
            }
        });
        ProductiveFarming.ENTITY_TYPES.getEntries().forEach(entityTypeRegistryObject -> {
            add(entityTypeRegistryObject.get(), capName(BuiltInRegistries.ENTITY_TYPE.getKey(entityTypeRegistryObject.get()).getPath()));
        });

        FarmingRegistrator.CROPS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.VANILLA_CROPS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.GRAPES.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.BERRIES.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.HERBS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.STEMS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.TRELLIS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.VERTICAL_TRELLIS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });
        FarmingRegistrator.SHROOMS.forEach(crop -> {
            add("tooltip." + ProductiveFarming.MODID + "." + crop.name() + ".latin", getLatinName(crop.name()));
        });

        add("tooltip." + ProductiveFarming.MODID + ".red_mushroom.latin", getLatinName("red_mushroom"));
        add("tooltip." + ProductiveFarming.MODID + ".brown_mushroom.latin", getLatinName("brown_mushroom"));
        add("tooltip." + ProductiveFarming.MODID + ".warped_fungus.latin", getLatinName("warped_fungus"));
        add("tooltip." + ProductiveFarming.MODID + ".crimson_fungus.latin", getLatinName("crimson_fungus"));

        // Ponder
        add(ProductiveFarming.MODID + ".ponder.farm_building.header", "Multiblock Farms Structure");
        add(ProductiveFarming.MODID + ".ponder.farm_building.text_1", "The minimum size for the farm is 3x3, corners are optional");
        add(ProductiveFarming.MODID + ".ponder.farm_building.text_2", "The inside of the farm can be any block you need for your crops");
        add(ProductiveFarming.MODID + ".ponder.farm_building.text_3", "Additionally, the farm multiblock must have a Farm Controller...");
        add(ProductiveFarming.MODID + ".ponder.farm_farming.header", "Multiblock Farming");
        add(ProductiveFarming.MODID + ".ponder.farm_farming.text_1", "Crops planted on the farm will be automatically harvested");
        add(ProductiveFarming.MODID + ".ponder.farm_farming.text_2", "When crops grow they have a small chance to improve their traits");
        add(ProductiveFarming.MODID + ".ponder.fish_farm.header", "Fish farms");
        add(ProductiveFarming.MODID + ".ponder.fish_farm.text_1", "If you make your farm taller you can use it as a fish farm");
        add(ProductiveFarming.MODID + ".ponder.fish_farm.text_2", "Creatures inside the farm boundaries will be automatically bred and slaughtered");
        add(ProductiveFarming.MODID + ".ponder.fish_farm.text_3", "Fish will produce nutrient rich water which can be pumped to a crop farm for increased growth");
        add(ProductiveFarming.MODID + ".ponder.fish_farm.text_4", "You can also combine the two farms and have an aquaponics system");
    }

    @Override
    public String getName() {
        return "Productive Farming translation provider";
    }

    private String capName(String name) {
        String[] nameParts = name.split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts) + "s";
    }

    private static String getLatinName(String name) {
        Map<String, String> names = new HashMap<>() {{
            put("wheat", "Triticum aestivum");
            put("potato", "Solanum tuberosum");
            put("carrot", "Daucus carota");
            put("beetroot", "Beta vulgaris");

            put("kadsura", "Kadsura japonica");
            put("blackberry", "Rubus fruticosus");
            put("blackcurrant", "Ribes nigrum");
            put("blueberry", "Vaccinium myrtillus");
            put("boysenberry", "Rubus fruticosus × Rubus idaeus");
            put("cloudberry", "Rubus chamaemorus");
            put("checkerberry", "Gaultheria procumbens");
            put("cranberry", "Vaccinium oxycoccos");
            put("golden_raspberry", "Rubus idaeus 'All Gold'");
            put("gooseberry", "Ribes uva-crispa");
            put("huckleberry", "Vaccinium ovatum");
            put("lingoberry", "Vaccinium vitis-idaea");
            put("miracle_berry", "Synsepalum dulcificum");
            put("raspberry", "Rubus idaeus");
            put("redcurrant", "Ribes rubrum");
            put("thimbleberry", "Rubus parviflorus");
            put("agave", "Agave tequilana");
            put("basil", "Ocimum basilicum");
            put("blue_borage", "Borago officinalis");
            put("cardamon", "Elettaria cardamomum");
            put("catnip", "Nepeta cataria");
            put("caraway", "Carum carvi");
            put("chamomile", "Matricaria chamomilla");
            put("chives", "Allium schoenoprasum");
            put("coriander", "Coriandrum sativum");
            put("cotton", "Gossypium arboreum");
            put("cumin", "Cuminum cyminum");
            put("dill", "Anethum graveolens");
            put("mint", "Mentha spicata");
            put("oregano", "Origanum vulgare");
            put("parsley", "Petroselinum crispum");
            put("rosemary", "Salvia rosmarinus");
            put("sage", "Salvia officinalis");
            put("fat_hen", "Chenopodium album");
            put("ferula", "Ferula assa-foetida");
            put("fenugreek", "Trigonella foenum-graecum");
            put("flax", "Linum usitatissimum");
            put("ostrich_fiddlehead", "Matteuccia struthiopteris");
            put("cantaloupe", "Cucumis melo var. Cantalupensis");
            put("honeydew_melon", "Cucumis melo var. Inodorus");
            put("red_grape", "Vitis vinifera");
            put("fox_grape", "Vitis labrusca");
            put("concord_grape", "Vitis labrusca x Vitis vinifera");
            put("cotton_candy_grape", "IFG Seven");
            put("green_grape", ""); // https://en.wikipedia.org/wiki/List_of_grape_varieties#Vitis_labrusca_(wine_and_table)
            put("butternut_squash", "Cucurbita moschata");
            put("spoon_gourd", "Cucurbita pepo var. ovifera");
            put("luffa", "Luffa aegyptiaca");
            put("cucumber", "Cucumis sativus");
            put("zucchini", "Cucurbita pepo var. longa");
            put("kiwi", "Actinidia deliciosa");
            put("hops", "Humulus lupulus");
            put("vanilla", "Vanilla planifolia");
            put("akebia", "Akebia quinata");
            put("goji_berry", "Lycium barbarum");
            put("arrowroot", "Maranta arundinacea");
            put("arugula", "Eruca sativa");
            put("green_bell_pepper", "");
            put("orange_bell_pepper", "");
            put("red_bell_pepper", "");
            put("yellow_bell_pepper", "");
            put("black_bell_pepper", "");
            put("purple_bell_pepper", "");
            put("white_bell_pepper", "");
            put("black_beans", "");
            put("bok_choy", "Brassica rapa s. chinensis");
            put("broccoli", "Brassica oleracea var. italica");
            put("brussels_sprout", "Brassica oleracea var. gemmifera");
            put("burdock_root", "Arctium lappa");
            put("butterhead_lettuce", "Lactuca sativa var. capitata");
            put("cabbage", "Brassica oleracea var. capitata f. alba");
            put("cauliflower", "Brassica oleracea var. botrytis");
            put("cassava", "Manihot esculenta");
            put("celery", "Apium graveolens var. dulce");
            put("chard", "Beta vulgaris subsp. vulgaris var. cicla");
            put("chili_pepper", "");
            put("collard", "Brassica oleracea var. viridis");
            put("daikon", "Raphanus sativus var. longipinnatus");
            put("eddoe", "Colocasia antiquorum");
            put("eggplant", "Solanum melongena");
            put("endive", "Cichorium intybus");
            put("garlic", "Allium sativum");
            put("iceberg_lettuce", "Lactuca sativa var. capitata");
            put("jalapeno", "Capsicum annuum");
            put("jute", "Corchorus capsularis");
            put("kale", "Brassica oleracea var. sabellica");
            put("kidney_beans", "Phaseolus vulgaris");
            put("kohlrabi", "Brassica oleracea var. gongylodes");
            put("leek", "Allium ampeloprasum var. porrum");
            put("lima_beans", "Phaseolus lunatus");
            put("onion", "Allium cepa");
            put("parsnip", "Pastinaca sativa");
            put("peas", "Lathyrus oleraceus");
            put("pinto_beans", "Phaseolus vulgaris");
            put("radish", "Raphanus sativus");
            put("rhubarb", "Rheum rhabarbarum");
            put("romain_lettuce", "Lactuca sativa var. longifolia");
            put("rutabaga", "Brassica napus var. napobrassica");
            put("salsify", "Tragopogon porrifolius");
            put("spinach", "Spinacia oleracea");
            put("strawberry", "Fragaria × ananassa");
            put("sugar_beet", "Beta vulgaris var. saccharifera");
            put("turnip", "Brassica rapa var. rapa");
            put("ulluco", "Ullucus tuberosus");
            put("wasabi", "Eutrema japonicum");
            put("yam", "Dioscorea cayenensis");
            put("peanuts", "Arachis hypogaea");
            put("ginger", "Zingiber officinale");
            put("green_bean", "Phaseolus vulgaris");
            put("spring_onion", "Allium fistulosum");
            put("saguaro", "Carnegiea gigantea");
            put("squash", "Cucurbita Pepo");
            put("sarsaparilla", "Smilax ornata");
            put("sweet_potato", "Ipomoea batatas");
            put("lentils", "Vicia lens");
            put("chickpeas", "Cicer arietinum");
            put("mustard", "Brassica juncea");
            put("black_pepper", "Piper nigrum");
            put("turmeric", "Curcuma longa");
            put("rice", "Oryza sativa");
            put("oats", "Avena sativa");
            put("barley", "Hordeum vulgare");
            put("rye", "Secale cereale");
            put("amaranth", "Amaranthus blitum");
            put("teff", "Eragrostis tef");
            put("pitaya", "Selenicereus undatus");
            put("monstera_deliciosa", "Monstera deliciosa");
            put("tobacco", "Nicotiana tabacum");
            put("tea", "Camellia sinensis");
            put("pineapple", "Ananas comosus");
            put("quinoa", "Chenopodium quinoa");
            put("yellow_dent_corn", "Zea mays convar. saccharata var. rugosa"); // TODO correct names for corn variants
            put("sugar_pearl_corn", "Zea mays convar. saccharata var. rugosa");
            put("rainbow_corn", "Zea mays convar. saccharata var. rugosa");
            put("blue_jade_corn", "Zea mays convar. saccharata var. rugosa");
            put("black_aztec_corn", "Zea mays convar. saccharata var. rugosa");
            put("tomatillo", "Physalis philadelphica");
            put("thyme", "Thymus vulgaris");
            put("beefsteak_tomato", "Solanum lycopersicum var. coustralee");
            put("black_beauty_tomato", "Solanum lycopersicum var. gates");
            put("blue_beauty_tomato", "Solanum lycopersicum");
            put("chocolate_pear_tomato", "Solanum lycopersicum var. terrior");
            put("sungold_tomato", "Solanum lycopersicum");
            put("white_wonder_tomato", "Solanum lycopersicum");
            put("yellow_pear_tomato", "Solanum lycopersicum");
            put("roma_tomato", "Solanum lycopersicum var. roma");
            put("cherry_tomato", "Solanum lycopersicum var. cerasiforme");
            put("konjac", "Amorphophallus konjac");
            put("okra", "Abelmoschus esculentus");
            put("asparagus", "Asparagus officinalis");
            put("artichoke", "Cynara cardunculus var. scolymus");
            put("malanga", "Xanthosoma sagittifolium");
            put("water_chestnut", "Eleocharis dulcis");
            put("watercress", "Nasturtium officinale");
            put("water_caltrop", "Trapa natans");
            put("prickly_pear", "Opuntia ficus-indica");
            put("sea_buckthorn", "Hippophae rhamnoides");
            put("soy_bean", "Glycine max");
            put("sweet_marjoram", "Origanum majorana");
            put("sorghum", "Sorghum bicolor");
            put("spearmint", "Mentha spicata");
            put("peppermint", "Mentha × piperita");
            put("watermint", "Mentha aquatica");
            put("lemon_balm", "Melissa officinalis");
            put("lemongrass", "Cymbopogon schoenanthus");
            put("wintergreen", "Gaultheria procumbens");
            // Shrooms
            put("red_mushroom", "Amanita muscaria");
            put("brown_mushroom", "Boletus edulis");
            put("warped_fungus", "Fungus contortus");
            put("crimson_fungus", "Fungus coccinus");
            put("black_truffle", "Tuber melanosporum");
            put("chanterelle", "Cantharellus cibarius");
            put("laetiporus", "Laetiporus speciosus");
            put("lions_mane", "Hericium erinaceus");
            put("morel", "Morchella esculenta");
            put("oyster_mushroom", "Pleurotus ostreatus");
            put("porcini", "Boletus edulis");
            put("portobello", "Agaricus bisporus");
            put("shiitake", "Lentinula edodes");
        }};

        return names.getOrDefault(name, "Missing taxonomy for " + name);
    }
}
