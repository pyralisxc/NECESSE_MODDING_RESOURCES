/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.MobContainerFormSwitcher;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.BuyAnimalContainer;
import necesse.inventory.container.mob.MobContainer;

public class BuyAnimalContainerForm<T extends BuyAnimalContainer>
extends MobContainerFormSwitcher<T> {
    public DialogueForm dialogueForm;
    public FormDialogueOption buyButton;

    public BuyAnimalContainerForm(Client client, int width, int height, T container) {
        super(client, container);
        this.dialogueForm = this.addComponent(new DialogueForm("buyAnimal", width, height, null, null));
        GameMessageBuilder builder = new GameMessageBuilder();
        for (InventoryItem item : ((BuyAnimalContainer)container).price.getItems()) {
            builder.append("\n ");
            builder.append(TypeParsers.getItemParseString(item) + "x" + item.getAmount() + " ").append(item.getItemLocalization());
        }
        this.dialogueForm.reset((GameMessage)new LocalMessage("ui", "buyanimal", "animal", ((MobContainer)container).getMob().getLocalization()), new LocalMessage("ui", "buyanimalcost", "animal", ((MobContainer)container).getMob().getLocalization(), "cost", builder));
        this.buyButton = this.dialogueForm.addDialogueOption(new LocalMessage("ui", "buybutton"), () -> container.buyAnimalAction.runAndSend());
        this.dialogueForm.addDialogueOption(new LocalMessage("ui", "backbutton"), () -> client.closeContainer(true));
        this.dialogueForm.setHeight(Math.max(this.dialogueForm.getContentHeight() + 5, height));
        ContainerComponent.setPosFocus(this.dialogueForm);
        this.updateBuyButton();
        this.makeCurrent(this.dialogueForm);
    }

    public BuyAnimalContainerForm(Client client, T container) {
        this(client, 408, 170, container);
    }

    public void updateBuyButton() {
        if (this.buyButton == null) {
            return;
        }
        this.buyButton.setActive(((BuyAnimalContainer)this.container).canPayForAnimal());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateBuyButton();
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.dialogueForm);
    }

    @Override
    public boolean shouldOpenInventory() {
        return true;
    }
}

