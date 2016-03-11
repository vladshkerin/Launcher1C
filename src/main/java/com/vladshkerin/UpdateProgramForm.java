package com.vladshkerin;

import com.vladshkerin.exception.FTPException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The form for program updates.
 */
public class UpdateProgramForm extends JDialog {

    private static final Logger logger = Logger.getLogger("com.vladshkerin.launcher1c");

    private static final int WIDTH_WINDOW = 500;
    private static final int HEIGHT_WINDOW = 100;

    private JLabel textLabel;
    private JButton updateButton;
    private JButton closeButton;

    private UpdateProgram updateProgram;

    public UpdateProgramForm(JFrame parent) {
        super(parent, Resource.getString("UpdateForm"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.log(Level.FINE, "Error set look and feel in update form.");
        }
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
        setSize(WIDTH_WINDOW, HEIGHT_WINDOW);

        add(createGUI(), BorderLayout.CENTER);
        setVisible(true);

        updateProgram = new UpdateProgram();
    }

    public void runCheckUpdate() {
        setTextLabel(Resource.getString("strWaitVersionUpdate") + "...");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (updateProgram.isUpdate()) {
                        setTextLabel(Resource.getString("strNewVersionUpdate")
                                + " \"" + Resource.getString("MainForm") + "\""
                                + " v." + updateProgram.getNewVersion());

                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                updateButton.setVisible(true);
                            }
                        });
                    } else {
                        setTextLabel(Resource.getString("strLatestVersionUpdate")
                                + " \"" + Resource.getString("MainForm") + "\"");
                    }
                } catch (FTPException e) {
                    setTextLabel(Resource.getString("strErrorUpdate"));
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void runUpdate() {
        setTextLabel(Resource.getString("strWaitUpdate") + "...");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (updateProgram.update()) {
                        setTextLabel(Resource.getString("strCompleteVersionUpdate")
                                + " v." + updateProgram.getNewVersion());

                        String text = Resource.getString("strCompleteUpdate") + "."
                                + "\n" + Resource.getString("strRestartProgram") + ".";
                        JOptionPane.showMessageDialog(null,
                                text,
                                Resource.getString("InformationForm"),
                                JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        String text = Resource.getString("strFileNotFoundUpdate")
                                + " \"" + Resource.getString("MainForm") + "\""
                                + ".jar";
                        JOptionPane.showMessageDialog(null,
                                text,
                                Resource.getString("WarningForm"),
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (FTPException e) {
                    setTextLabel(Resource.getString("strErrorUpdate") + ".");
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (updateButton == e.getSource()) {
                runUpdate();
                updateButton.setEnabled(false);
            } else if (closeButton == e.getSource()) {
                dispose();
            }
        }
    }

    private JPanel createGUI() {
        updateButton = new JButton(Resource.getString("UpdateButton"));
        closeButton = new JButton(Resource.getString("CloseButton"));
        textLabel = new JLabel(Resource.getString("strCheckVersionUpdate") + "...");

        ButtonListener buttonListener = new ButtonListener();
        updateButton.addActionListener(buttonListener);
        closeButton.addActionListener(buttonListener);

        updateButton.setVisible(false);

        JPanel gridText = new JPanel(new GridLayout(1, 1, 5, 0));
        gridText.add(textLabel);

        JPanel gridButton = new JPanel(new GridLayout(1, 2, 5, 0));
        gridButton.add(updateButton);
        gridButton.add(closeButton);

        JPanel flow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flow.add(gridButton);

        JPanel main = new JPanel(new GridLayout(2, 1, 5, 0));
        main.add(gridText, BorderLayout.NORTH);
        main.add(flow, BorderLayout.SOUTH);

        return main;
    }

    private void setTextLabel(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textLabel.setText(text);
            }
        });
    }
}
