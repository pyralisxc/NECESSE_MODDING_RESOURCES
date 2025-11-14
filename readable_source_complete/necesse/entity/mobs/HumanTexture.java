/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.gfx.gameTexture.GameTexture;

public class HumanTexture {
    public final GameTexture body;
    public final GameTexture leftArms;
    public final GameTexture rightArms;
    public final GameTexture frontBody;
    public final GameTexture frontLeftArms;
    public final GameTexture frontRightArms;

    public HumanTexture(GameTexture body, GameTexture leftArms, GameTexture rightArms) {
        this.body = body;
        this.leftArms = leftArms;
        this.rightArms = rightArms;
        this.frontBody = null;
        this.frontLeftArms = null;
        this.frontRightArms = null;
    }

    public HumanTexture(GameTexture body, GameTexture leftArms, GameTexture rightArms, GameTexture frontBody, GameTexture frontLeftArms, GameTexture frontRightArms) {
        this.body = body;
        this.leftArms = leftArms;
        this.rightArms = rightArms;
        this.frontBody = frontBody;
        this.frontLeftArms = frontLeftArms;
        this.frontRightArms = frontRightArms;
    }
}

