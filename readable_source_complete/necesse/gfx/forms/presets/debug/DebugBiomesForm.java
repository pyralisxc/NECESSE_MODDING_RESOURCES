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
import necesse.gfx.forms.components.lists.FormBiomeList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.PlaceBiomeGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.biomes.Biome;

public class DebugBiomesForm
extends Form {
    public FormTextInput biomeFilter;
    public FormBiomeList biomeList;
    public final DebugForm parent;

    public DebugBiomesForm(String name, final DebugForm parent) {
        super(name, 240, 400);
        this.parent = parent;
        this.addComponent(new FormTextButton("Back", 0, 360, 240)).onClicked(e -> {
            this.biomeFilter.setTyping(false);
            parent.makeCurrent(parent.world);
        });
        this.addComponent(new FormLabel("Biomes", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.biomeList = this.addComponent(new FormBiomeList(0, 40, this.getWidth(), this.getHeight() - 140){

            @Override
            public void onClicked(Biome biome) {
                PlaceBiomeGameTool tool = new PlaceBiomeGameTool(parent, biome);
                GameToolManager.clearGameTools(parent);
                GameToolManager.setGameTool(tool, parent);
            }
        });
        this.addComponent(new FormLabel("Search filter:", new FontOptions(12), -1, 10, 302));
        this.biomeFilter = this.addComponent(new FormTextInput(0, 320, FormInputSize.SIZE_32_TO_40, 240, -1));
        this.biomeFilter.placeHolder = new StaticMessage("Search filter");
        this.biomeFilter.rightClickToClear = true;
        this.biomeFilter.onChange(e -> this.biomeList.setFilter(((FormTextInput)e.from).getText()));
    }
}

