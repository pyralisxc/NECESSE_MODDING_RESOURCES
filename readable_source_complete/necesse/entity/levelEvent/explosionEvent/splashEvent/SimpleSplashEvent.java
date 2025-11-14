/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.levelEvent.explosionEvent.splashEvent.BuffSplashEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.ThemeColorRange;
import necesse.gfx.ThemeColorRegistry;

public class SimpleSplashEvent
extends BuffSplashEvent {
    protected int duration;
    protected Buff buff;
    public ThemeColorRange splashColorRange;

    public SimpleSplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(0.0f), 0.0f, null, null, 5000, ThemeColorRegistry.TEST_COLOR);
    }

    public SimpleSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner, Buff buff, int duration, ThemeColorRange splashColorRange) {
        super(x, y, range, damage, toolTier, owner);
        this.knockback = 0;
        this.duration = duration;
        this.buff = buff;
        this.splashColorRange = splashColorRange;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.buff.getID());
        writer.putNextShortUnsigned(this.splashColorRange.getID());
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int buffID = reader.getNextShortUnsigned();
        this.buff = BuffRegistry.getBuff(buffID);
        int crID = reader.getNextShortUnsigned();
        this.splashColorRange = ThemeColorRegistry.getColorByID(crID);
    }

    @Override
    protected ActiveBuff getBuff(Mob buffOwner) {
        return new ActiveBuff(this.buff, buffOwner, this.duration, (Attacker)this);
    }

    @Override
    protected Color getInnerSplashColor() {
        return this.splashColorRange.getRandomColor();
    }
}

