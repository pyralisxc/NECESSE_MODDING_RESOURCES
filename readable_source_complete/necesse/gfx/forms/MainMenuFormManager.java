/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;
import necesse.engine.GameInfo;
import necesse.engine.GameLaunch;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.state.MainMenu;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.PositionedDrawOptionsBox;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.MainMenuForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.TestCrashReportForm;

public class MainMenuFormManager
extends FormManager
implements ContinueComponentManager {
    public final MainMenu mainMenu;
    public FormSwitcher switcher;
    private final LinkedHashMap<String, ContinueComponent> continueComponents = new LinkedHashMap();
    private FormResizeWrapper connecting;
    public MainMenuForm mainForm;

    public MainMenuFormManager(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    @Override
    public void frameTick(TickManager tickManager) {
        super.frameTick(tickManager);
        this.updateActiveForms();
    }

    public void setup() {
        this.switcher = this.addComponent(new FormSwitcher());
        this.mainForm = new MainMenuForm(this.mainMenu);
        this.switcher.addComponent(this.mainForm);
        if (GameLaunch.launchOptions.containsKey("testcrash")) {
            this.addComponent(new TestCrashReportForm());
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective) {
        GameLoadingScreen.drawLogo(WindowManager.getWindow(), () -> this.mainForm.getBoundingBox().y);
        super.draw(tickManager, perspective);
        PositionedDrawOptionsBox inDevelopmentDrawBox = GameInfo.getInDevelopmentDrawBox(true, true);
        if (inDevelopmentDrawBox != null) {
            Object current;
            Rectangle boundingBox = !this.switcher.isCurrent(this.mainForm) ? ((current = this.switcher.getCurrent()) != null ? ((FormComponent)current).getBoundingBox() : this.mainForm.getBoundingBox()) : (this.mainForm.isCurrent(this.mainForm.main) ? this.mainForm.mainForm.getBoundingBox() : this.mainForm.getBoundingBox());
            inDevelopmentDrawBox.draw(boundingBox.x + boundingBox.width / 2, boundingBox.y - 4);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        if (this.connecting != null) {
            this.connecting.resizeLogic.run();
        }
        window.submitNextMoveEvent();
    }

    public void submitEscapeEvent(InputEvent escapeEvent) {
        if (this.connecting != null && this.switcher.isCurrent(this.connecting.component)) {
            this.cancelConnection();
            return;
        }
        ContinueComponent continueComponent = this.getFirstContinueComponent();
        if (continueComponent != null && this.switcher.isCurrent((FormComponent)((Object)continueComponent))) {
            if (continueComponent.canContinue()) {
                continueComponent.applyContinue();
            }
            return;
        }
        if (this.switcher.isCurrent(this.mainForm)) {
            this.mainForm.submitEscapeEvent(escapeEvent);
        }
    }

    public void cancelConnection() {
        if (this.mainMenu.getClient() != null) {
            if (this.mainMenu.getClient().getLocalServer() != null) {
                this.mainMenu.getClient().getLocalServer().stop();
            }
            this.mainMenu.getClient().disconnect("Connection cancelled");
        }
        this.updateActiveForms();
    }

    private ContinueComponent getFirstContinueComponent() {
        if (!this.continueComponents.isEmpty()) {
            return this.continueComponents.values().iterator().next();
        }
        return null;
    }

    private void updateActiveForms() {
        if (!this.continueComponents.isEmpty()) {
            FormComponent continueComponent = (FormComponent)((Object)this.getFirstContinueComponent());
            if (!this.switcher.isCurrent(continueComponent)) {
                this.switcher.makeCurrent(continueComponent);
            }
        } else {
            Client client = this.mainMenu.getClient();
            if (client != null && !client.isDisconnected()) {
                if (client.loading.isDone()) {
                    GlobalData.setCurrentState(new MainGame(client));
                } else if (this.connecting != null && !this.switcher.isCurrent(this.connecting.component)) {
                    this.switcher.makeCurrent(this.connecting.component);
                }
            } else if (!this.switcher.isCurrent(this.mainForm)) {
                this.switcher.makeCurrent(this.mainForm);
            }
        }
    }

    public void startConnection(Client client) {
        this.continueComponents.values().forEach(c -> this.switcher.removeComponent((FormComponent)((Object)c)));
        this.continueComponents.clear();
        if (this.connecting == null) {
            FormResizeWrapper wrapper = null;
            if (client != null) {
                wrapper = client.loading.getUnusedFormWrapper();
            }
            if (wrapper == null) {
                NoticeForm loading = this.switcher.addComponent(new NoticeForm("loading", 400, 120));
                loading.setupNotice(new LocalMessage("loading", "loading"), (GameMessage)new LocalMessage("ui", "connectcancel"));
                loading.onContinue(this::cancelConnection);
                wrapper = new FormResizeWrapper(loading, () -> {
                    GameWindow window = WindowManager.getWindow();
                    loading.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
                });
            }
            this.setConnectingComponent(wrapper);
        }
        this.updateActiveForms();
    }

    public void setConnectingComponent(FormResizeWrapper connecting) {
        if (this.connecting != null) {
            this.switcher.removeComponent(this.connecting.component);
        }
        this.connecting = connecting;
        this.switcher.addComponent(connecting.component);
        connecting.resizeLogic.run();
        this.updateActiveForms();
    }

    @Override
    public void addContinueForm(String key, ContinueComponent component) {
        if (key == null) {
            key = UUID.randomUUID().toString();
        }
        Objects.requireNonNull(component);
        ContinueComponent last = this.continueComponents.put(key, component);
        if (last != null) {
            this.switcher.removeComponent((FormComponent)((Object)last));
        }
        this.switcher.addComponent((FormComponent)((Object)component));
        ((FormComponent)((Object)component)).onWindowResized(WindowManager.getWindow());
        String finalKey = key;
        component.onContinue(() -> this.onContinueForm(finalKey, component));
        this.updateActiveForms();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    @Override
    public void removeContinueForm(String key) {
        ContinueComponent last = this.continueComponents.get(key);
        if (last != null) {
            this.switcher.removeComponent((FormComponent)((Object)last));
            this.updateActiveForms();
            ControllerInput.submitNextRefreshFocusEvent();
        }
    }

    @Override
    public boolean hasContinueForms() {
        return !this.continueComponents.isEmpty();
    }

    public void addNotice(GameMessage message, int noticeCooldown) {
        NoticeForm form = new NoticeForm("notice", 400, 120);
        form.setupNotice(message);
        form.setButtonCooldown(noticeCooldown);
        this.addContinueForm(null, form);
    }

    public void addNotice(GameMessage message) {
        this.addNotice(message, 2000);
    }

    public void addNotice(String message, int noticeCooldown) {
        this.addNotice(new StaticMessage(message), noticeCooldown);
    }

    public void addNotice(String message) {
        this.addNotice(new StaticMessage(message));
    }

    private void onContinueForm(String key, ContinueComponent comp) {
        this.switcher.removeComponent((FormComponent)((Object)comp));
        this.continueComponents.remove(key);
        this.updateActiveForms();
        ControllerInput.submitNextRefreshFocusEvent();
    }
}

