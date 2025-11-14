/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class PlaceTileGameTool
extends MouseDebugGameTool {
    public GameTile tile;
    public HudDrawElement hudDrawElement;

    public PlaceTileGameTool(DebugForm parent, GameTile tile) {
        super(parent, null);
        this.tile = tile;
    }

    @Override
    public void init() {
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
        this.hudDrawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                final int mouseTileX = PlaceTileGameTool.this.getMouseTileX();
                final int mouseTileY = PlaceTileGameTool.this.getMouseTileY();
                final PlayerMob player = PlaceTileGameTool.this.parent.client.getPlayer();
                final Level level = this.getLevel();
                if (this.getLevel().getTile(mouseTileX, mouseTileY) != PlaceTileGameTool.this.tile) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -100000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            PlaceTileGameTool.this.tile.drawPreview(level, mouseTileX, mouseTileY, 0.5f, player, camera);
                        }
                    });
                }
            }
        };
        this.getLevel().hudManager.addElement(this.hudDrawElement);
        this.onLeftClick(e -> {
            int mouseTileX = this.getMouseTileX();
            int mouseTileY = this.getMouseTileY();
            if (this.getLevel().getTile(mouseTileX, mouseTileY) != this.tile) {
                this.parent.client.network.sendPacket(new PacketChangeTile(this.getLevel(), mouseTileX, mouseTileY, this.tile.getID()));
            }
            return true;
        }, "Place tile");
        this.updatePlaceUsage();
        this.onRightClick(e -> {
            int mouseTileX = this.getMouseTileX();
            int mouseTileY = this.getMouseTileY();
            this.tile = this.getLevel().getTile(mouseTileX, mouseTileY);
            this.updatePlaceUsage();
            return true;
        }, "Select tile");
        this.onMouseMove(e -> {
            if (WindowManager.getWindow().isKeyDown(-100)) {
                int mouseTileX = this.getMouseTileX();
                int mouseTileY = this.getMouseTileY();
                if (this.getLevel().getTile(mouseTileX, mouseTileY) != this.tile) {
                    this.parent.client.network.sendPacket(new PacketChangeTile(this.getLevel(), mouseTileX, mouseTileY, this.tile.getID()));
                }
                return true;
            }
            return false;
        });
    }

    public void updatePlaceUsage() {
        this.keyUsages.put(-100, "Place " + this.tile.getDisplayName());
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }

    @Override
    public void isCleared() {
        super.isCleared();
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
    }
}

