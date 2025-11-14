/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class LevelObject {
    public final GameObject object;
    public final byte rotation;
    public final Level level;
    public final int layerID;
    public final int tileX;
    public final int tileY;
    public final boolean isPlayerPlaced;

    private LevelObject(Level level, int layerID, int tileX, int tileY, GameObject object, byte rotation, boolean isPlayerPlaced) {
        this.object = object;
        this.rotation = rotation;
        this.level = level;
        this.layerID = layerID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.isPlayerPlaced = isPlayerPlaced;
    }

    public LevelObject(Level level, int layerID, int tileX, int tileY) {
        this(level, layerID, tileX, tileY, level.getObject(layerID, tileX, tileY), level.getObjectRotation(layerID, tileX, tileY), level.objectLayer.isPlayerPlaced(layerID, tileX, tileY));
    }

    public LevelObject(Level level, int tileX, int tileY) {
        this(level, 0, tileX, tileY);
    }

    public static LevelObject custom(Level level, int layerID, int tileX, int tileY, GameObject object, byte rotation, boolean isPlayerPlaced) {
        return new LevelObject(level, layerID, tileX, tileY, object, rotation, isPlayerPlaced);
    }

    public MultiTile getMultiTile() {
        return this.object.getMultiTile(this.level, this.layerID, this.tileX, this.tileY);
    }

    public Optional<LevelObject> getMasterLevelObject() {
        return this.getMultiTile().getMasterLevelObject(this.level, this.layerID, this.tileX, this.tileY);
    }

    public boolean hasChanged() {
        return this.level.getObjectID(this.tileX, this.tileY) != this.object.getID() || this.level.getObjectRotation(this.tileX, this.tileY) != this.rotation;
    }

    public ObjectEntity getObjectEntity() {
        return this.level.entityManager.getObjectEntity(this.tileX, this.tileY);
    }

    public void drawMultiTilePreview(byte rotation, float alpha, PlayerMob player, GameCamera camera) {
        this.object.drawMultiTilePreview(this.level, this.tileX, this.tileY, rotation, alpha, player, camera);
    }

    public void drawPreview(byte rotation, float alpha, PlayerMob player, GameCamera camera) {
        this.object.drawPreview(this.level, this.tileX, this.tileY, rotation, alpha, player, camera);
    }

    public List<Rectangle> getCollisions(int rotation) {
        return this.object.getCollisions(this.level, this.tileX, this.tileY, rotation);
    }

    public List<ObjectHoverHitbox> getHoverHitboxes() {
        return this.object.getHoverHitboxes(this.level, this.layerID, this.tileX, this.tileY);
    }

    public int getHitboxLayerPriority() {
        return this.object.getHitboxLayerPriority(this.level, this.layerID, this.tileX, this.tileY);
    }

    public List<Rectangle> getProjectileCollisions(int rotation) {
        return this.object.getProjectileCollisions(this.level, this.tileX, this.tileY, rotation);
    }

    public List<Rectangle> getAttackThroughCollisions() {
        return this.object.getAttackThroughCollisions(this.level, this.tileX, this.tileY);
    }

    public int getLightLevel() {
        return this.object.getLightLevel(this.level, this.layerID, this.tileX, this.tileY);
    }

    public GameLight getLight() {
        return this.object.getLight(this.level, this.layerID, this.tileX, this.tileY);
    }

    public String canPlace(int rotation, boolean byPlayer) {
        return this.object.canPlace(this.level, this.tileX, this.tileY, rotation, byPlayer);
    }

    public void attemptPlace(PlayerMob player, String message) {
        this.object.attemptPlace(this.level, this.tileX, this.tileY, player, message);
    }

    public boolean checkPlaceCollision(int rotation, boolean checkClients) {
        return this.object.checkPlaceCollision(this.level, this.tileX, this.tileY, rotation, checkClients);
    }

    public boolean isValid() {
        return this.object.isValid(this.level, 0, this.tileX, this.tileY);
    }

    public void checkAround() {
        this.object.checkAround(this.level, this.tileX, this.tileY);
    }

    public boolean isSolid() {
        return this.object.isSolid(this.level, this.tileX, this.tileY);
    }

    public boolean canInteract(PlayerMob player) {
        return this.object.canInteract(this.level, this.tileX, this.tileY, player);
    }

    public String getInteractTip(PlayerMob perspective, boolean debug) {
        return this.object.getInteractTip(this.level, this.tileX, this.tileY, perspective, debug);
    }

    public void onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        this.object.onMouseHover(this.level, this.tileX, this.tileY, camera, perspective, debug);
    }

    public boolean isInInteractRange(PlayerMob perspective) {
        return this.object.isInInteractRange(this.level, this.tileX, this.tileY, perspective);
    }

    public void interact(PlayerMob player) {
        this.object.interact(this.level, this.tileX, this.tileY, player);
    }

    public ObjectEntity getNewObjectEntity() {
        return this.object.getNewObjectEntity(this.level, this.tileX, this.tileY);
    }

    public ObjectEntity getCurrentObjectEntity() {
        return this.object.getCurrentObjectEntity(this.level, this.tileX, this.tileY);
    }

    public <T extends ObjectEntity> T getCurrentObjectEntity(Class<T> expectedClass) {
        return this.object.getCurrentObjectEntity(this.level, this.tileX, this.tileY, expectedClass);
    }

    public void onObjectDestroyed(Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        this.object.onDestroyed(this.level, this.layerID, this.tileX, this.tileY, attacker, client, itemsDropped);
    }

    public void tick(Mob mob) {
        this.object.tick(mob, this.level, this.tileX, this.tileY);
    }

    public void tick() {
        this.object.tick(this.level, this.tileX, this.tileY);
    }

    public boolean isWireActive(int wireID) {
        return this.object.isWireActive(this.level, this.tileX, this.tileY, wireID);
    }

    public void attackThrough(GameDamage damage, Attacker attacker) {
        this.object.attackThrough(this.level, this.tileX, this.tileY, damage, attacker);
    }

    public void attackThrough(GameDamage damage) {
        this.object.attackThrough(this.level, this.tileX, this.tileY, damage);
    }

    public GameTooltips getMapTooltips() {
        return this.object.getMapTooltips(this.level, this.tileX, this.tileY);
    }

    public boolean isStillPresent(boolean checkRotation) {
        return this.level.getObjectID(this.tileX, this.tileY) == this.object.getID() && (!checkRotation || this.level.getObjectRotation(this.tileX, this.tileY) == this.rotation);
    }

    public String toString() {
        return super.toString() + "{" + this.tileX + "x" + this.tileY + ", " + this.level.getHostString() + ", " + this.object.getDisplayName() + "}";
    }
}

