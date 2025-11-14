/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.DungeonExitObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class DungeonExitObject
extends GameObject {
    public GameTexture texture;

    public DungeonExitObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(53, 54, 59);
        this.lightLevel = 50;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/dungeonexit");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        final TextureDrawOptionsEnd drawOptions = this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).light(light).pos(drawX, drawY - this.texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(0, 0, 32, this.texture.getHeight()).light(light).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        ObjectEntity objectEntity;
        if (level.isServer() && player.isServerClient() && (objectEntity = level.entityManager.getObjectEntity(x, y)) instanceof PortalObjectEntity) {
            ((PortalObjectEntity)objectEntity).use(level.getServer(), player.getServerClient());
        }
        super.interact(level, x, y, player);
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
                DungeonExitObject.this.texture.initDraw().size(16, 32).draw(x - 8, y - 24);
            }

            @Override
            public GameTooltips getMapTooltips() {
                return new StringTooltips(DungeonExitObject.this.getDisplayName());
            }
        };
    }
}

