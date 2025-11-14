/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class RegionLoaderGameTool
extends MouseDebugGameTool {
    private boolean setServer;

    public RegionLoaderGameTool(DebugForm parent) {
        super(parent, "Load/unload region tool");
    }

    @Override
    public void init() {
        this.setServer = false;
        this.onLeftClick(e -> {
            boolean isLoaded;
            Point regionPosition = this.getRegionPosition();
            ClientLevelLoading loading = this.parent.client.levelManager.loading();
            boolean bl = isLoaded = loading.isRegionLoaded(regionPosition.x, regionPosition.y) || loading.isRegionInQueue(regionPosition.x, regionPosition.y);
            if (!isLoaded) {
                loading.sendRequest(regionPosition.x, regionPosition.y);
            }
            return true;
        }, "Load region");
        this.onRightClick(e -> {
            Level serverLevel;
            Point regionPosition = this.getRegionPosition();
            ClientLevelLoading loading = this.parent.client.levelManager.loading();
            boolean regionLoaded = loading.isRegionLoaded(regionPosition.x, regionPosition.y);
            if (regionLoaded) {
                loading.unloadRegion(regionPosition.x, regionPosition.y, true);
            }
            if (this.setServer && (serverLevel = this.getServerLevel()) != null) {
                serverLevel.regionManager.unloadRegion(regionPosition.x, regionPosition.y);
                serverLevel.getServer().streamClients().forEach(client -> client.removeLoadedRegion(serverLevel, regionPosition.x, regionPosition.y, true, true));
            }
            return true;
        }, "Unload region");
        this.onKeyClick(82, e -> {
            Point regionPosition = this.getRegionPosition();
            ClientLevelLoading loading = this.parent.client.levelManager.loading();
            loading.unloadRegion(regionPosition.x, regionPosition.y, true);
            Level serverLevel = this.getServerLevel();
            if (serverLevel != null) {
                Region region = serverLevel.regionManager.getRegion(regionPosition.x, regionPosition.y, true);
                if (region != null) {
                    serverLevel.generateRegion(region);
                }
                serverLevel.getServer().streamClients().forEach(client -> client.removeLoadedRegion(serverLevel, regionPosition.x, regionPosition.y, true, true));
            } else {
                this.parent.client.chat.addMessage("Can only regenerate level when hosting");
            }
            return true;
        }, "Regenerate region");
        this.onKeyClick(73, e -> {
            this.setServer = !this.setServer;
            this.updateUsage();
            return true;
        }, "");
        this.onKeyClick(79, e -> {
            HUD.showRegionBounds = !HUD.showRegionBounds;
            return true;
        }, "Toggle show region bounds");
        this.onKeyClick(80, e -> {
            ClientLevelLoading.DEBUG_STREAMING_PAUSED = !ClientLevelLoading.DEBUG_STREAMING_PAUSED;
            this.updateUsage();
            return true;
        }, "");
        this.updateUsage();
    }

    public void updateUsage() {
        if (this.setServer) {
            this.setKeyUsage(73, "Set server: enabled");
        } else {
            this.setKeyUsage(73, "Set server: disabled");
        }
        if (ClientLevelLoading.DEBUG_STREAMING_PAUSED) {
            this.setKeyUsage(80, "Region loading paused: enabled");
        } else {
            this.setKeyUsage(80, "Region loading paused: disabled");
        }
    }

    public Point getRegionPosition() {
        int tileX = this.getMouseTileX();
        int tileY = this.getMouseTileY();
        Level level = this.getLevel();
        int regionX = level.regionManager.getRegionCoordByTile(tileX);
        int regionY = level.regionManager.getRegionCoordByTile(tileY);
        return new Point(regionX, regionY);
    }
}

