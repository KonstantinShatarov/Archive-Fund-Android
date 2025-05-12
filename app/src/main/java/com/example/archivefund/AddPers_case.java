package com.example.archivefund;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.HashMap;

public class AddPers_case extends AppCompatActivity {

    Spinner formSpinner, specialtySpinner;
    Button back, save;
    TextView titleO;
    EditText caseNumberEditText, studentNameEditText, expulsionYearEditText, shelfEditText, rackEditText, groupEditText;
    ArrayAdapter<String> specialtyAdapter;
    DatabaseAdapter dbAdapter;
    HashMap<String, Integer> specIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pers_case);
        caseNumberEditText = findViewById(R.id.caseNumberEditText);
        studentNameEditText = findViewById(R.id.studentNameEditText);
        expulsionYearEditText = findViewById(R.id.expulsionYearEditText);
        shelfEditText = findViewById(R.id.shelfEditText);
        groupEditText = findViewById(R.id.studentGroup);
        rackEditText = findViewById(R.id.rackEditText);
        formSpinner = findViewById(R.id.FormSpinner);
        titleO = findViewById(R.id.titleO);
        specialtySpinner = findViewById(R.id.specialtySpinner);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();
        ArrayAdapter<String> formAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Очная", "Заочная", "Очно-заочная"});
        formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formSpinner.setAdapter(formAdapter);
        loadSpecialties();
        boolean isEdit = getIntent().getBooleanExtra("isEdit", false);

        if (isEdit) {
            titleO.setText("Редактирование информации");
            long id = getIntent().getLongExtra("id", -1);
            caseNumberEditText.setText(getIntent().getStringExtra("case_number"));
            studentNameEditText.setText(getIntent().getStringExtra("name"));
            groupEditText.setText(getIntent().getStringExtra("group"));
            expulsionYearEditText.setText(getIntent().getStringExtra("expulsion_year"));
            shelfEditText.setText(getIntent().getStringExtra("shelf"));
            rackEditText.setText(getIntent().getStringExtra("rack"));
            String formOb = getIntent().getStringExtra("form");
            String specName = getIntent().getStringExtra("specialty");
            int formIndex = formAdapter.getPosition(formOb);
            formSpinner.setSelection(formIndex);
            int specIndex = specialtyAdapter.getPosition(specName);
            specialtySpinner.setSelection(specIndex);
            save.setOnClickListener(v -> {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog_custom, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(AddPers_case.this);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                TextView title = dialogView.findViewById(R.id.dialogTitle);
                TextView message = dialogView.findViewById(R.id.dialogMessage);
                Button positiveButton = dialogView.findViewById(R.id.positiveButton);
                Button negativeButton = dialogView.findViewById(R.id.negativeButton);
                title.setText("Подтверждение");
                message.setText("Сохранить изменения?");
                positiveButton.setOnClickListener(view -> {
                    updatePersonalCase(id);
                    dialog.dismiss();});
                negativeButton.setOnClickListener(view -> dialog.dismiss());
                dialog.show();});
        } else {titleO.setText("Добавление личного дела");save.setOnClickListener(v ->
                savePersonalCase());}
        back.setOnClickListener(v -> finish());}

    private void loadSpecialties() {
        ArrayList<String> specialties = new ArrayList<>();
        Cursor cursor = dbAdapter.getAllSpecializations();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SPEC_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SPEC_NAME));
                specialties.add(name);
                specIdMap.put(name, id);
            } while (cursor.moveToNext());
            specialtyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
            specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            specialtySpinner.setAdapter(specialtyAdapter);
        }
    }

    private void savePersonalCase() {
        try {
            String caseNumber = caseNumberEditText.getText().toString().trim();
            String fio = studentNameEditText.getText().toString().trim();
            String group = groupEditText.getText().toString().trim();
            String specName = specialtySpinner.getSelectedItem().toString();
            int specId = specIdMap.get(specName);
            String formObuch = formSpinner.getSelectedItem().toString();
            int godOtch = Integer.parseInt(expulsionYearEditText.getText().toString());
            int shelf = Integer.parseInt(shelfEditText.getText().toString());
            int rack = Integer.parseInt(rackEditText.getText().toString());
            long result = dbAdapter.insertPersonalCase(caseNumber, fio, group, formObuch, specId, godOtch, shelf, rack);
            if (result != -1) {
                Toast.makeText(this, "Личное дело добавлено", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {Toast.makeText(this, "Поля с * обязательны", Toast.LENGTH_SHORT).show();}
    }

    private void updatePersonalCase(long id) {
        try {
            String caseNumber = caseNumberEditText.getText().toString().trim();
            String fio = studentNameEditText.getText().toString().trim();
            String group = groupEditText.getText().toString().trim();
            String specName = specialtySpinner.getSelectedItem().toString();
            int specId = specIdMap.get(specName);
            String formObuch = formSpinner.getSelectedItem().toString();
            int godOtch = Integer.parseInt(expulsionYearEditText.getText().toString());
            int shelf = Integer.parseInt(shelfEditText.getText().toString());
            int rack = Integer.parseInt(rackEditText.getText().toString());
            int rowsAffected = dbAdapter.updatePersonalCase(id, caseNumber, fio, group,
                    formObuch, specId, godOtch, shelf, rack);
            if (rowsAffected > 0) {
                Toast.makeText(this, "Информация успешно обновлена", Toast.LENGTH_SHORT).show();
                finish();}
        } catch (Exception e) {Toast.makeText(this, "Поля с * обязательны", Toast.LENGTH_SHORT).show();}
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }
}
