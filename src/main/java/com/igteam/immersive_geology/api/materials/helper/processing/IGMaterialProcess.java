package com.igteam.immersive_geology.api.materials.helper.processing;

import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;

import java.util.*;

public class IGMaterialProcess {

    //Using a Set to prevent Duplicate Cases
    Set<IGProcessingMethod> data = new LinkedHashSet<>();

    public IGMaterialProcess(IGProcessingMethod... method){
        for (IGProcessingMethod m : method) {
            data.add(m);
            if(m instanceof IGVatProcessingMethod){
                IGVatProcessingMethod v = ((IGVatProcessingMethod) m).getReversedProcess();
                data.add(v);
            }
        }
    }

    public Set<IGProcessingMethod> getData() {
        return data;
    }
}
