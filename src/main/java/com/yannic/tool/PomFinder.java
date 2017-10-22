package com.yannic.tool;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yannic on 21/10/17.
 */
public class PomFinder {

    private Collection<File> collection;

    public Collection<File> findAllPom(File dir) {
        Collection<File> files = FileUtils.listFiles(
                dir,
                new RegexFileFilter("pom.xml"),
                FileFilterUtils.makeDirectoryOnly(HiddenFileFilter.VISIBLE)
        );
        this.collection = files;
        return files;
    }

    public Collection<File> findAllPomFromList(File listFile, File parent) throws IOException {
        List<String> list = FileUtils.readLines(listFile, Charset.defaultCharset());
        Collection<File> files = new ArrayList<File>(list.size());
        for (String s : list) {
            files.add(new File(parent, s));
        }
        this.collection = files;
        return files;
    }

    public void printFileCollection(File parent) {
        for (File f : collection) {
            System.out.println(getRelativePath(f, parent));
        }
    }

    // returns null if file isn't relative to folder
    public static String getRelativePath(File file, File folder) {
        String filePath = file.getAbsolutePath();
        String folderPath = folder.getAbsolutePath();
        if (filePath.startsWith(folderPath)) {
            return filePath.substring(folderPath.length() + 1);
        } else {
            return null;
        }
    }

    public Collection<File> getCollection() {
        return collection;
    }
}
