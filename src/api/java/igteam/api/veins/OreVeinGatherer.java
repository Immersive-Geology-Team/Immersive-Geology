package igteam.api.veins;

import igteam.api.materials.MineralEnum;
import igteam.api.materials.StoneEnum;
import igteam.api.materials.pattern.BlockPattern;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OreVeinGatherer {

    public static OreVeinGatherer INSTANCE = new OreVeinGatherer();
    public List<IGOreVein> RegisteredVeins;

    private OreVeinGatherer()
    {
        RegisteredVeins = new ArrayList<>();
    }

    public void initRegisteredVeins(){
        IGOreVein leadBearingSulfide = new IGOreVein(new ResourceLocation("minecraft:the_nether"))
                .setVeinName("lead_bearing_sulfide")
                .setFailChance(0.1f)
                .setVeinWeight(20)
                .addMineral(MineralEnum.Galena.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Sphalerite.getItemTag(BlockPattern.ore,StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Vanadinite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 20);

       IGOreVein copperBearingSulfide = new IGOreVein(new ResourceLocation("minecraft:the_nether"))
                .setVeinName("copper_bearing_sulfide")
                .setFailChance(0.1f)
                .setVeinWeight(20)
                .addMineral(MineralEnum.Pyrite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Chalcocite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 40)
                .addMineral(MineralEnum.Chalcopyrite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 20)
                .addMineral(MineralEnum.Acanthite.getItemTag(BlockPattern.ore, StoneEnum.Netherrack.instance()), 20);

       leadBearingSulfide.recalculateOutputShares();
       copperBearingSulfide.recalculateOutputShares();
       RegisteredVeins.add(leadBearingSulfide);
       RegisteredVeins.add(copperBearingSulfide);

    }


}
