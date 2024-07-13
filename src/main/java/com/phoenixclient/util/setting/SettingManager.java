package com.phoenixclient.util.setting;

import com.phoenixclient.util.file.CSVFile;
import com.phoenixclient.util.math.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingManager {

    public static final SettingManager DEFAULT_MANAGER = new SettingManager(new CSVFile("setting", "settings"));

    private final HashMap<String, Setting<?>> settingList = new HashMap<>();
    private final CSVFile csvFile;

    public SettingManager(CSVFile csvFile) {
        this.csvFile = csvFile;
    }

    @SuppressWarnings(value = "all")
    public boolean saveAll() {
        ArrayList<String[]> dataList = new ArrayList<>();

        for (Setting<?> setting : getSettingList().values()) {
            String[] settingDataList = {setting.getKey(), setting.get().toString(), setting.getType()};
            dataList.add(settingDataList);
        }
        return getCSVFile().save(dataList);
    }

    @SuppressWarnings(value = "all")
    public boolean loadAll() {
        ArrayList<String[]> dataList = getCSVFile().getData();

        try {
            for (String[] data : dataList) {
                try {
                    Setting setting = getSettingList().get(data[0]);
                    String savedVal = data[1];
                    String datatype = data[2];

                    if (datatype.equals("unloadable")) continue;

                    if (savedVal.equals("null") || datatype.equals("nullType")) setting.set(null);
                    else if (datatype.equals("string")) setting.set(savedVal);
                    else if (datatype.equals("integer")) setting.set(Integer.parseInt(savedVal));
                    else if (datatype.equals("float")) setting.set(Float.parseFloat(savedVal));
                    else if (datatype.equals("double")) setting.set(Double.parseDouble(savedVal));
                    else if (datatype.equals("boolean")) setting.set(Boolean.parseBoolean(savedVal));
                    else if (datatype.equals("vector")) {
                        String[] vecVals = savedVal.replace("<", "").replace(">", "").split("\\|");
                        setting.set(new Vector(Double.parseDouble(vecVals[0]), Double.parseDouble(vecVals[1]), Double.parseDouble(vecVals[2])));
                    } else if (datatype.equals("color")) {
                        String[] vecVals = savedVal.split("\\[")[1].replace("]", "").replace("r=", "").replace("g=", "").replace("b=", "").split(",");
                        setting.set(new Color(Integer.parseInt(vecVals[0]), Integer.parseInt(vecVals[1]), Integer.parseInt(vecVals[2]), Integer.parseInt(vecVals[3])));
                    }
                    //If we reach here, the setting will be set to its default value as it is un-readable
                } catch (Exception e) {
                    //The specific setting could not be found
                    e.printStackTrace();
                    continue;
                }
            }
            return true;
        } catch (Exception e) {
            //If the data cannot be set, return false
            return false;
        }
    }


    public HashMap<String, Setting<?>> getSettingList() {
        return settingList;
    }

    public CSVFile getCSVFile() {
        return csvFile;
    }

}
