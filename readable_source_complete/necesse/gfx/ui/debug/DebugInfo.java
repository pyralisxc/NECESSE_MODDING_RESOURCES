/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui.debug;

import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modifiers.ModifierTooltip;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.server.AdventureParty;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.recipe.Recipes;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class DebugInfo
extends Debug {
    @Override
    protected void drawDebug(Client client) {
        PlayerMob player = client.getPlayer();
        ServerClient serverClient = null;
        Level level = client.getLevel();
        if (level == null) {
            return;
        }
        Level serverLevel = null;
        if (client.getLocalServer() != null) {
            serverLevel = client.getLocalServer().world.getLevel(level.getIdentifier());
            serverClient = client.getLocalServer().getLocalServerClient();
            if (Settings.serverPerspective) {
                level = serverLevel;
            }
        }
        this.drawString("Player tile pos/speed: " + player.getTileX() + ", " + player.getTileY() + " / " + player.getCurrentSpeed());
        this.drawString("Player hp/armor: " + player.getHealth() + " / " + player.getArmor() + " (" + GameDamage.getDamageReduction(player.getArmor()) + ")");
        this.drawString("World Time, time: " + (int)((float)level.getWorldEntity().getWorldTime() / 1000.0f) + " (day " + level.getWorldEntity().getDay() + ", " + level.getWorldEntity().getDayTimeReadable() + ") " + level.getWorldEntity().getDayTimeInt() + "/" + level.getWorldEntity().getDayTimeMax() + ", " + level.getWorldEntity().getTime());
        this.drawString("AmbientLight: " + (Settings.serverPerspective && serverLevel != null ? serverLevel.lightManager.getAmbientLight() : level.lightManager.getAmbientLight()));
        this.drawString("Entities/projectiles: " + level.entityManager.getSize() + " / " + level.entityManager.projectiles.count() + " (" + level.entityManager.projectiles.countCache() + ")");
        this.drawString("Mobs/pickups: " + level.entityManager.mobs.count() + " (" + level.entityManager.mobs.countCache() + ") / " + level.entityManager.pickups.count() + " (" + level.entityManager.pickups.countCache() + ")");
        this.drawString("Particles/trails: " + level.entityManager.particles.count() + ", " + level.entityManager.particleOptions.count() + " / " + level.entityManager.trails.size());
        this.drawString("Chains/pillar: " + level.entityManager.chains.size() + " / " + level.entityManager.pillarHandlers.size());
        this.drawString("DamagedObjects: " + level.entityManager.damagedObjects.count());
        this.drawString("ObjectEntities/LevelEvents: " + level.entityManager.objectEntities.count() + " / " + level.entityManager.events.count());
        this.drawString("Recipes: " + Recipes.getTotalRecipes() + " (Hash: " + Integer.toHexString(Recipes.getHash()) + ")");
        this.drawString("Biome: " + level.baseBiome.getDisplayName() + (level.isIslandPosition() ? " (Size: " + GameMath.toDecimals(WorldGenerator.getIslandSize(level.getIslandX(), level.getIslandY()), 2) + ")" : ""));
        this.drawString("Level: " + level.getClass().getSimpleName() + ", identifier: " + level.getIdentifier() + ", size: " + (level.tileWidth <= 0 ? "INF" : Integer.valueOf(level.tileWidth)) + "x" + (level.tileHeight <= 0 ? "INF" : Integer.valueOf(level.tileHeight)));
        this.skipY(10);
        if (player.getDraggingItem() != null) {
            this.drawString("Dragging item: " + player.getDraggingItem().getItemDisplayName());
            this.drawString("Dragging amount: " + player.getDraggingItem().getAmount());
            this.skipY(10);
        }
        this.drawString("Server perspective: " + Settings.serverPerspective);
        this.skipY(10);
        this.drawString("Client in: " + client.packetManager.getAverageIn() + "/s (" + client.packetManager.getAverageInPackets() + "), Total: " + client.packetManager.getTotalIn() + " (" + client.packetManager.getTotalInPackets() + ")");
        this.drawString("Client out: " + client.packetManager.getAverageOut() + "/s (" + client.packetManager.getAverageOutPackets() + "), Total: " + client.packetManager.getTotalOut() + " (" + client.packetManager.getTotalOutPackets() + ")");
        this.drawString("Lost packets: In: " + client.packetManager.getLostInPackets() + ", Out: " + client.packetManager.getLostOutPackets());
        this.drawString("Slot: " + (client.getSlot() + 1) + "/" + client.getSlots());
        this.drawString("Raining: " + level.weatherLayer.isRaining());
        if (serverLevel != null) {
            this.drawString("Rain timer: " + GameUtils.formatSeconds(serverLevel.weatherLayer.getRemainingRainTime() / 1000L));
        }
        this.drawString("Playing sounds: " + SoundManager.getPlayingSoundsCount());
        this.drawString("Localization listeners: " + Localization.getListenersSize());
        this.drawString("Generated textures: " + GameTexture.getGeneratedTextureCount());
        this.drawString("Performance history: " + client.tickManager().getPerformanceHistorySize());
        this.drawString("Memory max: " + GameUtils.getByteString(Runtime.getRuntime().maxMemory()));
        long memoryTotal = Runtime.getRuntime().totalMemory();
        this.drawString("Memory total: " + GameUtils.getByteString(memoryTotal));
        long memoryUsed = memoryTotal - Runtime.getRuntime().freeMemory();
        double memoryUsedPercent = (double)memoryUsed / (double)memoryTotal;
        String memoryUsedString = GameUtils.getByteString(memoryUsed) + " (" + GameMath.toDecimals(memoryUsedPercent * 100.0, 2) + "%)";
        this.drawString("Memory used: " + memoryUsedString);
        ClientLevelLoading loading = client.levelManager.loading();
        if (loading != null) {
            this.skipY(10);
            int loadedRegions = loading.getRegionsLoadedCount();
            int queueRegions = loading.getRegionsLoadQueueCount();
            int requestedRegions = loading.getRegionsRequestedCount();
            this.drawString("Loaded regions: " + loadedRegions + ", Requested: " + requestedRegions + ", Queued: " + queueRegions);
            if (serverClient != null) {
                this.drawString("Server client regions: " + serverClient.getLoadedRegionsCount(level.getIdentifier()));
            }
        }
        this.skipY(10);
        AdventureParty adventureParty = Settings.serverPerspective && serverClient != null ? serverClient.adventureParty : client.adventureParty;
        String partyDebugString = adventureParty.getDebugString();
        if (partyDebugString != null) {
            this.drawString("Adventure party: " + partyDebugString);
        }
        this.skipY(10);
        SettlementsWorldData settlementsData = SettlementsWorldData.getSettlementsData(level);
        this.drawString("Total settlements: " + settlementsData.getTotalSettlements());
        this.drawString("Total loaded settlements: " + settlementsData.getTotalLoadedSettlements());
        this.skipY(10);
        NetworkSettlementData settlement = settlementsData.getNetworkDataAtTile(level.getIdentifier(), player.getTileX(), player.getTileY());
        if (settlement != null) {
            GameMessage settlementName = settlement.getSettlementName();
            this.drawString("Settlement name: " + (settlementName == null ? null : settlementName.translate()));
            this.drawString("Settlement owner: " + settlement.getOwnerAuth() + ", " + settlement.getOwnerName());
            this.drawString("Settlement team: " + settlement.getTeamID());
            this.drawString("Settlement disbanding: " + (settlement.isDisbanding() ? GameUtils.getTimeStringMillis(settlement.getDisbandTime() - level.getTime()) : "No"));
            if (settlement.isRaidActive()) {
                this.drawString("Settlement raid: Active");
            } else if (settlement.isRaidApproaching()) {
                this.drawString("Settlement raid: Approaching");
            } else {
                this.drawString("Settlement raid: None");
            }
        } else {
            this.drawString("No settlement found at player position");
        }
        this.skipY(10);
        this.drawString("Loaded regions: " + level.regionManager.getLoadedRegionsSize());
        if (serverLevel != null) {
            this.drawString("Server level loaded regions: " + serverLevel.regionManager.getLoadedRegionsSize());
        }
        if (serverClient != null) {
            this.drawString("Server client loaded regions: " + serverClient.getLoadedRegionsCount(level.getIdentifier()));
            this.skipY(10);
            int levelLoadedCount = serverClient.getServer().world.worldEntity.getLoadedLevelRegionsCount(serverClient.getLevelIdentifier());
            int totalLoadedCount = serverClient.getServer().world.worldEntity.getLoadedLevelRegionsCount(null);
            this.drawString("Server loaded preset regions: " + levelLoadedCount + ", total: " + totalLoadedCount);
        }
        if (WindowManager.getWindow().isKeyDown(340)) {
            LinkedList<ModifierTooltip> modifiers = level.buffManager.getModifierTooltips();
            this.skipY(10);
            if (modifiers.isEmpty()) {
                this.drawString("No level modifiers");
            } else {
                this.drawString("Level modifiers:");
                for (ModifierTooltip tooltip : modifiers) {
                    this.drawString(tooltip.tip.toMessage(null, null, null, false).translate());
                }
            }
        }
    }
}

