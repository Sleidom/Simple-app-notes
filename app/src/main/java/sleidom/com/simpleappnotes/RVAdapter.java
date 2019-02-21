package sleidom.com.simpleappnotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private static List<String> allNotes;
    private static Activity act;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView desc;
        ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv_note);
            title = itemView.findViewById(R.id.title_note);
            desc = itemView.findViewById(R.id.desc_note);
            delete = itemView.findViewById(R.id.delete_note);
        }

        public void bind(final String titleStr, final String description) {
            title.setText(titleStr);
            if (description.length() < 100)
                desc.setText(description);
            else
                desc.setText(description.substring(0, 99) + " [ ... ]");
            int resId = R.drawable.delete;
            delete.setImageResource(resId);

            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    generateAlertDelete(titleStr);

                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(act, EditNoteActivity.class);
                    intent.putExtra("Title", titleStr);
                    intent.putExtra("Description", description);
                    act.startActivityForResult(intent, 1);
                }
            });
        }
    }

    public RVAdapter(List<String> allNotes, Activity act) {
        this.allNotes = allNotes;
        this.act = act;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_note, viewGroup, false);
        ViewHolder hvh = new ViewHolder(v);
        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder historyViewHolder, int i) {
        historyViewHolder.bind(allNotes.get(i).split(";")[0], allNotes.get(i).split(";")[1]);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return allNotes.size();
    }

    public void updateData() {
        SharedPreferences prefs = act.getApplicationContext().getSharedPreferences("NotePref", 0);

        Map<String, ?> allNotesMap = prefs.getAll();
        allNotes.clear();
        for (Map.Entry<String, ?> entry : allNotesMap.entrySet()) {
            String title = entry.getKey();
            String description = (String) entry.getValue();
            allNotes.add(title + ";" + description);
        }

        Collections.sort(allNotes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        notifyDataSetChanged();
    }

    private void deleteNote(final String titleStr) {
        SharedPreferences prefs = act.getApplicationContext().getSharedPreferences("NotePref", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(titleStr);
        editor.commit();

        boolean end = false;
        int pos = 0;
        for (int i = 0; i < allNotes.size() && !end; i++) {
            if (allNotes.get(i).split(";")[0].equals(titleStr)) {
                allNotes.remove(i);
                end = true;
                pos = i;
            }
        }
        notifyItemRemoved(pos);
    }

    private void generateAlertDelete(final String titleStr) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);

        // set title
        alertDialogBuilder.setTitle(act.getString(R.string.delete_note));

        // set dialog message
        alertDialogBuilder
                .setMessage(act.getString(R.string.confirmation_delete) + " '" + titleStr + "'?")
                .setCancelable(false)
                .setPositiveButton(act.getString(R.string.Yes),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        deleteNote(titleStr);
                    }
                })
                .setNegativeButton(act.getString(R.string.No),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}