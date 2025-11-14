/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketBlinkScepter;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;
import necesse.level.maps.Level;

public class BlinkScepterTrinketItem
extends TrinketItem {
    protected GameTexture attackTexture;

    public BlinkScepterTrinketItem() {
        super(Item.Rarity.EPIC, 1200, TrinketsLootTable.trinkets);
        this.attackAnimTime.setBaseValue(200);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        return new Point((int)(player.x + aimDirX * 224.0f), (int)(player.y + aimDirY * 224.0f));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "blinksceptertip"));
        return tooltips;
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("blinksceptertrinket")};
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.attackTexture = GameTexture.fromFile("player/weapons/" + this.getStringID());
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return new GameSprite(this.attackTexture);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int range = Math.min(224, (int)attackerMob.getDistance(x, y));
        Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y);
        PacketBlinkScepter.applyToMob(level, attackerMob, dir.x, dir.y, range);
        PacketForceOfWind.addCooldownStack(attackerMob, 5.0f, level.isServer());
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();
        if (level.isServer()) {
            attackerMob.sendAttackerPacket(attackerMob, new PacketBlinkScepter(attackerMob, dir.x, dir.y, range));
        }
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.swoosh2, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.5f));
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        String out = super.canAttack(level, x, y, attackerMob, item);
        if (out != null) {
            return out;
        }
        return !attackerMob.isRiding() && !PacketForceOfWind.isOnCooldown(attackerMob) ? null : "";
    }
}

