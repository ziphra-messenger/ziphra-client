package com.privacity.cliente.singleton;

import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;
import com.privacity.common.util.UtilsStringAbstract;

import lombok.Getter;

@Getter
public class UtilsStringSingleton extends UtilsStringAbstract implements SingletonReset {


    private static UtilsStringSingleton instance;

        public static UtilsStringSingleton getInstance() {

            if (instance == null){
                instance = new UtilsStringSingleton();
            }
            return instance;
        }

        private UtilsStringSingleton() { }


    @Override
    public void reset() {
        ToolsUtil.forceGarbageCollector(instance);

    }
}
