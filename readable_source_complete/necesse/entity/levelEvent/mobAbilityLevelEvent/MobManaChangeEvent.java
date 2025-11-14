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

public class MobManaChangeEvent
extends MobAbilityLevelEvent {
    private float finalMana;
    private float change;

    public MobManaChangeEvent() {
    }

    public MobManaChangeEvent(Mob owner, float finalMana, float change) {
        super(owner, GameRandom.globalRandom);
        if (change == 0.0f) {
            throw new IllegalArgumentException("Cannot send a mana change event with 0 change.");
        }
        this.finalMana = finalMana;
        this.change = change;
    }

    public MobManaChangeEvent(Mob owner, float change) {
        this(owner, Math.max(0.0f, owner.getMana() + change), change);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.finalMana = reader.getNextFloat();
        this.change = reader.getNextFloat();
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.finalMana);
        writer.putNextFloat(this.change);
    }

    @Override
    public void init() {
        super.init();
        if (this.owner != null) {
            this.owner.setManaHidden(this.finalMana);
            if (this.isClient() && Math.abs(this.change) >= 1.0f) {
                int changeInt = (int)this.change;
                this.level.hudManager.addElement(new DamageText(this.owner, changeInt, changeInt > 0 ? new Color(51, 133, 224) : new Color(133, 51, 224), GameRandom.globalRandom.getIntBetween(30, 40)));
            }
        }
        this.over();
    }
}

