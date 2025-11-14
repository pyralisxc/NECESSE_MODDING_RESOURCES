/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.GameTileRange;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.maps.Level;

public class GlyphTrapObjectEntity
extends TrapObjectEntity {
    public final GameTileRange tileRange = new GameTileRange(1, new Point[0]);
    public boolean players = true;
    public boolean passiveMobs = true;
    public boolean hostileMobs = true;
    protected final Runnable onTrigger;
    protected float lastLightUpdatePercent;

    public GlyphTrapObjectEntity(Level level, int tileX, int tileY, Runnable onTrigger) {
        super(level, tileX, tileY, 10000L);
        this.onTrigger = onTrigger;
        if (!level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            this.passiveMobs = false;
            this.hostileMobs = false;
            this.players = true;
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.startCooldownTime = save.getLong("startCooldownTime", this.startCooldownTime);
        this.players = save.getBoolean("players", this.players);
        this.passiveMobs = save.getBoolean("passiveMobs", this.passiveMobs);
        this.hostileMobs = save.getBoolean("hostileMobs", this.hostileMobs);
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addLong("startCooldownTime", this.startCooldownTime);
        save.addBoolean("players", this.players);
        save.addBoolean("passiveMobs", this.passiveMobs);
        save.addBoolean("hostileMobs", this.hostileMobs);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextLong(this.startCooldownTime);
        writer.putNextBoolean(this.players);
        writer.putNextBoolean(this.passiveMobs);
        writer.putNextBoolean(this.hostileMobs);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.startCooldownTime = reader.getNextLong();
        this.players = reader.getNextBoolean();
        this.passiveMobs = reader.getNextBoolean();
        this.hostileMobs = reader.getNextBoolean();
    }

    @Override
    public boolean shouldRequestPacket() {
        return true;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        float cooldownPercent = this.getCooldownPercent();
        if (this.lastLightUpdatePercent != cooldownPercent) {
            this.lastLightUpdatePercent = cooldownPercent;
            this.getLevel().lightManager.updateStaticLight(this.tileX, this.tileY);
        }
    }

    @Override
    public void serverTick() {
        this.checkCollision();
    }

    protected void checkCollision() {
        Level level = this.getLevel();
        boolean foundAny = level.entityManager.mobs.getInRegionByTileRange(this.tileX, this.tileY, this.tileRange.maxRange).stream().anyMatch(m -> m.canLevelInteract() && !m.isFlying() && !m.isBoss() && (m.isHostile && this.hostileMobs || !m.isHostile && this.passiveMobs) && this.tileRange.isWithinRange(this.tileX, this.tileY, m.getTileX(), m.getTileY()));
        if (!foundAny && this.players && this.isServer()) {
            foundAny = level.entityManager.players.getInRegionByTileRange(this.tileX, this.tileY, this.tileRange.maxRange).stream().anyMatch(p -> (!p.isServerClient() || p.getServerClient().hasSpawned()) && p.canLevelInteract() && this.tileRange.isWithinRange(this.tileX, this.tileY, p.getTileX(), p.getTileY()));
        }
        if (foundAny) {
            this.onTrigger();
        }
    }

    public void onTrigger() {
        if (this.isClient() || this.onCooldown()) {
            return;
        }
        this.onTrigger.run();
        this.startCooldown();
        this.sendClientTriggerPacket();
    }

    @Override
    public void triggerTrap(int wireID, int dir) {
        super.triggerTrap(wireID, dir);
        if (this.isClient() || this.onCooldown()) {
            return;
        }
        if (this.otherWireActive(wireID)) {
            return;
        }
        this.onTrigger();
    }

    @Override
    public void onClientTrigger() {
        super.onClientTrigger();
        this.startCooldown();
    }
}

