package com.yannic.tool;

/**
 * Created by yannic on 20/10/17.
 */
public class Dependency implements Comparable<Dependency> {

    String groupId = null;
    String artefactId = null;
    String version = null;

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

}
