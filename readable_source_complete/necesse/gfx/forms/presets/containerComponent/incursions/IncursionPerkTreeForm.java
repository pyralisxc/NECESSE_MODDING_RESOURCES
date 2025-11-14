/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.incursions;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.containerComponent.ContainerFormSwitcher;
import necesse.gfx.forms.presets.containerComponent.incursions.IncursionPerkFormButton;
import necesse.gfx.forms.presets.containerComponent.object.FallenAltarContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.IncursionData;

public class IncursionPerkTreeForm
extends ContainerFormSwitcher<Container> {
    protected HashMap<IncursionPerk, IncursionPerkFormButton> perkFormButtons = new HashMap();
    public Form perkTreeForm;
    public Form tierGrayedOutBackground;
    FormContentBox perkTreeContent;
    private int heightPerPerk = 0;

    public IncursionPerkTreeForm(Client client, final AltarData altarData, final FallenAltarContainer altarContainer, FallenAltarContainerForm altarContainerForm, int width, int height) {
        super(client, altarContainer);
        this.perkTreeForm = this.addComponent(new Form(width, height));
        FormFlow flow = new FormFlow();
        flow.next(5);
        this.perkTreeForm.addComponent(new FormLocalLabel("ui", "incursionperktree", new FontOptions(20), 0, width / 2, flow.next(25)));
        Color breakLineBlackColor = new Color(0, 0, 0);
        FormBreakLine topBreakLine = this.perkTreeForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, flow.next(2), width, true));
        topBreakLine.color = breakLineBlackColor;
        int startX = 10;
        int perkRes = 64;
        int sidePadding = 35;
        int paddingBetweenPerks = (width - sidePadding * 2) / 6 - perkRes;
        int additionalYPadding = 32;
        int fontSize = 12;
        int breakLineWidth = 2;
        int breakLineTextureWidth = 3;
        int breakLineOffset = breakLineWidth + breakLineTextureWidth;
        int perksYOffSet = -additionalYPadding - breakLineOffset;
        this.perkTreeContent = this.perkTreeForm.addComponent(new FormContentBox(0, flow.next(), width, height - 64));
        this.perkTreeContent.shouldLimitScrollBarDrawArea = false;
        final GameTexture perkTreeChainTexture = GameResources.perkTreeChains;
        for (int i = IncursionData.MINIMUM_TIER; i <= IncursionData.TABLET_TIER_UPGRADE_CAP; ++i) {
            LocalMessage text = new LocalMessage("item", "tier", "tiernumber", i);
            Iterator<IncursionPerk> tierLabel = new FormLocalLabel(text, new FontOptions(fontSize), -1, startX, -i * (perkRes + paddingBetweenPerks + additionalYPadding));
            this.perkTreeContent.addComponent(tierLabel);
            FormBreakLine tierBreakLine = this.perkTreeContent.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, tierLabel.getX(), tierLabel.getY() - breakLineOffset, width - 20, true));
            tierBreakLine.zIndex = -4;
            tierBreakLine.color = new Color(0.0f, 0.0f, 0.0f, 0.5f);
            this.setupSideTabletDisplay(i, (FormLocalLabel)((Object)tierLabel), altarData);
        }
        int minYPos = 0;
        int perkStartX = startX + 64;
        for (final IncursionPerk perk : IncursionPerksRegistry.getPerks()) {
            int yPos = -(perk.tier * (perkRes + paddingBetweenPerks + additionalYPadding) + perksYOffSet);
            minYPos = Math.min(yPos, minYPos);
            int xPos = perkStartX + (perk.xPositionOnPerkTree - 1) * (perkRes + paddingBetweenPerks);
            IncursionPerkFormButton perkComponent = new IncursionPerkFormButton(xPos, yPos, altarData, perk, altarContainer, this);
            this.perkFormButtons.put(perk, perkComponent);
            this.perkTreeContent.addComponent(perkComponent);
            if (!perk.locksAllOtherPerksOnTier()) continue;
            final int chainRes = 32;
            FormPosition perkLockingThisTierPosition = perkComponent.getPosition();
            int firstLineWidth = perkLockingThisTierPosition.getX() + perkComponent.getWidth() / 2 - startX;
            final int secondLineWidth = width - firstLineWidth;
            final int secondLineStartX = firstLineWidth + perkComponent.getWidth() / 2 + startX;
            while (!this.isDivisibleByValue(firstLineWidth, 32)) {
                --firstLineWidth;
            }
            final int finalWidth = firstLineWidth;
            final int firstLineStartX = perkLockingThisTierPosition.getX() - finalWidth;
            this.perkTreeContent.addComponent(new FormCustomDraw(-chainRes / 2, yPos + chainRes / 2, finalWidth, 32){

                @Override
                public boolean isMouseOver(InputEvent event) {
                    return false;
                }

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    if (!perk.hasObtainedPerk(altarData)) {
                        1.drawWidthComponent(new GameSprite(perkTreeChainTexture, 0, 0, chainRes, chainRes, chainRes, chainRes), new GameSprite(perkTreeChainTexture, 1, 0, chainRes, chainRes, chainRes, chainRes), new GameSprite(perkTreeChainTexture, 2, 0, chainRes, chainRes, chainRes, chainRes), firstLineStartX - 64, this.getY(), finalWidth + 64, new Color(255, 255, 255), false);
                        this.zIndex = 1;
                    } else {
                        this.zIndex = -10;
                    }
                }
            });
            this.perkTreeContent.addComponent(new FormCustomDraw(-chainRes / 2, yPos + chainRes / 2, finalWidth, 32){

                @Override
                public boolean isMouseOver(InputEvent event) {
                    return false;
                }

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    if (!perk.hasObtainedPerk(altarData)) {
                        2.drawWidthComponent(new GameSprite(perkTreeChainTexture, 0, 0, chainRes, chainRes, chainRes, chainRes), new GameSprite(perkTreeChainTexture, 1, 0, chainRes, chainRes, chainRes, chainRes), new GameSprite(perkTreeChainTexture, 2, 0, chainRes, chainRes, chainRes, chainRes), secondLineStartX, this.getY(), secondLineWidth, new Color(255, 255, 255), false);
                        this.zIndex = 1;
                    } else {
                        this.zIndex = -10;
                    }
                }
            });
        }
        this.perkTreeContent.setContentBox(new Rectangle(0, minYPos + perksYOffSet, this.perkTreeContent.getWidth(), -minYPos - perksYOffSet - additionalYPadding + 32));
        for (IncursionPerk incursionPerk : this.perkFormButtons.keySet()) {
            if (incursionPerk.prerequisitePerksRequired.isEmpty()) continue;
            this.connectPerksWithLines(incursionPerk, altarData, perkRes + paddingBetweenPerks + additionalYPadding);
        }
        FormBreakLine bottomBreakLine = this.perkTreeForm.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 0, height - 32, width, true));
        bottomBreakLine.color = breakLineBlackColor;
        int buttonWidth = 100;
        FormLocalTextButton closeButton = new FormLocalTextButton((GameMessage)new LocalMessage("ui", "closebutton"), startX, height - 27, buttonWidth, FormInputSize.SIZE_24, ButtonColor.BASE){};
        closeButton.onClicked(event -> {
            altarContainerForm.makeCurrent(altarContainerForm.placeTabletForm);
            client.getPlayer().setInventoryExtended(true);
        });
        this.perkTreeForm.addComponent(closeButton);
        this.heightPerPerk = perkRes + paddingBetweenPerks + additionalYPadding;
        int grayedOutBackgroundDefaultHeight = IncursionData.ITEM_TIER_UPGRADE_CAP * this.heightPerPerk + 8;
        this.tierGrayedOutBackground = new Form(this.perkTreeContent.getWidth(), grayedOutBackgroundDefaultHeight){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                this.setPosition(-5, -IncursionPerkTreeForm.this.perkTreeContent.getContentBox().height - 6);
                this.setWidth(IncursionPerkTreeForm.this.perkTreeContent.getWidth() + 10);
                IncursionPerkTreeForm.this.updateGrayBackgroundHeight(altarData);
                this.zIndex = -10;
            }
        };
        this.tierGrayedOutBackground.setBackground(GameBackground.indentBorderless);
        this.tierGrayedOutBackground.setHeight(grayedOutBackgroundDefaultHeight);
        this.tierGrayedOutBackground.zIndex = -10;
        this.tierGrayedOutBackground.setPosition(0, -this.perkTreeContent.getContentBox().height - 6);
        this.perkTreeContent.addComponent(this.tierGrayedOutBackground);
        FormLocalTextButton respecButton = new FormLocalTextButton(new LocalMessage("ui", "resetperktree"), width - buttonWidth - 10, height - 27, buttonWidth, FormInputSize.SIZE_24, ButtonColor.BASE){

            @Override
            protected void addTooltips(PlayerMob perspective) {
                if (altarData.obtainedPerkIDs.isEmpty()) {
                    return;
                }
                ListGameTooltips tooltips = new ListGameTooltips();
                tooltips.add(Localization.translate("incursion", "removefullperktreecost"));
                CanCraft canCraft = altarContainer.canAffordFullPerkTreeRespec(altarData);
                Ingredient[] fullAltarRespecIngredientCost = altarContainer.getFullAltarRespecIngredientCost(altarData);
                tooltips.add(fullAltarRespecIngredientCost[0].getTooltips(canCraft.haveIngredients[0], true));
                if (canCraft.canCraft()) {
                    String buyTip = Localization.translate("incursion", "respecperktree", "value", (Object)altarContainer.getAltarDustAmountFromFullPerkTreeRespec(altarData));
                    if (Input.lastInputIsController) {
                        tooltips.add(new InputTooltip(ControllerInput.MENU_SELECT, buyTip));
                    } else {
                        tooltips.add(new InputTooltip(-100, buyTip));
                    }
                }
                if (!this.getDrawText().equals(this.getText())) {
                    tooltips.add(this.getText());
                }
                if (!tooltips.isEmpty()) {
                    GameTooltipManager.addTooltip(tooltips, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
                }
            }

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                if (altarData.obtainedPerkIDs.isEmpty()) {
                    return;
                }
                super.draw(tickManager, perspective, renderBox);
            }

            @Override
            public boolean isMouseOver(InputEvent event) {
                if (altarData.obtainedPerkIDs.isEmpty()) {
                    return false;
                }
                return super.isMouseOver(event);
            }

            @Override
            public boolean isActive() {
                CanCraft canCraft = altarContainer.canAffordFullPerkTreeRespec(altarData);
                if (!canCraft.canCraft()) {
                    return false;
                }
                return super.isActive();
            }
        };
        respecButton.onClicked(event -> {
            CanCraft canCraft = altarContainer.canAffordFullPerkTreeRespec(altarData);
            if (!canCraft.canCraft()) {
                return;
            }
            ConfirmationForm respecPerkTreeConfirmationForm = new ConfirmationForm("respecPerkTreeConfirmationForm", 400, 600);
            GameMessageBuilder builder = new GameMessageBuilder();
            int coinCost = altarData.obtainedPerkIDs.size() * 1000;
            builder.append(new LocalMessage("incursion", "confirmrespecperktree1"));
            builder.append(new StaticMessage("\n\n"));
            builder.append(new LocalMessage("incursion", "confirmrespecperktree2"));
            builder.append(new StaticMessage("\n" + coinCost + " [item=coinstack]\n\n"));
            builder.append(new LocalMessage("incursion", "confirmrespecperktree3"));
            builder.append(new StaticMessage("\n" + altarContainer.getAltarDustAmountFromFullPerkTreeRespec(altarData) + " [item=altardust]"));
            respecPerkTreeConfirmationForm.setupConfirmation(content -> {
                FontOptions fontOptions = new FontOptions(16);
                FormFairTypeLabel fairTypeLabel = content.addComponent(new FormFairTypeLabel(new StaticMessage(""), fontOptions, FairType.TextAlign.CENTER, content.getWidth() / 2, 10));
                fairTypeLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(fontOptions.getSize()));
                fairTypeLabel.setMaxWidth(content.getWidth() - 20);
                fairTypeLabel.setText(builder);
            }, (GameMessage)new LocalMessage("ui", "confirmbutton"), (GameMessage)new LocalMessage("ui", "backbutton"), () -> {
                altarContainer.resetPerkTree.runAndSend();
                this.tierGrayedOutBackground.setHeight(grayedOutBackgroundDefaultHeight);
                this.makeCurrent(this.perkTreeForm);
            }, () -> this.makeCurrent(this.perkTreeForm));
            this.addAndMakeCurrentTemporary(respecPerkTreeConfirmationForm, () -> this.removeComponent(respecPerkTreeConfirmationForm));
        });
        this.perkTreeForm.addComponent(respecButton);
        this.perkTreeContent.setScrollY(-minYPos);
        this.perkTreeForm.addComponent(new FormLocalLabel(new LocalMessage("incursion", "altartierlevel", "value", altarData.getAltarTier()), new FontOptions(16), 0, width / 2, height - 24){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                this.setText(new LocalMessage("incursion", "altartierlevel", "value", altarData.getAltarTier()));
            }
        });
        this.makeCurrent(this.perkTreeForm);
    }

    public void updateGrayBackgroundHeight(AltarData altarData) {
        this.tierGrayedOutBackground.setHeight((IncursionData.ITEM_TIER_UPGRADE_CAP - altarData.getAltarTier() - 1) * this.heightPerPerk + 8);
    }

    private boolean isDivisibleByValue(int valueToCheck, int valueToBeDivisibleBy) {
        return valueToCheck % valueToBeDivisibleBy == 0;
    }

    private void setupSideTabletDisplay(int i, FormLocalLabel tierLabel, final AltarData altarData) {
        ArrayList<IncursionBiome> incursionsFromBaseTier = IncursionBiomeRegistry.getIncursionsFromBaseTier(i);
        if (!incursionsFromBaseTier.isEmpty()) {
            int yOffset = 0;
            int tabletsShownPerColumn = 4;
            int xColumns = 0;
            int yPadding = 30;
            int xPadding = 32;
            for (IncursionBiome incursionBiome : incursionsFromBaseTier) {
                final InventoryItem gatewayTablet = new InventoryItem("gatewaytablet");
                GatewayTabletItem.initializeCustomGateTablet(gatewayTablet, GameRandom.globalRandom, i, incursionBiome);
                GameBlackboard tooltipBlackboard = new GameBlackboard().set("hideModifierAndRewards", true).set("showDropTip", true);
                FairType fairType = new FairType();
                IncursionPerk tabletPerk = null;
                for (IncursionPerk perk : IncursionPerksRegistry.getPerks()) {
                    if (perk.getTabletIDForTabletDropPerks() != incursionBiome.getID()) continue;
                    tabletPerk = perk;
                }
                final IncursionPerk finalTabletPerk = tabletPerk;
                fairType.append(new FairItemGlyph(24, gatewayTablet){

                    @Override
                    public GameTooltips getTooltip(InventoryItem currentDrawnItem) {
                        if (finalTabletPerk != null) {
                            if (altarData.obtainedPerkIDs.contains(finalTabletPerk.getID())) {
                                return super.getTooltip(currentDrawnItem);
                            }
                        } else {
                            return super.getTooltip(currentDrawnItem);
                        }
                        return null;
                    }

                    @Override
                    public void drawIcon(InventoryItem currentDrawnItem, float x, float y, int size, float alpha) {
                        if (finalTabletPerk != null) {
                            if (altarData.obtainedPerkIDs.contains(finalTabletPerk.getID())) {
                                gatewayTablet.drawIcon(null, (int)x, (int)y, 32, new Color(1.0f, 1.0f, 1.0f, alpha));
                            }
                        } else {
                            gatewayTablet.drawIcon(null, (int)x, (int)y, 32, new Color(1.0f, 1.0f, 1.0f, alpha));
                        }
                    }
                }.setTooltipBlackboard(tooltipBlackboard));
                if (yOffset >= yPadding * tabletsShownPerColumn) {
                    yOffset = 0;
                    ++xColumns;
                }
                FormFairTypeLabel formFairTypeLabel = new FormFairTypeLabel("", tierLabel.getX() + xPadding * xColumns, tierLabel.getY() + 18 + yOffset);
                formFairTypeLabel.setCustomFairType(fairType);
                this.perkTreeContent.addComponent(formFairTypeLabel);
                yOffset += yPadding;
            }
        }
    }

    public void connectPerksWithLines(final IncursionPerk currentPerk, final AltarData altarData, int paddingBetweenPerks) {
        final GameTexture gameTexture = GameResources.perkTreeLines;
        int spriteResolution = gameTexture.getWidth() / 2;
        IncursionPerkFormButton currentPerkFormButton = this.perkFormButtons.get(currentPerk);
        final int heightBeforeLineTurn = paddingBetweenPerks / 2 + spriteResolution / 2;
        FormPosition currentPerkPosition = currentPerkFormButton.getPosition();
        final int currentPerkCenterX = currentPerkPosition.getX() + currentPerkFormButton.getWidth() / 2 - spriteResolution / 2;
        final int currentPerkCenterY = currentPerkPosition.getY() + currentPerkFormButton.getHeight() / 2;
        for (final IncursionPerk prerequisitePerk : currentPerk.prerequisitePerksRequired) {
            GameSprite endSprite;
            GameSprite startSprite;
            int startX;
            FormPosition prerequisitePerkPosition = this.perkFormButtons.get(prerequisitePerk).getPosition();
            final int prerequisitePerkCenterX = prerequisitePerkPosition.getX() + currentPerkFormButton.getWidth() / 2 - spriteResolution / 2;
            final int prerequisitePerkCenterY = prerequisitePerkPosition.getY() + currentPerkFormButton.getHeight() / 2;
            if (currentPerkPosition.getX() == prerequisitePerkPosition.getX()) {
                this.perkTreeContent.addComponent(new FormCustomDraw(currentPerkCenterX, currentPerkCenterY, Math.abs(prerequisitePerkCenterY - currentPerkCenterY), Math.abs(prerequisitePerkCenterY - currentPerkCenterY)){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        8.drawWidthComponent(new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), this.getX(), this.getY(), Math.abs(prerequisitePerkCenterY - currentPerkCenterY), IncursionPerkTreeForm.this.getGrayScaleColor(currentPerk, prerequisitePerk, altarData), true);
                        this.zIndex = IncursionPerkTreeForm.this.getZIndex(currentPerk, prerequisitePerk, altarData, 0);
                    }
                });
                continue;
            }
            if (currentPerkPosition.getY() == prerequisitePerkPosition.getY()) {
                int startX2 = Math.min(currentPerkCenterX, prerequisitePerkCenterX) + 16;
                this.perkTreeContent.addComponent(new FormCustomDraw(startX2, currentPerkCenterY - 16, 32, 32){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        9.drawWidthComponent(new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), this.getX(), this.getY(), Math.abs(currentPerkCenterX - prerequisitePerkCenterX), IncursionPerkTreeForm.this.getGrayScaleColor(currentPerk, prerequisitePerk, altarData), false);
                        this.zIndex = IncursionPerkTreeForm.this.getZIndex(currentPerk, prerequisitePerk, altarData, 0);
                    }
                });
                continue;
            }
            final int topLineWidth = Math.abs(prerequisitePerkCenterY - currentPerkCenterY) - heightBeforeLineTurn;
            this.perkTreeContent.addComponent(new FormCustomDraw(currentPerkCenterX, currentPerkCenterY, topLineWidth, topLineWidth){

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    10.drawWidthComponent(new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), this.getX(), this.getY(), topLineWidth, IncursionPerkTreeForm.this.getGrayScaleColor(currentPerk, prerequisitePerk, altarData), true);
                    this.zIndex = IncursionPerkTreeForm.this.getZIndex(currentPerk, prerequisitePerk, altarData, 0);
                }
            });
            if (currentPerkPosition.getX() > prerequisitePerkPosition.getX()) {
                startX = prerequisitePerkCenterX;
                startSprite = new GameSprite(gameTexture, 1, 0, 32, 32, 32, 32);
                endSprite = new GameSprite(gameTexture, 1, 0, 32, 32, 32, 32, true, true);
            } else {
                startX = currentPerkCenterX;
                startSprite = new GameSprite(gameTexture, 1, 0, 32, 32, 32, 32, false, true);
                endSprite = new GameSprite(gameTexture, 1, 0, 32, 32, 32, 32, true, false);
            }
            final int centerLineWidth = Math.abs(currentPerkCenterX - prerequisitePerkCenterX) + spriteResolution;
            this.perkTreeContent.addComponent(new FormCustomDraw(startX, prerequisitePerkCenterY - heightBeforeLineTurn, centerLineWidth, centerLineWidth){

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    11.drawWidthComponent(startSprite, new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), endSprite, this.getX(), this.getY(), centerLineWidth, IncursionPerkTreeForm.this.getGrayScaleColor(currentPerk, prerequisitePerk, altarData), false);
                    this.zIndex = IncursionPerkTreeForm.this.getZIndex(currentPerk, prerequisitePerk, altarData, 1);
                }
            });
            this.perkTreeContent.addComponent(new FormCustomDraw(prerequisitePerkCenterX, prerequisitePerkCenterY - heightBeforeLineTurn + spriteResolution, heightBeforeLineTurn, heightBeforeLineTurn){

                @Override
                public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                    12.drawWidthComponent(new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), new GameSprite(gameTexture, 0, 0, 32, 32, 32, 32), this.getX(), this.getY(), heightBeforeLineTurn, IncursionPerkTreeForm.this.getGrayScaleColor(currentPerk, prerequisitePerk, altarData), true);
                    this.zIndex = IncursionPerkTreeForm.this.getZIndex(currentPerk, prerequisitePerk, altarData, 0);
                }
            });
        }
    }

    public Color getGrayScaleColor(IncursionPerk perk, IncursionPerk prerequisitePerk, AltarData altarData) {
        if (perk.isPerkLocked(altarData)) {
            return new Color(100, 100, 100);
        }
        return altarData.obtainedPerkIDs.contains(perk.getID()) && altarData.obtainedPerkIDs.contains(prerequisitePerk.getID()) ? new Color(255, 255, 255) : new Color(100, 100, 100);
    }

    public int getZIndex(IncursionPerk perk, IncursionPerk prerequisitePerk, AltarData altarData, int centerLineZOffset) {
        if (perk.isPerkLocked(altarData)) {
            return -2 - centerLineZOffset;
        }
        return this.hasBothPerks(perk, prerequisitePerk, altarData) ? -1 : -2 - centerLineZOffset;
    }

    public boolean hasBothPerks(IncursionPerk currentPerk, IncursionPerk prerequisitePerk, AltarData altarData) {
        return altarData.obtainedPerkIDs.contains(currentPerk.getID()) && altarData.obtainedPerkIDs.contains(prerequisitePerk.getID());
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }
}

