package de.thm.ap.leistungen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.Optional;

import de.thm.ap.leistungen.model.Record;

public class RecordFormActivity extends AppCompatActivity {

    private boolean updateMode;
    private Record updateRecord;
    private EditText modulNum, creditPoints, mark;
    private AutoCompleteTextView modulName;
    private Spinner year;
    private CheckBox summer, halfMark;
    private int tmp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_form);
        Optional.ofNullable(getSupportActionBar())
                .ifPresent(
                        actionBar -> actionBar.setDisplayHomeAsUpEnabled(true)
                );
        modulNum = findViewById(R.id.module_num);
        modulName = findViewById(R.id.module_name);
        creditPoints = findViewById(R.id.crp);
        mark = findViewById(R.id.note);
        year = findViewById(R.id.spinner);
        summer = findViewById(R.id.checkbox1);
        halfMark = findViewById(R.id.checkbox2);
        String[] names =
                getResources().getStringArray(R.array.module_names);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, names);
        modulName.setAdapter(adapter);
        String[] years =
                getResources().getStringArray(R.array.mark_year);
        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, years);
        year.setAdapter(adapter2);
        Intent intent = this.getIntent();
        updateMode = intent.getBooleanExtra("updateMode",false);
        if (updateMode) {
            Bundle bundle = intent.getExtras();
            updateRecord = (Record) bundle.getSerializable("record");
            loadObjectInfos(updateRecord);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("request", 0);
                setResult(RecordsActivity.RESULT_CANCELED, resultIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onSave(View view) {
        Toast toast = Toast.makeText(getApplicationContext(),"Gespeichert", Toast.LENGTH_SHORT);
        Record record = null;
        if (!updateMode) {
            record = new Record();
        } else if (updateMode) {
            record = updateRecord;
        }
        // validate user input
        boolean isValid = true;
        record.setModuleName(modulName.getText().toString().trim());
        if ("".equals(record.getModuleName())) {
            modulName.setError(getString(R.string.module_name_not_empty));
            isValid = false;
        }
        record.setModuleNum(modulNum.getText().toString().trim());
        if ("".equals(record.getModuleNum())) {
            modulNum.setError(getString(R.string.module_num_not_empty));
            isValid = false;
        }
        if (summer.isChecked()) {
            record.setSummerTerm(true);
        } else record.setSummerTerm(false);
        record.setYear(Integer.parseInt(year.getSelectedItem().toString()));
        try {
            tmp = Integer.parseInt(creditPoints.getText().toString());
            if (tmp > 15) {
                creditPoints.setError("Höchst. 15 Creditpoints eingeben!");
                isValid = false;
            }
            else {
                record.setCrp(tmp);
            }
        } catch (NumberFormatException e) {
            creditPoints.setError("Höchst. 15 Creditpoints eingeben!");
            isValid = false;
        }
        try {
            tmp = Integer.parseInt(mark.getText().toString());
            if (tmp != 0) {
                if (tmp < 50 || tmp > 100) {
                    mark.setError("Note muss mind. 50% oder höchst. 100% sein");
                    isValid = false;
                }
            } else {
                record.setMark(tmp);
            }
        } catch (NumberFormatException e) {
            mark.setError("Note muss mind. 50% oder höchst. 100% sein");
            isValid = false;
        }
        if (halfMark.isChecked()) {
            record.setHalfWeighted(true);
        } else record.setHalfWeighted(false);

        if (isValid) {
            if (!updateMode) {
                new RecordDAO(this).persist(record);
            } else if (updateMode) {
                new RecordDAO(this).update(record);
            }
            toast.show();
            Intent resultIntent = new Intent();
            setResult(RecordsActivity.RESULT_OK, resultIntent);
            finish();
        }
    }

    void loadObjectInfos(Record record) {
        modulNum.setText(record.getModuleNum());
        modulName.setText(record.getModuleName());
        creditPoints.setText(record.getCrp().toString());
        mark.setText(record.getMark().toString());
        for (int i = 0; i < year.getCount(); i++) {
            if (Integer.parseInt((String) year.getItemAtPosition(i)) == record.getYear()) {
                year.setSelection(i);
            }
        }
        halfMark.setChecked(record.isHalfWeighted());
        summer.setChecked(record.isSummerTerm());
    }
}

