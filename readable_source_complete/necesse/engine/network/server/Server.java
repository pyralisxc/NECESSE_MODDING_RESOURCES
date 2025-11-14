/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.server;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GameCache;
import necesse.engine.GameCrashLog;
import necesse.engine.GameEvents;
import necesse.engine.Settings;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.events.ServerClientConnectedEvent;
import necesse.engine.events.ServerClientDisconnectEvent;
import necesse.engine.events.ServerStartEvent;
import necesse.engine.events.ServerStopEvent;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.PacketManager;
import necesse.engine.network.client.Client;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketClientInstalledDLC;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.ServerClient;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.network.server.network.ServerNetwork;
import necesse.engine.network.server.network.ServerSingleplayerNetwork;
import necesse.engine.platforms.Platform;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.server.ServerTickThread;
import necesse.engine.state.MainMenu;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.entity.manager.WorldSavedEntityComponent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.presets.ConfirmationContinueForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.level.maps.Level;

public class Server {
    private static final int autoSaveIntervalInSec = 60;
    private static final int backupEachAutoSave = 15;
    public static int clientMoveTolerance = 30;
    public static final int migrationPrintCooldownMinutes = 1;
    public long lastMigrationTime;
    public HashMapSet<LevelIdentifier, Point> regionsMigrated = new HashMapSet();
    public HashSet<String> migratedNames = new HashSet();
    public boolean pauseForSpawnedPlayer;
    private boolean isPaused;
    private ServerClient[] clients;
    private final HashMap<Long, ServerClient> authClientMap = new HashMap();
    public ServerNetwork network;
    private ServerSettings settings;
    public World world;
    public HashMap<Long, String> usedNames;
    private boolean isStopped;
    private boolean stopCalled;
    private PacketDisconnect stopPacket;
    private final List<Consumer<Server>> stopEvents = new LinkedList<Consumer<Server>>();
    public List<ContinueComponent> stopErrors;
    int playersOnline;
    private int pauseCounter;
    private long saveTime;
    private TickManager tickManager;
    public final PacketManager packetManager;
    public final CommandsManager commandsManager;
    public ServerTickThread serverThread;
    private boolean hasServerStarted = false;
    protected GameMessage serverStartingMessage = null;
    protected boolean serverStartingAllowCancelling = true;
    protected Exception startupException = null;
    private Client client;
    private boolean isSinglePlayer;
    private boolean isHosted;
    private int autoSaves;

    public void printMigrationIfShould() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastPrint = currentTime - this.lastMigrationTime;
        if (timeSinceLastPrint > 60000L) {
            this.lastMigrationTime = currentTime;
            if (!this.migratedNames.isEmpty()) {
                int totalRegions = this.regionsMigrated.entrySet().stream().mapToInt(e -> ((HashSet)e.getValue()).size()).sum();
                String migratedNamesJoined = GameUtils.join(this.migratedNames.toArray(), ", ", " and ");
                System.out.println("Migrated " + totalRegions + " region" + (totalRegions > 1 ? "s" : "") + " on " + this.regionsMigrated.getSize() + " level" + (this.regionsMigrated.getSize() > 1 ? "s" : "") + " " + migratedNamesJoined + " within the last " + 1 + " minutes");
                this.regionsMigrated.clearAll();
                this.migratedNames.clear();
            }
        }
    }

    public Server(ServerSettings settings) throws IOException, FileSystemClosedException {
        this.settings = settings;
        GameSeasons.loadSeasons();
        this.serverThread = new ServerTickThread(this, "Server Thread", 60);
        this.packetManager = this.newPacketManager();
        this.commandsManager = new CommandsManager(this);
        this.network = settings.isSinglePlayer() ? new ServerSingleplayerNetwork(this) : Platform.getNetworkManager().createOpenServerNetwork(this, settings);
        this.clients = new ServerClient[settings.slots];
        this.world = new World(this);
    }

    public void start(ServerHostSettings hostSettings, boolean concurrently) throws IOException {
        this.usedNames = this.world.getUsedPlayerNames();
        if (concurrently) {
            this.serverThread.makeInitWorld(hostSettings);
        } else {
            try {
                this.world.init();
                this.markWorldInitialized(null, hostSettings);
            }
            catch (Exception e) {
                this.markWorldInitialized(e, hostSettings);
                return;
            }
        }
        this.serverThread.start();
    }

    public void markWorldInitialized(Exception exception, ServerHostSettings hostSettings) {
        try {
            if (exception != null) {
                throw exception;
            }
            if (hostSettings != null) {
                hostSettings.apply(this, false);
            }
            this.network.open();
            this.startSaveTimer();
        }
        catch (Exception e) {
            this.startupException = e;
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                message = e.getClass().getSimpleName();
            }
            this.stop(PacketDisconnect.networkError(-1, message), null);
            return;
        }
        this.hasServerStarted = true;
        GameEvents.triggerEvent(new ServerStartEvent(this));
    }

    public void setStartingMessage(GameMessage message, boolean allowCancelling) {
        if (this.serverStartingMessage == null || !this.serverStartingMessage.isSame(message)) {
            System.out.println(message.translate());
        }
        this.serverStartingAllowCancelling = allowCancelling;
        this.serverStartingMessage = message;
    }

    public GameMessage getStartingMessage() {
        return this.serverStartingMessage;
    }

    public boolean startingAllowCancelling() {
        return this.serverStartingAllowCancelling;
    }

    public Exception getStartupException() {
        return this.startupException;
    }

    public boolean hasStarted() {
        return this.hasServerStarted;
    }

    public void startHostFromSingleplayer(ServerSettings serverSettings, ServerHostSettings hostSettings) {
        if (this.settings.isSinglePlayer() && this.client != null) {
            serverSettings.creationSettings = this.settings.creationSettings;
            this.settings = serverSettings;
            hostSettings.apply(this, true);
            if (!this.settings.isSinglePlayer()) {
                if (this.settings.slots < this.clients.length) {
                    throw new IllegalArgumentException("Cannot decrease slots");
                }
                this.clients = Arrays.copyOf(this.clients, this.settings.slots);
                this.client.startedHosting(this);
                this.network = Platform.getNetworkManager().createOpenServerNetwork(this, this.settings);
                try {
                    this.network.open();
                }
                catch (IOException e) {
                    this.stop(PacketDisconnect.networkError(-1, e.getMessage()), null);
                    e.printStackTrace();
                }
                this.resume();
                SaveData continueSave = MainMenu.getContinueCacheSaveBase(MainMenu.ContinueMode.HOST);
                continueSave.addSafeString("worldFilePath", this.settings.creationSettings.worldFilePath.getAbsolutePath());
                SaveData hostSave = new SaveData("host");
                this.settings.addSaveData(hostSave);
                continueSave.addSaveData(hostSave);
                GameCache.cacheSave(continueSave, "continueLast");
            } else {
                System.err.println("Start host did not work, because provided port is still singleplayer");
            }
        } else {
            System.err.println("Cannot start host, because world is already hosting");
        }
    }

    public void makeSingleplayer(Client client) {
        this.client = client;
        this.isSinglePlayer = true;
    }

    public void makeHosted(Client client) {
        this.client = client;
        this.isHosted = true;
    }

    public void setTickManager(TickManager tickManager) {
        this.tickManager = tickManager;
    }

    public void frameTick(TickManager tickManager) {
        if (this.isStopped || this.stopCalled) {
            return;
        }
        this.packetManager.tickNetworkManager();
        NetworkPacket p = this.packetManager.nextPacket();
        while (p != null) {
            if (!GameRandom.globalRandom.getChance(this.packetManager.dropPacketChance)) {
                this.processPacket(p);
            }
            p = this.packetManager.nextPacket();
        }
        if (!this.isPaused()) {
            this.world.frameTick(tickManager);
            for (int i = 0; i < this.getSlots(); ++i) {
                if (this.getClient(i) == null) continue;
                this.getClient(i).tickMovement(tickManager.getDelta());
            }
        }
    }

    public void tick() {
        ServerClient client;
        int i;
        if (this.isStopped) {
            return;
        }
        if (this.stopCalled) {
            this.privateStop(true);
            return;
        }
        if (this.isSingleplayer() && this.getLocalClient().hasDisconnected() && !this.isStopped()) {
            this.stop();
        }
        if (this.isHosted() && this.getLocalClient().hasDisconnected() && !this.isStopped()) {
            this.stop();
        }
        this.tickAutoSave();
        this.network.tickUnknownPacketTimeouts();
        int playersOnline = 0;
        for (i = 0; i < this.getSlots(); ++i) {
            client = this.getClient(i);
            if (client == null) continue;
            ++playersOnline;
            client.tickTimeConnected();
        }
        this.playersOnline = playersOnline;
        this.pauseCounter = playersOnline == 0 ? ++this.pauseCounter : 0;
        if (!this.isPaused()) {
            this.world.serverTick();
            for (i = 0; i < this.getSlots(); ++i) {
                client = this.getClient(i);
                if (client == null) continue;
                try {
                    client.tick();
                    continue;
                }
                catch (Exception e) {
                    System.err.println("Error ticking client \"" + client.getName() + "\" resulted in a kick.");
                    e.printStackTrace();
                    this.disconnectClient(client.slot, PacketDisconnect.Code.INTERNAL_ERROR);
                }
            }
            ((Stream)this.world.levelManager.getLoadedLevels().stream().filter(l -> l.unloadLevelBuffer > 20 * Math.max(2, Settings.unloadLevelsCooldown)).sorted(Comparator.comparingInt(l -> l.shouldSave() ? 1 : 0)).sequential()).forEach(l -> {
                this.world.levelManager.unloadLevel((Level)l);
                this.world.saveLevel((Level)l);
                System.out.println("Unloaded level " + l.getIdentifier());
            });
        }
    }

    public ServerClient getPacketClient(NetworkInfo networkInfo) {
        if (networkInfo == null) {
            return this.getLocalServerClient();
        }
        return networkInfo.getClient(this.streamClients());
    }

    private void processPacket(NetworkPacket packet) {
        int type = packet.type;
        ServerClient client = this.getPacketClient(packet.networkInfo);
        if (client == null && PacketRegistry.onlyConnectedClients(type)) {
            this.network.submitUnknownPacket(packet);
            return;
        }
        try {
            if (client != null) {
                client.submitInPacket(packet);
            }
            packet.processServer(this, client);
        }
        catch (Exception e) {
            System.err.println("Error processing client \"" + (client == null ? "NULL" : client.getName()) + "\" packet (" + PacketRegistry.getPacketSimpleName(packet.type) + ") resulted in a kick.");
            e.printStackTrace();
            Client localClient = this.getLocalClient();
            if (localClient != null && client != null && localClient.getSlot() == client.slot) {
                GameCrashLog.printCrashLog(e, localClient, this, "Server packet crash", false);
                this.stop(PacketDisconnect.Code.SERVER_ERROR);
            }
            if (client != null) {
                this.disconnectClient(client.slot, PacketDisconnect.Code.INTERNAL_ERROR);
            }
            this.stop(PacketDisconnect.Code.INTERNAL_ERROR);
        }
    }

    public void pause() {
        this.isPaused = true;
    }

    public void resume() {
        this.isPaused = false;
    }

    public boolean isPaused() {
        if (this.pauseForSpawnedPlayer && (this.isSingleplayer() || this.isHosted())) {
            return true;
        }
        if (Settings.pauseWhenEmpty && this.pauseCounter >= 200) {
            return true;
        }
        return this.isPaused;
    }

    public Stream<ServerClient> streamClients() {
        return Arrays.stream(this.clients).filter(Objects::nonNull);
    }

    public Iterable<ServerClient> getClients() {
        return () -> this.streamClients().iterator();
    }

    public ServerClient getLocalServerClient() {
        return this.streamClients().filter(c -> c.networkInfo == null).findFirst().orElse(null);
    }

    public ServerClient getClient(int slot) {
        if (slot < 0 || slot >= this.clients.length) {
            return null;
        }
        return this.clients[slot];
    }

    public PlayerMob getPlayer(int slot) {
        ServerClient client = this.getClient(slot);
        if (client != null) {
            return client.playerMob;
        }
        return null;
    }

    public ServerClient getClientByAuth(long auth) {
        return this.authClientMap.get(auth);
    }

    public PlayerMob getPlayerByAuth(long auth) {
        ServerClient client = this.getClientByAuth(auth);
        if (client != null) {
            return client.playerMob;
        }
        return null;
    }

    public String getNameByAuth(long auth, String defaultValue) {
        ServerClient client = this.getClientByAuth(auth);
        if (client != null) {
            return client.getName();
        }
        return this.usedNames.getOrDefault(auth, defaultValue);
    }

    public boolean addClient(NetworkInfo networkInfo, long authentication, String version, boolean craftingUsesNearbyInventories, boolean trackNewQuests, PacketClientInstalledDLC installedDLC) {
        String consoleName;
        if (!this.isSingleplayer()) {
            System.out.println("Client \"" + authentication + "\" with address " + (networkInfo == null ? "LOCAL" : networkInfo.getDisplayName()) + " is connecting with version " + version + ".");
        }
        boolean hasClient = this.world.hasClient(authentication);
        String clientName = this.usedNames.getOrDefault(authentication, "N/A");
        if (Settings.isBanned(authentication) || hasClient && Settings.isBanned(clientName)) {
            if ((this.isSingleplayer() || this.isHosted()) && authentication == GameAuth.getAuthentication()) {
                System.out.println("The singleplayer/host client was banned, unbanning now.");
                Settings.removeBanned(String.valueOf(authentication));
                if (hasClient) {
                    Settings.removeBanned(clientName);
                }
            } else {
                this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.BANNED_CLIENT), networkInfo));
                String addition = hasClient ? " (" + clientName + ")." : ".";
                System.out.println("Client " + authentication + " is banned" + addition);
                return false;
            }
        }
        String string = consoleName = hasClient ? "\"" + clientName + "\"" : "\"" + authentication + "\"";
        if (!version.equals("1.0.1")) {
            System.out.println("Client " + consoleName + " had wrong version (" + version + ").");
            this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.WRONG_VERSION), networkInfo));
            return false;
        }
        for (int i = 0; i < this.getSlots(); ++i) {
            ServerClient client = this.getClient(i);
            if (client == null || client.authentication != authentication) continue;
            this.packetManager.startVerboseLogging(networkInfo, 10000);
            if (Objects.equals(client.networkInfo, networkInfo)) {
                System.out.println("Client " + consoleName + " is already connected. Sending another approved packet...");
                client.sendPacket(new PacketConnectApproved(this, client));
                return false;
            }
            System.out.println("Client " + consoleName + " was already playing.");
            this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.ALREADY_PLAYING), networkInfo));
            return false;
        }
        int slotOffset = 0;
        for (int i = 0; i < this.getSlots(); ++i) {
            int actualSlot = (i + slotOffset) % this.getSlots();
            if (this.getClient(actualSlot) != null) continue;
            System.out.println("Client " + consoleName + " connected on slot " + (actualSlot + 1) + "/" + this.getSlots() + ".");
            long sessionID = GameRandom.globalRandom.nextLong();
            if (!hasClient) {
                System.out.println("Creating new player: " + authentication);
                this.clients[actualSlot] = ServerClient.getNewPlayerClient(this, sessionID, networkInfo, actualSlot, authentication);
                this.clients[actualSlot].saveClient();
            } else {
                this.clients[actualSlot] = this.world.loadClient(sessionID, authentication, networkInfo, actualSlot);
                System.out.println("Loaded player: " + authentication);
                if (!this.clients[actualSlot].needAppearance()) {
                    this.clients[actualSlot].sendConnectingMessage();
                }
            }
            this.authClientMap.put(authentication, this.clients[actualSlot]);
            this.clients[actualSlot].sendPacket(new PacketConnectApproved(this, this.clients[actualSlot]));
            this.clients[actualSlot].craftingUsesNearbyInventories = craftingUsesNearbyInventories;
            this.clients[actualSlot].trackNewQuests = trackNewQuests;
            this.clients[actualSlot].applyClientInstalledDLCPacket(installedDLC);
            GameEvents.triggerEvent(new ServerClientConnectedEvent(this.clients[actualSlot]));
            return true;
        }
        System.out.println("Could not find a slot for client \"" + authentication + "\".");
        this.network.sendPacket(new NetworkPacket(new PacketDisconnect(-1, PacketDisconnect.Code.SERVER_FULL), networkInfo));
        return false;
    }

    public int getSlots() {
        return this.settings.slots;
    }

    public boolean isSingleplayer() {
        return this.isSinglePlayer;
    }

    public boolean isHosted() {
        return this.isHosted;
    }

    public Client getLocalClient() {
        return this.client;
    }

    public void sendCommand(String command, ServerClient client) {
        if (this.commandsManager.runServerCommand(new ParsedCommand(command), client)) {
            if (client != null) {
                System.out.println(client.getName() + " issued command: /" + command);
            }
        } else if (client != null) {
            System.out.println(client.getName() + " tried to issue command: /" + command);
        }
    }

    public void startSaveTimer() {
        this.saveTime = this.world.worldEntity.getTime() + (long)Math.max(1, 60) * 1000L;
    }

    public void tickAutoSave() {
        if (this.saveTime <= this.world.worldEntity.getTime()) {
            this.saveAll();
            if (this.autoSaves % 15 == 0) {
                Thread thread = new Thread(() -> {
                    try {
                        boolean isDirectory = World.isWorldADirectory(this.world.filePath);
                        File targetPath = WorldSave.getNextBackupPath(isDirectory);
                        if (targetPath.exists()) {
                            GameUtils.deleteFileOrFolder(targetPath);
                        }
                        World.copyWorld(this.world.filePath, targetPath, false);
                        File deprecatedFile = new File(World.getSavesPath() + targetPath.getName());
                        if (deprecatedFile.exists()) {
                            World.deleteWorld(deprecatedFile);
                        }
                    }
                    catch (Exception e) {
                        System.err.println("Error saving latest backup");
                        e.printStackTrace();
                    }
                }, "Latest backup");
                thread.start();
            }
            ++this.autoSaves;
            this.startSaveTimer();
        }
    }

    public void saveAll() {
        try {
            this.saveAll(false);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAll(boolean closeFileSystem) throws IOException {
        block10: {
            Object client;
            this.world.saveWorldEntity();
            this.world.settings.saveSettings();
            this.world.worldEntity.dataComponentManager.streamAll(WorldSavedEntityComponent.class).forEach(WorldSavedEntityComponent::onWorldSaved);
            for (int i = 0; i < this.getSlots(); ++i) {
                client = this.getClient(i);
                if (client == null) continue;
                ((ServerClient)client).saveClient();
            }
            for (Level level : this.world.levelManager.getLoadedLevels()) {
                this.world.saveLevel(level);
            }
            if (closeFileSystem) {
                this.world.closeFileSystem();
            } else {
                try {
                    this.world.reloadFileSystem();
                }
                catch (Exception e) {
                    client = this.getLocalClient();
                    if (client != null) {
                        e.printStackTrace();
                        if (!this.world.fileSystem.isOpen()) {
                            ((Client)client).overrideDisconnectMessage = Localization.translate("misc", "savefailed1") + "\n\n" + e.getMessage();
                            this.stop(PacketDisconnect.Code.SERVER_ERROR);
                            this.privateStop(false);
                            if (this.world.filePath.getName().endsWith("zip")) {
                                GameCache.removeCache("continueLast");
                                ConfirmationContinueForm continueZipForm = new ConfirmationContinueForm("");
                                continueZipForm.setupConfirmation(new LocalMessage("misc", "savecompressfailed"), (GameMessage)new LocalMessage("ui", "acceptbutton"), (GameMessage)new LocalMessage("ui", "declinebutton"), () -> {
                                    Settings.zipSaves = false;
                                    continueZipForm.applyContinue();
                                }, continueZipForm::applyContinue);
                                ((Client)client).additionalDisconnectContinueForms.add(continueZipForm);
                            }
                        } else {
                            ((Client)client).chat.addMessage(GameColor.RED.getColorCode() + Localization.translate("misc", "savefailed1"));
                            ((Client)client).chat.addMessage(GameColor.RED.getColorCode() + e.getMessage());
                            ((Client)client).chat.addMessage(GameColor.RED.getColorCode() + Localization.translate("misc", "savefailed2"));
                        }
                    }
                    System.err.println(Localization.translate("misc", "savefailed1"));
                    e.printStackTrace();
                    System.err.println(Localization.translate("misc", "savefailed2"));
                    if (this.world.fileSystem.isOpen()) break block10;
                    this.stop(PacketDisconnect.Code.SERVER_ERROR);
                    this.privateStop(false);
                }
            }
        }
    }

    public int getSlot(PlayerMob player) {
        for (int i = 0; i < this.getSlots(); ++i) {
            ServerClient client = this.getClient(i);
            if (client == null || player != client.playerMob) continue;
            return i;
        }
        return -1;
    }

    public boolean disconnectClient(int slot, PacketDisconnect.Code code) {
        return this.disconnectClient(this.getClient(slot), code);
    }

    public boolean disconnectClient(ServerClient client, PacketDisconnect.Code code) {
        return this.disconnectClient(client, new PacketDisconnect(client.slot, code));
    }

    public boolean disconnectClient(int slot, PacketDisconnect packet) {
        return this.disconnectClient(this.getClient(slot), packet);
    }

    public boolean disconnectClient(ServerClient client, PacketDisconnect packet) {
        if (client != null) {
            this.network.sendToAllClients(packet);
            this.disconnectClient(client, true);
            return true;
        }
        return false;
    }

    private void disconnectClient(ServerClient client, boolean save) {
        client.onUnloading();
        if (save) {
            client.saveClient();
        }
        GameEvents.triggerEvent(new ServerClientDisconnectEvent(client));
        client.dispose();
        this.clients[client.slot] = null;
        this.authClientMap.remove(client.authentication);
    }

    public int getPlayersOnline() {
        return this.playersOnline;
    }

    private void privateStop(boolean save) {
        ServerClient client;
        int i;
        block5: {
            this.world.levelManager.getLoadedLevels().stream().filter(l -> !l.shouldSave()).forEach(level -> this.streamClients().filter(c -> c.getLevelIdentifier().equals(level.getIdentifier())).forEach(c -> c.changeToFallbackLevel(level.getIdentifier(), false)));
            for (i = 0; i < this.getSlots(); ++i) {
                client = this.getClient(i);
                if (client == null) continue;
                client.sendPacket(new PacketDisconnect(this.stopPacket, i));
                client.onUnloading();
            }
            this.world.levelManager.getLoadedLevels().stream().sorted(Comparator.comparingInt(l -> l.shouldSave() ? 1 : 0)).forEach(level -> {
                level.onUnloading();
                level.dispose();
            });
            if (save) {
                try {
                    this.saveAll(true);
                    System.out.println("Saved all data.");
                    System.out.println("World time: " + this.world.worldEntity.getDayTimeInt() + ", day " + this.world.worldEntity.getDay());
                    System.out.println("Game time: " + this.world.worldEntity.getTime());
                    System.out.println("Total ticks: " + this.tickManager().getTotalTicks());
                    System.out.println("Received: " + this.packetManager.getTotalIn() + " (" + this.packetManager.getTotalInPackets() + " packets)");
                    System.out.println("Sent: " + this.packetManager.getTotalOut() + " (" + this.packetManager.getTotalOutPackets() + " packets)");
                    System.out.println("Stopped server on " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + " with code: " + (Object)((Object)this.stopPacket.code));
                }
                catch (IOException e) {
                    System.err.println(Localization.translate("misc", "savefailed1"));
                    e.printStackTrace();
                    GameCache.removeCache("continueLast");
                    this.stopErrors = new LinkedList<ContinueComponent>();
                    NoticeForm saveFailedForm = new NoticeForm("notice");
                    saveFailedForm.setupNotice(new GameMessageBuilder().append("misc", "savefailed1").append("\n\n" + e.getMessage()));
                    this.stopErrors.add(saveFailedForm);
                    if (this.world.fileSystem.isOpen()) break block5;
                    ConfirmationContinueForm continueZipForm = new ConfirmationContinueForm("");
                    continueZipForm.setupConfirmation(new LocalMessage("misc", "savecompressfailed"), (GameMessage)new LocalMessage("ui", "acceptbutton"), (GameMessage)new LocalMessage("ui", "declinebutton"), () -> {
                        Settings.zipSaves = false;
                        continueZipForm.applyContinue();
                    }, continueZipForm::applyContinue);
                    this.stopErrors.add(continueZipForm);
                }
            }
        }
        for (i = 0; i < this.getSlots(); ++i) {
            client = this.getClient(i);
            if (client == null) continue;
            client.dispose();
        }
        GameEvents.triggerEvent(new ServerStopEvent(this));
        this.network.close();
        this.world.dispose();
        this.isStopped = true;
        this.stopEvents.forEach(c -> c.accept(this));
    }

    public boolean isStopped() {
        return this.stopCalled || this.isStopped;
    }

    public boolean hasClosed() {
        return this.isStopped;
    }

    public void stop() {
        this.stop((PacketDisconnect)null, null);
    }

    public void stop(Consumer<Server> onStop) {
        this.stop((PacketDisconnect)null, onStop);
    }

    public void stop(PacketDisconnect.Code code) {
        this.stop(code, null);
    }

    public void stop(PacketDisconnect.Code code, Consumer<Server> onStop) {
        this.stop(code == null ? null : new PacketDisconnect(0, code), onStop);
    }

    public void stop(PacketDisconnect stopPacket, Consumer<Server> onStop) {
        if (stopPacket == null) {
            stopPacket = new PacketDisconnect(0, PacketDisconnect.Code.SERVER_STOPPED);
        }
        this.stopPacket = stopPacket;
        if (onStop != null) {
            this.stopEvents.add(onStop);
        }
        this.stopCalled = true;
    }

    public TickManager tickManager() {
        return this.tickManager;
    }

    private PacketManager newPacketManager() {
        return new PacketManager(){

            @Override
            public void processInstantly(NetworkPacket packet) {
                Server.this.processPacket(packet);
            }
        };
    }

    public ServerSettings getSettings() {
        return this.settings;
    }
}

