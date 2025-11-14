/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.ChatCommand
 *  necesse.engine.commands.CommandsManager
 *  necesse.engine.commands.ModularChatCommand
 *  necesse.engine.commands.PermissionLevel
 */
package medievalsim.commandcenter.wrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import medievalsim.commandcenter.CommandCategory;
import medievalsim.commandcenter.wrapper.NecesseCommandMetadata;
import medievalsim.util.ModLogger;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CommandsManager;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;

public class NecesseCommandRegistry {
    private static final Map<String, NecesseCommandMetadata> commands = new ConcurrentHashMap<String, NecesseCommandMetadata>();
    private static final Map<CommandCategory, List<NecesseCommandMetadata>> commandsByCategory = new ConcurrentHashMap<CommandCategory, List<NecesseCommandMetadata>>();
    private static boolean initialized = false;

    public static void initialize() {
        if (initialized) {
            ModLogger.warn("NecesseCommandRegistry already initialized");
            return;
        }
        ModLogger.info("Scanning Necesse commands for UI wrapper...");
        try {
            Field serverCommandsField = CommandsManager.class.getDeclaredField("serverCommands");
            serverCommandsField.setAccessible(true);
            List necesseCommands = (List)serverCommandsField.get(null);
            int scanned = 0;
            int parsed = 0;
            for (ChatCommand cmd : necesseCommands) {
                NecesseCommandMetadata metadata;
                ModularChatCommand modCmd;
                CommandCategory category;
                ++scanned;
                if (!(cmd instanceof ModularChatCommand) || (category = NecesseCommandRegistry.categorizeCommand(modCmd = (ModularChatCommand)cmd)) == null || (metadata = NecesseCommandMetadata.fromNecesseCommand(modCmd, category)) == null) continue;
                NecesseCommandRegistry.registerCommand(metadata);
                ++parsed;
            }
            initialized = true;
            ModLogger.info("Registered %d/%d Necesse commands for UI wrapper across %d categories", parsed, scanned, commandsByCategory.size());
        }
        catch (Exception e) {
            ModLogger.error("Failed to initialize NecesseCommandRegistry: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    private static void registerCommand(NecesseCommandMetadata metadata) {
        commands.put(metadata.getId(), metadata);
        commandsByCategory.computeIfAbsent(metadata.getCategory(), k -> new ArrayList()).add(metadata);
    }

    private static CommandCategory categorizeCommand(ModularChatCommand cmd) {
        String name = cmd.name.toLowerCase();
        if (name.equals("give") || name.equals("buff") || name.equals("clearbuff") || name.equals("health") || name.equals("maxhealth") || name.equals("mana") || name.equals("maxmana") || name.equals("heal") || name.equals("levels") || name.equals("hunger") || name.equals("invincibility") || name.equals("rain") || name.equals("time") || name.equals("difficulty") || name.equals("creativemode") || name.equals("hp") || name.equals("maxhp") || name.equals("setlevel") || name.equals("die") || name.equals("healmobs")) {
            return null;
        }
        if (name.equals("clearmobs") || name.equals("cleardrops") || name.equals("mow")) {
            return null;
        }
        if (name.equals("setposition") || name.equals("teleport")) {
            return CommandCategory.TELEPORT;
        }
        if (name.equals("clearall") || name.equals("copyinventory") || name.equals("copyitem") || name.equals("upgradeitem")) {
            return CommandCategory.INVENTORY;
        }
        if (name.equals("armorset") || name.equals("enchant")) {
            return null;
        }
        if (name.equals("regen") || name.equals("staticdamage") || name.equals("resilience") || name.equals("resethealthupgrades")) {
            return CommandCategory.PLAYER_STATS;
        }
        if (name.equals("allowcheats") || name.equals("deathpenalty") || name.equals("setdimension") || name.equals("setisland")) {
            return CommandCategory.WORLD;
        }
        if (name.equals("say") || name.equals("me") || name.equals("whisper") || name.equals("w") || name.equals("pm") || name.equals("createteam") || name.equals("inviteteam") || name.equals("leaveteam") || name.equals("setteam") || name.equals("setteamowner") || name.equals("clearteam") || name.equals("getteam")) {
            return CommandCategory.TEAMS;
        }
        if (name.equals("save") || name.equals("stop") || name.equals("kick") || name.equals("ban") || name.equals("unban") || name.equals("bans") || name.equals("password") || name.equals("motd") || name.equals("permissions") || name.equals("pausewhenempty") || name.equals("maxlatency") || name.equals("settings") || name.equals("changename") || name.equals("deleteplayerdata") || name.equals("settlements") || name.equals("spawnvisitor") || name.equals("spawnsettler") || name.equals("setlanguage")) {
            return CommandCategory.SERVER_ADMIN;
        }
        if (name.equals("startraid") || name.equals("endraid") || name.equals("completeincursion") || name.equals("raids") || name.equals("setraiddiff") || name.equals("setraidtier") || name.equals("startraidattack")) {
            return CommandCategory.RAIDS;
        }
        if (name.equals("clearevents") || name.equals("clearconnection")) {
            return CommandCategory.WORLD_EDITING;
        }
        if (name.equals("cleararea")) {
            return null;
        }
        if (name.equals("help") || name.equals("stophelp") || name.equals("whisperhelp") || name.equals("print") || name.equals("performance") || name.equals("network") || name.equals("playernames") || name.equals("players") || name.equals("playtime") || name.equals("myperms") || name.equals("itemgnd")) {
            return null;
        }
        return CommandCategory.OTHER;
    }

    public static NecesseCommandMetadata getCommand(String id) {
        return commands.get(id);
    }

    public static Collection<NecesseCommandMetadata> getAllCommands() {
        return commands.values();
    }

    public static List<NecesseCommandMetadata> getCommandsByCategory(CommandCategory category) {
        return commandsByCategory.getOrDefault((Object)category, Collections.emptyList());
    }

    public static List<NecesseCommandMetadata> getCommandsByPermission(PermissionLevel minLevel) {
        ArrayList<NecesseCommandMetadata> result = new ArrayList<NecesseCommandMetadata>();
        for (NecesseCommandMetadata cmd : commands.values()) {
            if (cmd.getPermission().ordinal() < minLevel.ordinal()) continue;
            result.add(cmd);
        }
        return result;
    }

    public static Set<CommandCategory> getAvailableCategories() {
        return commandsByCategory.keySet();
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static Map<String, Integer> getStatistics() {
        HashMap<String, Integer> stats = new HashMap<String, Integer>();
        stats.put("total_commands", commands.size());
        stats.put("total_categories", commandsByCategory.size());
        for (Map.Entry<CommandCategory, List<NecesseCommandMetadata>> entry : commandsByCategory.entrySet()) {
            stats.put("category_" + entry.getKey().name(), entry.getValue().size());
        }
        return stats;
    }
}

