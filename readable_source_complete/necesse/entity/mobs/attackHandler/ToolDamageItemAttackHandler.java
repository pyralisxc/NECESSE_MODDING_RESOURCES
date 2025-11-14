/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Line2D;
import necesse.engine.Settings;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.TileDamageOption;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.level.maps.Level;

public class ToolDamageItemAttackHandler
extends PlaceItemAttackHandler<ItemDamagePosition> {
    public final ToolDamageItem toolDamageItem;
    private boolean isMobHitExclusive = false;
    private int animAttack = 0;

    public ToolDamageItemAttackHandler(PlayerMob player, ItemAttackSlot slot, int startLevelX, int startLevelY, int seed, ToolDamageItem toolDamageItem, GNDItemMap attackContentMap) {
        super(player, slot, startLevelX, startLevelY, seed);
        this.toolDamageItem = toolDamageItem;
        if (attackContentMap != null) {
            this.runInitialPlace(attackContentMap);
        }
    }

    @Override
    protected ItemDamagePosition placeItem(InventoryItem item, int currentX, int currentY, Line2D playerPositionLine, GNDItemMap mapContent) {
        Mob damagedMob;
        Level level = this.attackerMob.getLevel();
        PlayerMob player = (PlayerMob)this.attackerMob;
        TileDamageOption tileDamageOption = null;
        if (mapContent == null) {
            mapContent = new GNDItemMap();
            damagedMob = this.toolDamageItem.getMobHitOption(level, currentX, currentY, player, playerPositionLine, item);
            if (damagedMob != null) {
                this.toolDamageItem.setupAttackMapContentHitMob(mapContent, level, damagedMob);
            } else if (!this.isMobHitExclusive) {
                ToolDamageItem.SmartMineTarget hitTile;
                if ((Settings.smartMining || Input.lastInputIsController && !ControllerInput.isCursorVisible()) && (hitTile = this.toolDamageItem.getFirstSmartHitTile(player.getLevel(), player, item, currentX, currentY)) != null) {
                    tileDamageOption = hitTile.tileDamageOption;
                    currentX = hitTile.x * 32 + 16;
                    currentY = hitTile.y * 32 + 16;
                }
                if (tileDamageOption == null && (tileDamageOption = this.toolDamageItem.getTileDamageOption(level, currentX, currentY, player, playerPositionLine, item, this.getPlaceCooldown() > 0)) == null) {
                    tileDamageOption = new TileDamageOption(-1, GameMath.getTileCoordinate(currentX), GameMath.getTileCoordinate(currentY));
                }
                this.toolDamageItem.setupAttackMapContentHitTile(mapContent, level, tileDamageOption);
            }
        } else {
            damagedMob = this.toolDamageItem.getMobHitFromMap(mapContent, level);
            if (damagedMob != null) {
                this.isMobHitExclusive = true;
            } else {
                tileDamageOption = this.toolDamageItem.getTileDamageOptionFromMap(mapContent, level);
            }
        }
        if ((damagedMob != null || tileDamageOption != null) && this.toolDamageItem.canAttack(level, currentX, currentY, player, item) == null) {
            int animAttack;
            InventoryItem newItem;
            if ((newItem = this.toolDamageItem.runTileAttack(level, currentX, currentY, player, playerPositionLine, item, animAttack = this.animAttack++, mapContent)).getAmount() <= 0) {
                this.slot.setItem(null);
            } else {
                this.slot.setItem(newItem);
            }
            return new ItemDamagePosition(currentX, currentY, mapContent, animAttack);
        }
        return null;
    }

    @Override
    protected boolean placeServerItem(InventoryItem item, ItemDamagePosition placePosition, Line2D playerPositionLine) {
        PlayerMob player;
        Level level = this.attackerMob.getLevel();
        if (this.toolDamageItem.canAttack(level, placePosition.placeX, placePosition.placeY, player = (PlayerMob)this.attackerMob, item) == null) {
            InventoryItem newItem = this.toolDamageItem.runTileAttack(level, placePosition.placeX, placePosition.placeY, player, playerPositionLine, item, placePosition.animAttack, placePosition.attackMapContent);
            if (newItem.getAmount() <= 0) {
                this.slot.setItem(null);
            } else {
                this.slot.setItem(newItem);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onServerPlaceInvalid(InventoryItem item, ItemDamagePosition placePosition, Line2D playerPositionLine) {
        PlayerMob player = (PlayerMob)this.attackerMob;
        Level level = player.getLevel();
        ServerClient client = player.getServerClient();
        TileDamageOption tileDamageOption = this.toolDamageItem.getTileDamageOptionFromMap(placePosition.attackMapContent, level);
        if (tileDamageOption != null) {
            level.checkTileAndObjectsHashGNDMap(client, placePosition.attackMapContent, tileDamageOption.tileX, tileDamageOption.tileY, false);
        }
    }

    @Override
    protected ItemDamagePosition createPlacePositionFromPacket(PacketReader reader) {
        return new ItemDamagePosition(reader);
    }

    @Override
    protected int getPlaceCooldown() {
        InventoryItem item = this.slot.getItem();
        if (item != null) {
            return this.toolDamageItem.getAttackHandlerDamageCooldown(item, this.attackerMob);
        }
        return 200;
    }

    @Override
    protected void showAttackAndSendAttacker(int targetX, int targetY, InventoryItem item) {
        super.showAttackAndSendAttacker(targetX, targetY, item);
        this.toolDamageItem.startToolItemEventAbilityEvent(this.attackerMob.getLevel(), targetX, targetY, this.attackerMob, this.attackerMob.getCurrentAttackHeight(), item, this.seed);
    }

    @Override
    public void drawHUDItemSelected(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
        super.drawHUDItemSelected(camera, level, player, item);
        if (this.isMobHitExclusive) {
            GameTooltipManager.addTooltip(new StringTooltips(Localization.translate("itemtooltip", "tooltargetingmobs")), TooltipLocation.INTERACT_FOCUS);
        }
    }

    protected static class ItemDamagePosition
    extends PlaceItemAttackHandler.PlacePosition {
        public final int animAttack;

        public ItemDamagePosition(int placeX, int placeY, GNDItemMap attackMapContent, int animAttack) {
            super(placeX, placeY, attackMapContent);
            this.animAttack = animAttack;
        }

        public ItemDamagePosition(PacketReader reader) {
            super(reader);
            this.animAttack = reader.getNextInt();
        }

        @Override
        public void writePacket(PacketWriter writer) {
            super.writePacket(writer);
            writer.putNextInt(this.animAttack);
        }
    }
}

