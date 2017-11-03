package com.chocohead.eumj;

import com.chocohead.eumj.te.Engine_TEs;

public class ServerProxy extends CommonProxy {
    public void init()
    {
        super.init();
        Engine_TEs.buildDummies(false);
    }
}
