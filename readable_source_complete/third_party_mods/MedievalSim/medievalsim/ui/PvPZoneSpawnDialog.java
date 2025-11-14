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

import medievalsim.packets.PacketPvPZoneSpawnResponse;
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

public class PvPZoneSpawnDialog
extends Form {
    private final Client client;
    private final int zoneID;

    public PvPZoneSpawnDialog(Client client, int zoneID, String zoneName, float damageMultiplier, int combatLockSeconds) {
        super("pvp_zone_spawn", 450, 280);
        this.client = client;
        this.zoneID = zoneID;
        this.setPosMiddle(WindowManager.getWindow().getHudWidth() / 2, WindowManager.getWindow().getHudHeight() / 2);
        int currentY = 20;
        int margin = 20;
        int contentWidth = this.getWidth() - margin * 2;
        this.addComponent((FormComponent)new FormLabel("Spawned in PVP Zone", new FontOptions(20), -1, this.getWidth() / 2, currentY));
        this.addComponent((FormComponent)new FormLabel("Zone: " + zoneName, new FontOptions(16), 0, margin, currentY += 40));
        this.addComponent((FormComponent)new FormLabel("You have spawned inside a PVP zone!", new FontOptions(14), 0, margin, currentY += 30));
        String damagePercentStr = PvPZone.formatDamagePercent(damageMultiplier);
        this.addComponent((FormComponent)new FormLabel("Damage: " + damagePercentStr + " | Combat Lock: " + combatLockSeconds + "s", new FontOptions(14), 0, margin, currentY += 25));
        this.addComponent((FormComponent)new FormLabel("Would you like to stay or teleport to world spawn?", new FontOptions(14), 0, margin, currentY += 35));
        int buttonWidth = (contentWidth - 10) / 2;
        FormTextButton stayButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Stay in Zone", margin, currentY += 40, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        stayButton.onClicked(e -> {
            client.network.sendPacket((Packet)new PacketPvPZoneSpawnResponse(zoneID, true));
            this.closeDialog();
        });
        FormTextButton teleportButton = (FormTextButton)this.addComponent((FormComponent)new FormTextButton("Teleport to Spawn", margin + buttonWidth + 10, currentY, buttonWidth, FormInputSize.SIZE_32, ButtonColor.BASE));
        teleportButton.onClicked(e -> {
            client.network.sendPacket((Packet)new PacketPvPZoneSpawnResponse(zoneID, false));
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

