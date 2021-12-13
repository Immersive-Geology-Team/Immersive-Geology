package com.igteam.immersive_geology.api.materials.helper.processing;

import com.igteam.immersive_geology.api.materials.helper.processing.methods.IGVatProcessingMethod;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class IGMaterialProcess {

    List<IGProcessingMethod> data = new LinkedList<IGProcessingMethod>();

    public IGMaterialProcess(IGProcessingMethod... method){
        for (IGProcessingMethod m : method) {
            data.add(m);
            if(m instanceof IGVatProcessingMethod){
                IGVatProcessingMethod v = ((IGVatProcessingMethod) m).getReversedProcess();
                data.add(v);
            }
        }
    }

    public List<IGProcessingMethod> getData() {
        return data;
    }
}
