/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.LinkedHashMap;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSellingShopComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.events.FullShopStockUpdateEvent;
import necesse.inventory.container.events.ShopWealthUpdateEvent;
import necesse.inventory.container.events.SingleShopStockUpdateEvent;
import necesse.inventory.container.mob.NetworkSellingShopItem;
import necesse.inventory.container.mob.ShopContainer;

public abstract class ShopContainerSellingForm
extends Form {
    public ShopContainer container;
    protected FormContentBox shopContent;
    protected LinkedHashMap<Integer, FormSellingShopComponent> shopComponents = new LinkedHashMap();
    protected FormFairTypeLabel wealthLabel;

    public ShopContainerSellingForm(ShopContainer container, int width, int height) {
        super("shopForm", width, height);
        this.container = container;
        this.updateFullContent();
    }

    @Override
    protected void init() {
        super.init();
        this.container.onEvent(ShopWealthUpdateEvent.class, e -> this.updateWealthLabel());
        this.container.onEvent(FullShopStockUpdateEvent.class, e -> {
            this.updateFullContent();
            this.updateWealthLabel();
            for (FormSellingShopComponent comp : this.shopComponents.values()) {
                comp.updateCanCraft();
            }
        });
        this.container.onEvent(SingleShopStockUpdateEvent.class, e -> {
            this.updateItemPositions();
            this.updateWealthLabel();
            FormSellingShopComponent component = this.shopComponents.get(e.shopItemID);
            if (component == null) {
                this.updateFullContent();
            } else {
                component.updateCanCraft();
            }
        });
    }

    protected void updateFullContent() {
        this.clearComponents();
        String shopName = MobRegistry.getLocalization(this.container.humanShop.getID()).translate();
        String shopHeader = GameUtils.maxString(shopName, new FontOptions(20), this.getWidth() - 10);
        FormFlow flow = new FormFlow(5);
        int buttonWidth = 150;
        this.addComponent(flow.nextY(new FormLabel(shopHeader, new FontOptions(20), -1, 5, 0), 4));
        this.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "shopsellingitems"), new FontOptions(16), -1, 5, 0), 4));
        this.addComponent(new FormLocalTextButton("ui", "backbutton", this.getWidth() - buttonWidth - 4, 4, buttonWidth, FormInputSize.SIZE_20, ButtonColor.BASE)).onClicked(e -> this.onBackPressed());
        int shopContentHeight = this.getHeight() - flow.next() - (this.container.shopWealth < 0 ? 0 : 30);
        this.shopContent = this.addComponent(new FormContentBox(0, flow.next(shopContentHeight), this.getWidth(), shopContentHeight));
        this.shopComponents = new LinkedHashMap();
        if (this.container.sellingItems != null) {
            for (NetworkSellingShopItem sellingItem : this.container.sellingItems.values()) {
                FormSellingShopComponent comp = this.shopContent.addComponent(new FormSellingShopComponent(0, 0, this.container, sellingItem));
                this.shopComponents.put(sellingItem.shopItemID, comp);
            }
        }
        this.updateItemPositions();
        if (this.container.shopWealth >= 0) {
            this.wealthLabel = this.addComponent(new FormFairTypeLabel(new StaticMessage(""), new FontOptions(16), FairType.TextAlign.LEFT, 5, flow.next() + 4));
            this.updateWealthLabel();
        }
    }

    public void updateItemPositions() {
        int currentX = 4;
        int currentY = 4;
        int compWidth = 40;
        int compHeight = 40;
        for (FormSellingShopComponent comp : this.shopComponents.values()) {
            if (currentX + compWidth > this.getWidth() - this.shopContent.getScrollBarWidth()) {
                currentX = 4;
                currentY += compHeight + 4;
            }
            comp.setPosition(currentX, currentY);
            currentX += compWidth + 4;
        }
        this.shopContent.setContentBox(new Rectangle(0, 0, this.getWidth(), currentY + compHeight + 4));
    }

    public void updateWealthLabel() {
        if (this.wealthLabel == null) {
            return;
        }
        FairType fairType = new FairType();
        FontOptions fontOptions = this.wealthLabel.getFontOptions();
        fairType.append(fontOptions, Localization.translate("ui", "shopwealth"));
        fairType.append(new FairItemGlyph(fontOptions.getSize(), new InventoryItem("coin")).dontShowTooltip());
        fairType.append(fontOptions, Integer.toString(this.container.shopWealth));
        this.wealthLabel.setCustomFairType(fairType);
    }

    public abstract void onBackPressed();

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this);
    }
}

