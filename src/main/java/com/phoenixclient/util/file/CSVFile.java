package com.phoenixclient.util.file;

import java.io.*;
import java.util.*;

public class CSVFile {

    private String folderPath;
    private String fileName;

    public CSVFile(String folderPath, String fileName) {
        this.folderPath = folderPath;
        this.fileName = fileName.replace(".csv","") + ".csv";
    }

    public <T> boolean save(ArrayList<T[]> list) {
        FileUtil.createFolderPath(folderPath); //Creates the folder path if it does not exist
        String outputFile = folderPath + (folderPath.isEmpty() ? "" : "/") + fileName;
        try {
            FileWriter writer = new FileWriter(outputFile);
            for (T[] rowData : list) {
                writer.write(String.join(",", (String[]) rowData) + "\n");
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing data to " + outputFile);
            e.printStackTrace();
            return false;
        }
    }

    //TODO: Find a way to make this work better with the chunks mod. Find a way to get K back to K[]
    public <T, K> boolean save(HashMap<T,K> hashMap) {
        FileUtil.createFolderPath(folderPath); //Creates the folder path if it does not exist
        String outputFile = folderPath + (folderPath.isEmpty() ? "" : "/") + fileName;
        try {
            FileWriter writer = new FileWriter(outputFile);

            for (T key : hashMap.keySet()) {
                //writer.write(key + "," + Arrays.toString(hashMap.get(key)).replace("[","").replace("]","") + "\n");
                writer.write(key + "," + hashMap.get(key) + "\n");
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error writing data to " + outputFile);
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String[]> getData() {
        File file = new File(folderPath + (folderPath.isEmpty() ? "" : "/") + fileName);
        ArrayList<String[]> rawDataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                rawDataList.add(row);
            }
        } catch (IOException e) {
            System.out.println("Error reading data from " + file.getName());
            e.printStackTrace();
        }
        return rawDataList;
    }

    public HashMap<String, String[]> getDataAsMap() {
        File file = new File(folderPath + (folderPath.isEmpty() ? "" : "/") + fileName);

        HashMap<String,String[]> rawDataHashMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                String key = row[0];
                String[] rowCut = line.replace(key + ",", "").split(",");
                rawDataHashMap.put(row[0], rowCut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawDataHashMap;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }


    public String getFileName() {
        return fileName;
    }

    public String getFolderPath() {
        return folderPath;
    }
}
