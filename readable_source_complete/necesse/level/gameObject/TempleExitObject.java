/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.entity.objectEntity.TempleExitObjectEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class TempleExitObject
extends StaticMultiObject {
    protected TempleExitObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "templeexit");
        this.mapColor = new Color(122, 102, 60);
        this.displayMapTooltip = true;
        this.lightLevel = 100;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
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
        ObjectEntity objectEntity;
        LevelObject master;
        if (level.isServer() && player.isServerClient() && (master = (LevelObject)this.getMultiTile(level, 0, x, y).getMasterLevelObject(level, 0, x, y).orElse(null)) != null && (objectEntity = level.entityManager.getObjectEntity(master.tileX, master.tileY)) instanceof PortalObjectEntity) {
            ((PortalObjectEntity)objectEntity).use(level.getServer(), player.getServerClient());
        }
        super.interact(level, x, y, player);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (this.isMultiTileMaster()) {
            return new TempleExitObjectEntity(level, x, y);
        }
        return super.getNewObjectEntity(level, x, y);
    }

    public static int[] registerTempleExit() {
        int[] ids = new int[6];
        Rectangle collision = new Rectangle(8, 8, 82, 56);
        ids[0] = ObjectRegistry.registerObject("templeexit", new TempleExitObject(0, 0, 3, 2, ids, collision), 0.0f, false);
        ids[1] = ObjectRegistry.registerObject("templeexit2", new TempleExitObject(1, 0, 3, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("templeexit3", new TempleExitObject(2, 0, 3, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("templeexit4", new TempleExitObject(0, 1, 3, 2, ids, collision), 0.0f, false);
        ids[4] = ObjectRegistry.registerObject("templeexit5", new TempleExitObject(1, 1, 3, 2, ids, collision), 0.0f, false);
        ids[5] = ObjectRegistry.registerObject("templeexit6", new TempleExitObject(2, 1, 3, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

