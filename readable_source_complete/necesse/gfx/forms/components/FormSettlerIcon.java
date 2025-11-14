/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.PlayerSprite;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HUD;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class FormSettlerIcon
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private final Settler settler;
    private final Mob mob;
    private final SettlementContainerForm<?> containerForm;
    public Supplier<GameTooltips> extraTooltips;

    public FormSettlerIcon(int x, int y, Settler settler, Mob mob, SettlementContainerForm<?> containerForm) {
        this.position = new FormFixedPosition(x, y);
        this.settler = settler;
        this.mob = mob;
        this.containerForm = containerForm;
        if (containerForm != null) {
            this.onClicked(e -> containerForm.selectedSettlers.selectOrDeselectSettler(mob.getUniqueID()));
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTooltips tooltips;
        PlayerSprite.drawInForms((drawX, drawY) -> this.settler.getSettlerFaceDrawOptions(drawX, drawY, 32, this.mob).draw(), this.getX(), this.getY());
        if (this.containerForm != null && this.containerForm.selectedSettlers.contains(this.mob.getUniqueID())) {
            HUD.selectBoundOptions(new Color(255, 255, 255), false, this.getX(), this.getY(), this.getX() + 32, this.getY() + 32).draw();
        }
        if (this.isHovering() && (tooltips = this.getTooltips()) != null) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    public GameTooltips getTooltips() {
        GameTooltips extra;
        ListGameTooltips tooltips = new ListGameTooltips(this.settler.getGenericMobName());
        if (this.containerForm != null) {
            if (Input.lastInputIsController) {
                tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, Localization.translate("ui", "settlementselectsettler")));
            } else {
                tooltips.add(new InputTooltip(-100, Localization.translate("ui", "settlementselectsettler")));
            }
        }
        if (this.extraTooltips != null && (extra = this.extraTooltips.get()) != null) {
            tooltips.add(extra);
        }
        return tooltips;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormSettlerIcon.singleBox(new Rectangle(this.getX(), this.getY(), 32, 32));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

