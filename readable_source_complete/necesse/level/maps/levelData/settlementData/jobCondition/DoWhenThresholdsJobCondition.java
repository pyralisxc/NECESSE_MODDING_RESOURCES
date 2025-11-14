/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.jobCondition;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Objects;
import necesse.engine.Settings;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairButtonGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.presets.containerComponent.settlement.SelectItemFloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionUpdatePacketSender;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;

public class DoWhenThresholdsJobCondition
extends JobCondition {
    protected ArrayList<ItemThreshold> thresholds = new ArrayList();

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        SaveData thresholdsSave = new SaveData("THRESHOLDS");
        for (ItemThreshold threshold : this.thresholds) {
            thresholdsSave.addSaveData(threshold.getSaveData(""));
        }
        save.addSaveData(thresholdsSave);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.thresholds.clear();
        LoadData thresholdsSave = save.getFirstLoadDataByName("THRESHOLDS");
        if (thresholdsSave != null) {
            for (LoadData thresholdSave : thresholdsSave.getLoadData()) {
                try {
                    ItemThreshold itemThreshold = new ItemThreshold(thresholdSave);
                    this.thresholds.add(itemThreshold);
                }
                catch (Exception e) {
                    System.err.println("Error loading item threshold: ");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextCollection(this.thresholds, threshold -> threshold.writePacket(writer));
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.thresholds = reader.getNextCollection(ArrayList::new, () -> new ItemThreshold(reader));
    }

    @Override
    public void applyUpdatePacket(int type, PacketReader reader) {
        super.applyUpdatePacket(type, reader);
        if (type == 0) {
            this.applySpawnPacket(reader);
            this.markDirty();
        }
    }

    public void sendFullUpdatePacket(JobConditionUpdatePacketSender sender) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        this.setupSpawnPacket(writer);
        sender.sendUpdatePacket(0, packet);
    }

    @Override
    public boolean isConditionMet(EntityJobWorker settlerMob, ServerSettlementData serverData) {
        SettlementStorageItemIDIndex itemStorageIndex = serverData.storageRecords.getIndex(SettlementStorageItemIDIndex.class);
        if (this.thresholds.isEmpty()) {
            return false;
        }
        for (ItemThreshold threshold : this.thresholds) {
            if (threshold.isConditionMet(itemStorageIndex)) continue;
            return false;
        }
        return true;
    }

    @Override
    public GameMessage getSelectedMessage() {
        if (this.thresholds.size() == 1) {
            ItemThreshold threshold = this.thresholds.get(0);
            String conditionString = TypeParsers.getItemParseString(new InventoryItem(threshold.item));
            conditionString = threshold.whenGreaterThan ? conditionString + " > " + threshold.threshold : conditionString + " < " + threshold.threshold;
            return new LocalMessage("ui", "conditiondowhen", "condition", conditionString);
        }
        if (this.thresholds.isEmpty()) {
            return new LocalMessage("ui", "conditiondowhen", "condition", "-");
        }
        StringBuilder itemStringBuilder = new StringBuilder();
        for (ItemThreshold threshold : this.thresholds) {
            itemStringBuilder.append(TypeParsers.getItemParseString(new InventoryItem(threshold.item)));
        }
        return new LocalMessage("ui", "conditiondowhen", "condition", itemStringBuilder.toString());
    }

    @Override
    public Form getConfigurationForm(Client client, int minWidth, final JobConditionUpdatePacketSender updatePacketSender, ArrayList<Runnable> updateListeners, final Runnable refreshForm) {
        Form form = new Form(Math.max(minWidth, 500), 0);
        FormFlow flow = new FormFlow(4);
        form.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "conditiondowhen", "condition", "X"), new FontOptions(20), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        form.addComponent(flow.nextY(new FormLocalLabel("ui", "conditiondowhenstoragetip", new FontOptions(16), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        flow.next(10);
        ArrayList<FormFairTypeLabel> labels = new ArrayList<FormFairTypeLabel>();
        ArrayList<FormContentIconButton> configButtons = new ArrayList<FormContentIconButton>(this.thresholds.size());
        ArrayList<FormContentIconButton> deleteButtons = new ArrayList<FormContentIconButton>(this.thresholds.size());
        final Runnable updateButtonPositions = () -> {
            int maxWidth = labels.stream().mapToInt(label -> label.getBoundingBox().width).max().orElse(0);
            for (FormContentIconButton configButton : configButtons) {
                configButton.setX(form.getWidth() / 2 - maxWidth / 2 - 6 - 20);
            }
            for (FormContentIconButton deleteButton : deleteButtons) {
                deleteButton.setX(form.getWidth() / 2 + maxWidth / 2 + 6);
            }
        };
        for (final ItemThreshold threshold : this.thresholds) {
            final FormContentIconButton configButton = form.addComponent(new FormContentIconButton(0, 0, FormInputSize.SIZE_20, ButtonColor.BASE, Settings.UI.button_collapsed_16, new LocalMessage("ui", "changebutton")));
            configButton.onClicked(e -> {
                threshold.whenGreaterThan = !threshold.whenGreaterThan;
                this.sendFullUpdatePacket(updatePacketSender);
                refreshForm.run();
            });
            configButtons.add(configButton);
            final FormContentIconButton deleteButton = form.addComponent(new FormContentIconButton(0, 0, FormInputSize.SIZE_20, ButtonColor.RED, Settings.UI.container_storage_remove, new LocalMessage("ui", "deletebutton")));
            deleteButton.onClicked(e -> {
                this.thresholds.remove(threshold);
                this.sendFullUpdatePacket(updatePacketSender);
                refreshForm.run();
            });
            deleteButtons.add(deleteButton);
            FontOptions fontOptions = new FontOptions(16);
            final FormFairTypeLabel label = new FormFairTypeLabel(new StaticMessage(""), fontOptions, FairType.TextAlign.CENTER, form.getWidth() / 2, 0);
            labels.add(label);
            Runnable updateLabel = new Runnable(){

                @Override
                public void run() {
                    threshold.updateLabel(label, updatePacketSender, this);
                    int buttonY = label.getY() + label.getBoundingBox().height / 2 - 8;
                    configButton.setY(buttonY);
                    deleteButton.setY(buttonY);
                    updateButtonPositions.run();
                }
            };
            threshold.updateLabel(label, updatePacketSender, updateLabel);
            Rectangle labelBoundingBox = label.getBoundingBox();
            int thresholdY = flow.next(Math.max(labelBoundingBox.height, 24) + 4);
            label.setY(thresholdY);
            form.addComponent(label);
            int buttonY = thresholdY + labelBoundingBox.height / 2 - 8;
            configButton.setY(buttonY);
            deleteButton.setY(buttonY);
        }
        updateButtonPositions.run();
        flow.next(10);
        FormLocalTextButton addItemButton = form.addComponent(new FormLocalTextButton("ui", "conditiondowhenadditemthreshold", form.getWidth() / 2 - 150, flow.next(28), 300, FormInputSize.SIZE_24, ButtonColor.BASE));
        addItemButton.onClicked(configureEvent -> {
            SelectItemFloatMenu menu = new SelectItemFloatMenu(addItemButton, client, 400, 400){

                @Override
                public void onItemSelected(Item item) {
                    DoWhenThresholdsJobCondition.this.thresholds.add(new ItemThreshold(item, 0, false));
                    DoWhenThresholdsJobCondition.this.sendFullUpdatePacket(updatePacketSender);
                    this.remove();
                    refreshForm.run();
                }
            };
            addItemButton.getManager().openFloatMenu((FloatMenu)menu, addItemButton.getX() - configureEvent.event.pos.hudX + 2, addItemButton.getY() - configureEvent.event.pos.hudY + 2);
        });
        form.setHeight(flow.next());
        return form;
    }

    public class ItemThreshold {
        private final Object repeatInc = new Object();
        private final Object repeatDec = new Object();
        public final Item item;
        public int threshold;
        public boolean whenGreaterThan;

        public ItemThreshold(Item item, int threshold, boolean whenGreaterThan) {
            Objects.requireNonNull(item);
            this.item = item;
            this.threshold = threshold;
            this.whenGreaterThan = whenGreaterThan;
        }

        public ItemThreshold(LoadData save) {
            String itemStringID = save.getSafeString("itemStringID");
            this.item = ItemRegistry.getItem(itemStringID);
            itemStringID = VersionMigration.tryFixStringID(itemStringID, VersionMigration.oldItemStringIDs);
            if (this.item == null) {
                throw new LoadDataException("Could not find job condition item threshold with item stringID: " + itemStringID);
            }
            this.threshold = save.getInt("threshold", this.threshold);
            this.whenGreaterThan = save.getBoolean("whenGreaterThan", this.whenGreaterThan);
        }

        public SaveData getSaveData(String name) {
            SaveData save = new SaveData(name);
            save.addSafeString("itemStringID", this.item.getStringID());
            save.addInt("threshold", this.threshold);
            save.addBoolean("whenGreaterThan", this.whenGreaterThan);
            return save;
        }

        public ItemThreshold(PacketReader reader) {
            this.item = ItemRegistry.getItem(reader.getNextShortUnsigned());
            Objects.requireNonNull(this.item);
            this.threshold = reader.getNextInt();
            this.whenGreaterThan = reader.getNextBoolean();
        }

        public void writePacket(PacketWriter writer) {
            writer.putNextShortUnsigned(this.item.getID());
            writer.putNextInt(this.threshold);
            writer.putNextBoolean(this.whenGreaterThan);
        }

        public boolean isConditionMet(SettlementStorageItemIDIndex itemStorageIndex) {
            int totalItems = itemStorageIndex.getTotalItems(this.item);
            if (this.whenGreaterThan) {
                return totalItems > this.threshold;
            }
            return totalItems < this.threshold;
        }

        public void updateLabel(final FormFairTypeLabel label, final JobConditionUpdatePacketSender updatePacketSender, final Runnable updateLabel) {
            FontOptions fontOptions = label.getFontOptions();
            String type = this.whenGreaterThan ? "conditiondowhenstoragegreaterthan" : "conditiondowhenstoragelessthan";
            FairType fairType = new FairType();
            fairType.append(fontOptions, new LocalMessage("ui", type, "count", "[[-]] " + this.threshold + " [[+]]", "item", TypeParsers.getItemParseString(new InventoryItem(this.item)) + " " + ItemRegistry.getDisplayName(this.item.getID())).translate());
            fairType.applyParsers(TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL, TypeParsers.ItemIcon(fontOptions.getSize(), false, FairItemGlyph::onlyShowNameTooltip), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions), TypeParsers.replaceParser("[[-]]", new FairButtonGlyph(16, 16){

                @Override
                public void handleEvent(float drawX, float drawY, InputEvent event) {
                    if ((event.getID() == -100 || event.isRepeatEvent(ItemThreshold.this.repeatDec)) && event.state) {
                        event.startRepeatEvents(ItemThreshold.this.repeatDec);
                        int amount = 1;
                        if (Control.INV_QUICK_MOVE.isDown()) {
                            amount = 10;
                        } else if (Control.INV_QUICK_TRASH.isDown() || Control.INV_QUICK_DROP.isDown()) {
                            amount = 100;
                        }
                        int lastRemainingTimes = ItemThreshold.this.threshold;
                        ItemThreshold.this.threshold = Math.max(0, ItemThreshold.this.threshold - amount);
                        if (lastRemainingTimes != ItemThreshold.this.threshold) {
                            if (updatePacketSender != null) {
                                DoWhenThresholdsJobCondition.this.sendFullUpdatePacket(updatePacketSender);
                            }
                            if (event.shouldSubmitSound()) {
                                label.playTickSound();
                            }
                            updateLabel.run();
                        }
                        WindowManager.getWindow().getInput().submitNextMoveEvent();
                    }
                }

                @Override
                public void draw(float x, float y, Color defaultColor) {
                    Color color = this.isHovering() ? (Color)label.getInterfaceStyle().button_minus_20.colorGetter.apply(ButtonState.HIGHLIGHTED) : (Color)label.getInterfaceStyle().button_minus_20.colorGetter.apply(ButtonState.ACTIVE);
                    label.getInterfaceStyle().button_minus_20.texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                    if (this.isHovering()) {
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }), TypeParsers.replaceParser("[[+]]", new FairButtonGlyph(16, 16){

                @Override
                public void handleEvent(float drawX, float drawY, InputEvent event) {
                    if ((event.getID() == -100 || event.isRepeatEvent(ItemThreshold.this.repeatInc)) && event.state) {
                        event.startRepeatEvents(ItemThreshold.this.repeatInc);
                        int amount = 1;
                        if (Control.INV_QUICK_MOVE.isDown()) {
                            amount = 10;
                        } else if (Control.INV_QUICK_TRASH.isDown() || Control.INV_QUICK_DROP.isDown()) {
                            amount = 100;
                        }
                        int lastRemainingTimes = ItemThreshold.this.threshold;
                        ItemThreshold.this.threshold = Math.min(65535, ItemThreshold.this.threshold + amount);
                        if (lastRemainingTimes != ItemThreshold.this.threshold) {
                            if (updatePacketSender != null) {
                                DoWhenThresholdsJobCondition.this.sendFullUpdatePacket(updatePacketSender);
                            }
                            if (event.shouldSubmitSound()) {
                                label.playTickSound();
                            }
                            updateLabel.run();
                        }
                        WindowManager.getWindow().getInput().submitNextMoveEvent();
                    }
                }

                @Override
                public void draw(float x, float y, Color defaultColor) {
                    Color color = this.isHovering() ? (Color)label.getInterfaceStyle().button_plus_20.colorGetter.apply(ButtonState.HIGHLIGHTED) : (Color)label.getInterfaceStyle().button_plus_20.colorGetter.apply(ButtonState.ACTIVE);
                    label.getInterfaceStyle().button_plus_20.texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                    if (this.isHovering()) {
                        Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                    }
                }
            }));
            label.setCustomFairType(fairType);
        }
    }
}

