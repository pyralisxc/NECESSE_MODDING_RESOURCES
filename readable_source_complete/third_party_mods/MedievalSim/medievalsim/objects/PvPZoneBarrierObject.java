/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.Packet
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.gameTooltips.GameTooltipManager
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.gfx.gameTooltips.TooltipLocation
 *  necesse.inventory.item.toolItem.ToolType
 *  necesse.level.gameObject.GameObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.regionSystem.RegionType
 */
package medievalsim.objects;

import java.awt.Rectangle;
import medievalsim.packets.PacketPvPZoneEntryDialog;
import medievalsim.packets.PacketPvPZoneExitDialog;
import medievalsim.zones.AdminZonesLevelData;
import medievalsim.zones.PvPZone;
import medievalsim.zones.PvPZoneTracker;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionType;

public class PvPZoneBarrierObject
extends GameObject {
    public PvPZoneBarrierObject() {
        super(new Rectangle(0, 0, 32, 32));
        this.isSolid = true;
        this.toolType = ToolType.UNBREAKABLE;
        this.regionType = RegionType.SOLID;
        this.mapColor = null;
        this.isLightTransparent = true;
        this.drawDamage = false;
        this.stackSize = 0;
    }

    public GameMessage preventsLadderPlacement(Level level, int tileX, int tileY) {
        return new StaticMessage("PVP Zone Boundary");
    }

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData != null) {
            for (PvPZone zone : zoneData.getPvPZones().values()) {
                int playerTileY;
                if (!this.isBarrierForZone(zone, x, y)) continue;
                int playerTileX = GameMath.getTileCoordinate((int)((int)perspective.x));
                boolean playerInZone = zone.containsTile(playerTileX, playerTileY = GameMath.getTileCoordinate((int)((int)perspective.y)));
                return playerInZone ? "Exit PVP Zone" : "Enter PVP Zone";
            }
        }
        return "PVP Zone Boundary";
    }

    public void onMouseHover(Level level, int x, int y, GameCamera camera, PlayerMob perspective, boolean debug) {
        AdminZonesLevelData zoneData;
        super.onMouseHover(level, x, y, camera, perspective, debug);
        if (perspective != null && level != null && (zoneData = AdminZonesLevelData.getZoneData(level, false)) != null) {
            for (PvPZone zone : zoneData.getPvPZones().values()) {
                if (zone == null || zone.zoning == null || !this.isBarrierForZone(zone, x, y)) continue;
                StringTooltips tooltips = new StringTooltips();
                tooltips.add("\u00a7cPVP Zone Boundary");
                tooltips.add("\u00a77Zone: \u00a7f" + zone.name);
                if (zone.damageMultiplier != 1.0f) {
                    tooltips.add("\u00a77Damage: \u00a7f" + (int)(zone.damageMultiplier * 100.0f) + "%");
                }
                if (zone.combatLockSeconds > 0) {
                    tooltips.add("\u00a77Combat Lock: \u00a7f" + zone.combatLockSeconds + "s");
                }
                GameTooltipManager.addTooltip((GameTooltips)tooltips, (TooltipLocation)TooltipLocation.INTERACT_FOCUS);
                break;
            }
        }
    }

    public void interact(Level level, int x, int y, PlayerMob player) {
        if (!level.isServer()) {
            return;
        }
        AdminZonesLevelData zoneData = AdminZonesLevelData.getZoneData(level, false);
        if (zoneData == null) {
            return;
        }
        ServerClient client = player.getServerClient();
        if (client == null) {
            return;
        }
        long serverTime = level.getServer().world.worldEntity.getTime();
        for (PvPZone zone : zoneData.getPvPZones().values()) {
            int playerTileY;
            if (zone == null || zone.zoning == null || !this.isBarrierForZone(zone, x, y)) continue;
            int playerTileX = GameMath.getTileCoordinate((int)((int)player.x));
            boolean playerInZone = zone.containsTile(playerTileX, playerTileY = GameMath.getTileCoordinate((int)((int)player.y)));
            if (playerInZone) {
                int remainingSeconds = 0;
                if (PvPZoneTracker.isInCombat(client, level, serverTime)) {
                    remainingSeconds = PvPZoneTracker.getRemainingCombatLockSeconds(client, level, serverTime);
                }
                level.getServer().network.sendPacket((Packet)new PacketPvPZoneExitDialog(zone.uniqueID, zone.name, remainingSeconds), client);
                break;
            }
            level.getServer().network.sendPacket((Packet)new PacketPvPZoneEntryDialog(zone.uniqueID, zone.name, zone.damageMultiplier, zone.combatLockSeconds), client);
            break;
        }
    }

    private boolean isBarrierForZone(PvPZone zone, int x, int y) {
        return zone.zoning.isEdgeTile(x, y);
    }
}

