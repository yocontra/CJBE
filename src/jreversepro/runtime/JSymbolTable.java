/*
* @(#)JSymbolTableImpl.java
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
**/
package jreversepro.runtime;

import jreversepro.common.Helper;
import jreversepro.common.KeyWords;
import jreversepro.reflect.JImport;
import jreversepro.reflect.JInstruction;
import jreversepro.reflect.JMethod;
import jreversepro.revengine.RevEngineException;

import java.util.*;

/**
 * JSymbolTable  - Symbol Table of a method containing local variables only.
 *
 * @author Karthik Kumar
 */
public class JSymbolTable implements KeyWords {

    /**
     * Map of the Symbols -
     * Key - local variable index - java.lang.Integer
     * Value - List of JLocalEntry.
     * since for the same localvariable index more than one
     * datatype may exist.
     */
    Map symbols;


    /**
     * Key - Symbol Name
     * Value - JLocalEntry.
     */
    Map symNames;

    /**
     * List of keywords in the language
     */
    static List keywords;

    /**
     * Maximum number of symbols that can be in the table at any given
     * time
     */
    int maxSymbols;

    /**
     * Maximum args in that symbol count mentioned in maxSymbols
     */
    int maxArgs;

    /**
     * basicIndex here.
     */
    int basicIndex;

    /**
     * Imported Classes here.
     */
    JImport imports;

    /**
     * Index of the argument into the symbol table.
     */
    public static final int ARG_INDEX = -1;

    /**
     * Static initializers - Keywords
     **/
    static {
        keywords = getKeyWordsList();
    }

    /**
     * @param rhsMethod Method for which this symbol table
     *                  is generated and used.
     * @param imports   ImportedClasses by this class.
     */
    public JSymbolTable(JMethod rhsMethod,
                        JImport imports) {

        this.imports = imports;
        List args = rhsMethod.getArgList();
        int startIndex = 1;

        maxSymbols = rhsMethod.getMaxLocals();
        maxArgs = args.size();

        //Symbols = new HashSet();
        symbols = new HashMap();
        symNames = new HashMap();

        basicIndex = (int) ('i');
        //Starts with variable 'i'.


        if (!rhsMethod.isStatic()) {
            startIndex = 1;
            addEntry(0, -1, THISCLASS, true);
            //Load the first entry for this pointer.
            //Current class reference.
        } else {
            maxArgs--;
            startIndex = 0;
        }

        //Loads the Arguments onto symbol table.
        loadSymbols(args, startIndex);
    }

    /**
     * @return Get the maximum number of symbols
     *         available in the scope of the current method.
     */
    public int getMaxSymbols() {
        return maxSymbols;
    }

    /**
     * @param aArgs      List of arguments containing the argument
     *                   types.
     * @param startIndex Initial Start Index of the variable count.
     */
    private void loadSymbols(List aArgs, int startIndex) {
        int symIndex = startIndex;
        for (int i = 0; i < aArgs.size(); i++) {
            String argType = (String) aArgs.get(i);
            addEntry(symIndex++, ARG_INDEX, argType, true);
            if (argType.equals(LONG)
                    || argType.equals(DOUBLE)) {
                //Since both long and double take two entries.
                //increment the symbol table count
                symIndex++;
            }
        }
    }

    /**
     * Adds a new datatype to the symboltable
     * dynamically .
     * Parameters  required are the
     *
     * @param aVarIndex variable index ,
     * @param aDatatype Datatype
     * @param aVarStore variable store index.
     * @param aDeclared If variable is declared.
     */
    public void assignDataType(int aVarIndex, String aDatatype,
                               int aVarStore, boolean aDeclared) {

        if ((aVarIndex > JInstruction.INVALID_VAR_INDEX
                && aVarIndex <= maxArgs)) {
            return;
            //No reassignment of data types for arguments.
        }
        JLocalEntry ent = getMatchingEntry(aVarIndex, aVarStore);
        if (ent != null) {
            if (aDatatype.equals(REFERENCE)) {
                return;
            }
        } else {
            if (aDatatype.equals(REFERENCE)) {
                aDatatype = LANG_OBJECT;
            }
        }
        addEntry(aVarIndex, aVarStore, aDatatype, aDeclared);
    }

    /**
     * This method primarily keeps track of the last line
     * that references the variable represented by
     * aVarIndex and the datatype aDataType.
     * Please note that this referencing is essential to define the
     * variables correctly, neither too early, nor too late.
     * Also this is not necessary for arguments, but more
     * necessary for localvariables whose declarations are
     * inside the method code block.
     *
     * @param aVarIndex variable index ,
     * @param aDatatype Datatype
     * @param aIndex    Refernced index.
     */
    public void addReference(int aVarIndex, String aDatatype,
                             int aIndex) {

        if ((aVarIndex > JInstruction.INVALID_VAR_INDEX
                && aVarIndex <= maxArgs)) {
            return;
            //Not necessary to keep track of referenced line numbers.
            //Since the variable is an argument.
        }
        List currentList = (List) symbols.get(new Integer(aVarIndex));
        JLocalEntry createEntry = new JLocalEntry(
                -1, //VarIndex irrelevant
                -1, //StoreIndex irrelevant.
                aDatatype,
                "", //Name irrelevant
                false); //Declared irrelevant
        int objIndex = currentList.indexOf(createEntry);
        if (objIndex != -1) {
            JLocalEntry ent = (JLocalEntry) currentList.get(objIndex);
            ent.setLastReferredIndex(aIndex);
        } else {
            //Helper.log("Something seriously wrong");
        }
    }

    /**
     * Given the endOfBranch of a branch, this method
     * returns a List of strings, of the form
     * <DataType> <VarName>
     * They represent the variables that are to be
     * declared before we enter into the branch
     * whose endOfBranch is given as argument.
     *
     * @param endOfBranch PC when branch ends.
     * @return Returns a List containing Strings in the above mentioned
     *         format.
     */
    public List defineVariable(int endOfBranch) {
        List result = new Vector();
        Enumeration
        enum=Collections.enumeration(symbols.values());
        while (
        enum.hasMoreElements()){
            List list = (List)
            enum.nextElement();
            for (int i = 0; i < list.size(); i++) {
                JLocalEntry ent = (JLocalEntry) list.get(i);
                if (!ent.isDeclared()
                        && ent.getLastReferredIndex() > endOfBranch) {

                    String type = imports.getClassName(
                            Helper.getJavaDataType(
                                    ent.getDeclarationType(),
                                    false));
                    result.add(type + " " + ent.getName());
                    ent.declareVariable();
                }
            }
        }
        return result;
    }


    /**
     * Touch variable is basically assigning a new datatype
     * in place of the old one.
     * This arises especially in the following case:
     * Say, we see
     * List list = new Vector();
     * list.add("sz");
     * On seeing new Vector() we conclude the var type is of
     * type Vector.
     * On seeing list.add("sz") we 'touch' it saying that
     * the type is now List and not Vector.
     *
     * @param aVarName Variable name
     * @param aNewType New Datatype
     */
    public void touchVariable(String aVarName, String aNewType) {
        Object obj = symNames.get(aVarName);
        if (obj != null) {
            JLocalEntry ent = (JLocalEntry) obj;
            if (!ent.isDeclared()) {
                String oldType = ent.getDeclarationType();
                if (!oldType.equals(aNewType)) {
                    String newName = genName(aNewType,
                            ent.getVarIndex());
                    ent.setName(newName);//Rename it again.
                    ent.setDeclarationType(aNewType);

                    //Modify the map of names again.
                    symNames.remove(aVarName);
                    symNames.put(newName, ent);
                }
            }

        } else {
            //Helper.log("touchVariable: DANGEROUS ");
        }
    }

    /**
     * @param aVarIndex Index of local variable into symbol table.
     * @param aInsIndex Index of instruction into bytecode array
     *                  of method.
     * @return Returns a name of the variable given the
     *         variable index and the instruction index.
     * @throws RevEngineException Thrown in case of any problem.
     */
    public String getName(int aVarIndex, int aInsIndex)
            throws RevEngineException {
        return getMatchingEntry(aVarIndex, aInsIndex).getName();
    }

    /**
     * @param aVarIndex Index of local variable into symbol table.
     * @param aInsIndex Index of instruction into bytecode array
     *                  of method.
     * @return Returns a dataType of the variable given the
     *         variable index and the instruction index.
     * @throws RevEngineException Thrown in case of any problem.
     */
    public String getDataType(int aVarIndex, int aInsIndex)
            throws RevEngineException {
        return getMatchingEntry(aVarIndex, aInsIndex).
                getDeclarationType();
    }

    /**
     * Declares a variable represented by aVarIndex and
     * aInsIndex and returns its  datatype.
     *
     * @param aVarIndex Index of local variable into symbol table.
     * @param aInsIndex Index of instruction into bytecode array
     *                  of method.
     * @return Returns a String containing type.
     * @throws RevEngineException Thrown in case of any problem.
     */
    public final String declare(int aVarIndex, int aInsIndex)
            throws RevEngineException {
        if (aVarIndex <= maxArgs) {
            return null;
        }
        JLocalEntry ent = getMatchingEntry(aVarIndex, aInsIndex);
        if (!ent.isDeclared()) {
            String declareType = ent.getDeclarationType();
            ent.declareVariable();
            return imports.getClassName(
                    Helper.getJavaDataType(declareType, false));
        } else {
            return null;
        }
    }

    /**
     * @param aVarIndex Index of local variable into symbol table.
     * @param aInsIndex Index of instruction into bytecode array
     *                  of method.
     * @return Returns a matching LocalTable entry
     *         given the variable index and the instruction index.
     */
    private JLocalEntry getMatchingEntry(int aVarIndex, int aInsIndex) {
        Object obj = symbols.get(new Integer(aVarIndex));
        if (obj != null) {
            List currentList = (List) obj;
            for (int i = currentList.size() - 1; i >= 0; i--) {
                JLocalEntry ent = (JLocalEntry) currentList.get(i);
                if (aInsIndex >= ent.getStoreIndex()) {
                    return ent;
                }
            }
        }
        return null;
    }


    /**
     * Adds a new entry to the localsymboltable.
     *
     * @param aVarIndex   Index of local variable into symbol table.
     * @param aStoreIndex Index when the variable is first initialized/
     *                    stored.
     * @param aDatatype   Datatype of the local variable entry.
     * @param aDeclared   If the local variable mentioned in declared or not.
     */
    private void addEntry(int aVarIndex, int aStoreIndex,
                          String aDatatype, boolean aDeclared) {

        Helper.log("Adding entry " + aVarIndex);
        aDeclared |= (aDatatype.indexOf("<") != -1);

        Object obj = symbols.get(new Integer(aVarIndex));

        List currentList;
        if (obj == null) {
            currentList = new Vector();
            symbols.put(new Integer(aVarIndex), currentList);
        } else {
            currentList = (List) obj;
        }
        String name = genName(aDatatype, aVarIndex);
        JLocalEntry ent = new JLocalEntry(
                aVarIndex, aStoreIndex,
                aDatatype,
                name, aDeclared);
        if (!currentList.contains(ent)) {
            currentList.add(ent);
            symNames.put(name, ent);
        }
    }

    /**
     * Generates an name for the type and the
     * variableIndex.
     *
     * @param aType     type of the variable.
     * @param aVarIndex Variable Index to symbol table.
     * @return Returns a name generated for the variable.
     */
    private String genName(String aType, int aVarIndex) {
        int lastArrIndex = aType.lastIndexOf("[");
        String name;
        boolean arrType = false;
        if (lastArrIndex != -1) {
            arrType = true;
            aType = aType.substring(lastArrIndex + 1);
        }
        boolean basicType = Helper.isBasicType(aType);
        aType = imports.getClassName(
                Helper.getJavaDataType(aType, true));
        if (basicType) {
            name = new Character((char) (basicIndex)).toString();
            basicIndex++;
        } else if (aType.compareTo(THISCLASS) == 0) {
            name = THIS;
        } else {
            name = aType.toLowerCase();
        }
        if (arrType) {
            name += "Arr";
        }
        if (symNames.get(name) != null || keywords.contains(name)) {
            name = genUniqueName(name, aVarIndex);
        }
        return name;
    }

    /**
     * Generate Unique name for the variables.
     *
     * @param name      Name of the variable.
     * @param aVarIndex variable index for which the name is to be
     *                  generated.
     * @return Returns a new unique name in the scope of this symbol
     *         table.
     */
    private String genUniqueName(String name, int aVarIndex) {
        StringBuffer sb = new StringBuffer(name);
        sb.append(aVarIndex);
        return sb.toString();
    }

    /**
     * @return Returns a list containing the keywords of the
     *         java language. All the individual members are String.
     */
    public static List getKeyWordsList() {
        List keywordList = new Vector();
        keywordList.add("abstract");
        keywordList.add("double");
        keywordList.add("int");
        keywordList.add("strictfp");
        keywordList.add("boolean");
        keywordList.add("else");
        keywordList.add("interface");
        keywordList.add("super");
        keywordList.add("synchronized");
        keywordList.add("break");
        keywordList.add("extends");
        keywordList.add("long");
        keywordList.add("switch");
        keywordList.add("byte");
        keywordList.add("final");
        keywordList.add("native");
        keywordList.add("case");
        keywordList.add("finally");
        keywordList.add("new");
//      keywordList.add("this");
        keywordList.add("catch");
        keywordList.add("float");
        keywordList.add("package");
        keywordList.add("throw");
        keywordList.add("char");
        keywordList.add("for");
        keywordList.add("private");
        keywordList.add("throws");
        keywordList.add("class");
        keywordList.add("goto");
        keywordList.add("protected");
        keywordList.add("transient");
        keywordList.add("const");
        keywordList.add("if");
        keywordList.add("public");
        keywordList.add("try");
        keywordList.add("continue");
        keywordList.add("implements");
        keywordList.add("return");
        keywordList.add("void");
        keywordList.add("default");
        keywordList.add("import");
        keywordList.add("short");
        keywordList.add("volatile");
        keywordList.add("do");
        keywordList.add("instanceof");
        keywordList.add("static");
        keywordList.add("while");
        return keywordList;
    }

    /**
     * @return Returns Stringified form of this class
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        if (symbols != null) {
            Iterator i1 = symbols.keySet().iterator();
            while (i1.hasNext()) {
                Integer k1 = (Integer) i1.next();
                Iterator i2 = ((List) symbols.get(k1)).iterator();
                if (i2.hasNext()) {
                    sb.append(k1 + " = ");
                    while (i2.hasNext()) {
                        sb.append(i2.next());
                    }
                }
            }
        }
        return sb.toString();
    }
}
