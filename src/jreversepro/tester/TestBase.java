package jreversepro.tester;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A base class to run tests around.  Modelled slightly after junit.  I'd like to
 * rewrite with junit later.  Maybe...
 */
public abstract class TestBase {
    private Class originalClass = null;
    private Class newClass = null;

    private TestContext context = null;


    /**
     * the name of the class we are testing
     */

    public abstract String getClassName();


    /**
     * perform the actual tests on either to original
     * class or the new class
     */

    public abstract void test(Class classtotest);


    /**
     * Describe <code>getContext</code> method here.
     *
     * @return a <code>TestContext</code> value
     */

    public TestContext getContext() {
        return context;
    }


    /**
     * Describe <code>setContext</code> method here.
     *
     * @param context a <code>TestContext</code> value
     */

    public void setContext(TestContext context) {
        this.context = context;
    }


    /**
     * Describe <code>getOriginalClass</code> method here.
     *
     * @return a <code>Class</code> value
     */

    public Class getOriginalClass() {
        return originalClass;
    }


    /**
     * Describe <code>setNewClass</code> method here.
     *
     * @return a <code>Class</code> value
     */

    public Class getNewClass() {
        return newClass;
    }


    /**
     * initialize for the tests to run
     * 1 - load original class
     * 2 - decompile / recompile class and load
     */
    public void init() {
        File tmpdir = new File(getContext().getCompileDir());
        tmpdir.mkdir();

        String origclassfile = getContext().getTestClassDir() + File.separatorChar + getClassName() + ".class";
        String javafile = getContext().getCompileDir() + File.separatorChar + getClassName() + ".java";

        log("DECOMPILE " + origclassfile + " to " + javafile);
        TestHelper.decompile(origclassfile, javafile);

        log("COMPILE " + getClassName() + " : " +
                TestHelper.compile(getContext().getCompileDir(), javafile));

        originalClass = TestHelper.loadClass(getContext().getTestClassDir(), getClassName());
        newClass = TestHelper.loadClass(getContext().getCompileDir(), getClassName());

        log("LOADED: orig=" + originalClass);
        log("LOADED: new=" + newClass);

    }


    /**
     * kill the classes.  maybe delete the intermediate files?
     */
    public void teardown() {
        originalClass = null;
        newClass = null;
    }

    /**
     * log a message to the test log
     *
     * @param message the message to log
     */
    public void log(String message) {
        System.out.println("[" + getClassName() + "] " + message);
    }


    public int invokeInt(Object o, String method) {
        Method found = null;

        try {
            Method[] m = o.getClass().getMethods();

            for (int i = 0; i < m.length; i++) {
                if (!m[i].getName().equals(method)) continue;
                if (Modifier.isStatic(m[i].getModifiers())) continue;
                if (m[i].getReturnType() != Integer.TYPE) continue;
                if (m[i].getParameterTypes().length > 0) continue;
                found = m[i];
                break;
            }
        } catch (Exception e) {
            log("invokeInt -- " + e.getMessage());
        }

        if (found == null) {
            log("couldn't find int method " + method);
            return -1;  // need better control flow!
        }

        try {
            return ((Integer) found.invoke(o, null)).intValue();
        } catch (Exception e) {
            log("Error invoking " + method + ": " + e.getMessage());
            return -1;  // again - flow here is pathetic.  junit will help
        }
    }

}
