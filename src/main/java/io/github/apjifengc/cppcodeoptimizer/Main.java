package io.github.apjifengc.cppcodeoptimizer;

import br.com.criativasoft.cpluslibparser.LibraryIndex;
import br.com.criativasoft.cpluslibparser.SourceParser;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;

import javax.xml.transform.Source;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Queue;
import java.util.logging.*;
import java.util.stream.Stream;

public class Main {
    public final static Logger LOG = Logger.getLogger("CppCodeOptimizer");

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

    public static void log(Level level, String info, Object... args) {
        if (debugMode) {
            LOG.log(level, info.formatted(args));
        }
    }

    public static void main(String[] args) {
        parseArguments(args);
        LOG.setLevel(Level.ALL);
        LOG.setUseParentHandlers(false);
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                Calendar calendar = Calendar.getInstance();
                return "[%02d:%02d:%02d] [%s] %s\n".formatted(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        calendar.get(Calendar.SECOND),
                        record.getLevel().getName(),
                        record.getMessage()
                );
            }
        };
        StreamHandler handler = new StreamHandler(
                OUTPUT,
                formatter
        );
        handler.setLevel(Level.ALL);
        LOG.addHandler(handler);
        log(Level.INFO, "Start parsing...");
        SourceParser parser = new SourceParser(LOG);
        parser.parse(inputFile);
        System.out.println(parser.getGlobalFunctions());
        log(Level.INFO, "Output done.");
    }
}
