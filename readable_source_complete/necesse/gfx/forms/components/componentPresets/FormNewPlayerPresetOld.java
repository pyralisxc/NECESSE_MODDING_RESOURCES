/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.componentPresets;

import java.awt.Color;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameEyes;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormHorizontalIntScroll;
import necesse.gfx.forms.components.FormHorizontalScroll;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPlayerIcon;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class FormNewPlayerPresetOld
extends Form {
    private PlayerMob newPlayer;
    public final FormPlayerIcon icon;
    public final FormHorizontalIntScroll rotate;
    public final FormHorizontalIntScroll hair;
    public final FormHorizontalIntScroll facialHair;
    public final FormHorizontalIntScroll hairColor;
    public final FormHorizontalIntScroll skin;
    public final FormHorizontalIntScroll eyes;
    public final FormSlider shirtRed;
    public final FormSlider shirtGreen;
    public final FormSlider shirtBlue;
    public final FormSlider shoesRed;
    public final FormSlider shoesGreen;
    public final FormSlider shoesBlue;
    public final FormLocalTextButton reset;
    public final FormLocalTextButton randomize;
    private PlayerMob undoPlayer;

    public FormNewPlayerPresetOld(int x, int y, int width) {
        super(width, 0);
        this.setPosition(x, y);
        this.drawBase = false;
        this.newPlayer = new PlayerMob(0L, null);
        this.icon = this.addComponent(new FormPlayerIcon(8, 4, 128, 128, this.newPlayer));
        int scrollX = 144;
        int scrollWidth = width - scrollX - 16;
        FormFlow scrollFlow = new FormFlow(5);
        this.reset = this.addComponent(new FormLocalTextButton("ui", "resetappearance", scrollX, scrollFlow.next(25), scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE));
        this.rotate = this.addComponent(new FormHorizontalIntScroll(scrollX, scrollFlow.next(20), scrollWidth, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "rotate"), 2, 0, 3));
        this.hair = this.addComponent(new FormHorizontalIntScroll(scrollX, scrollFlow.next(20), scrollWidth, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "hair"), 1, 0, GameHair.getTotalHair() - 1));
        this.facialHair = this.addComponent(new FormHorizontalIntScroll(scrollX, scrollFlow.next(20), scrollWidth, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "facialhair"), 1, 0, GameHair.getTotalFacialFeatures() - 1));
        this.hairColor = this.addComponent(new FormHorizontalIntScroll(scrollX, scrollFlow.next(20), scrollWidth, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "haircolor"), 0, 0, GameHair.getTotalHairColors() - 1));
        this.skin = this.addComponent(new FormHorizontalIntScroll(scrollX, scrollFlow.next(20), scrollWidth, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "skin"), 0, 0, GameSkin.getTotalSkins() - 1));
        this.eyes = this.addComponent(new FormHorizontalIntScroll(scrollX, scrollFlow.next(20), scrollWidth, FormHorizontalScroll.DrawOption.string, new LocalMessage("ui", "eyes"), 0, 0, GameEyes.getTotalColors() - 1));
        scrollFlow.next(15);
        this.randomize = this.addComponent(new FormLocalTextButton("ui", "randomappearance", 8, 120, 128, FormInputSize.SIZE_20, ButtonColor.BASE));
        int slidersStartY = Math.max(scrollFlow.next(), this.icon.getY() + this.icon.getHeight() + 8);
        FontOptions sliderFontOptions = new FontOptions(12);
        int sliderWidth = width / 2 - 20;
        FormFlow shirtColorFlow = new FormFlow(slidersStartY);
        this.addComponent(new FormLocalLabel("ui", "shirtcolor", new FontOptions(12), -1, 8, shirtColorFlow.next(15)));
        this.addComponent(new FormLocalTextButton("ui", "selectcolor", 8, shirtColorFlow.next(25), sliderWidth, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> {
            final Color startColor = new Color(this.newPlayer.look.getShirtColor().getRGB());
            ((FormButton)e.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(e.from, startColor){

                @Override
                public void onApplied(Color color) {
                    if (color != null) {
                        color = HumanLook.limitClothesColor(color);
                        ((FormNewPlayerPresetOld)FormNewPlayerPresetOld.this).newPlayer.look.setShirtColor(color);
                        FormNewPlayerPresetOld.this.updateLook();
                        FormNewPlayerPresetOld.this.resetUndo();
                        FormNewPlayerPresetOld.this.updateComponents();
                    } else {
                        ((FormNewPlayerPresetOld)FormNewPlayerPresetOld.this).newPlayer.look.setShirtColor(startColor);
                        FormNewPlayerPresetOld.this.updateLook();
                    }
                }

                @Override
                public void onSelected(Color color) {
                    color = HumanLook.limitClothesColor(color);
                    ((FormNewPlayerPresetOld)FormNewPlayerPresetOld.this).newPlayer.look.setShirtColor(color);
                    FormNewPlayerPresetOld.this.updateLook();
                }
            });
        });
        this.shirtRed = this.addComponent(shirtColorFlow.nextY(new FormLocalSlider("ui", "colorred", 8, 190, this.newPlayer.look.getShirtColor().getRed(), 50, 200, sliderWidth, sliderFontOptions), 5));
        this.shirtGreen = this.addComponent(shirtColorFlow.nextY(new FormLocalSlider("ui", "colorgreen", 8, 215, this.newPlayer.look.getShirtColor().getGreen(), 50, 200, sliderWidth, sliderFontOptions), 5));
        this.shirtBlue = this.addComponent(shirtColorFlow.nextY(new FormLocalSlider("ui", "colorblue", 8, 240, this.newPlayer.look.getShirtColor().getBlue(), 50, 200, sliderWidth, sliderFontOptions), 5));
        FormFlow shoesColorFlow = new FormFlow(slidersStartY);
        this.addComponent(new FormLocalLabel("ui", "shoescolor", new FontOptions(12), -1, width - sliderWidth - 8, shoesColorFlow.next(15)));
        this.addComponent(new FormLocalTextButton("ui", "selectcolor", width - sliderWidth - 8, shoesColorFlow.next(25), sliderWidth, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> {
            final Color startColor = new Color(this.newPlayer.look.getShoesColor().getRGB());
            ((FormButton)e.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(e.from, startColor){

                @Override
                public void onApplied(Color color) {
                    if (color != null) {
                        color = HumanLook.limitClothesColor(color);
                        ((FormNewPlayerPresetOld)FormNewPlayerPresetOld.this).newPlayer.look.setShoesColor(color);
                        FormNewPlayerPresetOld.this.updateLook();
                        FormNewPlayerPresetOld.this.resetUndo();
                        FormNewPlayerPresetOld.this.updateComponents();
                    } else {
                        ((FormNewPlayerPresetOld)FormNewPlayerPresetOld.this).newPlayer.look.setShoesColor(startColor);
                        FormNewPlayerPresetOld.this.updateLook();
                    }
                }

                @Override
                public void onSelected(Color color) {
                    color = HumanLook.limitClothesColor(color);
                    ((FormNewPlayerPresetOld)FormNewPlayerPresetOld.this).newPlayer.look.setShoesColor(color);
                    FormNewPlayerPresetOld.this.updateLook();
                }
            });
        });
        this.shoesRed = this.addComponent(shoesColorFlow.nextY(new FormLocalSlider("ui", "colorred", width - sliderWidth - 8, 190, this.newPlayer.look.getShoesColor().getRed(), 50, 200, sliderWidth, sliderFontOptions), 5));
        this.shoesGreen = this.addComponent(shoesColorFlow.nextY(new FormLocalSlider("ui", "colorgreen", width - sliderWidth - 8, 215, this.newPlayer.look.getShoesColor().getGreen(), 50, 200, sliderWidth, sliderFontOptions), 5));
        this.shoesBlue = this.addComponent(shoesColorFlow.nextY(new FormLocalSlider("ui", "colorblue", width - sliderWidth - 8, 240, this.newPlayer.look.getShoesColor().getBlue(), 50, 200, sliderWidth, sliderFontOptions), 5));
        this.setHeight(Math.max(shirtColorFlow.next(), shoesColorFlow.next()));
        this.loadDefault();
        this.rotate.onChanged(e -> {
            this.icon.setRotation((Integer)this.rotate.getValue());
            this.resetUndo();
        });
        this.skin.onChanged(e -> {
            this.newPlayer.look.setSkin((Integer)this.skin.getValue());
            this.resetUndo();
        });
        this.hair.onChanged(e -> {
            System.out.println("Hair number: " + this.hair.getValue());
            this.newPlayer.look.setHair((Integer)this.hair.getValue());
            this.resetUndo();
        });
        this.facialHair.onChanged(e -> {
            System.out.println("Beard number: " + this.facialHair.getValue());
            this.newPlayer.look.setFacialFeature((Integer)this.facialHair.getValue());
            this.resetUndo();
        });
        this.hairColor.onChanged(e -> {
            this.newPlayer.look.setHairColor((Integer)this.hairColor.getValue());
            this.resetUndo();
        });
        this.eyes.onChanged(e -> {
            this.newPlayer.look.setEyeColor((Integer)this.eyes.getValue());
            this.resetUndo();
        });
        this.shirtRed.onChanged(e -> {
            this.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
            this.updateLook();
            this.resetUndo();
        });
        this.shirtGreen.onChanged(e -> {
            this.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
            this.updateLook();
            this.resetUndo();
        });
        this.shirtBlue.onChanged(e -> {
            this.newPlayer.look.setShirtColor(new Color(this.shirtRed.getValue(), this.shirtGreen.getValue(), this.shirtBlue.getValue()));
            this.updateLook();
            this.resetUndo();
        });
        this.shoesRed.onChanged(e -> {
            this.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
            this.updateLook();
            this.resetUndo();
        });
        this.shoesGreen.onChanged(e -> {
            this.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
            this.updateLook();
            this.resetUndo();
        });
        this.shoesBlue.onChanged(e -> {
            this.newPlayer.look.setShoesColor(new Color(this.shoesRed.getValue(), this.shoesGreen.getValue(), this.shoesBlue.getValue()));
            this.updateLook();
            this.resetUndo();
        });
        this.reset.onClicked(e -> {
            if (this.undoPlayer != null) {
                this.undoDefault();
            } else {
                this.loadDefault(true);
            }
        });
        this.randomize.onClicked(e -> {
            this.randomize();
            this.resetUndo();
        });
    }

    public void reset() {
        this.resetUndo();
        this.setPlayer(new PlayerMob(0L, null));
    }

    private void loadDefault(boolean undoActive) {
        if (undoActive) {
            this.undoPlayer = this.newPlayer;
            this.reset.setLocalization("ui", "undoreset");
        }
        this.setPlayer(new PlayerMob(0L, null));
    }

    public void loadDefault() {
        this.loadDefault(false);
    }

    private void undoDefault() {
        if (this.undoPlayer == null) {
            return;
        }
        this.newPlayer = this.undoPlayer;
        this.setPlayer(this.newPlayer);
        this.resetUndo();
    }

    private void resetUndo() {
        if (this.undoPlayer == null) {
            return;
        }
        this.undoPlayer = null;
        this.reset.setLocalization("ui", "resetappearance");
    }

    private void randomize() {
        this.newPlayer.look.randomizeLook(false);
        this.updateComponents();
    }

    public void setPlayer(PlayerMob player) {
        this.newPlayer = player;
        this.updateComponents();
    }

    public void setLook(HumanLook look) {
        this.newPlayer.look = look;
        this.updateComponents();
    }

    public void updateComponents() {
        this.hair.setValue(this.newPlayer.look.getHair() % GameHair.getTotalHair());
        this.newPlayer.look.setHair((Integer)this.hair.getValue());
        this.facialHair.setValue(this.newPlayer.look.getFacialFeature() % GameHair.getTotalFacialFeatures());
        this.newPlayer.look.setFacialFeature((Integer)this.facialHair.getValue());
        this.hairColor.setValue(this.newPlayer.look.getHairColor() % GameHair.getTotalHairColors());
        this.newPlayer.look.setHairColor((Integer)this.hairColor.getValue());
        this.skin.setValue(this.newPlayer.look.getSkin() % GameSkin.getTotalSkins());
        this.newPlayer.look.setSkin((Integer)this.skin.getValue());
        this.eyes.setValue(this.newPlayer.look.getEyeColor());
        this.shirtRed.setValue(this.newPlayer.look.getShirtColor().getRed());
        this.shirtGreen.setValue(this.newPlayer.look.getShirtColor().getGreen());
        this.shirtBlue.setValue(this.newPlayer.look.getShirtColor().getBlue());
        this.shoesRed.setValue(this.newPlayer.look.getShoesColor().getRed());
        this.shoesGreen.setValue(this.newPlayer.look.getShoesColor().getGreen());
        this.shoesBlue.setValue(this.newPlayer.look.getShoesColor().getBlue());
        this.icon.setPlayer(this.newPlayer);
        this.updateLook();
    }

    protected void updateLook() {
        this.newPlayer.getInv().giveLookArmor();
    }

    public PlayerMob getNewPlayer() {
        this.newPlayer.getInv().giveStarterItems();
        return this.newPlayer;
    }
}

