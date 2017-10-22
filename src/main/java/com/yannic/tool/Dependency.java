package com.yannic.tool;

import java.io.File;
import java.io.IOException;

/**
 * Created by yannic on 20/10/17.
 */
public class Dependency implements Comparable<Dependency> {

    String groupId = null;
    String artefactId = null;
    String version = null;

    String location;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtefactId() {
        return artefactId;
    }

    public void setArtefactId(String artefactId) {
        this.artefactId = artefactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toString() {
        if (version != null) {
            return groupId + ":" + artefactId + ":" + version;
        } else {
            return groupId + ":" + artefactId + ":inherited";
        }
    }

    public int compareTo(Dependency o) {
        return toString().compareTo(o.toString());
    }

    public boolean isSnapshot() {
        return version.endsWith("SNAPSHOT");
    }

    public String getArtefactName() {
        return groupId + ":" + artefactId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLocation(File location) {
        try {
            this.location = location.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
