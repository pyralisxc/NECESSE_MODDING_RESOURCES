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

import medievalsim.packets.PacketPvPZoneEntryResponse;
import medievalsim.zones.PvPZone;
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

public class PvPZoneEntryDialog
extends Form {
    private final Client client;
    private final int zoneID;

    public PvPZoneEntryDialog(Client client, int zoneID, String zoneName, float damageMultiplier, int combatLockSeconds) {
        super("pvp_zone_entry", 450, 280);
        this.client = client;
        this.zoneID = zoneID;
        this.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        int currentY = 15;
        int margin = 20;
        int contentWidth = this.getWidth() - margin * 2;
        this.addComponent((FormComponent)new FormLabel("Enter PVP Zone?", new FontOptions(20), -1, this.getWidth() / 3, currentY));
        this.addComponent((FormComponent)new FormLabel(zoneName, new FontOptions(16), -1, this.getWidth() / 3, currentY += 35));
        String damagePercentStr = PvPZone.formatDamagePercent(damageMultiplier);
        this.addComponent((FormComponent)new FormLabel("Damage: " + damagePercentStr + " Combat Lock: " + combatLockSeconds + "s", new FontOptions(13), -1, this.getWidth() / 3, currentY += 30));
        this.addComponent((FormComponent)new FormLabel("PVP will be enabled when you enter", new FontOptions(13), -1, this.getWidth() / 3, currentY += 30));
        this.addComponent((FormComponent)new FormLabel("5 seconds of immunity on entry", new FontOptions(12), -1, this.getWidth() / 3, currentY += 20));
        int buttonWidth = (contentWidth - 10) / 2;
        FormTextButton enterButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Enter Zone", margin, currentY += 35, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        enterButton.onClicked(e -> {
            client.network.sendPacket((Packet)new PacketPvPZoneEntryResponse(zoneID, true));
            this.closeDialog();
        });
        FormTextButton declineButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Stay Outside", margin + buttonWidth + 10, currentY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        declineButton.onClicked(e -> {
            client.network.sendPacket((Packet)new PacketPvPZoneEntryResponse(zoneID, false));
            this.closeDialog();
        });
    }

    private void closeDialog() {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.removeComponent((FormComponent)this);
        }
        this.dispose();
    }
}

