/**
 * @(#)JCmdMain.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 2001 Karthik Kumar.
 * EMail: akkumar@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it , under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.If not, write to
 *  The Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330,
 *  Boston, MA 02111-1307, USA.
 *
 **/
package jreversepro;

import jreversepro.common.AppConstants;
import jreversepro.common.Helper;
import jreversepro.parser.ClassParserException;
import jreversepro.reflect.JClassInfo;
import jreversepro.revengine.JSerializer;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Karthik Kumar.
 */
public class JCmdMain
        implements AppConstants {

    /**
     * Prompt that appears *
     */
    static final String CMD_PROMPT = "\n(jrevpro)";

    /**
     * Command to exit *
     */
    static final String CMD_EXIT = "exit";

    /**
     * Disassembling command *
     */
    static final String CMD_DISASSEMBLE = "disassemble";

    /**
     * Disassembling command - Short form *
     */
    static final String CMD_DA = "da";

    /**
     * Toggle debug option *
     */
    static final String CMD_DEBUG = "debug";

    /**
     * Decomipiling command *
     */
    static final String CMD_DECOMPILE = "decompile";

    /**
     * Decomipiling command - short form *
     */
    static final String CMD_DC = "dc";

    /**
     * View constant pool *
     */
    static final String CMD_VIEWPOOL = "viewpool";

    /**
     * View constantpool - short form *
     */
    static final String CMD_VP = "vp";

    /**
     * swing option *
     */
    static final String CMD_SWING = "g";

    /**
     * AWT based UI *
     */
    static final String CMD_AWT = "u";

    /**
     * Help  *
     */
    static final String CMD_HELP = "help";

    /**
     * Interactive mode *
     */
    static final String CMD_INTERACTIVE = "i";

    /**
     * Help detailed message *
     */
    static final String HELP_MSG;

    /**
     * Command-line options.
     */
    static final String CMD_OPTIONS =
            "Syntax: jrevpro -i|-da|-dc|-vp <Class File>"
                    + "\n -i Interactive" + "\n -da disassemble"
                    + "\n -dc Decompile" + "\n -vp View ConstantPool";

    /**
     * List of possible commands in the interactive mode *
     */
    static final List LIST_CMDS;

    /**
     * Class Parser invoked.
     */
    private static JSerializer mSerializer;

    /** static initializer **/
    static {
        mSerializer = new JSerializer();

        StringBuffer sb = new StringBuffer();

        sb.append(CMD_DISASSEMBLE + " | " + CMD_DA
                + " [<OutputFile>] \t"
                + "Disassemble file\n");

        sb.append(CMD_DECOMPILE + " | " + CMD_DC
                + " [ <OutputFile> ] \t" + "Decompile file\n");
        sb.append(CMD_VIEWPOOL + " | " + CMD_VP
                + " [ <ConstantPoolIndex> ] \t"
                + "View ConstantPool\n");
        sb.append(CMD_DEBUG + "\t\t\t\t\t" + "Toggle debug mode\n");
        sb.append(CMD_HELP + "\t\t\t\t\t" + "Print this menu\n");
        sb.append(CMD_EXIT + "\t\t\t\t\t" + "Exit");

        HELP_MSG = sb.toString();

        LIST_CMDS = new ArrayList(12);
        LIST_CMDS.add(CMD_DA);
        LIST_CMDS.add(CMD_DC);
        LIST_CMDS.add(CMD_VP);
        LIST_CMDS.add(CMD_INTERACTIVE);
        LIST_CMDS.add(CMD_DISASSEMBLE);
        LIST_CMDS.add(CMD_DECOMPILE);
        LIST_CMDS.add(CMD_VIEWPOOL);
        LIST_CMDS.add(CMD_SWING);
        LIST_CMDS.add(CMD_AWT);
        LIST_CMDS.add(CMD_DEBUG);
        LIST_CMDS.add(CMD_HELP);
    }


    /**
     * Driving the application.
     *
     * @param aArgs Argument to Main.
     */
    public static void main(String[] aArgs) {
        System.out.println(GPL_INFO);

        if (!Helper.versionCheck()) {
            System.exit(1);
        }
        if (aArgs.length > 0 && aArgs.length < 4) {
            JCmdMain main = new JCmdMain();
            main.process(aArgs);

        } else {
            System.err.println(CMD_OPTIONS + "\n");
            System.exit(1);
        }
    }

    /**
     * @param args Arguments to the command-line module.
     */
    public void process(String[] args) {
        String cmd = args[0].substring(1);

        if (!LIST_CMDS.contains(cmd)) {
            System.err.println(CMD_OPTIONS + "\n");
            System.exit(1);
        }

        if (cmd.equals(CMD_SWING)) {
            (new JMainFrame()).setVisible(true);
            return;
        } else if (cmd.equals(CMD_AWT)) {
            (new JAwtFrame()).setVisible(true);
            return;
        }

        JClassInfo inf = loadClass(args[1]);
        if (cmd.equals(CMD_INTERACTIVE)) {
            if (args.length == 2) {
                listen(inf, args[1]);
            }
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append(cmd);

            for (int i = 2; i < args.length; i++) {
                sb.append(" " + args[i]);
            }
            process(inf, sb.toString());
        }
    }

    /**
     * @param infoClass Contains information about this class.
     * @param inputFile InputFile referring to the .classfile.
     */
    public void listen(JClassInfo infoClass, String inputFile) {
        if (infoClass == null) {
            return;
        }

        try {
            System.out.println("Type '" + CMD_HELP + "' to get started...");
            BufferedReader br
                    = new BufferedReader(
                    new InputStreamReader(System.in));
            boolean status = true;
            while (status) {
                System.out.print(CMD_PROMPT);
                String cmd = br.readLine();
                status = process(infoClass, cmd);
            }
        } catch (IOException ioex) {
            //Extreme case.
        }
    }

    /**
     * @param inputFile InputFile referring to the .classfile.
     * @return Returns a reference of type JClassInfo.
     */
    private JClassInfo loadClass(String inputFile) {
        JClassInfo infoClass = null;

        try {
            File file = new File(inputFile);

            if (!file.exists()) {
                URL url = new URL(inputFile);
                infoClass = mSerializer.loadClass(url);
            } else {
                infoClass = mSerializer.loadClass(file);
            }
            Helper.log("Successfully loaded class : " + inputFile);
        } catch (ClassParserException cpex) {
            System.err.println("Failed to load class : " + inputFile);
            System.err.println(cpex);
        } catch (IOException ioex) {
            // Extreme case.
            System.err.println("Failed to load class : " + inputFile);
        }
        return infoClass;
    }

    /**
     * Process an individual command.
     *
     * @param infoClass Information about the class.
     * @param cmd       cmd-line option given.
     * @return true, if processing is ok.
     *         false, in case it is exit command or any other problem.
     *         The shell exits on getting false from this method.
     */
    private boolean process(JClassInfo infoClass,
                            String cmd) {
        if (infoClass == null) {
            return false;
        }
        String result = null;
        StringTokenizer strTok = new StringTokenizer(cmd);
        ArrayList tokens = new ArrayList();
        while (strTok.hasMoreTokens()) {
            tokens.add(strTok.nextToken());
        }
        tokens.trimToSize();
        int size = tokens.size();
        if (size != 0) {
            String opcode = (String) tokens.get(0);
            if (opcode.equals(CMD_EXIT)) {
                return false;
            } else if (opcode.equals(CMD_DEBUG)) {
                boolean flag = Helper.toggleDebug();
                System.out.println("Debug mode is " + flag);
                return true;
            } else if (opcode.equals(CMD_DISASSEMBLE)
                    || opcode.equals(CMD_DA)) {
                try {
                    String output = null;
                    if (size > 2) {
                        System.err.println("disassemble [ <outputfile>] ");
                        return true;
                    } else if (size == 2) {
                        output = (String) tokens.get(1);
                    }
                    infoClass.reverseEngineer(true);
                    result = infoClass.getStringifiedClass(true);

                    PrintStream ps;
                    if (output != null) {
                        ps = new PrintStream(
                                new FileOutputStream(output));
                    } else {
                        ps = System.out;
                    }
                    ps.print(result);
                    if (!ps.equals(System.out)) {
                        ps.close();
                        Helper.log("Output successfully written to "
                                + output);
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                return true;
            } else if (opcode.equals(CMD_DECOMPILE)
                    || opcode.equals(CMD_DC)) {
                try {
                    String output = null;
                    if (size > 2) {
                        System.err.println("decompile [ <outputfile>] ");
                        return true;
                    } else if (size == 2) {
                        output = (String) tokens.get(1);
                    }
                    infoClass.reverseEngineer(false);
                    result = infoClass.getStringifiedClass(false);


                    PrintStream ps;
                    if (output != null) {
                        ps = new PrintStream(
                                new FileOutputStream(output));
                    } else {
                        ps = System.out;
                    }
                    ps.print(result);
                    if (!ps.equals(System.out)) {
                        ps.close();
                        Helper.log("Output successfully written to "
                                + output);
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                return true;
            } else if (opcode.equals(CMD_VIEWPOOL)
                    || opcode.equals(CMD_VP)) {
                String msg = "Syntax : viewpool [ <ConstantPoolIndex No> ] ";
                if (size == 1) {
                    msg = infoClass.getConstantPool().getEntryInfo();
                } else if (size == 2) {
                    try {
                        int lIndex = Integer.parseInt(
                                (String) tokens.get(1));
                        msg = infoClass.getConstantPool().
                                getEntryInfo(lIndex);
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                }
                System.out.println(msg);
                return true;
            } else if (opcode.equals(CMD_HELP)) {
                System.out.println(HELP_MSG);
                return true;
            } else {
                System.err.println("Unknown Command");
                return true;
            }
        } else {
            return true;
        }
    }
}
