/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.HumanLook;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentVarToggleButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.componentPresets.FormNewPlayerPreset;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.events.StylistSettlersUpdateContainerEvent;
import necesse.inventory.container.mob.StylistContainer;

public abstract class SettlerStyleForm
extends Form {
    public final StylistContainer container;
    public FormDropdownSelectionButton<HumanMob> selectionButton;
    public FormNewPlayerPreset newPlayerPreset;
    public FormFairTypeLabel costText;
    public FormLocalTextButton styleButton;
    public HumanMob currentSelectedSettler;

    public SettlerStyleForm(final StylistContainer container) {
        super("settlerStyle", 408, 10);
        this.container = container;
        FormFlow flow = new FormFlow(5);
        this.addComponent(new FormLocalLabel("ui", "stylistchoosesettler", new FontOptions(20), 0, this.getWidth() / 2, flow.next(25)));
        int selectWidth = Math.min(this.getWidth() - 8, 300);
        this.selectionButton = this.addComponent(new FormDropdownSelectionButton(this.getWidth() / 2 - selectWidth / 2, flow.next(30), FormInputSize.SIZE_24, ButtonColor.BASE, selectWidth));
        this.selectionButton.onSelected(e -> {
            this.currentSelectedSettler = (HumanMob)e.value;
            if (this.currentSelectedSettler != null) {
                this.newPlayerPreset.setLook(new HumanLook(this.currentSelectedSettler.look));
            }
            this.updateCostAndCanStyle();
            this.newPlayerPreset.setHidden(this.currentSelectedSettler == null);
        });
        container.onEvent(StylistSettlersUpdateContainerEvent.class, e -> {
            this.updateSelectButton();
            this.updateCostAndCanStyle();
        });
        this.newPlayerPreset = this.addComponent(flow.nextY(new FormNewPlayerPreset(0, 0, this.getWidth() - 5, true, false){

            @Override
            protected ArrayList<FormNewPlayerPreset.Section> getSections(Predicate<FormNewPlayerPreset.Section> isCurrent, int width) {
                ArrayList<FormNewPlayerPreset.Section> sections = super.getSections(isCurrent, width);
                sections.add(0, new FormNewPlayerPreset.Section(new FormNewPlayerPreset.DrawButtonFunction(){

                    @Override
                    public void draw(FormContentVarToggleButton button, int drawX, int drawY, int width, int height) {
                        ButtonIcon icon = this.getInterfaceStyle().button_reset_20;
                        Color color = (Color)icon.colorGetter.apply(button.getButtonState());
                        icon.texture.initDraw().color(color).posMiddle(drawX + width / 2, drawY + height / 2).draw();
                    }
                }, (GameMessage)new LocalMessage("ui", "stylistreset"), null, isCurrent){

                    @Override
                    public void onClicked(FormSwitcher switcher) {
                        if (SettlerStyleForm.this.currentSelectedSettler != null) {
                            SettlerStyleForm.this.newPlayerPreset.setLook(new HumanLook(SettlerStyleForm.this.currentSelectedSettler.look));
                        }
                        this.onChanged();
                    }
                });
                return sections;
            }

            @Override
            public void onChanged() {
                super.onChanged();
                SettlerStyleForm.this.updateCostAndCanStyle();
            }

            @Override
            protected void updateLook() {
                super.updateLook();
            }

            @Override
            public void modifyHumanDrawOptions(HumanDrawOptions drawOptions) {
                super.modifyHumanDrawOptions(drawOptions);
                if (SettlerStyleForm.this.currentSelectedSettler != null) {
                    drawOptions.chestplate(null);
                    drawOptions.boots(null);
                    SettlerStyleForm.this.currentSelectedSettler.setDefaultArmor(drawOptions);
                    drawOptions.helmet(null);
                }
            }

            @Override
            public ArrayList<InventoryItem> getSkinColorCost(int id) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getSkinColorCost(SettlerStyleForm.this.currentSelectedSettler.look.getSkin(), id);
            }

            @Override
            public ArrayList<InventoryItem> getEyeTypeCost(int id) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getEyeTypeCost(SettlerStyleForm.this.currentSelectedSettler.look.getEyeType(), id);
            }

            @Override
            public ArrayList<InventoryItem> getEyeColorCost(int id) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getEyeColorCost(SettlerStyleForm.this.currentSelectedSettler.look.getEyeColor(), id);
            }

            @Override
            public ArrayList<InventoryItem> getHairStyleCost(int id) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getHairStyleCost(SettlerStyleForm.this.currentSelectedSettler.look.getHair(), id);
            }

            @Override
            public ArrayList<InventoryItem> getFacialFeatureCost(int id) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getFacialFeatureCost(SettlerStyleForm.this.currentSelectedSettler.look.getFacialFeature(), id);
            }

            @Override
            public ArrayList<InventoryItem> getHairColorCost(int id) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getHairColorCost(SettlerStyleForm.this.currentSelectedSettler.look.getHairColor(), id);
            }

            @Override
            public ArrayList<InventoryItem> getShirtColorCost(Color color) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getShirtColorCost(SettlerStyleForm.this.currentSelectedSettler.look.getShirtColor(), color);
            }

            @Override
            public ArrayList<InventoryItem> getShoesColorCost(Color color) {
                return SettlerStyleForm.this.currentSelectedSettler == null ? null : container.getShoesColorCost(SettlerStyleForm.this.currentSelectedSettler.look.getShoesColor(), color);
            }
        }, 20));
        this.newPlayerPreset.setLook(new HumanLook(GameRandom.globalRandom, true));
        int labelY = flow.next(28);
        this.costText = this.addComponent(new FormFairTypeLabel("", 40, labelY - 8));
        this.costText.setMaxWidth(this.getWidth() - 80);
        int buttonsY = flow.next(40);
        this.addComponent(new FormLocalTextButton("ui", "backbutton", this.getWidth() / 2 + 2, buttonsY, this.getWidth() / 2 - 6)).onClicked(e -> this.onBackPressed());
        this.styleButton = this.addComponent(new FormLocalTextButton("ui", "stylistbuy", 4, buttonsY, this.getWidth() / 2 - 6));
        this.styleButton.onClicked(e -> {
            if (this.currentSelectedSettler != null && container.canStyle(container.getTotalStyleCost(this.currentSelectedSettler.look, this.newPlayerPreset.getLook()))) {
                Packet content = new Packet();
                PacketWriter writer = new PacketWriter(content);
                writer.putNextInt(this.currentSelectedSettler.getUniqueID());
                this.newPlayerPreset.getLook().setupContentPacket(writer, true);
                container.settlerStyleButton.runAndSend(content);
                this.currentSelectedSettler.look = new HumanLook(this.newPlayerPreset.getLook());
                this.updateCostAndCanStyle();
            }
        });
        this.setHeight(flow.next());
        this.updateCostAndCanStyle();
        this.updateSelectButton();
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                SettlerStyleForm.this.updateCostAndCanStyle();
            }

            @Override
            public boolean isDisposed() {
                return SettlerStyleForm.this.isDisposed();
            }
        });
    }

    public void updateSelectButton() {
        HumanMob selected = this.selectionButton.getSelected();
        int lastSelectedUniqueID = selected == null ? -1 : selected.getUniqueID();
        this.selectionButton.options.clear();
        HumanMob lastSelected = null;
        if (this.container.availableSettlers != null) {
            for (HumanMob humanMob : this.container.availableSettlers) {
                this.selectionButton.options.add(humanMob, humanMob.getLocalization());
                if (lastSelectedUniqueID == -1 || humanMob.getUniqueID() != lastSelectedUniqueID) continue;
                lastSelected = humanMob;
            }
        } else {
            this.selectionButton.options.add(null, new LocalMessage("ui", "stylistnosettlers"));
        }
        if (lastSelected != null) {
            this.selectionButton.setSelected(lastSelected, lastSelected.getLocalization());
        } else {
            if (this.container.availableSettlers != null && !this.container.availableSettlers.isEmpty()) {
                HumanMob first = this.container.availableSettlers.get(0);
                this.selectionButton.setSelected(first, first.getLocalization());
            } else {
                this.selectionButton.setSelected(null, new LocalMessage("ui", "stylistchoosesettler"));
            }
            this.updateCostAndCanStyle();
        }
        this.currentSelectedSettler = this.selectionButton.getSelected();
        this.newPlayerPreset.setHidden(this.currentSelectedSettler == null);
    }

    public void updateCostAndCanStyle() {
        HumanLook look = this.newPlayerPreset.getLook();
        ArrayList<InventoryItem> cost = this.currentSelectedSettler == null ? null : this.container.getTotalStyleCost(this.currentSelectedSettler.look, look);
        this.styleButton.setActive(this.container.canStyle(cost));
        if (cost == null) {
            cost = new ArrayList<InventoryItem>(Collections.singletonList(new InventoryItem("coin", 0)));
        }
        this.costText.setCustomFairType(this.getTotalCostFairType(cost));
    }

    public FairType getTotalCostFairType(ArrayList<InventoryItem> cost) {
        FontOptions fontOptions = new FontOptions(16);
        FairType fairType = new FairType();
        fairType.append(fontOptions, Localization.translate("ui", "stylistcost"));
        Iterator<InventoryItem> it = cost.iterator();
        while (it.hasNext()) {
            InventoryItem next = it.next();
            fairType.append(new FairItemGlyph(24, next).offsetY(4));
            fairType.append(fontOptions, "x " + next.getAmount());
            if (!it.hasNext()) continue;
            fairType.append(fontOptions, ",");
        }
        return fairType;
    }

    public void updateCanStyle() {
        HumanLook look = this.newPlayerPreset.getLook();
        ArrayList<InventoryItem> cost = this.currentSelectedSettler == null ? null : this.container.getTotalStyleCost(this.currentSelectedSettler.look, look);
        this.styleButton.setActive(this.container.canStyle(cost));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateCanStyle();
        super.draw(tickManager, perspective, renderBox);
    }

    public abstract void onBackPressed();
}

