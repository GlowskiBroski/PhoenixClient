package com.phoenixclient.util.file;

import java.io.File;

public class FileUtil {

    private static void createFolder(String path) {
        File folder = new File(path);
        folder.mkdir();
    }

    public static void createFolderPath(String path) {
        String[] folderSet = path.split("/");
        String currentPath = "";
        for (String folder : folderSet) {
            currentPath += folder + "/";
            createFolder(currentPath);
        }
    }

    public static void createFolderPath(File file) {
        createFolderPath(file.getPath().replace("\\", "/"));
    }

}
