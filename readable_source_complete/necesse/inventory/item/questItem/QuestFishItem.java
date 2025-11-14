/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.questItem;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.particle.CirclingFishParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.matItem.FishItemInterface;
import necesse.inventory.item.questItem.QuestItem;
import necesse.level.maps.Level;

public class QuestFishItem
extends QuestItem
implements FishItemInterface {
    public GameTexture circlingFishTexture;

    public QuestFishItem(int stackSize, GameMessage obtainTip) {
        super(stackSize, obtainTip);
    }

    public QuestFishItem(GameMessage obtainTip) {
        super(obtainTip);
    }

    public QuestFishItem() {
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.circlingFishTexture = GameTexture.fromFile("particles/circlingfish");
    }

    @Override
    public Particle getParticle(Level level, int x, int y, int lifeTime) {
        return new CirclingFishParticle(level, (float)x, (float)y, lifeTime, this.circlingFishTexture, 60);
    }
}

