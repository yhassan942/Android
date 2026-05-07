package com.example.android.util;
import android.content.Context;
import com.example.android.model.AppData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class DataStore {
    private static final String FILE_NAME = "photos_data.json";
    private final Context ctx;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DataStore(Context context){ this.ctx = context.getApplicationContext(); }

    public AppData load() {
        try (FileInputStream fis = ctx.openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            String json = sb.toString();
            AppData data = gson.fromJson(json, AppData.class);
            return data == null ? new AppData() : data;
        } catch (FileNotFoundException fnf) {
            return new AppData();
        } catch (IOException e) {
            e.printStackTrace();
            return new AppData();
        }
    }

    public void save(AppData data) {
        try (FileOutputStream fos = ctx.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             OutputStreamWriter osw = new OutputStreamWriter(fos)) {
            osw.write(gson.toJson(data));
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
