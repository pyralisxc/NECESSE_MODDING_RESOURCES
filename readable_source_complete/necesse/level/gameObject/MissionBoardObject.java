/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.missionBoard.MissionBoardContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.MissionBoard2Object;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

public class MissionBoardObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected int counterID;

    protected MissionBoardObject() {
        super(new Rectangle(32, 32));
        this.displayMapTooltip = true;
        this.mapColor = new Color(132, 91, 25);
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        this.rarity = Item.Rarity.RARE;
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/missionboard");
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public void tick(Level level, int x, int y) {
        ServerSettlementData serverData;
        super.tick(level, x, y);
        if (level.isServer() && (serverData = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), x, y)) != null) {
            serverData.setMissionBoardTile(new Point(x, y));
        }
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        Point offset = offsetMultiTile ? this.getPlaceOffset(1) : null;
        int tileX = GameMath.getTileCoordinate(levelX + (offset == null ? 0 : offset.x));
        int tileY = GameMath.getTileCoordinate(levelY + (offset == null ? 0 : offset.y));
        return new ArrayList<ObjectPlaceOption>(Collections.singleton(new ObjectPlaceOption(tileX, tileY, this, 1, false)));
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 4, y * 32 + 4, 24, 28);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(0, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        TextureDrawOptionsEnd options = texture.initDraw().sprite(0, 0, 32, texture.getHeight()).alpha(alpha).pos(drawX, drawY - texture.getHeight() + 32);
        options.draw();
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(new LocalMessage("ui", "missionboardtip"), 400);
        return tooltips;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            ServerClient serverClient = player.getServerClient();
            ServerSettlementData serverData = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), x, y);
            if (serverData != null) {
                ContainerRegistry.openAndSendContainer(serverClient, PacketOpenContainer.Settlement(ContainerRegistry.MISSION_BOARD_CONTAINER, serverData, MissionBoardContainer.getContainerContent(serverData)));
            } else {
                serverClient.sendChatMessage(new LocalMessage("ui", "settlementcouldnotfind"));
            }
        }
    }

    public static int[] registerMissionBoard() {
        int i2;
        MissionBoardObject o1 = new MissionBoardObject();
        MissionBoard2Object o2 = new MissionBoard2Object();
        int i1 = ObjectRegistry.registerObject("missionboard", o1, 500.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("missionboard2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }
}

