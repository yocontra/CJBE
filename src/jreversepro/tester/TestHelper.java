package jreversepro.tester;

import jreversepro.JCmdMain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class TestHelper {
    public static void decompile(String classfile, String javafile) {
        String[] args = new String[]{"-dc", classfile, javafile};

        JCmdMain main = new JCmdMain();
        main.process(args);
    }

    public static boolean compile(String rootdir, String javafile) {
        try {
            String[] cmd = new String[]{"javac", "-d", rootdir, javafile};
            Process process = Runtime.getRuntime().exec(cmd);

            InputStream in = process.getErrorStream();
            byte[] buf = new byte[8192];
            int nread = 0;
            int totread = 0;
            while ((nread = in.read(buf, 0, buf.length)) >= 0) {
                totread += nread;
                System.out.write(buf, 0, nread);
            }
            System.out.flush();

            // System.out.println("READ: " + totread); // error stream
            try {
                process.waitFor();
            } catch (InterruptedException e) {
            }

            return (process.exitValue() == 0);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static Class loadClass(String dir, String classname) {
        try {
            URL[] urls = new URL[]{new URL("file:" + dir + "/")};
            ClassLoader loader = URLClassLoader.newInstance(urls);

            return loader.loadClass(classname);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
