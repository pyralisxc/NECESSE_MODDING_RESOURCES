/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Point;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementStorageConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementWorkstationConfigForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.inventory.Inventory;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeRemoveEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;

public class SettlementObjectStatusFormManager {
    private final SettlementDependantContainer container;
    private final SettlementContainerObjectStatusManager manager;
    private final FormSwitcher switcher;
    private final FormComponent defaultForm;
    private final Client client;
    public FormContentIconButton configureStorageButton;
    public SettlementStorageConfigForm storageConfigForm;
    public boolean openStorageConfig;
    public FormContentIconButton configureWorkstationButton;
    public SettlementWorkstationConfigForm workstationConfigForm;
    public boolean openWorkstationConfig;

    public SettlementObjectStatusFormManager(SettlementDependantContainer container, SettlementContainerObjectStatusManager manager, FormSwitcher switcher, FormComponent defaultForm, Client client) {
        this.container = container;
        this.manager = manager;
        this.switcher = switcher;
        this.defaultForm = defaultForm;
        this.client = client;
        if (manager.canSettlementStorageConfigure) {
            container.onEvent(SettlementDataEvent.class, event -> this.updateConfigureButtons());
            container.onEvent(SettlementOpenStorageConfigEvent.class, event -> {
                manager.isSettlementStorage = true;
                if (this.openStorageConfig) {
                    this.setupConfigStorage((SettlementOpenStorageConfigEvent)event);
                }
                this.updateConfigureButtons();
            });
            container.onEvent(SettlementSingleStorageEvent.class, event -> this.updateConfigureButtons());
            container.onEvent(SettlementStorageChangeAllowedEvent.class, event -> {
                if (this.storageConfigForm != null && this.storageConfigForm.tile.x == event.tileX && this.storageConfigForm.tile.y == event.tileY) {
                    if (event.isItems) {
                        for (Item item : event.items) {
                            this.storageConfigForm.filter.setItemAllowed(item, event.allowed);
                            this.storageConfigForm.filterForm.updateAllButtons();
                        }
                    } else {
                        ItemCategoriesFilter.ItemCategoryFilter category = this.storageConfigForm.filter.getItemCategory(event.category.id);
                        category.setAllowed(event.allowed);
                        this.storageConfigForm.filterForm.updateButtons(event.category);
                    }
                }
            });
            container.onEvent(SettlementStorageLimitsEvent.class, event -> {
                if (this.storageConfigForm != null && this.storageConfigForm.tile.x == event.tileX && this.storageConfigForm.tile.y == event.tileY) {
                    if (event.isItems) {
                        this.storageConfigForm.filter.setItemAllowed(event.item, event.limits);
                        this.storageConfigForm.filterForm.updateAllButtons();
                    } else {
                        ItemCategoriesFilter.ItemCategoryFilter category = this.storageConfigForm.filter.getItemCategory(event.category.id);
                        category.setMaxItems(event.maxItems);
                        this.storageConfigForm.filterForm.updateButtons(event.category);
                    }
                }
            });
            container.onEvent(SettlementStoragePriorityLimitEvent.class, event -> {
                if (this.storageConfigForm != null && this.storageConfigForm.tile.x == event.tileX && this.storageConfigForm.tile.y == event.tileY) {
                    if (event.isPriority) {
                        this.storageConfigForm.updatePrioritySelect(event.priority);
                    } else {
                        this.storageConfigForm.filter.limitMode = event.limitMode;
                        this.storageConfigForm.filter.maxAmount = event.limit;
                        this.storageConfigForm.updateLimitMode();
                        this.storageConfigForm.updateLimitInput();
                    }
                }
            });
            container.onEvent(SettlementStorageFullUpdateEvent.class, event -> {
                if (this.storageConfigForm != null && this.storageConfigForm.tile.x == event.tileX && this.storageConfigForm.tile.y == event.tileY) {
                    this.storageConfigForm.updatePrioritySelect(event.priority);
                    this.storageConfigForm.filter.readPacket(new PacketReader(event.filterContent));
                    this.storageConfigForm.updateLimitInput();
                    this.storageConfigForm.filterForm.updateAllButtons();
                }
            });
        }
        if (manager.canSettlementWorkstationConfigure) {
            container.onEvent(SettlementSingleWorkstationsEvent.class, event -> this.updateConfigureButtons());
            container.onEvent(SettlementOpenWorkstationEvent.class, event -> {
                manager.isSettlementWorkstation = true;
                if (this.openWorkstationConfig) {
                    this.setupConfigWorkstation((SettlementOpenWorkstationEvent)event);
                }
                this.updateConfigureButtons();
            });
            container.onEvent(SettlementWorkstationEvent.class, event -> {
                if (this.workstationConfigForm != null && switcher.isCurrent(this.workstationConfigForm) && this.workstationConfigForm.tile.x == event.tileX && this.workstationConfigForm.tile.y == event.tileY) {
                    this.workstationConfigForm.setRecipes(event.recipes);
                }
            });
            container.onEvent(SettlementWorkstationRecipeUpdateEvent.class, event -> {
                if (this.workstationConfigForm != null && switcher.isCurrent(this.workstationConfigForm) && this.workstationConfigForm.tile.x == event.tileX && this.workstationConfigForm.tile.y == event.tileY) {
                    this.workstationConfigForm.onRecipeUpdate((SettlementWorkstationRecipeUpdateEvent)event);
                }
            });
            container.onEvent(SettlementWorkstationRecipeRemoveEvent.class, event -> {
                if (this.workstationConfigForm != null && switcher.isCurrent(this.workstationConfigForm) && this.workstationConfigForm.tile.x == event.tileX && this.workstationConfigForm.tile.y == event.tileY) {
                    this.workstationConfigForm.onRecipeRemove((SettlementWorkstationRecipeRemoveEvent)event);
                }
            });
        }
    }

    public void onStorageConfigBack() {
        this.switcher.makeCurrent(this.defaultForm);
    }

    public void onWorkstationConfigBack() {
        this.switcher.makeCurrent(this.defaultForm);
    }

    public boolean addStorageConfigButton(Form form, int x, int y) {
        if (this.manager.canSettlementStorageConfigure) {
            this.configureStorageButton = form.addComponent(new FormContentIconButton(x, y, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)form.getInterfaceStyle().container_storage_add, new GameMessage[0]){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    if (!this.isActive()) {
                        if (!SettlementObjectStatusFormManager.this.container.hasSettlement()) {
                            return new StringTooltips(Localization.translate("ui", "settlementnotfound"));
                        }
                        StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "settlementispriv"));
                        tooltips.add(Localization.translate("ui", "settlementprivatetip"), GameColor.LIGHT_GRAY, 400);
                        return tooltips;
                    }
                    if (((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.isSettlementStorage) {
                        return new StringTooltips(Localization.translate("ui", "settlementconfigurestorage"));
                    }
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "settlementaddstorage"));
                    tooltips.add(Localization.translate("ui", "settlementstoragetip"), GameColor.LIGHT_GRAY, 400);
                    return tooltips;
                }
            });
            this.configureStorageButton.onClicked(e -> {
                this.openStorageConfig = true;
                this.manager.openSettlementStorageConfig.runAndSend();
            });
            this.configureStorageButton.setCooldown(500);
            this.updateConfigureButtons();
            return true;
        }
        return false;
    }

    public boolean addWorkstationConfigButton(Form form, int x, int y) {
        if (this.manager.canSettlementWorkstationConfigure) {
            this.configureWorkstationButton = form.addComponent(new FormContentIconButton(x, y, FormInputSize.SIZE_24, ButtonColor.BASE, (ButtonTexture)form.getInterfaceStyle().container_storage_add, new GameMessage[0]){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    if (!this.isActive()) {
                        if (!SettlementObjectStatusFormManager.this.container.hasSettlement()) {
                            return new StringTooltips(Localization.translate("ui", "settlementnotfound"));
                        }
                        StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "settlementispriv"));
                        tooltips.add(Localization.translate("ui", "settlementprivatetip"), GameColor.LIGHT_GRAY, 400);
                        return tooltips;
                    }
                    if (((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.isSettlementWorkstation) {
                        return new StringTooltips(Localization.translate("ui", "settlementconfigureworkstation"));
                    }
                    StringTooltips tooltips = new StringTooltips(Localization.translate("ui", "settlementaddworkstation"));
                    tooltips.add(Localization.translate("ui", "settlementworkstationtip"), GameColor.LIGHT_GRAY, 400);
                    return tooltips;
                }
            });
            this.configureWorkstationButton.onClicked(e -> {
                this.openWorkstationConfig = true;
                this.manager.openWorkstationConfig.runAndSend();
            });
            this.configureWorkstationButton.setCooldown(500);
            this.updateConfigureButtons();
            return true;
        }
        return false;
    }

    public void addConfigButtonRow(Form form, FormFlow iconFlow, int y, int dir) {
        if (this.addWorkstationConfigButton(form, iconFlow.next() - 24, y)) {
            iconFlow.next(26 * dir);
        }
        if (this.addStorageConfigButton(form, iconFlow.next() - 24, y)) {
            iconFlow.next(26 * dir);
        }
    }

    public void setupConfigStorage(final SettlementOpenStorageConfigEvent update) {
        ItemCategoriesFilter filter;
        ObjectEntity objectEntity = this.manager.level.entityManager.getObjectEntity(update.tileX, update.tileY);
        Inventory inventory = null;
        GameMessage name = this.manager.level.getObjectName(update.tileX, update.tileY);
        if (objectEntity instanceof OEInventory) {
            filter = update.getFilter((OEInventory)((Object)objectEntity));
            name = ((OEInventory)((Object)objectEntity)).getInventoryName();
            inventory = ((OEInventory)((Object)objectEntity)).getInventory();
        } else {
            filter = new ItemCategoriesFilter(false);
        }
        int subscriptionID = this.manager.subscribeStorage.subscribe(new Point(update.tileX, update.tileY));
        SettlementStorageConfigForm newStorageConfig = this.switcher.addComponent(new SettlementStorageConfigForm("storageConfig", 500, 350, new Point(update.tileX, update.tileY), this.client, inventory, name, filter, update.priority){

            @Override
            public void onItemsChanged(Item[] items, boolean allowed) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.changeAllowedStorage.runAndSend(update.tileX, update.tileY, items, allowed);
            }

            @Override
            public void onItemLimitsChanged(Item item, ItemCategoriesFilter.ItemLimits limits) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.changeLimitsStorage.runAndSend(update.tileX, update.tileY, item, limits);
            }

            @Override
            public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.changeAllowedStorage.runAndSend(update.tileX, update.tileY, category, allowed);
            }

            @Override
            public void onCategoryLimitsChanged(ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.changeLimitsStorage.runAndSend(update.tileX, update.tileY, category, maxItems);
            }

            @Override
            public void onFullChange(ItemCategoriesFilter filter, int priority) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.fullUpdateSettlementStorage.runAndSend(update.tileX, update.tileY, filter, priority);
            }

            @Override
            public void onPriorityChange(int priority) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.priorityLimitStorage.runAndSendPriority(update.tileX, update.tileY, priority);
            }

            @Override
            public void onLimitChange(ItemCategoriesFilter.ItemLimitMode mode, int limit) {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.priorityLimitStorage.runAndSendLimit(update.tileX, update.tileY, mode, limit);
            }

            @Override
            public void onRemove() {
                ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.removeStorage.runAndSend(update.tileX, update.tileY);
                SettlementObjectStatusFormManager.this.onStorageConfigBack();
            }

            @Override
            public void onBack() {
                SettlementObjectStatusFormManager.this.onStorageConfigBack();
            }
        }, (form, active) -> {
            if (!active.booleanValue()) {
                this.switcher.removeComponent(form);
                this.storageConfigForm = null;
                this.manager.subscribeStorage.unsubscribe(subscriptionID);
            }
        });
        this.switcher.makeCurrent(newStorageConfig);
        this.storageConfigForm = newStorageConfig;
        this.storageConfigForm.setPosFocus();
        this.openStorageConfig = false;
    }

    public void setupConfigWorkstation(final SettlementOpenWorkstationEvent event) {
        SettlementWorkstationLevelObject workstationObject = null;
        GameObject object = this.manager.level.getObject(event.tileX, event.tileY);
        String name = object.getDisplayName();
        if (object instanceof SettlementWorkstationObject) {
            workstationObject = new SettlementWorkstationLevelObject(this.manager.level, event.tileX, event.tileY);
        }
        if (workstationObject != null) {
            int subscriptionID = this.manager.subscribeWorkstation.subscribe(new Point(event.tileX, event.tileY));
            SettlementWorkstationConfigForm newWorkstationConfig = this.switcher.addComponent(new SettlementWorkstationConfigForm("workstationConfig", 400, 240, new Point(event.tileX, event.tileY), this.client, new StaticMessage(name), this.container.client.playerMob, workstationObject, event.recipes){

                @Override
                public void onSubmitRemove(int uniqueID) {
                    ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.removeWorkstationRecipe.runAndSend(event.tileX, event.tileY, uniqueID);
                }

                @Override
                public void onSubmitUpdate(int index, SettlementWorkstationRecipe recipe) {
                    ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.updateWorkstationRecipe.runAndSend(event.tileX, event.tileY, index, recipe);
                }

                @Override
                public void onRemove() {
                    ((SettlementObjectStatusFormManager)SettlementObjectStatusFormManager.this).manager.removeWorkstation.runAndSend(event.tileX, event.tileY);
                    SettlementObjectStatusFormManager.this.onWorkstationConfigBack();
                }

                @Override
                public void onBack() {
                    SettlementObjectStatusFormManager.this.onWorkstationConfigBack();
                }
            }, (form, active) -> {
                if (!active.booleanValue()) {
                    this.manager.subscribeWorkstation.unsubscribe(subscriptionID);
                    this.switcher.removeComponent(form);
                    this.workstationConfigForm = null;
                }
            });
            this.switcher.makeCurrent(newWorkstationConfig);
            this.workstationConfigForm = newWorkstationConfig;
            this.workstationConfigForm.setPosFocus();
            this.openWorkstationConfig = false;
        }
    }

    protected void updateConfigureButtons() {
        if (this.configureStorageButton != null) {
            this.configureStorageButton.setActive(this.container.hasSettlementAccess(this.client) && this.manager.canSettlementStorageConfigure);
            if (this.manager.isSettlementStorage) {
                this.configureStorageButton.setIcon(this.configureStorageButton.getInterfaceStyle().container_storage_config);
                this.configureStorageButton.color = ButtonColor.BASE;
            } else {
                this.configureStorageButton.setIcon(this.configureStorageButton.getInterfaceStyle().container_storage_add);
                ButtonColor buttonColor = this.configureStorageButton.color = this.configureStorageButton.isActive() ? ButtonColor.GREEN : ButtonColor.BASE;
                if (this.storageConfigForm != null && this.switcher.isCurrent(this.storageConfigForm)) {
                    this.onStorageConfigBack();
                }
            }
        }
        if (this.configureWorkstationButton != null) {
            this.configureWorkstationButton.setActive(this.container.hasSettlementAccess(this.client) && this.manager.canSettlementWorkstationConfigure);
            if (this.manager.isSettlementWorkstation) {
                this.configureWorkstationButton.setIcon(this.configureWorkstationButton.getInterfaceStyle().container_storage_config);
                this.configureWorkstationButton.color = ButtonColor.BASE;
            } else {
                this.configureWorkstationButton.setIcon(this.configureWorkstationButton.getInterfaceStyle().container_storage_add);
                ButtonColor buttonColor = this.configureWorkstationButton.color = this.configureWorkstationButton.isActive() ? ButtonColor.GREEN : ButtonColor.BASE;
                if (this.workstationConfigForm != null && this.switcher.isCurrent(this.workstationConfigForm)) {
                    this.onWorkstationConfigBack();
                }
            }
        }
    }

    public void updateButtons() {
        this.updateConfigureButtons();
    }

    public void onWindowResized() {
        if (this.storageConfigForm != null) {
            this.storageConfigForm.setPosFocus();
        }
        if (this.workstationConfigForm != null) {
            this.workstationConfigForm.setPosFocus();
        }
    }
}

