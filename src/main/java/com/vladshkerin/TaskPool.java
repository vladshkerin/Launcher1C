package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for consistent running of list of processes
 */
public class TaskPool implements Runnable {

    private static Logger log = Logger.getLogger(TaskPool.class.getName());
    private final Object object = new Object();

    private Operations[] poolOperations;
    private JTextArea textArea;
    private JProgressBar progressBar;

    public TaskPool() {
        this.poolOperations = createPool();
    }

    public TaskPool(Operations operation) {
        this.poolOperations = new Operations[]{operation};
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void setProgressBar(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setMinimum(0);
                progressBar.setMaximum(poolOperations.length);
            }
        });

        int progress = 0;
        for (Operations operation : poolOperations) {
            try {
                synchronized (object) {
                    TaskWorker taskWorker = new TaskWorker(operation, ++progress);
                    taskWorker.execute();
                    object.wait();
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
                log.log(Level.WARNING, "interrupted pool operations.");
                break;
            }
        }

        String message;
        if (progress == poolOperations.length) {
            Toolkit.getDefaultToolkit().beep();
            message = Resource.getString("strCompleteUpdate") + ".";
        } else {
            message = Resource.getString("strInterruptedUpdate") + "!";
        }
        JOptionPane.showConfirmDialog(null,
                message,
                Resource.getString("WarningForm"),
                JOptionPane.DEFAULT_OPTION);

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
        } catch (NotFoundSettingException | ParseException e) {
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

    /**
     * Class for to handle a separate process
     */
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
                process.waitFor();
            } catch (InterruptedException | IOException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage(),
                        Resource.getString("ErrorForm"),
                        JOptionPane.ERROR_MESSAGE);
                log.log(Level.SEVERE, e.getMessage());
            }

            return null;
        }

        @Override
        protected void process(java.util.List<Void> chunks) {
            String date = new SimpleDateFormat("kk:mm:ss").format(System.currentTimeMillis());
            textArea.append(date + " - " +
                    Resource.getString("str" + operation.toString() + "Operation") +
                    "\n");
        }

        @Override
        public void done() {
            synchronized (object) {
                progressBar.setValue(progress);
                object.notify();
            }
        }
    }
}
