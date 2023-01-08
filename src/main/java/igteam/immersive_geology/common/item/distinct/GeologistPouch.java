package igteam.immersive_geology.common.item.distinct;

import igteam.immersive_geology.core.lib.IGLib;
import net.minecraft.item.Item;

public class GeologistPouch extends Item {

    public GeologistPouch(Item.Properties props) {
        super(props);
        this.setRegistryName(IGLib.MODID, "geologist_bag");
    }

    private static final int SLOT_COUNT = 18;

    public int getSlotCount() {return SLOT_COUNT;}


}
