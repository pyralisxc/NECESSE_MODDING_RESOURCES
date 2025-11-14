/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class TrainingDummyObjectEntity
extends ObjectEntity {
    private final boolean isSnowman;
    private int dummyMobID = -1;

    public TrainingDummyObjectEntity(Level level, int x, int y, boolean snowman) {
        super(level, "trainingdummy", x, y);
        this.isSnowman = snowman;
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        if (this.dummyMobID == -1) {
            this.generateMobID();
        }
        writer.putNextInt(this.dummyMobID);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.dummyMobID = reader.getNextInt();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        TrainingDummyMob m = this.getMob();
        if (m != null) {
            m.keepAlive(this);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        TrainingDummyMob m = this.getMob();
        if (m == null) {
            m = this.generateMobID();
            this.markDirty();
        }
        m.keepAlive(this);
    }

    private TrainingDummyMob generateMobID() {
        TrainingDummyMob lastMob = this.getMob();
        if (lastMob != null) {
            lastMob.remove();
        }
        TrainingDummyMob m = new TrainingDummyMob(this.isSnowman);
        this.getLevel().entityManager.addMob(m, this.tileX * 32 + 16, this.tileY * 32 + 16);
        this.dummyMobID = m.getUniqueID();
        return m;
    }

    private TrainingDummyMob getMob() {
        if (this.dummyMobID == -1) {
            return null;
        }
        Mob m = this.getLevel().entityManager.mobs.get(this.dummyMobID, false);
        if (m != null) {
            return (TrainingDummyMob)m;
        }
        return null;
    }

    @Override
    public void remove() {
        super.remove();
        TrainingDummyMob m = this.getMob();
        if (m != null) {
            m.remove();
        }
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            GameTooltipManager.addTooltip(new StringTooltips("MobID: " + this.dummyMobID), TooltipLocation.INTERACT_FOCUS);
        }
    }
}

