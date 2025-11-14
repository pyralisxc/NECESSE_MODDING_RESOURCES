/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.util.Zoning
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.SharedTextureDrawOptions
 *  necesse.gfx.drawables.SortedDrawable
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.level.maps.hudManager.HudDrawElement
 */
package medievalsim.zones;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import medievalsim.zones.AdminZone;
import medievalsim.zones.ProtectedZone;
import medievalsim.zones.PvPZone;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;

public class ZoneVisualizationHud
extends HudDrawElement {
    private final Supplier<Map<Integer, ProtectedZone>> protectedZonesSupplier;
    private final Supplier<Map<Integer, PvPZone>> pvpZonesSupplier;
    private final boolean showProtectedZones;
    private final boolean showPvPZones;

    public ZoneVisualizationHud(Supplier<Map<Integer, ProtectedZone>> protectedZonesSupplier, Supplier<Map<Integer, PvPZone>> pvpZonesSupplier, boolean showProtectedZones, boolean showPvPZones) {
        this.protectedZonesSupplier = protectedZonesSupplier;
        this.pvpZonesSupplier = pvpZonesSupplier;
        this.showProtectedZones = showProtectedZones;
        this.showPvPZones = showPvPZones;
    }

    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
        Map<Integer, PvPZone> pvpZones;
        Map<Integer, ProtectedZone> protectedZones;
        if (this.showProtectedZones && this.protectedZonesSupplier != null && (protectedZones = this.protectedZonesSupplier.get()) != null) {
            for (ProtectedZone protectedZone : protectedZones.values()) {
                this.drawZone(protectedZone, list, camera);
            }
        }
        if (this.showPvPZones && this.pvpZonesSupplier != null && (pvpZones = this.pvpZonesSupplier.get()) != null) {
            for (PvPZone pvPZone : pvpZones.values()) {
                this.drawZone(pvPZone, list, camera);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawZone(AdminZone zone, List<SortedDrawable> list, final GameCamera camera) {
        Zoning zoning;
        if (zone == null || zone.zoning == null) {
            return;
        }
        Color edgeColor = Color.getHSBColor((float)zone.colorHue / 360.0f, 0.8f, 1.0f);
        Color fillColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), 40);
        Zoning zoning2 = zoning = zone.zoning;
        synchronized (zoning2) {
            Rectangle bounds;
            final SharedTextureDrawOptions options = zone.zoning.getDrawOptions(edgeColor, fillColor, camera);
            if (options != null) {
                list.add(new SortedDrawable(){

                    public int getPriority() {
                        return -100000;
                    }

                    public void draw(TickManager tickManager) {
                        options.draw();
                    }
                });
            }
            if ((bounds = zone.zoning.getTileBounds()) != null && !bounds.isEmpty()) {
                final int centerTileX = bounds.x + bounds.width / 2;
                final int centerTileY = bounds.y + bounds.height / 2;
                final String zoneName = zone.name.isEmpty() ? "Unnamed Zone" : zone.name;
                final Color labelColor = edgeColor;
                list.add(new SortedDrawable(){

                    public int getPriority() {
                        return -100001;
                    }

                    public void draw(TickManager tickManager) {
                        int drawX = camera.getTileDrawX(centerTileX);
                        int drawY = camera.getTileDrawY(centerTileY);
                        FontOptions fontOptions = new FontOptions(20).outline().color(labelColor);
                        int textWidth = FontManager.bit.getWidthCeil(zoneName, fontOptions);
                        FontManager.bit.drawString((float)(drawX - textWidth / 2), (float)(drawY - 10), zoneName, fontOptions);
                    }
                });
            }
        }
    }
}

