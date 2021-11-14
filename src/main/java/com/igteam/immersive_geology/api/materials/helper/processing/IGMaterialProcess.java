package com.igteam.immersive_geology.api.materials.helper.processing;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IGMaterialProcess {

    List<IGProcessingMethod> data = new LinkedList<IGProcessingMethod>();

    public IGMaterialProcess(IGProcessingMethod... method){
        data.addAll(Arrays.asList(method));
    }

    public List<IGProcessingMethod> getData() {
        return data;
    }
}
