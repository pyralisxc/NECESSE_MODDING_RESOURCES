/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.SaplingObject;

public class TreeSaplingObject
extends SaplingObject
implements ForestrySaplingObject {
    public TreeSaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient, String ... additionalValidTiles) {
        super(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient, additionalValidTiles);
    }

    public TreeSaplingObject(String textureName, String resultObjectStringID, int minGrowTimeInSeconds, int maxGrowTimeInSeconds, boolean addAnySaplingIngredient) {
        super(textureName, resultObjectStringID, minGrowTimeInSeconds, maxGrowTimeInSeconds, addAnySaplingIngredient, new String[0]);
    }

    @Override
    public String getForestryResultObjectStringID() {
        return this.resultObjectStringID;
    }
}

