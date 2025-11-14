/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.stb.STBImageWrite
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.Platform
 */
package necesse.engine.util;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.IntersectionPoint;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelObjectHit;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;

public final class GameUtils {
    public static final MetricValue[] normalMetricSystem = new MetricValue[]{new MetricValue("G", 9), new MetricValue("M", 6), new MetricValue("k", 3), new MetricValue("", 0), new MetricValue("m", -3), new MetricValue("u", -6), new MetricValue("n", -9)};
    public static Pattern playerNameSymbolsPattern = Pattern.compile("[\\p{L}\\p{N} ]+");
    public static final String[] invalidFileNameSequences = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "\u0000"};
    public static final Pattern validFileNamePattern = Pattern.compile("[^" + Stream.of(invalidFileNameSequences).map(c -> "\\" + c).reduce("", (s1, s2) -> s1 + s2) + "]+");

    private GameUtils() {
        throw new IllegalStateException("GameUtils cannot be instantiated");
    }

    public static boolean deleteFileOrFolder(String path) {
        return GameUtils.deleteFileOrFolder(new File(path));
    }

    public static boolean deleteFileOrFolder(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File other : files) {
                    if (GameUtils.deleteFileOrFolder(other.getPath())) continue;
                    return false;
                }
            }
            return file.delete();
        }
        return file.delete();
    }

    private static void _copyFileOrFolder(File file, File target, CopyOption ... options) throws IOException {
        File[] files;
        if (!file.isDirectory() || !target.isDirectory()) {
            Files.copy(file.toPath(), target.toPath(), options);
        }
        if (file.isDirectory() && (files = file.listFiles()) != null) {
            for (File other : files) {
                GameUtils._copyFileOrFolder(other, GameUtils.resolveFile(target, other.getName()), options);
            }
        }
    }

    public static void copyFileOrFolder(File file, File target, CopyOption ... options) throws IOException {
        File absoluteDir;
        File absoluteParent;
        if (file.isFile() ? (absoluteParent = target.getAbsoluteFile().getParentFile()) != null && !absoluteParent.exists() && !absoluteParent.mkdirs() : !(absoluteDir = target.getAbsoluteFile()).exists() && !absoluteDir.mkdirs()) {
            throw new IOException("Could not make target directories for move");
        }
        GameUtils._copyFileOrFolder(file, target, options);
    }

    public static void copyFileOrFolderReplaceExisting(File file, File target) throws IOException {
        GameUtils.copyFileOrFolder(file, target, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void _moveFileOrFolder(File file, File target, CopyOption ... options) throws IOException {
        File[] files;
        if (!file.isDirectory() || !target.isDirectory()) {
            Files.move(file.toPath(), target.toPath(), options);
        }
        if (file.isDirectory() && (files = file.listFiles()) != null) {
            for (File other : files) {
                GameUtils._moveFileOrFolder(other, GameUtils.resolveFile(target, other.getName()), options);
            }
        }
    }

    public static void moveFileOrFolder(File file, File target, CopyOption ... options) throws IOException {
        File absoluteDir;
        File absoluteParent;
        if (file.isFile() ? (absoluteParent = target.getAbsoluteFile().getParentFile()) != null && !absoluteParent.exists() && !absoluteParent.mkdirs() : !(absoluteDir = target.getAbsoluteFile()).exists() && !absoluteDir.mkdirs()) {
            throw new IOException("Could not make target directories for move");
        }
        GameUtils._moveFileOrFolder(file, target, options);
    }

    public static void moveFileOrFolderReplaceExisting(File file, File target) throws IOException {
        GameUtils.moveFileOrFolder(file, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static File resolveFile(File parentFile, String siblingName) {
        return parentFile.toPath().resolve(siblingName).toFile();
    }

    public static void collectFiles(File file, Collection<File> files, Predicate<File> test) {
        GameUtils.collectFiles(file, files, test, false);
    }

    public static void collectFiles(File file, Collection<File> files, Predicate<File> test, boolean testFolders) {
        if (file.isDirectory()) {
            if (testFolders && test != null && !test.test(file)) {
                return;
            }
            File[] children = file.listFiles();
            if (children == null) {
                throw new NullPointerException("Could not read files inside folder " + file.getPath());
            }
            for (File child : children) {
                GameUtils.collectFiles(child, files, test, testFolders);
            }
        } else {
            if (test != null && !test.test(file)) {
                return;
            }
            files.add(file);
        }
    }

    public static String getNextUniqueFilename(String path, String suffix, String extension) {
        return GameUtils.getNextUniqueFilename(path, suffix, extension, s -> new File((String)s).exists());
    }

    public static String getNextUniqueFilename(String path, String suffix, String extension, Function<String, Boolean> fileExists) {
        String newPath;
        int count = 1;
        String countSuffix = "";
        while (fileExists.apply(newPath = path + suffix + countSuffix + (extension == null || extension.length() == 0 ? "" : "." + extension)).booleanValue()) {
            countSuffix = Integer.toString(++count);
        }
        return newPath;
    }

    public static boolean mkDirs(File file) {
        if (file.isDirectory()) {
            return file.mkdirs();
        }
        File parent = file.getParentFile();
        return parent == null || parent.exists() || parent.mkdirs();
    }

    public static void saveByteFile(byte[] data, File file) throws IOException {
        if (!GameUtils.mkDirs(file)) {
            throw new IllegalArgumentException("Could not create folder for file: " + file.getAbsolutePath());
        }
        FileOutputStream saveFile = new FileOutputStream(file);
        saveFile.write(data);
        saveFile.close();
    }

    public static void saveByteFile(byte[] data, String filePath) throws IOException {
        GameUtils.saveByteFile(data, new File(filePath));
    }

    public static byte[] loadByteFile(File file) throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("Could not find byte file");
        }
        FileInputStream readFile = new FileInputStream(file);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("Byte file too large");
        }
        return GameUtils.loadInputStream(readFile);
    }

    public static byte[] loadInputStream(InputStream is) throws IOException {
        int nRead;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        is.close();
        return buffer.toByteArray();
    }

    public static byte[] loadByteFile(String filePath) throws IOException {
        return GameUtils.loadByteFile(new File(filePath));
    }

    public static byte[] encodeToPNG(ByteBuffer buffer, int imageWidth, int imageHeight, int imageBPP) {
        int bufferSize = imageWidth * imageHeight * imageBPP;
        ByteBuffer flippedBuffer = BufferUtils.createByteBuffer((int)bufferSize);
        for (int y = imageHeight - 1; y >= 0; --y) {
            int index = imageWidth * y * imageBPP;
            byte[] row = new byte[imageWidth * imageBPP];
            buffer.position(index);
            buffer.get(row, 0, row.length);
            flippedBuffer.put(row, 0, row.length);
        }
        flippedBuffer.position(0);
        ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
        STBImageWrite.stbi_write_png_to_func((context, data, size) -> {
            ByteBuffer pngChunk = MemoryUtil.memByteBuffer((long)data, (int)size);
            byte[] chunkBytes = new byte[size];
            pngChunk.get(chunkBytes);
            try {
                pngOutput.write(chunkBytes);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, (long)0L, (int)imageWidth, (int)imageHeight, (int)imageBPP, (ByteBuffer)flippedBuffer, (int)(imageWidth * 4));
        return pngOutput.toByteArray();
    }

    public static byte[] encodeRGBToPNG(ByteBuffer buffer, int imageWidth, int imageHeight) {
        return GameUtils.encodeToPNG(buffer, imageWidth, imageHeight, 3);
    }

    public static byte[] encodeRGBAToPNG(ByteBuffer buffer, int imageWidth, int imageHeight) {
        return GameUtils.encodeToPNG(buffer, imageWidth, imageHeight, 4);
    }

    public static byte[] compressData(byte[] input) throws IOException {
        Deflater compressor = new Deflater();
        compressor.setLevel(9);
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        byte[] buffer = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buffer);
            bos.write(buffer, 0, count);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static byte[] decompressData(byte[] compressedInput) throws DataFormatException, IOException {
        int count;
        if (compressedInput.length == 0) {
            return new byte[0];
        }
        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedInput);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedInput.length);
        byte[] buffer = new byte[1024];
        while (!decompressor.finished() && (count = decompressor.inflate(buffer)) != 0) {
            bos.write(buffer, 0, count);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static String toBase64(byte[] data) {
        return Base64.getUrlEncoder().encodeToString(data);
    }

    public static byte[] fromBase64(String data) throws DataFormatException {
        try {
            return Base64.getUrlDecoder().decode(data);
        }
        catch (IllegalArgumentException e) {
            throw new DataFormatException("Invalid base64 data: " + e.getMessage());
        }
    }

    public static String insertNewLines(String message, FontOptions fontOptions, int maxWidth, String ignoreRegex) {
        return GameUtils.insertNewLines(message, fontOptions, maxWidth, ignoreRegex, true);
    }

    public static String insertNewLines(String message, FontOptions fontOptions, int maxWidth, String ignoreRegex, boolean splitLines) {
        if (splitLines) {
            String[] newLineSplit = message.split("\\n");
            StringBuilder builder = new StringBuilder();
            for (String line : newLineSplit) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                builder.append(GameUtils.insertNewLines(line, fontOptions, maxWidth, ignoreRegex, false));
            }
            return builder.toString().trim();
        }
        String[] words = message.split(" ");
        if (words.length == 0) {
            return message;
        }
        float spaceWidth = FontManager.bit.getWidth(' ', fontOptions);
        float lineWidth = 0.0f;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; ++i) {
            String word = words[i];
            if (word.length() == 0) continue;
            String wordIgnoreNL = word.replace("\n", "").trim();
            String wordIgnoreRegex = ignoreRegex == null ? wordIgnoreNL : wordIgnoreNL.replaceAll(ignoreRegex, "");
            float nextWordWidth = FontManager.bit.getWidth(wordIgnoreRegex, fontOptions);
            if (maxWidth > 0 && lineWidth + nextWordWidth > (float)maxWidth) {
                if (lineWidth != 0.0f) {
                    builder = new StringBuilder(builder.toString().trim() + "\n");
                    lineWidth = 0.0f;
                    --i;
                    continue;
                }
                char[] wordChars = word.toCharArray();
                char[] ignoredChars = wordIgnoreRegex.toCharArray();
                int k = 0;
                for (int j = 0; j < wordChars.length; ++j) {
                    char c = wordChars[j];
                    boolean shouldCount = k >= ignoredChars.length ? false : c == ignoredChars[k];
                    builder.append(c);
                    if (shouldCount && (lineWidth += FontManager.bit.getWidth(c, fontOptions)) > (float)maxWidth) {
                        builder = new StringBuilder(builder.toString().trim() + "\n");
                        lineWidth = 0.0f;
                    }
                    if (!shouldCount) continue;
                    ++k;
                }
                word = null;
            }
            if (word == null) continue;
            builder.append(word).append(" ");
            lineWidth += nextWordWidth + spaceWidth;
        }
        return builder.toString().trim();
    }

    public static ArrayList<String> breakString(String message, FontOptions fontOptions, int maxWidth) {
        return GameUtils.breakString(message, fontOptions, maxWidth, null);
    }

    public static ArrayList<String> breakString(String message, FontOptions fontOptions, int maxWidth, String ignoreRegex) {
        String[] lines = GameUtils.insertNewLines(message, fontOptions, maxWidth, ignoreRegex).split("\\n");
        ArrayList<String> out = new ArrayList<String>();
        Collections.addAll(out, lines);
        return out;
    }

    public static String maxString(String str, FontOptions fontOptions, int maxWidth) {
        return GameUtils.maxString(str, fontOptions, maxWidth, null);
    }

    public static String maxString(String str, FontOptions fontOptions, int maxWidth, String ignoreRegex) {
        String cStr = str;
        while (FontManager.bit.getWidthCeil(ignoreRegex == null ? cStr : cStr.replaceAll(ignoreRegex, ""), fontOptions) >= maxWidth) {
            if (cStr.length() == 1) {
                return "";
            }
            cStr = str.substring(0, cStr.length() - 1);
        }
        return cStr;
    }

    public static Integer tryParseInt(String input) {
        if (input == null) {
            return null;
        }
        boolean negative = false;
        int i = 0;
        int len = input.length();
        int limit = -2147483647;
        if (len > 0) {
            char firstChar = input.charAt(0);
            if (firstChar < '0') {
                if (firstChar == '-') {
                    negative = true;
                    limit = Integer.MIN_VALUE;
                } else if (firstChar != '+') {
                    return 0;
                }
                if (len == 1) {
                    return 0;
                }
                ++i;
            }
            int multmin = limit / 10;
            int result = 0;
            while (i < len) {
                int digit;
                if ((digit = Character.digit(input.charAt(i++), 10)) < 0 || result < multmin) {
                    return null;
                }
                if ((result *= 10) < limit + digit) {
                    return null;
                }
                result -= digit;
            }
            return negative ? result : -result;
        }
        return null;
    }

    public static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static BigDecimal exponent(BigDecimal number, int pow) {
        if (pow < 0) {
            return number.divide(BigDecimal.valueOf(10L).pow(Math.abs(pow)), new MathContext(0, RoundingMode.DOWN));
        }
        return number.multiply(BigDecimal.valueOf(10L).pow(pow));
    }

    public static String metricNumber(BigDecimal number, int decimalOffset, int decimals, boolean minimize, RoundingMode roundingMode, String spacer, MetricValue[] metricSystem) {
        String roundString;
        BigDecimal decimal;
        int sign;
        if (metricSystem.length == 0) {
            throw new IllegalArgumentException("metricSystem must have a least one value");
        }
        if (decimalOffset != 0) {
            number = GameUtils.exponent(number, decimalOffset);
        }
        if (spacer == null) {
            spacer = "";
        }
        if ((sign = number.signum()) == 0) {
            return "0" + spacer;
        }
        String prefix = "";
        if (sign < 0) {
            prefix = "-";
            number = number.abs();
        }
        MetricValue lastMetric = null;
        MetricValue[] metricValueArray = metricSystem;
        int n = metricValueArray.length;
        for (int i = 0; i < n; ++i) {
            MetricValue metric;
            lastMetric = metric = metricValueArray[i];
            if (number.compareTo(metric.divider) >= 0) break;
        }
        if ((decimal = number.divide(lastMetric.divider, new MathContext(0, RoundingMode.DOWN))).remainder(BigDecimal.ONE).equals(BigDecimal.ZERO)) {
            roundString = decimal.toString();
        } else if (minimize) {
            BigDecimal abs = decimal.abs();
            int digitsLeftOfDecimalPoint = abs.toString().length() - abs.scale() - 1;
            if (!abs.equals(decimal)) {
                ++digitsLeftOfDecimalPoint;
            }
            int maxDecimals = Math.max(0, 2 - digitsLeftOfDecimalPoint);
            roundString = decimal.setScale(Math.min(decimals, maxDecimals), roundingMode).toString();
        } else {
            roundString = decimal.setScale(decimals, roundingMode).toString();
        }
        return prefix + roundString + spacer + lastMetric.suffix;
    }

    public static String metricNumber(long number, int decimals, boolean minimize, RoundingMode roundingMode, String spacer) {
        return GameUtils.metricNumber(BigDecimal.valueOf(number), 0, decimals, minimize, roundingMode, spacer, normalMetricSystem);
    }

    public static String metricNumber(long number, int decimals, boolean minimize, String spacer) {
        return GameUtils.metricNumber(number, decimals, minimize, RoundingMode.HALF_UP, spacer);
    }

    public static String metricNumber(long number, int decimals, String spacer) {
        return GameUtils.metricNumber(number, decimals, false, spacer);
    }

    public static String metricNumber(long number, String spacer) {
        return GameUtils.metricNumber(number, 2, spacer);
    }

    public static String metricNumber(long number, int decimals) {
        return GameUtils.metricNumber(number, decimals, null);
    }

    public static String metricNumber(long number) {
        return GameUtils.metricNumber(number, null);
    }

    public static String getByteString(long bytes) {
        return GameUtils.metricNumber(bytes, " ") + "B";
    }

    public static String getTimeStringNano(long nanoSeconds) {
        long seconds = nanoSeconds / 1000000000L;
        String prefix = "";
        if (seconds > 60L) {
            prefix = GameUtils.formatSecondsMinutes(seconds, true) + " ";
            long cut = seconds / 60L * 60L;
            nanoSeconds -= cut * 1000000000L;
        }
        return prefix + GameUtils.metricNumber(BigDecimal.valueOf(nanoSeconds), -9, 2, false, RoundingMode.HALF_UP, " ", normalMetricSystem) + "s";
    }

    public static String getTimeStringMillis(long milliSeconds) {
        long seconds = milliSeconds / 1000L;
        String prefix = "";
        if (seconds > 60L) {
            prefix = GameUtils.formatSecondsMinutes(seconds, true) + " ";
            long cut = seconds / 60L * 60L;
            milliSeconds -= cut * 1000L;
        }
        return prefix + GameUtils.metricNumber(BigDecimal.valueOf(milliSeconds), -3, 2, false, RoundingMode.HALF_UP, " ", normalMetricSystem) + "s";
    }

    private static String formatSecondsMinutes(long seconds, boolean addSpaces) {
        long d = seconds / 3600L / 24L;
        long h = seconds / 3600L % 24L;
        long m = seconds / 60L % 60L;
        if (d > 0L) {
            return d + "d" + (addSpaces ? " " : "") + h + "h" + (addSpaces ? " " : "") + m + "m";
        }
        if (h > 0L) {
            return h + "h" + (addSpaces ? " " : "") + m + "m";
        }
        if (m > 0L) {
            return m + "m";
        }
        return "";
    }

    public static String formatSeconds(long seconds, boolean addSpaces) {
        long s = seconds % 60L;
        return GameUtils.formatSecondsMinutes(seconds, addSpaces) + s + (addSpaces ? " " : "") + "s";
    }

    public static String formatSeconds(long seconds) {
        return GameUtils.formatSeconds(seconds, false);
    }

    public static String formatNumber(long value) {
        return NumberFormat.getNumberInstance(Locale.ENGLISH).format(value);
    }

    public static String formatNumber(double value) {
        return NumberFormat.getNumberInstance(Locale.ENGLISH).format(value);
    }

    public static String padString(String input, int length, char c) {
        if (input.length() >= length) {
            return input;
        }
        StringBuilder builder = new StringBuilder(input);
        while (builder.length() < length) {
            builder.append(c);
        }
        return builder.toString();
    }

    public static void forEachMatcherResult(Pattern pattern, String input, Consumer<MatchResult> forEach) {
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            forEach.accept(matcher.toMatchResult());
        }
    }

    public static String matcherReplaceAll(Pattern pattern, String input, Function<MatchResult, String> replacer) {
        StringBuilder out = new StringBuilder();
        AtomicInteger currentPos = new AtomicInteger();
        GameUtils.forEachMatcherResult(pattern, input, result -> {
            String replace = (String)replacer.apply((MatchResult)result);
            if (replace != null) {
                out.append(input, currentPos.get(), result.start());
                out.append(replace);
                currentPos.set(result.end());
            }
        });
        out.append(input, currentPos.get(), input.length());
        return out.toString();
    }

    public static Dimension getPlayerNameLength() {
        return new Dimension(1, 30);
    }

    public static GameMessage isValidPlayerName(String name) {
        name = name.trim();
        Dimension length = GameUtils.getPlayerNameLength();
        if (name.length() < length.width || name.length() > length.height) {
            return new LocalMessage("ui", "playernamesize");
        }
        if (!playerNameSymbolsPattern.matcher(name).matches()) {
            return new LocalMessage("ui", "playernamesymbols");
        }
        return null;
    }

    public static Stream<? extends NetworkClient> streamNetworkClients(Level level) {
        if (level.isServer()) {
            return GameUtils.streamServerClients(level);
        }
        if (level.isClient()) {
            return GameUtils.streamClientClients(level);
        }
        return Stream.empty();
    }

    public static Stream<ClientClient> streamClientClients(Client client, LevelIdentifier levelIdentifier) {
        if (client == null) {
            return Stream.empty();
        }
        return client.streamClients().filter(c -> c != null && c.loadedPlayer && c.hasSpawned() && !c.isDead() && c.isSamePlace(levelIdentifier));
    }

    public static Stream<ClientClient> streamClientClients(Client client, Level level) {
        return GameUtils.streamClientClients(client, level.getIdentifier());
    }

    public static Stream<ClientClient> streamClientClients(Level level) {
        return GameUtils.streamClientClients(level.getClient(), level);
    }

    public static Stream<ServerClient> streamServerClients(Server server, LevelIdentifier levelIdentifier) {
        if (server == null) {
            return Stream.empty();
        }
        return server.streamClients().filter(c -> c.playerMob != null && c.hasSpawned() && !c.playerMob.removed() && c.isSamePlace(levelIdentifier));
    }

    public static Stream<ServerClient> streamServerClients(Server server, Level level) {
        return GameUtils.streamServerClients(server, level.getIdentifier());
    }

    public static Stream<ServerClient> streamServerClients(Level level) {
        return GameUtils.streamServerClients(level.getServer(), level);
    }

    public static Stream<Point> streamTileCoordinatesBetweenPoints(final Level level, final Point2D start, final Point2D end) {
        Iterator<Point> iterator = new Iterator<Point>(){
            Point next;
            double x;
            double y;
            int tileX;
            int tileY;
            double distanceTraveled;
            final double lineDistance;
            final double normalizedDirX;
            final double normalizedDirY;
            final int signDirX;
            final int signDirY;
            final int debugIndex = 0;
            {
                this.x = start.getX();
                this.y = start.getY();
                this.tileX = GameMath.getTileCoordinate(this.x);
                this.tileY = GameMath.getTileCoordinate(this.y);
                this.distanceTraveled = 0.0;
                this.lineDistance = start.distance(end);
                this.normalizedDirX = (end.getX() - start.getX()) / this.lineDistance;
                this.normalizedDirY = (end.getY() - start.getY()) / this.lineDistance;
                this.signDirX = (int)Math.signum(this.normalizedDirX);
                this.signDirY = (int)Math.signum(this.normalizedDirY);
                this.debugIndex = 0;
                this.next = this.computeNext();
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Point next() {
                if (this.next == null) {
                    throw new NoSuchElementException("Out of elements");
                }
                Point out = this.next;
                this.next = this.computeNext();
                return out;
            }

            private Point computeNext() {
                while (this.distanceTraveled <= this.lineDistance) {
                    double distanceToMove;
                    if (this.next == null || this.tileX != this.next.x || this.tileY != this.next.y) {
                        return new Point(this.tileX, this.tileY);
                    }
                    double xSign = Math.signum(this.x);
                    double ySign = Math.signum(this.y);
                    double toNextX = (((double)this.signDirX * xSign < 0.0 ? 0.0 : 32.0) - Math.abs(this.x) % 32.0) * xSign;
                    double toNextY = (((double)this.signDirY * ySign < 0.0 ? 0.0 : 32.0) - Math.abs(this.y) % 32.0) * ySign;
                    if (toNextX == 0.0) {
                        toNextX = this.signDirX * 32;
                    }
                    if (toNextY == 0.0) {
                        toNextY = this.signDirY * 32;
                    }
                    double distanceToMoveX = toNextX / this.normalizedDirX;
                    double distanceToMoveY = toNextY / this.normalizedDirY;
                    double biasX = 0.0;
                    double biasY = 0.0;
                    if (distanceToMoveX < distanceToMoveY) {
                        distanceToMove = distanceToMoveX;
                        biasX = this.signDirX;
                    } else {
                        distanceToMove = distanceToMoveY;
                        biasY = this.signDirY;
                    }
                    this.x += distanceToMove * this.normalizedDirX + biasX;
                    this.y += distanceToMove * this.normalizedDirY + biasY;
                    this.tileX = GameMath.getTileCoordinate(this.x);
                    this.tileY = GameMath.getTileCoordinate(this.y);
                    if (!level.isTileWithinBounds(this.tileX, this.tileY)) break;
                    this.distanceTraveled += distanceToMove;
                }
                return null;
            }
        };
        Iterable iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static Stream<Point> streamRegionCoordinatesBetweenPoints(final Level level, final Point2D start, final Point2D end) {
        Iterator<Point> iterator = new Iterator<Point>(){
            Point next;
            double x;
            double y;
            int regionX;
            int regionY;
            double distanceTraveled;
            final double lineDistance;
            final double normalizedDirX;
            final double normalizedDirY;
            final int signDirX;
            final int signDirY;
            static final double regionSize = 512.0;
            {
                this.x = start.getX();
                this.y = start.getY();
                this.regionX = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(this.x));
                this.regionY = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(this.y));
                this.distanceTraveled = 0.0;
                this.lineDistance = start.distance(end);
                this.normalizedDirX = (end.getX() - start.getX()) / this.lineDistance;
                this.normalizedDirY = (end.getY() - start.getY()) / this.lineDistance;
                this.signDirX = (int)Math.signum(this.normalizedDirX);
                this.signDirY = (int)Math.signum(this.normalizedDirY);
                this.next = this.computeNext();
            }

            @Override
            public boolean hasNext() {
                return this.next != null;
            }

            @Override
            public Point next() {
                if (this.next == null) {
                    throw new NoSuchElementException("Out of elements");
                }
                Point out = this.next;
                this.next = this.computeNext();
                return out;
            }

            private Point computeNext() {
                while (this.distanceTraveled <= this.lineDistance) {
                    double distanceToMove;
                    if (this.next == null || this.regionX != this.next.x || this.regionY != this.next.y) {
                        return new Point(this.regionX, this.regionY);
                    }
                    double xSign = Math.signum(this.x);
                    double ySign = Math.signum(this.y);
                    double toNextX = (((double)this.signDirX * xSign < 0.0 ? 0.0 : 512.0) - Math.abs(this.x) % 512.0) * xSign;
                    double toNextY = (((double)this.signDirY * ySign < 0.0 ? 0.0 : 512.0) - Math.abs(this.y) % 512.0) * ySign;
                    if (toNextX == 0.0) {
                        toNextX = (double)this.signDirX * 512.0;
                    }
                    if (toNextY == 0.0) {
                        toNextY = (double)this.signDirY * 512.0;
                    }
                    double distanceToMoveX = toNextX / this.normalizedDirX;
                    double distanceToMoveY = toNextY / this.normalizedDirY;
                    double biasX = 0.0;
                    double biasY = 0.0;
                    if (distanceToMoveX < distanceToMoveY) {
                        distanceToMove = distanceToMoveX;
                        biasX = this.signDirX;
                    } else {
                        distanceToMove = distanceToMoveY;
                        biasY = this.signDirY;
                    }
                    this.x += distanceToMove * this.normalizedDirX + biasX;
                    this.y += distanceToMove * this.normalizedDirY + biasY;
                    this.regionX = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(this.x));
                    this.regionY = GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(this.y));
                    if (!level.regionManager.isRegionWithinBounds(this.regionX, this.regionY)) break;
                    this.distanceTraveled += distanceToMove;
                }
                return null;
            }
        };
        Iterable iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static Mob getLevelMob(int uniqueID, Level level) {
        return GameUtils.getLevelMob(uniqueID, level, false);
    }

    public static Mob getLevelMob(int uniqueID, Level level, boolean searchCache) {
        return GameUtils.getLevelMob(uniqueID, level, searchCache, false);
    }

    public static Mob getLevelMob(int uniqueID, Level level, boolean searchCache, boolean allowOutsideLevelPlayers) {
        if (level == null) {
            return null;
        }
        Mob out = null;
        if (level.isServer()) {
            if (uniqueID < level.getServer().getSlots() && uniqueID >= 0) {
                if (level.getServer().getClient(uniqueID) == null) {
                    return null;
                }
                if (!allowOutsideLevelPlayers && !level.getServer().getClient(uniqueID).isSamePlace(level)) {
                    return null;
                }
                out = level.getServer().getClient((int)uniqueID).playerMob;
            }
        } else if (level.isClient() && uniqueID < level.getClient().getSlots() && uniqueID >= 0) {
            if (level.getClient().getClient(uniqueID) == null) {
                return null;
            }
            if (!allowOutsideLevelPlayers && !level.getClient().getClient(uniqueID).isSamePlace(level)) {
                return null;
            }
            out = level.getClient().getClient((int)uniqueID).playerMob;
        }
        if (out == null) {
            out = level.entityManager.mobs.get(uniqueID, searchCache);
        }
        return out;
    }

    public static Attacker getLevelAttacker(int uniqueID, Level level) {
        if (level == null) {
            return null;
        }
        Attacker out = GameUtils.getLevelMob(uniqueID, level);
        if (out == null) {
            out = level.entityManager.projectiles.get(uniqueID, true);
        }
        return out;
    }

    public static Point getValidMobLocationAroundObject(GameRandom random, Level level, Mob mob, int tileX, int tileY, boolean allowDiagonal) {
        ArrayList<Point> priority1Spawns = new ArrayList<Point>(8);
        ArrayList<Point> priority2Spawns = new ArrayList<Point>(8);
        ArrayList<Point> priority3Spawns = new ArrayList<Point>(8);
        level.regionManager.ensureTileIsLoaded(tileX, tileY);
        for (Point tile : level.getLevelObject(tileX, tileY).getMultiTile().getAdjacentTiles(tileX, tileY, allowDiagonal)) {
            int posX = tile.x * 32 + 16;
            int posY = tile.y * 32 + 16;
            if (!mob.collidesWith(level, posX, posY)) {
                if (!mob.collidesWithAnyMob(level, posX, posY)) {
                    priority1Spawns.add(new Point(posX, posY));
                }
                priority2Spawns.add(new Point(posX, posY));
            }
            priority3Spawns.add(new Point(posX, posY));
        }
        if (!priority1Spawns.isEmpty()) {
            return (Point)random.getOneOf(priority1Spawns);
        }
        if (!priority2Spawns.isEmpty()) {
            return (Point)random.getOneOf(priority2Spawns);
        }
        return (Point)random.getOneOf(priority3Spawns);
    }

    public static Rectangle rangeBounds(int x, int y, int range) {
        return new Rectangle(x - range, y - range, range * 2, range * 2);
    }

    public static Rectangle rangeBounds(float x, float y, int range) {
        return GameUtils.rangeBounds((int)x, (int)y, range);
    }

    public static Rectangle rangeTileBounds(int x, int y, int tileRange) {
        return GameUtils.rangeBounds(x, y, tileRange * 32);
    }

    public static Stream<Mob> streamTargetsRange(Mob attacker, int range) {
        return GameUtils.streamTargetsRange(attacker, attacker.getX(), attacker.getY(), range);
    }

    public static Stream<Mob> streamTargetsRange(Mob attacker, int posX, int posY, int range) {
        return GameUtils.streamTargets(attacker, GameUtils.rangeBounds(posX, posY, range));
    }

    public static Stream<Mob> streamTargetsTileRange(Mob attacker, int tileRange) {
        return GameUtils.streamTargetsTileRange(attacker, attacker.getX(), attacker.getY(), tileRange);
    }

    public static Stream<Mob> streamTargetsTileRange(Mob attacker, int posX, int posY, int tileRange) {
        return GameUtils.streamTargets(attacker, GameUtils.rangeTileBounds(posX, posY, tileRange));
    }

    public static Stream<Mob> streamTargets(Mob attacker, Shape hitBounds) {
        return GameUtils.streamTargets(attacker, hitBounds, 1);
    }

    public static Stream<Mob> streamTargets(Mob attacker, Level level, Shape hitBounds) {
        return GameUtils.streamTargets(attacker, level, hitBounds, 1);
    }

    public static <T> Stream<T> streamConcat(Stream<? extends T> ... streams) {
        Stream<? extends T> out = null;
        for (Stream<? extends T> stream : streams) {
            out = out == null ? stream : Stream.concat(out, stream);
        }
        if (out == null) {
            return Stream.empty();
        }
        return out;
    }

    public static NetworkClient getAttackerClient(Mob attacker) {
        NetworkClient followingClient = attacker.getFollowingClient();
        if (followingClient != null) {
            return followingClient;
        }
        if (attacker.isPlayer) {
            return ((PlayerMob)attacker).getNetworkClient();
        }
        return null;
    }

    public static Stream<Mob> streamTargets(Mob attacker, Shape hitBounds, int extraRegionRange) {
        return GameUtils.streamTargets(attacker, attacker == null ? null : attacker.getLevel(), hitBounds, extraRegionRange);
    }

    public static Stream<Mob> streamTargets(Mob attacker, Level level, Shape hitBounds, int extraRegionRange) {
        if (attacker == null || level == null) {
            return Stream.empty();
        }
        NetworkClient attackerClient = GameUtils.getAttackerClient(attacker);
        Stream<Mob> targets = hitBounds == null ? level.entityManager.mobs.stream() : level.entityManager.mobs.streamInRegionsShape(hitBounds, extraRegionRange);
        targets = targets.filter(m -> m.canBeTargeted(attacker, attackerClient));
        if (attackerClient != null && !attackerClient.pvpEnabled()) {
            return targets;
        }
        Stream<PlayerMob> players = hitBounds == null ? GameUtils.streamNetworkClients(level).map(c -> c.playerMob) : level.entityManager.players.streamInRegionsShape(hitBounds, extraRegionRange);
        players = players.filter(p -> {
            NetworkClient c = p.getNetworkClient();
            if (c == null || c.playerMob == null || c.playerMob.getLevel() == null) {
                return false;
            }
            if (c.isDead() || !c.hasSpawned()) {
                return false;
            }
            if (attackerClient != null && !c.pvpEnabled()) {
                return false;
            }
            return c.playerMob.canBeTargeted(attacker, attackerClient);
        });
        return Stream.concat(targets, players);
    }

    public static <T> String join(T[] objects, Function<T, String> toString, String join, String lastJoin) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < objects.length; ++i) {
            out.append(toString == null ? objects[i] : toString.apply(objects[i]));
            if (i == objects.length - 2) {
                out.append(lastJoin);
                continue;
            }
            if (i >= objects.length - 1) continue;
            out.append(join);
        }
        return out.toString();
    }

    public static String join(Object[] objects, String join, String lastJoin) {
        return GameUtils.join(objects, null, join, lastJoin);
    }

    public static <T> String join(T[] objects, Function<T, String> toString, String join) {
        return GameUtils.join(objects, toString, join, join);
    }

    public static String join(Object[] objects, String join) {
        return GameUtils.join(objects, null, join, join);
    }

    public static <T> void insertSortedList(List<T> list, T object, Comparator<T> comparator) {
        GameUtils.insertSortedList(list.listIterator(), object, comparator);
    }

    public static <T> void insertSortedList(ListIterator<T> iterator, T object, Comparator<T> comparator) {
        if (comparator == null) {
            if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("Must either supply comparator or object must be comparable.");
            }
            comparator = (o1, o2) -> ((Comparable)o1).compareTo(o2);
        }
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (comparator.compare(next, object) <= 0) continue;
            iterator.previous();
            iterator.add(object);
            return;
        }
        iterator.add(object);
    }

    public static <T> GameLinkedList.Element insertSortedList(GameLinkedList<T> list, T object, Comparator<T> comparator) {
        if (comparator == null) {
            if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("Must either supply comparator or object must be comparable.");
            }
            comparator = (o1, o2) -> ((Comparable)o1).compareTo(o2);
        }
        for (GameLinkedList.Element next = list.getFirstElement(); next != null; next = next.next()) {
            if (comparator.compare(next.object, object) <= 0) continue;
            return next.insertBefore(object);
        }
        return list.addLast(object);
    }

    public static <T> GameLinkedList.Element insertSortedListReversed(GameLinkedList<T> list, T object, Comparator<T> comparator) {
        if (comparator == null) {
            if (!(object instanceof Comparable)) {
                throw new IllegalArgumentException("Must either supply comparator or object must be comparable.");
            }
            comparator = (o1, o2) -> ((Comparable)o1).compareTo(o2);
        }
        for (GameLinkedList.Element last = list.getLastElement(); last != null; last = last.prev()) {
            if (comparator.compare(last.object, object) >= 0) continue;
            return last.insertAfter(object);
        }
        return list.addFirst(object);
    }

    public static Color getBrighterColor(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int i = (int)(1.0f / factor);
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, color.getAlpha());
        }
        if (r > 0 && r < i) {
            r = i;
        }
        if (g > 0 && g < i) {
            g = i;
        }
        if (b > 0 && b < i) {
            b = i;
        }
        return new Color(Math.min((int)((float)r / factor), 255), Math.min((int)((float)g / factor), 255), Math.min((int)((float)b / factor), 255), color.getAlpha());
    }

    public static Color getDarkerColor(Color color, float factor) {
        return new Color(Math.max((int)((float)color.getRed() * factor), 0), Math.max((int)((float)color.getGreen() * factor), 0), Math.max((int)((float)color.getBlue() * factor), 0), color.getAlpha());
    }

    public static float getStatusColorHue(float percent) {
        return GameMath.limit(percent, 0.0f, 1.0f) / 3.0f;
    }

    public static Color getStatusColor(float percent, float saturation, float brightness) {
        return Color.getHSBColor(GameUtils.getStatusColorHue(percent), saturation, brightness);
    }

    public static Color getStatusColorRedPref(float percent, float saturation, float brightness, float exponent) {
        percent = (float)Math.pow(percent, exponent);
        return Color.getHSBColor(GameUtils.getStatusColorHue(percent), saturation, brightness);
    }

    public static Color getStatusColorGreenPref(float percent, float saturation, float brightness, float exponent) {
        percent = Math.abs((float)Math.pow(Math.abs(percent - 1.0f), exponent) - 1.0f);
        return Color.getHSBColor(GameUtils.getStatusColorHue(percent), saturation, brightness);
    }

    public static Color getStatusColorLerp(float percent, float lowerLimit, float upperLimit) {
        float percentInv = Math.abs(percent - 1.0f);
        float red = GameMath.limit(percentInv * 2.0f, 0.0f, 1.0f);
        float green = GameMath.limit(percent * 2.0f, 0.0f, 1.0f);
        return new Color(GameMath.lerp(red, lowerLimit, upperLimit), GameMath.lerp(green, lowerLimit, upperLimit), 0.0f);
    }

    public static Color getStatusColorLerpRedPref(float percent, float lowerLimit, float upperLimit, float exponent) {
        percent = (float)Math.pow(percent, exponent);
        return GameUtils.getStatusColorLerp(percent, lowerLimit, upperLimit);
    }

    public static Color getStatusColorLerpGreenPref(float percent, float lowerLimit, float upperLimit, float exponent) {
        percent = Math.abs((float)Math.pow(Math.abs(percent - 1.0f), exponent) - 1.0f);
        return GameUtils.getStatusColorLerp(percent, lowerLimit, upperLimit);
    }

    public static float getBobbing(long time, int bobbingTime) {
        long timeMod = time % (long)bobbingTime;
        int halfBobbingTime = bobbingTime / 2;
        if (timeMod > (long)halfBobbingTime) {
            timeMod = (long)halfBobbingTime - timeMod % (long)halfBobbingTime;
        }
        return (float)timeMod / (float)halfBobbingTime;
    }

    public static float getTimeRotation(long time, int div, int accuracy) {
        if (accuracy <= 1) {
            return time % 360L;
        }
        return (float)(time * (long)accuracy / (long)div % (long)(360 * accuracy)) / (float)accuracy;
    }

    public static float getTimeRotation(long time, int div) {
        return GameUtils.getTimeRotation(time, div, 10);
    }

    public static float getAnimFloat(long time, int animTime) {
        return (float)(time % (long)animTime) / (float)animTime;
    }

    public static float getAnimFloatContinuous(long time, int animTime) {
        float anim = GameUtils.getAnimFloat(time, animTime);
        if (anim > 0.5f) {
            return Math.abs((anim - 0.5f) * 2.0f - 1.0f);
        }
        return anim * 2.0f;
    }

    public static int getAnim(long time, int frames, int animTime) {
        float frameTime = (float)animTime / (float)frames;
        return (int)Math.min((float)(time % (long)animTime) / frameTime, (float)(frames - 1));
    }

    public static int getAnimContinuous(long time, int[] frameTimes) {
        if (time < 0L) {
            return -1;
        }
        int totalTime = 0;
        for (int i = 0; i < frameTimes.length; ++i) {
            if (time < (long)frameTimes[i]) {
                return i;
            }
            totalTime += frameTimes[i];
            time -= (long)frameTimes[i];
        }
        return GameUtils.getAnim(time %= (long)totalTime, frameTimes);
    }

    public static int getAnim(long time, int[] frameTimes) {
        if (time < 0L) {
            return -1;
        }
        for (int i = 0; i < frameTimes.length; ++i) {
            if (time < (long)frameTimes[i]) {
                return i;
            }
            time -= (long)frameTimes[i];
        }
        return -1;
    }

    public static int getBounceAnim(long time, int bounceAmount, int bounceCooldown, int totalBounceTime, int firstBounceTime, int defaultNoBounce) {
        long animTime = time % (long)(bounceCooldown + totalBounceTime);
        if (animTime <= (long)totalBounceTime) {
            float bouncePerc;
            int calculatedBounce = 0;
            int bounceTimeOffset = 0;
            for (int bounceTime = firstBounceTime; bounceTime > 0 && !((bouncePerc = (float)(animTime - (long)bounceTimeOffset) / (float)bounceTime) < 0.0f); bounceTime /= 2) {
                float amountMod = (float)bounceTime / (float)firstBounceTime;
                if (amountMod * (float)bounceAmount < 1.0f) {
                    return defaultNoBounce;
                }
                calculatedBounce = (int)(GameMath.sin(bouncePerc * 180.0f) * (float)bounceAmount * amountMod);
                bounceTimeOffset += bounceTime;
            }
            return calculatedBounce;
        }
        return defaultNoBounce;
    }

    public static int getBounceAnim(long time, int bounceAmount, int bounceCooldown, int totalBounceTime, int firstBounceTime) {
        return GameUtils.getBounceAnim(time, bounceAmount, bounceCooldown, totalBounceTime, firstBounceTime, 0);
    }

    public static float getMultiplayerScaling(int playerCount, int maxPlayerCount, float percentStart, float percentDec) {
        float inc;
        if (percentDec == 0.0f) {
            return 1.0f + (float)(Math.min(playerCount, maxPlayerCount) - 1) * percentStart;
        }
        float multiplier = 1.0f;
        for (int i = 2; i <= Math.min(playerCount, maxPlayerCount) && !((inc = percentStart - percentDec * (float)(i - 2)) <= 0.0f); ++i) {
            multiplier += inc;
        }
        return multiplier;
    }

    public static float getMultiplayerScaling(int playerCount, float percentStart, float percentDec) {
        return GameUtils.getMultiplayerScaling(playerCount, 20, percentStart, percentDec);
    }

    public static float getMultiplayerScaling(int playerCount) {
        return GameUtils.getMultiplayerScaling(playerCount, 0.8f, 0.04f);
    }

    public static <T> RayLinkedList<T> castRay(double x, double y, double dx, double dy, double distance, double checkDistance, int maxBounces, Function<Line2D, IntersectionPoint<T>> rayHandler) {
        Point2D.Double dir = GameMath.normalize(dx, dy);
        if (Double.isNaN(dir.x) || Double.isNaN(dir.y)) {
            return new RayLinkedList();
        }
        Point2D.Double currentPos = new Point2D.Double(x, y);
        Line2D.Double currentLine = new Line2D.Double(currentPos.x, currentPos.y, currentPos.x + dir.x * Math.min(checkDistance, distance), currentPos.y + dir.y * Math.min(checkDistance, distance));
        double currentDistance = 0.0;
        RayLinkedList out = new RayLinkedList();
        boolean lastLine = false;
        while (out.size() <= maxBounces) {
            IntersectionPoint<T> collisionPoint = rayHandler.apply(currentLine);
            if (collisionPoint == null) {
                if (lastLine) {
                    out.addLast(new Ray<Object>(currentPos.x, currentPos.y, currentPos.x + dir.x * distance, currentPos.y + dir.y * distance, null, null));
                    out.totalDist += distance;
                    break;
                }
                double nextDist = distance - (currentDistance += ((Line2D)currentLine).getP1().distance(((Line2D)currentLine).getP2()));
                if (nextDist <= checkDistance) {
                    currentLine = new Line2D.Double(((Line2D)currentLine).getX2(), ((Line2D)currentLine).getY2(), ((Line2D)currentLine).getX2() + dir.x * nextDist, ((Line2D)currentLine).getY2() + dir.y * nextDist);
                    lastLine = true;
                    continue;
                }
                currentLine = new Line2D.Double(((Line2D)currentLine).getX2(), ((Line2D)currentLine).getY2(), ((Line2D)currentLine).getX2() + dir.x * checkDistance, ((Line2D)currentLine).getY2() + dir.y * checkDistance);
                continue;
            }
            if (collisionPoint.dir == IntersectionPoint.Dir.UP) {
                dir.y = -dir.y;
                collisionPoint.y += 0.1;
            } else if (collisionPoint.dir == IntersectionPoint.Dir.DOWN) {
                dir.y = -dir.y;
                collisionPoint.y -= 0.1;
            } else if (collisionPoint.dir == IntersectionPoint.Dir.LEFT) {
                dir.x = -dir.x;
                collisionPoint.x += 0.1;
            } else if (collisionPoint.dir == IntersectionPoint.Dir.RIGHT) {
                dir.x = -dir.x;
                collisionPoint.x -= 0.1;
            }
            Ray ray = new Ray(currentPos.x, currentPos.y, collisionPoint.x, collisionPoint.y, collisionPoint.target, collisionPoint.dir);
            out.addLast(ray);
            out.totalDist += ray.dist;
            currentLine = new Line2D.Double(collisionPoint.x, collisionPoint.y, collisionPoint.x + dir.x * Math.min(checkDistance, distance -= ray.dist), collisionPoint.y + dir.y * Math.min(checkDistance, distance));
            lastLine = false;
            currentDistance = 0.0;
            currentPos = new Point2D.Double(collisionPoint.x, collisionPoint.y);
        }
        return out;
    }

    public static <T> RayLinkedList<T> castRay(Line2D line, double checkDistance, int maxBounces, Function<Line2D, IntersectionPoint<T>> rayHandler) {
        return GameUtils.castRay(line.getX1(), line.getY1(), line.getX2() - line.getX1(), line.getY2() - line.getY1(), line.getP1().distance(line.getP2()), checkDistance, maxBounces, rayHandler);
    }

    public static <T> T castRayFirstHit(double x, double y, double dx, double dy, double distance, double checkDistance, Function<Line2D, T> rayHandler) {
        RayLinkedList<T> result = GameUtils.castRay(x, y, dx, dy, distance, checkDistance, 0, (Line2D line) -> {
            Object hit = rayHandler.apply((Line2D)line);
            if (hit == null) {
                return null;
            }
            return new IntersectionPoint(line.getX2(), line.getY2(), hit, IntersectionPoint.Dir.DOWN);
        });
        Ray lastHit = (Ray)result.getLast();
        if (lastHit == null || lastHit.targetHit == null) {
            return null;
        }
        return lastHit.targetHit;
    }

    public static <T> T castRayFirstHit(Line2D line, double checkDistance, Function<Line2D, T> rayHandler) {
        return GameUtils.castRayFirstHit(line.getX1(), line.getY1(), line.getX2() - line.getX1(), line.getY2() - line.getY1(), line.getP1().distance(line.getP2()), checkDistance, rayHandler);
    }

    public static RayLinkedList<LevelObjectHit> castRay(Level level, double x, double y, double dx, double dy, double distance, double checkDistance, int maxBounces, CollisionFilter collisionFilter, boolean checkInsideRect) {
        return GameUtils.castRay(x, y, dx, dy, distance, checkDistance, maxBounces, (Line2D line) -> {
            IntersectionPoint<LevelObjectHit> test = level.getCollisionPoint((List<LevelObjectHit>)level.getCollisions((Shape)line, collisionFilter), (Line2D)line, checkInsideRect);
            return test;
        });
    }

    public static RayLinkedList<LevelObjectHit> castRay(Level level, double x, double y, double dx, double dy, double distance, int maxBounces, CollisionFilter collisionFilter) {
        return GameUtils.castRay(level, x, y, dx, dy, distance, 100.0, maxBounces, collisionFilter, false);
    }

    public static Ray<LevelObjectHit> castRayFirstHit(Level level, double x, double y, double dx, double dy, double distance, CollisionFilter collisionFilter) {
        RayLinkedList<LevelObjectHit> rays = GameUtils.castRay(level, x, y, dx, dy, distance, 100.0, 0, collisionFilter, false);
        if (rays.isEmpty()) {
            return null;
        }
        return (Ray)rays.getFirst();
    }

    public static Ray<LevelObjectHit> castRayFirstHit(Level level, Line2D line, CollisionFilter collisionFilter) {
        return GameUtils.castRayFirstHit(level, line.getX1(), line.getY1(), line.getX2() - line.getX1(), line.getY2() - line.getY1(), line.getP1().distance(line.getP2()), collisionFilter);
    }

    public static LevelObject getInteractObjectHit(Level level, int hitX, int hitY, int layerID, Predicate<LevelObject> filter) {
        return GameUtils.getInteractObjectHit(level, hitX, hitY, layerID, filter, new LevelObject(level, GameMath.getTileCoordinate(hitX), GameMath.getTileCoordinate(hitY)));
    }

    public static LevelObject getInteractObjectHit(Level level, int hitX, int hitY, int layerID, Predicate<LevelObject> filter, LevelObject defaultReturn) {
        if (Settings.useTileObjectHitboxes) {
            int tileY;
            int tileX;
            ArrayList<LevelObject> list;
            LevelObject lo = null;
            lo = layerID == -1 ? ((list = level.objectLayer.getHitboxPriorityList(tileX = GameMath.getTileCoordinate(hitX), tileY = GameMath.getTileCoordinate(hitY), true)).isEmpty() ? new LevelObject(level, 0, tileX, tileY) : list.get(0)) : new LevelObject(level, layerID, GameMath.getTileCoordinate(hitX), GameMath.getTileCoordinate(hitY));
            if (lo != null && (filter == null || filter.test(lo))) {
                return lo;
            }
            return defaultReturn;
        }
        int startTileX = GameMath.getTileCoordinate(hitX - 100);
        int startTileY = GameMath.getTileCoordinate(hitY - 50);
        int endTileX = GameMath.getTileCoordinate(hitX + 100);
        int endTileY = GameMath.getTileCoordinate(hitY + 200);
        ObjectHoverHitbox best = null;
        for (int currentTileY = endTileY; currentTileY >= startTileY; --currentTileY) {
            for (int currentTileX = startTileX; currentTileX <= endTileX; ++currentTileX) {
                if (layerID == -1) {
                    ArrayList<LevelObject> list = level.objectLayer.getHitboxPriorityList(currentTileX, currentTileY, true);
                    for (LevelObject lo : list) {
                        if (filter != null && !filter.test(lo)) continue;
                        for (ObjectHoverHitbox box : lo.getHoverHitboxes()) {
                            if (!box.contains(hitX, hitY) || best != null && best.sortY >= box.sortY) continue;
                            best = box;
                        }
                    }
                    continue;
                }
                LevelObject lo = new LevelObject(level, layerID, currentTileX, currentTileY);
                if (filter != null && !filter.test(lo)) continue;
                for (ObjectHoverHitbox box : lo.getHoverHitboxes()) {
                    if (!box.contains(hitX, hitY) || best != null && best.sortY >= box.sortY) continue;
                    best = box;
                }
            }
            if (best != null) break;
        }
        if (best != null) {
            return new LevelObject(level, best.layerID, best.tileX, best.tileY);
        }
        return defaultReturn;
    }

    public static <T, R> Iterable<R> mapIterable(Iterator<T> iterator, Function<T, R> mapper) {
        return () -> GameUtils.mapIterator(iterator, mapper);
    }

    public static <T, R> Iterator<R> mapIterator(final Iterator<T> iterator, final Function<T, R> mapper) {
        return new Iterator<R>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(iterator.next());
            }
        };
    }

    public static <T> Stream<? extends T> concat(Stream<? extends T> ... streams) {
        if (streams.length == 0) {
            return Stream.empty();
        }
        Stream<? extends T> current = streams[0];
        for (int i = 1; i < streams.length; ++i) {
            current = Stream.concat(current, streams[i]);
        }
        return current;
    }

    public static <T> T[] concat(T[] a, T[] b) {
        T[] out = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    public static <T, R> R[] mapArray(T[] array, R[] c, Function<T, R> mapper) {
        R[] out = Arrays.copyOf(c, array.length);
        for (int i = 0; i < out.length; ++i) {
            out[i] = mapper.apply(array[i]);
        }
        return out;
    }

    public static <T> Iterator<T> concatIterators(Collection<Iterator<? extends T>> iterators) {
        return new ConcatIterator<T>(iterators);
    }

    @SafeVarargs
    public static <T> Iterator<T> concatIterators(Iterator<? extends T> ... iterators) {
        return new ConcatIterator<T>(iterators);
    }

    public static <T> Iterator<T> array2DIterator(T[][] array) {
        LinkedList<Iterator<T>> iterators = new LinkedList<Iterator<T>>();
        for (T[] ts : array) {
            iterators.add(GameUtils.arrayIterator(ts));
        }
        return GameUtils.concatIterators(iterators);
    }

    public static <T> Iterator<T> arrayIterator(T[] array) {
        return new ArrayIterator<T>(array);
    }

    public static long longHash(Object ... objects) {
        if (objects == null) {
            return 0L;
        }
        long hash = 1L;
        for (Object element : objects) {
            hash = 31L * hash + (long)(element == null ? 0 : element.hashCode());
        }
        return hash;
    }

    public static String toValidFileName(String name) {
        for (String invalidSequence : invalidFileNameSequences) {
            name = name.replace(invalidSequence, "");
        }
        return name;
    }

    public static String formatFileExtension(String file, String defaultExtension) {
        String fileName;
        try {
            fileName = new File(file).getName();
        }
        catch (Exception e) {
            fileName = file;
        }
        if (!fileName.contains(".")) {
            file = file + "." + defaultExtension;
        }
        return file;
    }

    public static String getFileExtension(String file) {
        String fileName;
        if (file == null) {
            return null;
        }
        try {
            fileName = new File(file).getName();
        }
        catch (Exception e) {
            fileName = file;
        }
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            return fileName.substring(i + 1);
        }
        return null;
    }

    public static String removeFileExtension(String file) {
        String extension = GameUtils.getFileExtension(file);
        if (extension != null) {
            return file.substring(0, file.length() - extension.length() - 1);
        }
        return file;
    }

    public static void openURL(String url) {
        try {
            GameUtils.openURL(new URI(url));
        }
        catch (URISyntaxException e) {
            System.err.println("Could not open invalid url :" + url);
        }
    }

    public static void openURL(URI uri) {
        System.out.println("Opening URL " + uri.toString());
        Thread thread = new Thread(() -> {
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(uri);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public static boolean openExplorerAtFile(File file) {
        if (file.exists()) {
            Thread thread = new Thread(() -> {
                try {
                    if (Platform.get() == Platform.WINDOWS) {
                        Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
                    } else {
                        File dir = file.getParentFile();
                        if (dir.exists() && dir.isDirectory() && Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(dir);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            return true;
        }
        GameLog.warn.println("Tried to open non existing file: " + file.getAbsolutePath());
        return false;
    }

    public static class MetricValue {
        public final String suffix;
        public final BigDecimal divider;

        public MetricValue(String suffix, BigDecimal divider) {
            this.suffix = suffix;
            this.divider = divider;
        }

        public MetricValue(String suffix, int exponent) {
            this(suffix, GameUtils.exponent(BigDecimal.ONE, exponent));
        }
    }

    private static class ConcatIterator<T>
    implements Iterator<T> {
        public final LinkedList<Iterator<? extends T>> iterators = new LinkedList();

        public ConcatIterator(Collection<Iterator<? extends T>> iterators) {
            this.iterators.addAll(iterators);
        }

        @SafeVarargs
        public ConcatIterator(Iterator<? extends T> ... iterators) {
            this(Arrays.asList(iterators));
        }

        private Iterator<? extends T> nextIterator() {
            while (!this.iterators.isEmpty()) {
                if (this.iterators.getFirst().hasNext()) {
                    return this.iterators.getFirst();
                }
                this.iterators.removeFirst();
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return this.nextIterator() != null;
        }

        @Override
        public T next() {
            Iterator<T> iterator = this.nextIterator();
            if (iterator != null) {
                return iterator.next();
            }
            throw new NoSuchElementException();
        }
    }

    private static class ArrayIterator<T>
    implements Iterator<T> {
        public final T[] array;
        public int i;

        public ArrayIterator(T[] array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return this.i < this.array.length;
        }

        @Override
        public T next() {
            return this.array[this.i++];
        }
    }
}

