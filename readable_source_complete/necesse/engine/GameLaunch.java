/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.modLoader.DevModProvider;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.util.GameUtils;
import necesse.engine.world.WorldSettings;
import necesse.gfx.gameTexture.GameTexture;

public class GameLaunch {
    public static HashMap<String, String> launchOptions;
    public static String[] fullArgs;
    public static String fullLaunchParameters;
    public static boolean instantContinue;
    public static String instantHost;
    public static String instantLoad;
    public static String useCharacter;
    public static String instantConnect;
    public static String instantLobbyConnect;
    public static int launchMonitor;
    private static final Pattern argsPattern;

    public static String[] quoteArgs(String[] args) {
        String[] quoted = new String[args.length];
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            quoted[i] = arg.contains(" ") && (!arg.startsWith("\"") || !arg.endsWith("\"")) ? "\"" + arg + "\"" : arg;
        }
        return quoted;
    }

    public static HashMap<String, String> parseLaunchOptions(String[] args) {
        args = GameLaunch.quoteArgs(args);
        String full = GameUtils.join(args, " ");
        int currentIndex = 0;
        HashMap<String, String> out = new HashMap<String, String>();
        while (currentIndex < full.length()) {
            char current = full.charAt(currentIndex);
            if (current == '-' || current == '+') {
                String currentOption;
                int nextSpace = full.indexOf(" ", currentIndex);
                if (nextSpace == -1) {
                    currentOption = full.substring(currentIndex + 1);
                    out.put(currentOption, "");
                    break;
                }
                currentOption = full.substring(currentIndex + 1, nextSpace);
                currentIndex = nextSpace + 1;
                String arg = null;
                Matcher matcher = argsPattern.matcher(full.substring(currentIndex));
                if (matcher.find()) {
                    arg = matcher.group(1) != null ? matcher.group(1) : (matcher.group(2) != null ? matcher.group(2) : matcher.group());
                }
                if (arg != null) {
                    if (arg.startsWith("-") || arg.startsWith("+")) {
                        out.put(currentOption, "");
                        continue;
                    }
                    out.put(currentOption, arg);
                    continue;
                }
                out.put(currentOption, "");
                break;
            }
            int nextOption = Math.max(full.indexOf("-", currentIndex), full.indexOf("+", currentIndex));
            if (nextOption == -1) break;
            currentIndex = nextOption;
        }
        return out;
    }

    public static HashMap<String, String> parseAndHandleLaunchOptions(String[] args) {
        fullArgs = args;
        HashMap<String, String> options = GameLaunch.parseLaunchOptions(args);
        fullLaunchParameters = GameUtils.join(GameLaunch.quoteArgs(args), " ");
        if (args.length > 0) {
            System.out.println("Launched game with arguments: " + fullLaunchParameters);
        }
        if (options.containsKey("dev")) {
            GlobalData.setDevMode();
            String authString = options.get("dev");
            if (!authString.isEmpty()) {
                try {
                    long authentication = Long.parseLong(authString);
                    if (authentication > 0L && authentication <= 500L) {
                        GameAuth.setTempAuth(authentication);
                    } else {
                        System.err.println("Invalid authentication number: " + authentication);
                        System.exit(0);
                    }
                }
                catch (NumberFormatException e) {
                    System.err.println("Authentication argument must be a number.");
                    System.exit(0);
                }
            }
        }
        if (options.containsKey("hiddencheats")) {
            WorldSettings.cheatsHidden = true;
        }
        if (options.containsKey("lowmemory")) {
            GlobalData.setLowMemoryMode();
        }
        if (options.containsKey("memorydebug")) {
            GameTexture.memoryDebug = true;
        }
        if (options.containsKey("mod")) {
            DevModProvider.devMod = options.get("mod");
        }
        if (options.containsKey("disablemods")) {
            ModLoader.disableMods = true;
        }
        if (options.containsKey("continue")) {
            instantContinue = true;
        }
        if (options.containsKey("load")) {
            instantLoad = options.get("load");
        }
        if (options.containsKey("host")) {
            instantHost = options.get("host");
        }
        if (options.containsKey("character")) {
            useCharacter = options.get("character");
        }
        if (options.containsKey("connect")) {
            instantConnect = options.get("connect");
        }
        if (options.containsKey("connect_lobby")) {
            instantLobbyConnect = options.get("connect_lobby");
        }
        if (options.containsKey("monitor")) {
            String param = options.get("monitor");
            if (!param.isEmpty()) {
                try {
                    launchMonitor = Integer.parseInt(param);
                    if (launchMonitor < 0) {
                        launchMonitor = -1;
                        throw new NumberFormatException("Monitor cannot be negative");
                    }
                }
                catch (NumberFormatException e) {
                    GameLog.warn.println("Monitor launch parameter is not a positive number: \"" + param + "\"");
                }
            } else {
                GameLog.warn.println("Monitor launch parameter cannot be empty");
            }
        }
        return options;
    }

    static {
        fullArgs = null;
        fullLaunchParameters = null;
        instantContinue = false;
        instantHost = null;
        instantLoad = null;
        useCharacter = null;
        instantConnect = null;
        instantLobbyConnect = null;
        launchMonitor = -1;
        argsPattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    }
}

