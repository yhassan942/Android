package com.example.android.util;
import com.example.android.model.AppData;

public class AppState {
    private static AppState instance;
    private AppData data;
    private DataStore store;

    private AppState(){}

    public static AppState get() {
        if(instance == null) instance = new AppState();
        return instance;
    }
    private boolean firstRun = true;

    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean value) {
        firstRun = value;
    }

    public void init(DataStore ds) {
        this.store = ds;
        this.data = ds.load();
    }

    public AppData getData(){ return data; }

    public void save(){ if (store != null && data != null) store.save(data); }
}
