package com.example.archivefund;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open() {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getAllPersonalCases() {
        String[] columns = new String[] {
                DatabaseHelper.PERS_ID + " AS _id",
                DatabaseHelper.PERS_CASE_NUMBER,
                DatabaseHelper.PERS_FIO,
                DatabaseHelper.PERS_GOD_OTCH
        };
        return database.query(DatabaseHelper.TABLE_PERSONAL_CASE, columns, null, null, null, null, null);
    }


    public Cursor getPersonalCaseById(long id) {
        String query = "SELECT pc.*, s.Name FROM Personal_case pc LEFT JOIN Specialization s ON pc.id_spec = s.id_spec WHERE pc.id_pers = ?";
        return database.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public long insertPersonalCase(String caseNumber, String fio, String group, String formOb, int specId,
                                   int god_otch, int numPol, int numShelf) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.PERS_CASE_NUMBER, caseNumber);
        cv.put(DatabaseHelper.PERS_FIO, fio);
        cv.put(DatabaseHelper.GROUP_NAME, group);
        cv.put(DatabaseHelper.PERS_FORM_OB, formOb);
        cv.put(DatabaseHelper.PERS_SPEC_ID, specId);
        cv.put(DatabaseHelper.PERS_GOD_OTCH, god_otch);
        cv.put(DatabaseHelper.PERS_NUM_POL, numPol);
        cv.put(DatabaseHelper.PERS_NUM_SHELF, numShelf);

        return database.insert(DatabaseHelper.TABLE_PERSONAL_CASE, null, cv);
    }

    public int updatePersonalCase(long id, String caseNumber, String fio, String group, String formOb, int specId,
                                  int godOtch, int numPol, int numShelf) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.PERS_CASE_NUMBER, caseNumber);
        cv.put(DatabaseHelper.PERS_FIO, fio);
        cv.put(DatabaseHelper.GROUP_NAME, group);
        cv.put(DatabaseHelper.PERS_FORM_OB, formOb);
        cv.put(DatabaseHelper.PERS_SPEC_ID, specId);
        cv.put(DatabaseHelper.PERS_GOD_OTCH, godOtch);
        cv.put(DatabaseHelper.PERS_NUM_POL, numPol);
        cv.put(DatabaseHelper.PERS_NUM_SHELF, numShelf);
        String whereClause = DatabaseHelper.PERS_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        return database.update(DatabaseHelper.TABLE_PERSONAL_CASE, cv, whereClause, whereArgs);
    }

    public Cursor searchPers_case(String fio, Integer godOtch, Integer specId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> conditions = new ArrayList<>();
        List<String> args = new ArrayList<>();
        String query = "SELECT id_pers AS _id, case_number, formOB, FIO, group_name, id_spec, god_otch, " +
                "Num_pol, Num_shelf FROM Personal_case";
        if (specId != null) {
            conditions.add("id_spec = ?");
            args.add(String.valueOf(specId));}
        if (fio != null && !fio.isEmpty()) {
            conditions.add(DatabaseHelper.PERS_FIO + " LIKE ?");
            args.add("%" + fio + "%");}
        if (godOtch != null) {
            conditions.add(DatabaseHelper.PERS_GOD_OTCH + " = ?");
            args.add(String.valueOf(godOtch));}
        if (!conditions.isEmpty()) {
            query += " WHERE " + String.join(" AND ", conditions);}
        return db.rawQuery(query, args.toArray(new String[0]));
    }


    public int deletePersonalCase(long id) {
        String whereClause = DatabaseHelper.PERS_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        return database.delete(DatabaseHelper.TABLE_PERSONAL_CASE, whereClause, whereArgs);
    }

    public Cursor getAllSpecializations() {
        return database.query(DatabaseHelper.TABLE_SPECIALIZATION, null, null, null, null, null, null);
    }
}