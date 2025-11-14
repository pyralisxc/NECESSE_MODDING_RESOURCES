/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.spawnItems;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.packet.PacketMobChat;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.ConsumableItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public class EvilsProtectorSpawnItem
extends ConsumableItem {
    public EvilsProtectorSpawnItem() {
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
        if (level.isClient()) {
            return null;
        }
        if (level instanceof IncursionLevel) {
            return "inincursion";
        }
        if (!level.getIdentifier().equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return "notsurface";
        }
        if (!level.getWorldEntity().isNight()) {
            return "notnight";
        }
        ArrayList<Point> spawnPoints = new ArrayList<Point>();
        Mob mob = MobRegistry.getMob("evilsprotector", level);
        int pTileX = player.getTileX();
        int pTileY = player.getTileY();
        for (int i = -10; i <= 10; ++i) {
            for (int j = -10; j <= 10; ++j) {
                int tileX = pTileX + i;
                int tileY = pTileY + j;
                if (level.isLiquidTile(tileX, tileY) || level.isShore(tileX, tileY) || mob.collidesWith(level, tileX * 32 + 16, tileY * 32 + 16)) continue;
                spawnPoints.add(new Point(tileX, tileY));
            }
        }
        if (spawnPoints.size() == 0) {
            return "nospace";
        }
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            GameMessage summonError;
            ServerClient serverClient = player.getServerClient();
            if (level instanceof IncursionLevel && (summonError = ((IncursionLevel)level).canSummonBoss("evilsprotector")) != null) {
                if (player != null && player.isServerClient()) {
                    serverClient.sendChatMessage(summonError);
                }
                return item;
            }
            ArrayList<Point> spawnPoints = new ArrayList<Point>();
            Mob mob = MobRegistry.getMob("evilsprotector", level);
            int pTileX = player.getTileX();
            int pTileY = player.getTileY();
            for (int i = -10; i <= 10; ++i) {
                for (int j = -10; j <= 10; ++j) {
                    int tileX = pTileX + i;
                    int tileY = pTileY + j;
                    if (level.isLiquidTile(tileX, tileY) || level.isShore(tileX, tileY) || mob.collidesWith(level, tileX * 32 + 16, tileY * 32 + 16)) continue;
                    spawnPoints.add(new Point(tileX, tileY));
                }
            }
            System.out.println("Evil's Protector has been summoned at " + level.getIdentifier() + ".");
            Point spawnPoint = !spawnPoints.isEmpty() ? (Point)GameRandom.globalRandom.getOneOf(spawnPoints) : new Point(player.getTileX() + GameRandom.globalRandom.getIntBetween(-8, 8), player.getTileY() + GameRandom.globalRandom.getIntBetween(-8, 8));
            level.entityManager.addMob(mob, spawnPoint.x * 32 + 16, spawnPoint.y * 32 + 16);
            level.getServer().network.sendToClientsWithEntity(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", mob.getLocalization())), mob);
            JournalChallenge challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.USE_MYSTERIOUS_PORTAL_ID);
            if (!challenge.isCompleted(serverClient) && challenge.isJournalEntryDiscovered(serverClient)) {
                challenge.markCompleted(serverClient);
                serverClient.forceCombineNewStats();
            }
            if (level instanceof IncursionLevel) {
                ((IncursionLevel)level).onBossSummoned(mob);
            }
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        String translationKey;
        if (level.isServer() && player != null && player.isServerClient() && error.equals("inincursion")) {
            player.getServerClient().sendChatMessage(new LocalMessage("misc", "cannotsummoninincursion"));
            return item;
        }
        if (level.isServer() && (translationKey = error.equals("alreadyspawned") ? null : (error.equals("notsurface") ? "portalnotonsurface" : (error.equals("notnight") ? "portalnotnight" : (error.equals("nospace") ? "portalnospace" : "portalerror")))) != null) {
            level.getServer().network.sendPacket((Packet)new PacketMobChat(player.getUniqueID(), "itemtooltip", translationKey), player.getServerClient());
        }
        return item;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "evilsevent"));
        return tooltips;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "relic");
    }
}

