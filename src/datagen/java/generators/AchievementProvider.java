package generators;

import blusunrize.immersiveengineering.common.blocks.IEBlocks;
import blusunrize.immersiveengineering.common.items.IEItems;
import blusunrize.immersiveengineering.common.util.IELogger;
import blusunrize.immersiveengineering.common.util.advancements.MultiblockTrigger;
import blusunrize.immersiveengineering.data.Advancements;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import igteam.api.main.IGMultiblockProvider;
import igteam.api.materials.*;
import igteam.api.materials.pattern.FluidPattern;
import igteam.api.materials.pattern.ItemPattern;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.command.FunctionObject;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class AchievementProvider extends AdvancementProvider {
    private final Path OUTPUT;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    //I've no clue here, used IE code as snippet
    public AchievementProvider(DataGenerator gen) {
        super(gen);
        this.OUTPUT = gen.getOutputFolder();
    }

    //Ripped off IE code here
    public void act(DirectoryCache cache) throws IOException {
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path path1 = createPath(this.OUTPUT, advancement);

                try {
                    IDataProvider.save(GSON, cache, advancement.copy().serialize(), path1);
                } catch (IOException var6) {
                    IELogger.error("Couldn't save advancement {}", new Object[]{path1, var6});
                }

            }
        };
        (new AchievementProvider.IGAdvancements()).accept(consumer);
    }

    private static Path createPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    protected static Advancement.Builder advancement(Advancement parent, IItemProvider display, String name, FrameType frame, boolean showToast, boolean announceToChat, boolean hidden) {
        return Advancement.Builder.builder().withParent(parent).withDisplay(display, new TranslationTextComponent("advancement.immersive_geology." + name), new TranslationTextComponent("advancement.immersive_geology." + name + ".desc"), (ResourceLocation)null, frame, showToast, announceToChat, hidden);
    }

    protected static Advancement.Builder advancement(Advancement parent, ItemStack display, String name, FrameType frame, boolean showToast, boolean announceToChat, boolean hidden) {
        return Advancement.Builder.builder().withParent(parent).withDisplay(display, new TranslationTextComponent("advancement.immersive_geology." + name), new TranslationTextComponent("advancement.immersive_geology." + name + ".desc"), (ResourceLocation)null, frame, showToast, announceToChat, hidden);
    }

    protected static AdvancementRewards reward(ResourceLocation loot) {
        return new AdvancementRewards(0, new ResourceLocation[]{loot}, new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
    }


    public static class IGAdvancements implements Consumer<Consumer<Advancement>> {
        public IGAdvancements() {
        }

        public void accept(Consumer<Advancement> consumer) {
            Advancement hclay = Advancement.Builder.builder().
                    withDisplay(MineralEnum.Kaolinite.getItem(ItemPattern.clay),
                            new TranslationTextComponent("advancement.immersive_geology.root"),
                            new TranslationTextComponent("advancement.immersive_geology.root.desc"),
                            new ResourceLocation("immersiveengineering:textures/block/wooden_decoration/treated_wood.png"), //let it be so for now?
                            FrameType.TASK, true, true, false)
                    .withCriterion("hclay", InventoryChangeTrigger.Instance.forItems(MineralEnum.Kaolinite.getItem(ItemPattern.clay))).
                    register(consumer, "immersive_geology:main/root");

            Advancement mb_bloomery = AchievementProvider.advancement(hclay, IGMultiblockProvider.bloomery, "mb_bloomery", FrameType.TASK, true, true, false)
                    .withCriterion("mb_bloomery", InventoryChangeTrigger.Instance.forItems( IGMultiblockProvider.bloomery)).register(consumer, "immersive_geology:main/craft_bloomery");

            Advancement mb_oreseparator = AchievementProvider.advancement(mb_bloomery, (IItemProvider) IGMultiblockProvider.gravityseparator, "mb_oreseparator", FrameType.TASK, true, true, false)
                    .withCriterion("mb_oreseparator", MultiblockTrigger.create(new ResourceLocation("immersive_geology", "multiblocks/gravityseparator"), net.minecraft.advancements.criterion.ItemPredicate.Builder.create().item(IEItems.Tools.hammer).build()))
                    .register(consumer, "immersive_geology:main/mb_oreseparator");

            Advancement mb_hydrojet = AchievementProvider.advancement(mb_bloomery, (IItemProvider) IGMultiblockProvider.hydrojet_cutter, "mb_hydrojet", FrameType.TASK, true, true, false)
                    .withCriterion("mb_hydrojet", MultiblockTrigger.create(new ResourceLocation("immersive_geology", "multiblocks/hydrojet_cutter"), net.minecraft.advancements.criterion.ItemPredicate.Builder.create().item(IEItems.Tools.hammer).build()))
                    .register(consumer, "immersive_geology:main/mb_hydrojet");

            Advancement ore_nether = AchievementProvider.advancement(mb_bloomery, StoneEnum.Netherrack.getItem(ItemPattern.stone_chunk),"ore_nether", FrameType.TASK, true, true, false)
                    .withCriterion("ore_nether", InventoryChangeTrigger.Instance.forItems( StoneEnum.Netherrack.getItem(ItemPattern.stone_chunk))).register(consumer, "immersive_geology:main/ore_nether");

            Advancement mb_rfurnace = AchievementProvider.advancement(ore_nether, (IItemProvider) IGMultiblockProvider.reverberation_furnace, "mb_rfurnace", FrameType.TASK, true, true, false)
                    .withCriterion("mb_rfurnace", MultiblockTrigger.create(new ResourceLocation("immersive_geology", "multiblocks/reverberation_furnace"), net.minecraft.advancements.criterion.ItemPredicate.Builder.create().item(IEItems.Tools.hammer).build()))
                    .register(consumer, "immersive_geology:main/mb_rfurnace");

            Advancement acid_sulfuric = AchievementProvider.advancement(mb_rfurnace, FluidEnum.SulfuricAcid.getItem(ItemPattern.flask),"acid_sulfuric", FrameType.TASK, true, true, false)
                    .withCriterion("acid_sulfuric", InventoryChangeTrigger.Instance.forItems(FluidEnum.SulfuricAcid.getItem(ItemPattern.flask))).register(consumer, "immersive_geology:main/acid_sulfuric");

            Advancement mb_vat = AchievementProvider.advancement(acid_sulfuric, (IItemProvider) IGMultiblockProvider.chemicalvat, "mb_vat", FrameType.TASK, true, true, false)
                    .withCriterion("mb_vat", MultiblockTrigger.create(new ResourceLocation("immersive_geology", "multiblocks/chemicalvat"), net.minecraft.advancements.criterion.ItemPredicate.Builder.create().item(IEItems.Tools.hammer).build()))
                    .register(consumer, "immersive_geology:main/mb_vat");

            Advancement mb_rkiln = AchievementProvider.advancement(acid_sulfuric, (IItemProvider) IGMultiblockProvider.rotarykiln, "mb_rkiln", FrameType.TASK, true, true, false)
                    .withCriterion("mb_rkiln", MultiblockTrigger.create(new ResourceLocation("immersive_geology", "multiblocks/rotarykiln"), net.minecraft.advancements.criterion.ItemPredicate.Builder.create().item(IEItems.Tools.hammer).build()))
                    .register(consumer, "immersive_geology:main/mb_rkiln");

            Advancement aluminium_ingot = AchievementProvider.advancement(mb_rkiln,MetalEnum.Aluminum.getItem(ItemPattern.ingot) ,"aluminium_ingot", FrameType.TASK, true, true, false)
                    .withCriterion("aluminium_ingot", InventoryChangeTrigger.Instance.forItems( MetalEnum.Aluminum.getItem(ItemPattern.ingot))).register(consumer, "immersive_geology:main/aluminium_ingot");

            Advancement mb_crystallizer = AchievementProvider.advancement(mb_vat, (IItemProvider) IGMultiblockProvider.crystallizer, "mb_crystallizer", FrameType.TASK, true, true, false)
                    .withCriterion("mb_crystallizer", MultiblockTrigger.create(new ResourceLocation("immersive_geology", "multiblocks/crystallizer"), net.minecraft.advancements.criterion.ItemPredicate.Builder.create().item(IEItems.Tools.hammer).build()))
                    .register(consumer, "immersive_geology:main/mb_crystallizer");

            Advancement metal_crystal = AchievementProvider.advancement(mb_crystallizer, MetalEnum.Silver.getItem(ItemPattern.crystal) ,"metal_crystal", FrameType.TASK, true, true, false)
                    .withCriterion("metal_crystal", InventoryChangeTrigger.Instance.forItems( new IItemProvider[]{MetalEnum.Silver.getItem(ItemPattern.crystal), MetalEnum.Nickel.getItem(ItemPattern.crystal)} )).register(consumer, "immersive_geology:main/metal_crystal");

        }
    }
}
