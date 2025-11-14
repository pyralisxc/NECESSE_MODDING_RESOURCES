/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture.doubleBed;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.gameObject.furniture.SettlerBedObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBed1FootObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBed1HeadObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBed2FootObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBed2HeadObject;

public abstract class DoubleBedBaseObject
extends FurnitureObject
implements SettlerBedObject {
    public final String textureName;
    public ObjectDamagedTextureArray baseTexture;

    public DoubleBedBaseObject(String textureName, ToolType toolType, Color mapColor) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.roomProperties.add("bed");
        this.furnitureType = "bed";
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.baseTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return null;
    }

    public static int[] registerDoubleBed(String stringID, String textureName, ToolType toolType, Color mapColor, float brokerValue, String ... category) {
        DoubleBed1HeadObject head1 = new DoubleBed1HeadObject(textureName, toolType, mapColor, category);
        DoubleBed1FootObject foot1 = new DoubleBed1FootObject(textureName, toolType, mapColor, category);
        DoubleBed2HeadObject head2 = new DoubleBed2HeadObject(textureName, toolType, mapColor, category);
        DoubleBed2FootObject foot2 = new DoubleBed2FootObject(textureName, toolType, mapColor, category);
        int head1ID = ObjectRegistry.registerObject(stringID, head1, brokerValue, true);
        int foot1ID = ObjectRegistry.registerObject(stringID + "foot1", foot1, 0.0f, false);
        int head2ID = ObjectRegistry.registerObject(stringID + "2", head2, 0.0f, false);
        int foot2ID = ObjectRegistry.registerObject(stringID + "foot2", foot2, 0.0f, false);
        head1.foot1ID = foot1ID;
        head1.head2ID = head2ID;
        head1.foot2ID = foot2ID;
        foot1.head1ID = head1ID;
        foot1.head2ID = head2ID;
        foot1.foot2ID = foot2ID;
        head2.head1ID = head1ID;
        head2.foot1ID = foot1ID;
        head2.foot2ID = foot2ID;
        foot2.head1ID = head1ID;
        foot2.foot1ID = foot1ID;
        foot2.head2ID = head2ID;
        return new int[]{head1ID, foot1ID, head2ID, head2ID};
    }

    public static int[] registerDoubleBed(String stringID, String textureName, Color mapColor, float brokerValue, String ... category) {
        return DoubleBedBaseObject.registerDoubleBed(stringID, textureName, ToolType.ALL, mapColor, brokerValue, category);
    }
}

