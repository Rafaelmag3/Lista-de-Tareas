package com.example.tareas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private BaseDatos baseDatos;
    private LinearLayout layoutCheckBox;
    private EditText editTextBusqueda;
    private Button btnBuscar;
    private Button btnAgregarNota;
    private List<CheckBox> listaCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseDatos = new BaseDatos(this);
        layoutCheckBox = findViewById(R.id.layout_checkbox);
        editTextBusqueda = findViewById(R.id.edit_text_busqueda);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnAgregarNota = findViewById(R.id.btn_agregar_nota);

        btnAgregarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NuevaNotaActivity.class);
                startActivity(intent);
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busqueda = editTextBusqueda.getText().toString().trim();
                if (busqueda.isEmpty()) {
                    // Si el campo de búsqueda está vacío, mostrar todas las tareas
                    mostrarTodasLasTareas();
                } else {
                    // Si hay texto en el campo de búsqueda, mostrar solo las tareas que coincidan con la búsqueda
                    Cursor cursor = baseDatos.buscarTareas(busqueda);
                    layoutCheckBox.removeAllViews(); //Elimina todas las vistas antiguas
                    mostrarTareasCoincidentes(busqueda);
                }
            }
        });
        mostrarTodasLasTareas();
    }
    private void mostrarTodasLasTareas() {
        Cursor cursor = baseDatos.obtenerTareas();
        mostrarTareas(cursor);
    }
    private void mostrarTareasCoincidentes(String busqueda) {
        Cursor cursor = baseDatos.buscarTareas(busqueda);
        mostrarTareas(cursor);
    }
    private void mostrarTareas(Cursor cursor) {

        listaCheckBox = new ArrayList<>();
        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()); // padding
        // Cargar el drawable del borde redondeado
        Drawable borderDrawable = getResources().getDrawable(R.drawable.checkbox_border);
        while (cursor.moveToNext()) {
            String tarea = cursor.getString(cursor.getColumnIndexOrThrow("tarea"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
            String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));

            final CheckBox checkBox = new CheckBox(MainActivity.this);
            checkBox.setText(tarea);
            int id = baseDatos.obtenerIdTarea(checkBox.getText().toString());
            checkBox.setTag(id);
            // Crear una SpannableString con el texto de la tarea
            SpannableString spannableString = new SpannableString(tarea + "\n" + fecha + "\n" + hora);
            // Aplicar un tamaño de fuente diferente a la tarea
            spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, tarea.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Asignar la SpannableString al texto del CheckBox
            checkBox.setText(spannableString);
            // Establecer el tamaño de la letra en sp
            float textSizeInSp = 13; // Tamaño de letra en sp
            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp);

            // Establecer el tipo de letra en negrita
            checkBox.setTypeface(null, Typeface.BOLD);
            checkBox.setPadding(paddingDp, paddingDp, paddingDp, paddingDp); // Añadir padding
            checkBox.setBackground(borderDrawable); // Establecer el borde redondeado como fondo
            // Establecer la altura del CheckBox
            int heightInDp = 80;
            int heightInPixels = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightInPixels);
            params.setMargins(0, 0, 0, 16); // Agrega un margen inferior de 16dp

            checkBox.setLayoutParams(params);
            listaCheckBox.add(checkBox);
            layoutCheckBox.addView(checkBox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Eliminar la tarea de la base de datos
                    Log.d("TAG", "El id obtenido es: " + id);
                    int id = (int) checkBox.getTag();
                    baseDatos.eliminarTarea(id);
                    // Muestra el toast "Tarea realizada"
                    Toast.makeText(MainActivity.this, "Tarea Completada", Toast.LENGTH_SHORT).show();
                    // Animar la eliminación del CheckBox
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_right);
                    checkBox.startAnimation(animation);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // Eliminar el CheckBox de la vista
                            layoutCheckBox.removeView(checkBox);
                            // Eliminar el CheckBox de la lista
                            listaCheckBox.remove(checkBox);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                }
            });
        }
        cursor.close();
    }
}
