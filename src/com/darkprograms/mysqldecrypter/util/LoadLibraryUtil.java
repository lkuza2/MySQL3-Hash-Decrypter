package com.darkprograms.mysqldecrypter.util;


import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;

/**
 * LoadLibraryUtil class that loads the required native dynamic library
 *
 * @author TheShadow
 */
public class LoadLibraryUtil {

    /**
     * Variable that holds LoadLibraryUtil instance
     */
    private static LoadLibraryUtil instance;

    /**
     * Gets the instance for LoadLibaryUtil
     *
     * @return Returns the instance
     */
    public static LoadLibraryUtil getInstance() {
        if (instance == null) {
            instance = new LoadLibraryUtil();
        }
        return instance;
    }

    /**
     * Variable that holds the key for Windows 32-bit
     */
    private static final String WINDOWS_32 = "win32";
    /**
     * Variable that holds the key for Windows 64-bit
     */
    private static final String WINDOWS_64 = "win64";
    /**
     * Variable that holds the key for linux 32 bit
     */
    private static final String LINUX_32 = "lin32";
    /**
     * Path to libs directory and part of the dynamic library's file name
     */
    private static final String LIBRARY = "/com/darkprograms/mysqldecrypter/libs/MySQLHashDecrypter";

    /**
     * HashMap that holds the library name, ex. win32 and its MD5 hash
     */
    HashMap<String, String> libs = new HashMap<String, String>();

    /**
     * Gets the MD5 hash of a file
     *
     * @param path Path to file
     * @return Returns hexadecimal String representation of hash
     */
    public String getMD5OfFile(String path) {
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[256];
            int read;

            StringBuilder hexString = new StringBuilder();
            while ((read = fileInputStream.read(bytes, 0, bytes.length)) != -1) {

                digest.update(bytes, 0, read);

            }
            byte[] hash = digest.digest();
            fileInputStream.close();

            for (int i = 0; i < hash.length; i++) {
                hexString.append(Integer.toHexString((hash[i] & 0xFF) | 0x100).substring(1, 3));
            }

            return hexString.toString();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Gets MD5 hash of data from an InputStream
     *
     * @param inputStream InputStream to get data from
     * @return Returns hexadecimal String representation of hash
     */
    public String getMD5OfStream(InputStream inputStream) {
        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[256];
            int read;

            StringBuffer hexString = new StringBuffer();
            while ((read = inputStream.read(bytes, 0, bytes.length)) != -1) {

                digest.update(bytes, 0, read);

            }
            byte[] hash = digest.digest();
            inputStream.close();

            for (int i = 0; i < hash.length; i++) {
                hexString.append(Integer.toHexString((hash[i] & 0xFF) | 0x100).substring(1, 3));
            }

            return hexString.toString();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes what libraries are included within the program and their MD5 hash
     */
    public void initializeLibs() {
        libs.put(LoadLibraryUtil.WINDOWS_32, "6a5011ac99a28aed8c158ace03f03cd5");
        libs.put(LoadLibraryUtil.WINDOWS_64, "070068e7e381371f11fdda507fd22cc0");
        libs.put(LoadLibraryUtil.LINUX_32, "ebd3278552d297825e1d35fbaf067f5a");
    }

    /**
     * Gets the system type representing a the text found in the keys of the libs HashMap
     *
     * @return Returns system type or OSNAME:OSARCH if system is not recognized
     */
    public String getSystemType() {
        String osname = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        if (osname.toLowerCase().contains("windows") && arch.toLowerCase().equals("x86")) {
            return LoadLibraryUtil.WINDOWS_32;
        } else if (osname.toLowerCase().contains("windows") && arch.toLowerCase().contains("amd64")) {
            return LoadLibraryUtil.WINDOWS_64;
        } else if (osname.toLowerCase().contains("linux") && arch.toLowerCase().contains("i386")) {
            return LoadLibraryUtil.LINUX_32;
        } else {
            return osname + ":" + arch;
        }
    }

    /**
     * Attempts to load a working native library for the program to use many ways
     */
    public void loadLibrary() {
        String systemType = getSystemType();
        int response;
        if (systemType.equals(LoadLibraryUtil.WINDOWS_32)) {
            response = sendMessageYesNoCancel("<html>The detected system is Windows 32-bit.  Press \"Yes\" to automatically load the required library.<br>" +
                    "If the information displayed is incorrect, press \"No\" to load your own library file.  Press \"Cancel\" to exit. </html>", "System");
            handleResponse(response, systemType);
        } else if (systemType.equals(LoadLibraryUtil.WINDOWS_64)) {
            response = sendMessageYesNoCancel("<html>The detected system is Windows 64-bit.  Press \"Yes\" to automatically load the required library.<br>" +
                    "If the information displayed is incorrect, press \"No\" to load your own library file.  Press \"Cancel\" to exit. </html>", "System");
            handleResponse(response, systemType);
        } else if (systemType.equals(LoadLibraryUtil.LINUX_32)) {
            response = sendMessageYesNoCancel("<html>The detected system is Linux 32-bit.  Press \"Yes\" to automatically load the required library.<br>" +
                    "If the information displayed is incorrect, press \"No\" to load your own library file.  Press \"Cancel\" to exit. </html>", "System");
            handleResponse(response, systemType);
        } else {
            String[] systemAndArch = systemType.split(":");
            response = sendMessageYesNo("<html>The detected system is " + systemAndArch[0] + " and the architecture is " + systemAndArch[1] + "-bit.<br>" +
                    "This program does not have a supported library for your system.  Would you like to load one manually?", "System Error", true);
            if (response == 0) {
                loadCustomLibraryFile();
            } else if (response == 1 || response == -1) {
                System.exit(0);
            }
        }
    }

    /**
     * Handles response from user.  Only handeled from a dialog in which the system is recognized.
     *
     * @param response   Integer representing response from dialog.
     * @param systemType System type of known system
     */
    public void handleResponse(int response, String systemType) {
        if (response == 0) {
            boolean linux = systemType.equals(LoadLibraryUtil.LINUX_32);
            loadAndVerifyLibrary(systemType, linux);
        } else if (response == 1) {
            loadCustomLibraryFile();
        } else if (response == 2 || response == -1) {
            System.exit(0);
        }
    }

    /**
     * Loads a verified Library that the program has recognized the system and found a library for
     *
     * @param systemType Recognized system type
     */
    public void loadAndVerifyLibrary(String systemType, boolean linux) {
        String suffix;
        if (linux) {
            suffix = ".so";
        } else {
            suffix = ".dll";
        }


        InputStream inputStream = this.getClass().getResourceAsStream(LoadLibraryUtil.LIBRARY + systemType + suffix);
        int response;

        if (inputStream == null) {
            response = sendMessageYesNo("<html>Failed to load library MySQLHashDecrypter" + systemType + suffix + "<br>" +
                    "Press \"Yes\" to load your own library.  Press \"No\" to exit.</html>", "Load Error", true);
            if (response == 0) {
                loadCustomLibraryFile();
            } else if (response == 1 || response == -1) {
                System.exit(0);
            }
        } else {
            String md5 = getMD5OfStream(inputStream);
            if (libs.get(systemType).equals(md5)) {
                loadVerifiedLibrary(systemType);
                sendMessage("Library MySQLHashDecrypter" + systemType + suffix + " has been loaded and verified successfully.", "Load Complete", false);
            } else {
                response = sendMessageYesNo("<html>Failed to verify integrity of library MySQLHashDecrypter" + systemType + ".dll<br>" +
                        "Press \"Yes\" to load your own library.  Press \"No\" to exit.</html>", "Load Error", true);
                if (response == 0) {
                    loadCustomLibraryFile();
                } else if (response == 1 || response == -1) {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Loads the file from the program into the temp directory.  This will not get deleted later.
     *
     * @param systemType Recgonized system type
     */
    public void loadVerifiedLibrary(String systemType) {
        try {
            String suffix;
            if (systemType.equals(LoadLibraryUtil.LINUX_32)) {
                suffix = ".so";
            } else {
                suffix = ".dll";
            }

            String tmpdir = System.getProperty("java.io.tmpdir");
            File tmpFile = new File(tmpdir + "/MySQLHashDecrypter" + systemType + suffix);

            tmpFile.delete();

            tmpFile.deleteOnExit();
            InputStream inputStream = this.getClass().getResourceAsStream(LoadLibraryUtil.LIBRARY + systemType + suffix);
            FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
            int read;

            byte[] buffer = new byte[256];

            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            inputStream.close();
            fileOutputStream.close();

            System.load(tmpFile.getAbsolutePath());
        } catch (Exception ex) {
            sendMessage("There was an error creating the library file.  Program will now exit.", "Library Error", true);
            ex.printStackTrace();
            System.exit(0);
        } catch (UnsatisfiedLinkError ex) {
            sendMessage("There was an error linking to the library file.  Program will not exit.", "Library Error", true);
            ex.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * Starts file dialog to load a custom library from the user
     */
    public void loadCustomLibraryFile() {
        JFileChooser jFileChooser = new JFileChooser();
        File file = new File("nothing");
        do {
            if (file == null) {
                noFileSelected();
            }
            jFileChooser.showOpenDialog(null);
            file = jFileChooser.getSelectedFile();
        } while (file == null);
        loadUnverifiedLibrary(file.getAbsolutePath());
    }

    /**
     * Loads an unverified library supplied from the user and tries to see if it is verified before telling the user it is not.
     *
     * @param path Path to user supplied library.
     */
    public void loadUnverifiedLibrary(String path) {
        File file = new File(path);
        String md5 = getMD5OfFile(path);
        if (!libs.containsValue(md5)) {
            int response = sendMessageYesNo("<html>Library integrity could not be verified against known hashes.<br>" +
                    "Would you like to load the file anyways? Pressing \"No\" will exit the program.", "Integrity Error", true);
            if (response == 0) {
                try {
                    System.load(path);
                } catch (UnsatisfiedLinkError ex) {
                    ex.printStackTrace();
                    sendMessage("Library file" + file.getName() + " is invalid and could not be loaded! Program will exit.", "Library Error", true);
                    System.exit(0);
                }
                sendMessage("Library file " + file.getName() + " loaded successfully.", "Library Loaded", false);
                return;
            } else if (response == 1 || response == -1) {
                System.exit(0);
            }
        } else {
            try {
                System.load(path);
                String systemType = null;
                Iterator<String> iterator = libs.keySet().iterator();
                while (iterator.hasNext()) {
                    systemType = iterator.next();
                    if (libs.get(systemType).equals(md5)) {
                        break;
                    }
                }
                sendMessage("<html>Library file " + file.getName() + " verified and loaded successfully.<br> Library is for System Type: " + systemType + "</html>", "Library Loaded", false);
            } catch (UnsatisfiedLinkError ex) {
                sendMessage("Library file" + file.getName() + " is invalid and could not be loaded!  Program will exit.", "Library Error", true);
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }

    /**
     * Dialog for no file being selected.
     */
    public void noFileSelected() {
        int response = sendMessageYesNo("No file was selected.  Would you like to quit?", "File Selection", true);
        if (response == 0) {
            System.exit(0);
        } else {
            return;
        }
    }

    /**
     * Shows a JOptionPane.showConfirmDialog YesNoAndCancel
     *
     * @param message Message to send
     * @param title   Title of dialog
     * @return Response
     */
    public int sendMessageYesNoCancel(String message, String title) {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a JOptionPane.showConfirmDialog YESNO options.
     *
     * @param message Message to display
     * @param title   Title
     * @param error   Pass true if you want the dialog to be an error dialog, false otherwise.
     * @return Returns response
     */
    public int sendMessageYesNo(String message, String title, boolean error) {
        if (error) {
            return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
        } else {
            return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Shows a JOptionPane.showMessageDialog
     *
     * @param message Message to display
     * @param title   Title
     * @param error   Pass true if you want the dialog to be an error dialog, false otherwise.
     */
    public void sendMessage(String message, String title, boolean error) {
        if (error) {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
