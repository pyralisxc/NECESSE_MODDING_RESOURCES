/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.incursionPerkTree.RescueSettlerRewardPerk;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.entity.Entity;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.hostile.bosses.ReturnPortalMob;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootList;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.level.gameObject.FallenAltarObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.levelBuffManager.LevelModifiers;

public class IncursionLevel
extends Level {
    public static LootTable randomTabletLootTable = new LootTable(new LootItemInterface(){

        @Override
        public void addPossibleLoot(LootList list, Object ... extra) {
            list.add("gatewaytablet");
        }

        @Override
        public void addItems(List<InventoryItem> list, GameRandom random, float lootMultiplier, Object ... extra) {
            Mob mob = LootTable.expectExtra(Mob.class, extra, 0);
            if (mob == null) {
                return;
            }
            Level level = mob.getLevel();
            if (!(level instanceof IncursionLevel)) {
                return;
            }
            IncursionLevel incursionLevel = (IncursionLevel)level;
            IncursionData incursionData = incursionLevel.incursionData;
            if (incursionData == null) {
                return;
            }
            IncursionLevelEvent event = incursionLevel.getIncursionLevelEvent();
            if (event == null) {
                return;
            }
            float chance = event.getRandomTabletDropChance(mob);
            if (chance <= 0.0f) {
                return;
            }
            LootTable.runChance(random, chance, lootMultiplier, remainingLootMultiplier -> {
                int tabletsDropping = 1;
                if (incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.CHANCE_TO_DROP_DOUBLE_TABLETS.getID()) && random.getChance(0.35f)) {
                    tabletsDropping = 2;
                }
                for (int i = 0; i < tabletsDropping; ++i) {
                    int tier;
                    InventoryItem gatewayTablet = new InventoryItem("gatewaytablet");
                    for (tier = incursionData.getTabletTier(); random.getChance(0.5f) && tier > 1; --tier) {
                    }
                    GatewayTabletItem.initializeGatewayTablet(gatewayTablet, random, tier, incursionData);
                    list.add(gatewayTablet);
                }
            });
        }
    });
    public IncursionData incursionData;
    public int incursionEventUniqueID;
    public LevelIdentifier altarLevelIdentifier;
    public int altarTileX = Integer.MIN_VALUE;
    public int altarTileY = Integer.MIN_VALUE;
    public int returnPortalUniqueID;
    public Point returnPortalPosition;

    public IncursionLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public IncursionLevel(LevelIdentifier identifier, int width, int height, IncursionData incursion, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
        this.incursionData = incursion;
        this.keepTrackOfReturnedItems = true;
        this.buffManager.forceUpdateBuffs();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("incursionEventUniqueID", this.incursionEventUniqueID);
        if (this.incursionData != null) {
            SaveData incursionSaveData = new SaveData("incursionData");
            this.incursionData.addSaveData(incursionSaveData);
            save.addSaveData(incursionSaveData);
        }
        if (this.altarLevelIdentifier != null) {
            save.addUnsafeString("altarLevel", this.altarLevelIdentifier.stringID);
        }
        if (this.altarTileX != Integer.MIN_VALUE) {
            save.addInt("altarTileX", this.altarTileX);
        }
        if (this.altarTileY != Integer.MIN_VALUE) {
            save.addInt("altarTileY", this.altarTileY);
        }
        save.addInt("returnPortalUniqueID", this.returnPortalUniqueID);
        if (this.returnPortalPosition != null) {
            save.addPoint("returnPortalPosition", this.returnPortalPosition);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.incursionEventUniqueID = save.getInt("incursionEventUniqueID", this.incursionEventUniqueID, false);
        LoadData incursionSaveData = save.getFirstLoadDataByName("incursionData");
        if (incursionSaveData != null) {
            try {
                this.incursionData = IncursionData.fromLoadData(incursionSaveData);
            }
            catch (Exception e) {
                System.err.println("Could not load incursion data from level " + this.getIdentifier());
                e.printStackTrace();
            }
        } else {
            this.incursionData = null;
        }
        this.buffManager.forceUpdateBuffs();
        String altarLevelIdentifierSave = save.getUnsafeString("altarLevel", null, false);
        if (altarLevelIdentifierSave != null) {
            this.altarLevelIdentifier = new LevelIdentifier(altarLevelIdentifierSave);
        }
        if (save.getInt("altarTileX", this.altarTileX) != Integer.MIN_VALUE) {
            this.altarTileX = save.getInt("altarTileX", this.altarTileX);
        }
        if (save.getInt("altarTileY", this.altarTileY) != Integer.MIN_VALUE) {
            this.altarTileY = save.getInt("altarTileY", this.altarTileY);
        }
        this.returnPortalUniqueID = save.getInt("returnPortalUniqueID", this.returnPortalUniqueID, false);
        this.returnPortalPosition = save.getPoint("returnPortalPosition", this.returnPortalPosition, false);
    }

    @Override
    public void writeLevelDataPacket(PacketWriter writer) {
        super.writeLevelDataPacket(writer);
        writer.putNextInt(this.incursionEventUniqueID);
        if (this.incursionData != null) {
            writer.putNextBoolean(true);
            IncursionData.writePacket(this.incursionData, writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void readLevelDataPacket(PacketReader reader) {
        super.readLevelDataPacket(reader);
        this.incursionEventUniqueID = reader.getNextInt();
        this.incursionData = reader.getNextBoolean() ? IncursionData.fromPacket(reader) : null;
    }

    public void spawnReturnPortal(float levelX, float levelY) {
        Mob mob = MobRegistry.getMob("returnportal", (Level)this);
        this.entityManager.addMob(mob, levelX, levelY);
        this.returnPortalPosition = mob.getPositionPoint();
        this.returnPortalUniqueID = mob.getUniqueID();
    }

    public Point getReturnPortalPosition() {
        Mob mob;
        Point returnPortalPosition = this.returnPortalPosition;
        if (returnPortalPosition == null && this.returnPortalUniqueID != 0 && (mob = this.entityManager.mobs.get(this.returnPortalUniqueID, false)) != null) {
            this.returnPortalPosition = returnPortalPosition = mob.getPositionPoint();
        }
        if (returnPortalPosition == null && (returnPortalPosition = (Point)this.entityManager.mobs.stream().filter(m -> m instanceof ReturnPortalMob).map(Entity::getPositionPoint).findFirst().orElse(null)) != null) {
            this.returnPortalPosition = returnPortalPosition;
        }
        if (returnPortalPosition == null) {
            returnPortalPosition = new Point(this.tileWidth * 32 / 2, this.tileHeight * 32 / 2);
        }
        return returnPortalPosition;
    }

    public ReturnPortalMob getReturnPortal() {
        if (this.returnPortalUniqueID == 0) {
            return null;
        }
        Mob mob = this.entityManager.mobs.get(this.returnPortalUniqueID, false);
        if (mob instanceof ReturnPortalMob) {
            return (ReturnPortalMob)mob;
        }
        return null;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultLevelModifiers() {
        Stream<ModifierValue<Boolean>> bannerDisabledStream = Stream.of(new ModifierValue<Boolean>(LevelModifiers.BANNER_OF_WAR_DISABLED, true));
        if (this.incursionData == null) {
            return Stream.concat(super.getDefaultLevelModifiers(), bannerDisabledStream);
        }
        return GameUtils.streamConcat(super.getDefaultLevelModifiers(), this.incursionData.getDefaultLevelModifiers(), bannerDisabledStream);
    }

    @Override
    public Stream<ModifierValue<?>> getMobModifiers(Mob mob) {
        Stream<ModifierValue<?>> out = super.getMobModifiers(mob);
        if (this.incursionData != null) {
            out = Stream.concat(out, this.incursionData.getMobModifiers(mob));
        }
        return out;
    }

    @Override
    public LootTable getExtraMobDrops(Mob mob) {
        if (this.incursionData == null) {
            return super.getExtraMobDrops(mob);
        }
        LootTable lootTable = new LootTable(super.getExtraMobDrops(mob), this.incursionData.getExtraMobDrops(mob));
        if (mob.isHostile && !mob.isSummoned) {
            lootTable = new LootTable(lootTable, new LootItemMultiplierIgnored(randomTabletLootTable));
        }
        return lootTable;
    }

    @Override
    public LootTable getExtraPrivateMobDrops(Mob mob, ServerClient client) {
        if (this.incursionData == null) {
            return super.getExtraPrivateMobDrops(mob, client);
        }
        return new LootTable(super.getExtraPrivateMobDrops(mob, client), this.incursionData.getExtraPrivateMobDrops(mob, client));
    }

    @Override
    public GameMessage getSetSpawnError(int x, int y, ServerClient client) {
        return new LocalMessage("misc", "spawnincursion");
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.incursionCrate;
    }

    public IncursionLevelEvent getIncursionLevelEvent() {
        LevelEvent event = this.entityManager.events.get(this.incursionEventUniqueID, false);
        if (event instanceof IncursionLevelEvent) {
            return (IncursionLevelEvent)event;
        }
        return null;
    }

    public GameMessage canSummonBoss(String bossStringID) {
        IncursionLevelEvent event = this.getIncursionLevelEvent();
        if (event != null) {
            return event.canSpawnBoss(bossStringID);
        }
        return new LocalMessage("misc", "cannotsummonhere");
    }

    public void onBossSummoned(Mob mob) {
        IncursionLevelEvent event = this.getIncursionLevelEvent();
        if (event != null) {
            event.onBossSummoned(mob);
        }
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        if (this.incursionData != null) {
            return this.incursionData.getDisplayName();
        }
        return super.getLocationMessage(tileX, tileY);
    }

    public void returnToAltar(ServerClient client) {
        if (this.altarLevelIdentifier == null || this.altarTileX == Integer.MIN_VALUE || this.altarTileY == Integer.MIN_VALUE) {
            this.altarLevelIdentifier = null;
            client.validateSpawnPoint(false);
            TeleportEvent e = new TeleportEvent(client, 0, client.spawnLevelIdentifier, 3.0f, null, level -> {
                Point spawnPos;
                if (!client.isDefaultSpawnPoint()) {
                    Point offset = RespawnObject.calculateSpawnOffset(level, client.spawnTile.x, client.spawnTile.y, client);
                    spawnPos = new Point(client.spawnTile.x * 32 + offset.x, client.spawnTile.y * 32 + offset.y);
                } else {
                    spawnPos = client.getPlayerPosFromTile((Level)level, client.spawnTile.x, client.spawnTile.y);
                }
                return new TeleportResult(true, spawnPos);
            });
            client.getLevel().entityManager.events.addHidden(e);
            return;
        }
        TeleportEvent e = new TeleportEvent(client, 0, this.altarLevelIdentifier, 3.0f, null, level -> {
            Point spawnPos;
            level.regionManager.ensureTileIsLoaded(this.altarTileX, this.altarTileY);
            GameObject object = level.getObject(this.altarTileX, this.altarTileY);
            if (object instanceof FallenAltarObject) {
                return new TeleportResult(true, client.getPlayerPosFromTile((Level)level, this.altarTileX, this.altarTileY));
            }
            this.altarLevelIdentifier = null;
            client.validateSpawnPoint(false);
            if (!client.isDefaultSpawnPoint()) {
                Point offset = RespawnObject.calculateSpawnOffset(level, client.spawnTile.x, client.spawnTile.y, client);
                if (offset == null) {
                    offset = new Point(0, 0);
                }
                spawnPos = new Point(client.spawnTile.x * 32 + offset.x, client.spawnTile.y * 32 + offset.y);
            } else {
                spawnPos = client.getPlayerPosFromTile((Level)level, client.spawnTile.x, client.spawnTile.y);
            }
            return new TeleportResult(true, spawnPos);
        });
        client.getLevel().entityManager.events.addHidden(e);
    }

    public FallenAltarObjectEntity getAltarObjectEntity() {
        if (!this.isServer()) {
            return null;
        }
        if (this.altarLevelIdentifier != null) {
            Level altarLevel = this.getServer().world.getLevel(this.altarLevelIdentifier);
            altarLevel.regionManager.ensureTileIsLoaded(this.altarTileX, this.altarTileY);
            ObjectEntity objectEntity = altarLevel.entityManager.getObjectEntity(this.altarTileX, this.altarTileY);
            if (objectEntity instanceof FallenAltarObjectEntity) {
                return (FallenAltarObjectEntity)objectEntity;
            }
        }
        return null;
    }

    public void markCanComplete(Supplier<Point> sharedLootDropPositionGetter, Supplier<Stream<ServerClient>> clientsStreamGetter, Function<ServerClient, Point> privateLootDropPositionGetter) {
        FallenAltarObjectEntity altarObjectEntity = this.getAltarObjectEntity();
        if (altarObjectEntity != null) {
            altarObjectEntity.markCanComplete(this);
        }
        if (this.incursionData == null) {
            return;
        }
        int lootDuplicationCounter = 1;
        if (this.incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.INCREASED_BOSS_LOOT.getID())) {
            lootDuplicationCounter = 2;
        }
        for (int i = 0; i < lootDuplicationCounter; ++i) {
            Point dropLocation = sharedLootDropPositionGetter.get();
            if (dropLocation == null) {
                dropLocation = new Point(this.tileWidth > 0 ? GameMath.getLevelCoordinate(this.tileWidth) / 2 + 16 : 0, this.tileHeight > 0 ? GameMath.getLevelCoordinate(this.tileHeight) / 2 + 16 : 0);
            }
            for (InventoryItem inventoryItem : this.incursionData.getPlayerSharedIncursionCompleteRewards().getRewards(false)) {
                int upToAmount = inventoryItem.getGndData().getInt("incursionUpToAmount");
                int count = 1;
                if (upToAmount > 1) {
                    count = GameRandom.globalRandom.getIntBetween(1, upToAmount);
                    inventoryItem.getGndData().clearItem("incursionUpToAmount");
                }
                for (int j = 0; j < count; ++j) {
                    String settlerReward;
                    if (count > 1) {
                        inventoryItem = inventoryItem.copy();
                    }
                    if ((settlerReward = inventoryItem.getGndData().getString("settlerIncursionReward")) != null && !settlerReward.isEmpty()) {
                        RescueSettlerRewardPerk.IncursionMobReward reward = RescueSettlerRewardPerk.settlers.get(settlerReward);
                        if (reward != null) {
                            reward.spawnMob(this, dropLocation.x, dropLocation.y);
                            if (!this.incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.RESCUE_MORE_SETTLERS_REWARD.getID())) continue;
                            float chancePerSettler = 0.4f;
                            int maxAdditionalSettlers = 2;
                            for (int k = 0; k < maxAdditionalSettlers && GameRandom.globalRandom.getChance(chancePerSettler); ++k) {
                                ArrayList<String> keys = new ArrayList<String>(RescueSettlerRewardPerk.settlers.keySet());
                                String key = GameRandom.globalRandom.getOneOf(keys);
                                RescueSettlerRewardPerk.settlers.get(key).spawnMob(this, dropLocation.x, dropLocation.y);
                            }
                            continue;
                        }
                        GameLog.warn.println("Detected invalid settler reward stringID: " + settlerReward);
                        continue;
                    }
                    ItemPickupEntity pickupEntity = inventoryItem.getPickupEntity(this, dropLocation.x, dropLocation.y);
                    pickupEntity.showsLightBeam = true;
                    this.entityManager.pickups.add(pickupEntity);
                }
            }
        }
        Stream<ServerClient> clients = null;
        if (clientsStreamGetter != null) {
            clients = clientsStreamGetter.get();
        }
        Stream<ServerClient> allClients = GameUtils.streamServerClients(this);
        clients = clients == null ? allClients : Stream.concat(clients, allClients);
        clients.distinct().forEach(c -> {
            this.incursionData.onCompleted((ServerClient)c);
            if (c.achievementsLoaded()) {
                c.achievements().COMPLETE_INCURSION.markCompleted((ServerClient)c);
            }
            for (InventoryItem inventoryItem : this.incursionData.getPlayerPersonalIncursionCompleteRewards().getRewards(false)) {
                Point dropLocation = (Point)privateLootDropPositionGetter.apply((ServerClient)c);
                if (dropLocation == null) {
                    dropLocation = c.playerMob.getPositionPoint();
                }
                ItemPickupEntity pickupEntity = inventoryItem.getPickupEntity(this, dropLocation.x, dropLocation.y);
                pickupEntity.setReservedAuth(c.authentication);
                pickupEntity.showsLightBeam = true;
                this.entityManager.pickups.add(pickupEntity);
            }
        });
    }

    public void generatePresetsBasedOnPerks(AltarData altarData, PresetGeneration presetGeneration, GameRandom random) {
        for (Integer obtainedPerkID : altarData.obtainedPerkIDs) {
            IncursionPerksRegistry.getPerk(obtainedPerkID).onIncursionStructuresGenerated(presetGeneration, random, null);
        }
    }

    public void generatePresetsBasedOnPerks(AltarData altarData, PresetGeneration presetGeneration, GameRandom random, Biome biome) {
        for (Integer obtainedPerkID : altarData.obtainedPerkIDs) {
            IncursionPerksRegistry.getPerk(obtainedPerkID).onIncursionStructuresGenerated(presetGeneration, random, biome);
        }
    }

    public void generateUpgradeAndAlchemyVeinsBasedOnPerks(AltarData altarData, CaveGeneration cg, String upgradeShardID, String alchemyShardID, GameRandom random) {
        for (Integer obtainedPerkID : altarData.obtainedPerkIDs) {
            IncursionPerksRegistry.getPerk(obtainedPerkID).onGenerateUpgradeAndAlchemyVeins(cg, upgradeShardID, alchemyShardID, random);
        }
    }

    @Override
    public void migrateToOldLevel(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier) {
        super.migrateToOldLevel(migrationData, oldLevelIdentifier);
        if (this.altarLevelIdentifier != null) {
            LevelIdentifier oldAltarLevelIdentifier = this.altarLevelIdentifier;
            this.altarLevelIdentifier = migrationData.getNewLevelIdentifier(oldAltarLevelIdentifier);
            Point tileOffset = migrationData.getTilePositionOffset(oldAltarLevelIdentifier);
            if (this.altarTileX != Integer.MIN_VALUE) {
                this.altarTileX += tileOffset.x;
            }
            if (this.altarTileY != Integer.MIN_VALUE) {
                this.altarTileY += tileOffset.y;
            }
        }
    }
}

