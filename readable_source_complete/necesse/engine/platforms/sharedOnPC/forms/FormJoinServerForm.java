/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.sharedOnPC.forms;

import java.awt.Rectangle;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PlatformSubForm;
import necesse.engine.network.client.Client;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.sharedOnPC.forms.FormServerList;
import necesse.engine.state.MainMenu;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.gameFont.FontOptions;

public abstract class FormJoinServerForm
extends FormSwitcher
implements PlatformSubForm {
    public final MainMenu mainMenu;
    protected Form mainForm;
    protected FormTextInput mDirectIP;
    protected FormTextInput mDirectPort;
    protected FormTextInput mAddName;
    protected FormTextInput mAddIP;
    protected FormTextInput mAddPort;
    protected FormServerList serversList;
    protected FormLocalTextButton mDelete;
    protected FormLocalTextButton mJoin;
    protected Form multiplayerAdd;
    protected Form multiplayerDirect;
    public ConfirmationForm confirmForm;

    public FormJoinServerForm(int width, int height, MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        this.mainForm = this.addComponent(new Form("joinServer", width, height));
        FormFlow multiFlow = new FormFlow();
        this.serversList = this.mainForm.addComponent(multiFlow.nextY(new FormServerList(0, 0, this.mainForm.getWidth(), 280)));
        this.serversList.onDoubleSelect(e -> this.serversList.connect(this.mainMenu));
        int nextMultiY = multiFlow.next(40);
        this.mJoin = this.mainForm.addComponent(new FormLocalTextButton("ui", "joinserver", 4, nextMultiY, this.mainForm.getWidth() / 2 - 6));
        this.mJoin.onClicked(e -> this.serversList.connect(this.mainMenu));
        this.mainForm.addComponent(new FormLocalTextButton("ui", "directjoin", this.mainForm.getWidth() / 2 + 2, nextMultiY, this.mainForm.getWidth() / 2 - 6)).onClicked(e -> {
            this.makeCurrent(this.multiplayerDirect);
            this.mDirectIP.setText("");
            this.mDirectPort.setText(Integer.toString(14159));
            this.mDirectIP.setTyping(true);
        });
        nextMultiY = multiFlow.next(40);
        this.mainForm.addComponent(new FormLocalTextButton("ui", "addserver", 4, nextMultiY, this.mainForm.getWidth() / 2 - 6)).onClicked(e -> {
            this.makeCurrent(this.multiplayerAdd);
            this.mAddName.setText("");
            this.mAddIP.setText("");
            this.mAddPort.setText(Integer.toString(14159));
            this.mAddName.setTyping(true);
        });
        this.mDelete = this.mainForm.addComponent(new FormLocalTextButton("ui", "deleteserver", this.mainForm.getWidth() / 2 + 2, nextMultiY, this.mainForm.getWidth() / 2 - 6));
        this.mDelete.onClicked(e -> {
            if (this.serversList.hasSelected()) {
                this.confirmForm.setupConfirmation(new LocalMessage("ui", "confirmdeleteserver", "server", this.serversList.getSelectedName()), () -> {
                    this.serversList.deleteSelected();
                    this.makeCurrent(this.mainForm);
                }, () -> this.makeCurrent(this.mainForm));
                this.makeCurrent(this.confirmForm);
            }
        });
        nextMultiY = multiFlow.next(40);
        this.mainForm.addComponent(new FormLocalTextButton("ui", "refreshservers", 4, nextMultiY, this.mainForm.getWidth() / 2 - 6)).onClicked(e -> {
            this.serversList.refresh();
            this.serversList.startLanSearch();
        });
        this.mainForm.addComponent(new FormLocalTextButton("ui", "backbutton", this.mainForm.getWidth() / 2 + 2, nextMultiY, this.mainForm.getWidth() / 2 - 6)).onClicked(e -> this.onBackPressed());
        this.mainForm.setHeight(multiFlow.next());
        this.multiplayerDirect = this.addComponent(new Form("multiplayerDirect", 320, 200));
        this.multiplayerDirect.addComponent(new FormLocalTextButton("ui", "backbutton", this.multiplayerDirect.getWidth() / 2 + 2, this.multiplayerDirect.getHeight() - 40, this.multiplayerDirect.getWidth() / 2 - 6)).onClicked(e -> this.makeCurrent(this.mainForm));
        this.multiplayerDirect.addComponent(new FormLocalTextButton("ui", "connectserver", 4, this.multiplayerDirect.getHeight() - 40, this.multiplayerDirect.getWidth() / 2 - 6)).onClicked(e -> {
            int port;
            try {
                port = Integer.parseInt(this.mDirectPort.getText());
                if (port < 0 || port > 65535) {
                    throw new Exception("Invalid port");
                }
            }
            catch (Exception portEx) {
                port = 14159;
                GameLog.warn.println("Invalid port, used default");
            }
            String ip = this.mDirectIP.getText();
            Client client = Platform.getNetworkManager().startJoinServerClient(ip, ip, port);
            mainMenu.startConnection(client, MainMenu.ConnectFrom.MultiplayerJoinServer);
        });
        this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "ipdesc", new FontOptions(20), -1, 10, 0));
        this.mDirectIP = this.multiplayerDirect.addComponent(new FormTextInput(4, 20, FormInputSize.SIZE_32_TO_40, this.multiplayerDirect.getWidth() - 8, 50));
        this.mDirectIP.tabTypingComponent = () -> this.mDirectPort;
        this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "ipexample", new FontOptions(12), -1, 10, 60));
        this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "portdesc", new FontOptions(20), -1, 10, 80));
        this.mDirectPort = this.multiplayerDirect.addComponent(new FormTextInput(4, 100, FormInputSize.SIZE_32_TO_40, this.multiplayerDirect.getWidth() - 8, 7));
        this.mDirectPort.setRegexMatchFull("[0-9]+");
        this.mDirectPort.tabTypingComponent = () -> this.mDirectIP;
        this.multiplayerDirect.addComponent(new FormLocalLabel("ui", "portexample", new FontOptions(12), -1, 10, 140));
        this.multiplayerAdd = this.addComponent(new Form("multiplayerAdd", 320, 280));
        this.multiplayerAdd.addComponent(new FormLocalTextButton("ui", "backbutton", this.multiplayerAdd.getWidth() / 2 + 2, this.multiplayerAdd.getHeight() - 40, this.multiplayerAdd.getWidth() / 2 - 6)).onClicked(e -> this.makeCurrent(this.mainForm));
        this.multiplayerAdd.addComponent(new FormLocalTextButton("ui", "addconfirm", 4, this.multiplayerAdd.getHeight() - 40, this.multiplayerAdd.getWidth() / 2 - 6)).onClicked(e -> {
            int port;
            try {
                port = Integer.parseInt(this.mAddPort.getText());
                if (port < 0 || port > 65535) {
                    throw new Exception("Invalid port");
                }
            }
            catch (Exception portEx) {
                port = 14159;
                GameLog.warn.println("Invalid port, used default.");
            }
            String ip = this.mAddIP.getText();
            try {
                this.serversList.addServer(this.mAddName.getText(), ip, port);
            }
            catch (IllegalArgumentException addEx) {
                System.err.println("Could not add server.");
                addEx.printStackTrace();
            }
            this.makeCurrent(this.mainForm);
        });
        this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "namedesc", new FontOptions(20), -1, 10, 0));
        this.mAddName = this.multiplayerAdd.addComponent(new FormTextInput(4, 20, FormInputSize.SIZE_32_TO_40, this.multiplayerAdd.getWidth() - 8, 50));
        this.mAddName.tabTypingComponent = () -> this.mAddIP;
        this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "nameexample", new FontOptions(12), -1, 10, 60));
        this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "ipdesc", new FontOptions(20), -1, 10, 80));
        this.mAddIP = this.multiplayerAdd.addComponent(new FormTextInput(4, 100, FormInputSize.SIZE_32_TO_40, this.multiplayerAdd.getWidth() - 8, 50));
        this.mAddIP.tabTypingComponent = () -> this.mAddPort;
        this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "ipexample", new FontOptions(12), -1, 10, 140));
        this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "portdesc", new FontOptions(20), -1, 10, 160));
        this.mAddPort = this.multiplayerAdd.addComponent(new FormTextInput(4, 180, FormInputSize.SIZE_32_TO_40, this.multiplayerAdd.getWidth() - 8, 7));
        this.mAddPort.setRegexMatchFull("[0-9]+");
        this.mAddPort.tabTypingComponent = () -> this.mAddName;
        this.multiplayerAdd.addComponent(new FormLocalLabel("ui", "portexample", new FontOptions(12), -1, 10, 220));
        this.confirmForm = this.addComponent(new ConfirmationForm("confirm"));
    }

    public abstract void onBackPressed();

    @Override
    public void reset() {
        this.serversList.loadServerList();
        this.makeCurrent(this.mainForm);
    }

    @Override
    public void submitEscapeEvent(InputEvent event) {
        if (this.isCurrent(this.multiplayerAdd) || this.isCurrent(this.multiplayerDirect)) {
            this.makeCurrent(this.mainForm);
            event.use();
        } else {
            this.onBackPressed();
            event.use();
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.mainForm)) {
            this.mDelete.setActive(this.serversList.canDeleteSelected());
            this.mJoin.setActive(this.serversList.hasSelected());
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.mainForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.multiplayerAdd.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.multiplayerDirect.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.confirmForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

