package com.vladshkerin;

import com.vladshkerin.exception.FTPException;
import com.vladshkerin.exception.NotFoundPropertyException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main form, designed to run and update the data (configuration) 1C.
 */
public class MainForm extends JFrame {

    private static Logger log = Logger.getLogger(MainForm.class.getName());

    private final JTextArea textArea = new JTextArea();
    private JButton enterpriseButton = new JButton();
    private JButton configButton = new JButton();
    private JButton updateButton = new JButton();
    private JButton testButton = new JButton();
    private JButton exitButton = new JButton();

    public MainForm() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            URL imageResource = Launcher1C.class.getResource("/images/ariant.png");
            setIconImage(ImageIO.read(imageResource));
        } catch (Exception e) {
            log.log(Level.WARNING, "Error set look and feel in main form");
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
        setSizeWindow();
        setPositionWindow();

        add(createGUI());

        runCheckUpdate();
    }

    protected class WindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            runSaveSettingsAndExit();
        }
    }

    protected class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("enterpriseButton")) {
                runProcessBuilder(Operations.ENTERPRISE);
                runSaveSettingsAndExit();
            } else if (e.getActionCommand().equals("configButton")) {
                runProcessBuilder(Operations.CONFIG);
                runSaveSettingsAndExit();
            } else if (e.getActionCommand().equals("updateButton")) {
                UpdateBaseForm form = new UpdateBaseForm(MainForm.this);
                form.runUpdateBase();
            } else if (e.getActionCommand().equals("testButton")) {
                runProcessBuilder(Operations.TEST);
            }
        }
    }

    protected class UpdateAction extends AbstractAction {

        UpdateAction() {
            putValue(NAME, Resource.getString("CheckUpdateButton"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new UpdateForm(MainForm.this);
        }
    }

    protected class ExitAction extends AbstractAction {

        ExitAction() {
            putValue(NAME, Resource.getString("ExitButton"));
        }

        public void actionPerformed(ActionEvent e) {
            runSaveSettingsAndExit();
        }
    }

    private JPanel createGUI() {
        WindowListener windowListener = new WindowListener();
        addWindowListener(windowListener);

        ButtonListener buttonListener = new ButtonListener();
        enterpriseButton.addActionListener(buttonListener);
        configButton.addActionListener(buttonListener);
        updateButton.addActionListener(buttonListener);
        testButton.addActionListener(buttonListener);
        exitButton.addActionListener(new ExitAction());

        enterpriseButton.setActionCommand("enterpriseButton");
        configButton.setActionCommand("configButton");
        updateButton.setActionCommand("updateButton");
        testButton.setActionCommand("testButton");
        exitButton.setActionCommand("exitButton");

        enterpriseButton.setText(Resource.getString("EnterpriseButton"));
        configButton.setText(Resource.getString("ConfigButton"));
        updateButton.setText(Resource.getString("UpdateButton"));
        testButton.setText(Resource.getString("TestButton"));
        exitButton.setText(Resource.getString("ExitButton"));

        enterpriseButton.setToolTipText(Resource.getString("strToolTipEnterpriseButton"));
        configButton.setToolTipText(Resource.getString("strToolTipConfigButton"));
        updateButton.setToolTipText(Resource.getString("strToolTipUpdateButton"));
        testButton.setToolTipText(Resource.getString("strToolTipTestButton"));

        textArea.setBorder(BorderFactory.createLineBorder(Color.lightGray));

        JPanel pMain = BoxLayoutUtils.createHorizontalPanel();
        pMain.setBorder(BorderFactory.createEmptyBorder(8, 5, 5, 8));

        JPanel pText = BoxLayoutUtils.createVerticalPanel();
        pText.setBorder(new CompoundBorder(
                new TitledBorder("Информационные базы"), new EmptyBorder(4, 4, 4, 4)));
        pText.add(textArea);

        JPanel pButton = BoxLayoutUtils.createVerticalPanel();
        pButton.add(BoxLayoutUtils.createVerticalStrut(6));
        pButton.add(enterpriseButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(10));
        pButton.add(configButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(25));
        pButton.add(updateButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(10));
        pButton.add(testButton);
        pButton.add(BoxLayoutUtils.createVerticalGlue());
        pButton.add(exitButton);
        pButton.add(BoxLayoutUtils.createVerticalStrut(3));

        GUITools.makeSameSize(enterpriseButton, configButton, updateButton, testButton, exitButton);

        BoxLayoutUtils.setGroupAlignmentY(Component.TOP_ALIGNMENT, pText, pButton, pMain);

        pMain.add(pText);
        pMain.add(Box.createHorizontalStrut(7));
        pMain.add(pButton);

        return pMain;
    }

    private JMenu createFileMenu() {

        JMenu file = new JMenu(Resource.getString("FileButton"));

        JMenuItem open = new JMenuItem(new UpdateAction());
        JMenuItem exit = new JMenuItem(new ExitAction());

        file.add(open);
        file.addSeparator();
        file.add(exit);

        return file;
    }

    private void setSizeWindow() {
        int widthWindow;
        int heightWindow;
        try {
            widthWindow = Integer.parseInt(Settings.getString("width.size.window"));
            widthWindow = (widthWindow < 400 ? 400 : widthWindow);
            heightWindow = Integer.parseInt(Settings.getString("height.size.window"));
            heightWindow = (heightWindow < 250 ? 250 : heightWindow);
        } catch (NotFoundPropertyException | NumberFormatException e) {
            widthWindow = 450;
            heightWindow = 300;
            log.log(Level.WARNING, e.getMessage());
        }
        setSize(widthWindow, heightWindow);
    }

    private void setPositionWindow() {
        Dimension dimScreen = Toolkit.getDefaultToolkit().getScreenSize();
        int defPositionX = (int) ((dimScreen.getWidth() - getWidth()) / 2);
        int defPositionY = (int) ((dimScreen.getHeight() - getHeight()) / 2);

        int positionX;
        int positionY;
        try {
            positionX = Integer.parseInt(Settings.getString("width.position.window"));
            positionY = Integer.parseInt(Settings.getString("height.position.window"));

            if (positionX > dimScreen.getWidth() || positionY > dimScreen.getHeight()) {
                throw new NotFoundPropertyException("Loaded position of the window exceeds screen size");
            }
        } catch (NotFoundPropertyException | NumberFormatException e) {
            positionX = defPositionX;
            positionY = defPositionY;
            log.log(Level.WARNING, e.getMessage());
        }

        setLocation(positionX, positionY);
    }

    private void runSaveSettingsAndExit() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> mapSettings = new LinkedHashMap<>();
                mapSettings.put("width.size.window", String.valueOf((int) getSize().getWidth()));
                mapSettings.put("height.size.window", String.valueOf((int) getSize().getHeight()));
                mapSettings.put("width.position.window", String.valueOf(getX()));
                mapSettings.put("height.position.window", String.valueOf(getY()));
                Settings.setProperties(mapSettings);
                try {
                    Settings.storeProperties();
                } catch (IOException e) {
                    log.log(Level.WARNING, e.getMessage());
                }
                System.exit(0);
            }
        });
        t.setDaemon(true);
        t.start();
        setVisible(false);
    }

    private void runCheckUpdate() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    UpdateProgram updateProgram = new UpdateProgram();
                    if (updateProgram.isUpdate()) {

                        StringBuilder sbMsg = new StringBuilder();
                        sbMsg.append(String.format("%s\n\n",
                                Resource.getString("strNewVersionUpdate") +
                                        " \"" + Resource.getString("MainForm") + "\"."));
                        sbMsg.append(String.format("%-24s%s\n",
                                Resource.getString("strCurrentVersion") + ":",
                                "v" + Resource.getString("Application.version")));
                        sbMsg.append(String.format("%-26s%s\n",
                                Resource.getString("strNewVersion") + ":",
                                "v" + updateProgram.getNewVersion()));
                        sbMsg.append(String.format(Resource.getCurrentLocale(), "%-21s%.3f MB\n\n",
                                Resource.getString("strSizeUpdate") + ":",
                                updateProgram.getSizeFile() / 1024.0 / 1024.0));
                        sbMsg.append(String.format("%s",
                                Resource.getString("strToUpgrade") + "?"));

                        int res = JOptionPane.showConfirmDialog(null,
                                sbMsg,
                                Resource.getString("UpdateForm"),
                                JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {

                            if (updateProgram.update()) {
                                String text = Resource.getString("strCompleteUpdate") + "."
                                        + "\n" + Resource.getString("strRestartProgram") + ".";
                                JOptionPane.showMessageDialog(null,
                                        text,
                                        Resource.getString("InformationForm"),
                                        JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            }
                        }
                    }
                } catch (FTPException e) {
                    log.log(Level.SEVERE, e.getMessage());
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void runProcessBuilder(Operations operations) {
        ProcessBuilder process = new ProcessBuilder(
                Command.getString(operations));
        try {
            process.start();
        } catch (IOException f) {
            JOptionPane.showMessageDialog(null,
                    f.getMessage(),
                    Resource.getString("ErrorForm"),
                    JOptionPane.ERROR_MESSAGE);
            log.log(Level.SEVERE, f.getMessage());
        }
    }
}
