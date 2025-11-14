/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import necesse.engine.localization.message.LocalMessage;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.actions.RequestJoinSettlementAction;
import necesse.inventory.container.settlement.events.SettlementDataEvent;

public class SettlementPrivateForm
extends Form {
    protected RequestJoinSettlementAction requestJoinAction;

    public SettlementPrivateForm(SettlementDataEvent basics, RequestJoinSettlementAction requestJoinAction) {
        super("settlementPrivate", 400, 40);
        this.requestJoinAction = requestJoinAction;
        this.updateContent(basics);
    }

    public void updateContent(SettlementDataEvent basics) {
        this.clearComponents();
        FormFlow flow = new FormFlow(5);
        this.addComponent(flow.nextY(new FormLocalLabel(basics.settlementName, new FontOptions(20), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 10));
        this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementispriv"), new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 10));
        this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementprivatetip"), new FontOptions(16), 0, this.getWidth() / 2, 0, this.getWidth() - 20), 10));
        this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementowner", "owner", basics.ownerName), new FontOptions(16), 0, this.getWidth() / 2, 0), 10));
        if (this.requestJoinAction != null) {
            int buttonWidth = Math.min(this.getWidth() - 8, 300);
            if (basics.isTeamPublic) {
                this.addComponent(flow.nextY(new FormLocalTextButton("ui", "teamjoin", this.getWidth() / 2 - buttonWidth / 2, 0, buttonWidth, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 20)).onClicked(e -> {
                    this.requestJoinAction.runAndSend(true);
                    ((FormButton)e.from).startCooldown(5000);
                });
            } else {
                this.addComponent(flow.nextY(new FormLocalTextButton("ui", "teamrequestjoin", this.getWidth() / 2 - buttonWidth / 2, 0, buttonWidth, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE), 20)).onClicked(e -> {
                    this.requestJoinAction.runAndSend(false);
                    ((FormButton)e.from).startCooldown(5000);
                });
            }
        }
        this.setHeight(flow.next());
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

