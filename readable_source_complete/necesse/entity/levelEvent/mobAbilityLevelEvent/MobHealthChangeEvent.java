/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.level.maps.hudManager.floatText.DamageText;

public class MobHealthChangeEvent
extends MobAbilityLevelEvent {
    private int finalHealth;
    private int change;

    public MobHealthChangeEvent() {
    }

    public MobHealthChangeEvent(Mob owner, int finalHealth, int change) {
        super(owner, GameRandom.globalRandom);
        if (change == 0) {
            throw new IllegalArgumentException("Cannot send a health change event with 0 change.");
        }
        this.finalHealth = finalHealth;
        this.change = change;
    }

    public MobHealthChangeEvent(Mob owner, int change) {
        this(owner, Math.max(0, owner.getHealth() + change), change);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.finalHealth = reader.getNextInt();
        this.change = reader.getNextInt();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.finalHealth);
        writer.putNextInt(this.change);
    }

    @Override
    public void init() {
        super.init();
        if (this.owner != null) {
            this.owner.setHealthHidden(this.finalHealth);
            if (this.isClient()) {
                this.level.hudManager.addElement(new DamageText(this.owner, this.change, this.change > 0 ? Color.GREEN : Color.RED, GameRandom.globalRandom.getIntBetween(30, 40)));
            }
        }
        this.over();
    }
}

