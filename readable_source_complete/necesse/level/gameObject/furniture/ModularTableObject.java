/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.DecorationHolderInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.TorchHolderInterface;
import necesse.level.gameObject.furniture.TableObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ModularTableObject
extends TableObject
implements TorchHolderInterface,
DecorationHolderInterface {
    protected String textureName;
    public ObjectDamagedTextureArray texture;

    public ModularTableObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(4, 4, 24, 24), mapColor);
        this.textureName = textureName;
        this.toolType = toolType;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -10, 32, 42);
        this.replaceRotations = false;
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    public ModularTableObject(String textureName, Color mapColor, String ... category) {
        this(textureName, ToolType.ALL, mapColor, category);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public DecorDrawOffset getTorchDrawOffset(Level level, int tileX, int tileY) {
        return new DecorDrawOffset(0, -18, 20, true);
    }

    @Override
    public DecorDrawOffset getDecorationDrawOffset(Level level, int tileX, int tileY, GameObject decoration) {
        return new DecorDrawOffset(0, -18, 20, true);
    }

    @Override
    public boolean canPlaceDecoration(Level level, int tileX, int tileY) {
        return !this.isTilePlaceOccupied(level, ObjectLayerRegistry.FENCE_AND_TABLE_DECOR, tileX, tileY, true);
    }

    @Override
    public Dimension getMaxDecorationSize(Level level, int tileX, int tileY) {
        return new Dimension(28, 28);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - 14;
        GameObject[] adj = level.getAdjacentObjects(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final ArrayList<TextureDrawOptionsEnd> draws = new ArrayList<TextureDrawOptionsEnd>();
        if (this.same(adj[1]) && this.same(adj[3])) {
            if (!this.same(adj[0])) {
                draws.add(texture.initDraw().sprite(2, 0, 16).light(light).pos(drawX, drawY));
            } else {
                draws.add(texture.initDraw().sprite(0, 2, 16).light(light).pos(drawX, drawY));
            }
        } else if (!this.same(adj[1]) && this.same(adj[3])) {
            draws.add(texture.initDraw().sprite(2, 2, 16).light(light).pos(drawX, drawY));
        } else if (this.same(adj[1]) && !this.same(adj[3])) {
            if (this.same(adj[0])) {
                draws.add(texture.initDraw().sprite(4, 0, 16).light(light).pos(drawX, drawY));
            } else {
                draws.add(texture.initDraw().sprite(4, 1, 16).light(light).pos(drawX, drawY));
            }
        } else {
            draws.add(texture.initDraw().sprite(0, 0, 16).light(light).pos(drawX, drawY));
        }
        if (this.same(adj[1]) && this.same(adj[4])) {
            if (!this.same(adj[2])) {
                draws.add(texture.initDraw().sprite(3, 0, 16).light(light).pos(drawX + 16, drawY));
            } else {
                draws.add(texture.initDraw().sprite(1, 2, 16).light(light).pos(drawX + 16, drawY));
            }
        } else if (!this.same(adj[1]) && this.same(adj[4])) {
            draws.add(texture.initDraw().sprite(3, 2, 16).light(light).pos(drawX + 16, drawY));
        } else if (this.same(adj[1]) && !this.same(adj[4])) {
            if (this.same(adj[2])) {
                draws.add(texture.initDraw().sprite(5, 0, 16).light(light).pos(drawX + 16, drawY));
            } else {
                draws.add(texture.initDraw().sprite(5, 1, 16).light(light).pos(drawX + 16, drawY));
            }
        } else {
            draws.add(texture.initDraw().sprite(1, 0, 16).light(light).pos(drawX + 16, drawY));
        }
        if (this.same(adj[6]) && this.same(adj[3])) {
            if (!this.same(adj[5])) {
                draws.add(texture.initDraw().sprite(2, 1, 16).light(light).pos(drawX, drawY + 16));
            } else {
                draws.add(texture.initDraw().sprite(0, 3, 16).light(light).pos(drawX, drawY + 16));
            }
        } else if (!this.same(adj[6]) && this.same(adj[3])) {
            draws.add(texture.initDraw().sprite(2, 3, 16).light(light).pos(drawX, drawY + 16));
        } else if (this.same(adj[6]) && !this.same(adj[3])) {
            draws.add(texture.initDraw().sprite(4, 1, 16).light(light).pos(drawX, drawY + 16));
        } else {
            draws.add(texture.initDraw().sprite(0, 1, 16).light(light).pos(drawX, drawY + 16));
        }
        if (this.same(adj[6]) && this.same(adj[4])) {
            if (!this.same(adj[7])) {
                draws.add(texture.initDraw().sprite(3, 1, 16).light(light).pos(drawX + 16, drawY + 16));
            } else {
                draws.add(texture.initDraw().sprite(1, 3, 16).light(light).pos(drawX + 16, drawY + 16));
            }
        } else if (!this.same(adj[6]) && this.same(adj[4])) {
            draws.add(texture.initDraw().sprite(3, 3, 16).light(light).pos(drawX + 16, drawY + 16));
        } else if (this.same(adj[6]) && !this.same(adj[4])) {
            draws.add(texture.initDraw().sprite(5, 1, 16).light(light).pos(drawX + 16, drawY + 16));
        } else {
            draws.add(texture.initDraw().sprite(1, 1, 16).light(light).pos(drawX + 16, drawY + 16));
        }
        if (!this.same(adj[6])) {
            boolean sameLeft = this.same(adj[3]);
            boolean sameRight = this.same(adj[4]);
            if (sameLeft && sameRight) {
                draws.add(texture.initDraw().sprite(2, 3, 32, 16).light(light).pos(drawX, drawY + 16 + 10));
            } else if (sameLeft) {
                draws.add(texture.initDraw().sprite(4, 3, 16).light(light).pos(drawX, drawY + 16 + 10));
                draws.add(texture.initDraw().sprite(5, 2, 16).light(light).pos(drawX + 16, drawY + 16 + 10));
            } else if (sameRight) {
                draws.add(texture.initDraw().sprite(4, 2, 16).light(light).pos(drawX, drawY + 16 + 10));
                draws.add(texture.initDraw().sprite(5, 3, 16).light(light).pos(drawX + 16, drawY + 16 + 10));
            } else {
                draws.add(texture.initDraw().sprite(2, 2, 32, 16).light(light).pos(drawX, drawY + 16 + 10));
            }
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.forEach(TextureDrawOptions::draw);
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - 14;
        GameObject[] adj = level.getAdjacentObjects(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        if (this.same(adj[1]) && this.same(adj[3])) {
            if (!this.same(adj[0])) {
                texture.initDraw().sprite(2, 0, 16).alpha(alpha).draw(drawX, drawY);
            } else {
                texture.initDraw().sprite(0, 2, 16).alpha(alpha).draw(drawX, drawY);
            }
        } else if (!this.same(adj[1]) && this.same(adj[3])) {
            texture.initDraw().sprite(2, 2, 16).alpha(alpha).draw(drawX, drawY);
        } else if (this.same(adj[1]) && !this.same(adj[3])) {
            if (this.same(adj[0])) {
                texture.initDraw().sprite(4, 0, 16).alpha(alpha).draw(drawX, drawY);
            } else {
                texture.initDraw().sprite(4, 1, 16).alpha(alpha).draw(drawX, drawY);
            }
        } else {
            texture.initDraw().sprite(0, 0, 16).alpha(alpha).draw(drawX, drawY);
        }
        if (this.same(adj[1]) && this.same(adj[4])) {
            if (!this.same(adj[2])) {
                texture.initDraw().sprite(3, 0, 16).alpha(alpha).draw(drawX + 16, drawY);
            } else {
                texture.initDraw().sprite(1, 2, 16).alpha(alpha).draw(drawX + 16, drawY);
            }
        } else if (!this.same(adj[1]) && this.same(adj[4])) {
            texture.initDraw().sprite(3, 2, 16).alpha(alpha).draw(drawX + 16, drawY);
        } else if (this.same(adj[1]) && !this.same(adj[4])) {
            if (this.same(adj[2])) {
                texture.initDraw().sprite(5, 0, 16).alpha(alpha).draw(drawX + 16, drawY);
            } else {
                texture.initDraw().sprite(5, 1, 16).alpha(alpha).draw(drawX + 16, drawY);
            }
        } else {
            texture.initDraw().sprite(1, 0, 16).alpha(alpha).draw(drawX + 16, drawY);
        }
        if (this.same(adj[6]) && this.same(adj[3])) {
            if (!this.same(adj[5])) {
                texture.initDraw().sprite(2, 1, 16).alpha(alpha).draw(drawX, drawY + 16);
            } else {
                texture.initDraw().sprite(0, 3, 16).alpha(alpha).draw(drawX, drawY + 16);
            }
        } else if (!this.same(adj[6]) && this.same(adj[3])) {
            texture.initDraw().sprite(2, 3, 16).alpha(alpha).draw(drawX, drawY + 16);
        } else if (this.same(adj[6]) && !this.same(adj[3])) {
            texture.initDraw().sprite(4, 1, 16).alpha(alpha).draw(drawX, drawY + 16);
        } else {
            texture.initDraw().sprite(0, 1, 16).alpha(alpha).draw(drawX, drawY + 16);
        }
        if (this.same(adj[6]) && this.same(adj[4])) {
            if (!this.same(adj[7])) {
                texture.initDraw().sprite(3, 1, 16).alpha(alpha).draw(drawX + 16, drawY + 16);
            } else {
                texture.initDraw().sprite(1, 3, 16).alpha(alpha).draw(drawX + 16, drawY + 16);
            }
        } else if (!this.same(adj[6]) && this.same(adj[4])) {
            texture.initDraw().sprite(3, 3, 16).alpha(alpha).draw(drawX + 16, drawY + 16);
        } else if (this.same(adj[6]) && !this.same(adj[4])) {
            texture.initDraw().sprite(5, 1, 16).alpha(alpha).draw(drawX + 16, drawY + 16);
        } else {
            texture.initDraw().sprite(1, 1, 16).alpha(alpha).draw(drawX + 16, drawY + 16);
        }
        if (!this.same(adj[6])) {
            boolean sameLeft = this.same(adj[3]);
            boolean sameRight = this.same(adj[4]);
            if (sameLeft && sameRight) {
                texture.initDraw().sprite(2, 3, 32, 16).alpha(alpha).draw(drawX, drawY + 16 + 10);
            } else if (sameLeft) {
                texture.initDraw().sprite(4, 3, 16).alpha(alpha).draw(drawX, drawY + 16 + 10);
                texture.initDraw().sprite(5, 2, 16).alpha(alpha).draw(drawX + 16, drawY + 16 + 10);
            } else if (sameRight) {
                texture.initDraw().sprite(4, 2, 16).alpha(alpha).draw(drawX, drawY + 16 + 10);
                texture.initDraw().sprite(5, 3, 16).alpha(alpha).draw(drawX + 16, drawY + 16 + 10);
            } else {
                texture.initDraw().sprite(2, 2, 32, 16).alpha(alpha).draw(drawX, drawY + 16 + 10);
            }
        }
    }

    private boolean same(GameObject object) {
        return object.getStringID().equals(this.getStringID());
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        Rectangle out = super.getCollision(level, x, y, rotation);
        if (this.same(level.getObject(x - 1, y))) {
            out.x -= 4;
            out.width += 4;
        }
        if (this.same(level.getObject(x, y - 1))) {
            out.y -= 4;
            out.height += 4;
        }
        if (this.same(level.getObject(x + 1, y))) {
            out.width += 4;
        }
        if (this.same(level.getObject(x, y + 1))) {
            out.height += 4;
        }
        return out;
    }
}

