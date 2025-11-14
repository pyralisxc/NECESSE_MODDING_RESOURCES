/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class PlaceObjectGameTool
extends MouseDebugGameTool {
    public GameObject object;
    public ArrayList<Integer> layers;
    public int layerIndex;
    public HudDrawElement hudDrawElement;

    public PlaceObjectGameTool(DebugForm parent, GameObject object) {
        super(parent, null);
        this.object = object;
        this.layers = new ArrayList<Integer>(object.getValidObjectLayers());
        this.layerIndex = 0;
    }

    public int getLayer() {
        if (this.layers.isEmpty()) {
            return 0;
        }
        if (this.layerIndex < 0 || this.layerIndex >= this.layers.size()) {
            this.layerIndex = Math.floorMod(this.layerIndex, this.layers.size());
        }
        return this.layers.get(this.layerIndex);
    }

    protected ObjectPlaceOption getBestPlaceOption(Level level, PlayerMob player) {
        ArrayList<ObjectPlaceOption> placeOptions = this.object.getPlaceOptions(level, this.getMouseX(), this.getMouseY(), player, player.isAttacking ? player.beforeAttackDir : player.getDir(), false);
        int layer = this.getLayer();
        for (ObjectPlaceOption po : placeOptions) {
            String error = po.object.canPlace(level, layer, po.tileX, po.tileY, po.rotation, true, false);
            if (error != null) continue;
            return po;
        }
        return placeOptions.isEmpty() ? null : placeOptions.get(0);
    }

    @Override
    public void init() {
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
        this.hudDrawElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, PlayerMob perspective) {
                int layer;
                final PlayerMob player = PlaceObjectGameTool.this.parent.client.getPlayer();
                final Level level = this.getLevel();
                final ObjectPlaceOption po = PlaceObjectGameTool.this.getBestPlaceOption(level, player);
                if (po != null && (level.getObjectID(layer = PlaceObjectGameTool.this.getLayer(), po.tileX, po.tileY) != po.object.getID() || level.getObjectRotation(layer, po.tileX, po.tileY) != po.rotation)) {
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -100000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            po.object.drawMultiTilePreview(level, po.tileX, po.tileY, po.rotation, 0.5f, player, camera);
                        }
                    });
                }
            }
        };
        this.getLevel().hudManager.addElement(this.hudDrawElement);
        this.onLeftClick(e -> {
            int layer;
            PlayerMob player = this.parent.client.getPlayer();
            Level level = this.getLevel();
            ObjectPlaceOption po = this.getBestPlaceOption(level, player);
            if (po != null && (level.getObjectID(layer = this.getLayer(), po.tileX, po.tileY) != po.object.getID() || level.getObjectRotation(layer, po.tileX, po.tileY) != po.rotation)) {
                this.parent.client.network.sendPacket(new PacketChangeObject(level, layer, po.tileX, po.tileY, po.object.getID(), po.rotation));
            }
            return true;
        }, "Place object");
        this.updatePlaceUsage();
        this.onRightClick(e -> {
            int mouseTileX = this.getMouseTileX();
            int mouseTileY = this.getMouseTileY();
            this.object = this.getLevel().getObject(mouseTileX, mouseTileY);
            this.layers = new ArrayList<Integer>(this.object.getValidObjectLayers());
            this.layerIndex = 0;
            this.updatePlaceUsage();
            return true;
        }, "Select object");
        if (this.layers.size() > 1) {
            this.onScroll(e -> {
                this.layerIndex += e.getMouseWheelY() < 0.0 ? -1 : 1;
                this.updatePlaceUsage();
                return true;
            }, "Change layer");
        }
        this.onMouseMove(e -> {
            if (WindowManager.getWindow().isKeyDown(-100)) {
                int layer;
                PlayerMob player = this.parent.client.getPlayer();
                Level level = this.getLevel();
                ObjectPlaceOption po = this.getBestPlaceOption(level, player);
                if (po != null && (level.getObjectID(layer = this.getLayer(), po.tileX, po.tileY) != po.object.getID() || level.getObjectRotation(layer, po.tileX, po.tileY) != po.rotation)) {
                    this.parent.client.network.sendPacket(new PacketChangeObject(level, layer, po.tileX, po.tileY, po.object.getID(), po.rotation));
                }
                return true;
            }
            return false;
        });
    }

    public void updatePlaceUsage() {
        String postfix = "";
        if (this.layers.size() > 1) {
            int layerID = this.getLayer();
            postfix = " on layer " + ObjectLayerRegistry.getLayerStringID(layerID) + " (" + layerID + ")";
        }
        this.keyUsages.put(-100, "Place " + this.object.getDisplayName() + postfix);
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

