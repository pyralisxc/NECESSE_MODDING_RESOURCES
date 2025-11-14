/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.item;

import java.awt.Rectangle;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.container.item.WrappingPaperContainer;

public class WrappingPaperContainerForm<T extends WrappingPaperContainer>
extends ContainerForm<T> {
    public FormLocalTextButton wrapButton;

    public WrappingPaperContainerForm(Client client, T container) {
        super(client, 300, 300, container);
        FormFlow flow = new FormFlow(10);
        if (!((WrappingPaperContainer)container).paperSlot.isClear()) {
            this.addComponent(flow.nextY(new FormLocalLabel(((WrappingPaperContainer)container).paperSlot.getItem().getItemLocalization(), new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 10));
        }
        this.addComponent(flow.nextY(new FormContainerSlot(client, (Container)container, ((WrappingPaperContainer)container).CONTENT_SLOT, this.getWidth() / 2 - 20, 10), 10));
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "presentmessage", new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20), 5));
        int inputHeight = 80;
        FormContentBox textContent = this.addComponent(new FormContentBox(4, flow.next(inputHeight) + 4, this.getWidth() - 8, inputHeight - 8, GameBackground.textBox));
        FormTextBox textBox = textContent.addComponent(new FormTextBox(new FontOptions(16), FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, textContent.getMinContentWidth(), 6, 300));
        textBox.allowTyping = true;
        textBox.setEmptyTextSpace(new Rectangle(textContent.getX(), textContent.getY(), textContent.getWidth(), textContent.getHeight()));
        textBox.onChange(e -> {
            Rectangle box = textContent.getContentBoxToFitComponents();
            textContent.setContentBox(box);
            textContent.scrollToFit(textBox.getCaretBoundingBox());
        });
        textBox.onCaretMove(e -> {
            if (!e.causedByMouse) {
                textContent.scrollToFit(textBox.getCaretBoundingBox());
            }
        });
        flow.next(10);
        int wrapButtonWidth = Math.min(150, this.getWidth() - 20);
        this.wrapButton = this.addComponent(flow.nextY(new FormLocalTextButton("ui", "wrapconfirm", this.getWidth() / 2 - wrapButtonWidth / 2, 60, wrapButtonWidth, FormInputSize.SIZE_24, ButtonColor.BASE), 10));
        this.wrapButton.onClicked(e -> container.wrapButton.runAndSend(textBox.getText()));
        this.setHeight(flow.next());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.wrapButton.setActive(((WrappingPaperContainer)this.container).canWrap());
        super.draw(tickManager, perspective, renderBox);
    }
}

