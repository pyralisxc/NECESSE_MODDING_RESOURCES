/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import necesse.engine.GameAuth;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.save.CharacterSaveNetworkData;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveData;
import necesse.engine.save.SaveSyntaxException;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;

public class CharacterSave {
    public final int characterUniqueID;
    public ArrayList<ModSaveInfo> lastMods = null;
    public final PlayerMob player;
    public final boolean cheatsEnabled;
    public final boolean creativeEnabled;
    public final PlayerStats characterStats;
    public final long timePlayed;
    public final long lastUsedAuth;
    public GameMessage lastUsed;
    private long timeModified;
    private String dateModified;
    private static final Pattern appendedFileNamePattern = Pattern.compile("(#\\d+)?(-backup(\\d+)?)?$");
    public static Pattern backupFileNamePattern = Pattern.compile("-backup(\\d+)?$");

    public CharacterSave(Client client) {
        this.characterUniqueID = client.getCharacterUniqueID();
        ClientClient me = client.getClient();
        this.player = me == null ? null : me.playerMob;
        this.cheatsEnabled = client.worldSettings != null && client.worldSettings.allowCheats;
        this.creativeEnabled = client.worldSettings != null && client.worldSettings.creativeMode;
        this.characterStats = client.characterStats;
        this.timePlayed = this.characterStats == null ? 0L : (long)this.characterStats.time_played.get();
        this.lastUsedAuth = GameAuth.getAuthentication();
        this.lastUsed = client.playingOnDisplayName != null ? new LocalMessage("ui", "characterlastworld", "world", client.playingOnDisplayName) : null;
    }

    public CharacterSave(CharacterSave original, int newUniqueID) {
        this.characterUniqueID = newUniqueID;
        this.player = original.player;
        this.cheatsEnabled = original.cheatsEnabled;
        this.creativeEnabled = original.creativeEnabled;
        this.characterStats = original.characterStats;
        this.timePlayed = original.timePlayed;
        this.lastUsedAuth = original.lastUsedAuth;
        this.lastMods = original.lastMods;
        this.lastUsed = original.lastUsed;
        this.timeModified = original.timeModified;
        this.dateModified = original.dateModified;
    }

    public CharacterSave(ServerClient client) {
        this.characterUniqueID = client.getCharacterUniqueID();
        this.player = client.playerMob;
        this.cheatsEnabled = client.getServer().world.settings.allowCheats;
        this.creativeEnabled = client.getServer().world.settings.creativeMode;
        this.characterStats = client.characterStats();
        this.timePlayed = this.characterStats == null ? 0L : (long)this.characterStats.time_played.get();
        this.lastUsedAuth = -1L;
    }

    private CharacterSave(int characterUniqueID, PlayerMob player, long timePlayed) {
        this.characterUniqueID = characterUniqueID;
        this.player = player;
        this.cheatsEnabled = false;
        this.creativeEnabled = false;
        this.characterStats = null;
        this.timePlayed = timePlayed;
        this.lastUsedAuth = -1L;
    }

    public static CharacterSave newCharacter(int characterUniqueID, PlayerMob player, long timePlayed) {
        return new CharacterSave(characterUniqueID, player, timePlayed);
    }

    public static CharacterSave newCharacter(PlayerMob player, long timePlayed) {
        return CharacterSave.newCharacter(CharacterSave.getNewUniqueCharacterID(null), player, timePlayed);
    }

    public CharacterSave(LoadData save, File filePath, long timeModified) {
        LoadData playerSave;
        LoadData modsData;
        this.timeModified = timeModified;
        if (timeModified != 0L) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.dateModified = sdf.format(timeModified);
        }
        this.characterUniqueID = save.getInt("characterUniqueID", -1, false);
        if (this.characterUniqueID == -1) {
            throw new IllegalArgumentException("Could not load character uniqueID");
        }
        this.player = new PlayerMob(this.characterUniqueID, null);
        this.player.playerName = save.getSafeString("name", "N/A");
        if (this.player.playerName.equals("N/A")) {
            throw new IllegalArgumentException("Could not load character \"" + this.characterUniqueID + "\" name");
        }
        if (filePath != null) {
            String fileNameWithoutExtension = GameUtils.removeFileExtension(filePath.getName());
            fileNameWithoutExtension = appendedFileNamePattern.matcher(fileNameWithoutExtension).replaceFirst("");
            String expectedFileName = CharacterSave.fromPlayerNameToFileName(this.player.playerName);
            if (!expectedFileName.equals(fileNameWithoutExtension)) {
                this.player.playerName = fileNameWithoutExtension;
            }
        }
        this.lastUsedAuth = save.getLong("lastUsedAuth", -1L, false);
        LoadData lastUsedSave = save.getFirstLoadDataByName("lastUsed");
        if (lastUsedSave != null) {
            try {
                this.lastUsed = GameMessage.loadSave(lastUsedSave);
            }
            catch (Exception expectedFileName) {
                // empty catch block
            }
        }
        if ((modsData = save.getFirstLoadDataByName("MODS")) != null) {
            this.lastMods = new ArrayList();
            for (LoadData modData : modsData.getLoadData()) {
                try {
                    this.lastMods.add(ModSaveInfo.fromSave(modData));
                }
                catch (LoadDataException e) {
                    GameLog.warn.println("Could not load mod info: " + e.getMessage());
                }
            }
        }
        if ((playerSave = save.getFirstLoadDataByName("PLAYER")) != null) {
            try {
                this.player.applyLoadedCharacterLoadData(playerSave);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Could not load character \"" + this.characterUniqueID + "\" player data", e);
            }
        } else {
            throw new IllegalArgumentException("Could not find character \"" + this.characterUniqueID + "\" player data");
        }
        this.cheatsEnabled = save.getBoolean("cheatsEnabled", false, false);
        this.creativeEnabled = save.getBoolean("creativeEnabled", false, false);
        LoadData statsSave = save.getFirstLoadDataByName("STATS");
        if (statsSave != null) {
            PlayerStats loadedStats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
            try {
                loadedStats.applyLoadData(statsSave);
            }
            catch (Exception e) {
                loadedStats = null;
                System.err.println("Could not load character \"" + this.characterUniqueID + "\" stats");
                e.printStackTrace();
            }
            this.characterStats = loadedStats;
        } else {
            this.characterStats = null;
        }
        this.timePlayed = this.characterStats == null ? 0L : (long)this.characterStats.time_played.get();
    }

    public void addSaveData(SaveData save) {
        save.addInt("characterUniqueID", this.characterUniqueID);
        save.addSafeString("name", this.player.playerName);
        if (this.lastUsedAuth != -1L) {
            save.addLong("lastUsedAuth", this.lastUsedAuth);
        }
        if (this.lastUsed != null) {
            save.addSaveData(this.lastUsed.getSaveData("lastUsed"));
        }
        SaveData modsData = new SaveData("MODS");
        for (LoadedMod mod : ModLoader.getEnabledMods()) {
            SaveData data = mod.getModSaveInfo().getSaveData();
            modsData.addSaveData(data);
        }
        save.addSaveData(modsData);
        SaveData playerSave = new SaveData("PLAYER");
        this.player.addLoadedCharacterSaveData(playerSave);
        save.addSaveData(playerSave);
        if (this.cheatsEnabled) {
            save.addBoolean("cheatsEnabled", true);
        }
        if (this.creativeEnabled) {
            save.addBoolean("creativeEnabled", true);
        }
        if (this.characterStats != null) {
            SaveData statsSave = new SaveData("STATS");
            this.characterStats.addSaveData(statsSave);
            save.addSaveData(statsSave);
        }
    }

    public CharacterSave(int characterUniqueID, CharacterSaveNetworkData data) {
        this.characterUniqueID = characterUniqueID;
        this.player = new PlayerMob(characterUniqueID, null);
        data.applyToPlayer(this.player);
        if (data.characterStatsData != null) {
            this.characterStats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
            data.applyToStats(this.characterStats);
        } else {
            this.characterStats = null;
        }
        this.timePlayed = data.timePlayed;
        this.lastUsedAuth = -1L;
        this.cheatsEnabled = data.cheatsEnabled;
        this.creativeEnabled = data.creativeEnabled;
    }

    public long getTimeModified() {
        return this.timeModified;
    }

    public String getModifiedDate() {
        return this.dateModified;
    }

    public static String fromPlayerNameToFileName(String playerName) {
        String fileName = GameUtils.toValidFileName(playerName);
        if (fileName.length() > 200) {
            fileName = fileName.substring(0, 200);
        } else if (fileName.isEmpty()) {
            return "char";
        }
        return fileName;
    }

    public static int getNewUniqueCharacterID(Predicate<Integer> isValidID) {
        for (int i = 0; i < 1000; ++i) {
            int nextID = GameRandom.globalRandom.nextInt();
            if (isValidID != null && !isValidID.test(nextID)) continue;
            return nextID;
        }
        GameLog.warn.println("Could not find a new valid character uniqueID. Using a random one");
        return GameRandom.globalRandom.nextInt();
    }

    public static File saveCharacter(CharacterSave character, File file, boolean createBackup) {
        SaveData characterSave = new SaveData("CHARACTER");
        character.addSaveData(characterSave);
        if (file == null) {
            String fileName = CharacterSave.fromPlayerNameToFileName(character.player.playerName);
            int extension = 0;
            while (true) {
                String fileNameWithExtension = fileName + (extension != 0 ? "#" + extension : "");
                File nextFile = new File(CharacterSave.getCharacterSavesPath() + fileNameWithExtension + ".dat");
                if (!nextFile.exists()) {
                    file = nextFile;
                    break;
                }
                try {
                    CharacterSave existingCharacter = new CharacterSave(new LoadData(file), null, 0L);
                    if (existingCharacter.characterUniqueID == character.characterUniqueID) {
                        file = nextFile;
                        break;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                ++extension;
            }
        }
        characterSave.saveScript(file);
        if (createBackup) {
            String fileNameRemovedExtension = GameUtils.removeFileExtension(file.getName());
            File backupFile = GameUtils.resolveFile(file.getParentFile(), fileNameRemovedExtension + "-backup" + ".dat");
            try {
                GameUtils.copyFileOrFolderReplaceExisting(file, backupFile);
            }
            catch (IOException e) {
                System.err.println("Could not create backup of character to " + fileNameRemovedExtension);
                e.printStackTrace();
            }
        }
        return file;
    }

    public static File saveCharacter(Client client, boolean createBackup) {
        ClientClient me = client.getClient();
        if (me != null && me.loadedPlayer && me.playerMob != null && client.worldSettings != null) {
            File file;
            client.characterFilePath = file = CharacterSave.saveCharacter(new CharacterSave(client), client.characterFilePath, createBackup);
            return file;
        }
        GameLog.warn.println("Could not save character because no character was loaded");
        return null;
    }

    public static File backupCharacterForCreativeIfNecessary(CharacterSave character, Client client) {
        File filePath = client.characterFilePath;
        if (filePath != null && !character.creativeEnabled && character.timePlayed > 0L) {
            character = new CharacterSave(character, CharacterSave.getNewUniqueCharacterID(null));
            String filePathNoExtension = GameUtils.removeFileExtension(filePath.getAbsolutePath());
            String backupFilePath = filePathNoExtension + " backup" + ".dat";
            File file = CharacterSave.saveCharacter(character, new File(backupFilePath), false);
            client.chat.addMessage(new LocalMessage("ui", "creativecharacterbackedupmessage", "character", character.player.playerName).translate());
            return file;
        }
        return null;
    }

    public static File backupCharacterForCreativeIfNecessary(Client client) {
        return CharacterSave.backupCharacterForCreativeIfNecessary(new CharacterSave(client), client);
    }

    public static String getCharacterSavesPath() {
        return GlobalData.appDataPath() + "saves/characters/";
    }

    public static boolean deleteCharacter(File file) {
        return GameUtils.deleteFileOrFolder(file);
    }

    public static void loadCharacters(BiConsumer<File, CharacterSave> onLoaded, Supplier<Boolean> isInterrupted, Consumer<Boolean> onDone, int limit) {
        if (isInterrupted != null && isInterrupted.get().booleanValue()) {
            if (onDone != null) {
                onDone.accept(true);
            }
            return;
        }
        File[] files = new File(CharacterSave.getCharacterSavesPath()).listFiles();
        if (files == null) {
            files = new File[]{};
        }
        ArrayList<ObjectValue> computedFiles = new ArrayList<ObjectValue>(files.length);
        for (File file : files) {
            if (!file.isFile() || backupFileNamePattern.matcher(GameUtils.removeFileExtension(file.getName())).find()) continue;
            computedFiles.add(new ObjectValue<File, ComparableSequence<Long>>(file, new ComparableSequence<Long>(file.lastModified())));
        }
        Comparator<ObjectValue> comparator = Comparator.comparing(f -> (ComparableSequence)f.value);
        computedFiles.sort(comparator.reversed());
        int loaded = 0;
        for (ObjectValue computedFile : computedFiles) {
            if (isInterrupted != null && isInterrupted.get().booleanValue()) {
                if (onDone != null) {
                    onDone.accept(true);
                }
                return;
            }
            if (limit >= 0 && loaded >= limit) break;
            try {
                LoadData characterData = new LoadData(SaveComponent.loadScriptRaw((File)computedFile.object, false));
                long lastModified = ((File)computedFile.object).lastModified();
                CharacterSave characterSave = new CharacterSave(characterData, (File)computedFile.object, lastModified);
                if (isInterrupted != null && isInterrupted.get().booleanValue()) {
                    if (onDone != null) {
                        onDone.accept(true);
                    }
                    return;
                }
                onLoaded.accept((File)computedFile.object, characterSave);
                ++loaded;
            }
            catch (IOException e) {
                System.err.println("Error loading character file: " + ((File)computedFile.object).getName());
                e.printStackTrace();
            }
            catch (IllegalArgumentException | SaveSyntaxException e) {
                System.err.println("Error loading character script: " + ((File)computedFile.object).getName());
                e.printStackTrace();
            }
            catch (Exception e) {
                System.err.println("Unknown error loading character: " + ((File)computedFile.object).getName());
                e.printStackTrace();
            }
        }
        if (onDone != null) {
            onDone.accept(false);
        }
    }

    public boolean achievementsDisabled() {
        return this.creativeEnabled || this.cheatsEnabled;
    }
}

