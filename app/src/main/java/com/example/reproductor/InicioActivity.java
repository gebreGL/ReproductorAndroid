package com.example.reproductor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reproductor.POJO.ArrayAdapterPOJO;
import com.example.reproductor.POJO.CancionesPOJO;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;

public class InicioActivity extends AppCompatActivity {

    private ListView lvCanciones;
    private AutoCompleteTextView acBuscarCancion;
    private Button btnActualizar;
    private Button btnBuscarCancion;
    private ArrayList<CancionesPOJO> canciones;
    private ArrayList<CancionesPOJO> arrayActual;
    private ArrayAdapterPOJO adapter;
    private ArrayList<CancionesPOJO> cancionesFavoritas = new ArrayList<CancionesPOJO>();
    private int posicion = 0;
    private boolean isVisible = false;

    private final int CODIGO_DE_SOLICITUD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        lvCanciones = findViewById(R.id.lvCanciones);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnBuscarCancion = findViewById(R.id.btnBuscarCancion);
        acBuscarCancion = findViewById(R.id.acBuscarCancion);

        registerForContextMenu(lvCanciones);
        acBuscarCancion.setThreshold(1);

        SQLiteAssetHelper dbHelper = new SQLiteAssetHelper(this, "bdReproductor.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Ejecuto una consulta en la tabla "mytable" y almaceno los datos en un ArrayList (canciones)
        Cursor cursor = db.rawQuery("SELECT * FROM canciones", null);
        canciones = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                CancionesPOJO cancion = new CancionesPOJO();
                String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                String artista = cursor.getString(cursor.getColumnIndex("artista"));
                String portada = cursor.getString(cursor.getColumnIndex("portada"));
                String ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                cancion.setNombre(nombre);
                cancion.setArtista(artista);
                cancion.setPortada(portada);
                cancion.setRuta(ruta);
                canciones.add(cancion);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        arrayActual = new ArrayList<>(canciones);

        adapter = new ArrayAdapterPOJO(
                this,
                R.layout.fila_canciones,
                canciones);

        lvCanciones.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        lvCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(InicioActivity.this, MainActivity.class);
                i.putParcelableArrayListExtra("arrayCanciones", arrayActual);
                posicion = position;
                i.putExtra("posicion", posicion);
                startActivity(i);
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("CancionesFavoritas", Context.MODE_PRIVATE);
                ArrayList<CancionesPOJO> cancionesFavoritasActualizadas = new ArrayList<>();

                for (CancionesPOJO elemento : canciones) {
                    boolean isFavorita = sharedPreferences.getBoolean(elemento.getNombre(), false);
                    if (isFavorita) {
                        cancionesFavoritasActualizadas.add(elemento);
                    }
                }
                if (cancionesFavoritasActualizadas.isEmpty()) {
                    btnActualizar.setVisibility(View.GONE);
                    Toast.makeText(InicioActivity.this, R.string.avisoCancionesFavoritas, Toast.LENGTH_SHORT).show();
                }

                adapter.clear();
                arrayActual = new ArrayList<>(cancionesFavoritasActualizadas);
                adapter.addAll(cancionesFavoritasActualizadas);
                adapter.notifyDataSetChanged();
            }
        });

        ArrayList<String> cancionesBuscar = new ArrayList<>();
        for (CancionesPOJO cancion : arrayActual) {
            cancionesBuscar.add(cancion.getNombre());
        }

        ArrayAdapter adapterAc = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                cancionesBuscar);

        acBuscarCancion.setAdapter(adapterAc);

        btnBuscarCancion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acBuscarCancion.getText().toString().equals("")) {
                    Toast.makeText(InicioActivity.this, R.string.avisoIntroducirCancion, Toast.LENGTH_SHORT).show();
                } else {
                    SQLiteAssetHelper dbHelper = new SQLiteAssetHelper(InicioActivity.this, "bdReproductor.db", null, 1);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String whereClause = "nombre = ?"; // Condición WHERE
                    String[] whereArgs = { acBuscarCancion.getText().toString() }; // Valor del nombre a buscar

                    Cursor cursor = db.rawQuery("SELECT * FROM canciones WHERE " + whereClause, whereArgs);
                    ArrayList<CancionesPOJO> cancionesBuscar = new ArrayList<>();

                    if (cursor.moveToFirst()) {
                        do {
                            CancionesPOJO cancion = new CancionesPOJO();
                            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                            String artista = cursor.getString(cursor.getColumnIndex("artista"));
                            String portada = cursor.getString(cursor.getColumnIndex("portada"));
                            String ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                            cancion.setNombre(nombre);
                            cancion.setArtista(artista);
                            cancion.setPortada(portada);
                            cancion.setRuta(ruta);
                            cancionesBuscar.add(cancion);
                        } while (cursor.moveToNext());
                    }
                    adapter.clear();
                    arrayActual = new ArrayList<>(cancionesBuscar);
                    adapter.addAll(cancionesBuscar);
                    adapter.notifyDataSetChanged();
                    cursor.close();
                    db.close();
                    dbHelper.close();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflado del recurso de tipo menu que tengo en XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);

        return true;
        /* Se cambia return super.onCreateOptionsMenu(menu); por return true; porque la respuesta
         es un booleano positivo ya que siempre se ejecuta */
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SQLiteAssetHelper dbHelper = new SQLiteAssetHelper(this, "bdReproductor.db", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch(item.getItemId()) {
            case R.id.porNombre:
                btnActualizar.setVisibility(View.GONE);
                // Consulta SQL para obtener objetos ordenados por nombre
                String consulta = "SELECT * FROM canciones ORDER BY nombre ASC";

                // Estoy utilizando un objeto de base de datos llamado "bd" para ejecutar la consulta
                cursor = db.rawQuery(consulta, null);
                ArrayList<CancionesPOJO> listaPorNombre = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        // Crear objeto MiObjeto a partir de los datos del cursor y añadirlo a la lista
                        String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                        String artista = cursor.getString(cursor.getColumnIndex("artista"));
                        String portada = cursor.getString(cursor.getColumnIndex("portada"));
                        String ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                        CancionesPOJO canciones = new CancionesPOJO(nombre, artista, portada, ruta);
                        listaPorNombre.add(canciones);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                adapter.clear();
                arrayActual = new ArrayList<>(listaPorNombre);
                adapter.addAll(arrayActual);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.porArtista:
                btnActualizar.setVisibility(View.GONE);
                // Consulta SQL para obtener objetos ordenados por artista
                String consulta2 = "SELECT * FROM canciones ORDER BY artista ASC";

                // Objeto de base de datos llamado "bd" para ejecutar la consulta
                cursor = db.rawQuery(consulta2, null);
                ArrayList<CancionesPOJO> listaPorArtista = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        // A partir de los datos del cursor, los añado a la lista
                        String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                        String artista = cursor.getString(cursor.getColumnIndex("artista"));
                        String portada = cursor.getString(cursor.getColumnIndex("portada"));
                        String ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                        CancionesPOJO canciones = new CancionesPOJO(nombre, artista, portada, ruta);
                        listaPorArtista.add(canciones);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                adapter.clear();
                arrayActual = new ArrayList<>(listaPorArtista);
                adapter.addAll(arrayActual);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.porFavoritas:
                SharedPreferences sharedPreferences = getSharedPreferences("CancionesFavoritas", Context.MODE_PRIVATE);
                ArrayList<CancionesPOJO> cancionesFavoritasActualizadas = new ArrayList<>();

                for (CancionesPOJO elemento : canciones) {
                    boolean isFavorita = sharedPreferences.getBoolean(elemento.getNombre(), false);
                    if (isFavorita) {
                        cancionesFavoritasActualizadas.add(elemento);
                    }
                }

                if (cancionesFavoritasActualizadas.isEmpty()) {
                    btnActualizar.setVisibility(View.GONE);
                    Toast.makeText(this, getString(R.string.avisoCancionesFavoritas), Toast.LENGTH_SHORT).show();
                } else {
                    btnActualizar.setVisibility(View.VISIBLE);
                }
                adapter.clear();
                arrayActual = new ArrayList<>(cancionesFavoritasActualizadas);
                adapter.addAll(arrayActual);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.buscarCancion:
                if (isVisible) {
                    btnActualizar.setVisibility(View.GONE);
                    acBuscarCancion.setVisibility(View.GONE);
                    btnBuscarCancion.setVisibility(View.GONE);
                    isVisible = false;
                    // Consulto en la tabla "canciones" y almaceno los datos en un ArrayList
                    cursor = db.rawQuery("SELECT * FROM canciones", null);
                    canciones = new ArrayList<>();
                    if (cursor.moveToFirst()) {
                        do {
                            CancionesPOJO cancion = new CancionesPOJO();
                            String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                            String artista = cursor.getString(cursor.getColumnIndex("artista"));
                            String portada = cursor.getString(cursor.getColumnIndex("portada"));
                            String ruta = cursor.getString(cursor.getColumnIndex("ruta"));
                            cancion.setNombre(nombre);
                            cancion.setArtista(artista);
                            cancion.setPortada(portada);
                            cancion.setRuta(ruta);
                            canciones.add(cancion);
                        } while (cursor.moveToNext());
                    }
                    adapter.clear();
                    arrayActual = new ArrayList<>(canciones);
                    adapter.addAll(arrayActual);
                    adapter.notifyDataSetChanged();
                    cursor.close();
                    db.close();
                    dbHelper.close();
                } else {
                    acBuscarCancion.setVisibility(View.VISIBLE);
                    btnBuscarCancion.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //Inflado del recurso de tipo menu context que tengo en XML
        MenuInflater inflate = getMenuInflater();
        menu.setHeaderTitle(R.string.opciones);
        menu.setHeaderIcon(R.drawable.opciones);
        inflate.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        Intent i;
        switch(item.getItemId()) {
            case R.id.reproducir:
                i = new Intent(InicioActivity.this, MainActivity.class);
                i.putParcelableArrayListExtra("arrayCanciones", arrayActual);
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                posicion = info.position;
                i.putExtra("posicion", posicion);
                startActivity(i);
                return true;
            case R.id.modificar:
                i = new Intent(InicioActivity.this, ModificarCancionActivity.class);
                i.putParcelableArrayListExtra("arrayCanciones", arrayActual);
                info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                posicion = info.position;
                i.putExtra("posicion", posicion);
                startActivityForResult(i, CODIGO_DE_SOLICITUD);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_DE_SOLICITUD && resultCode == RESULT_OK) {
            String nombreCancion = data.getStringExtra("nombreCancion");
            String nombreArtista = data.getStringExtra("nombreArtista");
            String nombreAntiguo = data.getStringExtra("nombreAntiguo");
            String artistaAntiguo = data.getStringExtra("artistaAntiguo");
            SQLiteAssetHelper dbHelper = new SQLiteAssetHelper(InicioActivity.this, "bdReproductor.db", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("nombre", nombreCancion);
            values.put("artista", nombreArtista);
            String whereClause = "nombre = ? AND artista = ?";
            String[] whereArgs = {nombreAntiguo, artistaAntiguo};
            db.update("canciones", values, whereClause, whereArgs);
            db.close();

            arrayActual.get(posicion).setNombre(nombreCancion);
            arrayActual.get(posicion).setArtista(nombreArtista);

            ((ArrayAdapterPOJO) lvCanciones.getAdapter()).notifyDataSetChanged();

        }
    }
}