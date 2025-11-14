/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.localization.message.StaticMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.lists.FormDebugItemList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontOptions;

public class DebugItemForm
extends Form {
    public FormDebugItemList itemList;
    public FormTextInput itemFilter;
    private final DebugForm parent;

    public DebugItemForm(String name, DebugForm parent) {
        super(name, 300, 400);
        this.parent = parent;
        this.addComponent(new FormLabel("Items", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.itemList = this.addComponent(new FormDebugItemList(0, 30, this.getWidth(), this.getHeight() - 130, parent.client));
        this.addComponent(new FormCheckBox("Show all", this.getWidth() - 100, this.getHeight() - 98)).onClicked(e -> this.itemList.showUnobtainable(((FormCheckBox)e.from).checked));
        this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, this.getHeight() - 98));
        this.itemFilter = this.addComponent(new FormTextInput(0, this.getHeight() - 80, FormInputSize.SIZE_32_TO_40, this.getWidth(), -1));
        this.itemFilter.placeHolder = new StaticMessage("Search filter");
        this.itemFilter.rightClickToClear = true;
        this.itemFilter.onChange(e -> this.itemList.setFilter(((FormTextInput)e.from).getText()));
        this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth())).onClicked(e -> {
            this.itemFilter.setTyping(false);
            parent.makeCurrent(parent.mainMenu);
        });
    }
}

