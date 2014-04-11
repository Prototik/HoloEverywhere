package org.holoeverywhere.plugin.test.app;

import org.holoeverywhere.plugin.test.jarlibrary.SomeUseful;
import org.holoeverywhere.plugin.test.library.TestApplication;

public class MyApplication extends TestApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        SomeUseful.doIt();
    }
}
