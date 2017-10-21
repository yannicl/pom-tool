package com.yannic.tool;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.Collection;

public class ReadXMLFile extends DefaultHandler {

    boolean isGroupId = false;
    boolean isArtefactId = false;
    boolean isVersion = false;

    Dependency dependency;
    DependencyRepository repository = new DependencyRepository();

    public void resetState() {
        isGroupId = false; isArtefactId = false; isVersion = false;
        dependency = new Dependency();
    }

    public void startElement(String uri, String localName,String qName,
                             Attributes attributes) throws SAXException {

        //System.out.println("Start Element :" + qName);

        if (qName.equalsIgnoreCase("dependency")) {
            resetState();
        }

        if (qName.equalsIgnoreCase("groupId")) {
            if (dependency != null) {
                isGroupId = true;
            }
        }

        if (qName.equalsIgnoreCase("artifactId")) {
            if (dependency != null) {
                isArtefactId = true;
            }
        }

        if (qName.equalsIgnoreCase("version")) {
            if (dependency != null) {
                isVersion = true;
            }
        }


    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        //System.out.println("End Element :" + qName);

        if (qName.equalsIgnoreCase("dependency")) {
            repository.add(dependency);
            dependency = null;
        }

    }

    public void printRepo() {
        System.out.println(this.repository.toString());
    }

    public void characters(char ch[], int start, int length) throws SAXException {

        if (isGroupId) {
            dependency.setGroupId(new String(ch, start, length));
            isGroupId = false;
        }

        if (isArtefactId) {
            dependency.setArtefactId(new String(ch, start, length));
            isArtefactId = false;
        }

        if (isVersion) {
            dependency.setVersion(new String(ch, start, length));
            isVersion = false;
        }

    }

    public static void main(String argv[]) {

        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            ReadXMLFile handler = new ReadXMLFile();

            Collection<File> files = FileUtils.listFiles(
                    new File("src"),
                    new RegexFileFilter("pom.xml"),
                    FileFilterUtils.makeDirectoryOnly(HiddenFileFilter.VISIBLE)
            );

            for(File file: files) {
                saxParser.parse(file, handler);
            }

            handler.printRepo();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}