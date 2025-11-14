/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.util.HashSet;
import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.manager.MobDeathListenerEntityComponent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class HuntIncursionEvent
extends IncursionLevelEvent
implements MobDeathListenerEntityComponent,
MobBuffsEntityComponent,
LevelBuffsEntityComponent {
    public int progress;
    public int max;

    public HuntIncursionEvent() {
    }

    public HuntIncursionEvent(String bossStringID, int progress, int max) {
        super(bossStringID);
        this.bossStringID = bossStringID;
        this.progress = progress;
        this.max = max;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("progress", this.progress);
        save.addInt("max", this.max);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.progress = save.getInt("progress", this.progress, false);
        this.max = save.getInt("max", this.max, false);
    }

    @Override
    public void setupUpdatePacket(PacketWriter writer) {
        super.setupUpdatePacket(writer);
        writer.putNextInt(this.progress);
        writer.putNextInt(this.max);
    }

    @Override
    public void applyUpdatePacket(PacketReader reader) {
        super.applyUpdatePacket(reader);
        this.progress = reader.getNextInt();
        this.max = reader.getNextInt();
    }

    @Override
    public void onLevelMobDied(Mob mob, Attacker attacker, HashSet<Attacker> attackers) {
        if (mob.isHostile && mob.countKillStat()) {
            ++this.progress;
            this.isDirty = true;
        }
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers() {
        if (this.isDone || this.isFighting || this.bossPortalSpawned) {
            return Stream.of(new ModifierValue<Boolean>(LevelModifiers.ENEMIES_RETREATING, true));
        }
        return Stream.empty();
    }

    public float getPercentProgress() {
        return GameMath.limit((float)this.progress / (float)this.max, 0.0f, 1.0f);
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers(Mob mob) {
        if (this.isDone || this.isFighting || this.bossPortalSpawned) {
            if (mob.isPlayer) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f), 1000000));
            }
        } else {
            if (mob.isPlayer) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(1.0f + this.getPercentProgress())), new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD).min(150));
            }
            if (mob.isHostile) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.CHASER_RANGE, Float.valueOf(this.getPercentProgress())));
            }
        }
        return Stream.empty();
    }

    @Override
    public boolean isObjectiveDone() {
        return this.progress >= this.max;
    }

    @Override
    public int getObjectiveCurrent() {
        return this.progress;
    }

    @Override
    public int getObjectiveMax() {
        return this.max;
    }
}

