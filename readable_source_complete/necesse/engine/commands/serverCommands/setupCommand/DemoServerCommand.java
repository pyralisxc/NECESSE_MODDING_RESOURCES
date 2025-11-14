/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands.setupCommand;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;
import necesse.engine.GameTileRange;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.commands.serverCommands.setupCommand.AsbjCommandBuild;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuild;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuildConcat;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuilds;
import necesse.engine.commands.serverCommands.setupCommand.CharacterBuildsParameterHandler;
import necesse.engine.commands.serverCommands.setupCommand.FindAndBuildArenaCustom;
import necesse.engine.commands.serverCommands.setupCommand.FindArenaWorldSetup;
import necesse.engine.commands.serverCommands.setupCommand.FindBiomeAndBuildArena;
import necesse.engine.commands.serverCommands.setupCommand.RunCommandBuild;
import necesse.engine.commands.serverCommands.setupCommand.SimpleArmorSetBuild;
import necesse.engine.commands.serverCommands.setupCommand.SimpleItemBuild;
import necesse.engine.commands.serverCommands.setupCommand.SimpleToolSetBuild;
import necesse.engine.commands.serverCommands.setupCommand.SimpleTrinketSetBuild;
import necesse.engine.commands.serverCommands.setupCommand.SummonBossBuild;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetup;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetupEntry;
import necesse.engine.commands.serverCommands.setupCommand.WorldSetupParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.level.maps.Level;
import necesse.level.maps.presets.AncientVultureArenaPreset;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class DemoServerCommand
extends ModularChatCommand {
    public static HashMap<String, WorldSetup> setups = new HashMap();
    public static HashMap<String, CharacterBuild> builds;
    public static ArrayList<String> combatPotions;
    public static Function<ServerClient, InventoryItem> combatPouchItemConstructor;

    public static CharacterBuild extraPotions(final String ... potionStringIDs) {
        return new CharacterBuild(Integer.MAX_VALUE){

            @Override
            public void apply(ServerClient client) {
                for (String stringID : potionStringIDs) {
                    InventoryItem potionItem = new InventoryItem(stringID);
                    potionItem.setAmount(potionItem.itemStackSize());
                    client.playerMob.getInv().addItem(potionItem, true, "itempickup", null);
                }
            }
        };
    }

    public DemoServerCommand() {
        super("demo", "Setups up a world and/or build for player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("setup", new WorldSetupParameterHandler(), true, new CmdParameter("forceNew", new BoolParameterHandler(), true, new CmdParameter[0])), new CmdParameter("builds", new CharacterBuildsParameterHandler(), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        WorldSetupEntry setupEntry = (WorldSetupEntry)args[1];
        boolean forceNew = (Boolean)args[2];
        CharacterBuilds buildEntries = (CharacterBuilds)args[3];
        if (target == null) {
            logs.add("Missing player");
            return;
        }
        if (setupEntry != null) {
            setupEntry.setup.apply(server, target, forceNew, logs);
        }
        if (buildEntries.builds.length > 0) {
            buildEntries.apply(target);
            logs.add("Applied " + GameUtils.join(buildEntries.builds, c -> c.name, ", ", " and "));
        }
        if (setupEntry == null && buildEntries.builds.length <= 0) {
            if (errors[3] != null) {
                logs.add(errors[3]);
            } else {
                logs.add("Missing either setup or build");
            }
        }
    }

    static {
        setups.put("evilsprotector", new FindBiomeAndBuildArena(10, 0, "plains", "forest"));
        setups.put("queenspider", new FindBiomeAndBuildArena(14, -1, "snow", "snowvillage"));
        setups.put("voidwizard", new FindArenaWorldSetup(-100, new String[]{BiomeRegistry.DUNGEON.getStringID()}){

            @Override
            public Point findArenaSpawnTile(Server server, ServerClient client, Level level) {
                int entranceID = ObjectRegistry.getObjectID("dungeonentrance");
                for (int x = 0; x < level.tileWidth; ++x) {
                    for (int y = 0; y < level.tileHeight; ++y) {
                        if (level.getObjectID(x, y) != entranceID) continue;
                        return new Point(x, y);
                    }
                }
                return null;
            }
        });
        setups.put("voidwizardarena", new FindArenaWorldSetup(-101, new String[]{BiomeRegistry.DUNGEON.getStringID()}){

            @Override
            public Point findArenaSpawnTile(Server server, ServerClient client, Level level) {
                return new Point(level.tileWidth / 2, level.tileHeight / 2);
            }
        });
        setups.put("swampguardian", new FindBiomeAndBuildArena(14, -1, "swamp"));
        setups.put("ancientvulture", new FindAndBuildArenaCustom(36, -1, new String[]{"desert"}){

            @Override
            public void buildArena(Server server, ServerClient client, Level level, int tileX, int tileY, int size) {
                new AncientVultureArenaPreset(size, GameRandom.globalRandom).applyToLevelCentered(level, tileX, tileY - 1);
                WorldSetup.clearBreakableObjects(level, tileX, tileY, size / 2);
                WorldSetup.placeTorches(level, GameRandom.globalRandom, tileX, tileY - 1, size / 2);
            }
        });
        setups.put("reaper", new FindBiomeAndBuildArena(14, -2, "plains", "forest"));
        setups.put("cryoqueen", new FindBiomeAndBuildArena(16, -2, "snow", "snowvillage"));
        setups.put("pestwarden", new FindBiomeAndBuildArena(18, -2, "swamp"));
        setups.put("fallenwizard", new FindArenaWorldSetup(-200, new String[]{"desert", "desertvillage"}){

            @Override
            public Point findArenaSpawnTile(Server server, ServerClient client, Level level) {
                int entranceID = ObjectRegistry.getObjectID("templeentrance");
                for (int x = 0; x < level.tileWidth; ++x) {
                    for (int y = 0; y < level.tileHeight; ++y) {
                        if (level.getObjectID(x, y) != entranceID) continue;
                        return new Point(x, y);
                    }
                }
                return null;
            }
        });
        setups.put("fallenwizardarena", new FindArenaWorldSetup(-201, new String[]{"desert", "desertvillage"}){

            @Override
            public Point findArenaSpawnTile(Server server, ServerClient client, Level level) {
                return new Point(level.tileWidth / 2, level.tileHeight / 2 + 10);
            }
        });
        setups.put("arena10", (server, client, forceNew, logs) -> {
            WorldSetup.buildRandomArena(client.getLevel(), GameRandom.globalRandom, client.playerMob.getTileX(), client.playerMob.getTileY(), 5, 15);
            WorldSetup.updateClientsLevel(client.getLevel(), client.playerMob.getTileX(), client.playerMob.getTileY(), 30);
        });
        setups.put("arena15", (server, client, forceNew, logs) -> {
            WorldSetup.buildRandomArena(client.getLevel(), GameRandom.globalRandom, client.playerMob.getTileX(), client.playerMob.getTileY(), 10, 20);
            WorldSetup.updateClientsLevel(client.getLevel(), client.playerMob.getTileX(), client.playerMob.getTileY(), 35);
        });
        setups.put("arena20", (server, client, forceNew, logs) -> {
            WorldSetup.buildRandomArena(client.getLevel(), GameRandom.globalRandom, client.playerMob.getTileX(), client.playerMob.getTileY(), 15, 25);
            WorldSetup.updateClientsLevel(client.getLevel(), client.playerMob.getTileX(), client.playerMob.getTileY(), 40);
        });
        builds = new HashMap();
        combatPotions = new ArrayList<String>(Arrays.asList("speedpotion", "healthregenpotion", "attackspeedpotion", "battlepotion", "resistancepotion", "thornspotion", "accuracypotion", "rapidpotion", "knockbackpotion"));
        combatPouchItemConstructor = c -> {
            InventoryItem pouchItem = new InventoryItem("potionpouch");
            Inventory inventory = new Inventory(combatPotions.size());
            for (int i = 0; i < combatPotions.size(); ++i) {
                InventoryItem potionItem = new InventoryItem(combatPotions.get(i));
                potionItem.setAmount(potionItem.itemStackSize());
                inventory.setItem(i, potionItem);
            }
            InternalInventoryItemInterface.setInternalInventory(pouchItem, inventory);
            return pouchItem;
        };
        builds.put("clearinv", new CharacterBuild(-1000000){

            @Override
            public void apply(ServerClient client) {
                client.playerMob.getInv().clearInventories();
            }
        });
        builds.put("hp100", new CharacterBuild(){

            @Override
            public void apply(ServerClient client) {
                client.playerMob.setMaxHealth(100);
                client.playerMob.setHealth(client.playerMob.getMaxHealth());
            }
        });
        builds.put("hp200", new CharacterBuild(){

            @Override
            public void apply(ServerClient client) {
                client.playerMob.setMaxHealth(200);
                client.playerMob.setHealth(client.playerMob.getMaxHealth());
            }
        });
        builds.put("hp300", new CharacterBuild(){

            @Override
            public void apply(ServerClient client) {
                client.playerMob.setMaxHealth(300);
                client.playerMob.setHealth(client.playerMob.getMaxHealth());
            }
        });
        builds.put("woodtools", new SimpleToolSetBuild("woodpickaxe", "woodaxe", "woodshovel"));
        builds.put("coppertools", new SimpleToolSetBuild("copperpickaxe", "copperaxe", "coppershovel"));
        builds.put("irontools", new SimpleToolSetBuild("ironpickaxe", "ironaxe", "ironshovel"));
        builds.put("frosttools", new SimpleToolSetBuild("frostpickaxe", "frostaxe", "frostshovel"));
        builds.put("demonictools", new SimpleToolSetBuild("demonicpickaxe", "demonicaxe", "demonicshovel"));
        builds.put("tungstentools", new SimpleToolSetBuild("tungstenpickaxe", "tungstenaxe", "tungstenshovel"));
        builds.put("glacialtools", new SimpleToolSetBuild("glacialpickaxe", "glacialaxe", "glacialshovel"));
        builds.put("myceliumtools", new SimpleToolSetBuild("myceliumpickaxe", "myceliumaxe", "myceliumshovel"));
        builds.put("ancientfossiltools", new SimpleToolSetBuild("ancientfossilpickaxe", "ancientfossilaxe", "ancientfossilshovel"));
        builds.put("leatherarmor", new SimpleArmorSetBuild("leatherhood", "leathershirt", "leatherboots"));
        builds.put("copperarmor", new SimpleArmorSetBuild("copperhelmet", "copperchestplate", "copperboots"));
        builds.put("ironarmor", new SimpleArmorSetBuild("ironhelmet", "ironchestplate", "ironboots"));
        builds.put("spiderarmor", new SimpleArmorSetBuild("spiderhelmet", "spiderchestplate", "spiderboots"));
        builds.put("frostarmor", new SimpleArmorSetBuild("frosthelmet", "frostchestplate", "frostboots"));
        builds.put("demonicarmor", new SimpleArmorSetBuild("demonichelmet", "demonicchestplate", "demonicboots"));
        builds.put("voidmaskarmor", new SimpleArmorSetBuild("voidmask", "voidrobe", "voidboots"));
        builds.put("voidhatarmor", new SimpleArmorSetBuild("voidhat", "voidrobe", "voidboots"));
        builds.put("ivyhelmetarmor", new SimpleArmorSetBuild("ivyhelmet", "ivychestplate", "ivyboots"));
        builds.put("ivyhoodarmor", new SimpleArmorSetBuild("ivyhood", "ivychestplate", "ivyboots"));
        builds.put("ivycircletarmor", new SimpleArmorSetBuild("ivycirclet", "ivychestplate", "ivyboots"));
        builds.put("quartzhelmetarmor", new SimpleArmorSetBuild("quartzhelmet", "quartzchestplate", "quartzboots"));
        builds.put("quartzcrownarmor", new SimpleArmorSetBuild("quartzcrown", "quartzchestplate", "quartzboots"));
        builds.put("tungstenarmor", new SimpleArmorSetBuild("tungstenhelmet", "tungstenchestplate", "tungstenboots"));
        builds.put("shadowhatarmor", new SimpleArmorSetBuild("shadowhat", "shadowmantle", "shadowboots"));
        builds.put("shadowhoodarmor", new SimpleArmorSetBuild("shadowhood", "shadowmantle", "shadowboots"));
        builds.put("ninjaarmor", new SimpleArmorSetBuild("ninjahood", "ninjarobe", "ninjashoes"));
        builds.put("glacialcircletarmor", new SimpleArmorSetBuild("glacialcirclet", "glacialchestplate", "glacialboots"));
        builds.put("glacialhelmetarmor", new SimpleArmorSetBuild("glacialhelmet", "glacialchestplate", "glacialboots"));
        builds.put("myceliumhoodarmor", new SimpleArmorSetBuild("myceliumhood", "myceliumchestplate", "myceliumboots"));
        builds.put("ancientfossilarmor", new SimpleArmorSetBuild("ancientfossilhelmet", "ancientfossilchestplate", "ancientfossilboots"));
        builds.put("leatherset", new CharacterBuildConcat("woodtools", "leatherarmor"));
        builds.put("copperset", new CharacterBuildConcat("coppertools", "copperarmor"));
        builds.put("ironset", new CharacterBuildConcat("irontools", "ironarmor"));
        builds.put("frostset", new CharacterBuildConcat("frosttools", "frostarmor"));
        builds.put("spiderset", new CharacterBuildConcat("irontools", "spiderarmor"));
        builds.put("demonicset", new CharacterBuildConcat("demonictools", "demonicarmor"));
        builds.put("voidmaskset", new CharacterBuildConcat("demonictools", "voidmaskarmor"));
        builds.put("voidhatset", new CharacterBuildConcat("demonictools", "voidhatarmor"));
        builds.put("ivyhelmetset", new CharacterBuildConcat("demonictools", "ivyhelmetarmor"));
        builds.put("ivyhoodset", new CharacterBuildConcat("demonictools", "ivyhoodarmor"));
        builds.put("ivycircletset", new CharacterBuildConcat("demonictools", "ivycircletarmor"));
        builds.put("quartzhelmetset", new CharacterBuildConcat("demonictools", "quartzhelmetarmor"));
        builds.put("quartzcrownset", new CharacterBuildConcat("demonictools", "quartzcrownarmor"));
        builds.put("tungstenset", new CharacterBuildConcat("tungstentools", "tungstenarmor"));
        builds.put("shadowhatset", new CharacterBuildConcat("tungstentools", "shadowhatarmor"));
        builds.put("shadowhoodset", new CharacterBuildConcat("tungstentools", "shadowhoodarmor"));
        builds.put("ninjaset", new CharacterBuildConcat("glacialtools", "ninjaarmor"));
        builds.put("glacialcircletset", new CharacterBuildConcat("glacialtools", "glacialcircletarmor"));
        builds.put("glacialhelmetset", new CharacterBuildConcat("glacialtools", "glacialhelmetarmor"));
        builds.put("myceliumhoodset", new CharacterBuildConcat("myceliumtools", "myceliumhoodarmor"));
        builds.put("ancientfossilset", new CharacterBuildConcat("ancientfossiltools", "ancientfossilarmor"));
        builds.put("forceofwind", new SimpleItemBuild(m -> new PlayerInventorySlot(m.equipment.getSelectedEquipmentInventory(1), 1), "forceofwind"));
        builds.put("zephyrboots", new SimpleItemBuild(m -> new PlayerInventorySlot(m.equipment.getSelectedEquipmentInventory(1), 1), "zephyrboots"));
        builds.put("blinkscepter", new SimpleItemBuild(m -> new PlayerInventorySlot(m.equipment.getSelectedEquipmentInventory(1), 1), "blinkscepter"));
        builds.put("healthpotions", new SimpleItemBuild(18, "healthpotion", 100));
        builds.put("greaterhealthpotions", new SimpleItemBuild(18, "greaterhealthpotion", 100));
        builds.put("summonevilsprotector", new SummonBossBuild("evilsprotector", 10));
        builds.put("evilsprotectormelee", new CharacterBuildConcat("clearinv", "hp100", "generalset", "healthpotions", "zephyrboots", "ironset", new SimpleItemBuild(28, "mysteriousportal", 5), new SimpleItemBuild(29, "juniorburger", 50), new SimpleTrinketSetBuild("vampiresgift", "fuzzydice", "regenpendant", "leatherglove"), new SimpleItemBuild("ironsword", 1), new SimpleItemBuild("ironspear", 1), new SimpleItemBuild("spiderboomerang", 1), new SimpleItemBuild("heavyhammer", 1)));
        builds.put("evilsprotectorranged", new CharacterBuildConcat("clearinv", "hp100", "generalset", "healthpotions", "zephyrboots", "leatherset", new SimpleItemBuild(28, "mysteriousportal", 5), new SimpleItemBuild(29, "blueberrycake", 50), new SimpleTrinketSetBuild("vampiresgift", "fuzzydice", "regenpendant", "trackerboot"), new SimpleItemBuild("ironbow", 1), new SimpleItemBuild("handgun", 1), new SimpleItemBuild("stonearrow", 1000), new SimpleItemBuild("simplebullet", 1000)));
        builds.put("evilsprotectormagic", new CharacterBuildConcat("clearinv", "hp100", "generalset", "healthpotions", "zephyrboots", "copperset", new SimpleItemBuild(28, "mysteriousportal", 5), new SimpleItemBuild(29, "smokedfillet", 50), new SimpleTrinketSetBuild("vampiresgift", "fuzzydice", "regenpendant", "trackerboot"), new SimpleItemBuild("woodstaff", 1), new SimpleItemBuild("bloodbolt", 1), new SimpleItemBuild("venomstaff", 1)));
        builds.put("summonqueenspider", new SummonBossBuild("queenspider"));
        builds.put("queenspidermelee", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "demonicset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "nachos", 50), new SimpleTrinketSetBuild("meleefoci", "vampiresgift", "regenpendant", "trackerboot"), new SimpleItemBuild("frostsword", 1), new SimpleItemBuild("frostspear", 1), new SimpleItemBuild("frostboomerang", 1), new SimpleItemBuild("heavyhammer", 1), new SimpleItemBuild("frostglaive", 1)));
        builds.put("queenspiderranged", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "frostset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "juniorburger", 50), new SimpleTrinketSetBuild("rangefoci", "vampiresgift", "regenpendant", "leatherglove"), new SimpleItemBuild("demonicbow", 1), new SimpleItemBuild("handgun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("simplebullet", 1000)));
        builds.put("queenspidermagic", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidhatset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "meatballs", 50), new SimpleTrinketSetBuild("magicfoci", "vampiresgift", "regenpendant", "leatherglove"), new SimpleItemBuild("woodstaff", 1), new SimpleItemBuild("bloodvolley", 1), new SimpleItemBuild("venomstaff", 1)));
        builds.put("queenspidersummon", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "spiderset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "candyapple", 50), new SimpleTrinketSetBuild("summonfoci", "vampiresgift", "regenpendant", "trackerboot"), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonvoidwizard", new SummonBossBuild("voidwizard", 5));
        builds.put("voidwizardmelee", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "demonicset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "nachos", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "trackerboot"), new SimpleItemBuild("demonicsword", 1), new SimpleItemBuild("voidspear", 1), new SimpleItemBuild("voidboomerang", 2), new SimpleItemBuild("lightninghammer", 1)));
        builds.put("voidwizardranged", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "frostset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "fishtaco", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "leatherglove"), new SimpleItemBuild("demonicbow", 1), new SimpleItemBuild("webbedgun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("voidwizardmagic", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidhatset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "smokedfillet", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "leatherglove"), new SimpleItemBuild("voidmissile", 1), new SimpleItemBuild("bloodvolley", 1), new SimpleItemBuild("venomstaff", 1)));
        builds.put("voidwizardsummon", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidmaskset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "blueberrycake", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "mesmertablet", "trackerboot"), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonswampguardian", new SummonBossBuild("swampguardian"));
        builds.put("swampguardianmelee", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhelmetset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "freshpotatosalad", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "frozenheart", "trackerboot"), new SimpleItemBuild("ivysword", 1), new SimpleItemBuild("voidspear", 1), new SimpleItemBuild("voidboomerang", 2), new SimpleItemBuild("lightninghammer", 1)));
        builds.put("swampguardianranged", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhoodset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "eggplantparmesan", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "luckycape", "frozenwave"), new SimpleItemBuild("ivybow", 1), new SimpleItemBuild("webbedgun", 1), new SimpleItemBuild("ironarrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("swampguardianmagic", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidhatset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "juniorburger", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "noblehorseshoe", "trackerboot"), new SimpleItemBuild("voidstaff", 1), new SimpleItemBuild("swamptome", 1), new SimpleItemBuild("voidmissile", 1), new SimpleItemBuild("venomstaff", 1)));
        builds.put("swampguardiansummon", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidmaskset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "fishandchips", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "mesmertablet", "regenpendant", "luckycape"), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonancientvulture", new SummonBossBuild("ancientvulture"));
        builds.put("ancientvulturemelee", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhelmetset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "freshpotatosalad", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "frozenheart", "luckycape"), new SimpleItemBuild("ivysword", 1), new SimpleItemBuild("ivyspear", 1), new SimpleItemBuild("quartzglaive", 1), new SimpleItemBuild("razorbladeboomerang", 1), new SimpleItemBuild("voidboomerang", 2)));
        builds.put("ancientvultureranged", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhoodset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "freshpotatosalad", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "noblehorseshoe", "trackerboot"), new SimpleItemBuild("ivybow", 1), new SimpleItemBuild("shotgun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("ancientvulturemagic", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "quartzhelmetset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "ricepudding", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "luckycape", "frozenwave"), new SimpleItemBuild("quartzstaff", 1), new SimpleItemBuild("voidstaff", 1), new SimpleItemBuild("swamptome", 1), new SimpleItemBuild("dredgingstaff", 1)));
        builds.put("ancientvulturesummon", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "quartzcrownset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "fishandchips", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "mesmertablet", "regenpendant", "luckycape"), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonpiratecaptain", new SummonBossBuild("piratecaptain", 8));
        builds.put("piratecaptainmelee", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhelmetset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "frozenheart", "trackerboot"), new SimpleItemBuild("cutlass", 1), new SimpleItemBuild("vulturestalon", 1), new SimpleItemBuild("razorbladeboomerang", 1), new SimpleItemBuild("icejavelin", 250)));
        builds.put("piratecaptainranged", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhoodset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "noblehorseshoe", "frozenwave"), new SimpleItemBuild("vulturesburst", 1), new SimpleItemBuild("machinegun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("piratecaptainmagic", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "quartzhelmetset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "regenpendant", "luckycape", "trackerboot"), new SimpleItemBuild("quartzstaff", 1), new SimpleItemBuild("voidstaff", 1), new SimpleItemBuild("dredgingstaff", 1)));
        builds.put("piratecaptainsummon", new CharacterBuildConcat("clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidmaskset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "mesmertablet", "regenpendant", "luckycape"), new SimpleItemBuild("vulturestaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonreaper", new SummonBossBuild("reaper"));
        builds.put("reapermelee", new CharacterBuildConcat("clearinv", "hp300", "generalset", "healthpotions", DemoServerCommand.extraPotions("strengthpotion"), "zephyrboots", "tungstenset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "bonehilt", "frozensoul", "lifependant"), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("tungstenboomerang", 4), new SimpleItemBuild("icejavelin", 250)));
        builds.put("reaperranged", new CharacterBuildConcat("clearinv", "hp300", "generalset", "healthpotions", DemoServerCommand.extraPotions("rangerpotion"), "zephyrboots", "shadowhoodset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "bonehilt", "noblehorseshoe", "lifependant"), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("flintlock", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("reapermagic", new CharacterBuildConcat("clearinv", "hp300", "generalset", "healthpotions", DemoServerCommand.extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "airvessel", "lifependant", "luckycape", "frozenwave"), new SimpleItemBuild("shadowbolt", 1), new SimpleItemBuild("quartzstaff", 1), new SimpleItemBuild("genielamp", 1), new SimpleItemBuild("elderlywand", 1)));
        builds.put("reapersummon", new CharacterBuildConcat("clearinv", "hp300", "generalset", "healthpotions", DemoServerCommand.extraPotions("minionpotion"), "forceofwind", "voidmaskset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfoci", "vampiresgift", "hysteriatablet", "lifependant", "airvessel"), new SimpleItemBuild("vulturestaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summoncryoqueen", new SummonBossBuild("cryoqueen"));
        builds.put("cryoqueenmelee", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("strengthpotion"), "zephyrboots", "glacialhelmetset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "frozensoul", "lifependant"), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("tungstenboomerang", 4), new SimpleItemBuild("glacialboomerang", 2)));
        builds.put("cryoqueenranged", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("rangerpotion"), "zephyrboots", "shadowhoodset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "noblehorseshoe", "trackerboot"), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("flintlock", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("cryoqueenmagic", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "lifependant", "luckycape", "airvessel"), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("shadowbolt", 1), new SimpleItemBuild("iciclestaff", 1), new SimpleItemBuild("elderlywand", 1)));
        builds.put("cryoqueensummon", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("minionpotion"), "forceofwind", "glacialcircletset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "lifependant", "airvessel"), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonpestwarden", new SummonBossBuild("pestwarden"));
        builds.put("pestwardenmelee", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("strengthpotion"), "zephyrboots", "glacialhelmetset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "frozensoul", "lifependant"), new SimpleItemBuild("cryoglaive", 1), new SimpleItemBuild("reaperscythe", 1), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("tungstenboomerang", 4), new SimpleItemBuild("glacialboomerang", 2)));
        builds.put("pestwardenranged", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("rangerpotion"), "zephyrboots", "myceliumhoodset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "noblehorseshoe", "trackerboot"), new SimpleItemBuild("druidsgreatbow", 1), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("cryoblaster", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("pestwardenmagic", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "lifependant", "luckycape", "airvessel"), new SimpleItemBuild("cryoquake", 1), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("shadowbolt", 1), new SimpleItemBuild("swampdwellerstaff", 1), new SimpleItemBuild("iciclestaff", 1), new SimpleItemBuild("elderlywand", 1)));
        builds.put("pestwardensummon", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("minionpotion"), "forceofwind", "glacialcircletset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "lifependant", "airvessel"), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonsageandgrit", new SummonBossBuild("sageandgrit"));
        builds.put("sagegritmelee", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("strengthpotion"), "zephyrboots", "ancientfossilset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "frozensoul", "lifependant"), new SimpleItemBuild("venomslasher", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("reaperscythe", 1), new SimpleItemBuild("cryoglaive", 1), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("glacialboomerang", 1)));
        builds.put("sagegritranged", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("rangerpotion"), "zephyrboots", "myceliumhoodset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "noblehorseshoe", "lifependant"), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("livingshotty", 1), new SimpleItemBuild("antiquerifle", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("druidsgreatbow", 1), new SimpleItemBuild("cryoblaster", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("sagegritmagic", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "luckycape", "airvessel"), new SimpleItemBuild("cryoquake", 1), new SimpleItemBuild("venomshower", 1), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("ancientdredgingstaff", 1), new SimpleItemBuild("swampdwellerstaff", 1), new SimpleItemBuild("shadowbolt", 1)));
        builds.put("sagegritsummon", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("minionpotion"), "forceofwind", "glacialcircletset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "scryingmirror", "airvessel"), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("summonfallenwizard", new SummonBossBuild("fallenwizard", 5));
        builds.put("fallenwizardmelee", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("strengthpotion"), "blinkscepter", "ancientfossilset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "ancientrelics", "frozensoul", "lifependant"), new SimpleItemBuild("antiquesword", 1), new SimpleItemBuild("dragonsrebound", 1), new SimpleItemBuild("venomslasher", 1), new SimpleItemBuild("reaperscythe", 1), new SimpleItemBuild("cryospear", 1), new SimpleItemBuild("cryoglaive", 1), new SimpleItemBuild("glacialboomerang", 1)));
        builds.put("fallenwizardranged", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("rangerpotion"), "blinkscepter", "shadowhoodset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "luckycape", "ancientrelics"), new SimpleItemBuild("antiquebow", 1), new SimpleItemBuild("antiquerifle", 1), new SimpleItemBuild("livingshotty", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("bowofdualism", 1), new SimpleItemBuild("cryoblaster", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)));
        builds.put("fallenwizardmagic", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("wisdompotion"), "blinkscepter", "shadowhatset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "bonehilt", "luckycape", "ancientrelics"), new SimpleItemBuild("dragonlance", 1), new SimpleItemBuild("cryoquake", 1), new SimpleItemBuild("venomshower", 1), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("ancientdredgingstaff", 1), new SimpleItemBuild("swampdwellerstaff", 1), new SimpleItemBuild("shadowbolt", 1)));
        builds.put("fallenwizardsummon", new CharacterBuildConcat("clearinv", "hp300", "generalset", "greaterhealthpotions", DemoServerCommand.extraPotions("minionpotion"), "blinkscepter", "glacialcircletset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild("balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "scryingmirror", "ancientrelics"), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("skeletonstaff", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)));
        builds.put("combatpouch", new SimpleItemBuild(combatPouchItemConstructor));
        builds.put("generalset", new CharacterBuildConcat(new SimpleItemBuild(19, combatPouchItemConstructor), new SimpleItemBuild(49, "torch", 100), new SimpleItemBuild(39, "recallscroll", 50), new SimpleItemBuild(38, "teleportationscroll", 50)));
        builds.put("timeday", new RunCommandBuild("time day"));
        builds.put("timedawn", new RunCommandBuild("time dawn"));
        builds.put("timemorning", new RunCommandBuild("time morning"));
        builds.put("timenoon", new RunCommandBuild("time noon"));
        builds.put("timemidday", new RunCommandBuild("time midday"));
        builds.put("timedusk", new RunCommandBuild("time dusk"));
        builds.put("timenight", new RunCommandBuild("time night"));
        builds.put("timemidnight", new RunCommandBuild("time midnight"));
        builds.put("rainclear", new RunCommandBuild("rain clear"));
        builds.put("teleportothers", new CharacterBuild(){

            @Override
            public void apply(ServerClient client) {
                ArrayList<Point> validPositions = null;
                for (ServerClient otherClient : client.getServer().getClients()) {
                    if (client == otherClient) continue;
                    if (validPositions == null) {
                        validPositions = new ArrayList<Point>();
                        int radius = 4;
                        for (Point tile : new GameTileRange(radius, new Point[0]).getValidTiles(client.playerMob.getTileX(), client.playerMob.getTileY())) {
                            int levelX = tile.x * 32 + 16;
                            int levelY = tile.y * 32 + 16;
                            if (client.playerMob.collidesWith(client.getLevel(), levelX, levelY)) continue;
                            validPositions.add(new Point(levelX, levelY));
                        }
                    }
                    Point pos = validPositions.isEmpty() ? new Point(client.playerMob.getX(), client.playerMob.getY()) : (Point)validPositions.remove(GameRandom.globalRandom.nextInt(validPositions.size()));
                    if (otherClient.isSamePlace(client)) {
                        RegionPositionGetter previousPosition = otherClient.playerMob.saveRegionPosition();
                        otherClient.playerMob.setPos(pos.x, pos.y, true);
                        client.getServer().network.sendToClientsWithAnyRegion(new PacketPlayerMovement(otherClient, true), otherClient.playerMob.getRegionPositionsCombined(previousPosition));
                        continue;
                    }
                    otherClient.changeIsland(client.getLevelIdentifier(), level -> pos, true);
                }
            }
        });
        builds.put("hp50000", new CharacterBuild(){

            @Override
            public void apply(ServerClient client) {
                client.playerMob.setMaxHealth(50000);
                client.playerMob.setHealth(50000);
            }
        });
        builds.put("nostarve", new CharacterBuild(){

            @Override
            public void apply(ServerClient client) {
                client.playerMob.addHunger(50000.0f);
            }
        });
        builds.put("test", new CharacterBuildConcat(new Object[]{"hp50000", "nostarve", "timeday", "rainclear", new AsbjCommandBuild()}){

            @Override
            public void apply(ServerClient client) {
                super.apply(client);
            }
        });
        builds.put("asbj", new CharacterBuildConcat("test", "clearinv", new SimpleItemBuild(49, "blueberrycake", 99), new AsbjCommandBuild("spikedbatboots", "toolbox", "piratetelescope"), new SimpleItemBuild(10, "coin", 999), new SimpleItemBuild(11, "stonearrow", 999), new SimpleItemBuild(12, "cannonball", 999), new SimpleItemBuild(13, "simplebullet", 999), new SimpleItemBuild(14, "ironbomb", 99)));
    }
}

