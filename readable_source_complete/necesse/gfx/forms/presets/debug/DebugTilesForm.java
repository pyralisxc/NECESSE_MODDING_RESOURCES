/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.gameTool.GameToolManager;
import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormTileList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.PlaceTileGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameTile.GameTile;

public class DebugTilesForm
extends Form {
    public FormTextInput tileFilter;
    public FormTileList tileList;
    public final DebugForm parent;

    public DebugTilesForm(String name, final DebugForm parent) {
        super(name, 240, 400);
        this.parent = parent;
        this.addComponent(new FormTextButton("Back", 0, 360, 240)).onClicked(e -> {
            this.tileFilter.setTyping(false);
            parent.makeCurrent(parent.world);
        });
        this.addComponent(new FormLabel("Tiles", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.tileList = this.addComponent(new FormTileList(0, 40, this.getWidth(), this.getHeight() - 140){

            @Override
            public void onClicked(GameTile tile) {
                PlaceTileGameTool tool = new PlaceTileGameTool(parent, tile);
                GameToolManager.clearGameTools(parent);
                GameToolManager.setGameTool(tool, parent);
            }
        });
        this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
        this.tileFilter = this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, 240, -1));
        this.tileFilter.placeHolder = new StaticMessage("Search filter");
        this.tileFilter.rightClickToClear = true;
        this.tileFilter.onChange(e -> this.tileList.setFilter(((FormTextInput)e.from).getText()));
    }
}

