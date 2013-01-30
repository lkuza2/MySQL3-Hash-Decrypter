package com.darkprograms.mysqldecrypter.util;

import com.darkprograms.mysqldecrypter.gui.MainGUI;

import java.util.Iterator;

/**
 * Thread that checks for completion of all cracking threads and starts new ones
 *
 * @author TheShadow
 */
public class CheckCompletionThread implements Runnable {

    /**
     * Checks if program is complete.  Also starts new threads and makes sure the amount <br></br>
     * of threads running is equal to the amound the user wanted running.
     */
    public void run() {
        MainUtil mu = MainUtil.getInstance();
        int threads = mu.getThreads();
        int runningThreads;
        String hash;
        Iterator<String> iterator;

        while (mu.getRunningState()) {
            if (mu.isComplete()) {
                MainGUI.getInstance().complete();
            }
            runningThreads = mu.lockedHashesSize();
            iterator = mu.getAllHashes();

            if (runningThreads != threads && mu.isHashAvailable()) {

                while (iterator.hasNext()) {
                    hash = iterator.next();
                    if (mu.getDecryptedPassword(hash) == null && mu.isHashLocked(hash) == false) {
                        mu.addLock(hash);
                        new Thread(new DecryptThreadUtil(hash)).start();
                        break;
                    }
                }
            }
            try {
                Thread.sleep(4000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
