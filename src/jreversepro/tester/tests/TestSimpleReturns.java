package jreversepro.tester.tests;

import jreversepro.tester.TestBase;

public class TestSimpleReturns
        extends TestBase {
    public String getClassName() {
        return "SimpleReturns";
    }

    public void test(Class c) {
        Object o;

        try {
            o = c.newInstance();
        } catch (Exception e) {
            log("couldn't create instance: " + e.getMessage());
            return;
        }

        int countok = 0;
        int countbad = 0;

        int res = invokeInt(o, "returnInt");
        if (res == 156) {
            countok++;
        } else {
            countbad++;
            log("returnInt should return 156 but returned " + res);
        }

        // ... etc ...

        log("-RESULT ok=" + countok + " notok=" + countbad);
    }
}
