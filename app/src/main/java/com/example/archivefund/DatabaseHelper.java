package com.example.archivefund;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Arhivefund.db";
    private static final int DATABASE_VERSION = 4;

    // Название таблиц
    public static final String TABLE_SPECIALIZATION = "Specialization";
    public static final String TABLE_PERSONAL_CASE = "Personal_case";

    // Specialization
    public static final String SPEC_ID = "id_spec";
    public static final String SPEC_KOD = "Kod";
    public static final String SPEC_NAME = "Name";
    public static final String SPEC_QOLIF = "Qolif";

    // Personal_case
    public static final String PERS_ID = "id_pers";
    public static final String PERS_CASE_NUMBER = "case_number";
    public static final String PERS_FORM_OB = "formOB";
    public static final String PERS_FIO = "FIO";
    public static final String GROUP_NAME = "group_name";
    public static final String PERS_SPEC_ID = "id_spec";
    public static final String PERS_GOD_OTCH = "god_otch";
    public static final String PERS_NUM_POL = "Num_pol";
    public static final String PERS_NUM_SHELF = "Num_shelf";

    private static final String CREATE_TABLE_SPECIALIZATION = "CREATE TABLE " + TABLE_SPECIALIZATION + "("
            + SPEC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SPEC_KOD + " TEXT NOT NULL,"
            + SPEC_NAME + " TEXT NOT NULL,"
            + SPEC_QOLIF + " TEXT NOT NULL)";

    private static final String CREATE_TABLE_PERSONAL_CASE = "CREATE TABLE " + TABLE_PERSONAL_CASE + "("
            + PERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PERS_CASE_NUMBER + " TEXT NOT NULL,"
            + PERS_FORM_OB + " TEXT NOT NULL,"
            + PERS_FIO + " TEXT NOT NULL,"
            + GROUP_NAME + " TEXT NOT NULL,"
            + PERS_SPEC_ID + " INTEGER NOT NULL,"
            + PERS_GOD_OTCH + " INTEGER NOT NULL,"
            + PERS_NUM_POL + " INTEGER NOT NULL,"
            + PERS_NUM_SHELF + " INTEGER NOT NULL,"
            + "FOREIGN KEY(" + PERS_SPEC_ID + ") REFERENCES " + TABLE_SPECIALIZATION + "(" + SPEC_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_SPECIALIZATION);
        db.execSQL(CREATE_TABLE_PERSONAL_CASE);

        db.execSQL("INSERT INTO " + TABLE_SPECIALIZATION +
                " ("+ SPEC_KOD + ", " + SPEC_NAME + ", " + SPEC_QOLIF + ") VALUES " +
                "('09.01.02', 'Информационные системы и программирование', 'Программист')," +
                "('38.02.01', 'Экономика и бухгалтерский учет', 'Бухгалтер')," +
                "('38.02.03', 'Операционная деятельность в логистике', 'Операционный логист')," +
                "('40.02.02', 'Правоохранительная деятельность', 'Юрист')," +
                "('44.02.01', 'Дошкольное образование', 'Воспитатель')," +
                "('44.02.02', 'Преподавание в начальных классах', 'Учитель')," +
                "('33.02.01', 'Фармация', 'Фармацевт')");

        db.execSQL("INSERT INTO " + TABLE_PERSONAL_CASE +
                "(" + PERS_CASE_NUMBER + "," + PERS_FORM_OB + "," + PERS_FIO +
                "," + GROUP_NAME + "," + PERS_SPEC_ID + "," + PERS_GOD_OTCH + "," + PERS_NUM_POL + "," +
                PERS_NUM_SHELF + ") VALUES " +
                "('0001','Очная','Иванов Иван Иванович','ИП-1',1,2025,2,3)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONAL_CASE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECIALIZATION);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}