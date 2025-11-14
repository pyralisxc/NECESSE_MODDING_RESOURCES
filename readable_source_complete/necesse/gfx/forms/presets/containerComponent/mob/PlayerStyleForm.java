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
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentVarToggleButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.componentPresets.FormNewPlayerPreset;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonIcon;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.StylistContainer;

public abstract class PlayerStyleForm
extends Form {
    public final StylistContainer container;
    public FormNewPlayerPreset newPlayerPreset;
    public FormFairTypeLabel costText;
    public FormLocalTextButton styleButton;

    public PlayerStyleForm(final StylistContainer container) {
        super("playerStyle", 408, 10);
        this.container = container;
        FormFlow flow = new FormFlow(5);
        this.addComponent(new FormLocalLabel("ui", "stylistchange", new FontOptions(20), 0, this.getWidth() / 2, flow.next(25)));
        this.newPlayerPreset = this.addComponent(flow.nextY(new FormNewPlayerPreset(0, 0, this.getWidth() - 5, true, true){

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
                        PlayerStyleForm.this.newPlayerPreset.setLook(new HumanLook(container.client.playerMob.look));
                        this.onChanged();
                    }
                });
                return sections;
            }

            @Override
            public void onChanged() {
                super.onChanged();
                PlayerStyleForm.this.updateCostAndCanStyle();
            }

            @Override
            public ArrayList<InventoryItem> getSkinColorCost(int id) {
                return container.getSkinColorCost(container.client.playerMob.look.getSkin(), id);
            }

            @Override
            public ArrayList<InventoryItem> getEyeTypeCost(int id) {
                return container.getEyeTypeCost(container.client.playerMob.look.getEyeType(), id);
            }

            @Override
            public ArrayList<InventoryItem> getEyeColorCost(int id) {
                return container.getEyeColorCost(container.client.playerMob.look.getEyeColor(), id);
            }

            @Override
            public ArrayList<InventoryItem> getHairStyleCost(int id) {
                return container.getHairStyleCost(container.client.playerMob.look.getHair(), id);
            }

            @Override
            public ArrayList<InventoryItem> getFacialFeatureCost(int id) {
                return container.getFacialFeatureCost(container.client.playerMob.look.getFacialFeature(), id);
            }

            @Override
            public ArrayList<InventoryItem> getHairColorCost(int id) {
                return container.getHairColorCost(container.client.playerMob.look.getHairColor(), id);
            }

            @Override
            public ArrayList<InventoryItem> getShirtColorCost(Color color) {
                return container.getShirtColorCost(container.client.playerMob.look.getShirtColor(), color);
            }

            @Override
            public ArrayList<InventoryItem> getShoesColorCost(Color color) {
                return container.getShoesColorCost(container.client.playerMob.look.getShoesColor(), color);
            }
        }, 20));
        this.newPlayerPreset.setLook(new HumanLook(container.client.playerMob.look));
        int labelY = flow.next(28);
        this.costText = this.addComponent(new FormFairTypeLabel("", 40, labelY - 8));
        this.costText.setMaxWidth(this.getWidth() - 80);
        int buttonsY = flow.next(40);
        this.addComponent(new FormLocalTextButton("ui", "backbutton", this.getWidth() / 2 + 2, buttonsY, this.getWidth() / 2 - 6)).onClicked(e -> this.onBackPressed());
        this.styleButton = this.addComponent(new FormLocalTextButton("ui", "stylistbuy", 4, buttonsY, this.getWidth() / 2 - 6));
        this.styleButton.onClicked(e -> {
            if (container.canStyle(container.getTotalStyleCost(container.client.playerMob.look, this.newPlayerPreset.getLook()))) {
                Packet content = new Packet();
                this.newPlayerPreset.getLook().setupContentPacket(new PacketWriter(content), true);
                container.playerStyleButton.runAndSend(content);
            }
        });
        this.setHeight(flow.next());
        this.updateCostAndCanStyle();
    }

    @Override
    protected void init() {
        super.init();
        Localization.addListener(new LocalizationChangeListener(){

            @Override
            public void onChange(Language language) {
                PlayerStyleForm.this.updateCostAndCanStyle();
            }

            @Override
            public boolean isDisposed() {
                return PlayerStyleForm.this.isDisposed();
            }
        });
    }

    public void updateCostAndCanStyle() {
        HumanLook look = this.newPlayerPreset.getLook();
        ArrayList<InventoryItem> cost = this.container.getTotalStyleCost(this.container.client.playerMob.look, look);
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
        ArrayList<InventoryItem> cost = this.container.getTotalStyleCost(this.container.client.playerMob.look, look);
        this.styleButton.setActive(this.container.canStyle(cost));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updateCanStyle();
        super.draw(tickManager, perspective, renderBox);
    }

    public abstract void onBackPressed();
}

