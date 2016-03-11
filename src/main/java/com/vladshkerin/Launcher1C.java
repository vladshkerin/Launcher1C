package com.vladshkerin;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * The class starts the main form of the gui interface.
 *
 * @author Shkerin Vladimir Nikolaevich
 */
public class Launcher1C {

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                    Launcher1C.class.getResourceAsStream("/logging.properties"));
            Logger.getLogger("com.vladshkerin.launcher1c").setLevel(Level.FINE);
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainForm form = new MainForm();
                form.setTitle(Resource.getString("MainForm"));
                form.setVisible(true);
            }
        });
    }
}