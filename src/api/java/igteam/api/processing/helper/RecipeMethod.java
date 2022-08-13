package igteam.api.processing.helper;

public enum RecipeMethod {
    Crafting, Separator, Bloomery, Chemical, Roasting, Calcination, Crystalization,
    Blasting, Crushing, basicSmelting, arcSmelting, Synthesis, Cutting;

    //Used to differentiate method types when using,
    //IGProcessingMethod and store extra information based on the processing method

    public String getMachineName() {
        switch (this){
            case Crafting:
                return "crafting_table";
            case Cutting:
                return "immersive_geology:hydrojet";
            case Blasting:
                return "immersive_engineering:crude_blast_furnace";
            case Bloomery:
                return "immersive_geology:bloomery";
            case Chemical:
                return "immersive_geology:chemicalvat";
            case Crushing:
                return "immersiveengineering:crusher";
            case Roasting:
                return "immersive_geology:reverberation_furnace";
            case Synthesis:
                return "immersiveengineering:refinery";
            case Separator:
                return "immersive_geology:gravityseparator";
            case arcSmelting:
                return "immersiveengineering:arc_furnace";
            case Calcination:
                return "immersive_geology:rotarykiln";
            case basicSmelting:
                return "minecraft:furnace";
            case Crystalization:
                return "immersive_geology:crystallizer";
        }
        return "unknown";
    }
}
