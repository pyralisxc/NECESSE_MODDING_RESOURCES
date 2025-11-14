/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.GameDifficulty;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ButtonOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public class DifficultySelectForm
extends Form {
    public GameDifficulty selectedDifficulty = GameDifficulty.CLASSIC;
    private final FormContentIconToggleButton[] difficultyButtons;
    private final FormLocalLabel[] difficultyLabels;
    private final FormContentBox difficultyContent;
    private FormLocalTextButton difficultyContinueButton;

    public DifficultySelectForm(Runnable createPressed, Runnable backPressed, Runnable moreSettingsPressed) {
        this(new ButtonOptions("ui", "createworld", createPressed), ButtonOptions.backButton(backPressed), new ButtonOptions("ui", "moresworldettings", moreSettingsPressed));
    }

    public DifficultySelectForm(ButtonOptions continueButton, ButtonOptions backButton, ButtonOptions moreButton) {
        super("difficultySelect", 730, 500);
        GameDifficulty[] difficulties = GameDifficulty.values();
        int difficultyButtonPadding = 8;
        int difficultyButtonWidth = 128;
        int difficultyButtonHeight = 143;
        FormFlow difficultyFlow = new FormFlow(10);
        this.addComponent(new FormLocalLabel("ui", "selectdifficulty", new FontOptions(20), 0, this.getWidth() / 2, difficultyFlow.next(30)));
        this.addComponent(difficultyFlow.nextY(new FormLocalLabel("ui", "wschangetip", new FontOptions(12), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 20));
        int buttonsY = difficultyFlow.next(difficultyButtonHeight + 10 + 16);
        this.difficultyButtons = new FormContentIconToggleButton[difficulties.length];
        this.difficultyLabels = new FormLocalLabel[difficulties.length];
        for (int i = 0; i < difficulties.length; ++i) {
            FormLocalLabel label;
            final GameDifficulty difficulty = difficulties[i];
            int widthPerButton = difficultyButtonWidth + difficultyButtonPadding * 2;
            int x = this.getWidth() / 2 - this.difficultyButtons.length * widthPerButton / 2 + i * widthPerButton + difficultyButtonPadding;
            this.difficultyLabels[i] = label = this.addComponent(new FormLocalLabel(difficulty.displayName, new FontOptions(20), 0, x + difficultyButtonWidth / 2, buttonsY + difficultyButtonHeight + 5));
            FormContentIconToggleButton button = this.addComponent(new FormContentIconToggleButton(x, buttonsY, difficultyButtonWidth, FormInputSize.background(difficultyButtonHeight, GameBackground.form, 20), ButtonColor.BASE, null, new GameMessage[]{difficulty.displayName}){

                @Override
                public Color getContentColor(ButtonIcon icon) {
                    if (this.isToggled()) {
                        return Color.WHITE;
                    }
                    if (this.isHovering()) {
                        return Color.WHITE;
                    }
                    return new Color(80, 110, 155);
                }

                @Override
                protected void drawContent(int x, int y, int width, int height) {
                    difficulty.buttonIconBackgroundSupplier.get().texture.initDraw().color(this.getContentColor(null)).draw(-16, -17);
                }

                @Override
                protected void drawTopContent(int x, int y, int width, int height) {
                    difficulty.buttonIconForegroundSupplier.get().texture.initDraw().color(this.getContentColor(null)).draw(x - 16, y - 17);
                }
            });
            button.onToggled(e -> {
                label.setColor(this.getInterfaceStyle().inactiveTextColor);
                this.selectedDifficulty = difficulty;
                this.updateDifficultyContent();
            });
            this.difficultyButtons[i] = button;
        }
        int difficultyContentHeight = 250;
        this.difficultyContent = this.addComponent(new FormContentBox(0, difficultyFlow.next(difficultyContentHeight), this.getWidth(), difficultyContentHeight));
        this.updateDifficultyContent();
        if (moreButton != null) {
            difficultyFlow.next(4);
            int buttonWidth = Math.min(this.difficultyContent.getWidth() - 20, 300);
            this.addComponent(new FormLocalTextButton(moreButton.text, this.getWidth() / 2 - buttonWidth / 2, difficultyFlow.next(28), buttonWidth, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> moreButton.pressed.run());
        }
        if (continueButton != null || backButton != null) {
            int difficultyButtonsY = difficultyFlow.next(40);
            if (continueButton == null || backButton == null) {
                ButtonOptions button = continueButton == null ? backButton : continueButton;
                this.difficultyContinueButton = this.addComponent(new FormLocalTextButton(button.text, 4, difficultyButtonsY, this.getWidth() - 8));
                this.difficultyContinueButton.onClicked(e -> button.pressed.run());
            } else {
                this.difficultyContinueButton = this.addComponent(new FormLocalTextButton(continueButton.text, 4, difficultyButtonsY, this.getWidth() / 2 - 6));
                this.difficultyContinueButton.onClicked(e -> continueButton.pressed.run());
                this.addComponent(new FormLocalTextButton(backButton.text, this.getWidth() / 2 + 2, difficultyButtonsY, this.getWidth() / 2 - 6)).onClicked(e -> backButton.pressed.run());
            }
        }
        this.setHeight(difficultyFlow.next());
    }

    public void updateDifficultyContent() {
        this.difficultyContent.clearComponents();
        for (FormContentIconToggleButton formContentIconToggleButton : this.difficultyButtons) {
            formContentIconToggleButton.setToggled(false);
        }
        for (FormComponent formComponent : this.difficultyLabels) {
            ((FormLabel)formComponent).setColor(this.getInterfaceStyle().inactiveTextColor);
        }
        if (this.selectedDifficulty != null) {
            this.difficultyButtons[this.selectedDifficulty.ordinal()].setToggled(true);
            this.difficultyLabels[this.selectedDifficulty.ordinal()].setColor(this.getInterfaceStyle().activeTextColor);
            FormFlow flow = new FormFlow(20);
            this.difficultyContent.addComponent(new FormLocalLabel(this.selectedDifficulty.displayName, new FontOptions(20), 0, this.difficultyContent.getWidth() / 2, flow.next(30)));
            int textMaxWidth = Math.min(600, this.difficultyContent.getWidth() - 20);
            this.difficultyContent.addComponent(flow.nextY(new FormLocalLabel(this.selectedDifficulty.description, new FontOptions(16), 0, this.difficultyContent.getWidth() / 2, 0, textMaxWidth), 20));
            this.difficultyContent.addComponent(new FormLocalLabel("ui", "difficultyeffects", new FontOptions(20), 0, this.difficultyContent.getWidth() / 2, flow.next(24)));
            for (GameMessage gameMessage : this.selectedDifficulty.getEffectMessages()) {
                FormFairTypeLabel effectsLabel = new FormFairTypeLabel("", this.difficultyContent.getWidth() / 2, flow.next());
                effectsLabel.setTextAlign(FairType.TextAlign.CENTER);
                effectsLabel.setFontOptions(new FontOptions(16));
                effectsLabel.setMaxWidth(textMaxWidth);
                effectsLabel.setText(gameMessage);
                this.difficultyContent.addComponent(flow.nextY(effectsLabel));
            }
            flow.next(10);
            this.difficultyContent.setContentBox(new Rectangle(this.difficultyContent.getWidth(), flow.next()));
        }
    }
}

