/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.Renderer
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.SortedDrawable
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package medievalsim.commandcenter.worldclick;

import java.awt.Color;
import java.util.List;
import medievalsim.commandcenter.worldclick.WorldClickHandler;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class WorldClickOverlay
extends HudDrawElement {
    private static final Color HIGHLIGHT_EDGE = new Color(255, 255, 0, 255);
    private static final Color HIGHLIGHT_FILL = new Color(255, 255, 0, 100);
    private static final Color LABEL_COLOR = Color.WHITE;
    private static final Color LABEL_BG = new Color(0, 0, 0, 180);

    public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
        WorldClickHandler handler = WorldClickHandler.getInstance();
        if (!handler.isActive()) {
            return;
        }
        final int hoverX = handler.getHoverTileX();
        final int hoverY = handler.getHoverTileY();
        if (hoverX < 0 || hoverY < 0) {
            return;
        }
        list.add(new SortedDrawable(){

            public int getPriority() {
                return -99999;
            }

            public void draw(TickManager tickManager) {
                int drawX = camera.getTileDrawX(hoverX);
                int drawY = camera.getTileDrawY(hoverY);
                Renderer.initQuadDraw((int)32, (int)32).color(HIGHLIGHT_FILL).draw(drawX, drawY);
                int borderWidth = 2;
                Renderer.initQuadDraw((int)32, (int)borderWidth).color(HIGHLIGHT_EDGE).draw(drawX, drawY);
                Renderer.initQuadDraw((int)32, (int)borderWidth).color(HIGHLIGHT_EDGE).draw(drawX, drawY + 32 - borderWidth);
                Renderer.initQuadDraw((int)borderWidth, (int)32).color(HIGHLIGHT_EDGE).draw(drawX, drawY);
                Renderer.initQuadDraw((int)borderWidth, (int)32).color(HIGHLIGHT_EDGE).draw(drawX + 32 - borderWidth, drawY);
            }
        });
        final String displayText = handler.getHoverDisplayString();
        if (displayText != null) {
            list.add(new SortedDrawable(){

                public int getPriority() {
                    return -99998;
                }

                public void draw(TickManager tickManager) {
                    int drawX = camera.getTileDrawX(hoverX) + 36;
                    int drawY = camera.getTileDrawY(hoverY) + 8;
                    FontOptions fontOptions = new FontOptions(16).color(LABEL_COLOR);
                    int textWidth = FontManager.bit.getWidthCeil(displayText, fontOptions);
                    int textHeight = FontManager.bit.getHeightCeil(displayText, fontOptions);
                    int padding = 4;
                    Renderer.initQuadDraw((int)(textWidth + padding * 2), (int)(textHeight + padding * 2)).color(LABEL_BG).draw(drawX - padding, drawY - padding);
                    FontManager.bit.drawString((float)drawX, (float)drawY, displayText, fontOptions);
                }
            });
        }
    }
}

