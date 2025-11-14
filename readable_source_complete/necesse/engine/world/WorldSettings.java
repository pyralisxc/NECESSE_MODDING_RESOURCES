/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameDifficulty;
import necesse.engine.GameLog;
import necesse.engine.GameRaidFrequency;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketSettings;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.World;
import necesse.engine.world.WorldFile;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;

public class WorldSettings {
    public static boolean cheatsHidden = false;
    public boolean allowCheats = false;
    public GameDifficulty difficulty = GameDifficulty.CLASSIC;
    public GameDeathPenalty deathPenalty = GameDeathPenalty.DROP_MATS;
    public GameRaidFrequency raidFrequency = GameRaidFrequency.OCCASIONALLY;
    public boolean survivalMode = true;
    public boolean playerHunger = true;
    public boolean disableMobSpawns = false;
    public boolean forcedPvP = false;
    public boolean allowOutsideCharacters = true;
    public boolean creativeMode = false;
    private boolean lastCreativeMode = false;
    public boolean disableMobAI = false;
    public float dayTimeMod = 1.0f;
    public float nightTimeMod = 1.0f;
    public int droppedItemsLifeMinutes = Settings.droppedItemsLifeMinutes;
    public boolean unloadSettlements = Settings.unloadSettlements;
    public int maxSettlementsPerPlayer = Settings.maxSettlementsPerPlayer;
    public int maxSettlersPerSettlement = Settings.maxSettlersPerSettlement;
    public String gameVersion = "1.0.1";
    private final World world;
    private Client client;

    public WorldSettings(World world) {
        this.world = world;
    }

    public WorldSettings(Client client, PacketReader reader, boolean beforeConnected) {
        this.world = null;
        this.client = client;
        if (beforeConnected) {
            this.applyBeforeConnectedPacket(reader);
        } else {
            this.applyContentPacket(reader);
        }
    }

    public void sendSettingsPacket() {
        if (this.world == null) {
            throw new NullPointerException("Cannot send settings packet from null world.");
        }
        this.world.server.network.sendToAllClients(new PacketSettings(this));
    }

    public void saveSettings() {
        if (this.creativeMode && !this.lastCreativeMode) {
            if (this.world.worldEntity != null && this.world.getWorldTime() != 0L) {
                this.creativeMode = false;
                Server server = this.world.server;
                server.saveAll();
                try {
                    File targetPath = WorldSave.getCreativeBackupPath(this.world.filePath);
                    if (targetPath.exists()) {
                        throw new FileAlreadyExistsException(targetPath.toString());
                    }
                    World.copyWorld(this.world.filePath, targetPath, false);
                    server.network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativeworldbackedupmessage", "worldname", this.world.displayName)));
                }
                catch (Exception e) {
                    GameLog.err.println("Error creating creative backup");
                    e.printStackTrace(GameLog.err);
                }
                this.creativeMode = true;
            }
            this.lastCreativeMode = true;
        }
        this.saveSettings(this.world.fileSystem.getWorldSettingsFile());
    }

    public void saveSettings(WorldFile file) {
        if (this.world == null) {
            throw new NullPointerException("Cannot save settings from null world.");
        }
        this.getSaveScript().saveScript(file);
    }

    public void loadSettings() {
        this.loadSettings(true);
    }

    public void loadSettings(boolean createFile) {
        if (this.world == null) {
            throw new NullPointerException("Cannot load settings from null world.");
        }
        WorldFile file = this.world.fileSystem.getWorldSettingsFile();
        if (file.exists() && !file.isDirectory()) {
            this.loadSaveScript(new LoadData(file));
        } else if (createFile) {
            this.saveSettings();
        }
    }

    private SaveData getSaveScript() {
        SaveData save = new SaveData("WORLDSETTINGS");
        save.addBoolean("allowCheats", this.allowCheats);
        save.addEnum("difficulty", this.difficulty);
        save.addEnum("deathPenalty", this.deathPenalty);
        save.addEnum("raidFrequency", this.raidFrequency);
        save.addBoolean("survivalMode", this.survivalMode);
        save.addBoolean("playerHunger", this.playerHunger);
        save.addBoolean("disableMobSpawns", this.disableMobSpawns);
        save.addBoolean("forcedPvP", this.forcedPvP, "True = players will always have PvP enabled");
        save.addBoolean("allowOutsideCharacters", this.allowOutsideCharacters);
        save.addBoolean("creativeMode", this.creativeMode);
        save.addBoolean("disableMobAI", this.disableMobAI);
        save.addFloat("dayTimeMod", this.dayTimeMod, "Day time modifier (The higher, the longer day will last, max 10)");
        save.addFloat("nightTimeMod", this.nightTimeMod, "Night time modifier (The higher, the longer night will last, max 10)");
        save.addSafeString("gameVersion", "1.0.1");
        return save;
    }

    private void loadSaveScript(LoadData save) {
        LoadData versionSave;
        this.allowCheats = save.getBoolean("allowCheats", this.allowCheats);
        this.difficulty = save.getEnum(GameDifficulty.class, "difficulty", this.difficulty, false);
        if ("EASY".equals(save.getUnsafeString("difficulty", null, false))) {
            this.difficulty = GameDifficulty.ADVENTURE;
        }
        this.deathPenalty = save.getEnum(GameDeathPenalty.class, "deathPenalty", this.deathPenalty, false);
        this.raidFrequency = save.getEnum(GameRaidFrequency.class, "raidFrequency", this.raidFrequency, false);
        this.survivalMode = save.getBoolean("survivalMode", this.survivalMode, false);
        this.playerHunger = save.getBoolean("playerHunger", this.playerHunger, false);
        this.disableMobSpawns = save.getBoolean("disableMobSpawns", this.disableMobSpawns);
        this.forcedPvP = save.getBoolean("forcedPvP", this.forcedPvP);
        if (save.hasLoadDataByName("allowOutsideCharacters")) {
            this.allowOutsideCharacters = save.getBoolean("allowOutsideCharacters", this.allowOutsideCharacters, false);
        } else if (this.world != null && this.world.server != null && !this.world.server.isHosted() && !this.world.server.isSingleplayer()) {
            this.allowOutsideCharacters = false;
            GameLog.warn.println("Set default allow outside characters to false for " + this.world.filePath.getName());
        }
        this.creativeMode = this.lastCreativeMode = save.getBoolean("creativeMode", this.creativeMode, false);
        this.disableMobAI = save.getBoolean("disableMobAI", this.disableMobAI, false);
        if (save.hasLoadDataByName("dayTimeMod")) {
            this.dayTimeMod = save.getFloat("dayTimeMod", this.dayTimeMod);
            this.nightTimeMod = save.getFloat("nightTimeMod", this.nightTimeMod);
            this.dayTimeMod = Math.min(Math.max(0.0f, this.dayTimeMod), 10.0f);
            this.nightTimeMod = Math.min(Math.max(0.0f, this.nightTimeMod), 10.0f);
            this.dayTimeMod = (float)((int)(this.dayTimeMod * 10.0f)) / 10.0f;
            this.nightTimeMod = (float)((int)(this.nightTimeMod * 10.0f)) / 10.0f;
            if (this.dayTimeMod == 0.0f && this.nightTimeMod == 0.0f) {
                this.dayTimeMod = 1.0f;
            }
        }
        if ((versionSave = save.getFirstLoadDataByName("gameVersion")) != null) {
            this.gameVersion = LoadData.getSafeString(versionSave);
        }
    }

    public void setupBeforeConnectedPacket(PacketWriter writer) {
        writer.putNextBoolean(this.allowCheats);
        writer.putNextByteUnsigned(this.difficulty.ordinal());
        writer.putNextByteUnsigned(this.deathPenalty.ordinal());
        writer.putNextByteUnsigned(this.raidFrequency.ordinal());
        writer.putNextBoolean(this.survivalMode);
        writer.putNextBoolean(this.playerHunger);
        writer.putNextBoolean(this.disableMobSpawns);
        writer.putNextBoolean(this.forcedPvP);
        writer.putNextBoolean(this.allowOutsideCharacters);
        writer.putNextBoolean(this.creativeMode);
        writer.putNextBoolean(this.disableMobAI);
        writer.putNextFloat(this.dayTimeMod);
        writer.putNextFloat(this.nightTimeMod);
        writer.putNextInt(this.droppedItemsLifeMinutes);
        writer.putNextBoolean(this.unloadSettlements);
        writer.putNextInt(this.maxSettlementsPerPlayer);
        writer.putNextInt(this.maxSettlersPerSettlement);
    }

    public void applyBeforeConnectedPacket(PacketReader reader) {
        this.allowCheats = reader.getNextBoolean();
        this.difficulty = GameDifficulty.values()[reader.getNextByteUnsigned()];
        this.deathPenalty = GameDeathPenalty.values()[reader.getNextByteUnsigned()];
        this.raidFrequency = GameRaidFrequency.values()[reader.getNextByteUnsigned()];
        this.survivalMode = reader.getNextBoolean();
        this.playerHunger = reader.getNextBoolean();
        this.disableMobSpawns = reader.getNextBoolean();
        this.forcedPvP = reader.getNextBoolean();
        this.allowOutsideCharacters = reader.getNextBoolean();
        boolean newCreativeMode = reader.getNextBoolean();
        if (this.client != null && this.client.characterFilePath != null && newCreativeMode) {
            CharacterSave.backupCharacterForCreativeIfNecessary(this.client);
        }
        this.creativeMode = newCreativeMode;
        this.disableMobAI = reader.getNextBoolean();
        this.dayTimeMod = reader.getNextFloat();
        this.nightTimeMod = reader.getNextFloat();
        if (this.world != null) {
            this.world.worldEntity.calculateAmbientLightValues();
        }
        if (this.client != null && this.client.worldEntity != null) {
            this.client.worldEntity.calculateAmbientLightValues();
        }
        this.droppedItemsLifeMinutes = reader.getNextInt();
        this.unloadSettlements = reader.getNextBoolean();
        this.maxSettlementsPerPlayer = reader.getNextInt();
        this.maxSettlersPerSettlement = reader.getNextInt();
        State state = GlobalData.getCurrentState();
        if (state instanceof MainGame) {
            ((MainGame)state).formManager.updateActive(true);
        }
    }

    public void setupContentPacket(PacketWriter writer) {
        this.setupBeforeConnectedPacket(writer);
    }

    public void applyContentPacket(PacketReader reader) {
        this.applyBeforeConnectedPacket(reader);
    }

    public void setupCreativePacket(PacketWriter writer) {
        writer.putNextByteUnsigned(this.difficulty.ordinal());
        writer.putNextByteUnsigned(this.deathPenalty.ordinal());
        writer.putNextByteUnsigned(this.raidFrequency.ordinal());
        writer.putNextBoolean(this.survivalMode);
        writer.putNextBoolean(this.playerHunger);
        writer.putNextBoolean(this.disableMobSpawns);
        writer.putNextBoolean(this.disableMobAI);
    }

    public void applyCreativePacket(PacketReader reader, ServerClient client) {
        boolean newMobAiDisabled;
        boolean newDisableMobSpawns;
        boolean newPlayerHunger;
        boolean newSurvivalMode;
        GameRaidFrequency newRaidFrequency;
        GameDeathPenalty newDeathPenalty;
        LocalMessage enabled = new LocalMessage("ui", "creativemsgenabled");
        LocalMessage disabled = new LocalMessage("ui", "creativemsgdisabled");
        GameDifficulty newDifficulty = GameDifficulty.values()[reader.getNextByteUnsigned()];
        if (newDifficulty != this.difficulty) {
            this.difficulty = newDifficulty;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativedifficultymsg", "player", client.getName(), "difficulty", this.difficulty.displayName)));
        }
        if ((newDeathPenalty = GameDeathPenalty.values()[reader.getNextByteUnsigned()]) != this.deathPenalty) {
            this.deathPenalty = newDeathPenalty;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativedeathpenaltymsg", "player", client.getName(), "penalty", this.deathPenalty.displayName)));
        }
        if ((newRaidFrequency = GameRaidFrequency.values()[reader.getNextByteUnsigned()]) != this.raidFrequency) {
            this.raidFrequency = newRaidFrequency;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativeraidfrequencymsg", "player", client.getName(), "frequency", this.raidFrequency.displayName)));
        }
        if ((newSurvivalMode = reader.getNextBoolean()) != this.survivalMode) {
            this.survivalMode = newSurvivalMode;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativesurvivalmodemsg", "player", client.getName(), "action", this.survivalMode ? enabled : disabled)));
        }
        if ((newPlayerHunger = reader.getNextBoolean()) != this.playerHunger) {
            this.playerHunger = newPlayerHunger;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativeplayerhungermsg", "player", client.getName(), "action", this.playerHunger ? enabled : disabled)));
        }
        if ((newDisableMobSpawns = reader.getNextBoolean()) != this.disableMobSpawns) {
            this.disableMobSpawns = newDisableMobSpawns;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativehostilemobspawningmsg", "player", client.getName(), "action", this.disableMobSpawns ? disabled : enabled)));
        }
        if ((newMobAiDisabled = reader.getNextBoolean()) != this.disableMobAI) {
            this.disableMobAI = newMobAiDisabled;
            client.getServer().network.sendToAllClients(new PacketChatMessage(new LocalMessage("ui", "creativemobaimsg", "player", client.getName(), "action", this.disableMobAI ? disabled : enabled)));
        }
    }

    public GameTooltips getTooltips(LocalMessage title) {
        StringTooltips tooltips = new StringTooltips(title.translate());
        tooltips.add(new LocalMessage("ui", "difficultytip", "difficulty", this.difficulty.displayName).translate());
        tooltips.add(new LocalMessage("ui", "dptip", "penalty", this.deathPenalty.displayName).translate());
        if (this.survivalMode) {
            tooltips.add(Localization.translate("ui", "survivalmode"));
        }
        if (this.creativeMode) {
            tooltips.add(Localization.translate("ui", "creativemode"));
        }
        if (this.disableMobAI) {
            tooltips.add(Localization.translate("ui", "creativemobaiisdisabled"));
        } else {
            tooltips.add(Localization.translate("ui", this.playerHunger ? "hungerenabled" : "hungerdisabled"));
        }
        tooltips.add(Localization.translate("ui", this.disableMobSpawns ? "mobsdisabled" : "mobsenabled"));
        tooltips.add(Localization.translate("ui", this.forcedPvP ? "forcepvpon" : "forcepvpoff"));
        tooltips.add(Localization.translate("ui", this.forcedPvP ? "forcepvpon" : "forcepvpoff"));
        GameWindow window = WindowManager.getWindow();
        if (window.isKeyDown(340) || window.isKeyDown(344)) {
            tooltips.add(Localization.translate("ui", "daymod", "mod", (int)(this.dayTimeMod * 100.0f) + "%"));
            tooltips.add(Localization.translate("ui", "nightmod", "mod", (int)(this.nightTimeMod * 100.0f) + "%"));
        } else {
            tooltips.add(Localization.translate("ui", "showadvanced"), GameColor.LIGHT_GRAY);
        }
        return tooltips;
    }

    public boolean playerHunger() {
        return this.survivalMode || this.playerHunger;
    }

    public void enableCheats() {
        if (this.allowCheats) {
            return;
        }
        this.allowCheats = true;
        this.saveSettings();
        this.sendSettingsPacket();
    }

    public void enableCreativeMode(boolean saveAndSync) {
        if (this.creativeMode) {
            return;
        }
        if (this.client != null && this.client.getClient() != null && this.client.getClient().isClient()) {
            CharacterSave.backupCharacterForCreativeIfNecessary(this.client);
        }
        this.creativeMode = true;
        if (saveAndSync) {
            this.saveSettings();
            this.sendSettingsPacket();
        }
    }

    public boolean achievementsEnabled() {
        return !this.allowCheats && !this.creativeMode;
    }

    public boolean cheatsAllowedOrHidden() {
        return this.allowCheats || cheatsHidden;
    }
}

