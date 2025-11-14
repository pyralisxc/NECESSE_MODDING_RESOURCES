/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.componentPresets.FormNewPlayerPreset;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontOptions;

public class DebugPlayerForm
extends Form {
    public FormNewPlayerPreset newPlayer;
    public FormSlider healthSlider;
    public final DebugForm parent;

    public DebugPlayerForm(String name, DebugForm parent) {
        super(name, 400, 10);
        this.parent = parent;
        Client client = parent.client;
        PlayerMob clientPlayer = client.getPlayer();
        FormFlow flow = new FormFlow(10);
        this.addComponent(new FormLabel("Player", new FontOptions(20), 0, this.getWidth() / 2, flow.next(25)));
        this.newPlayer = this.addComponent(flow.nextY(new FormNewPlayerPreset(0, 35, this.getWidth() - 10, true, true), 10));
        this.healthSlider = this.addComponent(flow.nextY(new FormSlider("Health", 8, 305, clientPlayer.getHealth(), 0, clientPlayer.getMaxHealth(), this.getWidth() - 16, new FontOptions(12)), 5));
        this.healthSlider.onGrab(e -> {
            if (!e.grabbed) {
                int healthValue = ((FormSlider)e.from).getValue();
                ClientClient me = client.getClient();
                me.playerMob.setHealthHidden(healthValue, 0.0f, 0.0f, null, true);
                client.network.sendPacket(new PacketMobHealth(me.playerMob, true));
            }
        });
        this.healthSlider.drawValueInPercent = false;
        int buttonsY = flow.next(40);
        this.addComponent(new FormTextButton("Save", 4, buttonsY, this.getWidth() / 2 - 6)).onClicked(e -> {
            PlayerMob player = this.newPlayer.getNewPlayer();
            ClientClient clientClient = client.getClient();
            player.playerName = clientClient.getName();
            clientClient.playerMob.look = new HumanLook(player.look);
            client.network.sendPacket(new PacketPlayerAppearance(client.getSlot(), client.getCharacterUniqueID(), player));
        });
        this.addComponent(new FormTextButton("Back", this.getWidth() / 2 + 2, buttonsY, this.getWidth() / 2 - 6)).onClicked(e -> parent.makeCurrent(parent.mainMenu));
        this.setHeight(flow.next());
    }

    public void refreshPlayer() {
        PlayerMob clientPlayer = this.parent.client.getPlayer();
        this.newPlayer.setLook(new HumanLook(clientPlayer.look));
        this.healthSlider.setRange(0, this.parent.client.getPlayer().getMaxHealth());
        this.healthSlider.setValue(this.parent.client.getPlayer().getHealth());
    }
}

