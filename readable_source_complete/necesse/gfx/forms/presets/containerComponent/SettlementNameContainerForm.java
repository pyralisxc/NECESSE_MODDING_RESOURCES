/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.inventory.container.SettlementNameContainer;
import necesse.level.maps.biomes.Biome;

public class SettlementNameContainerForm
extends ContainerForm<SettlementNameContainer> {
    private final FormTextInput input = this.addComponent(new FormTextInput(4, 0, FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, 40));

    public SettlementNameContainerForm(Client client, SettlementNameContainer container) {
        super(client, 400, 80, container);
        Biome biome = client.getLevel().getBiome(client.getPlayer().getTileX(), client.getPlayer().getTileY());
        this.input.placeHolder = new LocalMessage("settlement", "defname", "biome", biome.getLocalization());
        this.input.onSubmit(e -> this.submitName());
        this.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, 40, this.getWidth() - 8)).onClicked(e -> this.submitName());
    }

    public void submitName() {
        ((SettlementNameContainer)this.container).submitButton.runAndSend(this.getCurrentNameInput().getContentPacket());
    }

    public GameMessage getCurrentNameInput() {
        String text = this.input.getText();
        if (text.isEmpty()) {
            return this.input.placeHolder;
        }
        return new StaticMessage(text);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }
}

