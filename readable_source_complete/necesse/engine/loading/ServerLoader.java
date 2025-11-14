/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.loading;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import necesse.engine.GameAuth;
import necesse.engine.GameInfo;
import necesse.engine.GameLaunch;
import necesse.engine.GameLog;
import necesse.engine.GameSystemInfo;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.loading.Loader;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.server.PortedServerSettings;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.PlatformManager;
import necesse.engine.save.WorldSave;
import necesse.engine.server.ServerWindow;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameMath;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;

public class ServerLoader
extends Loader {
    private PortedServerSettings serverSettings;
    private Server server;
    private ArrayList<InputRequest> inputRequests;
    private ServerWindow serverWindow;
    private static int exitStatusCode = 0;

    @Override
    public boolean loadGame(String[] args, Platform platform) throws Exception {
        boolean clearConsole = true;
        GlobalData.setup(args);
        boolean logDebug = GameLaunch.parseLaunchOptions(args).containsKey("log_debug_prints");
        GameLog.startLogging(logDebug, "latest-server-log.txt");
        GameLaunch.launchOptions = GameLaunch.parseAndHandleLaunchOptions(args);
        GameSystemInfo.printSystemInfo(GameLog.file);
        if (!PlatformManager.initialize(platform)) {
            throw new RuntimeException("Could not load platform");
        }
        this.serverWindow = platform.getStandaloneServerGUI();
        this.serverWindow.init(this);
        if (GameLaunch.launchOptions.containsKey("settings")) {
            if (Settings.loadServerSettings(new File(GameLaunch.launchOptions.get("settings")), false)) {
                System.out.println("Loaded settings file at " + GameLaunch.launchOptions.get("settings"));
            } else {
                GameLog.warn.println("Could not load or find settings file at " + GameLaunch.launchOptions.get("settings"));
            }
        } else {
            Settings.loadServerSettings();
        }
        String world = GameLaunch.launchOptions.getOrDefault("world", Settings.serverWorld);
        if (!world.isEmpty()) {
            clearConsole = false;
        }
        this.handleLaunchArgs(GameLaunch.launchOptions);
        if (Settings.serverLogging) {
            String logsPath = GameLaunch.launchOptions.getOrDefault("logs", "logs");
            String logPath = logsPath + "/" + new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date()) + ".txt";
            GameLog.addLoggingPath(logDebug, logPath);
        }
        System.out.println("Loading dedicated server on version " + GameInfo.getFullVersionString() + ".");
        try {
            GlobalData.loadAll(true);
        }
        catch (ModLoadException e) {
            System.err.println("The mod " + e.mod.getModDebugString() + " causes an error on startup.");
            System.err.println("You can try to contact the author with the below error message, or remove the mod.");
            e.printStackTrace();
            this.serverWindow.showExit();
            return false;
        }
        GameAuth.loadAuth();
        String owner = GameLaunch.launchOptions.getOrDefault("owner", null);
        if (owner != null) {
            try {
                Settings.serverOwnerAuth = Long.parseLong(owner);
            }
            catch (NumberFormatException e) {
                Settings.serverOwnerAuth = -1L;
            }
            Settings.serverOwnerName = owner;
            System.out.println("Any client connecting with name \"" + owner + "\" will automatically get owner permissions");
        }
        File worldFile = null;
        if (world.isEmpty()) {
            ArrayList<ObjectValue<File, ComparableSequence<Long>>> saves;
            if (GameLaunch.launchOptions.containsKey("world") && !(saves = WorldSave.getSavesList(true, false)).isEmpty()) {
                worldFile = (File)saves.get((int)0).object;
                System.out.println("Loading most recent world: " + worldFile.getPath());
            }
        } else {
            worldFile = World.worldExistsWithName(world);
            if (worldFile != null) {
                System.out.println("Loading existing world at " + worldFile.getPath());
            } else {
                try {
                    String existingWorldError;
                    Path path = Paths.get(world, new String[0]);
                    if (!world.startsWith(".") && !path.isAbsolute()) {
                        path = Paths.get(World.getWorldsPath() + world, new String[0]);
                    }
                    String string = existingWorldError = World.worldExists(path.toFile()) ? WorldSave.isValidExistingWorld(path.toFile()) : "";
                    if (existingWorldError == null) {
                        System.out.println("Loading existing world at " + path);
                    } else if (!world.endsWith(".zip") && Settings.zipSaves) {
                        String zipExistingWorldError;
                        world = world + ".zip";
                        path = Paths.get(world, new String[0]);
                        if (!world.startsWith(".") && !path.isAbsolute()) {
                            path = Paths.get(World.getWorldsPath() + world, new String[0]);
                        }
                        String string2 = zipExistingWorldError = World.worldExists(path.toFile()) ? WorldSave.isValidExistingWorld(path.toFile()) : "";
                        if (zipExistingWorldError == null) {
                            System.out.println("Loading existing world at " + path);
                        } else {
                            System.out.println("Creating new world at " + path);
                        }
                    } else {
                        System.out.println("Creating new world at " + path);
                    }
                    worldFile = path.toFile();
                }
                catch (InvalidPathException e) {
                    System.err.println("Invalid world path: " + world);
                    return false;
                }
            }
        }
        if (worldFile != null) {
            this.serverSettings = PortedServerSettings.createHostServerSettings(new ServerCreationSettings(worldFile), Settings.serverSlots, Settings.serverPort);
            this.serverSettings.password = Settings.serverPassword;
        } else {
            this.inputRequests = new ArrayList();
            final ArrayList worldSaves = new ArrayList();
            WorldSave.loadSaves(worldSaves::add, null, null, 5);
            this.inputRequests.add(new InputRequest(){

                @Override
                public void init() {
                    if (worldSaves.isEmpty()) {
                        System.out.println("Type a name for the world to create");
                    } else {
                        System.out.println("Select a world, or type a new name to create one");
                        for (int i = 0; i < worldSaves.size(); ++i) {
                            System.out.println("  " + (i + 1) + ". " + ((WorldSave)worldSaves.get((int)i)).displayName);
                        }
                    }
                }

                @Override
                public boolean submitInput(String input) {
                    try {
                        File worldFilePath = World.getExistingWorldFilePath(input);
                        try {
                            int save = Integer.parseInt(input);
                            if (save >= 1 && save <= worldSaves.size()) {
                                worldFilePath = ((WorldSave)worldSaves.get((int)(save - 1))).filePath;
                            }
                        }
                        catch (NumberFormatException numberFormatException) {
                            // empty catch block
                        }
                        if (worldFilePath == null) {
                            worldFilePath = new File(World.getWorldsPath() + input + (Settings.zipSaves ? ".zip" : ""));
                        }
                        if (World.worldExists(worldFilePath)) {
                            System.out.println("Selected save: " + worldFilePath);
                        } else {
                            System.out.println("Creating new save: " + worldFilePath);
                        }
                        ServerLoader.this.serverSettings = PortedServerSettings.createHostServerSettings(new ServerCreationSettings(worldFilePath), Settings.serverSlots, Settings.serverPort);
                        ((ServerLoader)ServerLoader.this).serverSettings.password = Settings.serverPassword;
                        return true;
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                }
            });
            this.inputRequests.add(new InputRequest(){

                @Override
                public void init() {
                    System.out.println("Custom server options? (y/n)");
                }

                @Override
                public boolean submitInput(String input) {
                    if (input.toLowerCase().startsWith("y")) {
                        ServerLoader.this.inputRequests.add(new InputRequest(){

                            @Override
                            public void init() {
                                System.out.println("Please specify host port");
                            }

                            @Override
                            public boolean submitInput(String input) {
                                try {
                                    int port = Integer.parseInt(input);
                                    if (port < 0 || port > 65535) {
                                        System.out.println("Port must be between 0 and 65535");
                                        return false;
                                    }
                                    ServerLoader.this.serverSettings = PortedServerSettings.createHostServerSettings(new ServerCreationSettings(((ServerLoader)ServerLoader.this).serverSettings.creationSettings.worldFilePath), ((ServerLoader)ServerLoader.this).serverSettings.slots, port);
                                    Settings.serverPort = port;
                                    return true;
                                }
                                catch (NumberFormatException e) {
                                    System.out.println(input + " is not number");
                                }
                                catch (IllegalArgumentException e) {
                                    System.out.println(e.getMessage());
                                }
                                return false;
                            }
                        });
                        ServerLoader.this.inputRequests.add(new InputRequest(){

                            @Override
                            public void init() {
                                System.out.println("Please specify player slots (1 - 250)");
                            }

                            @Override
                            public boolean submitInput(String input) {
                                try {
                                    int slots = Integer.parseInt(input);
                                    ServerLoader.this.serverSettings = PortedServerSettings.createHostServerSettings(new ServerCreationSettings(((ServerLoader)ServerLoader.this).serverSettings.creationSettings.worldFilePath), slots, ((ServerLoader)ServerLoader.this).serverSettings.port);
                                    Settings.serverSlots = slots;
                                    return true;
                                }
                                catch (NumberFormatException e) {
                                    System.out.println(input + " is not number");
                                }
                                catch (IllegalArgumentException e) {
                                    System.out.println(e.getMessage());
                                }
                                return false;
                            }
                        });
                        ServerLoader.this.inputRequests.add(new InputRequest(){

                            @Override
                            public void init() {
                                System.out.println("Please specify server password (blank for none)");
                            }

                            @Override
                            public boolean submitInput(String input) {
                                ((ServerLoader)ServerLoader.this).serverSettings.password = input;
                                Settings.serverPassword = input;
                                if (input.isEmpty()) {
                                    System.out.println("> No password selected");
                                }
                                return true;
                            }
                        });
                        if (!World.worldExists(((ServerLoader)ServerLoader.this).serverSettings.creationSettings.worldFilePath)) {
                            ServerLoader.this.inputRequests.add(new InputRequest(){

                                @Override
                                public void init() {
                                    System.out.println("Please specify spawn seed (blank for random)");
                                }

                                @Override
                                public boolean submitInput(String input) {
                                    if (input.isEmpty()) {
                                        System.out.println("> Random spawn seed selected");
                                        ((ServerLoader)ServerLoader.this).serverSettings.creationSettings.worldSeed = ServerCreationSettings.getNewRandomSpawnSeed();
                                        return true;
                                    }
                                    if (!input.matches("[a-zA-Z0-9 ]+")) {
                                        System.out.println("Invalid world seed, only letters, spaces and numbers allowed");
                                    }
                                    if (input.length() > 50) {
                                        System.out.println("Seed too long (max 50 characters)");
                                        return false;
                                    }
                                    ((ServerLoader)ServerLoader.this).serverSettings.creationSettings.worldSeed = input;
                                    return true;
                                }
                            });
                            ServerLoader.this.inputRequests.add(new InputRequest(){

                                @Override
                                public void init() {
                                    System.out.println("Spawn guide house? (y/n)");
                                }

                                @Override
                                public boolean submitInput(String input) {
                                    if (input.toLowerCase().startsWith("y")) {
                                        ((ServerLoader)ServerLoader.this).serverSettings.creationSettings.spawnGuide = true;
                                        return true;
                                    }
                                    if (input.toLowerCase().startsWith("n")) {
                                        ((ServerLoader)ServerLoader.this).serverSettings.creationSettings.spawnGuide = false;
                                        return true;
                                    }
                                    return false;
                                }
                            });
                        }
                        return true;
                    }
                    return input.toLowerCase().startsWith("n");
                }
            });
            this.inputRequests.get(0).init();
            while (!this.inputRequests.isEmpty()) {
                ServerLoader.threadSleep(100);
            }
            Settings.saveServerSettings();
            if (clearConsole) {
                this.serverWindow.clearConsole();
            }
        }
        return true;
    }

    @Override
    public void unloadGame() {
        System.out.println("Server has stopped");
        System.out.println("Exiting in 2 seconds...");
        ServerLoader.threadSleep(2000);
        ModLoader.getAllMods().forEach(LoadedMod::dispose);
        this.serverWindow.dispose();
        PlatformManager.dispose();
        System.exit(exitStatusCode);
    }

    @Override
    public void startGame() {
        try {
            this.server = Platform.getNetworkManager().startServer(this.serverSettings, null);
        }
        catch (IOException | FileSystemClosedException e) {
            System.err.println("Error loading server world:");
            e.printStackTrace();
            if (this.server != null) {
                this.server.stop();
            }
            System.out.println("Server has stopped.");
            exitStatusCode = 1;
            return;
        }
        this.serverWindow.setServer(this.server);
        TickManager tickManager = new TickManager("main", 2){

            @Override
            public void update() {
                if (ServerLoader.this.server == null) {
                    return;
                }
                ServerLoader.this.serverWindow.updateGUI(ServerLoader.this.server.tickManager());
            }
        };
        if (this.server != null) {
            this.serverWindow.updateGUI(this.server.tickManager());
        }
        tickManager.init();
        while (this.server != null && !this.server.hasClosed()) {
            tickManager.tickLogic();
        }
    }

    private void handleLaunchArgs(HashMap<String, String> options) {
        if (options.containsKey("port")) {
            try {
                int port = Integer.parseInt(options.get("port"));
                Settings.serverPort = GameMath.limit(port, 0, 65535);
                if (port != Settings.serverPort) {
                    System.out.println("Limited port to " + Settings.serverPort);
                }
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse port parameter: " + options.get("port"));
            }
        }
        if (options.containsKey("slots")) {
            try {
                int slots = Integer.parseInt(options.get("slots"));
                Settings.serverSlots = GameMath.limit(slots, 1, 250);
                if (slots != Settings.serverSlots) {
                    System.out.println("Limited slots to " + Settings.serverSlots);
                }
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse slots parameter: " + options.get("slots"));
            }
        }
        Settings.serverPassword = options.getOrDefault("password", Settings.serverPassword);
        Settings.serverMOTD = options.getOrDefault("motd", Settings.serverMOTD);
        Settings.serverMOTD = Settings.serverMOTD.replace("\\n", "\n");
        if (options.containsKey("pausewhenempty")) {
            String pauseWhenEmpty = options.get("pausewhenempty");
            if (pauseWhenEmpty.equalsIgnoreCase("true") || pauseWhenEmpty.equalsIgnoreCase("1")) {
                Settings.pauseWhenEmpty = true;
            } else if (pauseWhenEmpty.equalsIgnoreCase("false") || pauseWhenEmpty.equalsIgnoreCase("0")) {
                Settings.pauseWhenEmpty = false;
            } else {
                GameLog.warn.println("Could not parse pausewhenempty parameter: " + pauseWhenEmpty);
            }
        }
        if (options.containsKey("strictserverauthority")) {
            String strictServerAuthority = options.get("strictserverauthority");
            if (strictServerAuthority.equalsIgnoreCase("true") || strictServerAuthority.equalsIgnoreCase("1")) {
                Settings.strictServerAuthority = true;
            } else if (strictServerAuthority.equalsIgnoreCase("false") || strictServerAuthority.equalsIgnoreCase("0")) {
                Settings.strictServerAuthority = false;
            } else {
                GameLog.warn.println("Could not parse strictserverauthority parameter: " + strictServerAuthority);
            }
        }
        if (options.containsKey("logging")) {
            String logging = options.get("logging");
            if (logging.equalsIgnoreCase("true") || logging.equalsIgnoreCase("1")) {
                Settings.serverLogging = true;
            } else if (logging.equalsIgnoreCase("false") || logging.equalsIgnoreCase("0")) {
                Settings.serverLogging = false;
            } else {
                GameLog.warn.println("Could not parse logging parameter: " + logging);
            }
        }
        if (options.containsKey("zipsaves")) {
            String zipSaves = options.get("zipsaves");
            boolean bl = Settings.zipSaves = zipSaves.equals("1") || zipSaves.equals("true");
        }
        if (options.containsKey("language")) {
            Settings.language = options.get("language");
            Language l = Localization.getLanguageStringID(Settings.language);
            if (l == null) {
                l = Localization.defaultLang;
                GameLog.warn.println("Could not find language " + Settings.language);
                Settings.language = Localization.defaultLang.stringID;
            }
            l.setCurrent();
        }
        Settings.bindIP = options.getOrDefault("ip", Settings.bindIP);
        if (options.containsKey("unloadlevels")) {
            try {
                int value = Integer.parseInt(options.get("unloadlevels"));
                Settings.unloadLevelsCooldown = Math.max(2, value);
                if (value != Settings.unloadLevelsCooldown) {
                    System.out.println("Limited unload levels cooldown to " + Settings.unloadLevelsCooldown + " seconds");
                }
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse unloadlevels parameter: " + options.get("unloadlevels"));
            }
        }
        if (options.containsKey("worldborder")) {
            try {
                int value = Integer.parseInt(options.get("worldborder"));
                Settings.worldBorderSize = Math.max(-1, value);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse worldborder parameter: " + options.get("worldborder"));
            }
        }
        if (options.containsKey("itemslife")) {
            try {
                int value = Integer.parseInt(options.get("itemslife"));
                Settings.droppedItemsLifeMinutes = Math.max(0, value);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse itemslife parameter: " + options.get("itemslife"));
            }
        }
        if (options.containsKey("unloadsettlements")) {
            String value = options.get("unloadsettlements");
            boolean bl = Settings.unloadSettlements = value.equals("1") || value.equals("true");
        }
        if (options.containsKey("maxsettlements")) {
            try {
                int value = Integer.parseInt(options.get("maxsettlements"));
                Settings.maxSettlementsPerPlayer = Math.max(-1, value);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse maxsettlements parameter: " + options.get("maxsettlements"));
            }
        }
        if (options.containsKey("maxsettlers")) {
            try {
                int value = Integer.parseInt(options.get("maxsettlers"));
                Settings.maxSettlersPerSettlement = Math.max(-1, value);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not parse maxsettlers parameter: " + options.get("maxsettlers"));
            }
        }
    }

    private static void threadSleep(int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    public void handleCommand(String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (!command.isEmpty()) {
            System.out.println("> " + command);
        }
        if (this.inputRequests != null && this.inputRequests.size() != 0) {
            if (this.inputRequests.get(0).submitInput(command)) {
                this.inputRequests.remove(0);
                if (!this.inputRequests.isEmpty()) {
                    this.inputRequests.get(0).init();
                }
            }
            return;
        }
        if (!command.isEmpty() && this.server != null) {
            this.server.sendCommand(command, null);
        }
    }

    private static abstract class InputRequest {
        private InputRequest() {
        }

        public abstract void init();

        public abstract boolean submitInput(String var1);
    }
}

