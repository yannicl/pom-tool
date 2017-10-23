package com.yannic.tool;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by yannic on 20/10/17.
 */
public class DependencyRepository extends TreeSet<Dependency> {

    private final static Logger log = Logger.getLogger(DependencyAnalyser.class.getName());

    private final Set<String> artefacts = new TreeSet<String>();

    public String toString() {
        StringBuffer array = new StringBuffer();
        for(Dependency dependency : this) {
            array.append(dependency.toString()).append("\n");
        }
        return array.toString();
    }

    public boolean containsArtefact(String artefactName) {
        return artefacts.contains(artefactName);
    }

    public Dependency getArtefact(String artefactName) {
        for(Dependency dependency : this) {
            if (dependency.getArtefactName().equals(artefactName)) {
                return dependency;
            }
        }
        return null;
    }

    public void register(Dependency dependency) {
        artefacts.add(dependency.getArtefactName());
        this.add(dependency);
    }

    public void registerUnique(Dependency dependency) {
        if (artefacts.contains(dependency.getArtefactName())) {
            log.severe("Must be a unique artefact: " + dependency);
        }
        register(dependency);
    }

    public void registerAllFromList(File listFile) throws IOException {
        List<String> list = FileUtils.readLines(listFile, Charset.defaultCharset());
        for (String s : list) {
            Dependency d = Dependency.createFromString(s);
            this.registerUnique(d);
        }
    }


}
