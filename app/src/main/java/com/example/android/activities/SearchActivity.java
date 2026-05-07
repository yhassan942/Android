package com.example.android.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android.R;
import com.example.android.adapters.SearchResultsAdapter;
import com.example.android.model.Album;
import com.example.android.model.Photo;
import com.example.android.model.Tag;
import com.example.android.util.AppState;
import java.util.*;

public class SearchActivity extends AppCompatActivity {
    private AutoCompleteTextView actv;
    private RadioGroup rgType;
    private Button btnAddCriteria, btnSearch;
    private ToggleButton toggleAnd;
    private ListView lvCriteria;
    private ArrayAdapter<String> criteriaAdapter;
    private List<Pair<Tag.Type, String>> criteriaList = new ArrayList<>();
    private Map<Tag.Type, Set<String>> valueIndex = new HashMap<>();
    private RecyclerView rvResults;
    private SearchResultsAdapter resultsAdapter;
    private List<SearchResultsAdapter.SearchResult> results = new ArrayList<>();

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_search);

        actv = findViewById(R.id.actvValue);
        rgType = findViewById(R.id.rgType);
        btnAddCriteria = findViewById(R.id.btnAddCriteria);
        btnSearch = findViewById(R.id.btnDoSearch);
        toggleAnd = findViewById(R.id.toggleAnd);
        lvCriteria = findViewById(R.id.lvCriteria);
        rvResults = findViewById(R.id.rvResults);
        rvResults.setLayoutManager(new LinearLayoutManager(this));

        criteriaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvCriteria.setAdapter(criteriaAdapter);

        buildValueIndex();

        btnAddCriteria.setOnClickListener(v -> {
            Tag.Type type = rgType.getCheckedRadioButtonId() == R.id.rbPerson ? Tag.Type.PERSON : Tag.Type.LOCATION;
            String val = actv.getText().toString().trim();
            if (val.isEmpty()) return;
            criteriaList.add(new Pair<>(type, val));
            criteriaAdapter.add(type.name() + ": " + val);
            actv.setText("");
        });

        btnSearch.setOnClickListener(v -> {
            boolean isAnd = toggleAnd.isChecked();
            results.clear();
            performSearch(isAnd);
            resultsAdapter = new SearchResultsAdapter(this, results, pos -> {
                // open in PhotoActivity
                SearchResultsAdapter.SearchResult r = results.get(pos);
                Intent i = new Intent(SearchActivity.this, PhotoActivity.class);
                i.putExtra("albumName", r.albumName);
                i.putExtra("photoIndex", r.photoIndex);
                startActivity(i);
            });
            rvResults.setAdapter(resultsAdapter);
        });

        lvCriteria.setOnItemLongClickListener((parent, view, position, id) -> {
            criteriaList.remove(position);
            criteriaAdapter.remove(criteriaAdapter.getItem(position));
            return true;
        });

        actv.setThreshold(1);
        actv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>()));
        rgType.setOnCheckedChangeListener((g, checkedId) -> updateAutoCompleteSource());
        updateAutoCompleteSource();

        findViewById(R.id.btnBackFromSearch).setOnClickListener(v -> finish());
    }

    private void buildValueIndex() {
        valueIndex.clear();
        valueIndex.put(Tag.Type.PERSON, new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
        valueIndex.put(Tag.Type.LOCATION, new TreeSet<>(String.CASE_INSENSITIVE_ORDER));
        for (Album a : AppState.get().getData().getAlbums()) {
            for (Photo p : a.getPhotos()) {
                for (Tag t : p.getTags()) {
                    valueIndex.get(t.getType()).add(t.getValue());
                }
            }
        }
    }

    private void updateAutoCompleteSource() {
        Tag.Type current = rgType.getCheckedRadioButtonId() == R.id.rbPerson ? Tag.Type.PERSON : Tag.Type.LOCATION;
        Set<String> vals = valueIndex.getOrDefault(current, Collections.emptySet());
        ArrayAdapter<String> ada = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(vals));
        actv.setAdapter(ada);
    }

    private void performSearch(boolean isAnd) {
        for (Album a : AppState.get().getData().getAlbums()) {
            List<Photo> photos = a.getPhotos();
            for (int pi = 0; pi < photos.size(); pi++) {
                Photo p = photos.get(pi);
                boolean matched = isAnd;
                if (criteriaList.isEmpty()) continue;
                for (Pair<Tag.Type, String> crit : criteriaList) {
                    Tag.Type type = crit.first;
                    String prefix = crit.second.toLowerCase(Locale.ROOT);
                    boolean thisMatch = false;
                    for (Tag t : p.getTags()) {
                        if (t.getType() == type && t.getValue().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                            thisMatch = true;
                            break;
                        }
                    }
                    if (isAnd) {
                        matched = matched && thisMatch;
                        if (!matched) break;
                    } else {
                        matched = matched || thisMatch;
                        if (matched) break;
                    }
                }
                if (matched) results.add(new SearchResultsAdapter.SearchResult(a.getName(), pi, p));
            }
        }
    }

    // simple Pair class (since Android Pair might be available, but to avoid imports we define)
    public static class Pair<A,B> {
        public final A first; public final B second;
        public Pair(A a, B b) { first = a; second = b; }
    }
}

