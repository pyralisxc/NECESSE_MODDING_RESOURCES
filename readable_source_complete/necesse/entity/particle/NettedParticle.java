/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class NettedParticle
extends Particle {
    protected Mob target;

    public NettedParticle(Mob target, long lifeTime) {
        super(target.getLevel(), target.x, target.y, lifeTime);
        this.target = target;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.target != null && this.target.removed() || this.removed()) {
            this.remove();
            return;
        }
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        Rectangle selectBox = this.target.getSelectBox();
        int drawX = camera.getDrawX(selectBox.x);
        int drawY = camera.getDrawY(selectBox.y + selectBox.height / 2 - 8);
        TextureDrawOptionsEnd options = GameResources.nettedDebuffNet.initDraw().light(light).pos(drawX, drawY);
        topList.add(t -> options.draw());
    }
}

