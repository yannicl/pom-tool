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
        Option read = new Option( "r", "read", false, "read all poms" );

        options.addOption(help);
        options.addOption(dir);
        options.addOption(list);
        options.addOption(files);
        options.addOption(read);


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

            if (cmd.hasOption("read")) {
                main.pomReader.readAllFiles(main.pomFinder.getCollection());
                main.analyser.analyse(main.pomReader.getProjectRepository(), main.pomReader.getRepository());
            }


        } catch (Exception e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("pom", options);
            e.printStackTrace();
            return;
        }

    }


}
