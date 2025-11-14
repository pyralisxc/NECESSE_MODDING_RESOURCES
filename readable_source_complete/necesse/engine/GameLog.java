/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.GlobalData;

public class GameLog {
    public static final PrintStream out = new FileConsoleStream(System.out, "", FormatPrefix.WHITE);
    public static final PrintStream warn = new FileConsoleStream(System.out, "(WARN) ", FormatPrefix.YELLOW);
    public static final PrintStream debug = new FileConsoleStream(System.out, "(DEBUG) ", FormatPrefix.BLUE);
    public static final PrintStream file = new FileConsoleStream(new NoPrintStream(), "", FormatPrefix.WHITE);
    public static final PrintStream err = new FileConsoleStream(System.err, "(ERR) ", FormatPrefix.RED);

    public static void startLogging(boolean logDebug, String ... logFilePaths) {
        System.setOut(out);
        System.setErr(err);
        for (String logFilePath : logFilePaths) {
            GameLog.addLoggingPath(logDebug, logFilePath);
        }
    }

    public static void addLoggingPath(boolean logDebug, String logFilePath) {
        logFilePath = GlobalData.appDataPath() + logFilePath;
        File parent = new File(logFilePath).getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            System.err.println("Could not create log directory: " + parent.getAbsolutePath());
        }
        try {
            File logFile = new File(logFilePath);
            FileOutputStream stream = new FileOutputStream(logFile);
            if (logDebug) {
                ((FileConsoleStream)debug).addFileOut(stream);
            }
            ((FileConsoleStream)out).addFileOut(stream);
            ((FileConsoleStream)warn).addFileOut(stream);
            ((FileConsoleStream)file).addFileOut(stream);
            ((FileConsoleStream)err).addFileOut(stream);
            debug.println("Started logging to: " + logFile.getAbsolutePath());
            if (logDebug) {
                debug.println("Debug file logging enabled");
            }
        }
        catch (FileNotFoundException e) {
            System.err.println("Could not write to log file: " + logFilePath);
        }
    }

    public static void setServerWrite(Consumer<String> serverForm) {
        ((FileConsoleStream)out).serverWrite = serverForm;
        ((FileConsoleStream)warn).serverWrite = serverForm;
        ((FileConsoleStream)err).serverWrite = serverForm;
    }

    private static class FileConsoleStream
    extends PrintStream {
        private List<FileOutputStream> fileOuts = new ArrayList<FileOutputStream>();
        private SimpleDateFormat dateFormat;
        private String formatPrefix;
        private String prefix = "";
        private Consumer<String> serverWrite = null;
        private boolean fileOut;
        private String lineSeparator;
        private boolean nextLine = true;

        public FileConsoleStream(PrintStream fileStream, String prefix, FormatPrefix ... formatPrefixes) {
            super(fileStream);
            if (formatPrefixes.length == 0) {
                this.formatPrefix = FormatPrefix.WHITE.prefix;
            } else {
                this.formatPrefix = "";
                for (FormatPrefix format : formatPrefixes) {
                    this.formatPrefix = this.formatPrefix + format.prefix;
                }
            }
            this.prefix = prefix == null ? "" : prefix;
            this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.lineSeparator = System.getProperty("line.separator");
            if (this.lineSeparator == null || this.lineSeparator.isEmpty()) {
                this.lineSeparator = "\n";
            }
            this.lineSeparator = "\n";
        }

        public void addFileOut(FileOutputStream stream) {
            this.fileOuts.add(stream);
        }

        @Override
        public void flush() {
            super.flush();
            for (FileOutputStream out : this.fileOuts) {
                try {
                    out.flush();
                }
                catch (IOException e) {
                    this.setError();
                }
            }
        }

        @Override
        public synchronized void println(String x) {
            super.println(x);
            this.nextLine = true;
            this.newFileLine();
        }

        @Override
        public synchronized void println() {
            this.println("");
        }

        @Override
        public synchronized void println(boolean x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(char x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(int x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(long x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(float x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(double x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(char[] x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void println(Object x) {
            this.println(String.valueOf(x));
        }

        @Override
        public synchronized void print(boolean b) {
            this.print(b ? "true" : "false");
        }

        @Override
        public synchronized void print(char c) {
            this.print(String.valueOf(c));
        }

        @Override
        public synchronized void print(int i) {
            this.print(String.valueOf(i));
        }

        @Override
        public synchronized void print(long l) {
            this.print(String.valueOf(l));
        }

        @Override
        public synchronized void print(float f) {
            this.print(String.valueOf(f));
        }

        @Override
        public synchronized void print(double d) {
            this.print(String.valueOf(d));
        }

        @Override
        public synchronized void print(Object obj) {
            this.print(String.valueOf(obj));
        }

        @Override
        public synchronized void print(String s) {
            String formatted = this.formatString(s);
            this.fileOut = true;
            super.print(formatted);
            this.fileOut = false;
            super.print(this.formatPrefix + formatted);
            if (this.serverWrite != null) {
                this.serverWrite.accept(this.prefix + s);
            }
        }

        private synchronized void newFileLine() {
            this.fileOut = true;
            super.print("\n");
            this.fileOut = false;
        }

        private String formatString(String s) {
            if (s == null) {
                return null;
            }
            boolean appendLineSeparator = false;
            boolean lastNextLine = this.nextLine;
            this.nextLine = false;
            if (s.endsWith(this.lineSeparator)) {
                appendLineSeparator = true;
                s = s.substring(0, s.length() - this.lineSeparator.length());
                this.nextLine = true;
            }
            String prefix = "[" + this.dateFormat.format(new Date()) + "] " + this.prefix;
            String out = s.replace(this.lineSeparator, this.lineSeparator + prefix);
            if (lastNextLine) {
                out = prefix + out;
            }
            if (appendLineSeparator) {
                out = out + this.lineSeparator;
            }
            return out;
        }

        @Override
        public synchronized void write(byte[] buf, int off, int len) {
            if (this.fileOut) {
                for (FileOutputStream out : this.fileOuts) {
                    try {
                        out.write(buf, off, len);
                    }
                    catch (IOException e) {
                        this.setError();
                    }
                }
            } else {
                super.write(buf, off, len);
            }
        }
    }

    public static enum FormatPrefix {
        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        PURPLE(35),
        CYAN(36),
        GRAY(37),
        WHITE(39);

        private String prefix;

        private FormatPrefix(int id) {
            this.prefix = "\u001b[" + id + "m";
        }
    }

    private static class NoPrintStream
    extends PrintStream {
        public NoPrintStream() {
            super(new OutputStream(){

                @Override
                public void write(int b) throws IOException {
                }
            });
        }
    }
}

