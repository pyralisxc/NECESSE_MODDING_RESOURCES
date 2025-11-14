/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.WorkstationDuo2Object;

class DemonicWorkstationDuo2Object
extends WorkstationDuo2Object {
    protected DemonicWorkstationDuo2Object() {
        this.mapColor = new Color(156, 51, 39);
        this.lightLevel = 100;
        this.lightHue = 270.0f;
        this.lightSat = 0.3f;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/demonicworkstationduo");
    }
}

