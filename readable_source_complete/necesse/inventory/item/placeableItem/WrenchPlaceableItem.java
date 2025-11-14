/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem;

import java.awt.geom.Line2D;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.attackHandler.SimplePlaceableItemAttackHandler;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.forms.presets.sidebar.SidebarForm;
import necesse.gfx.forms.presets.sidebar.WireEditSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class WrenchPlaceableItem
extends PlaceableItem
implements ItemInteractAction {
    public WrenchPlaceableItem() {
        super(1, false);
        this.controllerIsTileBasedPlacing = true;
        this.rarity = Item.Rarity.COMMON;
        this.setItemCategory("wiring");
        this.setItemCategory(ItemCategory.craftingManager, "wiring");
        this.keyWords.add("wire");
        this.keyWords.add("logic");
        this.keyWords.add("gate");
        this.keyWords.add("logicgate");
        this.attackXOffset = 8;
        this.attackYOffset = 8;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return this.getItemSprite(item, player);
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        if (!attackerMob.isPlayer) {
            return false;
        }
        return this.canInteractError(level, x, y, (PlayerMob)attackerMob, item) == null;
    }

    public String canInteractError(Level level, int x, int y, PlayerMob player, InventoryItem item) {
        int tileY;
        int tileX = GameMath.getTileCoordinate(x);
        if (level.isProtected(tileX, tileY = GameMath.getTileCoordinate(y))) {
            return "protected";
        }
        if (!level.logicLayer.hasGate(tileX, tileY)) {
            return "nogate";
        }
        if (player.getPositionPoint().distance(tileX * 32 + 16, tileY * 32 + 16) > (double)this.getPlaceRange(item, player)) {
            return "outofrange";
        }
        return null;
    }

    @Override
    public boolean overridesObjectInteract(Level level, PlayerMob player, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        LogicGateEntity entity;
        if (!attackerMob.isPlayer) {
            return item;
        }
        PlayerMob player = (PlayerMob)attackerMob;
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        if (level.isServer() && (entity = level.logicLayer.getEntity(tileX, tileY)) != null) {
            entity.openContainer(player.getServerClient());
        }
        return item;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        int tileY;
        int tileX = GameMath.getTileCoordinate(x);
        if (!level.isTileWithinBounds(tileX, tileY = GameMath.getTileCoordinate(y))) {
            return "outsidelevel";
        }
        if (level.isProtected(tileX, tileY)) {
            return "protected";
        }
        if (!this.isInPlaceRange(level, tileX * 32 + 16, tileY * 32 + 16, player, playerPositionLine, item)) {
            return "outofrange";
        }
        int ammoRequired = 0;
        if (mapContent != null) {
            for (int i = 0; i < 4; ++i) {
                if (!mapContent.getBoolean("editingWire" + i) || level.wireManager.hasWire(tileX, tileY, i)) continue;
                ++ammoRequired;
            }
        } else if (level.isClient()) {
            for (int i = 0; i < 4; ++i) {
                if (!WireEditSidebarForm.isEditing(i) || level.wireManager.hasWire(tileX, tileY, i)) continue;
                ++ammoRequired;
            }
        }
        if (ammoRequired == 0) {
            return "nowire";
        }
        if (ammoRequired > this.getAvailableAmmo(player)) {
            return "noammo";
        }
        return null;
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        for (int i = 0; i < 4; ++i) {
            map.setBoolean("editingWire" + i, WireEditSidebarForm.isEditing(i));
        }
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        ItemPlaceEvent event = new ItemPlaceEvent(level, tileX, tileY, player, item);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            int ammoUsed = 0;
            for (int i = 0; i < 4; ++i) {
                if (!mapContent.getBoolean("editingWire" + i) || level.wireManager.hasWire(tileX, tileY, i)) continue;
                level.wireManager.setWire(tileX, tileY, i, true);
                ++ammoUsed;
            }
            if (ammoUsed > 0) {
                if (level.isServer()) {
                    level.sendWireUpdatePacket(tileX, tileY);
                }
                player.getInv().main.removeItems(level, player, ItemRegistry.getItem("wire"), ammoUsed, "wireammo");
            }
        }
        return item;
    }

    @Override
    public float getAttackSpeedModifier(InventoryItem item, ItemAttackerMob attackerMob) {
        float superModifier = super.getAttackSpeedModifier(item, attackerMob);
        if (attackerMob != null && attackerMob.isPlayer && ((PlayerMob)attackerMob).hasGodMode()) {
            return superModifier;
        }
        return superModifier * (attackerMob == null ? 1.0f : attackerMob.buffManager.getModifier(BuffModifiers.BUILDING_SPEED).floatValue());
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.max(super.getAttackAnimTime(item, attackerMob), 50);
    }

    public int getAvailableAmmo(PlayerMob player) {
        if (player != null) {
            return player.getInv().main.getAmount(player.getLevel(), player, ItemRegistry.getItem("wire"), "wireammo");
        }
        return 0;
    }

    @Override
    public void draw(InventoryItem item, PlayerMob perspective, int x, int y, boolean inInventory) {
        super.draw(item, perspective, x, y, inInventory);
        if (inInventory) {
            int ammoAmount = this.getAvailableAmmo(perspective);
            if (ammoAmount > 999) {
                ammoAmount = 999;
            }
            String amountString = String.valueOf(ammoAmount);
            int width = FontManager.bit.getWidthCeil(amountString, tipFontOptions);
            FontManager.bit.drawString(x + 28 - width, y + 16, amountString, tipFontOptions);
        }
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "wrenchtip"));
        tooltips.add(Localization.translate("itemtooltip", "wrenchammo", "value", (Object)this.getAvailableAmmo(perspective)));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public boolean showWires() {
        return true;
    }

    @Override
    public SidebarForm getSidebar(InventoryItem item) {
        return new WireEditSidebarForm(item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int cooldown = this.getAttackCooldownTime(item, attackerMob);
        if (cooldown > 0) {
            return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        }
        if (!attackerMob.isPlayer) {
            return item;
        }
        attackerMob.startAttackHandler(new SimplePlaceableItemAttackHandler((PlayerMob)attackerMob, slot, x, y, seed, this, mapContent){

            @Override
            protected void onServerPlaceInvalid(InventoryItem item, PlaceItemAttackHandler.PlacePosition placePosition, Line2D playerPositionLine) {
                Level level = this.attackerMob.getLevel();
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                int tileX = GameMath.getTileCoordinate(placePosition.placeX);
                int tileY = GameMath.getTileCoordinate(placePosition.placeY);
                client.sendPacket(new PacketChangeWire(level, tileX, tileY, level.wireManager.getWireData(tileX, tileY)));
            }
        });
        return slot.getItem();
    }

    @Override
    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        if (this.canPlace(level, x, y, player, null, item, null) == null) {
            for (int i = 0; i < 4; ++i) {
                if (!WireEditSidebarForm.isEditing(i) || level.wireManager.hasWire(tileX, tileY, i)) continue;
                level.wireManager.drawWirePreset(tileX, tileY, camera, i);
            }
        }
    }

    @Override
    public void onMouseHoverTile(InventoryItem item, GameCamera camera, PlayerMob perspective, int mouseX, int mouseY, TilePosition pos, boolean isDebug) {
        super.onMouseHoverTile(item, camera, perspective, mouseX, mouseY, pos, isDebug);
        String interactError = this.canInteractError(pos.level, mouseX, mouseY, perspective, item);
        if (interactError == null) {
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
            GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "edittip")), TooltipLocation.INTERACT_FOCUS);
        } else if (interactError.equals("outofrange")) {
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
            GameTooltipManager.addTooltip(new InputTooltip(Control.MOUSE2, Localization.translate("controls", "edittip"), 0.5f), TooltipLocation.INTERACT_FOCUS);
        }
    }
}

