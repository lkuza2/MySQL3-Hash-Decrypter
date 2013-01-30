package com.darkprograms.mysqldecrypter.util;

import com.darkprograms.mysqldecrypter.gui.MainGUI;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 8/6/11
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateUIUtil implements Runnable {


    public void run() {
        MainUtil mu = MainUtil.getInstance();
        MainGUI mgui = MainGUI.getInstance();
        while (mu.getRunningState()) {
            mgui.updateAllStatus();
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return;
    }


}
