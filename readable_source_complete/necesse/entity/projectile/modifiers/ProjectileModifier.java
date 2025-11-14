/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.modifiers;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IDData;
import necesse.engine.registries.ProjectileModifierRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class ProjectileModifier {
    public final IDData idData = new IDData();
    public Projectile projectile;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public ProjectileModifier() {
        ProjectileModifierRegistry.instance.applyIDData(this.getClass(), this.idData);
    }

    public void setupSpawnPacket(PacketWriter writer) {
    }

    public void applySpawnPacket(PacketReader reader) {
    }

    public void setupPositionPacket(PacketWriter writer) {
    }

    public void applyPositionPacket(PacketReader reader) {
    }

    public void init() {
    }

    public void initChildProjectile(Projectile projectile, float childStrength, int childCount) {
    }

    public void postInit() {
    }

    public void onMoveTick(Point2D.Float startPos, double movedDist) {
    }

    public boolean onHit(Mob mob, LevelObjectHit object, float x, float y, boolean fromPacket, ServerClient packetSubmitter) {
        return false;
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
    }

    public Level getLevel() {
        return this.projectile.getLevel();
    }
}

