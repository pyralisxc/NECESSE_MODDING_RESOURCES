/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.engine.util.EventVariable;
import necesse.engine.util.HashMapArrayList;
import necesse.engine.util.ObjectValue;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconVarToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.settlement.CreateOrExpandWorkZoneGameTool;
import necesse.gfx.forms.presets.containerComponent.settlement.SelectTileGameTool;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkToolHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementStorageConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementWorkstationConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.HUD;
import necesse.inventory.Inventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementRestrictZoneRenameEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZonesEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationsEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;
import necesse.level.maps.multiTile.MultiTile;

public class SettlementAssignWorkForm<T extends SettlementContainer>
extends FormSwitcher
implements SettlementSubForm {
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    protected int workContentSubscriptionID;
    public Form work;
    public FormContentBox workContent;
    public SettlementStorageConfigForm storageConfig;
    public Point requestedStorageConfigPos = null;
    public SettlementWorkstationConfigForm workstationConfig;
    public Point requestedWorkstationConfigPos = null;
    public WorkZoneConfigComponent workZoneConfig;
    public int requestedWorkZoneConfigUniqueID;
    public int currentOpenWorkZoneConfigUniqueID;
    protected ArrayList<Point> storagePositions = new ArrayList();
    protected ArrayList<Point> workstationPositions = new ArrayList();
    protected HashMap<Integer, SettlementWorkZone> settlementWorkZones = new HashMap();
    protected List<HudDrawElement> hudElements = new ArrayList<HudDrawElement>();
    protected SettlementAssignWorkToolHandler toolHandler;
    protected boolean overrideShowZones;

    public SettlementAssignWorkForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.toolHandler = new SettlementAssignWorkToolHandler(client, (SettlementContainer)container, this);
        this.work = this.addComponent(new Form("jobs", 500, 250));
        FormFlow flow = new FormFlow(5);
        int contentHeight = this.work.getHeight();
        this.workContent = this.work.addComponent(new FormContentBox(0, 0, this.work.getWidth(), contentHeight));
        this.workContent.addComponent(new FormLocalLabel("ui", "settlementassignwork", new FontOptions(20), 0, this.work.getWidth() / 2, flow.next(30)));
        FormLocalTextButton assignStorage = this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignstorage", 16, flow.next(40), this.work.getWidth() - 32 - 32));
        assignStorage.onClicked(e -> this.startAssignStorageTool());
        assignStorage.setLocalTooltip(new LocalMessage("ui", "settlementstoragetip"));
        this.workContent.addComponent(this.getHideButton(assignStorage.getX() + assignStorage.getWidth(), assignStorage.getY(), Settings.hideSettlementStorage, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
        FormLocalTextButton assignStation = this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignworkstation", 16, flow.next(40), this.work.getWidth() - 32 - 32));
        assignStation.onClicked(e -> this.startAssignWorkstationTool());
        assignStation.setLocalTooltip(new LocalMessage("ui", "settlementworkstationtip"));
        this.workContent.addComponent(this.getHideButton(assignStation.getX() + assignStation.getWidth(), assignStation.getY(), Settings.hideSettlementWorkstations, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
        FormLocalTextButton assignForestryZone = this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignforestry", 16, flow.next(40), this.work.getWidth() - 32 - 32));
        assignForestryZone.onClicked(e -> this.startAssignForestryZoneTool());
        assignForestryZone.setLocalTooltip(new LocalMessage("ui", "settlementforestrytip"));
        this.workContent.addComponent(this.getHideButton(assignForestryZone.getX() + assignForestryZone.getWidth(), assignForestryZone.getY(), Settings.hideSettlementForestryZones, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
        FormLocalTextButton assignHusbandryZone = this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignhusbandry", 16, flow.next(40), this.work.getWidth() - 32 - 32));
        assignHusbandryZone.onClicked(e -> this.startAssignHusbandryZoneTool());
        assignHusbandryZone.setLocalTooltip(new LocalMessage("ui", "settlementhusbandrytip"));
        this.workContent.addComponent(this.getHideButton(assignHusbandryZone.getX() + assignHusbandryZone.getWidth(), assignHusbandryZone.getY(), Settings.hideSettlementHusbandryZones, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
        FormLocalTextButton assignFertilizeZone = this.workContent.addComponent(new FormLocalTextButton("ui", "settlementassignfertilize", 16, flow.next(40), this.work.getWidth() - 32 - 32));
        assignFertilizeZone.onClicked(e -> this.startAssignFertilizeZoneTool());
        assignFertilizeZone.setLocalTooltip(new LocalMessage("ui", "settlementfertilizetip"));
        this.workContent.addComponent(this.getHideButton(assignFertilizeZone.getX() + assignFertilizeZone.getWidth(), assignFertilizeZone.getY(), Settings.hideSettlementFertilizeZones, new LocalMessage("ui", "hidebutton"), new LocalMessage("ui", "showbutton")));
        this.workContent.setContentBox(new Rectangle(0, 0, this.work.getWidth(), flow.next()));
        this.makeCurrent(this.work);
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.container)).onEvent(SettlementStorageEvent.class, event -> {
            this.storagePositions = event.storage;
            this.updateHudElements();
            if (this.storageConfig != null && this.isCurrent(this.storageConfig) && !this.storagePositions.contains(this.storageConfig.tile)) {
                this.makeCurrent(this.work);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementSingleStorageEvent.class, event -> {
            if (event.exists) {
                if (!this.storagePositions.contains(new Point(event.tileX, event.tileY))) {
                    this.storagePositions.add(new Point(event.tileX, event.tileY));
                    this.updateHudElements();
                }
            } else if (this.storagePositions.removeIf(e -> e.x == event.tileX && e.y == event.tileY)) {
                this.updateHudElements();
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementStorageChangeAllowedEvent.class, event -> {
            if (this.storageConfig != null && this.storageConfig.tile.x == event.tileX && this.storageConfig.tile.y == event.tileY) {
                if (event.isItems) {
                    for (Item item : event.items) {
                        this.storageConfig.filter.setItemAllowed(item, event.allowed);
                        this.storageConfig.filterForm.updateAllButtons();
                    }
                } else {
                    ItemCategoriesFilter.ItemCategoryFilter category = this.storageConfig.filter.getItemCategory(event.category.id);
                    category.setAllowed(event.allowed);
                    this.storageConfig.filterForm.updateButtons(event.category);
                }
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementStorageLimitsEvent.class, event -> {
            if (this.storageConfig != null && this.storageConfig.tile.x == event.tileX && this.storageConfig.tile.y == event.tileY) {
                if (event.isItems) {
                    this.storageConfig.filter.setItemAllowed(event.item, event.limits);
                    this.storageConfig.filterForm.updateAllButtons();
                } else {
                    ItemCategoriesFilter.ItemCategoryFilter category = this.storageConfig.filter.getItemCategory(event.category.id);
                    category.setMaxItems(event.maxItems);
                    this.storageConfig.filterForm.updateButtons(event.category);
                }
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementStoragePriorityLimitEvent.class, event -> {
            if (this.storageConfig != null && this.storageConfig.tile.x == event.tileX && this.storageConfig.tile.y == event.tileY) {
                if (event.isPriority) {
                    this.storageConfig.updatePrioritySelect(event.priority);
                } else {
                    this.storageConfig.filter.limitMode = event.limitMode;
                    this.storageConfig.filter.maxAmount = event.limit;
                    this.storageConfig.updateLimitMode();
                    this.storageConfig.updateLimitInput();
                }
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementStorageFullUpdateEvent.class, event -> {
            if (this.storageConfig != null && this.storageConfig.tile.x == event.tileX && this.storageConfig.tile.y == event.tileY) {
                this.storageConfig.updatePrioritySelect(event.priority);
                this.storageConfig.filter.readPacket(new PacketReader(event.filterContent));
                this.storageConfig.updateLimitInput();
                this.storageConfig.filterForm.updateAllButtons();
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkstationsEvent.class, event -> {
            this.workstationPositions = event.workstations;
            this.updateHudElements();
            if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && !this.workstationPositions.contains(this.workstationConfig.tile)) {
                this.makeCurrent(this.work);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementSingleWorkstationsEvent.class, event -> {
            if (event.exists) {
                if (!this.workstationPositions.contains(new Point(event.tileX, event.tileY))) {
                    this.workstationPositions.add(new Point(event.tileX, event.tileY));
                    this.updateHudElements();
                }
            } else if (this.workstationPositions.removeIf(e -> e.x == event.tileX && e.y == event.tileY)) {
                this.updateHudElements();
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementOpenStorageConfigEvent.class, event -> {
            if (this.requestedStorageConfigPos != null && this.requestedStorageConfigPos.x == event.tileX && this.requestedStorageConfigPos.y == event.tileY) {
                this.setupConfigStorage((SettlementOpenStorageConfigEvent)event);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementOpenWorkstationEvent.class, event -> {
            if (this.requestedWorkstationConfigPos != null && this.requestedWorkstationConfigPos.x == event.tileX && this.requestedWorkstationConfigPos.y == event.tileY) {
                this.setupConfigWorkstation((SettlementOpenWorkstationEvent)event);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkstationEvent.class, event -> {
            if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && this.workstationConfig.tile.x == event.tileX && this.workstationConfig.tile.y == event.tileY) {
                this.workstationConfig.setRecipes(event.recipes);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkstationRecipeUpdateEvent.class, event -> {
            if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && this.workstationConfig.tile.x == event.tileX && this.workstationConfig.tile.y == event.tileY) {
                this.workstationConfig.onRecipeUpdate((SettlementWorkstationRecipeUpdateEvent)event);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkstationRecipeRemoveEvent.class, event -> {
            if (this.workstationConfig != null && this.isCurrent(this.workstationConfig) && this.workstationConfig.tile.x == event.tileX && this.workstationConfig.tile.y == event.tileY) {
                this.workstationConfig.onRecipeRemove((SettlementWorkstationRecipeRemoveEvent)event);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkZonesEvent.class, event -> {
            this.settlementWorkZones = event.zones;
            this.updateHudElements();
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkZoneRemovedEvent.class, event -> {
            if (this.settlementWorkZones.remove(event.zoneUniqueID) != null) {
                this.updateHudElements();
            }
            if (this.workZoneConfig != null && this.isCurrent((FormComponent)((Object)this.workZoneConfig)) && this.currentOpenWorkZoneConfigUniqueID == event.zoneUniqueID) {
                this.makeCurrent(this.work);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementWorkZoneChangedEvent.class, event -> {
            if (event.zone.shouldRemove()) {
                if (this.settlementWorkZones.remove(event.zone.getUniqueID()) != null) {
                    this.updateHudElements();
                }
            } else {
                this.settlementWorkZones.put(event.zone.getUniqueID(), event.zone);
                this.updateHudElements();
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementRestrictZoneRenameEvent.class, event -> {
            SettlementWorkZone zone = this.settlementWorkZones.get(event.restrictZoneUniqueID);
            if (zone != null) {
                zone.setName(event.name);
            }
        });
        ((Container)((Object)this.container)).onEvent(SettlementOpenWorkZoneConfigEvent.class, event -> {
            if (this.requestedWorkZoneConfigUniqueID == event.uniqueID) {
                this.setupWorkZoneConfig((SettlementOpenWorkZoneConfigEvent)event);
            }
        });
    }

    protected FormContentIconVarToggleButton getHideButton(int x, int y, final EventVariable<Boolean> isHiddenSetting, final GameMessage shownTooltip, final GameMessage hiddenTooltip) {
        FormContentIconVarToggleButton button = new FormContentIconVarToggleButton(x, y + 4, FormInputSize.SIZE_32, ButtonColor.BASE, isHiddenSetting::get, this.getInterfaceStyle().button_hidden_big, this.getInterfaceStyle().button_shown_big, new GameMessage[0]){

            @Override
            public GameTooltips getTooltips() {
                if (((Boolean)isHiddenSetting.get()).booleanValue()) {
                    return new StringTooltips(hiddenTooltip.translate());
                }
                return new StringTooltips(shownTooltip.translate());
            }
        };
        button.onClicked(e -> isHiddenSetting.set((Boolean)isHiddenSetting.get() == false));
        return button;
    }

    protected void updateHudElements() {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        if (!this.containerForm.isCurrent(this)) {
            return;
        }
        HashMapArrayList<Point, ObjectValue<GameTexture, BooleanSupplier>> tileIcons = new HashMapArrayList<Point, ObjectValue<GameTexture, BooleanSupplier>>();
        Level level = this.client.getLevel();
        for (Point point : this.storagePositions) {
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(point.x, point.y);
            if (!(objectEntity instanceof OEInventory)) continue;
            tileIcons.add(point, new ObjectValue<GameTexture, BooleanSupplier>(this.getInterfaceStyle().storage, () -> Settings.hideSettlementStorage.get() == false));
        }
        for (Point point : this.workstationPositions) {
            GameObject object = level.getObject(point.x, point.y);
            if (!(object instanceof SettlementWorkstationObject)) continue;
            tileIcons.add(point, new ObjectValue<GameTexture, BooleanSupplier>(this.getInterfaceStyle().workstation, () -> Settings.hideSettlementWorkstations.get() == false));
        }
        for (Map.Entry entry : tileIcons.entrySet()) {
            final Point tile = (Point)entry.getKey();
            final ArrayList icons = (ArrayList)entry.getValue();
            GameObject object = level.getObject(tile.x, tile.y);
            final MultiTile multiTile = object.getMultiTile(level, 0, tile.x, tile.y);
            HudDrawElement hudElement = new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    int iconsToShow = (int)icons.stream().filter(o -> ((BooleanSupplier)o.value).getAsBoolean()).count();
                    if (iconsToShow <= 0) {
                        return;
                    }
                    Point centerPos = multiTile.getCenterLevelPos(tile.x, tile.y);
                    final DrawOptionsList drawOptions = new DrawOptionsList();
                    Color color = new Color(255, 255, 255);
                    drawOptions.add(HUD.tileBoundOptions(camera, color, false, multiTile.getTileRectangle(tile.x, tile.y)));
                    int columns = (int)Math.ceil(Math.sqrt(iconsToShow));
                    int rows = iconsToShow / columns;
                    float sizeMod = Math.min(Math.min((float)multiTile.width / (float)columns, (float)multiTile.height / (float)rows), 1.0f);
                    int size = (int)(32.0f * sizeMod);
                    int iconsWidth = size * columns;
                    int iconsHeight = size * rows;
                    for (int i = 0; i < icons.size(); ++i) {
                        if (!((BooleanSupplier)((ObjectValue)icons.get((int)i)).value).getAsBoolean()) continue;
                        int column = i % columns;
                        int row = i / columns;
                        drawOptions.add(((GameTexture)((ObjectValue)icons.get((int)i)).object).initDraw().color(color).size(size).posMiddle(camera.getDrawX(centerPos.x) - iconsWidth / 2 + column * size + size / 2, camera.getDrawY(centerPos.y) - iconsHeight / 2 + row * size + size / 2));
                    }
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -1000000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            drawOptions.draw();
                        }
                    });
                }
            };
            level.hudManager.addElement(hudElement);
            this.hudElements.add(hudElement);
        }
        for (SettlementWorkZone settlementWorkZone : this.settlementWorkZones.values()) {
            HudDrawElement hudElement = settlementWorkZone.getHudDrawElement(-999500, () -> this.overrideShowZones);
            level.hudManager.addElement(hudElement);
            this.hudElements.add(hudElement);
        }
    }

    protected void startAssignStorageTool() {
        GameToolManager.clearGameTools(this);
        GameToolManager.setGameTool(new SelectTileGameTool(this.client.getLevel(), new LocalMessage("ui", "settlementassignstorage")){

            @Override
            public DrawOptions getIconTexture(Color color, int drawX, int drawY) {
                return SettlementAssignWorkForm.this.getInterfaceStyle().storage.initDraw().color(color).posMiddle(drawX, drawY);
            }

            @Override
            public boolean onSelected(InputEvent event, TilePosition pos) {
                LevelObject master;
                if (pos == null) {
                    return true;
                }
                if (event.state && (master = (LevelObject)pos.object().getMasterLevelObject().orElse(null)) != null) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).assignStorage.runAndSend(master.tileX, master.tileY);
                    return true;
                }
                return false;
            }

            @Override
            public GameMessage isValidTile(TilePosition pos) {
                this.lastHoverBounds = null;
                LevelObject master = pos.object().getMasterLevelObject().orElse(null);
                if (master != null) {
                    this.lastHoverBounds = master.getMultiTile().getTileRectangle(master.tileX, master.tileY);
                    if (SettlementAssignWorkForm.this.storagePositions.contains(new Point(master.tileX, master.tileY))) {
                        return new LocalMessage("ui", "settlementalreadyinventory");
                    }
                    ObjectEntity objectEntity = this.level.entityManager.getObjectEntity(master.tileX, master.tileY);
                    if (objectEntity instanceof OEInventory) {
                        if (((OEInventory)((Object)objectEntity)).getSettlementStorage() != null) {
                            return null;
                        }
                        return new LocalMessage("ui", "settlementcannotinventory");
                    }
                }
                return new LocalMessage("ui", "settlementnotinventory");
            }
        }, this);
    }

    public void openStorageConfig(int tileX, int tileY) {
        this.requestedStorageConfigPos = new Point(tileX, tileY);
        ((SettlementContainer)this.container).openStorage.runAndSend(tileX, tileY);
    }

    public void setupConfigStorage(final SettlementOpenStorageConfigEvent update) {
        ItemCategoriesFilter filter;
        Inventory inventory = null;
        GameMessage name = this.client.getLevel().getObjectName(update.tileX, update.tileY);
        ObjectEntity objectEntity = this.client.getLevel().entityManager.getObjectEntity(update.tileX, update.tileY);
        if (objectEntity instanceof OEInventory) {
            filter = update.getFilter((OEInventory)((Object)objectEntity));
            inventory = ((OEInventory)((Object)objectEntity)).getInventory();
            name = ((OEInventory)((Object)objectEntity)).getInventoryName();
        } else {
            filter = new ItemCategoriesFilter(false);
        }
        int subscribeID = ((SettlementContainer)this.container).subscribeStorage.subscribe(new Point(update.tileX, update.tileY));
        SettlementStorageConfigForm newStorageConfig = this.addComponent(new SettlementStorageConfigForm("storageConfig", 500, 350, new Point(update.tileX, update.tileY), this.client, inventory, name, filter, update.priority){

            @Override
            public void onItemsChanged(Item[] items, boolean allowed) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).changeAllowedStorage.runAndSend(update.tileX, update.tileY, items, allowed);
            }

            @Override
            public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).changeLimitsStorage.runAndSend(update.tileX, update.tileY, item, limits);
            }

            @Override
            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).changeAllowedStorage.runAndSend(update.tileX, update.tileY, category, allowed);
            }

            @Override
            public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).changeLimitsStorage.runAndSend(update.tileX, update.tileY, category, maxItems);
            }

            @Override
            public void onFullChange(ItemCategoriesFilter filter, int priority) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).fullUpdateSettlementStorage.runAndSend(update.tileX, update.tileY, filter, priority);
            }

            @Override
            public void onPriorityChange(int priority) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).priorityLimitStorage.runAndSendPriority(update.tileX, update.tileY, priority);
            }

            @Override
            public void onLimitChange(ItemCategoriesFilter.ItemLimitMode mode, int limit) {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).priorityLimitStorage.runAndSendLimit(update.tileX, update.tileY, mode, limit);
            }

            @Override
            public void onRemove() {
                ((SettlementContainer)SettlementAssignWorkForm.this.container).removeStorage.runAndSend(update.tileX, update.tileY);
                SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
            }

            @Override
            public void onBack() {
                SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
            }
        }, (form, active) -> {
            if (!active.booleanValue()) {
                this.removeComponent(form);
                this.storageConfig = null;
                ((SettlementContainer)this.container).subscribeStorage.unsubscribe(subscribeID);
            }
        });
        this.makeCurrent(newStorageConfig);
        this.storageConfig = newStorageConfig;
        this.storageConfig.setPosInventory();
        this.requestedStorageConfigPos = null;
    }

    protected void startAssignWorkstationTool() {
        GameToolManager.clearGameTools(this);
        GameToolManager.setGameTool(new SelectTileGameTool(this.client.getLevel(), new LocalMessage("ui", "settlementassignworkstation")){

            @Override
            public DrawOptions getIconTexture(Color color, int drawX, int drawY) {
                return SettlementAssignWorkForm.this.getInterfaceStyle().workstation.initDraw().color(color).posMiddle(drawX, drawY);
            }

            @Override
            public boolean onSelected(InputEvent event, TilePosition pos) {
                LevelObject master;
                if (pos == null) {
                    return true;
                }
                if (event.state && (master = (LevelObject)pos.object().getMasterLevelObject().orElse(null)) != null) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).assignWorkstation.runAndSend(master.tileX, master.tileY);
                    return true;
                }
                return false;
            }

            @Override
            public GameMessage isValidTile(TilePosition pos) {
                this.lastHoverBounds = null;
                LevelObject master = pos.object().getMasterLevelObject().orElse(null);
                if (master != null) {
                    this.lastHoverBounds = master.getMultiTile().getTileRectangle(master.tileX, master.tileY);
                    if (SettlementAssignWorkForm.this.workstationPositions.contains(new Point(master.tileX, master.tileY))) {
                        return new LocalMessage("ui", "settlementalreadyworkstation");
                    }
                    if (master.object instanceof SettlementWorkstationObject) {
                        return null;
                    }
                    return new LocalMessage("ui", "settlementcannotworkstation");
                }
                return new LocalMessage("ui", "settlementnotworkstation");
            }
        }, this);
    }

    public void openWorkstationConfig(int tileX, int tileY) {
        this.requestedWorkstationConfigPos = new Point(tileX, tileY);
        ((SettlementContainer)this.container).openWorkstation.runAndSend(tileX, tileY);
    }

    public void setupConfigWorkstation(final SettlementOpenWorkstationEvent event) {
        SettlementWorkstationLevelObject workstationObject = null;
        GameObject object = this.client.getLevel().getObject(event.tileX, event.tileY);
        String name = object.getDisplayName();
        if (object instanceof SettlementWorkstationObject) {
            workstationObject = new SettlementWorkstationLevelObject(this.client.getLevel(), event.tileX, event.tileY);
        }
        if (workstationObject != null) {
            int subscriptionID = ((SettlementContainer)this.container).subscribeWorkstation.subscribe(new Point(event.tileX, event.tileY));
            SettlementWorkstationConfigForm newWorkstationConfig = this.addComponent(new SettlementWorkstationConfigForm("workstationConfig", 400, 240, new Point(event.tileX, event.tileY), this.client, new StaticMessage(name), ((SettlementContainer)this.container).client.playerMob, workstationObject, event.recipes){

                @Override
                public void onSubmitRemove(int uniqueID) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).removeWorkstationRecipe.runAndSend(event.tileX, event.tileY, uniqueID);
                }

                @Override
                public void onSubmitUpdate(int index, SettlementWorkstationRecipe recipe) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).updateWorkstationRecipe.runAndSend(event.tileX, event.tileY, index, recipe);
                }

                @Override
                public void onRemove() {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).removeWorkstation.runAndSend(event.tileX, event.tileY);
                    SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
                }

                @Override
                public void onBack() {
                    SettlementAssignWorkForm.this.makeCurrent(SettlementAssignWorkForm.this.work);
                }
            }, (form, active) -> {
                if (!active.booleanValue()) {
                    ((SettlementContainer)this.container).subscribeWorkstation.unsubscribe(subscriptionID);
                    this.removeComponent(form);
                    this.workstationConfig = null;
                }
            });
            this.makeCurrent(newWorkstationConfig);
            this.workstationConfig = newWorkstationConfig;
            this.workstationConfig.setPosInventory();
            this.requestedWorkstationConfigPos = null;
        }
    }

    public void openWorkZoneConfig(SettlementWorkZone zone) {
        this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
        ((SettlementContainer)this.container).openWorkZoneConfig.runAndSend(zone.getUniqueID());
    }

    public void setupWorkZoneConfig(SettlementOpenWorkZoneConfigEvent event) {
        SettlementWorkZone zone = this.settlementWorkZones.get(event.uniqueID);
        if (zone != null) {
            WorkZoneConfigComponent settingsForm = zone.getSettingsForm(this, () -> this.makeCurrent(this.work), new PacketReader(event.configPacket));
            if (settingsForm != null) {
                int subscriptionID = ((SettlementContainer)this.container).subscribeWorkZoneConfig.subscribe(zone);
                zone.subscribeConfigEvents((SettlementContainer)this.container, () -> ((SettlementContainer)this.container).subscribeWorkZoneConfig.isActive(subscriptionID));
                FormComponent newWorkZoneConfigForm = this.addComponent((FormComponent)((Object)settingsForm), (form, active) -> {
                    if (!active.booleanValue()) {
                        ((SettlementContainer)this.container).subscribeWorkZoneConfig.unsubscribe(subscriptionID);
                        this.removeComponent(form);
                        this.workZoneConfig = null;
                        this.currentOpenWorkZoneConfigUniqueID = 0;
                    }
                });
                this.makeCurrent(newWorkZoneConfigForm);
                this.workZoneConfig = settingsForm;
                this.currentOpenWorkZoneConfigUniqueID = event.uniqueID;
            }
            this.requestedWorkZoneConfigUniqueID = 0;
        }
    }

    public void startAssignForestryZoneTool() {
        GameToolManager.clearGameTools(this);
        GameToolManager.setGameTool(new CreateOrExpandWorkZoneGameTool(this.client.getLevel()){

            @Override
            public Stream<SettlementWorkZone> streamEditZones() {
                return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().filter(z -> z.getID() == SettlementWorkZoneRegistry.FORESTRY_ID);
            }

            @Override
            public void onCreatedNewZone(Rectangle rectangle, Point anchor) {
                SettlementWorkZone zone = SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.FORESTRY_ID);
                zone.expandZone(this.level, rectangle, anchor, (x, y) -> SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (!zone.shouldRemove()) {
                    zone.generateUniqueID(i -> SettlementAssignWorkForm.this.settlementWorkZones.containsKey(i));
                    SettlementAssignWorkForm.this.settlementWorkZones.put(zone.getUniqueID(), zone);
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).createWorkZone.runAndSend(zone.getID(), zone.getUniqueID(), rectangle, anchor);
                    SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void onRemovedZone(SettlementWorkZone zone, Rectangle rectangle) {
                if (zone.shrinkZone(this.level, rectangle)) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).shrinkWorkZone.runAndSend(zone.getUniqueID(), rectangle);
                    if (zone.shouldRemove()) {
                        SettlementAssignWorkForm.this.settlementWorkZones.remove(zone.getUniqueID());
                    }
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void onExpandedZone(SettlementWorkZone zone, Rectangle rectangle, Point anchor) {
                boolean updated = zone.expandZone(this.level, rectangle, anchor, (x, y) -> SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (updated) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).expandWorkZone.runAndSend(zone.getUniqueID(), rectangle, anchor);
                    SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void isCancelled() {
                super.isCancelled();
                SettlementAssignWorkForm.this.overrideShowZones = false;
            }

            @Override
            public void isCleared() {
                super.isCleared();
                SettlementAssignWorkForm.this.overrideShowZones = false;
            }
        }, this);
        this.overrideShowZones = true;
    }

    public void startAssignHusbandryZoneTool() {
        GameToolManager.clearGameTools(this);
        GameToolManager.setGameTool(new CreateOrExpandWorkZoneGameTool(this.client.getLevel()){

            @Override
            public Stream<SettlementWorkZone> streamEditZones() {
                return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().filter(z -> z.getID() == SettlementWorkZoneRegistry.HUSBANDRY_ID);
            }

            @Override
            public void onCreatedNewZone(Rectangle rectangle, Point anchor) {
                SettlementWorkZone zone = SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.HUSBANDRY_ID);
                zone.expandZone(this.level, rectangle, anchor, (x, y) -> SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (!zone.shouldRemove()) {
                    zone.generateUniqueID(i -> SettlementAssignWorkForm.this.settlementWorkZones.containsKey(i));
                    SettlementAssignWorkForm.this.settlementWorkZones.put(zone.getUniqueID(), zone);
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).createWorkZone.runAndSend(zone.getID(), zone.getUniqueID(), rectangle, anchor);
                    SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void onRemovedZone(SettlementWorkZone zone, Rectangle rectangle) {
                if (zone.shrinkZone(this.level, rectangle)) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).shrinkWorkZone.runAndSend(zone.getUniqueID(), rectangle);
                    if (zone.shouldRemove()) {
                        SettlementAssignWorkForm.this.settlementWorkZones.remove(zone.getUniqueID());
                    }
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void onExpandedZone(SettlementWorkZone zone, Rectangle rectangle, Point anchor) {
                boolean updated = zone.expandZone(this.level, rectangle, anchor, (x, y) -> SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (updated) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).expandWorkZone.runAndSend(zone.getUniqueID(), rectangle, anchor);
                    SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void isCancelled() {
                super.isCancelled();
                SettlementAssignWorkForm.this.overrideShowZones = false;
            }

            @Override
            public void isCleared() {
                super.isCleared();
                SettlementAssignWorkForm.this.overrideShowZones = false;
            }
        }, this);
        this.overrideShowZones = true;
    }

    public void startAssignFertilizeZoneTool() {
        GameToolManager.clearGameTools(this);
        GameToolManager.setGameTool(new CreateOrExpandWorkZoneGameTool(this.client.getLevel()){

            @Override
            public Stream<SettlementWorkZone> streamEditZones() {
                return SettlementAssignWorkForm.this.settlementWorkZones.values().stream().filter(z -> z.getID() == SettlementWorkZoneRegistry.FERTILIZE_ID);
            }

            @Override
            public void onCreatedNewZone(Rectangle rectangle, Point anchor) {
                SettlementWorkZone zone = SettlementWorkZoneRegistry.getNewZone(SettlementWorkZoneRegistry.FERTILIZE_ID);
                zone.expandZone(this.level, rectangle, anchor, (x, y) -> SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (!zone.shouldRemove()) {
                    zone.generateUniqueID(i -> SettlementAssignWorkForm.this.settlementWorkZones.containsKey(i));
                    SettlementAssignWorkForm.this.settlementWorkZones.put(zone.getUniqueID(), zone);
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).createWorkZone.runAndSend(zone.getID(), zone.getUniqueID(), rectangle, anchor);
                    SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void onRemovedZone(SettlementWorkZone zone, Rectangle rectangle) {
                if (zone.shrinkZone(this.level, rectangle)) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).shrinkWorkZone.runAndSend(zone.getUniqueID(), rectangle);
                    if (zone.shouldRemove()) {
                        SettlementAssignWorkForm.this.settlementWorkZones.remove(zone.getUniqueID());
                    }
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void onExpandedZone(SettlementWorkZone zone, Rectangle rectangle, Point anchor) {
                boolean updated = zone.expandZone(this.level, rectangle, anchor, (x, y) -> SettlementAssignWorkForm.this.settlementWorkZones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (updated) {
                    ((SettlementContainer)SettlementAssignWorkForm.this.container).expandWorkZone.runAndSend(zone.getUniqueID(), rectangle, anchor);
                    SettlementAssignWorkForm.this.requestedWorkZoneConfigUniqueID = zone.getUniqueID();
                    SettlementAssignWorkForm.this.updateHudElements();
                }
            }

            @Override
            public void isCancelled() {
                super.isCancelled();
                SettlementAssignWorkForm.this.overrideShowZones = false;
            }

            @Override
            public void isCleared() {
                super.isCleared();
                SettlementAssignWorkForm.this.overrideShowZones = false;
            }
        }, this);
        this.overrideShowZones = true;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        ContainerComponent.setPosInventory(this.work);
        if (this.storageConfig != null) {
            this.storageConfig.setPosInventory();
        }
        if (this.workstationConfig != null) {
            this.workstationConfig.setPosInventory();
        }
    }

    @Override
    public void onSetCurrent(boolean current) {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        if (current) {
            this.makeCurrent(this.work);
            this.workContentSubscriptionID = ((SettlementContainer)this.container).subscribeWorkContent.subscribe();
        } else {
            ((SettlementContainer)this.container).subscribeWorkContent.unsubscribe(this.workContentSubscriptionID);
            GameToolManager.clearGameTools(this);
        }
    }

    @Override
    public void dispose() {
        GameToolManager.clearGameTools(this);
        this.hudElements.forEach(HudDrawElement::remove);
        super.dispose();
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementassignwork");
    }

    @Override
    public String getTypeString() {
        return "assignwork";
    }

    @Override
    public SettlementToolHandler getToolHandler() {
        return this.toolHandler;
    }
}

