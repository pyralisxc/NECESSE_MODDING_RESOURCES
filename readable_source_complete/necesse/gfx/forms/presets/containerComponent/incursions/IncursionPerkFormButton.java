/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.incursions;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.containerComponent.incursions.IncursionPerkTreeForm;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.level.maps.incursion.AltarData;

public class IncursionPerkFormButton
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private final AltarData altarData;
    public final IncursionPerk perk;
    public final FallenAltarContainer altarContainer;
    private float grayscaleValue = 1.0f;
    private boolean goingUp = true;

    public IncursionPerkFormButton(int x, int y, final AltarData altarData, final IncursionPerk perk, final FallenAltarContainer altarContainer, final IncursionPerkTreeForm treeForm) {
        this.altarData = altarData;
        this.perk = perk;
        this.altarContainer = altarContainer;
        this.position = new FormFixedPosition(x, y);
        this.acceptRightClicks = true;
        this.onClicked(new FormEventListener<FormInputEvent<FormButton>>(){

            @Override
            public void onEvent(FormInputEvent<FormButton> event) {
                if (event.event.isControllerEvent()) {
                    if (event.event.getControllerEvent().getState() == ControllerInput.MENU_SELECT && perk.canObtainPerk(altarData)) {
                        altarContainer.obtainPerk.runAndSend(perk.getID());
                    }
                    if (event.event.getControllerEvent().getState() == ControllerInput.MENU_BACK && perk.canRespecPerk(altarData)) {
                        treeForm.updateGrayBackgroundHeight(altarData);
                        altarContainer.respecPerk.runAndSend(perk.getID());
                    }
                } else {
                    if (event.event.getID() == -100 && perk.canObtainPerk(altarData)) {
                        altarContainer.obtainPerk.runAndSend(perk.getID());
                    }
                    if (event.event.getID() == -99 && perk.canRespecPerk(altarData)) {
                        altarContainer.respecPerk.runAndSend(perk.getID());
                    }
                }
            }
        });
    }

    public float getIconGreyScaleValue(TickManager tickManager) {
        if (this.perk.isPerkLocked(this.altarData)) {
            return 0.25f;
        }
        if (!this.altarData.obtainedPerkIDs.contains(this.perk.getID()) && this.perk.canObtainPerk(this.altarData)) {
            if (this.grayscaleValue >= 1.0f) {
                this.goingUp = false;
            } else if (this.grayscaleValue <= 0.75f) {
                this.goingUp = true;
            }
            float delta = tickManager.getDelta();
            this.grayscaleValue = this.goingUp ? (this.grayscaleValue += delta * 1.5E-4f) : (this.grayscaleValue -= delta * 1.5E-4f);
            return this.grayscaleValue;
        }
        return this.altarData.obtainedPerkIDs.contains(this.perk.getID()) ? 1.0f : 0.4f;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameTexture lockedPerkOverlay;
        int offSetRes;
        this.perk.iconTexture.initDraw().color(this.getIconGreyScaleValue(tickManager)).draw(this.getX(), this.getY());
        GameTexture perkTreePerkBorder = GameResources.perkTreePerkBorder;
        int perkTreePerkBorderOffSetRes = (perkTreePerkBorder.getWidth() - this.perk.iconTexture.getWidth()) / 2;
        perkTreePerkBorder.initDraw().color(this.perk.canObtainPerk(this.altarData) ? 1.0f : 0.4f).draw(this.getX() - perkTreePerkBorderOffSetRes, this.getY() - perkTreePerkBorderOffSetRes);
        if (this.perk.canObtainPerk(this.altarData) && this.perk.canAffordToBuyPerk(this.altarContainer)) {
            GameTexture perkTreeCanBuyPerkBorderTexture = GameResources.perkTreeCanBuyPerkBorder;
            offSetRes = (perkTreeCanBuyPerkBorderTexture.getWidth() - this.perk.iconTexture.getWidth()) / 2;
            perkTreeCanBuyPerkBorderTexture.initDraw().color(this.getIconGreyScaleValue(tickManager)).draw(this.getX() - offSetRes, this.getY() - offSetRes);
        }
        if (this.altarData.hasPerk(this.perk)) {
            GameTexture perkTreeOwnsPerkBorder = GameResources.perkTreeOwnsPerkBorder;
            offSetRes = (perkTreeOwnsPerkBorder.getWidth() - this.perk.iconTexture.getWidth()) / 2;
            perkTreeOwnsPerkBorder.initDraw().draw(this.getX() - offSetRes, this.getY() - offSetRes);
        }
        if (this.perk.isPerkLocked(this.altarData)) {
            lockedPerkOverlay = GameResources.lockedPerkOverlay;
            offSetRes = (lockedPerkOverlay.getWidth() - this.perk.iconTexture.getWidth()) / 2;
            lockedPerkOverlay.initDraw().posMiddle(this.getX(), this.getY()).draw(this.getX() - offSetRes, this.getY() - offSetRes);
        }
        if (this.perk.highlightLock) {
            lockedPerkOverlay = GameResources.lockedPerkOverlay;
            offSetRes = (lockedPerkOverlay.getWidth() - this.perk.iconTexture.getWidth()) / 2;
            lockedPerkOverlay.initDraw().posMiddle(this.getX(), this.getY()).draw(this.getX() - offSetRes, this.getY() - offSetRes);
        }
        if (this.perk.locksAllOtherPerksOnTier() && !this.perk.hasObtainedPerk(this.altarData)) {
            GameTexture perkTreePerkLocksOtherPerksBorderTexture = GameResources.perkTreePerkLocksOtherPerksBorder;
            offSetRes = (perkTreePerkLocksOtherPerksBorderTexture.getWidth() - this.perk.iconTexture.getWidth()) / 2;
            perkTreePerkLocksOtherPerksBorderTexture.initDraw().posMiddle(this.getX(), this.getY()).draw(this.getX() - offSetRes, this.getY() - offSetRes);
        }
        this.perk.highlightLock = false;
        if (this.isHovering()) {
            ListGameTooltips tooltips = this.perk.getTooltips(this.altarData, this.altarContainer);
            if (tooltips != null) {
                GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
            }
            for (IncursionPerk currentPerk : IncursionPerksRegistry.getPerks()) {
                if (!currentPerk.otherPerksThatLockThisPerk.contains(this.perk) || currentPerk.isPerkLocked(this.altarData) || !this.perk.canObtainPerk(this.altarData)) continue;
                currentPerk.highlightLock = true;
            }
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return IncursionPerkFormButton.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public int getWidth() {
        return 64;
    }

    public int getHeight() {
        return 64;
    }
}

