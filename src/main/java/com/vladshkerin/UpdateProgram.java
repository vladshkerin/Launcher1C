package com.vladshkerin;

import com.vladshkerin.exception.FTPException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * The class to the update program.
 */
public class UpdateProgram {

    private final FTPClient FTP;

    private static final Logger logger = Logger.getLogger("com.vladshkerin.launcher1c");

    private String nameFile = "Launcher1C";
    private String extensionFile = "zip";
    private String newVersion = "";
    private Long sizeFile = 0L;
    private String downloadURL = "_UPDATE_PROGRAM_/Launcher1C";
    private String server = "ftp.mag.ariant.ru";
    private int port = 21;

    public UpdateProgram() {
        FTP = new FTPClient();
    }

    public UpdateProgram(String server, int port) {
        this.server = server;
        this.port = port;

        FTP = new FTPClient();
    }

    public void setFileUpdate(String nameFileUpdate) {
        this.nameFile = nameFileUpdate;
    }

    public void setFileUpdate(String nameFileUpdate, String postfixFileUpdate) {
        this.nameFile = nameFileUpdate;
        this.extensionFile = postfixFileUpdate;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public Long getSizeFile() {
        return sizeFile;
    }

    public boolean isUpdate() throws FTPException {
        try {
            connectedFTP();
            setSettingFTP();

            Pattern pattern = Pattern.compile(nameFile +
                    "-\\d+.\\d+.\\d+" + "." + extensionFile);

            for (FTPFile file : FTP.listFiles(downloadURL)) {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    newVersion = file.getName()
                            .replace(nameFile + "-", "")
                            .replace("." + extensionFile, "");
                    sizeFile = file.getSize();
                    return newVersionAvailable();
                }
            }

        } catch (FTPConnectionClosedException e) {
            logger.log(Level.FINE, "FTP server closed connection: " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.FINE, "FTP error IO: " + e.getMessage());
        } finally {
            if (FTP.isConnected()) {
                try {
                    logoutFTP();
                    FTP.disconnect();
                } catch (IOException f) {
                    logger.log(Level.FINE, "FTP error disconnect: " + f.getMessage());
                }
            }
        }

        return false;
    }

    public boolean update() throws FTPException {
        try {
            connectedFTP();
            setSettingFTP();

            Pattern pattern = Pattern.compile(nameFile +
                    "-\\d+.\\d+.\\d+" + "." + extensionFile);

            for (FTPFile file : FTP.listFiles(downloadURL)) {
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {

                    Path tempFile = Files.createTempFile(nameFile + "_", "." + extensionFile);
                    OutputStream output = new BufferedOutputStream(
                            new FileOutputStream(tempFile.toFile()));
                    FTP.retrieveFile(File.separator + downloadURL +
                            File.separator + file.getName(), output);
                    output.flush();
                    output.close();

                    unZip(tempFile.toString(), Resource.getCurrentPath());
                    Files.delete(tempFile);

                    return true;
                }
            }
        } catch (FTPConnectionClosedException e) {
            logger.log(Level.FINE, "FTP server closed connection: " + e.getMessage());
        } catch (IOException e) {
            logger.log(Level.FINE, "FTP error IO: " + e.getMessage());
        } finally {
            if (FTP.isConnected()) {
                try {
                    logoutFTP();
                    FTP.disconnect();
                } catch (IOException f) {
                    logger.log(Level.FINE, "FTP error disconnect: " + f.getMessage());
                }
            }
        }

        return false;
    }

    public void unZip(String path, String dir_to) throws IOException {
        ZipFile zip = new ZipFile(path);
        Enumeration entries = zip.entries();
        LinkedList<ZipEntry> zfiles = new LinkedList<>();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.isDirectory()) {
                new File(dir_to + File.separator + entry.getName()).mkdir();
            } else {
                zfiles.add(entry);
            }
        }

        for (ZipEntry entry : zfiles) {
            InputStream in = zip.getInputStream(entry);
            OutputStream out = new FileOutputStream(dir_to + "/" + entry.getName());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) >= 0)
                out.write(buffer, 0, len);
            in.close();
            out.close();
        }

        zip.close();
    }

    private boolean newVersionAvailable() throws IOException {
        String oldVersion = Resource.getString("Application.version");
        if (!(newVersion.isEmpty() || oldVersion.isEmpty())) {

            if (oldVersion.contains("test-")) {
                oldVersion = oldVersion.replaceFirst("test-", "");
            }
            Integer newVer = Integer.valueOf(newVersion.replace(".", ""));
            Integer oldVer = Integer.valueOf(oldVersion.replace(".", ""));
            return newVer > oldVer;
        }
        return false;
    }

    private void connectedFTP() throws IOException, FTPException {
        if (port > 0) {
            FTP.connect(server, port);
        } else {
            FTP.connect(server);
        }

        // After connection attempt, you should check the reply code to verify success.
        int reply = FTP.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            throw new FTPException("FTP server refused connection.");
        }
    }

    private void setSettingFTP() throws IOException, FTPException {
        setSettingFTP(true, false);
    }

    private void setSettingFTP(boolean binaryTransfer, boolean localMode) throws IOException, FTPException {
        String login = "obmen";
        String password = "Nhfutlbz";
        if (!FTP.login(login, password)) {
            FTP.logout();
            throw new FTPException("FTP could not login to server.");
        }

        if (binaryTransfer) {
            FTP.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        } else {
            // in theory this should not be necessary as servers should default to ASCII
            // but they don't all do so - see NET-500
            FTP.setFileType(org.apache.commons.net.ftp.FTP.ASCII_FILE_TYPE);
        }

        // Use passive mode as default because most of us are behind firewalls these days.
        if (localMode) {
            FTP.enterLocalActiveMode();
        } else {
            FTP.enterLocalPassiveMode();
        }
    }

    private void logoutFTP() throws IOException {
        FTP.noop(); // check that control connection is working OK
        FTP.logout();
    }
}
