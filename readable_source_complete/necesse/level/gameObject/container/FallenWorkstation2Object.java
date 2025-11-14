/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.WorkstationDuo2Object;

class FallenWorkstation2Object
extends WorkstationDuo2Object {
    protected FallenWorkstation2Object() {
        this.mapColor = new Color(0, 107, 109);
        this.lightLevel = 100;
        this.lightHue = 220.0f;
        this.lightSat = 0.2f;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/fallenworkstation");
    }
}

