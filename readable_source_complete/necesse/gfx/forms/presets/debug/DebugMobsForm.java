/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import necesse.engine.AreaFinder;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.Server;
import necesse.engine.registries.ClassIDData;
import necesse.engine.registries.ClassIDDataContainer;
import necesse.engine.registries.IDData;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.RaiderMobPhase;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.friendly.HoneyBeeMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.mobs.friendly.QueenBeeMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.HunterHumanMob;
import necesse.entity.mobs.friendly.human.humanShop.MinerHumanMob;
import necesse.entity.mobs.hostile.HumanRaiderMob;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormMobList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.forms.presets.debug.tools.MoveMobDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;

public class DebugMobsForm
extends Form {
    public static ArrayList<FormMobList.MobConstructor> extraMobs = new ArrayList();
    public static ArrayList<FormMobList.ExtraConstructors> extraConstructors = new ArrayList();
    public FormMobList mobList;
    public FormTextInput mobFilter;

    public DebugMobsForm(String name, final DebugForm parent) {
        super(name, 240, 440);
        this.addComponent(new FormLabel("Mobs", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.mobList = this.addComponent(new FormMobList(0, 40, this.getWidth(), this.getHeight() - 180){

            @Override
            public void addElements(Consumer<FormMobList.MobConstructor> adder) {
                MobRegistry.getMobs().forEach(t -> adder.accept(new FormMobList.MobConstructor(t.getIDData().getStringID(), (ClassIDDataContainer)t){
                    final /* synthetic */ ClassIDDataContainer val$t;
                    {
                        this.val$t = classIDDataContainer;
                        super(displayName);
                    }

                    @Override
                    public Mob construct(Level level, int x, int y) {
                        return MobRegistry.getMob(this.val$t.getIDData().getID(), level);
                    }
                }));
                for (FormMobList.MobConstructor extraMob : extraMobs) {
                    adder.accept(extraMob);
                }
                for (FormMobList.ExtraConstructors getter : extraConstructors) {
                    Collection<FormMobList.MobConstructor> constructors = getter.getConstructors();
                    if (constructors == null) continue;
                    for (FormMobList.MobConstructor extraMob : constructors) {
                        adder.accept(extraMob);
                    }
                }
            }

            @Override
            public void onClicked(final FormMobList.MobConstructor constructor) {
                MouseDebugGameTool tool = new MouseDebugGameTool(parent, null){

                    @Override
                    public void init() {
                        this.onLeftClick(e -> {
                            constructor.spawn(this.parent, this.getLevel(), this.getMouseX(), this.getMouseY());
                            return true;
                        }, "Spawn " + constructor.displayName);
                        this.onRightClick(e -> {
                            int mouseX = this.getMouseX();
                            int mouseY = this.getMouseY();
                            for (Mob mob : this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(this.getMouseTileX(), this.getMouseTileY(), 1)) {
                                if (!mob.getSelectBox().contains(mouseX, mouseY)) continue;
                                this.parent.client.network.sendPacket(new PacketRemoveMob(mob.getUniqueID()));
                                break;
                            }
                            return true;
                        }, "Remove mob");
                    }
                };
                GameToolManager.clearGameTools(parent);
                GameToolManager.setGameTool(tool, parent);
            }
        });
        this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, this.getHeight() - 138));
        this.mobFilter = this.addComponent(new FormTextInput(0, this.getHeight() - 120, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
        this.mobFilter.placeHolder = new StaticMessage("Search filter");
        this.mobFilter.rightClickToClear = true;
        this.mobFilter.onChange(e -> this.mobList.setFilter(((FormTextInput)e.from).getText()));
        this.addComponent(new FormTextButton("Move mob tool", 0, this.getHeight() - 80, this.getWidth())).onClicked(e -> {
            MoveMobDebugGameTool tool = new MoveMobDebugGameTool(parent);
            GameToolManager.clearGameTools(parent);
            GameToolManager.setGameTool(tool, parent);
        });
        this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth())).onClicked(e -> {
            this.mobFilter.setTyping(false);
            parent.makeCurrent(parent.mainMenu);
        });
    }

    static {
        extraConstructors.add(() -> {
            ArrayList<FormMobList.MobConstructor> out = new ArrayList<FormMobList.MobConstructor>();
            for (ClassIDDataContainer<Mob> mob : MobRegistry.getMobs()) {
                IDData data = mob.getIDData();
                if (!HusbandryMob.class.isAssignableFrom(((ClassIDData)data).aClass)) continue;
                out.add(new FormMobList.MobConstructor(data.getStringID() + " tamed", (ClassIDData)data){
                    final /* synthetic */ ClassIDData val$data;
                    {
                        this.val$data = classIDData;
                        super(displayName);
                    }

                    @Override
                    public Mob construct(Level level, int x, int y) {
                        HusbandryMob husbandryMob = (HusbandryMob)MobRegistry.getMob(this.val$data.getStringID(), level);
                        husbandryMob.setTameness(1.0f);
                        return husbandryMob;
                    }
                });
                out.add(new FormMobList.MobConstructor(data.getStringID() + " baby", (ClassIDData)data){
                    final /* synthetic */ ClassIDData val$data;
                    {
                        this.val$data = classIDData;
                        super(displayName);
                    }

                    @Override
                    public Mob construct(Level level, int x, int y) {
                        HusbandryMob husbandryMob = (HusbandryMob)MobRegistry.getMob(this.val$data.getStringID(), level);
                        if (husbandryMob.getLevel() != null) {
                            husbandryMob.startBaby();
                        }
                        return husbandryMob;
                    }
                });
            }
            return out;
        });
        extraMobs.add(new FormMobList.AnimalKeeperWithAnimalsMobConstructor("animalkeeper with sheep", "sheep", 4));
        extraMobs.add(new FormMobList.AnimalKeeperWithAnimalsMobConstructor("animalkeeper with cows", "cow", 4));
        extraMobs.add(new FormMobList.AnimalKeeperWithAnimalsMobConstructor("animalkeeper with pigs", "pig", 4));
        extraMobs.add(new FormMobList.AnimalKeeperWithAnimalsMobConstructor("animalkeeper with chickens", "chicken", 4));
        extraMobs.add(new FormMobList.MobConstructor("losthunter"){

            @Override
            public Mob construct(Level level, int x, int y) {
                HunterHumanMob hunter = (HunterHumanMob)MobRegistry.getMob("hunterhuman", level);
                hunter.setLost(true);
                return hunter;
            }
        });
        extraMobs.add(new FormMobList.MobConstructor("lostminer"){

            @Override
            public Mob construct(Level level, int x, int y) {
                MinerHumanMob miner = (MinerHumanMob)MobRegistry.getMob("minerhuman", level);
                miner.setLost(true);
                return miner;
            }
        });
        extraMobs.add(new FormMobList.MobConstructor("lostexplorer"){

            @Override
            public Mob construct(Level level, int x, int y) {
                ExplorerHumanMob explorer = (ExplorerHumanMob)MobRegistry.getMob("explorerhuman", level);
                explorer.setLost(true);
                return explorer;
            }
        });
        extraConstructors.add(() -> {
            ArrayList<6> out = new ArrayList<6>();
            for (ClassIDDataContainer<Mob> mob : MobRegistry.getMobs()) {
                IDData data = mob.getIDData();
                if (!HumanMob.class.isAssignableFrom(((ClassIDData)data).aClass)) continue;
                out.add(new FormMobList.MobConstructor("trapped" + data.getStringID(), (ClassIDData)data){
                    final /* synthetic */ ClassIDData val$data;
                    {
                        this.val$data = classIDData;
                        super(displayName);
                    }

                    @Override
                    public Mob construct(Level level, int x, int y) {
                        HumanMob humanMob = (HumanMob)MobRegistry.getMob(this.val$data.getStringID(), level);
                        humanMob.setTrapped();
                        return humanMob;
                    }
                });
            }
            return out;
        });
        extraMobs.add(new FormMobList.MobConstructor("humanraideractive"){

            @Override
            public Mob construct(Level level, int x, int y) {
                ArrayList<SettlementRaidLoadout> loadouts;
                Level serverLevel;
                ServerSettlementData settlement;
                Server localServer;
                int tileX = GameMath.getTileCoordinate(x);
                int tileY = GameMath.getTileCoordinate(y);
                if (level != null && level.isClient() && (localServer = level.getClient().getLocalServer()) != null && (settlement = SettlementsWorldData.getSettlementsData(serverLevel = localServer.world.getLevel(level.getIdentifier())).getServerDataAtTile(serverLevel.getIdentifier(), tileX, tileY)) != null && !(loadouts = BasicSettlementRaidLevelEvent.getRaidDebugLoadouts(settlement, "humanraider", 1, false)).isEmpty()) {
                    ItemAttackerRaiderMob mob = GameRandom.globalRandom.getOneOf(loadouts).getNewMob(serverLevel);
                    mob.makeRaider(null, new Point(tileX, tileY), null, 0, 1337, 1.0f);
                    return mob;
                }
                ItemAttackerRaiderMob raider = (ItemAttackerRaiderMob)MobRegistry.getMob("humanraider", level);
                raider.makeRaider(null, new Point(tileX, tileY), null, 0, 1337, 1.0f);
                return raider;
            }
        });
        extraMobs.add(new FormMobList.MobConstructor("humanraiderlooting"){

            @Override
            public Mob construct(Level level, int x, int y) {
                ArrayList<SettlementRaidLoadout> loadouts;
                Level serverLevel;
                ServerSettlementData settlement;
                Server localServer;
                int tileX = GameMath.getTileCoordinate(x);
                int tileY = GameMath.getTileCoordinate(y);
                if (level != null && level.isClient() && (localServer = level.getClient().getLocalServer()) != null && (settlement = SettlementsWorldData.getSettlementsData(serverLevel = localServer.world.getLevel(level.getIdentifier())).getServerDataAtTile(serverLevel.getIdentifier(), tileX, tileY)) != null && !(loadouts = BasicSettlementRaidLevelEvent.getRaidDebugLoadouts(settlement, "humanraider", 1, false)).isEmpty()) {
                    ItemAttackerRaiderMob mob = GameRandom.globalRandom.getOneOf(loadouts).getNewMob(serverLevel);
                    mob.makeRaider(null, new Point(tileX, tileY), null, 0, 1337, 1.0f);
                    mob.setRaiderPhase(RaiderMobPhase.LOOTING);
                    return mob;
                }
                HumanRaiderMob raider = (HumanRaiderMob)MobRegistry.getMob("humanraider", level);
                raider.makeRaider(null, new Point(tileX, tileY), null, 0, 1337, 1.0f);
                return raider;
            }
        });
        extraMobs.add(new FormMobList.MobConstructor("frozenenemies"){

            @Override
            public void spawn(DebugForm parent, Level level, int x, int y) {
                ArrayList<Mob> mobs = SnowBiome.getSpawnedFrozenMobs(level, null, GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y), 0.3f, mob -> {
                    mob.spawnLightThreshold = new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD, 0).min(150);
                    return true;
                });
                while (mobs.size() > 15) {
                    mobs.remove(GameRandom.globalRandom.nextInt(mobs.size()));
                }
                for (Mob mob2 : mobs) {
                    parent.client.network.sendPacket(new PacketSpawnMob(mob2));
                }
            }

            @Override
            public Mob construct(Level level, int x, int y) {
                return MobRegistry.getMob("frostsentry", level);
            }
        });
        extraMobs.add(new FormMobList.MobConstructor("queenbeeswarm"){

            @Override
            public Mob construct(Level level, int x, int y) {
                return MobRegistry.getMob("queenbee", level);
            }

            @Override
            public void spawn(DebugForm parent, final Level level, int x, int y) {
                QueenBeeMob queenBee = (QueenBeeMob)this.construct(level, x, y);
                queenBee.resetUniqueID();
                queenBee.onSpawned(x, y);
                AreaFinder finder = new AreaFinder(queenBee, 30){

                    @Override
                    public boolean checkPoint(int x, int y) {
                        AbstractBeeHiveObjectEntity hiveEntity = level.entityManager.getObjectEntity(x, y, AbstractBeeHiveObjectEntity.class);
                        return hiveEntity != null && hiveEntity.canTakeMigratingQueen();
                    }
                };
                finder.runFinder();
                Point apiary = finder.getFirstFind();
                if (apiary != null) {
                    queenBee.setMigrationApiary(apiary.x, apiary.y);
                }
                parent.client.network.sendPacket(new PacketSpawnMob(queenBee));
                for (int i = 0; i < 10; ++i) {
                    HoneyBeeMob honeyBee = (HoneyBeeMob)MobRegistry.getMob("honeybee", level);
                    honeyBee.followingQueen.uniqueID = queenBee.getUniqueID();
                    honeyBee.resetUniqueID();
                    honeyBee.onSpawned(x + GameRandom.globalRandom.getIntOffset(0, 10), y + GameRandom.globalRandom.getIntOffset(0, 10));
                    parent.client.network.sendPacket(new PacketSpawnMob(honeyBee));
                }
            }
        });
    }
}

