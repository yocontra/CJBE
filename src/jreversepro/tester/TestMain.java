package jreversepro.tester;

import jreversepro.tester.tests.TestSimpleReturns;

import java.io.File;
import java.io.IOException;


/**
 * A simple test runner.
 */
public class TestMain {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ARGS: [jrevpro root dir]");
            return;
        }

        File rootdir = new File(args[0]);
        File classdir = new File(rootdir, "testclasses");
        File compiledir = new File(rootdir, "testrun" + System.currentTimeMillis());

        if (!rootdir.exists()) {
            System.out.println("root dir " + rootdir + " does not exist");
            return;
        }

        if (!classdir.exists()) {
            System.out.println("class dir " + classdir + " does not exist");
            return;
        }

        if (compiledir.exists()) {
            System.out.println("compile directory " + compiledir + " already exists - exiting");
            return;
        }

        compiledir.mkdir();

        TestContext context = null;

        try {
            String classdirname = classdir.getCanonicalPath();
            String compiledirname = compiledir.getCanonicalPath();

            context = new TestContext(classdirname, compiledirname);
        } catch (IOException e) {
            System.out.println("-- FAILED: " + e.getMessage());
        }

        runTest(context, TestSimpleReturns.class);
    }


    public static void runTest(TestContext context, Class tester) {
        System.out.println("TEST CASE " + tester.getName());

        TestBase testcase;

        try {
            testcase = (TestBase) tester.newInstance();
        } catch (Exception e) {
            System.out.println("-- FAILED: " + e.getMessage());
            return;
        }

        try {
            testcase.setContext(context);
            testcase.init();

            System.out.println("running against original class");
            testcase.test(testcase.getOriginalClass());
            System.out.println("running against decompiled class");
            testcase.test(testcase.getNewClass());
        } catch (Exception e) {
            System.out.println("-- FAILED: " + e.getMessage());
        } finally {
            testcase.teardown();
        }

        System.out.println("--DONE");

    }
}
