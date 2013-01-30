package com.darkprograms.mysqldecrypter.util;


import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * MainUtil class that holds most of the main variables and functions
 *
 * @author TheShadow
 */
public class MainUtil {

    /**
     * Instance for MainUtil
     */
    private static MainUtil instance;

    /**
     * Method that gets instance of MainUtil
     *
     * @return Returns instance
     */
    public static MainUtil getInstance() {
        if (instance == null) {
            instance = new MainUtil();
        }
        return instance;
    }

    /**
     * MainUtil constructor
     */
    private MainUtil() {

    }

    /**
     * String that contains the code for a hash that is invalid
     */
    public static final String INVALID_HASH_CODE = "<font color='red'>Invalid Hash.</font>";
    /**
     * String that contains the code for a hash that its password is not found
     */
    public static final String PASSWORD_NOT_FOUND = "<font color='red'>Password not found!.</font>";

    /**
     * Hash map the holds the encrypted hash in the key and the decrypted in the value.
     * Value is null if hash is currently being cracked or is to be cracked
     */
    private HashMap<String, String> passwords = new HashMap<String, String>();
    /**
     * Hash set that contains the encrypted hashes which are "locked".  These hashes are currently being cracked by a thread(s)
     * This is to ensure multiple threads to try to decrypt the same hash
     */
    private HashSet<String> lockedHashes = new HashSet<String>();

    /**
     * If the program is running this is true
     */
    private boolean isRunning = false;

    /**
     * Number of dots to show after Waiting text or Decrypting text
     */
    private int dots = 1;

    /**
     * Number of threads to run specified by the user
     */
    private int threads;

    /**
     * String writer thats data is used for the decrypted hashses Jeditorpane
     */
    private StringWriter strWriter = new StringWriter();

    /**
     * Adds a decrypted password to its encrypted key to the HashMap
     *
     * @param encrypted encrypted hash to add the decrypted password to
     * @param decrypted decrypted password to add
     */
    public void addDecryptedPasswordToEncrypted(String encrypted, String decrypted) {
        passwords.put(encrypted, decrypted);
    }

    /**
     * Adds an encrypted hash key to the HashMap, with its value being null
     *
     * @param encrypted Encrypted hash to add
     */
    public void addEncryptedPassword(String encrypted) {
        passwords.put(encrypted, null);
    }

    /**
     * Gets the decrypted value for the encrypted key from the HashMap
     *
     * @param encrypted Encrypted hash to get decrypted from
     * @return Returns null or the decrypted password, if any.
     */
    public String getDecryptedPassword(String encrypted) {
        return passwords.get(encrypted);
    }

    /**
     * Gets the size/amount of hashes in the password HashMap
     *
     * @return Returns an integer of the size
     */
    public int getHashesSize() {
        return passwords.size();
    }

    /**
     * Sets the current running state of the program
     *
     * @param state Set true if program is running, false otherwise.
     */
    public void setRunningState(boolean state) {
        isRunning = state;
    }

    /**
     * Gets the running state of the program
     *
     * @return True if running, false otherwise.
     */
    public boolean getRunningState() {
        return isRunning;
    }

    /**
     * Sets the amount of dots to show for the Waiting... or Decrypting text on the UI
     *
     * @param numberOfDots Number of dots to set
     */
    public void setDots(int numberOfDots) {
        dots = numberOfDots;
    }

    /**
     * Gets the amount of dots to set
     *
     * @return Number of dots
     */
    public int getDots() {
        return dots;
    }

    /**
     * Sets the amount of threads to run specified by the user
     *
     * @param threads Amount of threads
     */
    public void setThreads(int threads) {
        this.threads = threads;
    }

    /**
     * Gets the amount of threads to run specified by the user
     *
     * @return Number of threads
     */
    public int getThreads() {
        return threads;
    }

    /**
     * Adds a string to the StringWriter, appending it to the Writer
     *
     * @param str String to write
     */
    public void addStringToStrWriter(String str) {
        strWriter.append(str);
        strWriter.flush();
    }

    /**
     * Gets all text from the buffer of the StringWriter
     *
     * @return Returns a string of all text from the buffer
     */
    public String getTextFromBuffer() {
        return strWriter.getBuffer().toString();
    }

    /**
     * Clears the StringWriter by reintilizing it.
     */
    public void clearStringWriter() {
        strWriter = new StringWriter();
    }

    /**
     * Clears everything, the StringWriter, passswords hashmap, and the lockedHashes hashmap
     */
    public void clearAll() {
        clearStringWriter();
        passwords.clear();

        lockedHashes.clear();

    }

    /**
     * Adds a lock to a specified hash
     *
     * @param hash Hash to lock
     */
    public void addLock(String hash) {
        lockedHashes.add(hash);
    }

    /**
     * Checks if a hash is locked/being used by a thread
     *
     * @param hash Hash to check for
     * @return Returns true if the hash is locked, false otherwise
     */
    public boolean isHashLocked(String hash) {
        if (lockedHashes.contains(hash)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a lock to a hash
     *
     * @param hash Hash to remove lock
     */
    public void removeLock(String hash) {
        lockedHashes.remove(hash);
    }

    /**
     * Returns an Iterator for the keys to the password HashMap
     *
     * @return Iterator of key set
     */
    public Iterator<String> getAllHashes() {
        return passwords.keySet().iterator();
    }

    /**
     * Gets size of lockedHashes HashSet
     *
     * @return Returns an integer of size
     */
    public int lockedHashesSize() {
        return lockedHashes.size();
    }

    /**
     * Checks if there is a hash available to crack that isn't locked and hasn't already been cracked.
     *
     * @return Returns true if available, false otherwise
     */
    public boolean isHashAvailable() {
        String hash;
        Iterator<String> hashes = getAllHashes();
        while (hashes.hasNext()) {
            hash = hashes.next();
            if (getDecryptedPassword(hash) == null && isHashLocked(hash) != true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the program is done cracking by seeing if there are any value in passwords
     * HashMap that are null
     *
     * @return Returns true if complete, false otherwise
     */
    public boolean isComplete() {
        if (!passwords.containsValue(null)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Starts the decrypting threads.  This method starts a thread which continuously starts the separate decrypting threads.
     */
    public void startDecrypt() {
        new Thread(new CheckCompletionThread()).start();
    }

}
