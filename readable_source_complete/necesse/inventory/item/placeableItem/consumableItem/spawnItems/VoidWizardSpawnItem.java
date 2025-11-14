/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.spawnItems;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.VoidWizard;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.dungeon.DungeonArenaLevel;

public class VoidWizardSpawnItem
extends ConsumableItem {
    public VoidWizardSpawnItem() {
        super(1, true);
        this.itemCooldownTime.setBaseValue(2000);
        this.setItemCategory("consumable", "bossitems");
        this.dropsAsMatDeathPenalty = true;
        this.keyWords.add("boss");
        this.rarity = Item.Rarity.LEGENDARY;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        int tileY;
        int tileX;
        if (level instanceof IncursionLevel) {
            return null;
        }
        if (player == null) {
            tileX = GameMath.getTileCoordinate(x);
            tileY = GameMath.getTileCoordinate(y);
        } else {
            tileX = player.getTileX();
            tileY = player.getTileY();
        }
        if (!(level instanceof DungeonArenaLevel) || level.getBiome(tileX, tileY) != BiomeRegistry.DUNGEON) {
            return "notinarena";
        }
        if (level.entityManager.mobs.streamInRegionsShape(GameUtils.rangeTileBounds(x, y, 150), 0).anyMatch(m -> m.getStringID().equals("voidwizard"))) {
            return "alreadyspawned";
        }
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            Point2D.Float bossPos;
            int tileY;
            int tileX;
            GameMessage summonError;
            if (level instanceof IncursionLevel && (summonError = ((IncursionLevel)level).canSummonBoss("reaper")) != null) {
                if (player != null && player.isServerClient()) {
                    player.getServerClient().sendChatMessage(summonError);
                }
                return item;
            }
            Mob mob = MobRegistry.getMob("voidwizard", level);
            ((VoidWizard)mob).makeItemSpawned();
            if (player == null) {
                tileX = GameMath.getTileCoordinate(x);
                tileY = GameMath.getTileCoordinate(y);
            } else {
                tileX = player.getTileX();
                tileY = player.getTileY();
            }
            if (level instanceof DungeonArenaLevel && level.getBiome(tileX, tileY) == BiomeRegistry.DUNGEON) {
                bossPos = DungeonArenaLevel.getBossPosition();
            } else {
                ArrayList<Point> spawnPoints = new ArrayList<Point>();
                int pTileX = player.getTileX();
                int pTileY = player.getTileY();
                for (int i = -10; i <= 10; ++i) {
                    for (int j = -10; j <= 10; ++j) {
                        int spawnTileX = pTileX + i;
                        int spawnTileY = pTileY + j;
                        if (level.isLiquidTile(spawnTileX, spawnTileY) || level.isShore(spawnTileX, spawnTileY) || mob.collidesWith(level, spawnTileX * 32 + 16, spawnTileY * 32 + 16)) continue;
                        spawnPoints.add(new Point(spawnTileX, spawnTileY));
                    }
                }
                Point spawnPoint = !spawnPoints.isEmpty() ? (Point)GameRandom.globalRandom.getOneOf(spawnPoints) : new Point(player.getTileX() + GameRandom.globalRandom.getIntBetween(-8, 8), player.getTileY() + GameRandom.globalRandom.getIntBetween(-8, 8));
                bossPos = new Point2D.Float(spawnPoint.x * 32 + 16, spawnPoint.y * 32 + 16);
            }
            level.entityManager.addMob(mob, bossPos.x, bossPos.y);
            System.out.println("Void Wizard has been summoned at " + level.getIdentifier() + ".");
            if (this.isSingleUse(player)) {
                item.setAmount(item.getAmount() - 1);
            }
            if (level instanceof IncursionLevel) {
                ((IncursionLevel)level).onBossSummoned(mob);
            }
        }
        return item;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        if (level.isServer() && error.equals("notinarena")) {
            level.getServer().network.sendPacket((Packet)new PacketMobChat(player.getUniqueID(), "itemtooltip", "callernoarena"), player.getServerClient());
        }
        return item;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "voidcallertip1"));
        tooltips.add(Localization.translate("itemtooltip", "voidcallertip2"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "relic");
    }
}

