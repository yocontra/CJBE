/*
 * @(#)ConfirmCloseDialog.java
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
/*
 *  ConfirmCloseDialog : Appears if the user wants to close the dialog.
 *
 */
package jreversepro.gui;

import javax.swing.*;

/**
 * ConfirmCloseDialog is te dialog that appears at the end of
 * application when the user prompts to close the application.
 *
 * @author Karthik Kumar
 * @version 1.3
 */
public final class ConfirmCloseDialog {

    /**
     * Prompts the user if (s)he would exit the application.
     *
     * @param aAppFrame Application Frame.
     * @return true, if the user chooses to close the app.
     *         false, otherwise.
     */
    public static boolean confirmExit(JFrame aAppFrame) {
        int OptionSelect = JOptionPane.showConfirmDialog(
                aAppFrame,
                "Are you sure you want to exit ?",
                "Confirm Exit",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (OptionSelect == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
}