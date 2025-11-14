/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.gameTool.GameToolManager;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.PlaceBiomeGameTool;
import necesse.gfx.forms.presets.debug.tools.PlaceObjectGameTool;
import necesse.gfx.forms.presets.debug.tools.PlaceTileGameTool;
import necesse.gfx.gameFont.FontOptions;

public class DebugWorldForm
extends Form {
    public final DebugForm parent;

    public DebugWorldForm(String name, DebugForm parent) {
        super(name, 160, 320);
        this.parent = parent;
        this.addComponent(new FormLabel("World", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.addComponent(new FormTextButton("Biomes", 0, 40, this.getWidth())).onClicked(e -> {
            parent.biomes.biomeList.populateIfNotAlready();
            parent.makeCurrent(parent.biomes);
            PlaceBiomeGameTool tool = new PlaceBiomeGameTool(parent, BiomeRegistry.FOREST);
            GameToolManager.clearGameTools(parent);
            GameToolManager.setGameTool(tool, parent);
        });
        this.addComponent(new FormTextButton("Tiles", 0, 80, this.getWidth())).onClicked(e -> {
            parent.tiles.tileList.populateIfNotAlready();
            parent.makeCurrent(parent.tiles);
            PlaceTileGameTool tool = new PlaceTileGameTool(parent, TileRegistry.getTile(TileRegistry.waterID));
            GameToolManager.clearGameTools(parent);
            GameToolManager.setGameTool(tool, parent);
        });
        this.addComponent(new FormTextButton("Objects", 0, 120, this.getWidth())).onClicked(e -> {
            parent.objects.objectList.populateIfNotAlready();
            parent.makeCurrent(parent.objects);
            PlaceObjectGameTool tool = new PlaceObjectGameTool(parent, ObjectRegistry.getObject(0));
            GameToolManager.clearGameTools(parent);
            GameToolManager.setGameTool(tool, parent);
        });
        this.addComponent(new FormTextButton("Wires", 0, 160, this.getWidth())).onClicked(e -> parent.makeCurrent(parent.wire));
        this.addComponent(new FormTextButton("Time", 0, 200, this.getWidth())).onClicked(e -> parent.makeCurrent(parent.time));
        this.addComponent(new FormTextButton("Toggle rain", 0, 240, this.getWidth())).onClicked(e -> {
            String toggle = parent.client.getLevel().weatherLayer.isRaining() ? "clear" : "start";
            parent.client.network.sendPacket(new PacketChatMessage(parent.client.getSlot(), "/rain " + toggle));
        });
        this.addComponent(new FormTextButton("Back", 0, 280, this.getWidth())).onClicked(e -> parent.makeCurrent(parent.mainMenu));
    }
}

