/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;

public class TileInfoGameTool
extends MouseDebugGameTool {
    public TileInfoGameTool(DebugForm parent) {
        super(parent, "Tile info");
        this.onLeftClick(e -> {
            GameTile t = this.getLevel().getTile(this.getMouseTileX(), this.getMouseTileY());
            if (t != null) {
                parent.client.chat.addMessage("Tile (" + this.getMouseTileX() + ", " + this.getMouseTileY() + "):");
                parent.client.chat.addMessage("Name: " + t.getDisplayName());
                parent.client.chat.addMessage("ID: " + t.getID());
                parent.client.chat.addMessage("NameID: " + t.getStringID());
                parent.client.chat.addMessage("Health: " + t.tileHealth);
                parent.client.chat.addMessage("ToolTier: " + t.toolTier);
            } else {
                parent.client.chat.addMessage("Tile (" + this.getMouseTileX() + ", " + this.getMouseTileY() + ") is N/A");
            }
            return true;
        }, "Get tile info");
        this.onRightClick(e -> {
            GameObject o = this.getLevel().getObject(this.getMouseTileX(), this.getMouseTileY());
            if (o != null) {
                parent.client.chat.addMessage("Object (" + this.getMouseTileX() + ", " + this.getMouseTileY() + "):");
                parent.client.chat.addMessage("Rotation: " + this.getLevel().getObjectRotation(this.getMouseTileX(), this.getMouseTileY()));
                parent.client.chat.addMessage("Name: " + o.getDisplayName());
                parent.client.chat.addMessage("ID: " + o.getID());
                parent.client.chat.addMessage("NameID: " + o.getStringID());
                parent.client.chat.addMessage("Health: " + o.objectHealth);
                parent.client.chat.addMessage("ToolTier: " + o.toolTier);
            } else {
                parent.client.chat.addMessage("Object (" + this.getMouseTileX() + ", " + this.getMouseTileY() + ") is N/A");
            }
            return true;
        }, "Get object info");
    }

    @Override
    public void init() {
    }
}

