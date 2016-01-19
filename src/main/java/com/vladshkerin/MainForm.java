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

    private static final int MINIMUM_WIDTH_WINDOW = 340;
    private static final int MINIMUM_HEIGHT_WINDOW = 180;
    private static final int MAXIMIM_WIDTH_WINDOW = 500;
    private static final int MAXIMIM_HEIGHT_WINDOW = 350;

    private final JTextArea textArea = new JTextArea();
    private JProgressBar progressBar = new JProgressBar();
    private JButton runButton = new JButton();
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
            log.log(Level.SEVERE, "Error set look and feel in main form");
        }

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocale(Resource.getCurrentLocale());
//        setMinimumSize(new Dimension(MINIMUM_WIDTH_WINDOW, MINIMUM_HEIGHT_WINDOW));
        setMaximumSize(new Dimension(MAXIMIM_WIDTH_WINDOW, MAXIMIM_HEIGHT_WINDOW));
        setSizeWindow();
        setPositionWindow();

        add(createGUI());

        pack();

        runCheckUpdate();
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

                    runButton.setEnabled(false);
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
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Error wait TaskWorker", e);
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    runButton.setEnabled(true);
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
                if (operation == Operations.RUN) {
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
            if (operation == Operations.RUN) {
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
            if (e.getActionCommand().equals("runButton")) {
                Thread thread = new Thread(new TaskPool(Operations.RUN));
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

        progressBar.setOrientation(SwingConstants.HORIZONTAL);

        scrollPane.add(textArea);
        scrollPane.add(scrollBar);

        WindowListener windowListener = new WindowListener();
        addWindowListener(windowListener);

        ButtonListener buttonListener = new ButtonListener();
        runButton.addActionListener(buttonListener);
        updateButton.addActionListener(buttonListener);
        exitButton.addActionListener(new ExitAction());

        runButton.setText(Resource.getString("RunButton"));
        updateButton.setText(Resource.getString("UpdateButton"));
        exitButton.setText(Resource.getString("ExitButton"));

        runButton.setActionCommand("runButton");
        updateButton.setActionCommand("updateButton");
        exitButton.setActionCommand("exitButton");

        runButton.setMnemonic('з');
        updateButton.setMnemonic('о');
        exitButton.setMnemonic('в');

        runButton.setToolTipText("Запуск программы 1С");
        updateButton.setToolTipText("Запуск обновления базы/конфигурации 1С");
        textArea.setToolTipText("<html><b><ul>Описание потоков:</b>" +
                "<li>KILL - завершает все процессы 1С (без сохранения данных!)" +
                "<li>UNLOAD_DB - выгружает базу 1С" +
                "<li>UPDATE - обновляет данные" +
                "<li>UPGRADE - обновляет конфигурацию" +
                "<li>TEST - тестирует и восстанавливает базу");

//        JMenuBar menuBar = new JMenuBar();
//        menuBar.add(createFileMenu());

        // Version 1
        JPanel main = BoxLayoutUtils.createVerticalPanel();
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel progressPanel = BoxLayoutUtils.createHorizontalPanel();
        progressPanel.add(progressBar);

        JPanel textPanel = BoxLayoutUtils.createHorizontalPanel();
        textPanel.add(scrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JPanel grid = new JPanel(new GridLayout(1, 3, 5, 0));
        grid.add(runButton);
        grid.add(updateButton);
        grid.add(exitButton);
        buttonPanel.add(grid);

        // Выравнивание компонентов
        BoxLayoutUtils.setGroupAlignmentX(Component.LEFT_ALIGNMENT,
                progressPanel, textPanel, buttonPanel);

        main.add(progressPanel);
        main.add(BoxLayoutUtils.createVerticalStrut(5));
//        main.add(textPanel, BorderLayout.CENTER);
        main.add(BoxLayoutUtils.createVerticalStrut(5));
        main.add(buttonPanel, BorderLayout.SOUTH);

        // Version 2
//        JPanel gridProcess = new JPanel(new GridLayout(1, 1, 5, 0));
//        gridProcess.add(progressBar);
//
//        JPanel gridText = new JPanel(new GridLayout(1, 1, 5, 0));
//        gridText.add(scrollPane);
//
//        JPanel gridButton = new JPanel(new GridLayout(1, 3, 5, 0));
//        gridButton.add(runButton);
//        gridButton.add(updateButton);
//        gridButton.add(exitButton);
//
//        JPanel flow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        flow.add(gridButton);
//
//        JPanel main = new JPanel(new GridLayout(3, 1, 5, 0));
//        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
//        main.add(gridProcess);
//        main.add(gridText);
//        main.add(flow, BorderLayout.SOUTH);

        return main;
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

    private Operations[] createPool() {
        Operations[] arrayOperations;
        Calendar currentDate = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        Calendar lastDate = GregorianCalendar.getInstance(Resource.getCurrentLocale());
        try {
            String strLastDate = Settings.getString("last.date.unload_db");
            SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern("dd.MM.yyyy");
            lastDate.setTime(format.parse(strLastDate));

        } catch (NotFoundPropertyException | ParseException e) {
            log.log(Level.WARNING, e.getMessage());
        }

        lastDate.add(Calendar.DAY_OF_YEAR, 7);
        if (lastDate.before(currentDate)) {
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

//            if (widthWindow > MAXIMIM_WIDTH_WINDOW || heightWindow > MAXIMIM_HEIGHT_WINDOW
//                    || widthWindow < MINIMUM_WIDTH_WINDOW || heightWindow < MINIMUM_HEIGHT_WINDOW) {
//                throw new NotFoundPropertyException("Loaded size does not correspond " +
//                        "to the maximum or minimum window sizes");
//            }
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
                    mapSettings.put("last.date.unload_db",
                            new SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis()));
                    Settings.setProperties(mapSettings);
                    try {
                        Settings.storeProperties();
                    } catch (NotFoundPropertyException e) {
                        log.log(Level.WARNING, e.getMessage(), e);
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

                        String msg = Resource.getString("strNewVersionUpdate")
                                + " \"" + Resource.getString("MainForm") + "\":"
                                + "\n  " + Resource.getString("strCurrentVersion")
                                + " v." + Resource.getString("Application.version")
                                + "\n  " + Resource.getString("strNewVersion")
                                + " v." + updateProgram.getNewVersion()
                                + "\n" + Resource.getString("strToUpgrade") + "?";
                        int res = JOptionPane.showConfirmDialog(null,
                                msg,
                                Resource.getString("QuestionForm"),
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
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
