package edu.breksa.Trim;

import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static Config config = new Config();
    public static List<String> files = new ArrayList<String>();
    public static int emptylines = 0;
    public static int trimedlines = 0;
    public static int processedfiles = 0;
    public static int scannedfiles = 0;
    public static CmdLineParser cmdLineParser = new CmdLineParser(config);
    public static String newline = System.getProperty("line.separator");
    public static List<String> extentions = new ArrayList<String>();


    public static void badBye() {
        System.out.println("Usage: java -jar Trim.jar [file1, file2, etc] [options]");
        cmdLineParser.printUsage(System.out);
        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            badBye();
        }
        try {
            cmdLineParser.parseArgument(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            badBye();
        }
        if (config.files.isEmpty()) {
            badBye();
        }
        if (!config.rel && !config.tl) {
            badBye();
        }
        if (config.recursive && config.extentionString.equals("")) {
            System.err.println("You need to provide atleast one extension in order to use Trim recursively");
            System.exit(0);
        } else {
            extentions = Arrays.asList(config.extentionString.split("\\s*,\\s*"));
        }
        for (String string : config.files) {
            File file = new File(string);
            if (!file.exists()) {
                System.err.println("File \"" + file.getAbsolutePath() + "\" does not exist...");
                continue;
            }
            filterFile(file, 0);
        }
        if (files.isEmpty()) {
            System.err.println("No valid files provided/found.");
        }
        for (String string : files) {
            if (processFile(string)) {
                scannedfiles++;
            }
        }
        if (scannedfiles == 0) {
            System.out.println("Scanned 0 files.");
            return;
        }
        System.out.println(String.format("Processed %s of %s file(s)[%s], removed %s empty lines and trimmed %s lines.", processedfiles, scannedfiles, (new DecimalFormat("#%").format((double) processedfiles / (double) scannedfiles)), emptylines, trimedlines));
    }

    private static Boolean processFile(String filepath) {
        String relativePath = new File(System.getProperty("user.dir")).toURI().relativize(new File(filepath).toURI()).getPath();
        System.out.println("Working with file: \"" + relativePath + "\"...");
        try {
            File newFile = new File(filepath);
            FileReader fr = new FileReader(newFile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            String tmpLine;
            Boolean changed = false;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (config.rel) {
                    if (line.equals("")) {
                        emptylines++;
                        changed = true;
                        continue;
                    }
                }
                if (config.tl) {
                    tmpLine = line;
                    if (!tmpLine.trim().equals(line)) {
                        line = line.trim();
                        trimedlines++;
                        changed = true;
                    }
                }
                if (config.rel) {
                    if (line.equals("")) {
                        continue;
                    }
                }
                stringBuilder.append(line + newline);
            }
            fr.close();
            if (!changed) {
                return true;
            }
            File backupFile = new File(filepath.concat(".bk"));
            if (backupFile.exists()) {
                System.err.println("A backup file for \"" + filepath + "\" already exists, not writing changes.");
                return true;
            }
            File oldFile = new File(filepath);
            oldFile.renameTo(backupFile);
            String string = stringBuilder.toString();
            FileWriter fw = new FileWriter(newFile);
            fw.write(string, 0, string.length());
            fw.close();
            if (changed) {
                processedfiles++;
            }
            return true;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    private static void filterFile(File file, int level) {
        if (file.isDirectory()) {
            if (!Main.config.recursive) {
                return;
            }
            if (config.recursionlevel != 0) {
                if (level > config.recursionlevel) {
                    return;
                }
            }
            if (file.listFiles() == null) {
                return;
            }
            for (File subfile : file.listFiles()) {
                filterFile(subfile, level + 1);
            }
            return;
        }
        if (!extentions.contains("*")) {
            for (String ext : extentions) {
                if (!ext.equalsIgnoreCase(getFileExtension(file))) {
                    return;
                }
            }
        }
        if (!files.contains(file.getAbsolutePath())) {
            files.add(file.getAbsolutePath());
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

}
