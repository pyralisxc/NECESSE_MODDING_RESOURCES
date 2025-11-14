/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.Platform
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GameLaunch;
import necesse.engine.GlobalData;
import necesse.engine.MouseDraggingElement;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModNextListData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropAtElement;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.lists.FormModListElement;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import org.lwjgl.system.Platform;

public class ModsForm
extends FormSwitcher {
    private final ContinueComponentManager continueComponentManager;
    private ConfirmationForm confirmationForm;
    private final Form main;
    private final FormContentBox modListContent;
    private final FormContentBox infoContent;
    private LoadedMod currentInfoMod;
    private List<FormModListElement> modList;
    private FormLocalTextButton saveButton;

    public ModsForm(String name, ContinueComponentManager continueComponentManager) {
        this.continueComponentManager = continueComponentManager;
        this.main = this.addComponent(new Form(name, 800, 500));
        this.main.addComponent(new FormLocalLabel("ui", "mods", new FontOptions(20), 0, this.main.getWidth() / 2, 10));
        this.main.addComponent(new FormLocalLabel("ui", "modsalpha", new FontOptions(12).color(this.getInterfaceStyle().errorTextColor), -1, 28, 8));
        this.main.addComponent(new FormContentIconButton(5, 4, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_help_20, new LocalMessage("ui", "modsalphatip")));
        this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "modinfogameversion", "version", "1.0.1"), new FontOptions(12), -1, 5, 25));
        this.main.addComponent(new FormLocalLabel(new LocalMessage("ui", "modsloadorder"), new FontOptions(16), -1, 5, 40));
        this.modListContent = this.main.addComponent(new FormContentBox(5, 60, this.main.getWidth() / 2 - 10, this.main.getHeight() - 100));
        this.infoContent = this.main.addComponent(new FormContentBox(this.main.getWidth() / 2 + 5, 40, this.main.getWidth() / 2 - 10, this.main.getHeight() - 80));
        this.main.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, this.main.getWidth() / 2 - 1, 40, this.main.getHeight() - 80, false));
        this.main.addComponent(new FormLocalTextButton("ui", "backbutton", this.main.getWidth() / 2 + 2, this.main.getHeight() - 40, this.main.getWidth() / 2 - 6)).onClicked(e -> {
            if (this.saveButton.isActive()) {
                this.confirmationForm.setupConfirmation(new LocalMessage("ui", "confirmnosave"), (GameMessage)new LocalMessage("ui", "savebutton"), (GameMessage)new LocalMessage("ui", "dontsavebutton"), () -> this.savePressed(true), () -> this.backPressed());
                this.makeCurrent(this.confirmationForm);
            } else {
                this.backPressed();
            }
        });
        this.saveButton = this.main.addComponent(new FormLocalTextButton("ui", "savebutton", 4, this.main.getHeight() - 40, this.main.getWidth() / 2 - 6));
        this.saveButton.onClicked(e -> this.savePressed(false));
        this.saveButton.setActive(false);
        this.confirmationForm = this.addComponent(new ConfirmationForm("confirm"));
        this.resetModsList();
        this.resetCurrent();
    }

    public void resetModsList() {
        this.infoContent.clearComponents();
        this.currentInfoMod = null;
        this.modList = new ArrayList<FormModListElement>();
        this.modListContent.clearComponents();
        List<ModNextListData> myMods = ModLoader.getAllModsSortedByCurrentList();
        Comparator<ModNextListData> comparator = Comparator.comparingInt(d -> d.enabled ? -1000 : d.mod.loadLocation.modProvider.getLoadOrder());
        comparator = comparator.thenComparing(d -> d.enabled ? "" : d.mod.name);
        myMods.sort(comparator);
        for (ModNextListData mod : myMods) {
            FormModListElement e = this.modListContent.addComponent(new FormModListElement(mod, this.modListContent.getMinContentWidth()){

                @Override
                public void onEnabledChanged(boolean enabled) {
                    ModsForm.this.saveButton.setActive(true);
                    this.moveUpButton.setActive(enabled && this.getCurrentIndex() > 0);
                    this.moveDownButton.setActive(enabled && this.getCurrentIndex() < ModsForm.this.modList.size() - 1);
                    this.updateDepends(ModsForm.this.modList);
                    if (enabled) {
                        for (FormModListElement mod : ModsForm.this.modList) {
                            if (mod == this || !mod.mod.id.equals(this.mod.id) || !mod.listData.enabled) continue;
                            mod.listData.enabled = false;
                            mod.enabledCheckbox.checked = false;
                        }
                    }
                    if (ModsForm.this.currentInfoMod == this.mod) {
                        ModsForm.this.setupInfo(this.mod, this.dependsMet, this.optionalDependsMet);
                    }
                }

                @Override
                public void onMovedUp() {
                    Input input = WindowManager.getWindow().getInput();
                    if (input.isKeyDown(340) || input.isKeyDown(344)) {
                        ModsForm.this.move(this.getCurrentIndex(), 0);
                    } else {
                        ModsForm.this.moveUp(this.getCurrentIndex());
                    }
                    ModsForm.this.saveButton.setActive(true);
                    Rectangle box = this.getBoundingBox();
                    int scrollPadding = 30;
                    Rectangle extraBox = new Rectangle(box.x - scrollPadding, box.y - scrollPadding, box.width + scrollPadding * 2, box.height + scrollPadding * 2);
                    ModsForm.this.modListContent.scrollToFit(extraBox);
                }

                @Override
                public void onMovedDown() {
                    Input input = WindowManager.getWindow().getInput();
                    if (input.isKeyDown(340) || input.isKeyDown(344)) {
                        ModsForm.this.move(this.getCurrentIndex(), ModsForm.this.modList.size());
                    } else {
                        ModsForm.this.moveDown(this.getCurrentIndex());
                    }
                    ModsForm.this.saveButton.setActive(true);
                    Rectangle box = this.getBoundingBox();
                    int scrollPadding = 30;
                    Rectangle extraBox = new Rectangle(box.x - scrollPadding, box.y - scrollPadding, box.width + scrollPadding * 2, box.height + scrollPadding * 2);
                    ModsForm.this.modListContent.scrollToFit(extraBox);
                }

                @Override
                public void onStartDragged() {
                    Renderer.setMouseDraggingElement(new ModListDraggingElement(this));
                }

                @Override
                public void onSelected() {
                    ModsForm.this.setupInfo(this.mod, this.dependsMet, this.optionalDependsMet);
                }

                @Override
                public boolean isCurrentlySelected() {
                    return ModsForm.this.currentInfoMod == this.mod;
                }
            });
            this.modList.add(e);
        }
        if (this.modList.isEmpty()) {
            this.modListContent.addComponent(new FormLocalLabel("ui", "modsempty", new FontOptions(20), 0, this.modListContent.getWidth() / 2, 10, this.modListContent.getWidth() - 20));
        }
        this.infoContent.addComponent(new FormLocalLabel("ui", "modsinfoselect", new FontOptions(16), 0, this.infoContent.getWidth() / 2, 5, this.infoContent.getWidth() - 10));
        this.saveButton.setActive(false);
        this.updateModsList();
    }

    public void updateModsList() {
        FormFlow flow = new FormFlow(5);
        this.modListContent.removeComponentsIf(c -> c instanceof FormDropAtElement);
        if (this.modList.isEmpty()) {
            this.modListContent.setContentBox(new Rectangle(this.modListContent.getWidth(), this.modListContent.getHeight()));
        } else {
            for (int i = 0; i < this.modList.size(); ++i) {
                int dropAtHeight;
                int dropAtY;
                FormModListElement last;
                final FormModListElement current = this.modList.get(i);
                FormModListElement formModListElement = last = i <= 0 ? null : this.modList.get(i - 1);
                if (last == null) {
                    dropAtY = flow.next();
                    dropAtHeight = current.getHeight() / 2;
                } else {
                    dropAtY = flow.next() - last.getHeight() / 2;
                    dropAtHeight = last.getHeight() / 2 + current.getHeight() / 2;
                }
                final int finalI = i;
                this.modListContent.addComponent(new FormDropAtElement(0, dropAtY, this.modListContent.getWidth(), dropAtHeight){

                    @Override
                    public void onReleasedAt(InputEvent event) {
                        MouseDraggingElement e = Renderer.getMouseDraggingElement();
                        if (e instanceof ModListDraggingElement) {
                            Renderer.setMouseDraggingElement(null);
                            ModListDraggingElement draggingElement = (ModListDraggingElement)e;
                            ModsForm.this.move(draggingElement.element.getCurrentIndex(), finalI);
                            this.playTickSound();
                        }
                    }

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        if (this.isHovering() && Renderer.getMouseDraggingElement() instanceof ModListDraggingElement) {
                            Renderer.initQuadDraw(this.width, 2).color(this.getInterfaceStyle().activeTextColor).draw(this.getX(), current.getY() - 1);
                        }
                    }
                }, 1000);
                if (i == this.modList.size() - 1) {
                    this.modListContent.addComponent(new FormDropAtElement(0, flow.next() + current.getHeight() / 2, this.modListContent.getWidth(), current.getHeight() / 2){

                        @Override
                        public void onReleasedAt(InputEvent event) {
                            MouseDraggingElement e = Renderer.getMouseDraggingElement();
                            if (e instanceof ModListDraggingElement) {
                                Renderer.setMouseDraggingElement(null);
                                ModListDraggingElement draggingElement = (ModListDraggingElement)e;
                                ModsForm.this.move(draggingElement.element.getCurrentIndex(), finalI + 1);
                                this.playTickSound();
                            }
                        }

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                            if (this.isHovering() && Renderer.getMouseDraggingElement() instanceof ModListDraggingElement) {
                                Renderer.initQuadDraw(this.width, 2).color(this.getInterfaceStyle().activeTextColor).draw(this.getX(), current.getY() + current.getHeight() - 1);
                            }
                        }
                    }, 1000);
                }
                current.setPosition(0, flow.next(current.getHeight()));
                current.setCurrentIndex(this.modList, i);
                current.updateDepends(this.modList);
            }
            this.modListContent.setContentBox(new Rectangle(this.modListContent.getWidth(), flow.next() + 5));
        }
    }

    public void moveUp(int index) {
        if (index > 0 && index < this.modList.size()) {
            FormModListElement e = this.modList.remove(index);
            int newIndex = Math.max(0, index - 1);
            this.modList.add(newIndex, e);
            if (this.currentInfoMod == e.mod) {
                this.setupInfo(e.mod, e.dependsMet, e.optionalDependsMet);
            }
        }
        this.updateModsList();
    }

    public void moveDown(int index) {
        if (index >= 0 && index < this.modList.size() - 1) {
            FormModListElement e = this.modList.remove(index);
            int newIndex = Math.min(this.modList.size(), index + 1);
            this.modList.add(newIndex, e);
            if (this.currentInfoMod == e.mod) {
                this.setupInfo(e.mod, e.dependsMet, e.optionalDependsMet);
            }
        }
        this.updateModsList();
    }

    public void move(int fromIndex, int toIndex) {
        fromIndex = GameMath.limit(fromIndex, 0, this.modList.size() - 1);
        toIndex = GameMath.limit(toIndex, 0, this.modList.size());
        FormModListElement e = this.modList.get(fromIndex);
        this.modList.add(toIndex, e);
        this.modList.remove(fromIndex + (toIndex <= fromIndex ? 1 : 0));
        this.saveButton.setActive(true);
        if (this.currentInfoMod == e.mod) {
            this.setupInfo(e.mod, e.dependsMet, e.optionalDependsMet);
        }
        this.updateModsList();
    }

    private void addInfoContent(FormFlow flow, int x, String text, FontOptions fontOptions) {
        this.infoContent.addComponent(flow.nextY(new FormFairTypeLabel(text, x, 5).setFontOptions(fontOptions).setMaxWidth(this.infoContent.getMinContentWidth() - 5 - x), 5));
    }

    private void addInfoContent(FormFlow flow, String text, FontOptions fontOptions) {
        this.addInfoContent(flow, 5, text, fontOptions);
    }

    private void addInfoContent(FormFlow flow, String text) {
        this.addInfoContent(flow, text, new FontOptions(16));
    }

    public void setupInfo(LoadedMod mod, boolean[] dependsMet, boolean[] optionalDependsMet) {
        this.infoContent.clearComponents();
        mod.loadLocation.modProvider.provideModInfoContent(this.infoContent, mod, dependsMet, optionalDependsMet, this, this.continueComponentManager);
    }

    private void savePressed(boolean submitBack) {
        List<ModListData> list = this.modList.stream().map(e -> e.listData).collect(Collectors.toList());
        ModLoader.saveModListSettings(list);
        Runnable restart = ModsForm.restartGameRunnable();
        this.confirmationForm.setupConfirmation(new LocalMessage("ui", "modssavenotice"), (GameMessage)new LocalMessage("ui", restart != null ? "modsrestart" : "modsquit"), (GameMessage)new LocalMessage("ui", "modslater"), () -> {
            if (restart != null) {
                restart.run();
            } else {
                WindowManager.getWindow().requestClose();
            }
        }, () -> {
            this.makeCurrent(this.main);
            if (submitBack) {
                this.backPressed();
            }
        });
        this.saveButton.setActive(false);
        this.makeCurrent(this.confirmationForm);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.main.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.confirmationForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void resetCurrent() {
        this.makeCurrent(this.main);
    }

    public void backPressed() {
    }

    public static Runnable restartGameRunnable() {
        switch (Platform.get()) {
            case LINUX: {
                return null;
            }
            case MACOSX: {
                return null;
            }
            case WINDOWS: {
                Path exePath = Paths.get(GlobalData.rootPath(), "Necesse.exe");
                if (!Files.exists(exePath, new LinkOption[0])) {
                    return null;
                }
                Path jarPath = Paths.get(GlobalData.rootPath(), "Necesse.jar");
                if (!Files.exists(jarPath, new LinkOption[0])) {
                    return null;
                }
                String javaHome = System.getProperty("java.home");
                if (javaHome == null) {
                    return null;
                }
                Path javaw = Paths.get(javaHome, "bin", "javaw.exe");
                return () -> {
                    WindowManager.getWindow().requestClose();
                    try {
                        String fullArgsCombined = "";
                        if (GameLaunch.fullArgs != null) {
                            fullArgsCombined = GameUtils.join(GameLaunch.fullArgs, " ");
                        }
                        Runtime.getRuntime().exec(javaw.toAbsolutePath() + " -cp \"" + jarPath.toAbsolutePath() + "\" RestartGameMain -wait 1000 -command \"" + exePath.toAbsolutePath() + " " + fullArgsCombined + " \"");
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                };
            }
        }
        return null;
    }

    public static class ModListDraggingElement
    implements MouseDraggingElement {
        public final FormModListElement element;

        public ModListDraggingElement(FormModListElement element) {
            this.element = element;
        }

        @Override
        public boolean draw(int mouseX, int mouseY) {
            FontManager.bit.drawString(mouseX, mouseY - 20, this.element.mod.name, new FontOptions(16).outline().alphaf(0.8f));
            return true;
        }
    }
}

