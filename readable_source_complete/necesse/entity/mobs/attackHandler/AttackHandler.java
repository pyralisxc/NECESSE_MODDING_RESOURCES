/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public abstract class AttackHandler {
    public final ItemAttackerMob attackerMob;
    public final ItemAttackSlot slot;
    public final InventoryItem item;
    public final int updateInterval;
    private double updateTimer;
    private boolean isFromInteract;
    private boolean setInitialTarget;
    private int nullTargetBuffer;
    protected Mob lastItemAttackerTarget;

    public AttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int updateInterval) {
        this.attackerMob = attackerMob;
        this.slot = slot;
        this.item = slot.getItem();
        if (this.item == null) {
            throw new IllegalStateException("Could not find item for attack handler: " + attackerMob.getDisplayName() + ", " + slot);
        }
        this.updateInterval = updateInterval;
        this.updateTimer = 0.0;
    }

    public AttackHandler startFromInteract() {
        this.isFromInteract = true;
        return this;
    }

    public boolean isFromInteract() {
        return this.isFromInteract;
    }

    public final void setItemAttackerTarget(Mob target) {
        if (!this.setInitialTarget) {
            this.lastItemAttackerTarget = target;
            if (target != null) {
                this.onItemAttackerTargetUpdate(null, target);
            }
            this.setInitialTarget = true;
        }
        if (target != this.lastItemAttackerTarget) {
            if (target == null) {
                ++this.nullTargetBuffer;
                if (this.nullTargetBuffer <= 5) {
                    return;
                }
            }
            this.nullTargetBuffer = 0;
            this.onItemAttackerTargetUpdate(this.lastItemAttackerTarget, target);
        } else if (target != null) {
            this.nullTargetBuffer = 0;
        }
    }

    public void onItemAttackerTargetUpdate(Mob lastTarget, Mob newTarget) {
        this.lastItemAttackerTarget = newTarget;
        if (newTarget == null) {
            this.attackerMob.endAttackHandler(false);
        }
    }

    public final void tickUpdate(float delta) {
        if (!this.slot.isStillValid(this.attackerMob, this.item)) {
            this.attackerMob.endAttackHandler(false);
            return;
        }
        this.updateTimer += (double)delta;
        while (this.updateTimer >= (double)this.updateInterval) {
            this.updateTimer -= (double)this.updateInterval;
            this.onUpdate();
        }
    }

    public void onMouseInteracted(int levelX, int levelY) {
    }

    public void onControllerInteracted(float aimX, float aimY) {
    }

    public boolean getConstantInteract() {
        return false;
    }

    public final void clientEndAttack() {
        this.attackerMob.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.clientEnd());
        this.attackerMob.endAttackHandler(true);
    }

    protected void setupClientUpdatePacket(PlayerMob player, PacketWriter writer) {
    }

    protected final void sendPacketUpdate(boolean executePacket) {
        if (!this.attackerMob.isPlayer) {
            throw new IllegalStateException("Cannot send attack handler packet update for non-player mob");
        }
        PlayerMob player = (PlayerMob)this.attackerMob;
        Packet content = new Packet();
        this.setupClientUpdatePacket(player, new PacketWriter(content));
        if (this.attackerMob.isServer()) {
            this.attackerMob.getLevel().getServer().network.sendPacket((Packet)PacketPlayerAttackHandler.update(content), player.getServerClient());
        } else if (this.attackerMob.isClient()) {
            this.attackerMob.getLevel().getClient().network.sendPacket(PacketPlayerAttackHandler.update(content));
        }
        if (executePacket) {
            this.onUpdatePacket(new PacketReader(content));
        }
    }

    public void onUpdatePacket(PacketReader reader) {
    }

    public abstract void onUpdate();

    public abstract void onEndAttack(boolean var1);

    public boolean isFrom(InventoryItem item, ItemAttackSlot slot) {
        if (item != null && !this.item.equals(this.attackerMob.getLevel(), item, true, false, "equals")) {
            return false;
        }
        return slot == null || this.slot.isSameSlot(slot);
    }

    public boolean canRunAttack(Level level, int attackX, int attackY, ItemAttackerMob attackerMob, InventoryItem item, ItemAttackSlot slot) {
        return false;
    }

    public void drawHUDItemSelected(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
    }

    public void drawControllerAimPos(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
        if (item != null) {
            item.item.drawControllerAimPos(camera, level, player, item);
        }
    }
}

