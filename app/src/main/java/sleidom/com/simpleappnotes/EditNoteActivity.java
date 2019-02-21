package sleidom.com.simpleappnotes;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;

public class EditNoteActivity extends Activity {

    private FancyButton btnAdd;
    private EditText etTitle;
    private EditText etDescription;
    private String titleIntent;
    private String descriptionIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        final Intent intent = getIntent();
        titleIntent = intent.getStringExtra("Title");
        descriptionIntent = intent.getStringExtra("Description");

        etTitle = findViewById(R.id.editTitle);
        etDescription = findViewById(R.id.editDesc);
        btnAdd = findViewById(R.id.btnAdd);

        if (titleIntent != null) {
            etTitle.setText(titleIntent);
            etDescription.setText(descriptionIntent);
            btnAdd.setText(getString(R.string.modify_note_text));
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = saveNote();
                if (saved) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), R.string.add_note_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean saveNote() {
        boolean saved = false;
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotePref", 0);
        SharedPreferences.Editor editor = prefs.edit();


        if (titleIntent != null && !title.equals(titleIntent)) {
            editor.remove(titleIntent);
        }

        if (!(title == null || title.trim().equals("") || description == null || description.trim().equals(""))) {
            editor.putString(title, description);
            editor.commit();
            saved = true;
        }
        return saved;
    }
}
