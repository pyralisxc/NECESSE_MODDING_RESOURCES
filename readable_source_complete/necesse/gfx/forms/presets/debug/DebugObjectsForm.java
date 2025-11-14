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
import necesse.gfx.forms.components.lists.FormObjectList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.PlaceObjectGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameObject.GameObject;

public class DebugObjectsForm
extends Form {
    public FormTextInput objectFilter;
    public FormObjectList objectList;
    public final DebugForm parent;

    public DebugObjectsForm(String name, final DebugForm parent) {
        super(name, 240, 400);
        this.parent = parent;
        this.addComponent(new FormTextButton("Back", 0, 360, 240)).onClicked(e -> {
            parent.makeCurrent(parent.world);
            this.objectFilter.setTyping(false);
        });
        this.addComponent(new FormLabel("Objects", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.objectList = this.addComponent(new FormObjectList(0, 40, this.getWidth(), this.getHeight() - 140){

            @Override
            public void onClicked(GameObject object) {
                PlaceObjectGameTool tool = new PlaceObjectGameTool(parent, object);
                GameToolManager.clearGameTools(parent);
                GameToolManager.setGameTool(tool, parent);
            }
        });
        this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
        this.objectFilter = this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
        this.objectFilter.placeHolder = new StaticMessage("Search filter");
        this.objectFilter.rightClickToClear = true;
        this.objectFilter.onChange(e -> this.objectList.setFilter(((FormTextInput)e.from).getText()));
    }
}

