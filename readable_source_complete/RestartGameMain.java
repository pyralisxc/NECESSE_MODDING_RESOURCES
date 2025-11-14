/*
 * Decompiled with CFR 0.152.
 */
import java.io.IOException;
import java.util.HashMap;
import necesse.engine.GameLaunch;
import necesse.engine.GameLog;

public class RestartGameMain {
    public static void main(String[] args) {
        HashMap<String, String> options = GameLaunch.parseLaunchOptions(args);
        boolean debug = options.containsKey("debug");
        if (debug) {
            GameLog.startLogging(false, "restart-log.txt");
            System.out.println("Launch options: " + options);
        }
        String wait = options.get("wait");
        if (debug) {
            System.out.println("WAIT: " + wait);
        }
        if (wait != null) {
            try {
                Thread.sleep(Integer.parseInt(wait));
            }
            catch (NumberFormatException e) {
                System.err.println("Could not parse wait option: " + wait);
            }
            catch (InterruptedException e) {
                // empty catch block
            }
        }
        String command = options.get("command");
        if (debug) {
            System.out.println("COMMAND: " + command);
        }
        if (command != null) {
            try {
                Runtime.getRuntime().exec(command);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Did not have command option");
        }
    }
}

