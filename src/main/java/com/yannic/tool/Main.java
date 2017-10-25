package com.yannic.tool;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by yannic on 21/10/17.
 */
public class Main {

    PomFinder pomFinder = new PomFinder();
    PomReader pomReader = new PomReader();
    DependencyAnalyser analyser = new DependencyAnalyser();
    DependencyRepository ref = new DependencyRepository();

    public Main() {

    }

    /**
     *
     */

    public static void main(String args[]) {

        Main main = new Main();

        // create Options object
        Options options = new Options();

        Option help = new Option( "h", "help", false, "print this message" );
        Option dir = new Option( "d", "dir", true, "parent directory" );
        dir.setRequired(true);
        Option list = new Option( "l", "list", false, "list all poms" );
        Option files = new Option( "f", "files", true, "file containing the list of all poms" );
        Option analyze = new Option( "a", "analyze", false, "analyze all poms" );
        Option create = new Option( "c", "create-ref", false, "create reference list" );
        Option load = new Option( "r", "load-ref", true, "load reference list" );
        Option print = new Option( "v", "print", false, "print all lists" );

        options.addOption(help);
        options.addOption(dir);
        options.addOption(list);
        options.addOption(files);
        options.addOption(load);
        options.addOption(create);
        options.addOption(analyze);
        options.addOption(print);


        try {

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse( options, args);

            if (cmd.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("pom", options);
                return;
            }

            File dirFile = new File(cmd.getOptionValue("dir"));

            if (cmd.hasOption("list")) {
                main.pomFinder.findAllPom(dirFile);
                main.pomFinder.printFileCollection(dirFile);
                return;
            }

            if (cmd.hasOption("files")) {
                main.pomFinder.findAllPomFromList(new File(cmd.getOptionValue("files")), dirFile);
            } else {
                main.pomFinder.findAllPom(dirFile);
            }

            if (cmd.hasOption("load-ref")) {
                main.ref.registerAllFromList(new File(cmd.getOptionValue("load-ref")));
            }

            if (cmd.hasOption("analyze")) {
                main.pomReader.readAllFiles(main.pomFinder.getCollection());
                main.analyser.analyse(main.pomReader.getProjectRepository(), main.pomReader.getRepository(), main.ref);
            }

            if (cmd.hasOption("create-ref")) {
                main.pomReader.readAllFiles(main.pomFinder.getCollection());
                main.pomReader.printProjectRepo();
            }

            if (cmd.hasOption("print")) {
                main.pomReader.printProjectRepo();
                main.pomReader.printRepo();
            }


        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("pom", options);
            e.printStackTrace();
            return;
        }

    }


}
