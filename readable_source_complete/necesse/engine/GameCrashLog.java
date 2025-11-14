/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModRuntimeException;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.reports.CrashJFrame;
import necesse.reports.CrashReportData;
import necesse.reports.ModCrashJFrame;
import necesse.reports.NoticeJFrame;

public class GameCrashLog {
    private static final String LOG_PATH = "latest-crash.log";

    public static void printCrashLog(Throwable e, Client client, Server server, String state, boolean openCrashReport) {
        GameCrashLog.printCrashLog(Collections.singletonList(e), client, server, state, openCrashReport);
    }

    public static void printCrashLog(List<Throwable> errors, Client client, Server server, String state, boolean openCrashReport) {
        File file = new File(LOG_PATH);
        System.err.println("Printing crash log to " + file.getAbsolutePath());
        try {
            CrashReportData data = new CrashReportData(errors, client, server, state);
            FileOutputStream fo = new FileOutputStream(file);
            PrintStream printStream = new PrintStream(fo);
            data.printFullReport(printStream, file);
            fo.close();
            if (client != null) {
                client.serverCrashReport = data;
            }
            if (openCrashReport) {
                GameCrashLog.openCrashFrame(data);
                if (GlobalData.getCurrentGameLoop() != null) {
                    GlobalData.getCurrentGameLoop().stopMainGameLoop();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openCrashFrame(CrashReportData data) {
        Throwable[] modExceptions = (ModRuntimeException[])data.errors.stream().filter(e -> e instanceof ModRuntimeException).map(e -> (ModRuntimeException)e).toArray(ModRuntimeException[]::new);
        if (modExceptions.length > 0) {
            ModCrashJFrame crashJFrame = new ModCrashJFrame(Collections.singletonList(modExceptions[0].mod), modExceptions);
            crashJFrame.setVisible(true);
            crashJFrame.requestFocus();
        } else {
            ArrayList<LoadedMod> responsibleMods = ModLoader.getResponsibleMods(data.errors, true);
            if (!responsibleMods.isEmpty()) {
                ModCrashJFrame crashJFrame = new ModCrashJFrame(responsibleMods, data.errors.toArray(new Exception[0]));
                crashJFrame.setVisible(true);
                crashJFrame.requestFocus();
                return;
            }
            if (GameCrashLog.checkAnyCause(data.errors, (Throwable e) -> e instanceof OutOfMemoryError)) {
                NoticeJFrame noticeFrame = new NoticeJFrame(400, Localization.translate("misc", "outofmemory"));
                noticeFrame.setVisible(true);
                noticeFrame.requestFocus();
            } else {
                CrashJFrame crashFrame = new CrashJFrame(data);
                crashFrame.setVisible(true);
                crashFrame.requestFocus();
            }
        }
    }

    public static boolean checkAnyCause(Iterable<Throwable> errors, Predicate<Throwable> predicate) {
        for (Throwable error : errors) {
            if (!GameCrashLog.checkAnyCause(error, predicate)) continue;
            return true;
        }
        return false;
    }

    public static boolean checkAnyCause(Throwable error, Predicate<Throwable> predicate) {
        if (predicate.test(error)) {
            return true;
        }
        Throwable cause = error.getCause();
        if (cause != null) {
            return GameCrashLog.checkAnyCause(cause, predicate);
        }
        return false;
    }
}

