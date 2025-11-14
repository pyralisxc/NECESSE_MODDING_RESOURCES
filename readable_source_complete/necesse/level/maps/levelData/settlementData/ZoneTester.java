/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

public interface ZoneTester {
    public boolean containsTile(int var1, int var2);

    default public ZoneTester and(ZoneTester other) {
        if (other == null) {
            return this;
        }
        return (tileX, tileY) -> this.containsTile(tileX, tileY) && other.containsTile(tileX, tileY);
    }

    default public ZoneTester or(ZoneTester other) {
        if (other == null) {
            return this;
        }
        return (tileX, tileY) -> this.containsTile(tileX, tileY) || other.containsTile(tileX, tileY);
    }
}

