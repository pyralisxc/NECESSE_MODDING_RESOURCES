/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.componentPresets;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameEyes;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.HumanLook;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.fairType.FairColorChangeGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairSpacerGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormColorPicker;
import necesse.gfx.forms.components.FormContentVarToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPlayerIcon;
import necesse.gfx.forms.components.FormTextureButton;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.FairTypeTooltip;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShirtArmorItem;
import necesse.inventory.item.armorItem.cosmetics.misc.ShoesArmorItem;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.light.GameLight;

public class FormNewPlayerPreset
extends Form {
    public static Color[] DEFAULT_SHIRT_AND_SHOES_COLORS = new Color[]{new Color(0x191919), new Color(0x4B4B4B), new Color(0x969696), new Color(0xE1E1E1), new Color(14249068), new Color(0xD90000), new Color(0x800000), new Color(14262124), new Color(14247168), new Color(8403968), new Color(14276460), new Color(14275840), new Color(8420608), new Color(8313196), new Color(2414848), new Color(1409024), new Color(7133621), new Color(55696), new Color(32853), new Color(7127001), new Color(42713), new Color(25216), new Color(7107289), new Color(3033), new Color(1664), new Color(10775769), new Color(7340249), new Color(4325504), new Color(14249157), new Color(14221489), new Color(0x800068)};
    private static final FormInputSize BUTTON_SIZE;
    private PlayerMob newPlayer;
    private final boolean allowSupernaturalChanges;
    private final boolean allowClothesChance;
    public final FormPlayerIcon icon;

    public FormNewPlayerPreset(int x, int y, int width, boolean allowSupernaturalChanges, boolean allowClothesChance) {
        super(width, 0);
        this.setPosition(x, y);
        this.drawBase = false;
        this.newPlayer = new PlayerMob(0L, null);
        this.allowSupernaturalChanges = allowSupernaturalChanges;
        this.allowClothesChance = allowClothesChance;
        FormFlow flow = new FormFlow(5);
        this.addComponent(flow.nextY(new FormLocalLabel("ui", "clickplayerrotate", new FontOptions(12), 0, x + width / 2, 0, width - 20)));
        int iconY = flow.next(128);
        this.icon = this.addComponent(new FormPlayerIcon(x + width / 2 - 64, iconY, 128, 128, this.newPlayer){

            @Override
            public void modifyHumanDrawOptions(HumanDrawOptions drawOptions) {
                super.modifyHumanDrawOptions(drawOptions);
                FormNewPlayerPreset.this.modifyHumanDrawOptions(drawOptions);
            }
        });
        this.icon.acceptRightClicks = true;
        this.icon.onClicked(e -> {
            if (e.event.getID() == -99) {
                this.icon.setRotation(this.icon.getRotation() - 1);
            } else {
                this.icon.setRotation(this.icon.getRotation() + 1);
            }
        });
        FormTextureButton leftRotateButton = this.addComponent(new FormTextureButton(x + width / 2 - 64 + 15, iconY + 64 + 20, () -> new GameSprite(this.getInterfaceStyle().rotate_arrow).mirrorX().mirrorY(), -1, 128, FairType.TextAlign.RIGHT, FairType.TextAlign.CENTER){

            @Override
            public Color getDrawColor() {
                return this.getButtonState().textColorGetter.apply(this.getInterfaceStyle());
            }
        }, 10);
        FormTextureButton rightRotateButton = this.addComponent(new FormTextureButton(x + width / 2 + 64 - 15, iconY + 64 + 20, () -> new GameSprite(this.getInterfaceStyle().rotate_arrow), -1, 128, FairType.TextAlign.LEFT, FairType.TextAlign.CENTER){

            @Override
            public Color getDrawColor() {
                return this.getButtonState().textColorGetter.apply(this.getInterfaceStyle());
            }
        }, 10);
        leftRotateButton.acceptRightClicks = true;
        leftRotateButton.onClicked(e -> {
            if (e.event.getID() == -99) {
                this.icon.setRotation(this.icon.getRotation() + 1);
            } else {
                this.icon.setRotation(this.icon.getRotation() - 1);
            }
        });
        rightRotateButton.acceptRightClicks = true;
        rightRotateButton.onClicked(e -> {
            if (e.event.getID() == -99) {
                this.icon.setRotation(this.icon.getRotation() + 1);
            } else {
                this.icon.setRotation(this.icon.getRotation() - 1);
            }
        });
        FormSwitcher contentSwitcher = this.addComponent(new FormSwitcher());
        contentSwitcher.useInactiveHitBoxes = true;
        ArrayList<Section> sections = this.getSections(s -> s.selectionContent != null && contentSwitcher.isCurrent(s.selectionContent), width);
        int buttonPadding = 1;
        int totalButtonWidth = FormNewPlayerPreset.BUTTON_SIZE.height + buttonPadding * 2;
        int buttonsPerRow = GameMath.limit(width / totalButtonWidth, 1, sections.size());
        int totalRows = (int)Math.ceil((double)sections.size() / (double)buttonsPerRow);
        int startX = 0;
        int startY = flow.next();
        for (int i = 0; i < sections.size(); ++i) {
            Section section = sections.get(i);
            int column = i % buttonsPerRow;
            int row = i / buttonsPerRow;
            int buttonsThisRow = Math.min(sections.size() - buttonsPerRow * row, buttonsPerRow);
            int xOffset = width / 2 - buttonsThisRow * totalButtonWidth / 2 - buttonPadding;
            int buttonX = startX + xOffset + column * (FormNewPlayerPreset.BUTTON_SIZE.height + buttonPadding * 2) + buttonPadding;
            int buttonY = startY + row * (FormNewPlayerPreset.BUTTON_SIZE.height + buttonPadding * 2) + buttonPadding;
            this.addComponent(section.button);
            if (section.selectionContent != null) {
                contentSwitcher.addComponent(section.selectionContent);
            }
            section.button.setPosition(buttonX, buttonY);
            section.button.onClicked(e -> section.onClicked(contentSwitcher));
        }
        flow.next(totalRows * (FormNewPlayerPreset.BUTTON_SIZE.height + buttonPadding * 2) + 5);
        int maxSectionHeight = 0;
        Form first = null;
        for (Section section : sections) {
            if (section.selectionContent == null) continue;
            if (first == null) {
                first = section.selectionContent;
            }
            section.selectionContent.setPosition(0, flow.next());
            maxSectionHeight = Math.max(maxSectionHeight, section.selectionContent.getHeight());
        }
        if (first != null) {
            contentSwitcher.makeCurrent(first);
        }
        flow.next(maxSectionHeight);
        flow.next(10);
        this.setHeight(flow.next());
        this.reset();
    }

    protected ArrayList<Section> getSections(Predicate<Section> isCurrent, int width) {
        ArrayList<Section> sections = new ArrayList<Section>();
        sections.add(new Section(new DrawButtonFunction(){

            @Override
            public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                ButtonIcon icon = FormNewPlayerPreset.this.getInterfaceStyle().inventory_sort;
                Color color = (Color)icon.colorGetter.apply(button.getButtonState());
                icon.texture.initDraw().color(color).posMiddle(drawX + width / 2, drawY + height / 2).draw();
            }
        }, (GameMessage)new LocalMessage("ui", "randomappearance"), null, (Predicate)isCurrent){

            @Override
            public void onClicked(FormSwitcher switcher) {
                FormNewPlayerPreset.this.randomize();
                FormNewPlayerPreset.this.onChanged();
            }
        });
        if (this.allowSupernaturalChanges) {
            sections.add(new Section(new DrawButtonFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                    HumanLook look = new HumanLook(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look);
                    look.setHair(0);
                    look.setFacialFeature(0);
                    HumanDrawOptions humanOptions = new HumanDrawOptions(null, look, false);
                    humanOptions.drawEyes(false);
                    Point offset = FormNewPlayerPreset.this.getSkinFaceDrawOffset();
                    Settler.getHumanFaceDrawOptions(humanOptions, BUTTON_SIZE.height, drawX + offset.x, drawY + offset.y).draw();
                }
            }, new LocalMessage("ui", "skincolor"), this.getSelectionContent(BUTTON_SIZE, width, GameSkin.getTotalSkins(), new SelectionButtonDrawFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                    HumanLook look = new HumanLook(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look);
                    look.setHair(0);
                    look.setFacialFeature(0);
                    look.setSkin(id);
                    HumanDrawOptions humanOptions = new HumanDrawOptions(null, look, false);
                    Point offset = FormNewPlayerPreset.this.getSkinFaceDrawOffset();
                    Settler.getHumanFaceDrawOptions(humanOptions, button.size.height, drawX + offset.x, drawY + offset.y).draw();
                }
            }, id -> id == this.newPlayer.look.getSkin(), (id, event) -> {
                this.newPlayer.look.setSkin((int)id);
                this.onChanged();
            }, this::getSkinColorCost), isCurrent));
            sections.add(new Section(new DrawButtonFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                    HumanLook look = new HumanLook(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look);
                    look.setHair(0);
                    look.setFacialFeature(0);
                    HumanDrawOptions humanOptions = new HumanDrawOptions(null, look, false);
                    GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
                    Point offset = FormNewPlayerPreset.this.getEyeTypeFaceDrawOffset();
                    Settler.getHumanFaceDrawOptions(humanOptions, button.size.height * 2, drawX + offset.x, drawY + offset.y, options -> options.sprite(0, 3).dir(3)).draw();
                    GameTexture.overrideBlendQuality = null;
                }
            }, new LocalMessage("ui", "eyes"), this.combineContent(this.addHeader(new LocalMessage("ui", "eyetype"), 12, this.getSelectionContent(BUTTON_SIZE, width, GameEyes.getTotalEyeTypes(), new SelectionButtonDrawFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                    HumanLook look = new HumanLook(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look);
                    look.setHair(0);
                    look.setFacialFeature(0);
                    look.setEyeType(id);
                    HumanDrawOptions humanOptions = new HumanDrawOptions(null, look, false);
                    GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
                    Point offset = FormNewPlayerPreset.this.getEyeTypeFaceDrawOffset();
                    Settler.getHumanFaceDrawOptions(humanOptions, button.size.height * 2, drawX + offset.x, drawY + offset.y, options -> options.sprite(0, 3).dir(3)).draw();
                    GameTexture.overrideBlendQuality = null;
                }
            }, id -> id == this.newPlayer.look.getEyeType(), (id, event) -> {
                this.newPlayer.look.setEyeType((int)id);
                this.onChanged();
            }, this::getEyeTypeCost)), this.addHeader(new LocalMessage("ui", "eyecolor"), 12, this.getSelectionContent(BUTTON_SIZE, width, GameEyes.getTotalColors(), new SelectionButtonDrawFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                    HumanLook look = new HumanLook(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look);
                    look.setHair(0);
                    look.setFacialFeature(0);
                    look.setEyeColor(id);
                    HumanDrawOptions humanOptions = new HumanDrawOptions(null, look, false);
                    GameTexture.overrideBlendQuality = GameTexture.BlendQuality.NEAREST;
                    Point offset = FormNewPlayerPreset.this.getEyeColorFaceDrawOffset();
                    Settler.getHumanFaceDrawOptions(humanOptions, button.size.height * 2, drawX + offset.x, drawY + offset.y, options -> options.sprite(0, 3).dir(3)).draw();
                    GameTexture.overrideBlendQuality = null;
                }
            }, id -> id == this.newPlayer.look.getEyeColor(), (id, event) -> {
                this.newPlayer.look.setEyeColor((int)id);
                this.onChanged();
            }, this::getEyeColorCost))), isCurrent));
        }
        sections.add(new Section(new DrawButtonFunction(){

            @Override
            public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                int hairStyleIndex = ((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getHair();
                GameTexture wigTexture = GameHair.getHair(hairStyleIndex).getWigTexture(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getHairColor());
                wigTexture.initDraw().size(button.size.height).posMiddle(drawX + width / 2, drawY + height / 2).draw();
            }
        }, new LocalMessage("ui", "hairstyle"), this.getSelectionContentIcons(BUTTON_SIZE, width, GameHair.getTotalHair(), id -> new GameSprite(GameHair.getHair(id).getWigTexture(this.newPlayer.look.getHairColor()), FormNewPlayerPreset.BUTTON_SIZE.height), id -> id == this.newPlayer.look.getHair(), (id, event) -> {
            this.newPlayer.look.setHair((int)id);
            this.onChanged();
        }, this::getHairStyleCost), isCurrent));
        sections.add(new Section(new DrawButtonFunction(){

            @Override
            public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                int hairStyleIndex = ((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getFacialFeature();
                GameTexture wigTexture = GameHair.getFacialFeature(hairStyleIndex).getWigTexture(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getHairColor());
                wigTexture.initDraw().size(button.size.height).posMiddle(drawX + width / 2, drawY + height / 2).draw();
            }
        }, new LocalMessage("ui", "facialhair"), this.getSelectionContentIcons(BUTTON_SIZE, width, GameHair.getTotalFacialFeatures(), id -> new GameSprite(GameHair.getFacialFeature(id).getWigTexture(this.newPlayer.look.getHairColor()), FormNewPlayerPreset.BUTTON_SIZE.height), id -> id == this.newPlayer.look.getFacialFeature(), (id, event) -> {
            this.newPlayer.look.setFacialFeature((int)id);
            this.onChanged();
        }, this::getFacialFeatureCost), isCurrent));
        sections.add(new Section(new DrawButtonFunction(){

            @Override
            public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                int hairColorIndex = ((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getHairColor();
                Color color = GameHair.colors.getSkinColor((int)hairColorIndex).colors.get(3);
                FormNewPlayerPreset.this.getInterfaceStyle().paintbrush_grayscale.initDraw().color(color).posMiddle(drawX + width / 2, drawY + height / 2).draw();
                FormNewPlayerPreset.this.getInterfaceStyle().paintbrush_handle.initDraw().posMiddle(drawX + width / 2, drawY + height / 2).draw();
            }
        }, new LocalMessage("ui", "haircolor"), this.getSelectionContent(BUTTON_SIZE, width, GameHair.getTotalHairColors(), new SelectionButtonDrawFunction(){

            @Override
            public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                if (((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getHair() == 0) {
                    Color color = GameHair.colors.getSkinColor((int)id).colors.get(3);
                    FormNewPlayerPreset.this.getInterfaceStyle().paintbrush_grayscale.initDraw().color(color).posMiddle(drawX + width / 2, drawY + height / 2).draw();
                    FormNewPlayerPreset.this.getInterfaceStyle().paintbrush_handle.initDraw().posMiddle(drawX + width / 2, drawY + height / 2).draw();
                } else {
                    GameSprite hairSprite = new GameSprite(GameHair.getHair(((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getHair()).getWigTexture(id), BUTTON_SIZE.height);
                    hairSprite.initDraw().light(new GameLight(current || hovering ? 150.0f : 136.36363f)).posMiddle(drawX + width / 2, drawY + height / 2).draw();
                }
            }
        }, id -> id == this.newPlayer.look.getHairColor(), (id, event) -> {
            this.newPlayer.look.setHairColor((int)id);
            this.onChanged();
        }, this::getHairColorCost), isCurrent));
        if (this.allowClothesChance) {
            sections.add(new Section(new DrawButtonFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                    InventoryItem item = ShirtArmorItem.addColorData(new InventoryItem("shirt"), ((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getShirtColor());
                    int size = Math.min(width, height);
                    item.drawIcon(null, drawX + width / 2 - size / 2, drawY + height / 2 - size / 2, size, null);
                }
            }, new LocalMessage("ui", "shirtcolor"), this.getSelectionColorOrCustom(BUTTON_SIZE, width, DEFAULT_SHIRT_AND_SHOES_COLORS, new SelectionButtonDrawFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                    Color color = DEFAULT_SHIRT_AND_SHOES_COLORS[id];
                    InventoryItem item = ShoesArmorItem.addColorData(new InventoryItem("shirt"), color);
                    int size = Math.min(width, height);
                    item.drawIcon(null, drawX + width / 2 - size / 2, drawY + height / 2 - size / 2, size, null);
                }
            }, () -> this.newPlayer.look.getShirtColor(), color -> {
                color = HumanLook.limitClothesColor(color);
                this.newPlayer.look.setShirtColor((Color)color);
                this.updateLook();
            }, color -> {
                color = HumanLook.limitClothesColor(color);
                this.newPlayer.look.setShirtColor((Color)color);
                this.updateLook();
                this.updateComponents();
                this.onChanged();
            }, this::getShirtColorCost), isCurrent));
            sections.add(new Section(new DrawButtonFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                    InventoryItem item = ShoesArmorItem.addColorData(new InventoryItem("shoes"), ((FormNewPlayerPreset)FormNewPlayerPreset.this).newPlayer.look.getShoesColor());
                    int size = Math.min(width, height);
                    item.drawIcon(null, drawX + width / 2 - size / 2, drawY + height / 2 - size / 2, size, null);
                }
            }, new LocalMessage("ui", "shoescolor"), this.getSelectionColorOrCustom(BUTTON_SIZE, width, DEFAULT_SHIRT_AND_SHOES_COLORS, new SelectionButtonDrawFunction(){

                @Override
                public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                    Color color = DEFAULT_SHIRT_AND_SHOES_COLORS[id];
                    InventoryItem item = ShoesArmorItem.addColorData(new InventoryItem("shoes"), color);
                    int size = Math.min(width, height);
                    item.drawIcon(null, drawX + width / 2 - size / 2, drawY + height / 2 - size / 2, size, null);
                }
            }, () -> this.newPlayer.look.getShoesColor(), color -> {
                color = HumanLook.limitClothesColor(color);
                this.newPlayer.look.setShoesColor((Color)color);
                this.updateLook();
            }, color -> {
                color = HumanLook.limitClothesColor(color);
                this.newPlayer.look.setShoesColor((Color)color);
                this.updateLook();
                this.updateComponents();
                this.onChanged();
            }, this::getShoesColorCost), isCurrent));
        }
        return sections;
    }

    public Point getSkinFaceDrawOffset() {
        return new Point(-3, -4);
    }

    public Point getEyeTypeFaceDrawOffset() {
        return new Point(-22, -26);
    }

    public Point getEyeColorFaceDrawOffset() {
        return new Point(-22, -26);
    }

    public Form getSelectionContentIcons(FormInputSize buttonSize, int width, int count, IntFunction<GameSprite> buttonContent, IntPredicate isCurrent, BiConsumer<Integer, FormInputEvent<FormButton>> onClicked, Function<Integer, ArrayList<InventoryItem>> costGetter) {
        return this.getSelectionContent(buttonSize, width, count, (button, id, drawX, drawY, w, h, current, hovering) -> {
            GameSprite sprite = (GameSprite)buttonContent.apply(id);
            if (sprite != null) {
                sprite.initDraw().light(new GameLight(current || hovering ? 150.0f : 136.36363f)).posMiddle(drawX + w / 2, drawY + h / 2).draw();
            }
        }, isCurrent, onClicked, costGetter);
    }

    public Form getSelectionContentColors(FormInputSize buttonSize, int width, int count, IntFunction<Color> buttonColor, IntPredicate isCurrent, BiConsumer<Integer, FormInputEvent<FormButton>> onClicked, Function<Integer, ArrayList<InventoryItem>> costGetter) {
        return this.getSelectionContent(buttonSize, width, count, (button, id, drawX, drawY, w, h, current, hovering) -> {
            int buttonExtra = button.size.buttonDownContentDrawOffset;
            Renderer.initQuadDraw(w, h + buttonExtra).colorLight((Color)buttonColor.apply(id), new GameLight(current || hovering ? 150.0f : 120.0f)).draw(drawX, drawY - buttonExtra);
        }, isCurrent, onClicked, costGetter);
    }

    public Form getSelectionContentNumber(FormInputSize buttonSize, int width, int count, IntPredicate isCurrent, BiConsumer<Integer, FormInputEvent<FormButton>> onClicked, Function<Integer, ArrayList<InventoryItem>> costGetter) {
        return this.getSelectionContent(buttonSize, width, count, (button, id, drawX, drawY, w, h, current, hovering) -> {
            FontOptions fontOptions = button.size.getFontOptions().color(this.getInterfaceStyle().activeTextColor);
            String text = "" + (id + 1);
            int textWidth = FontManager.bit.getWidthCeil(text, fontOptions);
            FontManager.bit.drawString(drawX + w / 2 - textWidth / 2, drawY + button.size.fontDrawOffset - 2, text, fontOptions);
        }, isCurrent, onClicked, costGetter);
    }

    public Form getSelectionColorOrCustom(FormInputSize buttonSize, int width, final Color[] colors, final SelectionButtonDrawFunction contentDraw, Supplier<Color> currentColorGetter, final Consumer<Color> onSelected, final Consumer<Color> onApply, Function<Color, ArrayList<InventoryItem>> costGetter) {
        return this.getSelectionContent(buttonSize, width, colors.length + 1, new SelectionButtonDrawFunction(){

            @Override
            public void draw(FormContentVarToggleButton button, int id, int drawX, int drawY, int width, int height, boolean current, boolean hovering) {
                if (id < colors.length) {
                    contentDraw.draw(button, id, drawX, drawY, width, height, current, hovering);
                } else {
                    int buttonExtra = button.size.buttonDownContentDrawOffset;
                    FormColorPicker.drawHueBar(drawX, drawY - buttonExtra, width, height + buttonExtra, hue -> Color.getHSBColor(hue.floatValue(), 1.0f, current || hovering ? 1.0f : 0.75f));
                }
            }
        }, id -> id < colors.length && ((Color)currentColorGetter.get()).equals(colors[id]), (id, event) -> {
            if (id < colors.length) {
                Color color = colors[id];
                onApply.accept(color);
            } else {
                final Color startColor = (Color)currentColorGetter.get();
                ((FormButton)event.from).getManager().openFloatMenu(new ColorSelectorFloatMenu(event.from, startColor){

                    @Override
                    public void onApplied(Color color) {
                        if (color == null) {
                            onApply.accept(startColor);
                        } else {
                            onApply.accept(color);
                        }
                    }

                    @Override
                    public void onSelected(Color color) {
                        onSelected.accept(color);
                    }
                });
            }
        }, id -> {
            if (id < colors.length) {
                return (ArrayList)costGetter.apply(colors[id]);
            }
            return (ArrayList)costGetter.apply(null);
        });
    }

    public Form getSelectionContent(FormInputSize buttonSize, int width, int count, SelectionButtonDrawFunction contentDraw, IntPredicate isCurrent, BiConsumer<Integer, FormInputEvent<FormButton>> onClicked, Function<Integer, ArrayList<InventoryItem>> costGetter) {
        return this.getSelectionContent(buttonSize, width, count, contentDraw, isCurrent, onClicked, costGetter, null);
    }

    public Form getSelectionContent(FormInputSize buttonSize, int width, int count, final SelectionButtonDrawFunction contentDraw, IntPredicate isCurrent, BiConsumer<Integer, FormInputEvent<FormButton>> onClicked, final Function<Integer, ArrayList<InventoryItem>> costGetter, final Function<Integer, GameMessage> tooltipGetter) {
        Form form = new Form(width, 10);
        form.drawBase = false;
        int contentPadding = 4;
        int buttonPadding = 1;
        int totalButtonWidth = buttonSize.height + buttonPadding * 2;
        int buttonsPerRow = GameMath.limit(width / totalButtonWidth, 1, count);
        int totalRows = (int)Math.ceil((double)count / (double)buttonsPerRow);
        int i = 0;
        while (i < count) {
            int column = i % buttonsPerRow;
            int row = i / buttonsPerRow;
            int buttonsThisRow = Math.min(count - buttonsPerRow * row, buttonsPerRow);
            int xOffset = width / 2 - buttonsThisRow * totalButtonWidth / 2 - buttonPadding;
            int buttonX = contentPadding + xOffset + column * totalButtonWidth + buttonPadding;
            int buttonY = contentPadding + row * totalButtonWidth + buttonPadding;
            final int finalI = i++;
            FormContentVarToggleButton button = form.addComponent(new FormContentVarToggleButton(buttonX, buttonY, buttonSize.height, buttonSize, ButtonColor.BASE, () -> isCurrent.test(finalI)){

                @Override
                protected void drawContent(int x, int y, int width, int height) {
                    contentDraw.draw(this, finalI, x, y, width, height, this.isToggled(), this.isHovering());
                }

                @Override
                protected void addTooltips(PlayerMob perspective) {
                    GameMessage tooltip;
                    super.addTooltips(perspective);
                    GameBackground background = null;
                    ListGameTooltips tooltips = new ListGameTooltips();
                    if (tooltipGetter != null && (tooltip = (GameMessage)tooltipGetter.apply(finalI)) != null) {
                        tooltips.add(tooltip);
                    }
                    if (costGetter != null) {
                        ArrayList cost = (ArrayList)costGetter.apply(finalI);
                        FontOptions fontOptions = new FontOptions(16).outline();
                        if (cost != null && !cost.isEmpty()) {
                            background = GameBackground.getItemTooltipBackground();
                            tooltips.add(new LocalMessage("ui", "stylistcost"));
                            for (InventoryItem inventoryItem : cost) {
                                FairType fairType = new FairType();
                                fairType.append(new FairColorChangeGlyph(inventoryItem.item.getRarityColor(inventoryItem)));
                                fairType.append(new FairItemGlyph(fontOptions.getSize(), inventoryItem));
                                fairType.append(new FairSpacerGlyph(5.0f, 2.0f));
                                fairType.append(fontOptions, GameUtils.formatNumber(inventoryItem.getAmount()));
                                fairType.append(fontOptions, " " + inventoryItem.getItemDisplayName());
                                tooltips.add(new FairTypeTooltip(fairType, 10));
                            }
                        }
                    }
                    if (!tooltips.isEmpty()) {
                        GameTooltipManager.addTooltip(tooltips, background, TooltipLocation.FORM_FOCUS);
                    }
                }
            });
            button.onClicked(e -> onClicked.accept(finalI, (FormInputEvent<FormButton>)e));
        }
        form.setHeight(totalRows * totalButtonWidth + contentPadding * 2);
        return form;
    }

    public Form addHeader(GameMessage message, int size, Form form) {
        Form out = new Form(form.getWidth(), 0);
        out.drawBase = false;
        FormFlow flow = new FormFlow();
        out.addComponent(flow.nextY(new FormLocalLabel(message, new FontOptions(size), 0, form.getWidth() / 2, 0, form.getWidth() - 20)));
        out.addComponent(flow.nextY(form));
        out.setHeight(flow.next() + 10);
        return out;
    }

    public Form combineContent(Form ... forms) {
        Form out = new Form(0, 0);
        out.drawBase = false;
        int height = 0;
        for (Form form : forms) {
            out.addComponent(form);
            form.setPosition(0, height);
            height += form.getHeight();
            out.setWidth(Math.max(out.getWidth(), form.getWidth()));
        }
        out.setHeight(height);
        return out;
    }

    public void reset() {
        this.setPlayer(new PlayerMob(0L, null));
    }

    public void randomize() {
        if (this.allowSupernaturalChanges) {
            this.newPlayer.look.randomizeLook(false);
        } else {
            this.newPlayer.look.randomizeLook(false, true, false, false, false);
        }
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
        this.icon.setPlayer(this.newPlayer);
        this.updateLook();
    }

    public HumanLook getLook() {
        return this.newPlayer.look;
    }

    public void onChanged() {
    }

    protected void updateLook() {
        this.newPlayer.getInv().giveLookArmor();
    }

    public PlayerMob getNewPlayer() {
        this.newPlayer.getInv().giveStarterItems();
        return this.newPlayer;
    }

    public void modifyHumanDrawOptions(HumanDrawOptions drawOptions) {
    }

    public ArrayList<InventoryItem> getSkinColorCost(int id) {
        return null;
    }

    public ArrayList<InventoryItem> getEyeTypeCost(int id) {
        return null;
    }

    public ArrayList<InventoryItem> getEyeColorCost(int id) {
        return null;
    }

    public ArrayList<InventoryItem> getHairStyleCost(int id) {
        return null;
    }

    public ArrayList<InventoryItem> getFacialFeatureCost(int id) {
        return null;
    }

    public ArrayList<InventoryItem> getHairColorCost(int id) {
        return null;
    }

    public ArrayList<InventoryItem> getShirtColorCost(Color color) {
        return null;
    }

    public ArrayList<InventoryItem> getShoesColorCost(Color color) {
        return null;
    }

    static {
        for (int i = 0; i < DEFAULT_SHIRT_AND_SHOES_COLORS.length; ++i) {
            FormNewPlayerPreset.DEFAULT_SHIRT_AND_SHOES_COLORS[i] = HumanLook.limitClothesColor(DEFAULT_SHIRT_AND_SHOES_COLORS[i]);
        }
        BUTTON_SIZE = FormInputSize.SIZE_32;
    }

    public class Section {
        public FormContentVarToggleButton button;
        public Form selectionContent;

        public Section(FormContentVarToggleButton button, Form selectionContent) {
            this.button = button;
            this.selectionContent = selectionContent;
        }

        public Section(final DrawButtonFunction drawButton, final GameMessage tooltip, Form selectionContent, Predicate<Section> isCurrent) {
            this.button = new FormContentVarToggleButton(0, 0, BUTTON_SIZE.height, BUTTON_SIZE, ButtonColor.BASE, () -> isCurrent.test(this)){

                @Override
                protected void drawContent(int x, int y, int width, int height) {
                    drawButton.draw(this, x, y, width, height);
                }

                @Override
                protected void addTooltips(PlayerMob perspective) {
                    if (tooltip != null) {
                        GameTooltipManager.addTooltip(new StringTooltips(tooltip.translate()), TooltipLocation.FORM_FOCUS);
                    }
                }
            };
            this.selectionContent = selectionContent;
        }

        public void onClicked(FormSwitcher switcher) {
            switcher.makeCurrent(this.selectionContent);
        }
    }

    public static interface DrawButtonFunction {
        public void draw(FormContentVarToggleButton var1, int var2, int var3, int var4, int var5);
    }

    @FunctionalInterface
    public static interface SelectionButtonDrawFunction {
        public void draw(FormContentVarToggleButton var1, int var2, int var3, int var4, int var5, int var6, boolean var7, boolean var8);
    }
}

