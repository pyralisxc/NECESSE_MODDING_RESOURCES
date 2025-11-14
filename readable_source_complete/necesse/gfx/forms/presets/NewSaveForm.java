/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.zip.ZipError;
import necesse.engine.Settings;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.position.FormRelativePosition;
import necesse.gfx.forms.presets.DifficultySelectForm;
import necesse.gfx.forms.presets.LabeledTextInputForm;
import necesse.gfx.forms.presets.NewSaveWorldSettingsForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;

public abstract class NewSaveForm
extends FormSwitcher {
    protected LabeledTextInputForm worldNameForm;
    protected FormContentIconToggleButton survivalButton;
    protected FormContentIconToggleButton creativeButton;
    protected FormLocalLabel worldTypeDescription;
    protected FormLocalLabel worldTypeInfo;
    protected final LocalMessage survivalDescription = new LocalMessage("ui", "newworldsurvivalmodedescription");
    protected final LocalMessage creativeDescription = new LocalMessage("ui", "newworldcreativemodedescription");
    protected final LocalMessage survivalInfo = new LocalMessage("ui", "newworldsurvivalmodeinfo");
    protected final LocalMessage creativeInfo = new LocalMessage("ui", "newworldcreativemodeinfo");
    protected NewSaveWorldSettingsForm settingsForm;
    protected DifficultySelectForm difficultyForm;

    public NewSaveForm() {
        this.worldNameForm = this.addComponent(new LabeledTextInputForm("worldName", (GameMessage)new LocalMessage("ui", "chooseworldname"), true, GameUtils.validFileNamePattern, (GameMessage)new LocalMessage("ui", "continuebutton"), (GameMessage)new LocalMessage("ui", "backbutton")){

            @Override
            public GameMessage getInputError(String text) {
                if (text.isEmpty() || WorldSave.isLatestBackup(text)) {
                    return new StaticMessage("");
                }
                return null;
            }

            @Override
            public void onConfirmed(String text) {
                if (NewSaveForm.this.survivalButton.isToggled()) {
                    NewSaveForm.this.makeCurrent(NewSaveForm.this.difficultyForm);
                } else {
                    NewSaveForm.this.createPressed();
                }
            }

            @Override
            public void onCancelled() {
                NewSaveForm.this.backPressed();
            }
        });
        this.worldNameForm.setHeight(this.worldNameForm.getHeight() + 340);
        this.worldNameForm.getComponents().forEach(component -> {
            if (component instanceof FormPositionContainer) {
                FormPositionContainer positionContainer = (FormPositionContainer)((Object)component);
                positionContainer.addPosition(0, 340);
            }
        });
        this.worldNameForm.addComponent(new FormLocalLabel("ui", "newworldselectworldtype", new FontOptions(20), 0, this.worldNameForm.getWidth() / 2, 10));
        this.addWorldTypeButtons(50, 35);
        this.worldTypeDescription = this.worldNameForm.addComponent(new FormLocalLabel(this.survivalDescription, new FontOptions(16), 0, this.worldNameForm.getWidth() / 2, 245, this.worldNameForm.getWidth() - 30));
        this.worldTypeInfo = this.worldNameForm.addComponent(new FormLocalLabel(this.survivalInfo, new FontOptions(16), 0, 0, 0, this.worldNameForm.getWidth() - 30));
        this.worldTypeInfo.setPosition(new FormRelativePosition((FormPositionContainer)this.worldTypeDescription, () -> 0, () -> 10 + this.worldTypeDescription.getHeight()));
        this.difficultyForm = this.addComponent(new DifficultySelectForm(this::createPressed, () -> this.makeCurrent(this.worldNameForm), () -> this.makeCurrent(this.settingsForm)));
        this.settingsForm = this.addComponent(new NewSaveWorldSettingsForm(this::createPressed, () -> this.makeCurrent(this.difficultyForm)));
        this.makeCurrent(this.worldNameForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    private void addWorldTypeButtons(int y, int spacing) {
        int width = 128;
        int totalWidth = width * 2 + spacing;
        int x = this.worldNameForm.getWidth() / 2 - totalWidth / 2;
        this.survivalButton = this.worldNameForm.addComponent(new FormContentIconToggleButton(x, y, width, FormInputSize.background(144, GameBackground.form, 20), ButtonColor.BASE, null, new GameMessage[]{new LocalMessage("ui", "newworldsurvivalmode")}){

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
                this.getInterfaceStyle().world_type_survival.texture.initDraw().color(this.getContentColor(null)).draw(-16, -17);
            }

            @Override
            protected void drawTopContent(int x, int y, int width, int height) {
                this.getInterfaceStyle().world_type_survival_toplayer.texture.initDraw().color(this.getContentColor(null)).draw(x - 16, y - 17);
            }
        });
        this.survivalButton.onToggled(event -> {
            this.creativeButton.setToggled(false);
            this.survivalButton.setToggled(true);
            this.worldTypeDescription.setLocalization(this.survivalDescription);
            this.worldTypeInfo.setLocalization(this.survivalInfo);
            this.worldTypeInfo.setColor(this.getInterfaceStyle().activeTextColor);
        });
        this.worldNameForm.addComponent(new FormLocalLabel("ui", "newworldsurvivalmode", new FontOptions(20), 0, x + width / 2, y + 144 + 5));
        this.creativeButton = this.worldNameForm.addComponent(new FormContentIconToggleButton(x + width + spacing, y, width, FormInputSize.background(144, GameBackground.form, 20), ButtonColor.BASE, null, new GameMessage[]{new LocalMessage("ui", "newworldcreativemode")}){

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
                this.getInterfaceStyle().world_type_creative.texture.initDraw().color(this.getContentColor(null)).draw(-16, -17);
            }

            @Override
            protected void drawTopContent(int x, int y, int width, int height) {
                this.getInterfaceStyle().world_type_creative_toplayer.texture.initDraw().color(this.getContentColor(null)).draw(x - 16, y - 17);
            }
        });
        this.creativeButton.onToggled(event -> {
            this.survivalButton.setToggled(false);
            this.creativeButton.setToggled(true);
            this.worldTypeDescription.setLocalization(this.creativeDescription);
            this.worldTypeInfo.setLocalization(this.creativeInfo);
            this.worldTypeInfo.setColor(this.getInterfaceStyle().errorTextColor);
        });
        this.worldNameForm.addComponent(new FormLocalLabel("ui", "newworldcreativemode", new FontOptions(20), 0, x + width / 2 + width + spacing, y + 144 + 5));
        this.survivalButton.setToggled(true);
    }

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.difficultyForm)) {
            this.makeCurrent(this.worldNameForm);
            event.use();
        } else if (this.isCurrent(this.settingsForm)) {
            this.makeCurrent(this.difficultyForm);
            event.use();
        } else if (this.isCurrent(this.worldNameForm)) {
            this.backPressed();
            event.use();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.worldNameForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.difficultyForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.settingsForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void onStarted() {
        this.makeCurrent(this.worldNameForm);
        this.worldNameForm.startTyping();
    }

    public void reset() {
        this.worldNameForm.setInput("");
        this.settingsForm.reset();
    }

    private void createPressed() {
        String saveName = WorldSave.addNumberSuffixIfExists(this.worldNameForm.getInputText());
        try {
            World tempWorld = World.getSaveDataWorld(new File(World.getWorldsPath() + saveName + (Settings.zipSaves ? ".zip" : "")), false);
            if (this.survivalButton.isToggled()) {
                tempWorld.settings.difficulty = this.difficultyForm.selectedDifficulty;
                this.settingsForm.applyToWorldSettings(tempWorld.settings);
            } else {
                tempWorld.settings.creativeMode = true;
            }
            tempWorld.settings.saveSettings();
            tempWorld.closeFileSystem();
        }
        catch (IOException | ZipError ex) {
            this.error(Localization.translate("misc", "createworldfailed") + "\n\n\"" + ex.getMessage() + "\"");
            ex.printStackTrace();
            return;
        }
        catch (FileSystemClosedException ex) {
            this.error(Localization.translate("misc", "createworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
            ex.printStackTrace();
            return;
        }
        catch (InvalidPathException ex) {
            this.error(Localization.translate("misc", "createworldfailed") + "\n\n" + Localization.translate("misc", "invalidworldname"));
            ex.printStackTrace();
            return;
        }
        ServerCreationSettings serverCreationSettings = new ServerCreationSettings(new File(World.getWorldsPath() + saveName + (Settings.zipSaves ? ".zip" : "")), this.settingsForm.getWorldSeed(), this.settingsForm.shouldSpawnStarterHouse());
        this.createPressed(serverCreationSettings);
    }

    public abstract void createPressed(ServerCreationSettings var1);

    public abstract void error(String var1);

    public abstract void backPressed();
}

