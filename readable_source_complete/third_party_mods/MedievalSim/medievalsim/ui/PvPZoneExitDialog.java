/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.network.Packet
 *  necesse.engine.network.client.Client
 *  necesse.engine.state.MainGame
 *  necesse.engine.window.WindowManager
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.FormTextButton
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.ui;

import medievalsim.packets.PacketPvPZoneExitResponse;
import necesse.engine.GlobalData;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.state.MainGame;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class PvPZoneExitDialog
extends Form {
    private final int zoneID;
    private final Client client;

    public PvPZoneExitDialog(Client client, int zoneID, String zoneName, int remainingCombatLockSeconds) {
        super("pvp_zone_exit", 450, 260);
        this.client = client;
        this.zoneID = zoneID;
        this.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        int currentY = 15;
        int margin = 20;
        int contentWidth = this.getWidth() - margin * 2;
        this.addComponent((FormComponent)new FormLabel("Exit PVP Zone?", new FontOptions(20), -1, this.getWidth() / 3, currentY));
        this.addComponent((FormComponent)new FormLabel(zoneName, new FontOptions(16), -1, this.getWidth() / 3, currentY += 35));
        currentY += 30;
        if (remainingCombatLockSeconds > 0) {
            this.addComponent((FormComponent)new FormLabel("Combat Lock: " + remainingCombatLockSeconds + "s remaining", new FontOptions(14), -1, this.getWidth() / 3, currentY));
            this.addComponent((FormComponent)new FormLabel("You cannot exit while in combat!", new FontOptions(13), -1, this.getWidth() / 3, currentY += 25));
            currentY += 35;
        } else {
            this.addComponent((FormComponent)new FormLabel("PVP will be disabled when you exit", new FontOptions(13), -1, this.getWidth() / 3, currentY));
            currentY += 35;
        }
        int buttonWidth = (contentWidth - 10) / 2;
        if (remainingCombatLockSeconds > 0) {
            FormTextButton stayButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Stay Inside", (this.getWidth() - buttonWidth) / 2, currentY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
            stayButton.onClicked(e -> this.closeDialog());
        } else {
            FormTextButton exitButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Exit Zone", margin, currentY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
            exitButton.onClicked(e -> {
                client.network.sendPacket((Packet)new PacketPvPZoneExitResponse(zoneID, true));
                this.closeDialog();
            });
            FormTextButton stayButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Stay Inside", margin + buttonWidth + 10, currentY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
            stayButton.onClicked(e -> {
                client.network.sendPacket((Packet)new PacketPvPZoneExitResponse(zoneID, false));
                this.closeDialog();
            });
        }
    }

    private void closeDialog() {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.removeComponent((FormComponent)this);
        }
        this.dispose();
    }
}

