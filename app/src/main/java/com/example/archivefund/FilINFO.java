package com.example.archivefund;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class FilINFO extends AppCompatActivity {

    private DatabaseAdapter databaseAdapter;
    private Button back, edit, delete;
    private long id;
    private String caseNumber, form, name, specialty, group, expulsionYear, shelf, rack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fil_info);
        back = findViewById(R.id.back);
        edit = findViewById(R.id.edit);
        delete = findViewById(R.id.delete);

        back.setOnClickListener(v -> finish());
        edit.setOnClickListener(editLD);
        delete.setOnClickListener(deleteLD);

        databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.open();

        id = getIntent().getLongExtra("id", -1);
        if (id != -1) {
            loadData(id);
        }
    }

    View.OnClickListener editLD = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(FilINFO.this, AddPers_case.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("id", id);
            intent.putExtra("case_number", caseNumber);
            intent.putExtra("form", form);
            intent.putExtra("name", name);
            intent.putExtra("specialty", specialty);
            intent.putExtra("group", group);
            intent.putExtra("expulsion_year", expulsionYear);
            intent.putExtra("shelf", shelf);
            intent.putExtra("rack", rack);
            startActivity(intent);
        }
    };

    View.OnClickListener deleteLD = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_dialog_custom, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(FilINFO.this);
            builder.setView(dialogView);
            TextView title = dialogView.findViewById(R.id.dialogTitle);
            TextView message = dialogView.findViewById(R.id.dialogMessage);
            Button positiveButton = dialogView.findViewById(R.id.positiveButton);
            Button negativeButton = dialogView.findViewById(R.id.negativeButton);
            title.setText("Удаление личного дела №" + caseNumber);
            message.setText("Вы уверены, что хотите удалить это личное дело?");
            final AlertDialog dialog = builder.create();
            dialog.show();
            positiveButton.setOnClickListener(v1 -> {
                databaseAdapter.deletePersonalCase(id);
                finish();});
            negativeButton.setOnClickListener(v1 -> {
                dialog.dismiss();});
        }
    };

    private void loadData(long id)
    {
        Cursor cursor = databaseAdapter.getPersonalCaseById(id);

        if (cursor != null && cursor.moveToFirst()) {
            caseNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_CASE_NUMBER));
            form = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_FORM_OB));
            ((EditText) findViewById(R.id.Form_obuch)).setText(form);
            name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_FIO));
            specialty = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.SPEC_NAME));
            group = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_NAME));
            expulsionYear = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_GOD_OTCH));
            rack = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_NUM_POL));
            shelf = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PERS_NUM_SHELF));
            ((EditText) findViewById(R.id.caseNumber)).setText(caseNumber);
            ((EditText) findViewById(R.id.Form_obuch)).setText(form);
            ((EditText) findViewById(R.id.FIO)).setText(name);
            ((EditText) findViewById(R.id.spec)).setText(specialty);
            ((EditText) findViewById(R.id.Group)).setText(group);
            ((EditText) findViewById(R.id.God_otc)).setText(expulsionYear);
            ((EditText) findViewById(R.id.caseNumberEditText)).setText(rack);
            ((TextView) findViewById(R.id.shelf)).setText(shelf);
        } else {
            Toast.makeText(this, "Личное дело не найдено", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (id != -1) {
            loadData(id);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseAdapter != null) {
            databaseAdapter.close();
        }
    }
}
