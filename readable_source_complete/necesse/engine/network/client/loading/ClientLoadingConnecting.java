/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.GameCache;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingPhase;
import necesse.engine.network.packet.PacketConnectApproved;
import necesse.engine.network.packet.PacketConnectRequest;
import necesse.engine.network.packet.PacketRequestPassword;
import necesse.engine.network.server.Server;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPasswordInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameFont.FontOptions;

public class ClientLoadingConnecting
extends ClientLoadingPhase {
    private FormSwitcher switcher;
    private NoticeForm connecting;
    private Form passwordForm;
    private FormPasswordInput passwordInput;
    private boolean waitingForPass;
    private boolean rememberPass = true;
    private String password = "";

    public ClientLoadingConnecting(ClientLoading loading) {
        super(loading, false);
    }

    @Override
    public FormResizeWrapper start() {
        if (this.client.getLocalServer() != null) {
            this.password = this.client.getLocalServer().getSettings().password;
        }
        this.waitingForPass = false;
        this.switcher = new FormSwitcher();
        this.connecting = this.switcher.addComponent(new NoticeForm("connecting", 400, 120){

            @Override
            public boolean shouldButtonBeInactive() {
                if (ClientLoadingConnecting.this.client.getLocalServer() != null && !ClientLoadingConnecting.this.client.getLocalServer().startingAllowCancelling()) {
                    return true;
                }
                return super.shouldButtonBeInactive();
            }
        });
        this.connecting.setupNotice(new LocalMessage("loading", "connecting"), (GameMessage)new LocalMessage("ui", "connectcancel"));
        this.connecting.onContinue(this::cancelConnection);
        this.passwordForm = this.switcher.addComponent(new Form("password", 320, 120));
        this.passwordForm.addComponent(new FormLocalLabel("ui", "passwordreq", new FontOptions(16), 0, this.passwordForm.getWidth() / 2, 5));
        this.passwordInput = this.passwordForm.addComponent(new FormPasswordInput(4, 20, FormInputSize.SIZE_32_TO_40, this.passwordForm.getWidth() - 8, 50));
        this.passwordForm.addComponent(new FormLocalCheckBox("ui", "rememberpass", 10, 65, this.rememberPass)).onClicked(e -> {
            this.rememberPass = ((FormCheckBox)e.from).checked;
        });
        this.passwordInput.onSubmit(e -> {
            if (!(this.passwordInput.getText().isEmpty() || e.event.getID() != 257 && e.event.getID() != 335)) {
                this.submitPassword();
            }
        });
        FormLocalTextButton connectButton = this.passwordForm.addComponent(new FormLocalTextButton("ui", "connectserver", 4, this.passwordForm.getHeight() - 40, this.passwordForm.getWidth() / 2 - 6));
        connectButton.onClicked(e -> this.submitPassword());
        this.passwordInput.onChange(e -> connectButton.setActive(!this.passwordInput.getText().isEmpty()));
        connectButton.setActive(false);
        this.passwordForm.addComponent(new FormLocalTextButton("ui", "connectcancel", this.passwordForm.getWidth() / 2 + 2, this.passwordForm.getHeight() - 40, this.passwordForm.getWidth() / 2 - 6)).onClicked(e -> this.cancelConnection());
        this.switcher.makeCurrent(this.connecting);
        return new FormResizeWrapper(this.switcher, () -> {
            GameWindow window = WindowManager.getWindow();
            this.connecting.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            this.passwordForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        });
    }

    @Override
    public GameMessage getLoadingMessage() {
        GameMessage startingMessage;
        if (this.waitingForPass) {
            return new StaticMessage("WAITING_PASSWORD");
        }
        Server server = this.client.getLocalServer();
        if (server != null && !server.hasStarted() && (startingMessage = server.getStartingMessage()) != null) {
            return startingMessage;
        }
        return new LocalMessage("loading", "connecting");
    }

    private void submitPassword() {
        this.password = this.passwordInput.getText();
        Server server = this.client.getLocalServer();
        if (server == null || server.hasStarted()) {
            this.client.network.sendPacket(new PacketConnectRequest(this.password));
        } else {
            this.updateConnecting();
            this.setWait(100);
        }
        this.waitingForPass = false;
        this.switcher.makeCurrent(this.connecting);
    }

    @Override
    public void tick() {
        if (this.isWaiting() || this.waitingForPass) {
            return;
        }
        Server server = this.client.getLocalServer();
        if (server == null || server.hasStarted()) {
            this.client.network.sendPacket(new PacketConnectRequest(this.password));
            this.setWait(2500);
        } else {
            this.updateConnecting();
            this.setWait(100);
        }
    }

    public void updateConnecting() {
        Server server = this.client.getLocalServer();
        if (server != null) {
            GameMessage startingMessage = server.getStartingMessage();
            if (startingMessage != null) {
                this.connecting.setupNotice(startingMessage, (GameMessage)new LocalMessage("ui", "connectcancel"));
            }
        } else {
            this.connecting.setupNotice(new LocalMessage("loading", "connecting"), (GameMessage)new LocalMessage("ui", "connectcancel"));
        }
        GameWindow window = WindowManager.getWindow();
        this.connecting.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void submitApprovedPacket(PacketConnectApproved packet) {
        this.markDone();
    }

    public void submitRequestPasswordPacket(PacketRequestPassword packet) {
        if (this.switcher != null && !this.switcher.isDisposed()) {
            byte[] passwordBytes;
            if (GameCache.cacheFileExists(this.loading.getClientCachePath(packet.worldUniqueID, "Password")) && (passwordBytes = GameCache.getBytes(this.loading.getClientCachePath(packet.worldUniqueID, "Password"))) != null && passwordBytes.length > 0) {
                this.password = new String(passwordBytes);
                this.client.network.sendPacket(new PacketConnectRequest(this.password));
                return;
            }
            this.waitingForPass = true;
            this.switcher.makeCurrent(this.passwordForm);
            this.passwordInput.setText("");
            this.passwordInput.setTyping(true);
        } else {
            GameLog.warn.println("Received request password packet on wrong loading phase");
        }
    }

    public void submitWrongPassword(long worldUniqueID) {
        this.password = "";
        GameCache.removeCache(this.loading.getClientCachePath(worldUniqueID, "Password"));
        GameLog.debug.println("Wiped password cache");
    }

    @Override
    public void end() {
        if (this.rememberPass && !this.password.isEmpty()) {
            GameCache.cacheBytes(this.password.getBytes(), this.loading.getClientCachePath("Password"));
            GameLog.debug.println("Saved password bytes");
        }
    }
}

