/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CarpentersBench2Object;

class TungstenCarpentersBench2Object
extends CarpentersBench2Object {
    protected TungstenCarpentersBench2Object() {
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/tungstencarpentersbench");
    }
}

