/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.GlobalData;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.item.ItemAttackerWeaponItem;

public abstract class MousePositionAttackHandler
extends AttackHandler {
    protected int lastX;
    protected int lastY;

    public MousePositionAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int updateInterval) {
        super(attackerMob, slot, updateInterval);
    }

    public Point getNextClientLevelPos(PlayerMob player, GameCamera camera) {
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            Point2D.Float aimDir = player.getControllerAimDir();
            return this.item.item.getControllerAttackLevelPos(player.getLevel(), aimDir.x, aimDir.y, player, this.item);
        }
        return new Point(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
    }

    public Point getNextItemAttackerLevelPos(Mob currentTarget) {
        return ((ItemAttackerWeaponItem)((Object)this.item.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, this.item);
    }

    @Override
    protected void setupClientUpdatePacket(PlayerMob player, PacketWriter writer) {
        super.setupClientUpdatePacket(player, writer);
        GameCamera camera = GlobalData.getCurrentState().getCamera();
        Point next = this.getNextClientLevelPos(player, camera);
        writer.putNextInt(next.x);
        writer.putNextInt(next.y);
    }

    @Override
    public void onUpdatePacket(PacketReader reader) {
        super.onUpdatePacket(reader);
        this.lastX = reader.getNextInt();
        this.lastY = reader.getNextInt();
    }

    @Override
    public void onUpdate() {
        if (this.attackerMob.isPlayer) {
            if (this.attackerMob.isClient()) {
                PlayerMob player = (PlayerMob)this.attackerMob;
                GameCamera camera = GlobalData.getCurrentState().getCamera();
                Point next = this.getNextClientLevelPos(player, camera);
                if (next.x != this.lastX || next.y != this.lastY) {
                    this.sendPacketUpdate(true);
                }
            }
        } else if (this.lastItemAttackerTarget != null) {
            Point next = this.getNextItemAttackerLevelPos(this.lastItemAttackerTarget);
            if (this.lastX != next.x || this.lastY != next.y) {
                this.lastX = next.x;
                this.lastY = next.y;
                this.onItemAttackerTargetChanged();
            }
        }
    }

    public void onItemAttackerTargetChanged() {
    }
}

