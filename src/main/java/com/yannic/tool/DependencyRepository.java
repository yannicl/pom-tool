package com.yannic.tool;

import java.util.TreeSet;

/**
 * Created by yannic on 20/10/17.
 */
public class DependencyRepository extends TreeSet<Dependency> {

    public String toString() {
        StringBuffer array = new StringBuffer();
        for(Dependency dependency : this) {
            array.append(dependency.toString()).append("\n");
        }
        return array.toString();
    }


}
