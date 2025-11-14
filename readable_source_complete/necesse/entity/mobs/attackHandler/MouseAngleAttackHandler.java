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
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.item.ItemAttackerWeaponItem;

public abstract class MouseAngleAttackHandler
extends AttackHandler {
    protected float currentAngle;
    protected int targetAngle;
    protected float speed;

    public MouseAngleAttackHandler(ItemAttackerMob itemAttacker, ItemAttackSlot slot, int updateInterval, float speed, int startTargetX, int startTargetY) {
        super(itemAttacker, slot, updateInterval);
        this.speed = speed;
        this.targetAngle = (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)startTargetX - itemAttacker.x, (float)startTargetY - itemAttacker.y)));
        this.currentAngle = this.targetAngle;
    }

    public int getNextClientAngle(PlayerMob player, GameCamera camera) {
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            Point2D.Float aimDir = player.getControllerAimDir();
            return (int)GameMath.fixAngle(GameMath.getAngle(aimDir));
        }
        return (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)camera.getMouseLevelPosX() - player.x, (float)camera.getMouseLevelPosY() - player.y)));
    }

    public int getNextItemAttackerAngle(Mob currentTarget) {
        Point attackPos = ((ItemAttackerWeaponItem)((Object)this.item.item)).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, this.item);
        return (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)attackPos.x - this.attackerMob.x, (float)attackPos.y - this.attackerMob.y)));
    }

    @Override
    protected void setupClientUpdatePacket(PlayerMob player, PacketWriter writer) {
        super.setupClientUpdatePacket(player, writer);
        GameCamera camera = GlobalData.getCurrentState().getCamera();
        writer.putNextInt(this.getNextClientAngle(player, camera));
    }

    @Override
    public void onUpdatePacket(PacketReader reader) {
        super.onUpdatePacket(reader);
        this.targetAngle = reader.getNextInt();
    }

    @Override
    public void onUpdate() {
        if (this.attackerMob.isPlayer) {
            GameCamera camera;
            PlayerMob player;
            if (this.attackerMob.isClient() && this.getNextClientAngle(player = (PlayerMob)this.attackerMob, camera = GlobalData.getCurrentState().getCamera()) != this.targetAngle) {
                this.sendPacketUpdate(true);
            }
        } else if (this.lastItemAttackerTarget != null) {
            this.targetAngle = this.getNextItemAttackerAngle(this.lastItemAttackerTarget);
        }
        if (this.currentAngle != (float)this.targetAngle) {
            float delta = GameMath.getAngleDifference(this.currentAngle, this.targetAngle);
            float change = this.speed * (float)this.updateInterval / 250.0f;
            if (Math.abs(delta) < change) {
                this.currentAngle = this.targetAngle;
            } else if (delta < 0.0f) {
                this.currentAngle = GameMath.fixAngle(this.currentAngle + change);
                if (GameMath.getAngleDifference(this.currentAngle, this.targetAngle) > 0.0f) {
                    this.currentAngle = this.targetAngle;
                }
            } else if (delta > 0.0f) {
                this.currentAngle = GameMath.fixAngle(this.currentAngle - change);
                if (GameMath.getAngleDifference(this.currentAngle, this.targetAngle) < 0.0f) {
                    this.currentAngle = this.targetAngle;
                }
            }
        }
    }
}

