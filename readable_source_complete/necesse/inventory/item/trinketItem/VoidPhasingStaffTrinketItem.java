/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketForceOfWind;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.VoidPhasingStaffTrinketBuff;
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
import necesse.level.maps.Level;

public class VoidPhasingStaffTrinketItem
extends TrinketItem {
    protected GameTexture attackTexture;

    public VoidPhasingStaffTrinketItem() {
        super(Item.Rarity.EPIC, 1200, null);
        this.attackAnimTime.setBaseValue(200);
        this.attackXOffset = 30;
        this.attackYOffset = 30;
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
    public boolean animDrawBehindHand(InventoryItem item) {
        return true;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(-30.0f);
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        return new Point((int)(player.x + aimDirX * 32.0f * 20.0f), (int)(player.y + aimDirY * 32.0f * 20.0f));
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "voidphasingstafftip"));
        return tooltips;
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        return new TrinketBuff[]{(TrinketBuff)BuffRegistry.getBuff("voidphasingstafftrinket")};
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (attackerMob.isPlayer && attackerMob instanceof PlayerMob) {
            VoidPhasingStaffTrinketBuff.useBlinkAbility((PlayerMob)attackerMob, x, y);
        }
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.swoosh2, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.5f));
        }
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

