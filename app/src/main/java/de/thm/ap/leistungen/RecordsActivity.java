package de.thm.ap.leistungen;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.leistungen.model.Record;

public class RecordsActivity extends AppCompatActivity {
    private ListView recordsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recordsListView = findViewById(R.id.records_list);
        recordsListView
                .setEmptyView(findViewById(R.id.records_list_empty));
    }

    @Override
    protected void onStart() {
        Context context = this;
        List<Record> selectedRecords = new ArrayList<>();
        List<Record> records = new RecordDAO(this).findAll();
        super.onStart();
        ArrayAdapter<Record> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, records);
        recordsListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recordsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recordsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Here you can do something when items are selected/de-selected,
                // such as update the title in the CAB
                Record record = (Record) recordsListView.getItemAtPosition(position);
                if (checked) selectedRecords.add(record);
                else selectedRecords.remove(record);
                mode.setTitle(selectedRecords.size() + " ausgewählt");
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.action_email:
                        String bodyMessage = "";
                        String subject = "Meine Leistungen " + selectedRecords.size();
                        for (int i = 0; i < selectedRecords.size(); i++) {
                            bodyMessage += selectedRecords.get(i).toString() + "\n";
                        }
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_TEXT, bodyMessage);
                        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                        startActivity(intent);
                        return true;
                    case R.id.action_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setTitle("Löschen");
                        builder.setMessage("Sind sie sich sicher?");

                        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < selectedRecords.size(); i++) {
                                    new RecordDAO(context).delete(selectedRecords.get(i));
                                    records.remove(selectedRecords.get(i));
                                    adapter.remove(selectedRecords.get(i));

                                }
                            }
                        });

                        builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                        //deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.selected_menu, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
                selectedRecords.clear();
                recordsListView.invalidateViews();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // Here you can perform updates to the CAB due to
                // an <code><a href="/reference/android/view/ActionMode.html#invalidate()">invalidate()</a></code> request
                return false;
            }
        });

        recordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = (Record) recordsListView.getItemAtPosition(position);
                if (record != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("record", record);
                    Intent intent = new Intent(view.getContext(), RecordFormActivity.class);
                    intent.putExtras(bundle);
                    intent.putExtra("updateMode", true);
                    startActivity(intent);
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // This adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.records, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Intent i = new Intent(this, RecordFormActivity.class);
                startActivity(i);
                return true;
            case R.id.action_stats:
                List<Record> records = new RecordDAO(this).findAll();
                String messegae = "";
                if (records.size() > 0) {
                    Stats stats = new Stats(records);
                    messegae += "Leistungen " + records.size() + "\n";
                    messegae += "50% Leistungen " + stats.getSumHalfWeighted() + "\n";
                    messegae += "Summe Crp " + stats.getSumCrp() + "\n";
                    messegae += "Crp bis zum Ziel " + stats.getCrpToEnd() + "\n";
                    messegae += "Durchschnitt " + stats.averageMark + "%\n";
                } else {
                    messegae += "Keine Leistungen vorhanden!";
                }
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Statistik");

                alertDialog.setMessage(messegae);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Schliessen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}

