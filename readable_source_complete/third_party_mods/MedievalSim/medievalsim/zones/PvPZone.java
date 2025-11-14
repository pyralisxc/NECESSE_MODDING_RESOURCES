/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.util.PointHashSet
 *  necesse.level.maps.Level
 */
package medievalsim.zones;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import medievalsim.util.RuntimeConstants;
import medievalsim.zones.AdminZone;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZoneBarrierManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.Level;

public class PvPZone
extends AdminZone {
    public static final String TYPE_ID = "pvp";
    public float damageMultiplier = RuntimeConstants.Zones.getDefaultDamageMultiplier();
    public int combatLockSeconds = RuntimeConstants.Zones.getDefaultCombatLockSeconds();
    public float dotDamageMultiplier = 1.0f;
    public float dotIntervalMultiplier = 1.0f;

    public PvPZone() {
    }

    public PvPZone(int uniqueID, String name, long creatorAuth, int colorHue) {
        super(uniqueID, name, creatorAuth, colorHue);
    }

    public PvPZone(int uniqueID, String name, long creatorAuth, int colorHue, float damageMultiplier, int combatLockSeconds) {
        super(uniqueID, name, creatorAuth, colorHue);
        this.damageMultiplier = damageMultiplier;
        this.combatLockSeconds = combatLockSeconds;
    }

    public static String formatDamagePercent(float damageMultiplier) {
        float percent = damageMultiplier * 100.0f;
        return String.format(Locale.ROOT, "%.1f%%", Float.valueOf(percent));
    }

    @Override
    public String getTypeID() {
        return TYPE_ID;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addFloat("damageMultiplier", this.damageMultiplier);
        save.addFloat("dotDamageMultiplier", this.dotDamageMultiplier);
        save.addFloat("dotIntervalMultiplier", this.dotIntervalMultiplier);
        save.addInt("combatLockSeconds", this.combatLockSeconds);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.damageMultiplier = save.getFloat("damageMultiplier", 0.05f);
        this.dotDamageMultiplier = save.getFloat("dotDamageMultiplier", 1.0f);
        this.dotIntervalMultiplier = save.getFloat("dotIntervalMultiplier", 1.0f);
        this.combatLockSeconds = save.getInt("combatLockSeconds", 3);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        writer.putNextFloat(this.damageMultiplier);
        writer.putNextFloat(this.dotDamageMultiplier);
        writer.putNextFloat(this.dotIntervalMultiplier);
        writer.putNextInt(this.combatLockSeconds);
    }

    @Override
    public void readPacket(PacketReader reader) {
        super.readPacket(reader);
        this.damageMultiplier = reader.getNextFloat();
        this.dotDamageMultiplier = reader.getNextFloat();
        this.dotIntervalMultiplier = reader.getNextFloat();
        this.combatLockSeconds = reader.getNextInt();
    }

    public void createBarriers(Level level) {
        if (level != null && level.isServer()) {
            PvPZoneBarrierManager.createBarrier(level, this);
        }
    }

    public void updateBarriers(Level level) {
        if (level != null && level.isServer()) {
            PvPZoneBarrierManager.updateBarrier(level, this);
        }
    }

    public void removeBarriers(Level level) {
        if (level != null && level.isServer()) {
            PvPZoneBarrierManager.removeBarrier(level, this);
        }
    }

    @Override
    public boolean expand(Rectangle rectangle) {
        boolean changed = super.expand(rectangle);
        return changed;
    }

    @Override
    public boolean shrink(Rectangle rectangle) {
        boolean changed = super.shrink(rectangle);
        return changed;
    }

    public boolean expandAndUpdateBarriers(Level level, Rectangle rectangle) {
        AdminZonesLevelData data;
        HashMap<Integer, Collection<Point>> oldEdgesSnapshot = new HashMap<Integer, Collection<Point>>();
        try {
            PointHashSet edge = this.zoning.getEdgeTiles();
            if (edge != null) {
                ArrayList<Point> l = new ArrayList<Point>();
                for (Object o : edge) {
                    if (!(o instanceof Point)) continue;
                    l.add(new Point((Point)o));
                }
                oldEdgesSnapshot.put(this.uniqueID, l);
            }
        }
        catch (Exception edge) {
            // empty catch block
        }
        boolean changed = this.expand(rectangle);
        if (changed && level != null && level.isServer() && (data = AdminZonesLevelData.getZoneData(level, false)) != null) {
            data.resolveAfterZoneChange(this, level, null, false, oldEdgesSnapshot);
        }
        return changed;
    }

    public boolean shrinkAndUpdateBarriers(Level level, Rectangle rectangle) {
        AdminZonesLevelData data;
        HashMap<Integer, Collection<Point>> oldEdgesSnapshot = new HashMap<Integer, Collection<Point>>();
        try {
            PointHashSet edge = this.zoning.getEdgeTiles();
            if (edge != null) {
                ArrayList<Point> l = new ArrayList<Point>();
                for (Object o : edge) {
                    if (!(o instanceof Point)) continue;
                    l.add(new Point((Point)o));
                }
                oldEdgesSnapshot.put(this.uniqueID, l);
            }
        }
        catch (Exception edge) {
            // empty catch block
        }
        boolean changed = this.shrink(rectangle);
        if (changed && level != null && level.isServer() && (data = AdminZonesLevelData.getZoneData(level, false)) != null) {
            data.resolveAfterZoneChange(this, level, null, false, oldEdgesSnapshot);
        }
        return changed;
    }
}

