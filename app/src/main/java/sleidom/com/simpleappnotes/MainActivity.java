package sleidom.com.simpleappnotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private RecyclerView rv;
    private ImageView btnAdd;
    private Context context;
    private RVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.context = this;

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotePref", 0);

        Map<String, ?> allNotes = prefs.getAll();
        List<String> allNotesList = new ArrayList<String>();
        for (Map.Entry<String, ?> entry : allNotes.entrySet()) {
            String title = entry.getKey();
            String description = (String) entry.getValue();
            allNotesList.add(title + ";" + description);
        }
        Collections.sort(allNotesList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        this.rv = findViewById(R.id.rvh);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        this.rv.setLayoutManager(llm);

        this.adapter = new RVAdapter(allNotesList, this);
        rv.setAdapter(adapter);


        btnAdd = findViewById(R.id.add_new_note);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditNoteActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                adapter.updateData();
            }
        }
    }
}
