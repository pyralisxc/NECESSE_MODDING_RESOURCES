/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DungeonExitObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.DungeonExitObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrialExitObject
extends DungeonExitObject {
    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/trialexit");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTile tile = level.getTile(tileX, tileY);
        Color color = tile.getMapColor(level, tileX, tileY);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(0, 0, 32, 64).color(color).light(light).pos(drawX, drawY - 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new DungeonExitObjectEntity(level, x, y){

            @Override
            public boolean shouldDrawOnMap() {
                return true;
            }

            @Override
            public Rectangle drawOnMapBox(double tileScale, boolean isMinimap) {
                return new Rectangle(-8, -24, 16, 32);
            }

            @Override
            public void drawOnMap(TickManager tickManager, Client client, int x, int y, double tileScale, Rectangle drawBounds, boolean isMinimap) {
                GameTile tile = this.getLevel().getTile(this.tileX, this.tileY);
                Color color = tile.getMapColor(this.getLevel(), this.tileX, this.tileY);
                TrialExitObject.this.texture.initDraw().size(16, 32).color(color).draw(x - 8, y - 24);
            }

            @Override
            public GameTooltips getMapTooltips() {
                return new StringTooltips(TrialExitObject.this.getDisplayName());
            }
        };
    }
}

