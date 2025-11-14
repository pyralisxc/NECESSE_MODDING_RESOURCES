/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.followerSummonItem;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class FollowerSummonPlaceableItem
extends PlaceableItem {
    protected String mobType;
    protected String summonType;
    protected String buffType;
    protected FollowPosition followPosition;
    protected int maxSummons;

    public FollowerSummonPlaceableItem(int stackSize, boolean singleUse, String mobType, FollowPosition followPosition, String summonType, String buffType, int maxSummons) {
        super(stackSize, singleUse);
        this.keyWords.add("follower");
        this.mobType = mobType;
        this.followPosition = followPosition;
        this.summonType = summonType;
        this.buffType = buffType;
        this.maxSummons = maxSummons;
        this.worldDrawSize = 32;
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    public int getMaxSummons(ItemAttackerMob attackerMob) {
        return this.maxSummons;
    }

    public int getSummonSpaceTaken() {
        return 1;
    }

    protected String canSummon(Level level, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        if (this.getMaxSummons(player) < this.getSummonSpaceTaken()) {
            return "nosummonspace";
        }
        return null;
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        this.setupSummonMapContent(map);
    }

    protected void setupSummonMapContent(GNDItemMap map) {
    }

    protected void summonServerMob(Level level, ItemAttackerMob attackerMob, InventoryItem item, GNDItemMap mapContent) {
        Mob mob = MobRegistry.getMob(this.mobType, level);
        attackerMob.serverFollowersManager.addFollower(this.summonType, mob, this.followPosition, this.buffType, (float)this.getSummonSpaceTaken(), this::getMaxSummons, null, false);
        Point spawnPoint = this.findSpawnLocation(level, mob, attackerMob.getTileX(), attackerMob.getTileY(), 1);
        this.beforeSpawn(mob, item);
        level.entityManager.addMob(mob, spawnPoint.x, spawnPoint.y);
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (level.isServer()) {
            this.summonServerMob(level, player, item, mapContent);
        }
        if (this.isSingleUse(player)) {
            item.setAmount(item.getAmount() - 1);
        }
        return item;
    }

    protected void beforeSpawn(Mob mob, InventoryItem item) {
    }

    protected Point findSpawnLocation(Level level, Mob mob, int tileX, int tileY, int maxTileRange) {
        ArrayList<Point> possibleSpawns = new ArrayList<Point>();
        for (int x = tileX - maxTileRange; x <= tileX + maxTileRange; ++x) {
            for (int y = tileY - maxTileRange; y <= tileY + maxTileRange; ++y) {
                int posY;
                int posX;
                if (x == tileX && y == tileY || mob.collidesWith(level, posX = x * 32 + 16, posY = y * 32 + 16)) continue;
                possibleSpawns.add(new Point(posX, posY));
            }
        }
        if (possibleSpawns.size() > 0) {
            return (Point)possibleSpawns.get(GameRandom.globalRandom.nextInt(possibleSpawns.size()));
        }
        return new Point(tileX * 32 + 16, tileY * 32 + 16);
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        return this.canSummon(level, player, item, mapContent);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        int maxSummons;
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.addAll(this.getAnimalTooltips(item, perspective));
        int spaceTaken = this.getSummonSpaceTaken();
        if (spaceTaken != 1) {
            tooltips.add(Localization.translate("itemtooltip", "summonuseslots", "count", (Object)spaceTaken));
        }
        if ((maxSummons = this.getMaxSummons(perspective)) != 1) {
            tooltips.add(Localization.translate("itemtooltip", "summonslots", "count", (Object)maxSummons));
        }
        if (this.isSingleUse(perspective)) {
            tooltips.add(Localization.translate("itemtooltip", "singleuse"));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "infiniteuse"));
        }
        return tooltips;
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int maxSummons = this.getMaxSummons(perspective);
            if (maxSummons > 999) {
                maxSummons = 999;
            }
            if (maxSummons != 1) {
                String amountString = String.valueOf(maxSummons);
                int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
                FontManager.bit.drawString(x + 28 - width, y + 16, amountString, tipFontOptions);
            }
        }
    }

    protected ListGameTooltips getAnimalTooltips(InventoryItem item, Attacker attacker) {
        return new ListGameTooltips(Localization.translate("itemtooltip", "summontip", "mob", MobRegistry.getDisplayName(MobRegistry.getMobID(this.mobType))));
    }
}

