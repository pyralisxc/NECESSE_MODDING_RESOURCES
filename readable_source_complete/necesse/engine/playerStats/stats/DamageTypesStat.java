/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats.stats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.GameStat;
import necesse.engine.playerStats.GameStats;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.gameDamageType.DamageType;

public class DamageTypesStat
extends GameStat {
    protected HashSet<DamageType> dirtyTypes = new HashSet();
    protected HashMap<DamageType, Integer> damage = new HashMap();

    public DamageTypesStat(EmptyStats parent, String stringID) {
        super(parent, stringID);
    }

    @Override
    public void clean() {
        super.clean();
        this.dirtyTypes.clear();
    }

    protected void setDamage(DamageType type, int amount, boolean updateSteam) {
        int prevStat = this.damage.getOrDefault(type, 0);
        if (prevStat == amount) {
            return;
        }
        this.damage.put(type, amount);
        if (updateSteam) {
            this.updatePlatform(type);
        }
        this.dirtyTypes.add(type);
        this.markDirty();
    }

    public void addDamage(DamageType type, int damage) {
        if (this.parent.mode == EmptyStats.Mode.READ_ONLY) {
            throw new IllegalStateException("Cannot set read only stats");
        }
        this.setDamage(type, this.damage.getOrDefault(type, 0) + damage, true);
    }

    public int getDamage(DamageType type) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.damage.getOrDefault(type, 0);
    }

    public void forEach(BiConsumer<DamageType, Integer> action) {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        this.damage.forEach(action);
    }

    public Stream<Map.Entry<DamageType, Integer>> streamEach() {
        if (this.parent.mode == EmptyStats.Mode.WRITE_ONLY) {
            throw new IllegalStateException("Cannot get write only stats");
        }
        return this.damage.entrySet().stream();
    }

    @Override
    public void combine(GameStat stat) {
        if (stat instanceof DamageTypesStat) {
            DamageTypesStat other = (DamageTypesStat)stat;
            other.damage.forEach((? super K t, ? super V v) -> this.setDamage((DamageType)t, this.damage.getOrDefault(t, 0) + v, true));
        }
    }

    @Override
    public void resetCombine() {
        this.damage.clear();
    }

    protected void updatePlatform(DamageType type) {
        if (!this.parent.controlAchievements) {
            return;
        }
        String statKey = type.getSteamStatKey();
        if (statKey != null) {
            Platform.getStatsProvider().setStat(statKey, this.damage.get(type), false);
        }
    }

    @Override
    public void loadStatFromPlatform(GameStats stats) {
        for (DamageType type : DamageTypeRegistry.getDamageTypes()) {
            String statKey = type.getSteamStatKey();
            if (statKey == null) continue;
            int stat = Math.max(stats.getStatByName(statKey, 0), this.damage.getOrDefault(type, 0));
            this.damage.put(type, stat);
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        this.damage.forEach((? super K type, ? super V damage) -> save.addInt(type.getStringID(), (int)damage));
    }

    @Override
    public void applyLoadData(LoadData save) {
        this.damage.clear();
        for (LoadData component : save.getLoadData()) {
            if (!component.isData()) continue;
            try {
                int amount = Integer.parseInt(component.getData());
                DamageType damageType = DamageTypeRegistry.getDamageType(component.getName());
                if (damageType == null) {
                    damageType = DamageTypeRegistry.getDamageType(component.getName().toLowerCase());
                }
                if (damageType == null) continue;
                this.setDamage(damageType, amount, false);
            }
            catch (NumberFormatException e) {
                GameLog.warn.println("Could not load damage types stat number: " + component.getData());
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.damage.size());
        this.damage.forEach((? super K type, ? super V amount) -> {
            writer.putNextShortUnsigned(type.getID());
            writer.putNextInt((int)amount);
        });
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        this.damage.clear();
        this.dirtyTypes.clear();
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int typeID = reader.getNextShortUnsigned();
            this.setDamage(DamageTypeRegistry.getDamageType(typeID), reader.getNextInt(), true);
        }
    }

    @Override
    public void setupDirtyPacket(PacketWriter writer) {
        writer.putNextShortUnsigned(this.dirtyTypes.size());
        for (DamageType type : this.dirtyTypes) {
            writer.putNextShortUnsigned(type.getID());
            writer.putNextInt(this.getDamage(type));
        }
    }

    @Override
    public void applyDirtyPacket(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        for (int i = 0; i < amount; ++i) {
            int typeID = reader.getNextShortUnsigned();
            this.setDamage(DamageTypeRegistry.getDamageType(typeID), reader.getNextInt(), true);
        }
    }
}

