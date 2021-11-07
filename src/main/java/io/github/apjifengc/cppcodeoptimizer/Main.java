package io.github.apjifengc.cppcodeoptimizer;

import br.com.criativasoft.cpluslibparser.SourceParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class Main {
    private static final String USAGE = "Usage: <file> [-o <output_file>] [-d]";
    private static final String HELP = """
                <file>   Select the input file.
            -h, --help   Show this help message.
            -o, --output Select the output file.
            -d, --debug  Print the debug info.""";
    public static final PrintStream OUTPUT = System.out;
    public static final PrintStream ERROR = System.err;
    private static File inputFile;
    private static File outputFile;
    private static PrintStream output;
    private static boolean debugMode = false;

    private static void parseArguments(String[] args) {
        Queue<String> argsQueue = new ArrayDeque<>(Arrays.asList(args));
        if (argsQueue.isEmpty()) {
            OUTPUT.println(USAGE);

            System.exit(0);
        }
        while (!argsQueue.isEmpty()) {
            String argType = argsQueue.poll();
            switch (argType) {
                case "-h":
                case "--help":
                    OUTPUT.println(USAGE);
                    OUTPUT.println(HELP);
                    System.exit(0);
                case "-o":
                case "--output":
                    if (argsQueue.isEmpty()) {
                        ERROR.println("Error: '-o' expects output file name.");
                        System.exit(-1);
                    }
                    if (outputFile != null) {
                        ERROR.println("Error: Cannot use more than 1 '-o' argument.");
                        System.exit(-1);
                    }
                    try {
                        outputFile = new File(argsQueue.peek());
                        output = new PrintStream(outputFile);
                    } catch (IOException e) {
                        ERROR.printf("Error: Cannot open the output file '%s'.\n", argsQueue.peek());
                        e.printStackTrace();
                        System.exit(-1);
                    }
                    argsQueue.remove();
                    break;
                case "-d":
                case "--debug":
                    debugMode = true;
                    break;
                default:
                    if (argType.startsWith("-")) {
                        ERROR.printf("Error: Unknown argument '%s'.\n", argType);
                        System.exit(-1);
                    } else {
                        inputFile = new File(argType);
                    }
            }
        }
        if (outputFile == null) {
            int pos = inputFile.getName().lastIndexOf('.');
            if (pos == -1) {
                outputFile = new File(inputFile + "_optimized");
            } else {
                String fileName = inputFile.getName().substring(0, pos);
                String fileSuffix = inputFile.getName().substring(pos);
                outputFile = new File(fileName + "_optimized" + fileSuffix);
            }
            try {
                output = new PrintStream(outputFile);
            } catch (FileNotFoundException e) {
                ERROR.printf("Error: Cannot open the output file '%s'.\n", outputFile);
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void log(String info, Object... args) {
        if (debugMode) {
            OUTPUT.printf(info + "\n", args);
        }
    }

    public static void main(String[] args) {
        parseArguments(args);
        log("Start parsing...");
        SourceParser parser = new SourceParser();
        parser.parse(inputFile);
        log("Output done.");
    }
}
