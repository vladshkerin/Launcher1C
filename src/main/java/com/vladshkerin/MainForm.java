package com.vladshkerin;

import com.vladshkerin.exception.FTPException;
import com.vladshkerin.exception.NotFoundPathException;
import com.vladshkerin.exception.NotFoundPropertyException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main form, designed to run and update the data (configuration) 1C.
 */
public class MainForm extends JFrame {

    private static Logger log = Logger.getLogger(UpdateProgram.class.getName());

    private static final int MINIMUM_WIDTH_WINDOW = 400;
    private static final int MINIMUM_HEIGHT_WINDOW = 250;
    private static final int MAXIMIM_WIDTH_WINDOW = 600;
    private static final int MAXIMIM_HEIGHT_WINDOW = 400;

    private final JTextArea textArea = new JTextArea();
    private JProgressBar progressBar = new JProgressBar();
    private JButton enterpriseButton = new JButton();
    private JButton configButton = new JButton();
    private JButton updateButton = new JButton();
    private JButton exitButton = new JButton();
    private JScrollBar scrollBar = new JScrollBar();
    private JScrollPane scrollPane = new JScrollPane();

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
        setMinimumSize(new Dimension(MINIMUM_WIDTH_WINDOW, MINIMUM_HEIGHT_WINDOW));
        setMaximumSize(new Dimension(MAXIMIM_WIDTH_WINDOW, MAXIMIM_HEIGHT_WINDOW));
        setSizeWindow();
        setPositionWindow();

        add(createGUI());

//        runCheckUpdate();
    }

    public class TaskPool implements Runnable {

        private Operations[] poolOperations;

        public TaskPool(Operations[] pool) {
            this.poolOperations = pool;
        }

        public TaskPool(Operations operation) {
            this.poolOperations = new Operations[]{operation};
        }

        @Override
        public void run() {
            try {
                Command.checkDefaultPath();
            } catch (NotFoundPathException e) {
                JOptionPane.showMessageDialog(null,
                        Resource.getString("strPathNotFound") + ":\n\"" + e.getMessage() + "\"",
                        Resource.getString("ErrorForm"),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    progressBar.setMinimum(0);
                    progressBar.setMaximum(poolOperations.length);

                    enterpriseButton.setEnabled(false);
                    updateButton.setEnabled(false);
                }
            });

            int progress = 0;
            for (Operations operation : poolOperations) {
                try {
                    synchronized (textArea) {
                        TaskWorker taskWorker = new TaskWorker(operation, ++progress);
                        taskWorker.execute();

                        textArea.wait();
                    }

                    if (Operations.UNLOAD_DB.equals(operation)) {
                        try {
                            Settings.setProperty("last.date.unload_db",
                                    new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()));
                            Settings.storeProperties();
                        } catch (IOException e) {
                            //TODO empty
                        }
                    }
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Error wait TaskWorker: ", e.getMessage());
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    enterpriseButton.setEnabled(true);
                    updateButton.setEnabled(true);
                }
            });

            Toolkit.getDefaultToolkit().beep();

            JOptionPane.showConfirmDialog(null,
                    Resource.getString("strCompleteUpdate") + "!",
                    Resource.getString("WarningForm"),
                    JOptionPane.DEFAULT_OPTION);
        }
    }

    public class TaskWorker extends SwingWorker<Void, Void> {

        private Operations operation;
        private int progress;

        public TaskWorker(Operations operation, int progress) {
            this.operation = operation;
            this.progress = progress;
        }

        @Override
        public Void doInBackground() {
            publish();

            ProcessBuilder processBuilder = new ProcessBuilder(Command.getString(operation));
            processBuilder.redirectErrorStream(true);

            try {
                Process process = processBuilder.start();
                if (operation == Operations.ENTERPRISE || operation == Operations.CONFIG) {
                    TimeUnit.SECONDS.sleep(1);
                } else {
                    process.waitFor();
                }
            } catch (InterruptedException | IOException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage(),
                        Resource.getString("ErrorForm"),
                        JOptionPane.ERROR_MESSAGE);
            }

            return null;
        }

        @Override
        protected void process(List<Void> chunks) {
            String date = new SimpleDateFormat("kk:mm:ss").format(System.currentTimeMillis());
            textArea.append(date + " " +
                    Resource.getString("strStartOperation") + " " +
                    operation.toString() + " . . . ");
        }

        @Override
        public void done() {
            if (operation == Operations.ENTERPRISE) {
                System.exit(0);
            } else {
                synchronized (textArea) {
                    progressBar.setValue(progress);
                    textArea.append(Resource.getString("strCompleteOperation") + "\n");
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());

                    textArea.notify();
                }
            }
        }
    }

    protected class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent event) {
            runDialogExit();
        }
    }

    protected class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("enterpriseButton")) {
                Thread thread = new Thread(new TaskPool(Operations.ENTERPRISE));
                thread.start();
            } else if (e.getActionCommand().equals("configButton")) {
                Thread thread = new Thread(new TaskPool(Operations.CONFIG));
                thread.start();
            } else if (e.getActionCommand().equals("updateButton")) {
                Thread thread = new Thread(new TaskPool(createPool()));
                thread.start();
            }
        }
    }

    protected class UpdateAction extends AbstractAction {

        UpdateAction() {
            putValue(NAME, Resource.getString("CheckUpdateButton"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
//            new UpdateForm(Launcher1C.MainForm);
            new UpdateForm(new JFrame());
        }
    }

    protected class ExitAction extends AbstractAction {

        ExitAction() {
            putValue(NAME, Resource.getString("ExitButton"));
        }

        public void actionPerformed(ActionEvent e) {
            runDialogExit();
        }
    }

    private JPanel createGUI() {

        WindowListener windowListener = new WindowListener();
        addWindowListener(windowListener);

        ButtonListener buttonListener = new ButtonListener();
        enterpriseButton.addActionListener(buttonListener);
        configButton.addActionListener(buttonListener);
        updateButton.addActionListener(buttonListener);
        exitButton.addActionListener(new ExitAction());

        enterpriseButton.setActionCommand("enterpriseButton");
        configButton.setActionCommand("configButton");
        updateButton.setActionCommand("updateButton");
        exitButton.setActionCommand("exitButton");

        enterpriseButton.setText(Resource.getString("EnterpriseButton"));
        configButton.setText(Resource.getString("ConfigButton"));
        updateButton.setText(Resource.getString("UpdateButton"));
        exitButton.setText(Resource.getString("ExitButton"));

        enterpriseButton.setToolTipText(Resource.getString("strToolTipEnterpriseButton"));
        configButton.setToolTipText(Resource.getString("strToolTipConfigButton"));
        updateButton.setToolTipText(Resource.getString("strToolTipUpdateButton"));
        textArea.setToolTipText(Resource.getString("strToolTipTextArea"));
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(10000);

        progressBar.setOrientation(SwingConstants.HORIZONTAL);

        scrollPane.add(textArea);
        scrollPane.add(scrollBar);

        JPanel pMain = createPanel(BoxLayout.X_AXIS);
        pMain.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel pText = createPanel(BoxLayout.Y_AXIS);
        pText.add(textArea);
        pText.add(Box.createVerticalStrut(10));
        pText.add(progressBar);

        JPanel pButton = createPanel(BoxLayout.Y_AXIS);
        pButton.add(enterpriseButton);
        pButton.add(Box.createVerticalStrut(10));
        pButton.add(configButton);
        pButton.add(Box.createVerticalStrut(10));
        pButton.add(updateButton);
        pButton.add(Box.createVerticalGlue());
        pButton.add(exitButton);

        makeSameSize(enterpriseButton, configButton, updateButton, exitButton);

        pMain.add(pText);
        pMain.add(Box.createHorizontalStrut(15));
        pMain.add(pButton);

        return pMain;
    }

    private JPanel createPanel(int boxLayout) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, boxLayout));
        return p;
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

    private void makeSameSize(JComponent... cs) {
        Dimension maxSize = cs[0].getPreferredSize();
        for (JComponent c : cs) {
            if (c.getPreferredSize().width > maxSize.width) {
                maxSize = c.getPreferredSize();
            }
        }

        for (JComponent c : cs) {
            c.setPreferredSize(maxSize);
            c.setMinimumSize(maxSize);
            c.setMaximumSize(maxSize);
        }
    }

    private Operations[] createPool() {
        Operations[] arrayOperations;
        Calendar currentCalendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        Calendar lastCalendar = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        try {
            String strLastDate = Settings.getString("last.date.unload_db");
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy");
            lastCalendar.setTime(format.parse(strLastDate));
            lastCalendar.add(Calendar.DAY_OF_YEAR, 7);
        } catch (NotFoundPropertyException | ParseException e) {
            lastCalendar = currentCalendar;
            log.log(Level.WARNING, e.getMessage());
        }

        if (lastCalendar.compareTo(currentCalendar) <= 0) {
            arrayOperations = new Operations[]{
                    Operations.KILL, Operations.UNLOAD_DB, Operations.UPDATE,
                    Operations.UPGRADE, Operations.TEST, Operations.UPDATE
            };
        } else {
            arrayOperations = new Operations[]{
                    Operations.KILL, Operations.UPDATE,
                    Operations.UPGRADE, Operations.UPDATE
            };
        }

        return arrayOperations;
    }

    private void setSizeWindow() {

        int defWidthWindow = (int) getSize().getWidth();
        int defHeightWindow = (int) getSize().getHeight();

        int widthWindow;
        int heightWindow;
        try {
            widthWindow = Integer.parseInt(Settings.getString("width.size.window"));
            heightWindow = Integer.parseInt(Settings.getString("height.size.window"));

            if (widthWindow > MAXIMIM_WIDTH_WINDOW || heightWindow > MAXIMIM_HEIGHT_WINDOW
                    || widthWindow < MINIMUM_WIDTH_WINDOW || heightWindow < MINIMUM_HEIGHT_WINDOW) {
                throw new NotFoundPropertyException("Loaded size does not correspond " +
                        "to the maximum or minimum window sizes");
            }
            if (widthWindow > MAXIMIM_WIDTH_WINDOW || heightWindow > MAXIMIM_HEIGHT_WINDOW) {
                throw new NotFoundPropertyException("Loaded size does not correspond " +
                        "to the maximum or minimum window sizes");
            }
        } catch (NotFoundPropertyException | NumberFormatException e) {
            widthWindow = defWidthWindow;
            heightWindow = defHeightWindow;
            log.log(Level.WARNING, e.getMessage());
        }

        setSize(widthWindow, heightWindow);
    }

    private void setPositionWindow() {

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int defPositionX = (int) ((dimension.getWidth() - getWidth()) / 2);
        int defPositionY = (int) ((dimension.getHeight() - getHeight()) / 2);

        int positionX;
        int positionY;
        try {
            positionX = Integer.parseInt(Settings.getString("width.position.window"));
            positionY = Integer.parseInt(Settings.getString("height.position.window"));

            if (positionX > dimension.getWidth() || positionY > dimension.getHeight()) {
                throw new NotFoundPropertyException("Loaded position of the window exceeds screen size");
            }
        } catch (NotFoundPropertyException | NumberFormatException e) {
            positionX = defPositionX;
            positionY = defPositionY;
            log.log(Level.WARNING, e.getMessage());
        }

        setLocation(positionX, positionY);
    }

    private void runDialogExit() {

        int res = JOptionPane.showConfirmDialog(null,
                Resource.getString("strQuestionExit"),
                Resource.getString("QuestionForm"),
                JOptionPane.YES_NO_OPTION);

        if (res == JOptionPane.YES_OPTION) {
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
        }
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
}
