package com.darkprograms.mysqldecrypter.util;

/**
 * DecryptThreadUtil class, thread that decrypts hashes via native method
 */
public class DecryptThreadUtil implements Runnable {

    /**
     * Hash to crack
     */
    private String hash;

    /**
     * Constructor for the class/thread
     *
     * @param hash Hash to crack/decrypt
     */
    public DecryptThreadUtil(String hash) {
        this.hash = hash;
    }

    /**
     * Method that starts to decrypt hash.  Removes lock when done
     */
    public void run() {
        MainUtil mu = MainUtil.getInstance();

        mu.addDecryptedPasswordToEncrypted(hash, HashUtil.getInstance().getDecryptedHash(hash));
        mu.removeLock(hash);
        return;
    }


}
