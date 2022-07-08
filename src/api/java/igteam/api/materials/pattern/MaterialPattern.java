package igteam.api.materials.pattern;

import igteam.api.menu.ItemSubGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface MaterialPattern {
    String getName();

    ItemSubGroup getSubGroup();

    boolean isComplexPattern();

    //Used for Tags (Clay is a bitch)
    boolean hasSuffix();
    String getSuffix();

    static MaterialPattern[] values(){
        List<MaterialPattern> patternList = new ArrayList<MaterialPattern>();
        patternList.addAll(Arrays.asList(ItemPattern.values()));
        patternList.addAll(Arrays.asList(BlockPattern.values()));
        patternList.addAll(Arrays.asList(FluidPattern.values()));

        return patternList.toArray(new MaterialPattern[patternList.size()]);
    }

    default MaterialPattern get() {return this;};
}
