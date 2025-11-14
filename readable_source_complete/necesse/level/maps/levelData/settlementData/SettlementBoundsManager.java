/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.OneWorldNPCVillageData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.ZoneTester;

public class SettlementBoundsManager {
    public static int secondsToTickTiles = 120;
    public static FlagTierRegion[] flagTiers = new FlagTierRegion[]{new FlagTierRegion(null, new Rectangle(-2, -2, 5, 5)), new FlagTierRegion(new Ingredient[]{new Ingredient("coin", 2500), new Ingredient("demonicbar", 40)}, new Rectangle(-3, -3, 7, 7)), new FlagTierRegion(new Ingredient[]{new Ingredient("coin", 5000), new Ingredient("frostshard", 20), new Ingredient("runestone", 20), new Ingredient("ivybar", 20), new Ingredient("quartz", 20)}, new Rectangle(-4, -4, 9, 9)), new FlagTierRegion(new Ingredient[]{new Ingredient("coin", 10000), new Ingredient("tungstenbar", 40)}, new Rectangle(-5, -5, 11, 11)), new FlagTierRegion(new Ingredient[]{new Ingredient("coin", 20000), new Ingredient("glacialbar", 20), new Ingredient("amber", 20), new Ingredient("myceliumbar", 20), new Ingredient("ancientfossilbar", 20)}, new Rectangle(-6, -6, 13, 13)), new FlagTierRegion(new Ingredient[]{new Ingredient("coin", 50000), new Ingredient("amethyst", 20), new Ingredient("sapphire", 20), new Ingredient("emerald", 20), new Ingredient("ruby", 20), new Ingredient("topaz", 20)}, new Rectangle(-7, -7, 15, 15)), new FlagTierRegion(new Ingredient[]{new Ingredient("coin", 100000), new Ingredient("shadowessence", 50), new Ingredient("cryoessence", 50), new Ingredient("bioessence", 50), new Ingredient("primordialessence", 50), new Ingredient("slimeessence", 50), new Ingredient("bloodessence", 50), new Ingredient("spideressence", 50)}, new Rectangle(-8, -8, 17, 17))};
    protected final ServerSettlementData data;
    protected PointHashSet visitorSpawnTilesCache;
    protected double tileTickBuffer;
    protected Point nextTileTick;

    public SettlementBoundsManager(ServerSettlementData data) {
        this.data = data;
    }

    public void addSaveData(SaveData save) {
        if (this.nextTileTick != null) {
            save.addPoint("nextTileTick", this.nextTileTick);
        }
    }

    public void applySaveData(LoadData save) {
        this.nextTileTick = save.getPoint("nextTileTick", null, false);
        this.visitorSpawnTilesCache = null;
    }

    public static Ingredient[] getTierUpgradeCost(int flagTier) {
        if (++flagTier < 0 || flagTier >= flagTiers.length) {
            return null;
        }
        return SettlementBoundsManager.flagTiers[flagTier].upgradeCost;
    }

    public static Rectangle getUncenteredRegionRectangleFromTier(int flagTier) {
        flagTier = GameMath.limit(flagTier, 0, flagTiers.length - 1);
        return SettlementBoundsManager.flagTiers[flagTier].range;
    }

    public static Rectangle getRegionRectangleFromTier(int settlementTileX, int settlementTileY, int flagTier) {
        int regionX = GameMath.getRegionCoordByTile(settlementTileX);
        int regionY = GameMath.getRegionCoordByTile(settlementTileY);
        Rectangle regionRectangle = SettlementBoundsManager.getUncenteredRegionRectangleFromTier(flagTier);
        return new Rectangle(regionX + regionRectangle.x, regionY + regionRectangle.y, regionRectangle.width, regionRectangle.height);
    }

    public static Rectangle getLoadedRegionRectangleFromTier(int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle regionRectangle = SettlementBoundsManager.getRegionRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return new Rectangle(regionRectangle.x - 1, regionRectangle.y - 1, regionRectangle.width + 2, regionRectangle.height + 2);
    }

    public static Rectangle getLoadedTileRectangleFromTier(int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle regionRectangle = SettlementBoundsManager.getLoadedRegionRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return new Rectangle(GameMath.getTileCoordByRegion(regionRectangle.x), GameMath.getTileCoordByRegion(regionRectangle.y), GameMath.getTileCoordByRegion(regionRectangle.width), GameMath.getTileCoordByRegion(regionRectangle.height));
    }

    public static Rectangle getTileRectangleFromTier(int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle regionRectangle = SettlementBoundsManager.getRegionRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return new Rectangle(GameMath.getTileCoordByRegion(regionRectangle.x), GameMath.getTileCoordByRegion(regionRectangle.y), GameMath.getTileCoordByRegion(regionRectangle.width), GameMath.getTileCoordByRegion(regionRectangle.height));
    }

    public static Rectangle getLevelRectangleFromTier(int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle tileRectangle = SettlementBoundsManager.getTileRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return new Rectangle(GameMath.getLevelCoordinate(tileRectangle.x), GameMath.getLevelCoordinate(tileRectangle.y), GameMath.getLevelCoordinate(tileRectangle.width), GameMath.getLevelCoordinate(tileRectangle.height));
    }

    public static Stream<Point> streamRegionPositions(int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle regionRectangle = SettlementBoundsManager.getRegionRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return IntStream.range(regionRectangle.x, regionRectangle.x + regionRectangle.width).boxed().flatMap(x -> IntStream.range(regionRectangle.y, regionRectangle.y + regionRectangle.height).boxed().map(y -> new Point((int)x, (int)y)));
    }

    public static Iterable<Point> getRegionPositions(int settlementTileX, int settlementTileY, int flagTier) {
        return SettlementBoundsManager.streamRegionPositions(settlementTileX, settlementTileY, flagTier)::iterator;
    }

    public static boolean isTileWithinBounds(int tileX, int tileY, int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle tileRectangle = SettlementBoundsManager.getTileRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return tileRectangle.contains(tileX, tileY);
    }

    public static boolean isTileWithinLoadedRegionBounds(int tileX, int tileY, int settlementTileX, int settlementTileY, int flagTier) {
        Rectangle tileRectangle = SettlementBoundsManager.getLoadedTileRectangleFromTier(settlementTileX, settlementTileY, flagTier);
        return tileRectangle.contains(tileX, tileY);
    }

    public Rectangle getLoadedRegionRectangle() {
        return SettlementBoundsManager.getLoadedRegionRectangleFromTier(this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public Rectangle getRegionRectangle() {
        return SettlementBoundsManager.getRegionRectangleFromTier(this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public Rectangle getTileRectangle() {
        return SettlementBoundsManager.getTileRectangleFromTier(this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public Rectangle getLevelRectangle() {
        return SettlementBoundsManager.getLevelRectangleFromTier(this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public Stream<Point> streamRegionPositions() {
        return SettlementBoundsManager.streamRegionPositions(this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public Iterable<Point> getRegionPositions() {
        return SettlementBoundsManager.getRegionPositions(this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public boolean isTileWithinBounds(int tileX, int tileY) {
        return SettlementBoundsManager.isTileWithinBounds(tileX, tileY, this.data.networkData.getTileX(), this.data.networkData.getTileY(), this.data.networkData.getFlagTier());
    }

    public boolean expandSettlementIfPossible(ServerClient client, SettlementDependantContainer container) {
        if (this.data.getFlagTier() >= flagTiers.length - 1) {
            client.sendChatMessage(new LocalMessage("ui", "settlementfullyexpanded"));
            return false;
        }
        int newTier = this.data.getFlagTier() + 1;
        FlagTierRegion nextUpgrade = flagTiers[newTier];
        PlayerMob playerMob = client.playerMob;
        if (playerMob == null) {
            client.sendChatMessage(new StaticMessage(GameColor.RED.getColorCode() + "Error when expanding settlement: Client has no player mob."));
            return false;
        }
        Level level = this.data.getLevel();
        Recipe recipe = new Recipe("air", RecipeTechRegistry.NONE, nextUpgrade.upgradeCost);
        if (!container.canCraftRecipe(recipe, container.getCraftInventories(), true).canCraft()) {
            client.sendChatMessage(new StaticMessage(GameColor.RED.getColorCode() + "Error when expanding settlement: missing ingredients"));
            return false;
        }
        int flagX = this.data.networkData.getTileX();
        int flagY = this.data.networkData.getTileY();
        OneWorldNPCVillageData villageData = OneWorldNPCVillageData.getVillageData(level, false);
        if (villageData != null && !villageData.canPlaceSettlementFlagAt(flagX, flagY, newTier)) {
            client.sendChatMessage(new LocalMessage("misc", "tooclosevillage"));
            return false;
        }
        Rectangle currentRegionRectangle = SettlementBoundsManager.getRegionRectangleFromTier(flagX, flagY, this.data.getFlagTier());
        Rectangle upgradeRegionRectangle = SettlementBoundsManager.getRegionRectangleFromTier(flagX, flagY, newTier);
        for (int regionX = upgradeRegionRectangle.x; regionX < upgradeRegionRectangle.x + upgradeRegionRectangle.width; ++regionX) {
            for (int regionY = upgradeRegionRectangle.y; regionY < upgradeRegionRectangle.y + upgradeRegionRectangle.height; ++regionY) {
                int settlementUniqueID;
                if (currentRegionRectangle.contains(regionX, regionY) || (settlementUniqueID = this.data.worldData.getSettlementUniqueIDAtRegion(level.getIdentifier(), regionX, regionY)) == 0) continue;
                client.sendChatMessage(new LocalMessage("misc", "tooclosesettlement"));
                return false;
            }
        }
        recipe.craft(level, playerMob, container.getCraftInventories());
        this.data.networkData.setFlagTier(newTier);
        this.data.networkData.streamTeamMembers().forEach(teamClient -> teamClient.sendChatMessage(new LocalMessage("ui", "settlementexpanded", "client", client.getName(), "settlement", this.data.getSettlementName())));
        this.checkExpansionAchievements();
        return true;
    }

    public void checkExpansionAchievements() {
        int tier = this.data.networkData.getFlagTier();
        if (tier > 0) {
            this.data.networkData.streamTeamMembers().forEach(client -> {
                if (client.achievementsLoaded()) {
                    client.achievements().SETTLING_DOWN.markCompleted((ServerClient)client);
                }
            });
        }
        if (tier >= flagTiers.length - 1) {
            this.data.networkData.streamTeamMembers().forEach(client -> {
                if (client.achievementsLoaded()) {
                    client.achievements().EXPANSIONIST.markCompleted((ServerClient)client);
                }
            });
        }
    }

    public void tickTiles() {
        Rectangle tileRectangle = this.getTileRectangle();
        if (tileRectangle == null) {
            return;
        }
        Point flagTile = this.data.getFlagTile();
        if (flagTile == null) {
            return;
        }
        int totalTiles = tileRectangle.width * tileRectangle.height;
        this.tileTickBuffer += (double)totalTiles / 20.0 / (double)secondsToTickTiles;
        if (this.nextTileTick == null) {
            this.nextTileTick = new Point(tileRectangle.x, tileRectangle.y);
        }
        while (this.tileTickBuffer >= 1.0) {
            this.tileTickBuffer -= 1.0;
            this.data.tickTile(this.nextTileTick.x, this.nextTileTick.y);
            ++this.nextTileTick.x;
            if (this.nextTileTick.x < tileRectangle.x + tileRectangle.width) continue;
            this.nextTileTick.x = tileRectangle.x;
            ++this.nextTileTick.y;
            if (this.nextTileTick.y < tileRectangle.y + tileRectangle.height) continue;
            this.nextTileTick.y = tileRectangle.y;
        }
    }

    public ZoneTester getZoneTester() {
        Rectangle tileRectangle = this.getTileRectangle();
        return tileRectangle::contains;
    }

    public static class FlagTierRegion {
        public final Ingredient[] upgradeCost;
        public final Rectangle range;

        public FlagTierRegion(Ingredient[] upgradeCost, Rectangle range) {
            this.upgradeCost = upgradeCost;
            this.range = range;
        }
    }
}

