package igteam.immersive_geology.materials.pattern;

import igteam.immersive_geology.menu.ItemSubGroup;

public interface MaterialPattern {
    String getName();

    ItemSubGroup getSubGroup();

    boolean isComplexPattern();
}
