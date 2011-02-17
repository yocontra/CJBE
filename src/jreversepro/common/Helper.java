/*
 * @(#)Helper.java
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
 */
package jreversepro.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper contains a list of assorted methods that 'helps'
 * in manipulating the data present in the class file.
 *
 * @author Karthik Kumar
 */
public class Helper implements KeyWords {

    /**
     * Private constructor to prevent any instance from being created.
     */
    private Helper() {
    }

    /**
     * Working Version Could be compromised
     */
    static final String DEFAULT_VERSION = "1.2.2";


    /**
     * Debug flag
     * Default value = false.
     */
    static boolean debugFlag;

    /**
     * static initializer method.
     **/
    static {
        debugFlag = false;
    }

    /**
     * @param logMsg Message to be logged.
     */
    public static void log(String logMsg) {
        if (debugFlag) {
            System.out.println(logMsg);
        }
    }

    /**
     * @param ex Exception to be logged.
     */
    public static void log(Exception ex) {
        if (debugFlag) {
            ex.printStackTrace(System.out);
        }
    }

    /**
     * Log without end-of-line at the end.
     *
     * @param logMsg Message to be logged.
     */
    public static void logNoEol(String logMsg) {
        if (debugFlag) {
            System.out.print(logMsg);
        }
    }

    /**
     * Toggles the debug flag.
     *
     * @return Returns the new debug flag after toggling.
     */
    public static boolean toggleDebug() {
        debugFlag = !debugFlag;
        return debugFlag;
    }

    /**
     * @return the value of debug flag.
     */
    public static boolean isDebug() {
        return debugFlag;
    }

    /**
     * Returns the Package name alone from a fully qualified name.
     * <p/>
     * For Example , if <code>FullName = java/lang/StringBuffer,</code>
     * <br>then a call to <code>getPackageName(arg)</code> returns the
     * value <code>java.lang</code>.
     * <p/>
     *
     * @param aFullName A Fully Qualified Name.
     * @return the package name , alone with the dots separating the
     *         classes.
     */
    public static String getPackageName(String aFullName) {
        aFullName = Helper.getJavaDataType(aFullName, false);
        aFullName = aFullName.replace('/', '.');
        int dotIndex = aFullName.lastIndexOf(".");
        if (dotIndex != -1) {
            return aFullName.substring(0, dotIndex);
        } else {
            return "";
        }
    }


    /**
     * Determines the Java representation , given the JVM representation
     * of data types.
     * <p/>
     * <table>
     * <tr><th><code>dataType</code> </th>
     * <th><code>formatDataType(dataType)</code></th></tr>
     * <tr><td><code>B</code></td><td>byte</td></tr>
     * <tr><td><code>C</code></td><td>char</td></tr>
     * <tr><td><code>D</code></td><td>double</td></tr>
     * <tr><td><code>F</code></td><td>float</td></tr>
     * <tr><td><code>I</code></td><td>int</td></tr>
     * <tr><td><code>J</code></td><td>long</td></tr>
     * <tr><td><code>S</code></td><td>short</td></tr>
     * <tr><td><code>V</code></td><td>void</td></tr>
     * <tr><td><code>Z</code></td><td>boolean</td></tr>
     * <tr><td><code>[Z</code></td><td>boolean [] , array representation
     * </td></tr>
     * <tr><td><code>Ljava/lang/String </code></td><td>
     * <code> java/lang/String</code> </td></tr>
     * </table>
     *
     * @param aDataType  JVM representation of the data type.
     * @param associated If set, then an array representation is returned.�
     * @return Java Language representation of aDataType.
     */
    public static String getJavaDataType(String aDataType,
                                         boolean associated) {

        char firstChar = aDataType.charAt(0);

        if (aDataType.length() == 1) {
            switch (firstChar) {
                case 'B':
                    return "byte";
                case 'C':
                    return "char";
                case 'D':
                    return "double";
                case 'F':
                    return "float";
                case 'I':
                    return "int";
                case 'J':
                    return "long";
                case 'S':
                    return "short";
                case 'V':
                    return "void";
                case 'Z':
                    return "boolean";
                default:
                    return "invalid";
            }
        } else if (firstChar == '[') {
            String type = getJavaDataType(aDataType.substring(1),
                    associated);
            if (!associated) {
                return type + "[]";
            } else {
                return type;
            }
        } else if (firstChar == 'L') {
            int len = aDataType.length();
            if (aDataType.indexOf(";") == -1) {
                return aDataType.substring(1);
            } else {
                return aDataType.substring(1, len - 1);
            }
        }
        return aDataType;
    }

    /**
     * Determines the length of the JVM datatype representation
     * given the JVM signature.
     * <p/>
     * <table>
     * <tr><th><code>dataType</code> </th>
     * <th><code>getSignTokenLength(dataType)(dataType)</code></th></tr>
     * <tr><td><code>all basic data types</code></td><td>1</td></tr>
     * <tr><td><code>[XYZ</code></td><td>len(XYZ) + 1
     * </td></tr>
     * <tr><td><code>Ljava/lang/String </code></td><td>
     * <code> len(Ljava/lang/String)</code> </td></tr>
     * </table>
     *
     * @param aDataType Signature of a method as present in
     *                  the class file in JVM representation, containing a list
     *                  of datatypes.
     * @return the length of the first valid datatype.
     */
    public static int getSignTokenLength(String aDataType) {
        char ch = aDataType.charAt(0);
        switch (ch) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'V':
            case 'Z':
                return 1;
            case '[':
                return (
                        getSignTokenLength(aDataType.substring(1)) + 1);
            case 'L':
                int semiColon = aDataType.indexOf(";");
                if (semiColon == -1) {
                    return aDataType.length();
                } else {
                    return (semiColon + 1);
                }
            default:
                return 0;
        }
    }

    /**
     * Returns the arguments in array form
     * given the JVM signature.
     * <p/>
     * For example , <code>IILjava/lang/String</code>
     * could be returned as <br>
     * <code>( int , int , java/lang/String )</code>.
     *
     * @param aSignature Signature of the method.
     * @return The method arguments as a List
     */
    public static List getArguments(String aSignature) {
        List args = new ArrayList();
        int endIndex = aSignature.indexOf(")");
        if (endIndex != 1) {
            aSignature = aSignature.substring(1, endIndex);

            String origStr = aSignature;
            int length = origStr.length();
            //Start Processing Rhs
            int curIndex = 0;

            while (curIndex < length) {
                aSignature = origStr.substring(curIndex);
                int tokenLength = getSignTokenLength(aSignature);
                String tokenString =
                        aSignature.substring(0, tokenLength);
                int semiColon = tokenString.indexOf(";");
                if (semiColon != -1) {
                    tokenString = tokenString.substring(0, semiColon);
                }
                args.add(tokenString);
                curIndex += tokenLength;
            }
        }
        return args;
    }


    /**
     * Given the Signature of the method , this provides us the
     * return type.
     * <p/>
     *
     * @param aSignature Signature of the method.
     * @return the return type associated with the method signature,
     *         The type returned corresponds to JVM representation.
     */
    public static String getReturnType(String aSignature) {
        int index = aSignature.indexOf(")");
        return aSignature.substring(index + 1);
    }

    /**
     * Extracts the value of a particular number of bits.
     * <p/>
     * For example <code>lowNBits(169 , 5 )</code>
     * returns <code> (10101001,5) -> 10101 i.e  21 </code> <br>.
     *
     * @param aValue   Value containing the integer in string form.
     * @param aNumBits Number of bits that is to be extracted.
     * @return the masked value of the N Bit Number.
     */
    public static String lowNbits(String aValue, int aNumBits) {
        int val = Integer.parseInt(aValue);
        if (val == 0) {
            return aValue;
        } else {
            int res = 0;
            for (int i = 0; i < aNumBits; i++) {
                int mask = (int) Math.pow(2, i);
                if ((val & mask) != 0) {
                    res += mask;
                }
            }
            return (String.valueOf(res));
        }
    }

    /**
     * Inserts a '\' before all the escape characters ,
     * line '\n' , '\t' to provide better readability.
     * <p/>
     *
     * @param aLiteral String containing the escape characters.
     * @return the new String containing the new escape sequence
     *         of characters.
     */
    public static String replaceEscapeChars(String aLiteral) {
        StringBuffer result = new StringBuffer("");
        for (int i = 0; i < aLiteral.length(); i++) {
            result.append(representChar(aLiteral.charAt(i)));
        }
        return result.toString();
    }

    /**
     * Returns the String representation of the character .
     *
     * @param aChar - Character.
     * @return the new String representing the character.
     */
    private static String representChar(char aChar) {
        switch (aChar) {
            case '\n':
                return "\\n";
            case '\t':
                return "\\t";
            case '\\':
                return "\\\\";
            case '"':
                return "\\" + "\"";
            default:
                return String.valueOf(aChar);
        }
    }


    /**
     * Checks for the version compatibility between the system JRE
     * and the JRE for which the application is written for.
     *
     * @return true , if System JRE is >= DEFAULT_VERISON ( 1.2.2 ).<br>
     *         false , otherwise.
     */
    public static boolean versionCheck() {
        String version = System.getProperty("java.version");
        for (int i = 0; i <= 5; i += 2) {
            int versionVal = (int) (version.charAt(i));
            int workingVal = (int) (DEFAULT_VERSION.charAt(i));
            if (versionVal > workingVal) {
                return true;
            } else if (versionVal < workingVal) {
                System.err.println(
                        "This Software is designed to run under "
                                + DEFAULT_VERSION);
                System.err.println("Please upgrade your JRE"
                        + " from http://java.sun.com/products/j2se"
                        + " for your operating system");
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if the given datatype is a basic data type or not.
     *
     * @param type the datatype to be checked.
     * @return true , if it is.
     *         false , otherwise.
     */
    public static boolean isBasicType(String type) {
        return (type.equals(KeyWords.INT)
                || type.equals(KeyWords.BOOLEAN)
                || type.equals(KeyWords.BYTE)
                || type.equals(KeyWords.CHAR)
                || type.equals(KeyWords.SHORT)
                || type.equals(KeyWords.FLOAT)
                || type.equals(KeyWords.LONG)
                || type.equals(KeyWords.DOUBLE));
    }

    /**
     * Both boolean and char are represented as integers .
     * This takes care of the conversions
     *
     * @param value    Old Value.
     * @param datatype Datatype of the value.
     * @return Returns the new value after making appropriate
     *         changes.
     */
    public static String getValue(String value, String datatype) {
        if (datatype == null) {
            return value;
        }
        int lastQIndex = value.lastIndexOf('?');
        int lastColonIndex = value.lastIndexOf(":");

        if (lastQIndex == -1
                || lastColonIndex == -1
                || lastQIndex > lastColonIndex) {
            return getAtomicValue(value, datatype);
        }
        String condition = value.substring(0, lastQIndex);
        String val1 = value.substring(lastQIndex + 1, lastColonIndex);
        String val2 = value.substring(lastColonIndex + 1);

        StringBuffer result = new StringBuffer(condition);
        result.append("? " + getAtomicValue(val1, datatype));
        result.append(": " + getAtomicValue(val2, datatype));

        return result.toString();
    }


    /**
     * Both boolean and char are represented as integers .
     * This takes care of the conversions
     *
     * @param value    Old Value.
     * @param datatype Datatype of the value.
     * @return Returns the new value after making appropriate
     *         changes.
     */
    private static String getAtomicValue(String value, String datatype) {
        value = value.trim();
        if (datatype.equals(JVM_BOOLEAN)) { //boolean
            if (value.compareTo("1") == 0) {
                return TRUE;
            } else if (value.compareTo("0") == 0) {
                return FALSE;
            } else {
                return value;
            }
        } else if (datatype.equals(JVM_CHAR)) {  //Character
            try {
                StringBuffer sb = new StringBuffer("");
                int intvalue = Integer.parseInt(value);
                sb.append("'" + (char) intvalue + "'");
                return sb.toString();
            } catch (NumberFormatException _ex) {
                return value;
            }
        }
        return value;
    }

    /**
     * Converts a signed 'byte' to an unsigned integer.
     * <p/>
     *
     * @param aByteVal a Byte Value.
     * @return unsigned integer equivalent of aByteVal.
     */
    public static int signedToUnsigned(int aByteVal) {
        return (aByteVal < 0) ? (aByteVal += 256) : aByteVal;
    }
}
