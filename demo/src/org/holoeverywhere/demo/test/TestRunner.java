
package org.holoeverywhere.demo.test;

import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

import junit.framework.TestSuite;

public class TestRunner extends InstrumentationTestRunner {
    @Override
    public TestSuite getAllTests() {
        InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
        suite.addTestSuite(ApplicationTest.class);
        suite.addTestSuite(ActivityTest.class);
        return suite;
    }

    @Override
    public ClassLoader getLoader() {
        return TestRunner.class.getClassLoader();
    }
}
