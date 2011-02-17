/*
 * @(#)JCustomFileChooser.java
 *
 * JReversePro - Java Decompiler / Disassembler.
 * Copyright (C) 2000 Karthik Kumar.
 * EMail: akarthikkumar@hotmail.com
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
package jreversepro.gui;

import javax.swing.*;
import java.io.File;

/**
 * FileChooser to choose  /view a group of files.
 *
 * @author Karthik Kumar
 * @version 1.3
 */
public class JCustomFileChooser extends JFileChooser {
    /**
     * mFileExtension Extension of the files to be viewed.
     */
    private String mFileExtension;

    /**
     * Description of the files to be viewed.
     */
    private String mFileDescription;

    /**
     * @param aDir         Default Directory of the File Chooser.
     * @param aDescription Description of the extension aExtension
     * @param aExtension   Extension of the file
     * @param aToolTipText Tooltip text used when viewing file.
     */
    public JCustomFileChooser(String aDir,
                              String aDescription,
                              String aExtension,
                              String aToolTipText) {
        super(aDir);

        mFileExtension = aExtension;
        mFileDescription = aDescription;

        setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        setFileFilter(new CustomFileFilter());
        setApproveButtonToolTipText(aToolTipText);
    }

    /**
     * Makes the file chooser visible.
     *
     * @param aParent       Parent Frame
     * @param aSelectButton Name of the button to be selected.
     * @return the status returned by the file chooser.
     */
    public int showChooser(JFrame aParent, String aSelectButton) {
        return showDialog(aParent, aSelectButton);
    }

    /**
     * File Filter.
     *
     * @author Karthik Kumar
     */
    class CustomFileFilter
            extends javax.swing.filechooser.FileFilter {

        /**
         * @param aFilterFile File to be filtered.
         * @return true, if this file is passed by the filter.
         *         false, otherwise.
         */
        public boolean accept(File aFilterFile) {
            return (aFilterFile.isDirectory()
                    || aFilterFile.getName().endsWith(mFileExtension));
        }

        /**
         * @return Description of the Filter involved.
         */
        public String getDescription() {
            return mFileDescription + "(" + mFileExtension + ")";
        }
    }

}