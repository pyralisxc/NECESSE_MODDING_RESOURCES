/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.PvPTeamsContainer;

public abstract class FormTeamInvite
extends Form {
    public Color backgroundColor;

    public FormTeamInvite(int x, int y, int width, int height, PvPTeamsContainer.InviteData invite, Color backgroundColor) {
        super(width, height);
        this.setPosition(x, y);
        this.shouldLimitDrawArea = false;
        this.backgroundColor = backgroundColor;
        this.addComponent(new FormContentIconButton(0, height / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_checked_20, new LocalMessage("ui", "acceptbutton"))).onClicked(e -> this.onAccept(invite));
        this.addComponent(new FormContentIconButton(20, height / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_escaped_20, new LocalMessage("ui", "declinebutton"))).onClicked(e -> this.onDecline(invite));
        this.addComponent(new FormLabel(invite.teamName, new FontOptions(16), -1, 44, height / 2 - 8, width - 44 - 4));
    }

    public abstract void onAccept(PvPTeamsContainer.InviteData var1);

    public abstract void onDecline(PvPTeamsContainer.InviteData var1);

    @Override
    public void drawBase(TickManager tickManager) {
        if (this.backgroundColor != null) {
            int margin = this.getInterfaceStyle().form.edgeMargin;
            int edgeResolution = this.getInterfaceStyle().form.edgeResolution;
            this.getInterfaceStyle().form.getCenterDrawOptions(this.getX() - edgeResolution + margin, this.getY() - edgeResolution + margin, this.getWidth() + edgeResolution * 2 - margin * 2, 20 + edgeResolution * 2 - margin * 2).forEachDraw(c -> c.color(this.backgroundColor)).draw();
        }
    }

    @Override
    public void drawEdge(TickManager tickManager) {
        if (this.backgroundColor != null) {
            int margin = this.getInterfaceStyle().form.edgeMargin;
            int edgeResolution = this.getInterfaceStyle().form.edgeResolution;
            this.getInterfaceStyle().form.getCenterEdgeDrawOptions(this.getX() - edgeResolution + margin, this.getY() - edgeResolution + margin, this.getWidth() + edgeResolution * 2 - margin * 2, 20 + edgeResolution * 2 - margin * 2).forEachDraw(c -> c.color(this.backgroundColor)).draw();
        }
    }
}

