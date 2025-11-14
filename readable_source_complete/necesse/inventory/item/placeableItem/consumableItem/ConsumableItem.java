/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem;

import java.awt.Point;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.PlaceableItem;

public class ConsumableItem
extends PlaceableItem {
    public boolean allowRightClickToConsume;

    public ConsumableItem(int stackSize, boolean singleUse) {
        super(stackSize, singleUse);
        this.setItemCategory("consumable");
        this.setItemCategory(ItemCategory.craftingManager, "consumable");
        this.keyWords.add("consumeable");
    }

    @Override
    public boolean getConstantUse(InventoryItem item) {
        return false;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        if (this.allowRightClickToConsume && slot.getInventory() instanceof PlayerInventory) {
            return () -> {
                if (container.getClient().isClient()) {
                    Point dir;
                    PlayerMob player = container.getClient().playerMob;
                    switch (player.getDir()) {
                        case 0: {
                            dir = new Point(0, -1);
                            break;
                        }
                        case 1: {
                            dir = new Point(1, 0);
                            break;
                        }
                        case 2: {
                            dir = new Point(0, 1);
                            break;
                        }
                        default: {
                            dir = new Point(-1, 0);
                        }
                    }
                    PlayerInventorySlot invSlot = new PlayerInventorySlot((PlayerInventory)slot.getInventory(), slot.getInventorySlot());
                    if (player.tryAttack(invSlot, player.getX() + dir.x * 100, player.getY() + dir.y * 100) && invSlot.getItem(player.getInv()) == null) {
                        return new ContainerActionResult(null);
                    }
                }
                return new ContainerActionResult(645719);
            };
        }
        return super.getInventoryRightClickAction(container, item, slotIndex, slot);
    }

    public static GameMessage getDurationMessage(int seconds, boolean showAll) {
        int minutes = 0;
        int hours = 0;
        if (seconds >= 60) {
            minutes = seconds / 60;
            seconds -= minutes * 60;
            if (minutes >= 60) {
                hours = minutes / 60;
                minutes -= hours * 60;
            }
        }
        GameMessageBuilder builder = new GameMessageBuilder();
        if (hours > 0) {
            if (hours != 1) {
                builder.append(new LocalMessage("itemtooltip", "hoursduration", "value", hours));
            } else {
                builder.append(new LocalMessage("itemtooltip", "hourduration", "value", hours));
            }
            if (!showAll && hours >= 10) {
                return builder;
            }
        }
        if (minutes > 0) {
            if (builder.size() != 0) {
                builder.append(" ");
            }
            if (minutes != 1) {
                builder.append(new LocalMessage("itemtooltip", "minsduration", "value", minutes));
            } else {
                builder.append(new LocalMessage("itemtooltip", "minduration", "value", minutes));
            }
            if (!(showAll || hours <= 0 && minutes < 10)) {
                return builder;
            }
        }
        if (seconds > 0 || builder.size() == 0) {
            if (builder.size() != 0) {
                builder.append(" ");
            }
            if (seconds != 1) {
                builder.append(new LocalMessage("itemtooltip", "secsduration", "value", seconds));
            } else {
                builder.append(new LocalMessage("itemtooltip", "secduration", "value", seconds));
            }
        }
        return builder;
    }

    public static GameMessage getSpoilsTimeWithRateMessage(int seconds, float spoilRateModifier) {
        if (spoilRateModifier > 0.0f) {
            String percentNumber = GameUtils.formatNumber(spoilRateModifier * 100.0f);
            return new LocalMessage("itemtooltip", "spoilstimewithrate", "time", ConsumableItem.getDurationMessage(seconds, false), "rate", percentNumber + "%");
        }
        return ConsumableItem.getSpoilsTimeMessage(seconds);
    }

    public static GameMessage getSpoilsTimeMessage(int seconds) {
        return new LocalMessage("itemtooltip", "spoilstime", "time", ConsumableItem.getDurationMessage(seconds, false));
    }

    public static GameMessage getSpoilStoppedTimeMessage(int seconds) {
        return new LocalMessage("itemtooltip", "spoilingstopped", "time", ConsumableItem.getDurationMessage(seconds, false));
    }

    public static GameMessage getBuffDurationMessage(int seconds) {
        return new LocalMessage("itemtooltip", "buffduration", "time", ConsumableItem.getDurationMessage(seconds, true));
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("itemcategory", "consumable");
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (drawOptions.dir == 0) {
            drawOptions.swingRotationInv(attackProgress, 150.0f, -50.0f);
        } else if (drawOptions.dir == 2) {
            drawOptions.swingRotationInv(attackProgress, 150.0f, -100.0f);
        } else {
            drawOptions.swingRotationInv(attackProgress);
        }
    }
}

