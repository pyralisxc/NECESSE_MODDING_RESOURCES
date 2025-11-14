/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketChatMessage
 *  necesse.engine.network.packet.PacketMobChat
 *  necesse.engine.registries.MobRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LevelIdentifier
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.IncursionLevel
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.RegionPositionGetter
 */
package aphorea.items.consumable;

import aphorea.items.vanillaitemtypes.AphConsumableItem;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPositionGetter;

public class UnstableCore
extends AphConsumableItem {
    public UnstableCore() {
        super(5, true);
        this.itemCooldownTime.setBaseValue(2000);
        this.setItemCategory(new String[]{"consumable", "bossitems"});
        this.dropsAsMatDeathPenalty = true;
        this.keyWords.add("boss");
        this.rarity = Item.Rarity.LEGENDARY;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (level.isClient()) {
            return null;
        }
        if (level instanceof IncursionLevel) {
            return "inincursion";
        }
        if (!level.getIdentifier().equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return "notsurface";
        }
        if (level.getServer().world.worldEntity.isNight()) {
            return "night";
        }
        ArrayList<Point> spawnPoints = new ArrayList<Point>();
        Mob mob = MobRegistry.getMob((String)"unstablegelslime", (Level)level);
        int pTileX = player.getX() / 32;
        int pTileY = player.getY() / 32;
        for (int i = -10; i <= 10; ++i) {
            for (int j = -10; j <= 10; ++j) {
                int tileX = pTileX + i;
                int tileY = pTileY + j;
                if (level.isLiquidTile(tileX, tileY) || level.isShore(tileX, tileY) || mob.collidesWith(level, tileX * 32 + 16, tileY * 32 + 16)) continue;
                spawnPoints.add(new Point(tileX, tileY));
            }
        }
        if (spawnPoints.isEmpty()) {
            return "nospace";
        }
        return null;
    }

    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            GameMessage summonError;
            if (level instanceof IncursionLevel && (summonError = ((IncursionLevel)level).canSummonBoss("unstablegelslime")) != null) {
                if (player != null && player.isServerClient()) {
                    player.getServerClient().sendChatMessage(summonError);
                }
                return item;
            }
            ArrayList<Point> spawnPoints = new ArrayList<Point>();
            Mob mob = MobRegistry.getMob((String)"unstablegelslime", (Level)level);
            int pTileX = player.getX() / 32;
            int pTileY = player.getY() / 32;
            for (int i = -10; i <= 10; ++i) {
                for (int j = -10; j <= 10; ++j) {
                    int tileX = pTileX + i;
                    int tileY = pTileY + j;
                    if (level.isLiquidTile(tileX, tileY) || level.isShore(tileX, tileY) || mob.collidesWith(level, tileX * 32 + 16, tileY * 32 + 16)) continue;
                    spawnPoints.add(new Point(tileX, tileY));
                }
            }
            System.out.println("Unstable Gel Slime has been summoned at " + level.getIdentifier() + ".");
            Point spawnPoint = !spawnPoints.isEmpty() ? (Point)GameRandom.globalRandom.getOneOf(spawnPoints) : new Point(player.getTileX() + GameRandom.globalRandom.getIntBetween(-8, 8), player.getTileY() + GameRandom.globalRandom.getIntBetween(-8, 8));
            level.entityManager.addMob(mob, (float)(spawnPoint.x * 32 + 16), (float)(spawnPoint.y * 32 + 16));
            level.getServer().network.sendToClientsWithEntity((Packet)new PacketChatMessage((GameMessage)new LocalMessage("misc", "bosssummon", "name", mob.getLocalization())), (RegionPositionGetter)mob);
            if (level instanceof IncursionLevel) {
                ((IncursionLevel)level).onBossSummoned(mob);
            }
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        if (level.isServer() && player != null && player.isServerClient() && error.equals("inincursion")) {
            player.getServerClient().sendChatMessage((GameMessage)new LocalMessage("misc", "cannotsummoninincursion"));
        } else if (level.isServer() && player != null) {
            if (error.equals("night")) {
                level.getServer().network.sendPacket((Packet)new PacketMobChat(player.getUniqueID(), "message", "cantuseatnight"), player.getServerClient());
            } else {
                String translationKey;
                switch (error) {
                    case "alreadyspawned": {
                        translationKey = null;
                        break;
                    }
                    case "notsurface": {
                        translationKey = "portalnotonsurface";
                        break;
                    }
                    case "nospace": {
                        translationKey = "portalnospace";
                        break;
                    }
                    default: {
                        translationKey = "portalerror";
                    }
                }
                if (translationKey != null) {
                    level.getServer().network.sendPacket((Packet)new PacketMobChat(player.getUniqueID(), "itemtooltip", translationKey), player.getServerClient());
                }
            }
        }
        return item;
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"relic");
    }

    public ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"unstablecore"));
        return tooltips;
    }
}

