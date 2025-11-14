/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.mountItem;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.WoodBoatMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlaceableItemInterface;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.mountItem.MountItem;
import necesse.level.maps.Level;

public class WoodBoatMountItem
extends MountItem
implements PlaceableItemInterface {
    public WoodBoatMountItem() {
        super("woodboatmount");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = this.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "boattip1"));
        return tooltips;
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
            if (level.isServer()) {
                Mob mob = MobRegistry.getMob("woodboat", level);
                if (mob != null) {
                    mob.resetUniqueID();
                    mob.setDir(attackerMob.getDir());
                    level.entityManager.addMob(mob, x, y);
                }
            } else if (level.isClient() && Settings.showControlTips) {
                level.getClient().setMessage(new LocalMessage("misc", "boatplacetip"), Color.WHITE, 5.0f);
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
        Mob mob = MobRegistry.getMob("woodboat", level);
        if (mob != null) {
            mob.setPos(x, y, true);
            if (mob.collidesWith(level)) {
                return "collision";
            }
        }
        return null;
    }

    @Override
    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        String error = this.canPlace(level, x, y, player, item, null);
        if (error == null) {
            WoodBoatMob.drawPlacePreview(level, x, y, player.getDir(), camera);
        }
    }
}

