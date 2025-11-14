/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.jobs.LevelJob;

public abstract class PlaceObjectLevelJob
extends LevelJob {
    public final int objectID;
    public final int objectRotation;

    public PlaceObjectLevelJob(int tileX, int tileY, int objectID, int rotation) {
        super(tileX, tileY);
        this.objectID = objectID;
        this.objectRotation = rotation;
    }

    public PlaceObjectLevelJob(int tileX, int tileY, int objectID) {
        this(tileX, tileY, objectID, 0);
    }

    public PlaceObjectLevelJob(LoadData save) {
        super(save);
        String stringID = save.getUnsafeString("objectStringID");
        this.objectID = ObjectRegistry.getObjectID(stringID);
        if (this.objectID == -1) {
            throw new LoadDataException("Could not find object with stringID \"" + stringID + "\"");
        }
        this.objectRotation = save.getInt("objectRotation");
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addUnsafeString("objectStringID", this.getObject().getStringID());
        save.addInt("objectRotation", this.objectRotation);
    }

    @Override
    public boolean isValid() {
        GameObject object = this.getObject();
        return object.canPlace(this.getLevel(), this.tileX, this.tileY, this.objectRotation, true) == null;
    }

    public GameObject getObject() {
        return ObjectRegistry.getObject(this.objectID);
    }

    public boolean canPlaceCollision() {
        return !this.getObject().checkPlaceCollision(this.getLevel(), this.tileX, this.tileY, this.objectRotation, true);
    }
}

