/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.object;

import java.awt.Rectangle;
import necesse.engine.network.client.Client;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.fairType.parsers.TypeParser;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormTextBox;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.container.object.SignContainer;

public class SignContainerForm<T extends SignContainer>
extends ContainerForm<T> {
    private String lastSavedText;
    private final FormContentBox textContent = this.addComponent(new FormContentBox(8, 8, this.getWidth() - 16, this.getHeight() - 16, GameBackground.textBox));
    private final FormTextBox text;

    public static TypeParser[] getParsers(FontOptions fontOptions) {
        return new TypeParser[]{TypeParsers.GAME_COLOR, TypeParsers.REMOVE_URL, TypeParsers.URL_OPEN, TypeParsers.ItemIcon(fontOptions.getSize()), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions)};
    }

    public SignContainerForm(Client client, T container) {
        super(client, 400, 160, container);
        FontOptions textOptions = new FontOptions(16);
        this.text = this.textContent.addComponent(new FormTextBox(textOptions, FairType.TextAlign.LEFT, this.getInterfaceStyle().textBoxTextColor, 0, 0, this.textContent.getMinContentWidth(), 20, 300, (SignContainer)container){
            final /* synthetic */ SignContainer val$container;
            {
                this.val$container = signContainer;
                super(fontOptions, align, textColor, x, y, maxWidth, maxLines, maxLength);
            }

            @Override
            public void changedTyping(boolean value) {
                super.changedTyping(value);
                if (!value) {
                    String newText = SignContainerForm.this.text.getText();
                    if (!SignContainerForm.this.lastSavedText.equals(newText)) {
                        SignContainerForm.this.lastSavedText = newText;
                        this.val$container.updateTextAction.runAndSend(newText);
                    }
                }
            }
        });
        this.text.setParsers(SignContainerForm.getParsers(textOptions));
        this.text.allowItemAppend = true;
        this.text.setEmptyTextSpace(new Rectangle(this.textContent.getX(), this.textContent.getY(), this.textContent.getWidth(), this.textContent.getHeight()));
        this.text.setText(((SignContainer)container).objectEntity.getTextString());
        this.lastSavedText = this.text.getText();
        this.text.onChange(e -> {
            Rectangle box = this.textContent.getContentBoxToFitComponents();
            this.textContent.setContentBox(box);
            this.textContent.scrollToFit(this.text.getCaretBoundingBox());
        });
        this.text.onCaretMove(e -> {
            if (!e.causedByMouse) {
                this.textContent.scrollToFit(this.text.getCaretBoundingBox());
            }
        });
        this.text.onInputEvent(e -> {
            if (e.event.getID() == 256) {
                ((FormTypingComponent)e.from).setTyping(false);
                e.event.use();
                e.preventDefault();
            }
        });
        Rectangle box = this.textContent.getContentBoxToFitComponents();
        this.textContent.setContentBox(box);
    }
}

