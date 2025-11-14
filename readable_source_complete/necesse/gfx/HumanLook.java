/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import java.util.List;
import java.util.function.Function;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.gfx.GameEyes;
import necesse.gfx.GameHair;
import necesse.gfx.GameSkin;
import necesse.gfx.GameSkinLoader;
import necesse.gfx.HumanGender;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.light.GameLight;

public class HumanLook {
    private byte hair;
    private byte facialFeature;
    private byte hairColor;
    private byte skin;
    private byte eyeColor;
    private byte eyeType;
    private Color shirtColor;
    private Color shoesColor;

    public HumanLook() {
        this.resetDefault();
    }

    public HumanLook(GameRandom random, boolean onlyHumanLike) {
        this.randomizeLook(random, onlyHumanLike);
    }

    public HumanLook(int hair, int facialFeature, int hairColor, int skin, int eyeColor, int eyeType, Color shirtColor, Color shoesColor) {
        this.setHair(hair);
        this.setFacialFeature(facialFeature);
        this.setHairColor(hairColor);
        this.setSkin(skin);
        this.setEyeColor(eyeColor);
        this.setShirtColor(shirtColor);
        this.setShoesColor(shoesColor);
        this.setEyeType(eyeType);
    }

    public HumanLook(HumanLook copy) {
        this.copy(copy);
    }

    public HumanLook(PacketReader pr) {
        this.resetDefault();
        this.applyContentPacket(pr);
    }

    public void copy(HumanLook look) {
        this.hair = look.hair;
        this.facialFeature = look.facialFeature;
        this.hairColor = look.hairColor;
        this.skin = look.skin;
        this.eyeColor = look.eyeColor;
        this.eyeType = look.eyeType;
        this.shirtColor = new Color(look.shirtColor.getRGB());
        this.shoesColor = new Color(look.shoesColor.getRGB());
    }

    public void resetDefault() {
        this.hair = 1;
        this.facialFeature = 0;
        this.hairColor = 0;
        this.skin = 0;
        this.eyeColor = 0;
        this.eyeType = 0;
        this.shirtColor = new Color(110, 110, 200);
        this.shoesColor = new Color(110, 110, 200);
    }

    public void randomizeLook(boolean onlyHumanLike) {
        this.randomizeLook(GameRandom.globalRandom, onlyHumanLike);
    }

    public void randomizeLook(boolean onlyHumanLike, boolean randomFacialFeature, boolean randomSkin, boolean changeEyeType, boolean randomEyeColor) {
        HumanGender gender = GameRandom.globalRandom.getOneOf(HumanGender.MALE, HumanGender.FEMALE, HumanGender.NEUTRAL);
        this.randomizeLook(GameRandom.globalRandom, onlyHumanLike, gender, randomSkin, changeEyeType, randomEyeColor, randomFacialFeature);
    }

    public void randomizeLook(GameRandom random, boolean onlyHumanLike) {
        HumanGender gender = random.getOneOf(HumanGender.MALE, HumanGender.FEMALE, HumanGender.NEUTRAL);
        this.randomizeLook(random, onlyHumanLike, gender, true, true, true, true);
    }

    public void randomizeLook(GameRandom random, boolean onlyHumanLike, HumanGender gender, boolean randomSkin, boolean changeEyeType, boolean randomEyeColor, boolean randomFacialFeature) {
        if (changeEyeType) {
            this.setEyeType(GameEyes.getRandomEyesBasedOnGender(random, gender));
        }
        this.setHair(this.getRandomHairStyleBasedOnGender(random, gender));
        if (gender == HumanGender.MALE) {
            if (randomFacialFeature) {
                this.setFacialFeature(GameHair.getRandomFacialFeature(random));
            }
        } else {
            this.setFacialFeature(0);
        }
        this.setHairColor(GameHair.getRandomHairColor(random));
        if (randomSkin) {
            this.setSkin(GameSkin.getRandomSkinColor(random, onlyHumanLike));
        }
        if (randomEyeColor) {
            this.setEyeColor(GameEyes.getRandomEyeColor(random));
        }
        this.shirtColor = new Color(random.getIntBetween(50, 200), random.getIntBetween(50, 200), random.getIntBetween(50, 200));
        this.shoesColor = new Color(random.getIntBetween(50, 200), random.getIntBetween(50, 200), random.getIntBetween(50, 200));
    }

    public int getRandomHairStyleBasedOnGender(GameRandom random, HumanGender gender) {
        switch (gender) {
            case MALE: {
                boolean chanceForMaleHair = random.getChance(0.7f);
                if (chanceForMaleHair) {
                    return GameHair.getRandomHairBasedOnGender(random, HumanGender.MALE);
                }
                return GameHair.getRandomHairBasedOnGender(random, HumanGender.NEUTRAL);
            }
            case FEMALE: {
                boolean chanceForFemaleHair = random.getChance(0.7f);
                if (chanceForFemaleHair) {
                    return GameHair.getRandomHairBasedOnGender(random, HumanGender.FEMALE);
                }
                return GameHair.getRandomHairBasedOnGender(random, HumanGender.NEUTRAL);
            }
        }
        return GameHair.getRandomHairBasedOnGender(random, HumanGender.NEUTRAL);
    }

    public void setupContentPacket(PacketWriter writer, boolean includeClothesColor) {
        writer.putNextBoolean(includeClothesColor);
        writer.putNextByte(this.hair);
        writer.putNextByte(this.facialFeature);
        writer.putNextByte(this.hairColor);
        writer.putNextByte(this.skin);
        writer.putNextByte(this.eyeType);
        writer.putNextByte(this.eyeColor);
        if (includeClothesColor) {
            writer.putNextByteUnsigned(this.shirtColor.getRed());
            writer.putNextByteUnsigned(this.shirtColor.getGreen());
            writer.putNextByteUnsigned(this.shirtColor.getBlue());
            writer.putNextByteUnsigned(this.shoesColor.getRed());
            writer.putNextByteUnsigned(this.shoesColor.getGreen());
            writer.putNextByteUnsigned(this.shoesColor.getBlue());
        }
    }

    public HumanLook applyContentPacket(PacketReader reader) {
        boolean includesClothesColor = reader.getNextBoolean();
        this.hair = reader.getNextByte();
        this.facialFeature = reader.getNextByte();
        this.hairColor = reader.getNextByte();
        this.skin = reader.getNextByte();
        this.eyeType = reader.getNextByte();
        this.eyeColor = reader.getNextByte();
        if (includesClothesColor) {
            this.shirtColor = new Color(reader.getNextByteUnsigned(), reader.getNextByteUnsigned(), reader.getNextByteUnsigned());
            this.shoesColor = new Color(reader.getNextByteUnsigned(), reader.getNextByteUnsigned(), reader.getNextByteUnsigned());
        }
        return this;
    }

    public void addSaveData(SaveData save) {
        save.addColor("shirtColor", this.getShirtColor());
        save.addColor("shoesColor", this.getShoesColor());
        save.addInt("skin", this.getSkin());
        save.addInt("hair", this.getHair());
        save.addInt("facialFeature", this.getFacialFeature());
        save.addInt("hairColor", this.getHairColor());
        save.addInt("eyeType", this.getEyeType());
        save.addInt("eyeColor", this.getEyeColor());
    }

    public void applyLoadData(LoadData save) {
        this.setShirtColor(save.getColor("shirtColor", this.getShirtColor()));
        if (save.hasLoadDataByName("bootsColor")) {
            this.setShoesColor(save.getColor("bootsColor", this.getShoesColor()));
        } else {
            this.setShoesColor(save.getColor("shoesColor", this.getShoesColor()));
        }
        this.setSkin(save.getInt("skin", this.getSkin()));
        this.setHair(save.getInt("hair", this.getHair()));
        this.setFacialFeature(save.getInt("facialFeature", this.getFacialFeature(), false));
        this.setHairColor(save.getInt("hairColor", this.getHairColor()));
        this.setEyeType(save.getInt("eyeType", this.getEyeType(), false));
        this.setEyeColor(save.getInt("eyeColor", this.getEyeColor()));
    }

    public GameEyes getEyes() {
        return GameEyes.getEyes(this.eyeType);
    }

    public static <T> List<T> getClosedEyesDrawOptions(int eyeType, int eyeColor, int skinColor, boolean humanlikeOnly, Function<GameTexture, T> mapper) {
        return GameEyes.getEyes(eyeType).getClosedColorTextures(eyeColor, skinColor, humanlikeOnly, mapper);
    }

    public static <T> List<T> getOpenEyesDrawOptions(int eyeType, int eyeColor, int skinColor, boolean humanlikeOnly, Function<GameTexture, T> mapper) {
        return GameEyes.getEyes(eyeType).getOpenColorTextures(eyeColor, skinColor, humanlikeOnly, mapper);
    }

    public static DrawOptions getEyesDrawOptions(int eyeType, int eyeColor, int skinColor, boolean humanlikeOnly, boolean closed, int drawX, int drawY, int spriteX, int spriteY, int width, int height, boolean mirrorX, boolean mirrorY, float alpha, GameLight light, MaskShaderOptions mask) {
        Function<GameTexture, DrawOptions> mapper = texture -> texture.initDraw().sprite(spriteX, spriteY, 64).light(light).alpha(alpha).size(width, height).mirror(mirrorX, mirrorY).addMaskShader(mask).pos(drawX, drawY);
        if (closed) {
            return new DrawOptionsList(HumanLook.getClosedEyesDrawOptions(eyeType, eyeColor, skinColor, humanlikeOnly, mapper));
        }
        return new DrawOptionsList(HumanLook.getOpenEyesDrawOptions(eyeType, eyeColor, skinColor, humanlikeOnly, mapper));
    }

    public DrawOptions getEyesDrawOptions(boolean humanlikeOnly, boolean closed, int drawX, int drawY, int spriteX, int spriteY, int width, int height, boolean mirrorX, boolean mirrorY, float alpha, GameLight light, MaskShaderOptions mask) {
        return HumanLook.getEyesDrawOptions(this.getEyeType(), this.getEyeColor(), this.getSkin(), humanlikeOnly, closed, drawX, drawY, spriteX, spriteY, width, height, mirrorX, mirrorY, alpha, light, mask);
    }

    public void setHair(int hair) {
        this.hair = (byte)hair;
    }

    public void setFacialFeature(int hair) {
        this.facialFeature = (byte)hair;
    }

    public void setHairColor(int hairColor) {
        this.hairColor = (byte)hairColor;
    }

    public void setSkin(int skin) {
        this.skin = (byte)skin;
    }

    public void setEyeType(int eyeType) {
        this.eyeType = (byte)eyeType;
    }

    public void setEyeColor(int eyeColor) {
        this.eyeColor = (byte)eyeColor;
    }

    public void setShirtColor(Color shirtColor) {
        this.shirtColor = shirtColor;
    }

    public void setShoesColor(Color shoesColor) {
        this.shoesColor = shoesColor;
    }

    public int getHair() {
        return this.hair & 0xFF;
    }

    public int getFacialFeature() {
        return this.facialFeature & 0xFF;
    }

    public int getHairColor() {
        return this.hairColor & 0xFF;
    }

    public int getSkin() {
        return this.skin & 0xFF;
    }

    public int getEyeType() {
        return this.eyeType & 0xFF;
    }

    public int getEyeColor() {
        return this.eyeColor & 0xFF;
    }

    public Color getShirtColor() {
        return this.shirtColor;
    }

    public Color getShoesColor() {
        return this.shoesColor;
    }

    public GameSkin getGameSkin(boolean onlyHumanlike) {
        return GameSkin.getSkin(this.getSkin(), onlyHumanlike);
    }

    public GameTexture getHairTexture() {
        return GameHair.getHair(this.getHair()).getHairTexture(this.getHairColor());
    }

    public GameTexture getBackHairTexture() {
        return GameHair.getHair(this.getHair()).getBackHairTexture(this.getHairColor());
    }

    public GameTexture getWigTexture() {
        return GameHair.getHair(this.getHair()).getWigTexture(this.getHairColor());
    }

    public GameTexture getFacialFeatureTexture() {
        return GameHair.getFacialFeature(this.getFacialFeature()).getHairTexture(this.getHairColor());
    }

    public GameTexture getBackFacialFeatureTexture() {
        return GameHair.getFacialFeature(this.getFacialFeature()).getBackHairTexture(this.getHairColor());
    }

    public static void loadTextures() {
        GameSkinLoader loader = new GameSkinLoader();
        loader.startLoaderThreads();
        try {
            GameSkin.loadSkinTextures(loader);
            GameHair.loadHairTextures(loader);
            GameEyes.loadEyeTextures(loader);
        }
        finally {
            loader.endLoaderThreads();
        }
    }

    public static Color limitClothesColor(Color color) {
        return new Color(GameMath.limit(color.getRed(), 25, 225), GameMath.limit(color.getGreen(), 25, 225), GameMath.limit(color.getBlue(), 25, 225));
    }
}

