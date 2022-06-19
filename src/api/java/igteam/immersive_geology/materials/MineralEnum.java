package igteam.immersive_geology.materials;

import igteam.immersive_geology.config.IGOreConfig;
import igteam.immersive_geology.materials.data.MaterialBase;
import igteam.immersive_geology.materials.data.mineral.MaterialBaseMineral;
import igteam.immersive_geology.materials.data.mineral.variants.*;
import igteam.immersive_geology.materials.helper.CrystalFamily;
import igteam.immersive_geology.materials.helper.MaterialInterface;
import igteam.immersive_geology.materials.pattern.BlockPattern;
import igteam.immersive_geology.materials.pattern.FluidPattern;
import igteam.immersive_geology.materials.pattern.ItemPattern;
import igteam.immersive_geology.materials.pattern.MaterialPattern;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import igteam.immersive_geology.processing.IGProcessingStage;

import java.util.Set;

public enum MineralEnum implements MaterialInterface<MaterialBaseMineral> {
    Alumina(new MaterialMineralAlumina()),
    Anatase(new MaterialMineralAnatase()),
    Acanthite(new MaterialMineralAcanthite()),
    Chalcocite(new MaterialMineralChalcocite()),
    Cuprite(new MaterialMineralCuprite()),
 //   Zircon(new MaterialMineralZircon()),
    Monazite(new MaterialMineralMonazite()),
    Ilmenite(new MaterialMineralIlmenite()),
    Cobaltite(new MaterialMineralCobaltite()),
    Cassiterite(new MaterialMineralCassiterite()),
    Chalcopyrite(new MaterialMineralChalcopyrite()),
    Chromite(new MaterialMineralChromite()),
    Cryolite(new MaterialMineralCryolite()),
    Ferberite(new MaterialMineralFerberite()),
    Fluorite(new MaterialMineralFluorite()),  //TODO Immersive Engineering has trouble with this as it's looking for a GEM version under the tag forge:gem/fluorite
    Gypsum(new MaterialMineralGypsum()),
    Hematite(new MaterialMineralHematite()),
    Hubnerite(new MaterialMineralHubnerite()),
    Pyrolusite(new MaterialMineralPyrolusite()),
    RockSalt(new MaterialMineralRockSalt()),
    SaltPeter(new MaterialMineralSaltpeter()),
    Thorianite(new MaterialMineralThorianite()),
    Thorite(new MaterialMineralThorite()),
    Uraninite(new MaterialMineralUraninite()),
    Sphalerite(new MaterialMineralSphalerite()),
    Smithsonite(new MaterialMineralSmithsonite()),
    Ullmannite(new MaterialMineralUllmannite()),
    Galena(new MaterialMineralGalena()),
    Pyrite(new MaterialMineralPyrite()),
    Vanadinite(new MaterialMineralVanadinite()),
    Kaolinite(new MaterialMineralKaolinite()),
    Unobtania(new MaterialMineralUnobtania());

    private final MaterialBaseMineral material;

    MineralEnum(MaterialBaseMineral m){
        this.material = m;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern) {
        return material.getStack(pattern);
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        ItemStack i = material.getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        ItemStack i = material.getStack(pattern, secondaryMaterial);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, int amount) {
        return material.getStack(pattern, amount);
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        ItemStack i = material.getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material, amount) : i;
    }

    @Override
    public ItemStack getStack(MaterialPattern pattern, MaterialBase secondaryMaterial, int amount) {
        ItemStack i = material.getStack(pattern, secondaryMaterial, amount);
        return i.isEmpty() ? secondaryMaterial.getStack(pattern, material, amount) : i;
    }

    @Override
    public FluidStack getFluidStack(MaterialPattern pattern, int amount) {
        return material.getFluidStack(pattern, amount);
    }

    @Override
    public FluidStack getFluidStack(MaterialPattern pattern, MaterialInterface secondaryMaterial, int amount) {
        return material.getFluidStack(pattern, secondaryMaterial, amount);
    }


    @Override
    public Item getItem(MaterialPattern pattern) {
        return material.getItem((ItemPattern) pattern);
    }

    @Override
    public Item getItem(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Item i = material.getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, material) : i;
    }

    @Override
    public Item getItem(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Item i = material.getItem(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getItem(pattern, material) : i;
    }

    @Override
    public Block getBlock(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        Block i = material.getBlock(pattern, secondaryMaterial);
        return i == null ? secondaryMaterial.getBlock(pattern, material) : i;
    }

    @Override
    public Block getBlock(MaterialPattern pattern, MaterialBase secondaryMaterial) {
        Block b = material.getBlock(pattern, secondaryMaterial);
        return b == null ? secondaryMaterial.getBlock(pattern, material) : b;
    }

    @Override
    public ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern) {
        return material.getFluidTag(pattern);
    }

    @Override
    public ITag.INamedTag<Fluid> getFluidTag(FluidPattern pattern, MaterialBase... materials) {
        return material.getFluidTag(pattern, materials);
    }

    @Override
    public Fluid getFluid(MaterialPattern pattern) {
        return material.getFluid(pattern);
    }

    @Override
    public Fluid getFluid(MaterialPattern pattern, MaterialInterface secondaryMaterial) {
        return material.getFluid(pattern, secondaryMaterial);
    }

    @Override
    public Block getBlock(MaterialPattern pattern) {
        return material.getBlock((BlockPattern) pattern);
    }

    @Override
    public ITag.INamedTag<?> getTag(MaterialPattern pattern) {
        return material.getTag(pattern);
    }

    @Override
    public boolean hasPattern(MaterialPattern pattern) {
        return material.hasPattern(pattern);
    }

    @Override
    public String getName() {
        return material.getName();
    }

    @Override
    public Set<IGProcessingStage> getStages() {
        return material.getStages();
    }

    @Override
    public void build() {
        material.build();
    }

    @Override
    public int getColor(MaterialPattern p) {
        return material.getColor(p);
    }

    @Override
    public ITag.INamedTag<Item> getItemTag(MaterialPattern pattern) {
        return material.getItemTag(pattern);
    }

    @Override
    public ITag.INamedTag<Block> getBlockTag(BlockPattern pattern) {
        return material.getBlockTag(pattern);
    }

    @Override
    public ITag.INamedTag<Item> getItemTag(MaterialPattern pattern, MaterialBase... materials) {
        return material.getItemTag(pattern, materials);
    }

    @Override
    public ITag.INamedTag<Block> getBlockTag(BlockPattern pattern, MaterialBase... materials) {
        return material.getBlockTag(pattern, materials);
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern) {
        return material.getTextureLocation(pattern);
    }

    @Override
    public ResourceLocation getTextureLocation(MaterialPattern pattern, int subtype) {
        return material.getTextureLocation(pattern, subtype);
    }

    @Override
    public CrystalFamily getCrystalFamily() {
        return material.getCrystalFamily();
    }

    @Override
    public boolean hasExistingImplementation() {
        return material.hasExistingImplementation();
    }

    @Override
    public boolean generateOreFor(MaterialInterface m) {
        return material.generateOreFor(m);
    }

    @Override
    public MaterialBaseMineral get() {
        return material;
    }

    @Override
    public IGOreConfig getGenerationConfig() {
        return material.getGenerationConfig();
    }

    @Override
    public boolean isFluidPortable(ItemPattern pattern){
        return false;
    }

}
