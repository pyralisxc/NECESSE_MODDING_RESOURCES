/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.AscendedBowProjectileToolItem;

public class AscendedBowAttackHandler
extends MousePositionAttackHandler {
    private final InventoryItem invItem;
    private final AscendedBowProjectileToolItem toolItem;
    private long lastShootTime;
    private int attackSeed = 0;
    private final int actualAttackCooldown;

    public AscendedBowAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int targetX, int targetY, int attackSeed, AscendedBowProjectileToolItem toolItem) {
        super(attackerMob, slot, 50);
        this.lastX = targetX;
        this.lastY = targetY;
        this.toolItem = toolItem;
        this.attackSeed = attackSeed;
        this.invItem = this.item;
        float multiplier = 1.0f / toolItem.getAttackSpeedModifier(this.invItem, attackerMob);
        this.actualAttackCooldown = (int)((float)toolItem.getAttackAnimTime(this.invItem, attackerMob) * multiplier);
        this.lastShootTime = attackerMob.getTime() - (long)this.actualAttackCooldown;
    }

    @Override
    public void onMouseInteracted(int levelX, int levelY) {
        this.toggleBow();
    }

    @Override
    public void onControllerInteracted(float aimX, float aimY) {
        this.toggleBow();
    }

    @Override
    protected void setupClientUpdatePacket(PlayerMob player, PacketWriter writer) {
        super.setupClientUpdatePacket(player, writer);
        writer.putNextBoolean(AscendedBowProjectileToolItem.getToggledBowState(this.invItem));
    }

    @Override
    public void onUpdatePacket(PacketReader reader) {
        super.onUpdatePacket(reader);
        boolean isToggled = reader.getNextBoolean();
        AscendedBowProjectileToolItem.setToggledBowState(isToggled, this.invItem, this.attackerMob);
    }

    public void toggleBow() {
        boolean nextToggledState = !AscendedBowProjectileToolItem.getToggledBowState(this.invItem);
        AscendedBowProjectileToolItem.setToggledBowState(nextToggledState, this.invItem, this.attackerMob);
        if (this.attackerMob.isPlayer && this.attackerMob.isClient()) {
            this.sendPacketUpdate(true);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        long currentTime = this.attackerMob.getTime();
        if (currentTime >= this.lastShootTime + (long)this.actualAttackCooldown) {
            this.lastShootTime += (long)this.actualAttackCooldown;
            ++this.attackSeed;
            this.doAttack();
        }
    }

    protected void doAttack() {
        InventoryItem attackItem = this.invItem.copy();
        attackItem.getGndData().setBoolean("ascendedAttack", true);
        if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
            Point attackPos = ((ItemAttackerWeaponItem)((Object)attackItem.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
            this.lastX = attackPos.x;
            this.lastY = attackPos.y;
        }
        GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.attackSeed);
        this.toolItem.superOnAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, this.attackSeed, attackMap);
        for (ActiveBuff b : this.attackerMob.buffManager.getArrayBuffs()) {
            b.onItemAttacked(this.lastX, this.lastY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), attackItem, this.slot, 0, attackMap);
        }
        if (!this.attackerMob.isPlayer && GameMath.getTileCoordinate(this.lastX) % 2 == 0) {
            this.toggleBow();
        }
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        this.attackerMob.doAndSendStopAttackAttacker(false);
        InventoryItem slotItem = this.slot.getItem();
        if (slotItem != null && slotItem.item instanceof AscendedBowProjectileToolItem) {
            boolean toggledBowState = AscendedBowProjectileToolItem.getToggledBowState(this.invItem);
            AscendedBowProjectileToolItem.setToggledBowState(toggledBowState, slotItem, null);
        }
    }
}

