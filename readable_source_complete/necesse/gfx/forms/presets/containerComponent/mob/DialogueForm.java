/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitchedComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDialogueOption;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;

public class DialogueForm
extends Form
implements FormSwitchedComponent {
    public FormContentBox content;
    public FormFlow flow;
    public int optionCounter;
    private final ArrayList<FormDialogueOption> dialogueOptions = new ArrayList();

    public DialogueForm(String name, int width, int height, GameMessage header, GameMessage intro) {
        super(name, width, height);
        this.content = this.addComponent(new FormContentBox(0, 0, width, height));
        this.reset(header, intro);
    }

    public void reset(BiConsumer<FormContentBox, FormFlow> adder) {
        this.dialogueOptions.clear();
        this.content.clearComponents();
        this.optionCounter = 1;
        this.flow = new FormFlow(4);
        adder.accept(this.content, this.flow);
        this.flow.next(10);
        this.updateContent();
    }

    public void reset(GameMessage header, BiConsumer<FormContentBox, FormFlow> adder) {
        this.reset((FormContentBox content, FormFlow flow) -> {
            if (header != null) {
                String mainHeader = GameUtils.maxString(header.translate(), new FontOptions(20), this.getWidth() - 10);
                content.addComponent(flow.nextY(new FormLabel(mainHeader, new FontOptions(20), -1, 4, 0), 5));
            }
            if (adder != null) {
                adder.accept((FormContentBox)content, (FormFlow)flow);
            }
        });
    }

    public void reset(GameMessage header) {
        this.reset(header, (BiConsumer<FormContentBox, FormFlow>)null);
    }

    public void reset(GameMessage header, GameMessage intro, BiConsumer<FormContentBox, FormFlow> adder) {
        this.reset(header, (FormContentBox content, FormFlow flow) -> {
            if (intro != null) {
                DialogueForm.addText(content, flow, intro);
            }
            if (adder != null) {
                adder.accept((FormContentBox)content, (FormFlow)flow);
            }
        });
    }

    public void reset(GameMessage header, GameMessage intro) {
        this.reset(header, intro, null);
    }

    public static FormFairTypeLabel addText(FormContentBox content, FormFlow flow, GameMessage text, int x, int width) {
        FormFairTypeLabel fairType = content.addComponent(new FormFairTypeLabel(text, x, 0));
        fairType.setMaxWidth(width);
        FontOptions fontOptions = new FontOptions(16);
        fairType.setFontOptions(fontOptions);
        fairType.setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(fontOptions), TypeParsers.ItemIcon(16), TypeParsers.MobIcon(16));
        flow.nextY(fairType);
        return fairType;
    }

    public static FormFairTypeLabel addText(FormContentBox content, FormFlow flow, GameMessage text) {
        return DialogueForm.addText(content, flow, text, 5, content.getWidth() - 10);
    }

    public FormDialogueOption addDialogueOption(GameMessage message, Runnable onClick) {
        return this.addDialogueOption(this.optionCounter++, message, onClick);
    }

    public FormDialogueOption addDialogueOption(int optionNumber, GameMessage message, Runnable onClick) {
        FormDialogueOption dialogueOption = this.addDialogueOption(new FormDialogueOption(optionNumber, message, new FontOptions(16), 0, 0, 0));
        dialogueOption.onClicked(e -> onClick.run());
        return dialogueOption;
    }

    public FormDialogueOption addDialogueOption(FormDialogueOption dialogueOption) {
        dialogueOption.setPosition(20, 0);
        dialogueOption.setMaxWidth(this.getWidth() - 30);
        this.content.addComponent(this.flow.nextY(dialogueOption, 6));
        this.dialogueOptions.add(dialogueOption);
        this.updateContent();
        return dialogueOption;
    }

    public void updateContent() {
        this.content.setContentBox(new Rectangle(0, 0, this.getWidth(), this.flow.next()));
    }

    public int getContentHeight() {
        return this.flow.next();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        this.content.setHeight(height);
    }

    @Override
    public void onSwitched(boolean active) {
        if (active) {
            this.setNextControllerFocus(this.dialogueOptions.toArray(new FormDialogueOption[0]));
        }
    }
}

