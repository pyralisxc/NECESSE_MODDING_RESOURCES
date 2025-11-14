/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.buffs.staticBuffs.ShownCooldownBuff
 *  necesse.gfx.gameTexture.GameTexture
 */
package aphorea.buffs;

import java.io.FileNotFoundException;
import necesse.entity.mobs.buffs.staticBuffs.ShownCooldownBuff;
import necesse.gfx.gameTexture.GameTexture;

public class AphShownBuff
extends ShownCooldownBuff {
    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw((String)("buffs/" + this.getStringID()));
        }
        catch (FileNotFoundException var2) {
            this.iconTexture = GameTexture.fromFile((String)"buffs/positive");
        }
    }
}

