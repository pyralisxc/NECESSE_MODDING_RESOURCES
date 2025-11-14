/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.gameTool.GameToolManager;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;

public class DebugWireForm
extends Form {
    public FormLocalCheckBox[] wires;
    public final DebugForm parent;

    public DebugWireForm(String name, DebugForm parent) {
        super(name, 160, 200);
        this.parent = parent;
        this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth())).onClicked(e -> parent.makeCurrent(parent.world));
        this.addComponent(new FormLabel("Wires", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.wires = new FormLocalCheckBox[4];
        for (int i = 0; i < 4; ++i) {
            this.wires[i] = this.addComponent(new FormLocalCheckBox("ui", "wire" + i, 10, 40 + i * 20));
            this.wires[i].handleClicksIfNoEventHandlers = true;
        }
        this.addComponent(new FormTextButton("Start tool", 0, this.getHeight() - 80, this.getWidth())).onClicked(e -> {
            MouseDebugGameTool tool = new MouseDebugGameTool(parent, null){

                @Override
                public void init() {
                    this.onLeftClick(e -> {
                        int mouseTileX = this.getMouseTileX();
                        int mouseTileY = this.getMouseTileY();
                        Level level = this.parent.client.getLevel();
                        boolean change = false;
                        for (int i = 0; i < 4; ++i) {
                            if (!DebugWireForm.this.wires[i].checked || level.wireManager.hasWire(mouseTileX, mouseTileY, i)) continue;
                            level.wireManager.setWire(mouseTileX, mouseTileY, i, true);
                            change = true;
                        }
                        if (change) {
                            this.parent.client.network.sendPacket(new PacketChangeWire(level, mouseTileX, mouseTileY, level.wireManager.getWireData(mouseTileX, mouseTileY)));
                        }
                        return true;
                    }, "Place wire");
                    this.onRightClick(e -> {
                        int mouseTileX = this.getMouseTileX();
                        int mouseTileY = this.getMouseTileY();
                        Level level = this.parent.client.getLevel();
                        boolean change = false;
                        for (int i = 0; i < 4; ++i) {
                            if (!DebugWireForm.this.wires[i].checked || !level.wireManager.hasWire(mouseTileX, mouseTileY, i)) continue;
                            level.wireManager.setWire(mouseTileX, mouseTileY, i, false);
                            change = true;
                        }
                        if (change) {
                            this.parent.client.network.sendPacket(new PacketChangeWire(level, mouseTileX, mouseTileY, level.wireManager.getWireData(mouseTileX, mouseTileY)));
                        }
                        return true;
                    }, "Remove wire");
                    this.onMouseMove(e -> {
                        GameWindow window = WindowManager.getWindow();
                        if (window.isKeyDown(-100)) {
                            int mouseTileX = this.getMouseTileX();
                            int mouseTileY = this.getMouseTileY();
                            Level level = this.parent.client.getLevel();
                            boolean change = false;
                            for (int i = 0; i < 4; ++i) {
                                if (!DebugWireForm.this.wires[i].checked || level.wireManager.hasWire(mouseTileX, mouseTileY, i)) continue;
                                level.wireManager.setWire(mouseTileX, mouseTileY, i, true);
                                change = true;
                            }
                            if (change) {
                                this.parent.client.network.sendPacket(new PacketChangeWire(level, mouseTileX, mouseTileY, level.wireManager.getWireData(mouseTileX, mouseTileY)));
                            }
                            return true;
                        }
                        if (window.isKeyDown(-99)) {
                            int mouseTileX = this.getMouseTileX();
                            int mouseTileY = this.getMouseTileY();
                            Level level = this.parent.client.getLevel();
                            boolean change = false;
                            for (int i = 0; i < 4; ++i) {
                                if (!DebugWireForm.this.wires[i].checked || !level.wireManager.hasWire(mouseTileX, mouseTileY, i)) continue;
                                level.wireManager.setWire(mouseTileX, mouseTileY, i, false);
                                change = true;
                            }
                            if (change) {
                                this.parent.client.network.sendPacket(new PacketChangeWire(level, mouseTileX, mouseTileY, level.wireManager.getWireData(mouseTileX, mouseTileY)));
                            }
                        }
                        return false;
                    });
                }
            };
            GameToolManager.clearGameTools(parent);
            GameToolManager.setGameTool(tool, parent);
        });
    }
}

