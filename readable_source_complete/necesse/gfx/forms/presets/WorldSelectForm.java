/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipError;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.state.MainMenu;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormWorldSaveComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.LabeledTextInputForm;
import necesse.gfx.forms.presets.NewSaveForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class WorldSelectForm
extends FormSwitcher {
    protected MainMenu mainMenu;
    protected Form selectForm;
    protected FormContentBox charactersBox;
    protected Thread loadThread;
    protected NewSaveForm createNewForm;

    public WorldSelectForm(final MainMenu mainMenu, GameMessage backButton) {
        this.mainMenu = mainMenu;
        FormFlow selectFlow = new FormFlow(10);
        this.selectForm = this.addComponent(new Form("selectCharacter", 400, 10));
        this.selectForm.addComponent(selectFlow.nextY(new FormLocalLabel("ui", "selectworld", new FontOptions(20), 0, this.selectForm.getWidth() / 2, 0), 10));
        this.charactersBox = this.selectForm.addComponent(selectFlow.nextY(new FormContentBox(0, 0, this.selectForm.getWidth(), 350), 4));
        this.selectForm.addComponent(selectFlow.nextY(new FormLocalTextButton("ui", "createnewworld", 4, 0, this.selectForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> {
            this.makeCurrent(this.createNewForm);
            this.createNewForm.onStarted();
        });
        this.selectForm.addComponent(selectFlow.nextY(new FormLocalTextButton(backButton, 4, 0, this.selectForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> this.onBackPressed());
        this.selectForm.setHeight(selectFlow.next());
        this.createNewForm = this.addComponent(new NewSaveForm(){

            @Override
            public void createPressed(ServerCreationSettings settings) {
                try {
                    WorldSelectForm.this.onSelected(new WorldSave(settings), true);
                }
                catch (IOException | ZipError ex) {
                    mainMenu.addNotice(Localization.translate("misc", "createworldfailed") + "\n\n\"" + ex.getMessage() + "\"");
                    ex.printStackTrace();
                }
                catch (FileSystemClosedException ex) {
                    mainMenu.addNotice(Localization.translate("misc", "createworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                    ex.printStackTrace();
                }
            }

            @Override
            public void error(String error) {
                mainMenu.addNotice(error);
            }

            @Override
            public void backPressed() {
                WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
            }
        });
        this.onWindowResized(WindowManager.getWindow());
        this.makeCurrent(this.selectForm);
    }

    public abstract void onSelected(WorldSave var1, boolean var2);

    public abstract void onBackPressed();

    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.createNewForm)) {
            this.createNewForm.submitEscapeEvent(event);
        } else if (!this.isCurrent(this.selectForm)) {
            this.makeCurrent(this.selectForm);
            event.use();
        } else {
            this.onBackPressed();
            event.use();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void init() {
        WorldSelectForm worldSelectForm = this;
        synchronized (worldSelectForm) {
            super.init();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        WorldSelectForm worldSelectForm = this;
        synchronized (worldSelectForm) {
            super.addNextControllerFocus(list, currentXOffset, currentYOffset, customNavigationHandler, area, draw);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadWorlds() {
        WorldSelectForm worldSelectForm = this;
        synchronized (worldSelectForm) {
            if (this.loadThread != null) {
                this.loadThread.interrupt();
            }
            this.charactersBox.clearComponents();
            final FormFlow flow = new FormFlow(0);
            final AtomicBoolean addedSaves = new AtomicBoolean(false);
            this.loadThread = new Thread("CharacterLoader"){

                @Override
                public void run() {
                    WorldSave.loadSaves(save -> {
                        WorldSelectForm worldSelectForm = WorldSelectForm.this;
                        synchronized (worldSelectForm) {
                            FormWorldSaveComponent comp = new FormWorldSaveComponent(WorldSelectForm.this.charactersBox.getMinContentWidth() - 5, (WorldSave)save){

                                @Override
                                public void onSelectPressed() {
                                    WorldSelectForm.this.onSelected(this.worldSave, false);
                                }

                                @Override
                                public void onRenamePressed() {
                                    WorldSelectForm.this.startRename(this.worldSave);
                                }

                                @Override
                                public void onBackupPressed(boolean isAlreadyBackup) {
                                    WorldSelectForm.this.startBackup(this.worldSave, isAlreadyBackup);
                                }

                                @Override
                                public void onDeletePressed() {
                                    WorldSelectForm.this.startDeleteConfirm(this.worldSave);
                                }
                            };
                            comp.setX(5);
                            WorldSelectForm.this.charactersBox.addComponent((FormWorldSaveComponent)flow.nextY(comp));
                            WorldSelectForm.this.charactersBox.setContentBox(new Rectangle(WorldSelectForm.this.charactersBox.getWidth(), flow.next()));
                            addedSaves.set(true);
                        }
                    }, this::isInterrupted, interrupted -> {
                        if (interrupted.booleanValue()) {
                            return;
                        }
                        if (!addedSaves.get()) {
                            WorldSelectForm.this.charactersBox.addComponent(new FormLocalLabel("ui", "nosavestip", new FontOptions(16), 0, WorldSelectForm.this.charactersBox.getWidth() / 2, 10, WorldSelectForm.this.charactersBox.getMinContentWidth() - 10));
                        }
                    }, -1);
                }
            };
            this.loadThread.start();
        }
    }

    protected void startRename(final WorldSave worldSave) {
        LabeledTextInputForm renameForm = new LabeledTextInputForm("renameInput", new LocalMessage("ui", "renameworld"), false, GameUtils.validFileNamePattern, new LocalMessage("ui", "renamebutton"), new LocalMessage("ui", "backbutton")){

            @Override
            public GameMessage getInputError(String text) {
                if (text.isEmpty()) {
                    return new StaticMessage("");
                }
                if (World.worldExistsWithName(text) != null || WorldSave.isLatestBackup(text)) {
                    return new LocalMessage("ui", "renametaken");
                }
                return null;
            }

            @Override
            public void onConfirmed(String text) {
                String extension = GameUtils.getFileExtension(worldSave.filePath.getName());
                String targetName = text + (extension == null ? "" : "." + extension);
                if (!targetName.equals(worldSave.filePath.getName())) {
                    try {
                        World.moveWorld(worldSave.filePath, GameUtils.resolveFile(worldSave.filePath.getParentFile(), targetName));
                    }
                    catch (IOException e) {
                        WorldSelectForm.this.mainMenu.addNotice(new LocalMessage("misc", "renamefailed"));
                        System.err.println("Failed to rename world :(");
                        e.printStackTrace();
                    }
                }
                WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
                WorldSelectForm.this.loadWorlds();
            }

            @Override
            public void onCancelled() {
                WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
            }
        };
        renameForm.setInput(worldSave.displayName);
        this.addComponent(renameForm, (component, active) -> {
            if (!active.booleanValue()) {
                this.removeComponent(component);
            }
        });
        this.makeCurrent(renameForm);
        renameForm.selectAllAndSetTyping();
    }

    protected void startBackup(final WorldSave worldSave, boolean isAlreadyBackup) {
        LabeledTextInputForm backupForm = new LabeledTextInputForm("backupInput", new LocalMessage("ui", "chooseworldname"), false, GameUtils.validFileNamePattern, new LocalMessage("ui", isAlreadyBackup ? "restoresave" : "backupsave"), new LocalMessage("ui", "backbutton")){

            @Override
            public GameMessage getInputError(String text) {
                if (text.isEmpty()) {
                    return new StaticMessage("");
                }
                if (World.worldExistsWithName(text) != null || WorldSave.isLatestBackup(text)) {
                    return new LocalMessage("ui", "renametaken");
                }
                return null;
            }

            @Override
            public void onConfirmed(String text) {
                String extension = GameUtils.getFileExtension(worldSave.filePath.getName());
                if (extension == null) {
                    extension = "";
                }
                try {
                    File targetFilePath = GameUtils.resolveFile(worldSave.filePath.getParentFile(), text + "." + extension);
                    World.copyWorld(worldSave.filePath, targetFilePath, true);
                    try {
                        WorldSave newWorldSave = new WorldSave(targetFilePath, true, false, false);
                        WorldEntity worldEntity = newWorldSave.getWorldEntity();
                        worldEntity.resetUniqueID();
                        worldEntity.shouldSaveLoadedGameVersion = true;
                        newWorldSave.getWorld().saveWorldEntity();
                        newWorldSave.closeWorldFileSystem();
                        System.out.println("Successfully backed up " + worldSave.filePath + " and changed uniqueID");
                    }
                    catch (IOException | ZipError ex) {
                        WorldSelectForm.this.mainMenu.addNotice(Localization.translate("misc", "backupworldfailed") + "\n\n\"" + ex.getMessage() + "\"");
                        ex.printStackTrace();
                    }
                    catch (FileSystemClosedException ex) {
                        WorldSelectForm.this.mainMenu.addNotice(Localization.translate("misc", "backupworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                        ex.printStackTrace();
                    }
                }
                catch (IOException e) {
                    System.err.println("Error copying world " + worldSave.filePath.getName() + " to " + text + extension);
                }
                WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
                WorldSelectForm.this.loadWorlds();
            }

            @Override
            public void onCancelled() {
                WorldSelectForm.this.makeCurrent(WorldSelectForm.this.selectForm);
            }
        };
        backupForm.setInput(worldSave.displayName + (isAlreadyBackup ? " - Restored" : " - Backup"));
        this.addComponent(backupForm, (component, active) -> {
            if (!active.booleanValue()) {
                this.removeComponent(component);
            }
        });
        this.makeCurrent(backupForm);
        backupForm.selectAllAndSetTyping();
    }

    protected void startDeleteConfirm(WorldSave worldSave) {
        ConfirmationForm confirmForm = new ConfirmationForm("deleteCharacter");
        confirmForm.setupConfirmation(new LocalMessage("ui", "confirmdeleteworld", "world", worldSave.filePath.getName()), () -> {
            System.out.println("Deleting world: " + worldSave.filePath.getName());
            if (World.deleteWorld(worldSave.filePath)) {
                this.loadWorlds();
            } else {
                System.err.println("Error deleting world " + worldSave.filePath.getName());
            }
            this.makeCurrent(this.selectForm);
        }, () -> this.makeCurrent(this.selectForm));
        this.addComponent(confirmForm, (component, active) -> {
            if (!active.booleanValue()) {
                this.removeComponent(component);
            }
        });
        this.makeCurrent(confirmForm);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        WorldSelectForm worldSelectForm = this;
        synchronized (worldSelectForm) {
            super.draw(tickManager, perspective, renderBox);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.selectForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.createNewForm.onWindowResized(window);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.loadThread != null) {
            this.loadThread.interrupt();
        }
        this.loadThread = null;
    }
}

