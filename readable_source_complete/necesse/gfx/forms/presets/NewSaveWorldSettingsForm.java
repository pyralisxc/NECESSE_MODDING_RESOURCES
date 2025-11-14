/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameRaidFrequency;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.world.WorldSettings;
import necesse.gfx.forms.ButtonOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class NewSaveWorldSettingsForm
extends Form {
    protected FormContentBox settingsContent;
    protected FormTextInput worldSeed;
    protected FormLocalTextButton resetWorldSeed;
    protected FormLocalCheckBox spawnStarterHouse;
    protected FormDropdownSelectionButton<GameDeathPenalty> deathPenalty;
    protected FormDropdownSelectionButton<GameRaidFrequency> raidFrequency;
    protected FormLocalCheckBox survivalMode;
    protected FormLocalCheckBox playerHunger;
    protected FormLocalSlider dayTimeMod;
    protected FormLocalSlider nightTimeMod;

    public NewSaveWorldSettingsForm(Runnable createPressed, Runnable backPressed) {
        this(new ButtonOptions("ui", "createworld", createPressed), ButtonOptions.backButton(backPressed));
    }

    public NewSaveWorldSettingsForm(ButtonOptions continueButton, ButtonOptions backButton) {
        super("saveSettings", 500, 535);
        int selectorWidth = Math.min(Math.max(this.getWidth() - 50, 350), this.getWidth() - 20);
        int selectorX = this.getWidth() / 2 - selectorWidth / 2;
        this.settingsContent = this.addComponent(new FormContentBox(0, 0, this.getWidth(), this.getHeight() - 40));
        this.settingsContent.controllerScrollPadding = 60;
        int maxContentWidth = Math.min(Math.max(this.getWidth() - 50, 350), this.getWidth() - 20);
        int maxContentWidthXOffset = (this.settingsContent.getWidth() - maxContentWidth) / 2;
        FormFlow settingsFlow = new FormFlow(10);
        WorldSettings worldSettings = new WorldSettings(null);
        this.settingsContent.addComponent(new FormLocalLabel("ui", "worldsettings", new FontOptions(20), 0, this.settingsContent.getWidth() / 2, settingsFlow.next(30)));
        this.settingsContent.addComponent(settingsFlow.nextY(new FormLocalLabel("ui", "wschangetip", new FontOptions(12), 0, this.settingsContent.getWidth() / 2, 0, maxContentWidth), 10));
        this.settingsContent.addComponent(new FormLocalLabel("ui", "deathpenalty", new FontOptions(16), 0, this.settingsContent.getWidth() / 2, settingsFlow.next(20)));
        this.deathPenalty = this.settingsContent.addComponent(new FormDropdownSelectionButton(selectorX, settingsFlow.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, selectorWidth));
        for (GameDeathPenalty gameDeathPenalty : GameDeathPenalty.values()) {
            this.deathPenalty.options.add(gameDeathPenalty, gameDeathPenalty.displayName, () -> value.description);
        }
        this.deathPenalty.setSelected(worldSettings.deathPenalty, worldSettings.deathPenalty.displayName);
        this.settingsContent.addComponent(new FormLocalLabel("ui", "raidfrequency", new FontOptions(16), 0, this.settingsContent.getWidth() / 2, settingsFlow.next(20)));
        this.raidFrequency = this.settingsContent.addComponent(new FormDropdownSelectionButton(selectorX, settingsFlow.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, selectorWidth));
        for (Enum enum_ : GameRaidFrequency.values()) {
            this.raidFrequency.options.add(enum_, ((GameRaidFrequency)enum_).displayName, () -> NewSaveWorldSettingsForm.lambda$new$1((GameRaidFrequency)enum_));
        }
        this.raidFrequency.setSelected(worldSettings.raidFrequency, worldSettings.raidFrequency.displayName);
        settingsFlow.next(10);
        this.survivalMode = this.settingsContent.addComponent(new FormLocalCheckBox("ui", "survivalmode", 10, settingsFlow.next(), maxContentWidth).useButtonTexture());
        this.survivalMode.onClicked(e -> {
            this.playerHunger.setActive(!((FormCheckBox)e.from).checked);
            if (!this.playerHunger.checked) {
                this.playerHunger.checked = ((FormCheckBox)e.from).checked;
            }
        });
        this.survivalMode.checked = worldSettings.survivalMode;
        Rectangle survivalModeBox = this.survivalMode.getBoundingBox();
        this.survivalMode.setPosition(this.settingsContent.getWidth() / 2 - survivalModeBox.width / 2, settingsFlow.next(survivalModeBox.height + 10));
        this.settingsContent.addComponent(settingsFlow.nextY(new FormLocalLabel("ui", "survivalmodetip", new FontOptions(12), 0, this.settingsContent.getWidth() / 2, 0, maxContentWidth), 8));
        this.playerHunger = this.settingsContent.addComponent(new FormLocalCheckBox("ui", "playerhungerbox", 10, settingsFlow.next(), maxContentWidth).useButtonTexture());
        this.playerHunger.handleClicksIfNoEventHandlers = true;
        if (this.survivalMode.checked) {
            this.playerHunger.setActive(false);
        }
        this.playerHunger.checked = worldSettings.playerHunger;
        Rectangle playerHungerBox = this.playerHunger.getBoundingBox();
        this.playerHunger.setPosition(this.settingsContent.getWidth() / 2 - playerHungerBox.width / 2, settingsFlow.next(playerHungerBox.height + 15));
        this.playerHunger.controllerUpFocus = this.survivalMode;
        this.survivalMode.controllerDownFocus = this.playerHunger;
        this.raidFrequency.controllerDownFocus = this.survivalMode;
        settingsFlow.next(10);
        int halfBoxWidth = maxContentWidth / 2;
        this.settingsContent.addComponent(new FormLocalLabel("ui", "worldseed", new FontOptions(16), -1, maxContentWidthXOffset + 6, settingsFlow.next(20)));
        int n = settingsFlow.next(50);
        this.worldSeed = this.settingsContent.addComponent(new FormTextInput(maxContentWidthXOffset, n, FormInputSize.SIZE_32_TO_40, halfBoxWidth, 50));
        this.worldSeed.setRegexMatchFull("[a-zA-Z0-9 ]+");
        this.resetWorldSeed = this.settingsContent.addComponent(new FormLocalTextButton("ui", "resetseed", maxContentWidthXOffset + halfBoxWidth, n, halfBoxWidth));
        this.resetWorldSeed.onClicked(e -> this.setNewRandomSpawnSeed());
        this.spawnStarterHouse = this.settingsContent.addComponent(new FormLocalCheckBox("ui", "spawnguide", maxContentWidthXOffset + 6, settingsFlow.next(24)).useButtonTexture());
        this.spawnStarterHouse.handleClicksIfNoEventHandlers = true;
        this.spawnStarterHouse.checked = true;
        this.worldSeed.controllerDownFocus = this.spawnStarterHouse;
        this.spawnStarterHouse.controllerUpFocus = this.worldSeed;
        settingsFlow.next(10);
        this.settingsContent.addComponent(new FormLocalLabel("ui", "worldadvanced", new FontOptions(20), 0, this.settingsContent.getWidth() / 2, settingsFlow.next(30)));
        this.dayTimeMod = this.settingsContent.addComponent(settingsFlow.nextY(new FormLocalSlider("ui", "daymodnew", maxContentWidthXOffset, 10, 10, 5, 50, maxContentWidth, new FontOptions(12)){

            @Override
            public String getValueText() {
                return this.getValue() * 10 + "%";
            }
        }, 5));
        this.dayTimeMod.allowScroll = false;
        this.nightTimeMod = this.settingsContent.addComponent(settingsFlow.nextY(new FormLocalSlider("ui", "nightmodnew", maxContentWidthXOffset, 10, 10, 5, 50, maxContentWidth, new FontOptions(12)){

            @Override
            public String getValueText() {
                return this.getValue() * 10 + "%";
            }
        }, 5));
        this.nightTimeMod.allowScroll = false;
        this.settingsContent.setContentBox(new Rectangle(0, 0, this.settingsContent.getWidth(), settingsFlow.next()));
        this.addComponent(new FormLocalTextButton(continueButton.text, 4, this.getHeight() - 40, this.getWidth() / 2 - 6)).onClicked(e -> continueButton.pressed.run());
        this.addComponent(new FormLocalTextButton(backButton.text, this.getWidth() / 2 + 2, this.getHeight() - 40, this.getWidth() / 2 - 6)).onClicked(e -> backButton.pressed.run());
        this.setNewRandomSpawnSeed();
    }

    public void setNewRandomSpawnSeed() {
        this.worldSeed.setText(ServerCreationSettings.getNewRandomSpawnSeed());
    }

    public String getWorldSeed() {
        return this.worldSeed.getText();
    }

    public void reset() {
        this.setNewRandomSpawnSeed();
    }

    public boolean shouldSpawnStarterHouse() {
        return this.spawnStarterHouse.checked;
    }

    public void applyToWorldSettings(WorldSettings settings) {
        settings.deathPenalty = this.deathPenalty.getSelected();
        settings.raidFrequency = this.raidFrequency.getSelected();
        settings.survivalMode = this.survivalMode.checked;
        settings.playerHunger = this.playerHunger.checked;
        settings.dayTimeMod = (float)this.dayTimeMod.getValue() / 10.0f;
        settings.nightTimeMod = (float)this.nightTimeMod.getValue() / 10.0f;
    }

    private static /* synthetic */ GameMessage lambda$new$1(GameRaidFrequency value) {
        return value.description;
    }
}

