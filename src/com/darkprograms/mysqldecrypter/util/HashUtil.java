package com.darkprograms.mysqldecrypter.util;

/**
 * HashUtil class that contains the Native method to decrypt a old MySQL hash
 *
 * @author TheShadow
 */
public class HashUtil {

    /**
     * Native method that calls the external cracking function from a dynamic library via the JNI<br>
     *
     * @param hash Hash you want to crack
     * @return Returns a integer array of of each characters ascii integer.
     */
    private static native int[] getHash(String hash);

    /**
     * Instance variable for HashUtil
     */
    private static HashUtil instance;

    /**
     * Gets the instance for HashUtil
     *
     * @return The instance
     */
    public static HashUtil getInstance() {
        if (instance == null) {
            instance = new HashUtil();
        }
        return instance;
    }

    /**
     * Constructor for HashUtil
     */
    private HashUtil() {

    }

    /**
     * Method that calls native method to decrypt a hash.  Converts integers to a string by casting each by (char)<br>
     * Error code 300 is an invalid hash and 400 is that the password was not found<br>
     * Is synchronised to prevent crashing when multiple threads call the native method at once.
     *
     * @param hash The hash to decrypt
     * @return Returns the string representation of the password
     */
    public synchronized String getDecryptedHash(String hash) {
        int[] password = getHash(hash);
        int currentLetter;
        int index = 0;

        if (password.length < 12) {
            if (password[0] == 300) {
                return MainUtil.INVALID_HASH_CODE;

            } else if (password[0] == 400) {
                return MainUtil.PASSWORD_NOT_FOUND;
            }
        }

        char[] chrPassword = new char[12];

        while ((currentLetter = password[index]) != 0) {
            if (currentLetter > 255) {
                break;
            }
            chrPassword[index] = (char) currentLetter;
            index++;
        }
        return "<font color='green'>" + String.copyValueOf(chrPassword) + "</font>";
    }

    /**
     * Java method called via external dynamic library.  Not used in the Java program at all.<br>
     * Dynamic library calls this method to see if the user has pressed stop in order for the library to stop<br>
     * so its running thread can stop.
     *
     * @return Returns true if program is complete, false otherwise
     */
    public boolean complete() {
        if (MainUtil.getInstance().getRunningState() == false) {
            return true;
        } else {
            return false;
        }
    }

}
