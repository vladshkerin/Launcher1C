package com.vladshkerin;

import com.vladshkerin.exception.NotFoundSettingException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class for creation, checking and receiving commands 1C.
 */
public class Command {

    private static final Logger logger = Logger.getLogger("com.vladshkerin.launcher1c");
    private static String lang = Resource.getCurrentLocale().getLanguage();
    private static Settings settings = Property.getInstance();

    private Command() {
        //TODO empty
    }

    /**
     * Returns a string parameters the program 1C.
     */
    public static String[] getString(Operations operation) {
        String[] cmd = new String[]{};
        try {
            String path1c, pathBase, pathBackup, file1c, fileTest, fileBackup;
            switch (operation) {
                case ENTERPRISE:
                    path1c = settings.getString("path.1c");
                    pathBase = settings.getString("path.base");
                    file1c = settings.getString("file.1c");
                    cmd = new String[]{
                            path1c + file1c, "ENTERPRISE", "/F" + pathBase, "/N" + "Ревизор",
                            "/L" + lang, "/DisableStartupMessages"
                    };
                    break;
                case CONFIG:
                    path1c = settings.getString("path.1c");
                    pathBase = settings.getString("path.base");
                    file1c = settings.getString("file.1c");
                    cmd = new String[]{
                            path1c + file1c, "CONFIG", "/F" + pathBase, "/N" + "server",
                            "/L" + lang, "/DisableStartupMessages"
                    };
                    break;
                case CHECK:
                    path1c = settings.getString("path.1c");
                    fileTest = settings.getString("file.test");
                    cmd = new String[]{
                            path1c + fileTest
                    };
                    break;
                case UNLOAD_DB:
                    path1c = settings.getString("path.1c");
                    pathBase = settings.getString("path.base");
                    pathBackup = settings.getString("path.backup");
                    file1c = settings.getString("file.1c");
                    fileBackup = settings.getString("file.backup");
                    cmd = new String[]{
                            path1c + file1c, "CONFIG", "/F" + pathBase, "/N" + "server", "/P" + "server",
                            "/L" + lang, "/DumpIB" + pathBackup + "\\" + fileBackup,
                            "/DisableStartupMessages"
                    };
                    break;
                case KILL:
                    cmd = new String[]{
                            "taskkill", "/F", "/IM", "1cv8.exe"
                    };
                    break;
                case UPDATE:
                    path1c = settings.getString("path.1c");
                    pathBase = settings.getString("path.base");
                    file1c = settings.getString("file.1c");
                    cmd = new String[]{
                            path1c + file1c, "ENTERPRISE", "/F" + pathBase, "/N" + "server", "/P" + "server",
                            "/L" + lang, "/C" + "OBMEN#ЦентральныйОбменРевизии#*",
                            "/DisableStartupMessages"
                    };
                    break;
                case UPGRADE:
                    path1c = settings.getString("path.1c");
                    pathBase = settings.getString("path.base");
                    file1c = settings.getString("file.1c");
                    cmd = new String[]{
                            path1c + file1c, "CONFIG", "/F" + pathBase, "/N" + "server", "/P" + "server",
                            "/L" + lang, "/UpdateDBCfg",
                            "/DisableStartupMessages"
                    };
                    break;
                case TEST:
                    path1c = settings.getString("path.1c");
                    pathBase = settings.getString("path.base");
                    file1c = settings.getString("file.1c");
                    cmd = new String[]{
                            path1c + file1c, "CONFIG", "/F" + pathBase, "/N" + "server", "/P" + "server",
                            "/L" + lang, "/IBCheckAndRepair -ReIndex -LogIntegrity",
                            "/DisableStartupMessages"
                    };
            }
        } catch (NotFoundSettingException e) {
            logger.log(Level.CONFIG, e.getMessage());
        }
        return cmd;
    }
}