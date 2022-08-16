package igteam.api.veins;

import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.pattern.BlockPattern;
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

    private void initEndVeins(){
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

    public void initRegisteredVeins() {
        initNetherVeins();
        initEndVeins();
    }


}
