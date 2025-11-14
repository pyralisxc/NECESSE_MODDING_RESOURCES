/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import necesse.engine.GameAuth;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.teams.PvPTeamsContainer;

public abstract class FormTeamMember
extends Form {
    public Color backgroundColor;

    public FormTeamMember(int x, int y, int width, int height, PvPTeamsContainer.MemberData member, boolean isOwner, Color backgroundColor) {
        super(width, height);
        this.setPosition(x, y);
        this.shouldLimitDrawArea = false;
        this.backgroundColor = backgroundColor;
        int nameX = 4;
        if (isOwner) {
            if (member.auth != GameAuth.getAuthentication()) {
                this.addComponent(new FormContentIconButton(0, height / 2 - 10, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_more, new GameMessage[0])).onClicked(e -> {
                    SelectionFloatMenu menu = new SelectionFloatMenu(this);
                    menu.add(Localization.translate("ui", "teamkick", "name", member.name), () -> {
                        this.onKickMember(member);
                        menu.remove();
                    });
                    menu.add(Localization.translate("ui", "teampassowner"), () -> {
                        this.onPassOwnership(member);
                        menu.remove();
                    });
                    this.getManager().openFloatMenu(menu);
                });
            }
            nameX += 20;
        }
        this.addComponent(new FormLabel(member.name, new FontOptions(16), -1, nameX, height / 2 - 8, width - nameX - 4));
    }

    public abstract void onKickMember(PvPTeamsContainer.MemberData var1);

    public abstract void onPassOwnership(PvPTeamsContainer.MemberData var1);

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

