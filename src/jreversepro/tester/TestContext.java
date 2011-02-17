package jreversepro.tester;

/**
 * keep some config information for the test cases
 */
public class TestContext {
    private String testclassdir = null;
    private String compiledir = null;

    /**
     * Creates a new <code>TestContext</code> instance.
     *
     * @param testclassdir a <code>String</code> value
     * @param compiledir   a <code>String</code> value
     */
    public TestContext(String testclassdir, String compiledir) {
        this.testclassdir = testclassdir;
        this.compiledir = compiledir;
    }

    /**
     * return the directory the test class files are in
     *
     * @return the directory the tests are in
     */
    public String getTestClassDir() {
        return testclassdir;
    }


    /**
     * get our compile directory
     *
     * @return the compile directory
     */
    public String getCompileDir() {
        return compiledir;
    }


}
