/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

public enum RegionType {
    OPEN(0, false, false),
    FENCE(0, false, true),
    FENCE_GATE(0, true, true),
    SOLID(0, false, true),
    DOOR(1, true, true),
    WALL(2, false, true),
    SUMMON_IGNORED(0, false, true);

    public final int roomInt;
    public final boolean isDoor;
    public final boolean isSolid;

    private RegionType(int roomInt, boolean isDoor, boolean isSolid) {
        this.roomInt = roomInt;
        this.isDoor = isDoor;
        this.isSolid = isSolid;
    }
}

