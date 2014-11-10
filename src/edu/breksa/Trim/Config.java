package edu.breksa.Trim;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.List;

public class Config {
    @Option(name = "--recursive", aliases = "-r", usage = "If Trim should search recursively")
    public Boolean recursive = false;
    @Option(name = "--recursion-level", aliases = "-rl", usage = "The number of directories Trim will go down recursively. 0 = no limit")
    public int recursionlevel = 0;
    @Option(name = "--remove-empty-lines", aliases = "-rel", usage = "Remove empty lines")
    public Boolean rel = false;
    @Option(name = "--trim-lines", aliases = "-tl", usage = "Trime each line")
    public Boolean tl = false;
    @Option(name = "--file-extensions", aliases = "-fe", usage = "A comma-delimited list of file extensions (not case sensitive)")
    public String extentionString = "";
    @Argument(required = true)
    List<String> files = new ArrayList<String>();
}