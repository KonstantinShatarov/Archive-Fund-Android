package com.example.archivefund;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button searchBtn, addPersCaseBtn;
    ListView persCaseListView;
    DatabaseAdapter databaseAdapter;
    Cursor userCursor;
    Spinner specSpinner;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBtn = findViewById(R.id.Search);
        addPersCaseBtn = findViewById(R.id.addPers_case);
        persCaseListView = findViewById(R.id.Pers_case);
        specSpinner = findViewById(R.id.Spec);
        searchBtn.setOnClickListener(searchLD);
        databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();
        loadSpecializationsToSpinner();
        loadPersonalCases();
        setupListeners();
    }

    View.OnClickListener searchLD = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String godOtchStr = ((android.widget.EditText) findViewById(R.id.god_otch)).getText().toString().trim();
            String fio = ((android.widget.EditText) findViewById(R.id.FIO)).getText().toString().trim();
            String selectedSpec = specSpinner.getSelectedItem() != null ? specSpinner.getSelectedItem().toString() : "";
            Integer specId = specIdMap.get(selectedSpec);
            Integer godOtch = null;
            if (!godOtchStr.isEmpty()) {
                try {
                    godOtch = Integer.parseInt(godOtchStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Неверный формат года", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Cursor filteredCursor = databaseAdapter.searchPers_case(fio, godOtch, specId);
            if (filteredCursor != null && userAdapter != null) {
                Log.d("DEBUG_CURSOR", "Cursor count: " + filteredCursor.getCount());
                userAdapter.changeCursor(filteredCursor);
            } else {
                Toast.makeText(MainActivity.this, "Ничего не найдено или адаптер не инициализирован", Toast.LENGTH_SHORT).show();
            }
        }
    };

    ArrayAdapter<String> specialtyAdapter;
    java.util.Map<String, Integer> specIdMap = new java.util.HashMap<>();

    private void loadSpecializationsToSpinner() {
        ArrayList<String> specialties = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = databaseAdapter.getAllSpecializations();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.SPEC_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SPEC_NAME));
                    specialties.add(name);
                    specIdMap.put(name, id);
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "Специальности не найдены", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при загрузке специальностей", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        specialtyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialties);
        specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specSpinner.setAdapter(specialtyAdapter);
    }

    private void loadPersonalCases()
    {
        try {
            userCursor = databaseAdapter.getAllPersonalCases();
            String[] fromColumns = {DatabaseHelper.PERS_CASE_NUMBER, DatabaseHelper.PERS_FIO, DatabaseHelper.PERS_GOD_OTCH};
            int[] toViews = { android.R.id.text1, android.R.id.text2, android.R.id.text1 };
            userAdapter = new SimpleCursorAdapter(this, R.layout.custom_two_line_item, userCursor,
                    fromColumns, toViews, 0);
            userAdapter.setViewBinder((view, cursor, columnIndex) -> {
                if (view.getId() == android.R.id.text1) {
                    String fio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_FIO));
                    String graduationYear = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_GOD_OTCH));
                    ((android.widget.TextView) view).setText(fio + " — Год отчисления: " + graduationYear);
                    return true;
                } else if (view.getId() == android.R.id.text2) {
                    String caseNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_CASE_NUMBER));
                    ((android.widget.TextView) view).setText("Личное дело №" + caseNumber);
                    return true;
                }
                return false;
            });

            persCaseListView.setAdapter(userAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка загрузки личных дел", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        addPersCaseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddPers_case.class);
            startActivity(intent);
        });

        persCaseListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, FilINFO.class);
            intent.putExtra("id", id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseAdapter != null) {
            loadPersonalCases();
            loadSpecializationsToSpinner();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userCursor != null) {
            userCursor.close();
        }
        if (databaseAdapter != null) {
            databaseAdapter.close();
        }
    }
}