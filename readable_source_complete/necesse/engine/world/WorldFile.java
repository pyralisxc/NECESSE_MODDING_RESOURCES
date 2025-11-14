/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;
import necesse.engine.util.GameUtils;

public class WorldFile {
    protected final Path path;

    protected WorldFile(Path path) {
        this.path = path;
    }

    public boolean exists() {
        try {
            return Files.exists(this.path, new LinkOption[0]);
        }
        catch (ClosedFileSystemException e) {
            return false;
        }
    }

    public boolean isDirectory() {
        try {
            return Files.isDirectory(this.path, new LinkOption[0]);
        }
        catch (ClosedFileSystemException e) {
            return false;
        }
    }

    public Iterator<String> iterateFilesInDirectory() throws IOException {
        if (this.exists() && !this.isDirectory()) {
            throw new NotDirectoryException(this.path.toString());
        }
        if (!this.exists()) {
            return Collections.emptyIterator();
        }
        return GameUtils.mapIterator(Files.newDirectoryStream(this.path).iterator(), p -> p.getFileName().toString());
    }

    public Iterable<String> getFilesInDirectory() throws IOException {
        Iterator<String> iterator = this.iterateFilesInDirectory();
        return () -> iterator;
    }

    public Iterator<WorldFile> iteratePathsInDirectory() throws IOException {
        if (this.exists() && !this.isDirectory()) {
            throw new NotDirectoryException(this.path.toString());
        }
        if (!this.exists()) {
            return Collections.emptyIterator();
        }
        return GameUtils.mapIterator(Files.newDirectoryStream(this.path).iterator(), WorldFile::new);
    }

    public Iterable<WorldFile> getPathsInDirectory() throws IOException {
        Iterator<WorldFile> iterator = this.iteratePathsInDirectory();
        return () -> iterator;
    }

    public void copyTo(WorldFile to, CopyOption ... options) throws IOException {
        this.copyTo(to.path, options);
    }

    public void copyTo(Path to, CopyOption ... options) throws IOException {
        if (this.isDirectory()) {
            Files.createDirectory(to, new FileAttribute[0]);
            for (WorldFile worldFile : this.getPathsInDirectory()) {
                worldFile.copyTo(to.resolve(worldFile.getFileName().toString()), options);
            }
        } else {
            Files.copy(this.path, to, options);
        }
    }

    public void moveTo(WorldFile to, CopyOption ... options) throws IOException {
        this.moveTo(to.path, options);
    }

    public void moveTo(Path to, CopyOption ... options) throws IOException {
        LinkedList<Path> deleteDirectories = new LinkedList<Path>();
        this.moveTo(to, deleteDirectories, options);
        while (!deleteDirectories.isEmpty()) {
            Path last = deleteDirectories.removeLast();
            Files.delete(last);
        }
    }

    private void moveTo(Path to, LinkedList<Path> deleteDirectories, CopyOption ... options) throws IOException {
        if (this.isDirectory()) {
            Files.createDirectory(to, new FileAttribute[0]);
            deleteDirectories.add(this.path);
            for (WorldFile worldFile : this.getPathsInDirectory()) {
                worldFile.moveTo(to.resolve(worldFile.getFileName().toString()), deleteDirectories, options);
            }
        } else {
            Files.move(this.path, to, options);
        }
    }

    public BufferedWriter writer(Charset charset, boolean allowOverride) throws IOException {
        if (!allowOverride && this.exists()) {
            return null;
        }
        Path parent = this.path.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        return Files.newBufferedWriter(this.path, charset, new OpenOption[0]);
    }

    public BufferedWriter writer(boolean allowOverride) throws IOException {
        return this.writer(StandardCharsets.UTF_8, allowOverride);
    }

    public void write(byte[] data, boolean allowOverride) throws IOException {
        if (!allowOverride && Files.exists(this.path, new LinkOption[0])) {
            return;
        }
        Path parent = this.path.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        Files.write(this.path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public void write(byte[] data) throws IOException {
        this.write(data, true);
    }

    public byte[] read() throws IOException {
        return Files.readAllBytes(this.path);
    }

    public BufferedReader reader(Charset charset) throws IOException {
        return Files.newBufferedReader(this.path, charset);
    }

    public BufferedReader reader() throws IOException {
        return this.reader(StandardCharsets.UTF_8);
    }

    public InputStream inputStream(OpenOption ... openOptions) throws IOException {
        return Files.newInputStream(this.path, openOptions);
    }

    public OutputStream outputStream(OpenOption ... openOptions) throws IOException {
        return Files.newOutputStream(this.path, openOptions);
    }

    public boolean delete() throws IOException {
        return this.delete(false);
    }

    public boolean delete(boolean deleteDirectoryContent) throws IOException {
        if (Files.isDirectory(this.path, new LinkOption[0]) && deleteDirectoryContent) {
            Files.walkFileTree(this.path, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return Files.deleteIfExists(this.path);
    }

    public FileSystem getFileSystem() {
        return this.path.getFileSystem();
    }

    public boolean isAbsolute() {
        return this.path.isAbsolute();
    }

    public Path getRoot() {
        return this.path.getRoot();
    }

    public Path getFileName() {
        return this.path.getFileName();
    }

    public Path getParent() {
        return this.path.getParent();
    }

    public int getNameCount() {
        return this.path.getNameCount();
    }

    public Path getName(int index) {
        return this.path.getName(index);
    }

    public Path subpath(int beginIndex, int endIndex) {
        return this.path.subpath(beginIndex, endIndex);
    }

    public boolean startsWith(Path other) {
        return this.path.startsWith(other);
    }

    public boolean startsWith(String other) {
        return this.path.startsWith(other);
    }

    public boolean endsWith(Path other) {
        return this.path.endsWith(other);
    }

    public boolean endsWith(String other) {
        return this.path.endsWith(other);
    }

    public Path normalize() {
        return this.path.normalize();
    }

    public Path resolve(Path other) {
        return this.path.resolve(other);
    }

    public Path resolve(String other) {
        return this.path.resolve(other);
    }

    public Path resolveSibling(Path other) {
        return this.path.resolveSibling(other);
    }

    public Path resolveSibling(String other) {
        return this.path.resolveSibling(other);
    }

    public Path relativize(Path other) {
        return this.path.relativize(other);
    }

    public URI toUri() {
        return this.path.toUri();
    }

    public Path toAbsolutePath() {
        return this.path.toAbsolutePath();
    }

    public Path toRealPath(LinkOption ... options) throws IOException {
        return this.path.toRealPath(options);
    }

    public File toFile() {
        return this.path.toFile();
    }

    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier ... modifiers) throws IOException {
        return this.path.register(watcher, events, modifiers);
    }

    public WatchKey register(WatchService watcher, WatchEvent.Kind<?> ... events) throws IOException {
        return this.path.register(watcher, events);
    }

    public Iterator<Path> iterator() {
        return this.path.iterator();
    }

    public int compareTo(Path other) {
        return this.path.compareTo(other);
    }

    public void forEach(Consumer<? super Path> action) {
        this.path.forEach(action);
    }

    public Spliterator<Path> spliterator() {
        return this.path.spliterator();
    }

    public String toString() {
        return this.path.toString();
    }
}

