/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.util.Zoning;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLocalTextButtonToggle;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementCommandForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerGameTool;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementPrivateForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementRestrictForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSettingsForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSettlersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementWorkPrioritiesForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietsForm;
import necesse.gfx.forms.presets.containerComponent.settlement.equipment.SettlementEquipmentForm;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.MapHudDrawElement;
import necesse.level.maps.levelData.settlementData.SettlementBoundsManager;

public class SettlementContainerForm<T extends SettlementContainer>
extends ContainerFormList<T> {
    public static String lastOpenType;
    private final Form menuBar;
    private final FormSwitcher contentSwitcher;
    private final LinkedList<SettlementSubForm> menus;
    private final SettlementPrivateForm privateForm;
    public int settlerBasicsSubscription = -1;
    public ArrayList<SettlementSettlerBasicData> settlers = new ArrayList();
    public ArrayList<SettlementLockedBedData> lockedBeds = new ArrayList();
    private final SettlementCommandForm<T> commandForm;
    public final SelectedSettlersHandler selectedSettlers;
    public SettlementContainerGameTool tool;
    private HudDrawElement hudDrawElement;

    public SettlementContainerForm(Client client, T container) {
        super(client, container);
        this.selectedSettlers = new SelectedSettlersHandler(client){

            @Override
            public void updateSelectedSettlers(boolean switchToCommandForm) {
                SettlementContainerForm.this.updateSelectedSettlers(switchToCommandForm);
            }
        };
        this.menuBar = this.addComponent(new Form("menubar", 800, 40));
        this.contentSwitcher = this.addComponent(new FormSwitcher());
        this.menus = new LinkedList();
        this.menus.add(new SettlementSettingsForm<T>(client, container, this));
        this.menus.add(new SettlementSettlersForm<T>(client, container, this));
        this.commandForm = new SettlementCommandForm<T>(client, container, this);
        this.menus.add(this.commandForm);
        this.menus.add(new SettlementEquipmentForm<T>(client, container, this));
        this.menus.add(new SettlementDietsForm<T>(client, container, this));
        this.menus.add(new SettlementRestrictForm<T>(client, container, this));
        this.menus.add(new SettlementWorkPrioritiesForm<T>(client, container, this));
        this.menus.add(new SettlementAssignWorkForm<T>(client, container, this));
        for (SettlementSubForm menu : this.menus) {
            this.contentSwitcher.addComponent((FormComponent)((Object)menu), (c, active) -> ((SettlementSubForm)((Object)c)).onSetCurrent((boolean)active));
        }
        this.updateMenuBar();
        this.privateForm = this.contentSwitcher.addComponent(new SettlementPrivateForm(((SettlementContainer)container).settlementData, ((SettlementContainer)container).requestJoin));
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    protected void init() {
        ((SettlementContainer)this.container).onEvent(SettlementDataEvent.class, e -> this.updatePrivateForm());
        ((SettlementContainer)this.container).onEvent(SettlementSettlersChangedEvent.class, event -> {
            if (((SettlementContainer)this.container).hasSettlementAccess(this.client)) {
                ((SettlementContainer)this.container).requestSettlerBasics.runAndSend();
            }
        });
        ((SettlementContainer)this.container).onEvent(SettlementSettlerBasicsEvent.class, event -> {
            this.settlers = event.settlers;
            this.lockedBeds = event.lockedBeds;
            SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                Set possibleSelected = this.settlers.stream().map(d -> d.mobUniqueID).collect(Collectors.toSet());
                this.selectedSettlers.cleanUp(possibleSelected::contains);
            }
        });
        this.selectedSettlers.init();
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
        }
        this.hudDrawElement = this.client.getLevel().hudManager.addElement(new MapHudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                if (((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData == null) {
                    return;
                }
                Rectangle levelRectangle = SettlementBoundsManager.getLevelRectangleFromTier(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileX, ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileY, ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.flagTier);
                final SharedTextureDrawOptions options = Zoning.getRectangleDrawOptions(levelRectangle, new Color(0.2f, 0.2f, 0.6f, 0.5f), new Color(0.2f, 0.2f, 0.6f, 0.0f), 128, camera);
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return -100000;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        options.draw();
                    }
                });
            }

            @Override
            public Rectangle getMapLevelDrawBounds() {
                if (((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData == null) {
                    return null;
                }
                Rectangle levelRectangle = SettlementBoundsManager.getLevelRectangleFromTier(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileX, ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileY, ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.flagTier);
                levelRectangle.x -= GameMath.getLevelCoordinate(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileX);
                levelRectangle.y -= GameMath.getLevelCoordinate(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileY);
                return levelRectangle;
            }

            @Override
            public Point getMapLevelPos() {
                if (((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData == null) {
                    return new Point();
                }
                return new Point(GameMath.getLevelCoordinate(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileX), GameMath.getLevelCoordinate(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileY));
            }

            @Override
            public void drawOnMap(TickManager tickManager, Client client, int drawX, int drawY, double tileScale, Rectangle drawBounds, boolean isMinimap) {
                if (((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData == null) {
                    return;
                }
                Rectangle tileRectangle = SettlementBoundsManager.getTileRectangleFromTier(((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileX, ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileY, ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.flagTier);
                tileRectangle.x -= ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileX;
                tileRectangle.y -= ((SettlementContainer)((SettlementContainerForm)SettlementContainerForm.this).container).settlementData.tileY;
                tileRectangle.x = (int)((double)tileRectangle.x * tileScale);
                tileRectangle.y = (int)((double)tileRectangle.y * tileScale);
                tileRectangle.width = (int)((double)tileRectangle.width * tileScale);
                tileRectangle.height = (int)((double)tileRectangle.height * tileScale);
                SharedTextureDrawOptions options = Zoning.getRectangleDrawOptions(tileRectangle, new Color(0.2f, 0.2f, 0.6f, 0.7f), new Color(0.2f, 0.2f, 0.6f, 0.0f), (int)(4.0 * tileScale), drawX, drawY);
                options.draw();
            }
        });
        this.updatePrivateForm();
        if (lastOpenType != null) {
            if (!this.contentSwitcher.isCurrent(this.privateForm)) {
                for (SettlementSubForm menu : this.menus) {
                    if (!lastOpenType.equals(menu.getTypeString())) continue;
                    menu.onMenuButtonClicked(this.contentSwitcher);
                    break;
                }
            } else {
                lastOpenType = null;
            }
        }
        super.init();
    }

    private void updateMenuBar() {
        GameWindow window = WindowManager.getWindow();
        int minWidth = Math.min(50 * this.menus.size(), 200);
        int maxWidth = window.getHudWidth() - 200;
        this.menuBar.setWidth(GameMath.limit(200 * this.menus.size(), minWidth, Math.max(maxWidth, minWidth)));
        this.menuBar.clearComponents();
        int usedWidth = 4;
        int i = 0;
        for (final SettlementSubForm menu : this.menus) {
            int remainingButtons = this.menus.size() - i;
            int remainingWidth = this.menuBar.getWidth() - 4 - usedWidth;
            int width = remainingWidth / remainingButtons;
            FormLocalTextButtonToggle button = this.menuBar.addComponent(new FormLocalTextButtonToggle(menu.getMenuButtonName(), usedWidth, 0, width, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE){

                @Override
                public boolean isToggled() {
                    return SettlementContainerForm.this.contentSwitcher.isCurrent((FormComponent)((Object)menu));
                }
            });
            button.controllerFocusHashcode = "settlementMenuButton" + menu.getTypeString();
            button.onClicked(e -> {
                e.preventDefault();
                ((FormButton)e.from).playTickSound();
                if (this.contentSwitcher.isCurrent((FormComponent)((Object)menu))) {
                    this.contentSwitcher.clearCurrent();
                    lastOpenType = null;
                } else {
                    menu.onMenuButtonClicked(this.contentSwitcher);
                    lastOpenType = menu.getTypeString();
                }
            });
            usedWidth += width;
            ++i;
        }
        this.menuBar.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() - this.menuBar.getHeight() / 2 - this.getInterfaceStyle().formSpacing - 28);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.updatePrivateFormActive(false);
        super.draw(tickManager, perspective, renderBox);
    }

    private void updatePrivateForm() {
        this.privateForm.updateContent(((SettlementContainer)this.container).settlementData);
        this.updatePrivateFormActive(true);
    }

    private void updatePrivateFormActive(boolean forceUpdate) {
        if (!((SettlementContainer)this.container).hasSettlementAccess(this.client)) {
            if (forceUpdate || !this.contentSwitcher.isCurrent(this.privateForm)) {
                this.contentSwitcher.makeCurrent(this.privateForm);
                this.menuBar.setHidden(true);
                if (this.settlerBasicsSubscription != -1) {
                    ((SettlementContainer)this.container).subscribeSettlerBasics.unsubscribe(this.settlerBasicsSubscription);
                }
                this.settlerBasicsSubscription = -1;
                if (!this.selectedSettlers.isEmpty()) {
                    this.selectedSettlers.clear();
                }
                if (this.tool != null) {
                    GameToolManager.clearGameTool(this.tool);
                }
                this.tool = null;
            }
        } else if (forceUpdate || this.contentSwitcher.isCurrent(this.privateForm)) {
            if (this.contentSwitcher.isCurrent(this.privateForm)) {
                this.contentSwitcher.clearCurrent();
            }
            this.menuBar.setHidden(false);
            if (this.settlerBasicsSubscription == -1) {
                this.settlerBasicsSubscription = ((SettlementContainer)this.container).subscribeSettlerBasics.subscribe();
                ((SettlementContainer)this.container).requestSettlerBasics.runAndSend();
                if (this.tool != null) {
                    GameToolManager.clearGameTool(this.tool);
                }
                this.tool = new SettlementContainerGameTool(this.client, this.selectedSettlers, (SettlementContainer)this.container, this);
                GameToolManager.setGameTool(this.tool);
            }
        }
    }

    public boolean isCurrent(SettlementSubForm form) {
        return this.contentSwitcher.isCurrent((FormComponent)((Object)form));
    }

    public SettlementSubForm getCurrentSubForm() {
        Object current = this.contentSwitcher.getCurrent();
        if (current instanceof SettlementSubForm) {
            return (SettlementSubForm)current;
        }
        return null;
    }

    public SettlementToolHandler getCurrentToolHandler() {
        SettlementSubForm current = this.getCurrentSubForm();
        if (current != null) {
            return current.getToolHandler();
        }
        return null;
    }

    public void updateSelectedSettlers(boolean switchToCommandForm) {
        if (!((SettlementContainer)this.container).hasSettlementAccess(this.client)) {
            return;
        }
        if (!this.selectedSettlers.isEmpty()) {
            if (switchToCommandForm && !this.contentSwitcher.isCurrent(this.commandForm) || this.contentSwitcher.getCurrent() == null) {
                this.commandForm.onMenuButtonClicked(this.contentSwitcher);
                lastOpenType = this.commandForm.getTypeString();
            }
            this.commandForm.updateSelectedForm();
        } else if (this.contentSwitcher.isCurrent(this.commandForm)) {
            this.commandForm.updateCurrentForm();
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateMenuBar();
    }

    @Override
    public void dispose() {
        this.selectedSettlers.dispose();
        GameToolManager.clearGameTool(this.tool);
        if (this.hudDrawElement != null) {
            this.hudDrawElement.remove();
            this.hudDrawElement = null;
        }
        super.dispose();
    }

    @Override
    public boolean shouldOpenInventory() {
        return false;
    }

    @Override
    public boolean shouldShowInventory() {
        return false;
    }

    @Override
    public boolean shouldShowToolbar() {
        return false;
    }
}

