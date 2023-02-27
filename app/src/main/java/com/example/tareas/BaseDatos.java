package com.example.tareas;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
public class BaseDatos extends SQLiteOpenHelper {
    // Versión de la base de datos
    private static final int VERSION_BASEDATOS = 1;
    // Nombre de la base de datos
    private static final String NOMBRE_BASEDATOS = "notas.db";
    // Nombre de la tabla
    private static final String NOMBRE_TABLA = "notas";

    // Nombre de las columnas
    private static final String COLUMNA_ID = "_id";
    private static final String COLUMNA_TAREA = "tarea";
    private static final String COLUMNA_FECHA = "fecha";
    private static final String COLUMNA_HORA = "hora";
    // Sentencia SQL para crear la tabla
    private static final String CREAR_TABLA = "CREATE TABLE " + NOMBRE_TABLA + "("
            + COLUMNA_ID + " INTEGER PRIMARY KEY,"
            + COLUMNA_TAREA + " TEXT,"
            + COLUMNA_FECHA + " TEXT,"
            + COLUMNA_HORA + " TEXT)";

    public BaseDatos(Context context) {
        super(context, NOMBRE_BASEDATOS, null, VERSION_BASEDATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABLA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA);
        onCreate(db);
    }

    public long agregarTarea(String nombreTarea, String fecha, String hora) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_TAREA, nombreTarea);
        valores.put(COLUMNA_FECHA, fecha);
        valores.put(COLUMNA_HORA, hora);
        long resultado = db.insert(NOMBRE_TABLA, null, valores);
        db.close();
        return resultado;
    }
    public Cursor obtenerTareas() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_TAREA, COLUMNA_FECHA, COLUMNA_HORA};
        Cursor cursor = db.query(NOMBRE_TABLA, columnas, null, null, null, null, null);
        return cursor;
    }
    public Cursor buscarTareas(String busqueda) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_TAREA, COLUMNA_FECHA, COLUMNA_HORA};
        String[] argumentos = {"%" + busqueda + "%"};
        Cursor cursor = db.query(NOMBRE_TABLA, columnas, COLUMNA_TAREA + " LIKE ?", argumentos, null, null, null);
        return cursor;
    }
    public int obtenerIdTarea(String tareaTexto) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columnas = {COLUMNA_ID};
        String seleccion = COLUMNA_TAREA + "=?";
        String[] seleccionArgs = {tareaTexto};
        //Cursor cursor = db.query(NOMBRE_TABLA, columnas, seleccion, seleccionArgs, null, null, null);
        Cursor cursor = db.rawQuery("SELECT * FROM " + NOMBRE_TABLA + " WHERE " + COLUMNA_TAREA + "=?", new String[]{tareaTexto});
        if (cursor.getCount() == 0) {
            Log.d("TAG", "La tarea no se encontró en la base de datos");
            return -1;
        }

        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNA_ID));
        } else {
            Log.d("TAG", "No se encontró la tarea en la base de datos");
        }
        cursor.close();
        db.close();
        return id;
    }
    public void eliminarTarea(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.delete(NOMBRE_TABLA, COLUMNA_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

}
