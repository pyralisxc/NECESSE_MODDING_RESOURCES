/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.LandscapingStation2Object;

class TungstenLandscapingStation2Object
extends LandscapingStation2Object {
    protected TungstenLandscapingStation2Object() {
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/tungstenlandscapingstation");
    }
}

