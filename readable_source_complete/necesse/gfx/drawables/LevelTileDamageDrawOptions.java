/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.DamagedObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class LevelTileDamageDrawOptions {
    protected SharedTextureDrawOptions list = new SharedTextureDrawOptions(GameResources.tileDamageOverlay);
    protected int sprites = GameResources.tileDamageOverlay.getWidth() / 32;

    public void addDamage(TickManager tickManager, GameTile tile, Level level, int tileX, int tileY, GameCamera camera) {
        if (!tile.drawDamage) {
            return;
        }
        DamagedObjectEntity damagedEntity = level.entityManager.getDamagedObjectEntity(tileX, tileY);
        if (damagedEntity != null && damagedEntity.tileDamage > 0) {
            float damagePercent = (float)damagedEntity.tileDamage / (float)tile.tileHealth;
            int sprite = Math.min((int)(damagePercent * (float)this.sprites), this.sprites - 1);
            this.list.addSprite(sprite, 0, 32).pos(camera.getTileDrawX(tileX), camera.getTileDrawY(tileY));
        }
    }

    public void draw() {
        this.list.draw();
    }
}

