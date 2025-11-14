/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.TileEntity;
import necesse.entity.events.EntityEvent;
import necesse.entity.events.ObjectEntityEventRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.networkField.NetworkField;
import necesse.entity.objectEntity.ObjectEntityNetworkFieldRegistry;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.presets.PresetRotation;

public class ObjectEntity
extends TileEntity {
    public String type;
    public boolean shouldSave = true;
    private final boolean oeInventory;
    private final boolean oeUsers;
    private final ObjectEntityNetworkFieldRegistry networkFields = new ObjectEntityNetworkFieldRegistry(this);
    private final ObjectEntityEventRegistry events = new ObjectEntityEventRegistry(this);
    protected SoundPlayer ambientSoundPlayer;
    protected static final int defaultAmbienceFalloffDistance = 256;

    public ObjectEntity(Level level, String type, int tileX, int tileY) {
        super(level, tileX, tileY);
        this.type = type;
        this.oeInventory = this instanceof OEInventory;
        this.oeUsers = this instanceof OEUsers;
    }

    public void addSaveData(SaveData save) {
    }

    public void applyLoadData(LoadData save) {
    }

    public void addPresetSaveData(SaveData save) {
        this.addSaveData(save);
    }

    public void applyPresetLoadData(LoadData save, boolean mirrorX, boolean mirrorY, PresetRotation rotation) {
        this.applyLoadData(save);
    }

    public void setupContentPacket(PacketWriter writer) {
        this.networkFields.writeSpawnPacket(writer);
    }

    public void applyContentPacket(PacketReader reader) {
        this.networkFields.readSpawnPacket(reader);
    }

    public boolean shouldRequestPacket() {
        return true;
    }

    @Override
    public void init() {
        super.init();
        this.networkFields.closeRegistry();
        this.events.closeRegistry();
    }

    public void frameTick(float delta) {
    }

    @Override
    public void clientTick() {
        if (this.shouldPlayAmbientSound()) {
            this.playAmbientSound();
        }
        if (this.ambienceIsLooping() && this.ambientSoundPlayer != null) {
            this.ambientSoundPlayer.refreshLooping();
        }
    }

    public boolean shouldPlayAmbientSound() {
        return false;
    }

    public boolean ambienceIsLooping() {
        return true;
    }

    protected SoundSettings getAmbientSound() {
        return null;
    }

    protected void playAmbientSound() {
        if (this.ambientSoundPlayer == null || this.ambientSoundPlayer.isDone()) {
            SoundSettings svp = this.getAmbientSound();
            if (svp == null || svp.sounds == null) {
                return;
            }
            Point soundPos = new Point(this.tileX * 32 + 16, this.tileY * 32 + 16);
            GameObject thisObject = this.getObject();
            if (thisObject.isMultiTile()) {
                if (!thisObject.isMultiTileMaster()) {
                    return;
                }
                soundPos = thisObject.getMultiTile(this.getLevel().getObjectRotation(this.tileX, this.tileY)).getCenterLevelPos(this.tileX, this.tileY);
            }
            svp.setFallOffDistanceIfNotSet(256);
            this.ambientSoundPlayer = SoundManager.playSound(svp, SoundEffect.effect(soundPos.x, soundPos.y));
        }
    }

    @Override
    public void serverTick() {
        if (this.isServer()) {
            this.networkFields.tickSync();
        }
    }

    public ArrayList<InventoryItem> getDroppedItems() {
        ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        return list;
    }

    public void onMouseHover(PlayerMob perspective, boolean debug) {
    }

    public boolean implementsOEInventory() {
        return this.oeInventory;
    }

    public boolean implementsOEUsers() {
        return this.oeUsers;
    }

    public boolean shouldSave() {
        return this.shouldSave;
    }

    public GameObject getObject() {
        return this.getLevel().getObject(this.tileX, this.tileY);
    }

    public LevelObject getLevelObject() {
        return this.getLevel().getLevelObject(this.tileX, this.tileY);
    }

    public final void runNetworkFieldUpdate(PacketReader reader) {
        this.networkFields.readUpdatePacket(reader);
    }

    public <T extends NetworkField<?>> T registerNetworkField(T field) {
        return this.networkFields.registerField(field);
    }

    public final void runEvent(int id, PacketReader reader) {
        this.events.runEvent(id, reader);
    }

    public <T extends EntityEvent> T registerEvent(T ability) {
        return this.events.registerEvent(ability);
    }

    public void onObjectDestroyed(GameObject previousObject, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        if (this.ambientSoundPlayer != null) {
            this.ambientSoundPlayer.stop();
        }
    }

    public String toString() {
        return super.toString() + "{" + this.tileX + "x" + this.tileY + ", " + this.getHostString() + ", " + this.getObject().getDisplayName() + "}";
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
    }
}

