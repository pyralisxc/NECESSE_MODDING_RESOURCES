/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

public interface ForestrySaplingObject {
    public String getForestryResultObjectStringID();

    default public boolean shouldForestryPlantAtTile(int tileX, int tileY) {
        return tileX % 2 == 0 && tileY % 2 == 0;
    }
}

