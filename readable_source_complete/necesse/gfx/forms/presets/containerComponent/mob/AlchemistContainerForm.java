/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.mob;

import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.mob.AlchemistContainer;
import necesse.inventory.item.placeableItem.FireworkPlaceableItem;

public class AlchemistContainerForm<T extends AlchemistContainer>
extends ShopContainerForm<T> {
    public Form fireworkForm;
    public LinkedList<FormContentIconToggleButton> shapeButtons = new LinkedList();
    public LinkedList<FormContentIconToggleButton> colorButtons = new LinkedList();
    public LinkedList<FormContentIconToggleButton> crackleButtons = new LinkedList();
    public FireworkPlaceableItem.FireworksShape selectedShape = null;
    public FireworkPlaceableItem.FireworkColor selectedColor = null;
    public FireworkPlaceableItem.FireworkCrackle selectedCrackle = null;
    public int costY;
    public FormFairTypeLabel costLabel;
    public FormLocalTextButton buyButton;
    public FormLocalTextButton backButton;

    public AlchemistContainerForm(Client client, T container, int width, int height, int maxExpeditionsHeight) {
        super(client, container, width, height, maxExpeditionsHeight);
        this.fireworkForm = this.addComponent(new Form("firework", width, 360));
        FormFlow fireworkFlow = new FormFlow(5);
        this.fireworkForm.addComponent(new FormLocalLabel("ui", "alchemistfirework", new FontOptions(20), -1, 5, fireworkFlow.next(30)));
        int buttonWidth = 32;
        int buttonPadding = 8;
        int buttonsPerRow = width / (buttonWidth + buttonPadding);
        this.fireworkForm.addComponent(fireworkFlow.nextY(new FormLocalLabel("ui", "fireworkshape", new FontOptions(16), -1, 5, 0, this.fireworkForm.getWidth() - 10), 4));
        int button = 0;
        FormContentIconToggleButton iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_random, new LocalMessage("itemtooltip", "fireworkrandom")));
        this.shapeButtons.add(iconButton);
        iconButton.setToggled(true);
        iconButton.onToggled(e -> {
            this.shapeButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedShape == null) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedShape = null;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_sphere, FireworkPlaceableItem.FireworksShape.Sphere.displayName));
        this.shapeButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.shapeButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Sphere) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedShape = FireworkPlaceableItem.FireworksShape.Sphere;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_splash, FireworkPlaceableItem.FireworksShape.Splash.displayName));
        this.shapeButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.shapeButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Splash) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedShape = FireworkPlaceableItem.FireworksShape.Splash;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_disc, FireworkPlaceableItem.FireworksShape.Disc.displayName));
        this.shapeButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.shapeButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Disc) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedShape = FireworkPlaceableItem.FireworksShape.Disc;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_star, FireworkPlaceableItem.FireworksShape.Star.displayName));
        this.shapeButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.shapeButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Star) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedShape = FireworkPlaceableItem.FireworksShape.Star;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_heart, FireworkPlaceableItem.FireworksShape.Heart.displayName));
        this.shapeButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.shapeButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedShape == FireworkPlaceableItem.FireworksShape.Heart) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedShape = FireworkPlaceableItem.FireworksShape.Heart;
            this.updateCost();
        });
        int rows = (int)Math.ceil((float)(++button) / (float)buttonsPerRow);
        fireworkFlow.next(rows * (buttonWidth + buttonPadding));
        this.fireworkForm.addComponent(fireworkFlow.nextY(new FormLocalLabel("ui", "fireworkcolor", new FontOptions(16), -1, 5, 0, this.fireworkForm.getWidth() - 10), 4));
        button = 0;
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_random, new LocalMessage("itemtooltip", "fireworkrandom")));
        this.colorButtons.add(iconButton);
        iconButton.setToggled(true);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == null) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = null;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_confetti, FireworkPlaceableItem.FireworkColor.Confetti.displayName));
        this.colorButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Confetti) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = FireworkPlaceableItem.FireworkColor.Confetti;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_flame, FireworkPlaceableItem.FireworkColor.Flame.displayName));
        this.colorButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Flame) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = FireworkPlaceableItem.FireworkColor.Flame;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_red, FireworkPlaceableItem.FireworkColor.Red.displayName));
        this.colorButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Red) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = FireworkPlaceableItem.FireworkColor.Red;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_green, FireworkPlaceableItem.FireworkColor.Green.displayName));
        this.colorButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Green) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = FireworkPlaceableItem.FireworkColor.Green;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_blue, FireworkPlaceableItem.FireworkColor.Blue.displayName));
        this.colorButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Blue) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = FireworkPlaceableItem.FireworkColor.Blue;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_pink, FireworkPlaceableItem.FireworkColor.Pink.displayName));
        this.colorButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.colorButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedColor == FireworkPlaceableItem.FireworkColor.Pink) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedColor = FireworkPlaceableItem.FireworkColor.Pink;
            this.updateCost();
        });
        rows = (int)Math.ceil((float)(++button) / (float)buttonsPerRow);
        fireworkFlow.next(rows * (buttonWidth + buttonPadding));
        this.fireworkForm.addComponent(fireworkFlow.nextY(new FormLocalLabel("ui", "fireworkcrackle", new FontOptions(16), -1, 5, 0, this.fireworkForm.getWidth() - 10), 4));
        button = 0;
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_random, new LocalMessage("itemtooltip", "fireworkrandom")));
        this.crackleButtons.add(iconButton);
        iconButton.setToggled(true);
        iconButton.onToggled(e -> {
            this.crackleButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedCrackle == null) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedCrackle = null;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_crackle, FireworkPlaceableItem.FireworkCrackle.Crackle.displayName));
        this.crackleButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.crackleButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedCrackle == FireworkPlaceableItem.FireworkCrackle.Crackle) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedCrackle = FireworkPlaceableItem.FireworkCrackle.Crackle;
            this.updateCost();
        });
        iconButton = this.fireworkForm.addComponent(new FormContentIconToggleButton(++button % buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, fireworkFlow.next() + button / buttonsPerRow * (buttonWidth + buttonPadding) + buttonPadding / 2, FormInputSize.SIZE_32, ButtonColor.BASE, this.getInterfaceStyle().firework_nocrackle, FireworkPlaceableItem.FireworkCrackle.NoCrackle.displayName));
        this.crackleButtons.add(iconButton);
        iconButton.onToggled(e -> {
            this.crackleButtons.stream().filter(b -> b != e.from).forEach(b -> b.setToggled(false));
            if (this.selectedCrackle == FireworkPlaceableItem.FireworkCrackle.NoCrackle) {
                ((FormButtonToggle)e.from).setToggled(true);
            }
            this.selectedCrackle = FireworkPlaceableItem.FireworkCrackle.NoCrackle;
            this.updateCost();
        });
        rows = (int)Math.ceil((float)(++button) / (float)buttonsPerRow);
        fireworkFlow.next(rows * (buttonWidth + buttonPadding));
        this.costY = fireworkFlow.next() + 5;
        this.costLabel = this.fireworkForm.addComponent(new FormFairTypeLabel("", 5, 0), -100);
        fireworkFlow.next(30);
        int halfFireworkWidth = this.fireworkForm.getWidth() / 2;
        this.buyButton = this.fireworkForm.addComponent(new FormLocalTextButton("ui", "buybutton", 4, 0, halfFireworkWidth - 6));
        this.buyButton.onClicked(e -> {
            int amount = 1;
            if (Control.CRAFT_10.isDown()) {
                amount = 10;
            } else if (Control.CRAFT_ALL.isDown()) {
                amount = 65535;
            }
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            this.getFireworkData().writePacket(writer);
            writer.putNextShortUnsigned(amount);
            container.buyFireworkButton.runAndSend(content);
            e.preventDefault();
        });
        this.buyButton.acceptMouseRepeatEvents = true;
        this.backButton = this.fireworkForm.addComponent(new FormLocalTextButton("ui", "backbutton", halfFireworkWidth + 2, 0, halfFireworkWidth - 6));
        this.backButton.onClicked(e -> this.makeCurrent(this.dialogueForm));
        this.fireworkForm.setHeight(fireworkFlow.next());
        this.updateCost();
    }

    public AlchemistContainerForm(Client client, T container) {
        this(client, container, 408, defaultHeight, defaultHeight);
    }

    @Override
    protected void addShopDialogueOptions() {
        super.addShopDialogueOptions();
        boolean valid = false;
        if (((AlchemistContainer)this.container).sellingItems != null && !((AlchemistContainer)this.container).sellingItems.isEmpty()) {
            valid = true;
        } else if (((AlchemistContainer)this.container).buyingItems != null && !((AlchemistContainer)this.container).buyingItems.isEmpty()) {
            valid = true;
        }
        if (valid) {
            this.dialogueForm.addDialogueOption(new LocalMessage("ui", "alchemstwantfirework"), () -> this.makeCurrent(this.fireworkForm));
        }
    }

    public void updateCost() {
        GNDItemMap fireworkData = this.getFireworkData();
        int cost = ((AlchemistContainer)this.container).getFireworksCost(fireworkData);
        InventoryItem item = new InventoryItem("fireworkrocket");
        item.setGndData(fireworkData);
        FormFlow flow = new FormFlow(this.costY);
        this.costLabel.setText(new LocalMessage("ui", "alchemistfireworkcost", "cost", cost, "firework", TypeParsers.getItemParseString(item)));
        FontOptions fontOptions = new FontOptions(16).color(this.getInterfaceStyle().activeTextColor);
        this.costLabel.setFontOptions(fontOptions);
        this.costLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.InputIcon(fontOptions), TypeParsers.ItemIcon(16));
        flow.nextY(this.costLabel, 5);
        int buttonY = flow.next(40);
        this.buyButton.setY(buttonY);
        this.backButton.setY(buttonY);
        this.fireworkForm.setHeight(flow.next());
        this.updateCanBuy();
        ContainerComponent.setPosFocus(this.fireworkForm);
    }

    public void updateCanBuy() {
        this.buyButton.setActive(((AlchemistContainer)this.container).canBuyFirework(this.getFireworkData()));
    }

    public GNDItemMap getFireworkData() {
        GNDItemMap gndData = new GNDItemMap();
        new FireworkPlaceableItem.FireworkItemCreator().shape(this.selectedShape).color(this.selectedColor).crackle(this.selectedCrackle).applyToData(gndData);
        return gndData;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isCurrent(this.fireworkForm)) {
            this.updateCanBuy();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosFocus(this.fireworkForm);
    }
}

