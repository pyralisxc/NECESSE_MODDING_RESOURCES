/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.network.packet.PacketChatMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontOptions;

public class DebugTimeForm
extends Form {
    public final DebugForm parent;

    public DebugTimeForm(String name, DebugForm parent) {
        super(name, 240, 200);
        this.parent = parent;
        this.addComponent(new FormTextButton("Back", 0, 160, this.getWidth())).onClicked(e -> parent.makeCurrent(parent.world));
        this.addComponent(new FormLabel("Time", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.addComponent(new FormTextButton("Morning", 0, 40, this.getWidth() / 2)).onClicked(e -> this.sendChat("/time morning"));
        this.addComponent(new FormTextButton("Night", this.getWidth() / 2, 40, this.getWidth() / 2)).onClicked(e -> this.sendChat("/time night"));
        this.addComponent(new FormTextButton("Noon", 0, 80, this.getWidth() / 2)).onClicked(e -> this.sendChat("/time noon"));
        this.addComponent(new FormTextButton("Midnight", this.getWidth() / 2, 80, this.getWidth() / 2)).onClicked(e -> this.sendChat("/time midnight"));
        this.addComponent(new FormTextButton("+10", 0, 120, this.getWidth() / 2)).onClicked(e -> this.sendChat("/time add 10"));
        this.addComponent(new FormTextButton("+100", this.getWidth() / 2, 120, this.getWidth() / 2)).onClicked(e -> this.sendChat("/time add 100"));
    }

    private void sendChat(String command) {
        this.parent.client.network.sendPacket(new PacketChatMessage(this.parent.client.getSlot(), command));
    }
}

