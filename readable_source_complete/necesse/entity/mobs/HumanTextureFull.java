/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.entity.mobs.HumanTexture;
import necesse.gfx.gameTexture.GameTexture;

public class HumanTextureFull
extends HumanTexture {
    public final GameTexture head;
    public final GameTexture eyelids;
    public final GameTexture hair;
    public final GameTexture feet;
    public final GameTexture frontHead;
    public final GameTexture backHair;
    public final GameTexture frontFeet;

    public HumanTextureFull(GameTexture head, GameTexture frontHead, GameTexture eyelids, GameTexture hair, GameTexture backHair, GameTexture body, GameTexture frontBody, GameTexture leftArms, GameTexture frontLeftArms, GameTexture rightArms, GameTexture frontRightArms, GameTexture feet, GameTexture frontFeet) {
        super(body, leftArms, rightArms, frontBody, frontLeftArms, frontRightArms);
        this.head = head;
        this.frontHead = frontHead;
        this.eyelids = eyelids;
        this.hair = hair;
        this.backHair = backHair;
        this.feet = feet;
        this.frontFeet = frontFeet;
    }
}

