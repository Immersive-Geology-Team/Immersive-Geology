package igteam.api.veins;

import blusunrize.immersiveengineering.api.IEApi;
import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.pattern.BlockPattern;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class OreVeinGatherer {

    public List<IGOreVein> RegisteredVeins;

    public OreVeinGatherer() {
        RegisteredVeins = new ArrayList<>();
    }


    private void initNetherVeins() {
        IGOreVein leadBearingSulfide = new IGOreVein(new ResourceLocation("minecraft:the_nether"))
                .setVeinName("lead_bearing_sulfide")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Galena.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Sphalerite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Vanadinite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 20);

        IGOreVein copperBearingSulfide = new IGOreVein(new ResourceLocation("minecraft:the_nether"))
                .setVeinName("copper_bearing_sulfide")
                .setFailChance(0.1f)
                .setVeinWeight(25)
                .addMineral(MineralEnum.Pyrite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Chalcocite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Chalcopyrite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 20)
                .addMineral(MineralEnum.Acanthite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 20);

        IGOreVein heavyMetalBearingSulfide = new IGOreVein(new ResourceLocation("minecraft:the_nether"))
                .setVeinName("cobalt_bearing_sulfide")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Cobaltite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 30)
                .addMineral(MineralEnum.Ullmannite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 70);

        heavyMetalBearingSulfide.recalculateOutputShares();
        leadBearingSulfide.recalculateOutputShares();
        copperBearingSulfide.recalculateOutputShares();

        RegisteredVeins.add(leadBearingSulfide);
        RegisteredVeins.add(heavyMetalBearingSulfide);
        RegisteredVeins.add(copperBearingSulfide);
    }

    private void initEndVeins() {
        IGOreVein alluvialSediment = new IGOreVein(new ResourceLocation("minecraft:the_end"))
                .setVeinName("fine_alluvial_sediment")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Alumina.getItemTag(BlockPattern.ore, StoneEnum.End_stone.instance()), 40)
                .addMineral(MineralEnum.Anatase.getItemTag(BlockPattern.ore, StoneEnum.End_stone.instance()), 40)
                .addMineral(MineralEnum.Cryolite.getItemTag(BlockPattern.ore, StoneEnum.End_stone.instance()), 20);

        IGOreVein wolframite = new IGOreVein(new ResourceLocation("minecraft:the_end"))
                .setVeinName("ig_wolframite")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Ferberite.getItemTag(BlockPattern.ore, StoneEnum.End_stone.instance()), 40)
                .addMineral(MineralEnum.Hubnerite.getItemTag(BlockPattern.ore, StoneEnum.End_stone.instance()), 40);

        alluvialSediment.recalculateOutputShares();
        wolframite.recalculateOutputShares();
        RegisteredVeins.add(alluvialSediment);
        RegisteredVeins.add(wolframite);
    }

    private void initOverworldVeins() {
        IGOreVein alluvialSediment = new IGOreVein(new ResourceLocation("minecraft:overworld"))
                .setVeinName("alluvial_sediment")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(ItemTags.makeWrapperTag("forge:ores/aluminum"), 40)
                .addMineral(MineralEnum.Ilmenite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 20)
                .addMineral(MineralEnum.Kaolinite.getItemTag(BlockPattern.storage), 40);

        IGOreVein metamorphicIntrusion = new IGOreVein(new ResourceLocation("minecraft:overworld"))
                .setVeinName("metamorphic_intrusion")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Pyrolusite.getItemTag(BlockPattern.ore, StoneEnum.Granite.instance()), 40)
                .addMineral(MineralEnum.Chromite.getItemTag(BlockPattern.ore, StoneEnum.Granite.instance()), 40)
                .addMineral(MineralEnum.Hematite.getItemTag(BlockPattern.ore, StoneEnum.Granite.instance()), 80);

        IGOreVein copperBearingOxides = new IGOreVein(new ResourceLocation("minecraft:overworld"))
                .setVeinName("copper_bearing_oxides")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Cuprite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 40)
                .addMineral(MineralEnum.Hematite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 40);

        IGOreVein tinBearingOxides = new IGOreVein(new ResourceLocation("minecraft:overworld"))
                .setVeinName("tin_bearing_oxides")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Scheelite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 40)
                .addMineral(MineralEnum.Fluorite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 20)
                .addMineral(MineralEnum.Cassiterite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 80);

        IGOreVein deterioratedIgneousRock = new IGOreVein(new ResourceLocation("minecraft:overworld"))
                .setVeinName("deteriorated_igneous_rock")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.Uraninite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 40)
                .addMineral(MineralEnum.Monazite.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 20);

        IGOreVein evaporiteSediment = new IGOreVein(new ResourceLocation("minecraft:overworld"))
                .setVeinName("evaporite_sediment")
                .setFailChance(0.1f)
                .setVeinWeight(15)
                .addMineral(MineralEnum.RockSalt.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 60)
                .addMineral(MineralEnum.SaltPeter.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 40)
                .addMineral(MineralEnum.Gypsum.getItemTag(BlockPattern.ore, StoneEnum.Stone.instance()), 10);


        tinBearingOxides.recalculateOutputShares();
        copperBearingOxides.recalculateOutputShares();
        alluvialSediment.recalculateOutputShares();
        metamorphicIntrusion.recalculateOutputShares();
        deterioratedIgneousRock.recalculateOutputShares();
        evaporiteSediment.recalculateOutputShares();

        RegisteredVeins.add(evaporiteSediment);
        RegisteredVeins.add(deterioratedIgneousRock);
        RegisteredVeins.add(alluvialSediment);
        RegisteredVeins.add(metamorphicIntrusion);
        RegisteredVeins.add(tinBearingOxides);
        RegisteredVeins.add(copperBearingOxides);

    }

    public void initRegisteredVeins() {
        initNetherVeins();
        initEndVeins();
        initOverworldVeins();
    }


}
