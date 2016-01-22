package com.vladshkerin;

import com.vladshkerin.exception.NotFoundPathException;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Class for creation, checking and receiving commands 1C
 */
public class Command {

    private static String path1c;
    private static String pathBase;
    private static String pathBackup;
    private static String backBase;
    private static String lang;

    static {
        path1c = "C:\\Program Files\\1cv82\\8.2.19.90\\bin\\1cv8.exe";
        pathBase = "C:\\base1c";
        pathBackup = "C:\\backup";
        backBase = "base1c_" +
                new SimpleDateFormat("MM_yyyy").format(System.currentTimeMillis());
        lang = "En";
    }

    /**
     * Checks all the path to start and maintenance 1C.
     *
     * @throws NotFoundPathException
     */
    public static void checkDefaultPath() throws NotFoundPathException {
        if (!(new File(path1c).exists())) {
            String[] mas = new String[]{
                    "C:\\Program Files\\1cv82\\8.2.19.130\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.19.130\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.19.90\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.19.90\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.19.83\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.19.83\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.19.76\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.19.76\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.18.109\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.18.109\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.17.169\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.17.169\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.16.362\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.16.362\\bin\\1cv8.exe",
                    "C:\\Program Files\\1cv82\\8.2.15.294\\bin\\1cv8.exe",
                    "C:\\Program Files (x86)\\1cv82\\8.2.15.294\\bin\\1cv8.exe"
            };

            boolean flag = false;
            File file1c;
            for (String str : mas) {
                file1c = new File(str);
                if (file1c.exists()) {
                    path1c = str;
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                throw new NotFoundPathException(path1c);
            }
        }

        if (!(new File(pathBase).exists())) {
            throw new NotFoundPathException(pathBase);
        }

        if (!(new File(pathBackup).exists())) {
            throw new NotFoundPathException(pathBackup);
        }
    }

    /**
     * Returns a string parameters the program 1C.
     */
    public static String[] getString(Operations operation) {
        String[] cmd = new String[]{};
        switch (operation) {
            case ENTERPRISE:
                cmd = new String[]{
                        path1c, "ENTERPRISE", "/F" + pathBase, "/N" + "Ревизор", "/LRu  ", "/DisableStartupMessages"
                };
                break;
            case CONFIG:
                cmd = new String[]{
                        path1c, "CONFIG", "/F" + pathBase, "/N" + "Ревизор", "/LRu  ", "/DisableStartupMessages"
                };
                break;
            case UNLOAD_DB:
                cmd = new String[]{
                        path1c, "CONFIG", "/F" + pathBase, "/N" + "server", "/P" + "server", "/L" + lang,
                        "/DumpIB" + pathBackup + "\\" + backBase,
                        "/DisableStartupMessages"
                };
                break;
            case KILL:
                cmd = new String[]{
                        "taskkill", "/F", "/IM", "1cv8.exe"
                };
                break;
            case COPY:
                cmd = new String[]{
                        "copy", "/Y",
                        pathBackup + "\\" + backBase + ".bak",
                        pathBackup + "\\" + backBase
                };
                break;
            case UPDATE:
                cmd = new String[]{
                        path1c, "ENTERPRISE", "/F" + pathBase, "/N" + "server", "/P" + "server", "/L" + lang,
                        "/C" + "OBMEN#ЦентральныйОбменРевизии#*",
                        "/DisableStartupMessages"
                };
                break;
            case UPGRADE:
                cmd = new String[]{
                        path1c, "CONFIG", "/F" + pathBase, "/N" + "server", "/P" + "server", "/L" + lang,
                        "/UpdateDBCfg",
                        "/DisableStartupMessages"
                };
                break;
            case TEST:
                cmd = new String[]{
                        path1c, "CONFIG", "/F" + pathBase, "/N" + "server", "/P" + "server", "/L" + lang,
                        "/IBCheckAndRepair -ReIndex -LogIntegrity",
                        "/DisableStartupMessages"
                };
        }
        return cmd;
    }
}
