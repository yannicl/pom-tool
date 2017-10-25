package com.yannic.tool;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

public class PomReader extends DefaultHandler {

    private final static Logger log = Logger.getLogger(PomReader.class.getName());

    boolean isGroupId = false;
    boolean isArtefactId = false;
    boolean isVersion = false;

    boolean isProjectGroupId = false;
    boolean isProjectArtefactId = false;
    boolean isProjectVersion = false;

    boolean isParentGroupId = false;
    boolean isParentArtefactId = false;
    boolean isParentVersion = false;

    File currentFile;

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
    }

    Dependency dependency;
    Dependency project;
    Dependency parent;
    DependencyRepository repository = new DependencyRepository();
    DependencyRepository projectRepository = new DependencyRepository();

    public void resetState() {
        isGroupId = false; isArtefactId = false; isVersion = false;
        isProjectGroupId = false; isProjectArtefactId = false; isProjectVersion = false;
        isParentGroupId = false; isParentArtefactId = false; isParentVersion = false;
    }

    public void endProject() {
        if (project != null) {
            if (parent != null) {
                if (project.getGroupId() == null) {
                    project.setGroupId(parent.getGroupId());
                }
                if (project.getVersion() == null) {
                    project.setVersion(parent.getVersion());
                }
            }
            projectRepository.registerUnique(project);
            project = null;
        }
        resetState();
    }

    public void startElement(String uri, String localName,String qName,
                             Attributes attributes) throws SAXException {

        //System.out.println("Start Element :" + qName);

        if (qName.equalsIgnoreCase("dependency")) {
            resetState();
            dependency = new Dependency();
            dependency.setLocation(currentFile);
        }

        if (qName.equalsIgnoreCase("parent")) {
            resetState();
            parent = new Dependency();
            parent.setLocation(currentFile);
        }

        if (qName.equalsIgnoreCase("project")) {
            resetState();
            project = new Dependency();
            project.setLocation(currentFile);
        }

        if (qName.equalsIgnoreCase("dependencies")) {
            endProject();
            dependency = null;
        }

        if (qName.equalsIgnoreCase("build")) {
            endProject();
            dependency = null;
        }

        if (qName.equalsIgnoreCase("groupId")) {
            if (dependency != null) {
                isGroupId = true;
            } else if (project != null) {
                isProjectGroupId = true;
            } else if (parent != null) {
                isParentGroupId = true;
            }
        }

        if (qName.equalsIgnoreCase("artifactId")) {
            if (dependency != null) {
                isArtefactId = true;
            } else if (project != null) {
                isProjectArtefactId = true;
            } else if (parent != null) {
                isParentArtefactId = true;
            }
        }

        if (qName.equalsIgnoreCase("version")) {
            if (dependency != null) {
                isVersion = true;
            } else if (project != null) {
                isProjectVersion = true;
            } else if (parent != null) {
                isParentVersion = true;
            }
        }


    }

    public void endElement(String uri, String localName,
                           String qName) throws SAXException {

        //System.out.println("End Element :" + qName);

        if (qName.equalsIgnoreCase("dependency")) {
            repository.register(dependency);
            dependency = null;
        }

        if (qName.equalsIgnoreCase("parent")) {
            repository.register(parent);
        }

        if (qName.equalsIgnoreCase("project")) {
            endProject();
            dependency = null;
        }

    }

    public void printRepo() {
        System.out.println(this.repository.toString());
    }

    public void printProjectRepo() {
        System.out.println(this.projectRepository.toString());
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

        if (isProjectGroupId) {
            project.setGroupId(new String(ch, start, length));
            isProjectGroupId = false;
        }

        if (isProjectArtefactId) {
            project.setArtefactId(new String(ch, start, length));
            isProjectArtefactId = false;
        }

        if (isProjectVersion) {
            project.setVersion(new String(ch, start, length));
            isProjectVersion = false;
        }

        if (isParentGroupId) {
            parent.setGroupId(new String(ch, start, length));
            isParentGroupId = false;
        }

        if (isParentArtefactId) {
            parent.setArtefactId(new String(ch, start, length));
            isParentArtefactId = false;
        }

        if (isParentVersion) {
            parent.setVersion(new String(ch, start, length));
            isParentVersion = false;
        }

    }

    public void readAllFiles(Collection<File> files) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        log.info(files.size() + " files to read");
        for(File file: files) {
            this.setCurrentFile(file);
            saxParser.parse(file, this);
        }
    }

    public DependencyRepository getRepository() {
        return repository;
    }

    public DependencyRepository getProjectRepository() {
        return projectRepository;
    }

}