/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import necesse.engine.GlobalData;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketPlaceAttackHandlerUpdate;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;

public abstract class PlaceItemAttackHandler<T extends PlacePosition>
extends AttackHandler {
    protected int lastPlayerPositionX;
    protected int lastPlayerPositionY;
    protected int lastPlaceX;
    protected int lastPlaceY;
    protected long lastPlaceTime;
    public final int seed;

    public PlaceItemAttackHandler(PlayerMob player, ItemAttackSlot slot, int startLevelX, int startLevelY, int seed) {
        super(player, slot, 50);
        this.seed = seed;
        this.lastPlayerPositionX = player.getX();
        this.lastPlayerPositionY = player.getY();
        this.lastPlaceX = startLevelX;
        this.lastPlaceY = startLevelY;
        this.lastPlaceTime = player.getLocalTime();
    }

    public void runInitialPlace(GNDItemMap attackMapContent) {
        InventoryItem item = this.slot.getItem();
        if (item == null || item.item.getID() != this.item.item.getID()) {
            this.attackerMob.endAttackHandler(false);
            return;
        }
        this.placeItem(item, this.lastPlaceX, this.lastPlaceY, null, attackMapContent);
    }

    public Point getNextClientLevelPos(PlayerMob player, GameCamera camera) {
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            Point2D.Float aimDir = player.getControllerAimDir();
            return this.item.item.getControllerAttackLevelPos(player.getLevel(), aimDir.x, aimDir.y, player, this.item);
        }
        return new Point(camera.getMouseLevelPosX(), camera.getMouseLevelPosY());
    }

    public void handlePlaceClientUpdatePacket(int nextPlaceX, int nextPlaceY, PacketReader reader) {
        Line2D.Float playerPositionLine = null;
        if (this.lastPlayerPositionX != this.attackerMob.getX() || this.lastPlayerPositionY != this.attackerMob.getY()) {
            playerPositionLine = new Line2D.Float(this.lastPlayerPositionX, this.lastPlayerPositionY, this.attackerMob.getX(), this.attackerMob.getY());
        }
        this.lastPlaceX = nextPlaceX;
        this.lastPlaceY = nextPlaceY;
        this.lastPlayerPositionX = this.attackerMob.getX();
        this.lastPlayerPositionY = this.attackerMob.getY();
        ArrayList placePositions = reader.getNextCollection(ArrayList::new, () -> this.createPlacePositionFromPacket(reader));
        int placesThisTick = this.getAndUpdatePlacesThisTick(1);
        this.runServerPlace(placesThisTick, placePositions, playerPositionLine);
    }

    public void sendClientUpdatePacket(int nextPlaceX, int nextPlaceY, LinkedList<T> placePositions) {
        Packet placeOptionsPacket = new Packet();
        PacketWriter writer = new PacketWriter(placeOptionsPacket);
        writer.putNextCollection(placePositions, p -> p.writePacket(writer));
        this.attackerMob.getLevel().getClient().network.sendPacket(new PacketPlaceAttackHandlerUpdate(this, nextPlaceX, nextPlaceY, placeOptionsPacket));
    }

    protected LinkedList<T> runAndFindPlace(int lastPlaceX, int lastPlaceY, int nextPlaceX, int nextPlaceY, int placesThisTick, Line2D playerPositionLine) {
        LinkedList<T> placePositions = new LinkedList<T>();
        InventoryItem item = this.slot.getItem();
        if (item == null || item.item.getID() != this.item.item.getID()) {
            this.attackerMob.endAttackHandler(false);
            return placePositions;
        }
        if (placesThisTick > 0) {
            float totalDistance = GameMath.getExactDistance(nextPlaceX, nextPlaceY, lastPlaceX, lastPlaceY);
            float distancePerPlace = totalDistance / (float)placesThisTick;
            Point2D.Float dir = GameMath.normalize(lastPlaceX - nextPlaceX, lastPlaceY - nextPlaceY);
            for (float currentDistance = 0.0f; currentDistance <= totalDistance; currentDistance += (float)this.getLineProgressPerCheck()) {
                int currentX = (int)((float)nextPlaceX + dir.x * currentDistance);
                int currentY = (int)((float)nextPlaceY + dir.y * currentDistance);
                T placePosition = this.placeItem(item, currentX, currentY, playerPositionLine, null);
                if (placePosition == null) continue;
                ((PlayerMob)this.attackerMob).currentAttackLastPlacePosition = new Point(((PlacePosition)placePosition).placeX, ((PlacePosition)placePosition).placeY);
                placePositions.add(placePosition);
                currentDistance += distancePerPlace;
                if (--placesThisTick <= 0) break;
                item = this.slot.getItem();
                if (item != null && item.item.getID() == this.item.item.getID()) continue;
                this.attackerMob.endAttackHandler(false);
                break;
            }
        }
        return placePositions;
    }

    protected void runServerPlace(int placesThisTick, ArrayList<T> placePositions, Line2D playerPositionLine) {
        InventoryItem item = this.slot.getItem();
        if (item == null || item.item.getID() != this.item.item.getID()) {
            this.attackerMob.endAttackHandler(false);
            for (PlacePosition placePosition : placePositions) {
                this.onServerPlaceInvalid(item, placePosition, playerPositionLine);
            }
            this.slot.markDirty();
            return;
        }
        for (PlacePosition placePosition : placePositions) {
            if (placesThisTick > 0) {
                if (!this.placeServerItem(item, placePosition, playerPositionLine)) continue;
                ((PlayerMob)this.attackerMob).currentAttackLastPlacePosition = new Point(placePosition.placeX, placePosition.placeY);
                item = this.slot.getItem();
                if (item == null || item.item.getID() != this.item.item.getID()) {
                    this.attackerMob.endAttackHandler(false);
                    placesThisTick = 0;
                    this.slot.markDirty();
                    continue;
                }
                --placesThisTick;
                continue;
            }
            this.onServerPlaceInvalid(item, placePosition, playerPositionLine);
            this.slot.markDirty();
        }
    }

    @Override
    public void onUpdate() {
        InventoryItem item = this.slot.getItem();
        if (item == null || item.item.getID() != this.item.item.getID()) {
            this.attackerMob.endAttackHandler(false);
            return;
        }
        PlayerMob player = (PlayerMob)this.attackerMob;
        int placesThisTick = this.getAndUpdatePlacesThisTick(0);
        if (this.attackerMob.isClient()) {
            LinkedList<Object> placePositions;
            boolean forceSendUpdate;
            GameCamera camera = GlobalData.getCurrentState().getCamera();
            if (camera == null) {
                return;
            }
            Point nextPlayerPos = player.getPositionPoint();
            Point nextPlacePos = this.getNextClientLevelPos(player, camera);
            boolean bl = forceSendUpdate = nextPlacePos.x != this.lastPlaceX || nextPlacePos.y != this.lastPlaceY || nextPlayerPos.x != this.lastPlayerPositionX || nextPlayerPos.y != this.lastPlayerPositionY;
            if (placesThisTick > 0) {
                Line2D.Float playerPositionLine = null;
                if (this.lastPlayerPositionX != nextPlayerPos.x || this.lastPlayerPositionY != nextPlayerPos.y) {
                    playerPositionLine = new Line2D.Float(this.lastPlayerPositionX, this.lastPlayerPositionY, player.getX(), player.getY());
                }
                placePositions = this.runAndFindPlace(this.lastPlaceX, this.lastPlaceY, nextPlacePos.x, nextPlacePos.y, placesThisTick, playerPositionLine);
                this.lastPlaceX = nextPlacePos.x;
                this.lastPlaceY = nextPlacePos.y;
                this.lastPlayerPositionX = nextPlayerPos.x;
                this.lastPlayerPositionY = nextPlayerPos.y;
            } else {
                placePositions = new LinkedList();
            }
            if (forceSendUpdate || !placePositions.isEmpty()) {
                this.sendClientUpdatePacket(nextPlacePos.x, nextPlacePos.y, placePositions);
            }
        }
        this.checkAndShowAttack(this.lastPlaceX, this.lastPlaceY, item);
    }

    public void checkAndShowAttack(int targetX, int targetY, InventoryItem item) {
        long nextAttackCooldown = this.attackerMob.getNextAttackCooldown();
        if (nextAttackCooldown <= 0L) {
            this.showAttackAndSendAttacker(targetX, targetY, item);
        }
    }

    protected void showAttackAndSendAttacker(int targetX, int targetY, InventoryItem item) {
        this.attackerMob.showAttackAndSendAttacker(item, targetX, targetY, 0, this.seed);
    }

    public int getAndUpdatePlacesThisTick(int extraPlacesTolerance) {
        int placesThisTick;
        int placeCooldown = this.getPlaceCooldown();
        long currentTime = this.attackerMob.getLocalTime();
        long timeSinceLastCheck = currentTime - this.lastPlaceTime;
        int n = placesThisTick = placeCooldown <= 0 ? Integer.MAX_VALUE : (int)(timeSinceLastCheck / (long)placeCooldown) + extraPlacesTolerance;
        if (placeCooldown <= 0 || placesThisTick - extraPlacesTolerance > 0) {
            this.lastPlaceTime = Math.max(currentTime, this.lastPlaceTime);
        }
        return placesThisTick;
    }

    @Override
    public void onEndAttack(boolean bySelf) {
    }

    protected int getLineProgressPerCheck() {
        return 4;
    }

    protected abstract int getPlaceCooldown();

    protected abstract T placeItem(InventoryItem var1, int var2, int var3, Line2D var4, GNDItemMap var5);

    protected abstract boolean placeServerItem(InventoryItem var1, T var2, Line2D var3);

    protected abstract void onServerPlaceInvalid(InventoryItem var1, T var2, Line2D var3);

    protected abstract T createPlacePositionFromPacket(PacketReader var1);

    protected static class PlacePosition {
        public final int placeX;
        public final int placeY;
        public final GNDItemMap attackMapContent;

        public PlacePosition(int placeX, int placeY, GNDItemMap attackMapContent) {
            this.placeX = placeX;
            this.placeY = placeY;
            this.attackMapContent = attackMapContent;
        }

        public PlacePosition(PacketReader reader) {
            this.placeX = reader.getNextInt();
            this.placeY = reader.getNextInt();
            this.attackMapContent = new GNDItemMap(reader);
        }

        public void writePacket(PacketWriter writer) {
            writer.putNextInt(this.placeX);
            writer.putNextInt(this.placeY);
            this.attackMapContent.writePacket(writer);
        }
    }
}

