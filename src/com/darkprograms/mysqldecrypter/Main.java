package com.darkprograms.mysqldecrypter;

import com.darkprograms.mysqldecrypter.gui.MainGUI;
import com.darkprograms.mysqldecrypter.util.LoadLibraryUtil;

import javax.swing.*;

/**
 * Main class
 */
public class Main {

    /**
     * Method called on start of program
     *
     * @param args arguments passed to program
     */
    public static void main(String args[]) {
        //System.load("C:\\Users\\User\\Documents\\Visual Studio 2010\\Projects\\MySQLHashDecrypter\\x64\\Release\\MySQLHashDecrypterwin64.dll");
        LoadLibraryUtil loadLibraryUtil = LoadLibraryUtil.getInstance();
        loadLibraryUtil.initializeLibs();

        //System.out.println(System.getProperties());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        loadLibraryUtil.loadLibrary();
        MainGUI gui = MainGUI.getInstance();
        gui.setVisible(true);
        gui.setLocationRelativeTo(null);
        //new Thread(new DecryptThreadUtil("5825244c4d534d25")).start();
        //new Thread(new DecryptThreadUtil("5825244c4d534d24")).start();

    }
}
