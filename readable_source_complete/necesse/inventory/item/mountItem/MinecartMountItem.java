/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.mountItem;

import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.MinecartLinePos;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.entity.mobs.summon.MinecartMob;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MinecartMountMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.mountItem.MountItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;

public class MinecartMountItem
extends MountItem
implements PlaceableItemInterface {
    public MinecartMountItem() {
        super("minecartmount");
        this.setMounterPos = false;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = this.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "minecarttip"));
        return tooltips;
    }

    @Override
    public String canUseMount(InventoryItem item, PlayerMob player, Level level) {
        Mob lastMount = player.getMount();
        if (lastMount != null) {
            return null;
        }
        String superError = super.canUseMount(item, player, level);
        if (superError != null) {
            return superError;
        }
        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        for (int tileX = playerTileX - 1; tileX <= playerTileX + 1; ++tileX) {
            for (int tileY = playerTileY - 1; tileY <= playerTileY + 1; ++tileY) {
                GameObject object = level.getObject(tileX, tileY);
                if (!(object instanceof MinecartTrackObject) || object instanceof TrapTrackObject) continue;
                return null;
            }
        }
        return Localization.translate("misc", "cannotusemounthere", "mount", this.getDisplayName(item));
    }

    @Override
    public Point2D.Float getMountSpawnPos(Mob mount, ServerClient client, float playerX, float playerY, InventoryItem item, Level level) {
        PlayerMob player = client.playerMob;
        float bestDist = Float.MAX_VALUE;
        MinecartLinePos bestPos = null;
        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();
        for (int tileX = playerTileX - 1; tileX <= playerTileX + 1; ++tileX) {
            for (int tileY = playerTileY - 1; tileY <= playerTileY + 1; ++tileY) {
                MinecartLines lines;
                MinecartLinePos pos;
                GameObject object = level.getObject(tileX, tileY);
                if (!(object instanceof MinecartTrackObject) || object instanceof TrapTrackObject || (pos = (lines = ((MinecartTrackObject)object).getMinecartLines(level, tileX, tileY, 0.0f, 0.0f, false)).getMinecartPos(player.x, player.y, player.getDir())) == null) continue;
                float distance = player.getDistance(pos.x, pos.y);
                if (bestPos != null && !(distance < bestDist)) continue;
                bestPos = pos;
                bestDist = distance;
            }
        }
        if (bestPos != null) {
            mount.setDir(bestPos.dir);
            ((MinecartMountMob)mount).minecartDir = bestPos.dir;
            return new Point2D.Float(bestPos.x, bestPos.y);
        }
        return super.getMountSpawnPos(mount, client, playerX, playerY, item, level);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer) {
            return item;
        }
        PlayerMob player = (PlayerMob)attackerMob;
        if (this.canPlace(level, x, y, player, item, mapContent) == null) {
            Mob mob;
            if (level.isServer() && (mob = MobRegistry.getMob("minecart", level)) instanceof MinecartMob) {
                ((MinecartMob)mob).minecartDir = attackerMob.isAttacking ? attackerMob.beforeAttackDir : attackerMob.getDir();
                mob.resetUniqueID();
                level.entityManager.addMob(mob, x, y);
            }
            if (level.isClient()) {
                SoundManager.playSound(GameResources.blunthit, (SoundEffect)SoundEffect.effect(x, y).volume(0.3f));
                SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(x, y).volume(0.3f));
            }
            item.setAmount(item.getAmount() - 1);
            return item;
        }
        return item;
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return null;
    }

    protected String canPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent) {
        if (player.getPositionPoint().distance(x, y) > 100.0) {
            return "outofrange";
        }
        Mob mob = MobRegistry.getMob("minecart", level);
        if (mob != null) {
            mob.setPos(x, y, true);
            if (mob.collidesWith(level)) {
                return "collision";
            }
            GameObject object = level.getObject(mob.getTileX(), mob.getTileY());
            if (!(object instanceof MinecartTrackObject) || object instanceof TrapTrackObject) {
                return "nottracks";
            }
        }
        return null;
    }

    @Override
    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        String error = this.canPlace(level, x, y, player, item, null);
        if (error == null) {
            int placeDir = player.isAttacking ? player.beforeAttackDir : player.getDir();
            MinecartMob.drawPlacePreview(level, x, y, placeDir, camera);
        }
    }
}

