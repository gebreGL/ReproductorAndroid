package com.example.reproductor.POJO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reproductor.R;

import java.util.ArrayList;

public class ArrayAdapterPOJO extends ArrayAdapter<CancionesPOJO> {

    private Activity context;
    private int miLayoutPersonalizado;
    private ArrayList<CancionesPOJO> canciones;
    private ArrayList<CancionesPOJO> cancionesFavoritas = new ArrayList<CancionesPOJO>();

    public ArrayAdapterPOJO(@NonNull Activity context, int resource, @NonNull ArrayList<CancionesPOJO> objects) {
        super(context, resource, objects);
        this.context = context;
        this.miLayoutPersonalizado = resource;
        this.canciones = objects;
    }

    @SuppressLint("ResourceType")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View fila = convertView;

        // Creo la instancia del objeto inflador --> LayoutInflater
        LayoutInflater layoutInflater = context.getLayoutInflater();

        // Aplico el método de inflado sobre el objeto inflador
        fila = layoutInflater.inflate(R.layout.fila_canciones, null);

        // Capturo los id de cada componente de mi layout personalizado (en este caso TextView e ImageView)
        TextView tvNombre = fila.findViewById(R.id.tvNombre);
        TextView tvArtista = fila.findViewById(R.id.tvArtista);
        ImageView ivPortada = fila.findViewById(R.id.ivPortada);
        ImageView ivFavorita = fila.findViewById(R.id.ivMeGusta);

        CancionesPOJO cancion = canciones.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences("CancionesFavoritas", Context.MODE_PRIVATE);

        // Obtengo el estado de la canción en favoritos
        boolean isFavorita = sharedPreferences.getBoolean(cancion.getNombre(), false); // false es el valor predeterminado si la clave no existe

        if (isFavorita) {
            // La canción está en favoritos
            ivFavorita.setImageResource(R.drawable.corazon_con);
        } else {
            // La canción no está en favoritos
            ivFavorita.setImageResource(R.drawable.corazon_sin);
        }

        ivFavorita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtengo el estado de la canción en favoritos
                boolean isFavorita = sharedPreferences.getBoolean(cancion.getNombre(), false); // false es el valor predeterminado si la clave no existe

                // Cambio el estado de favorito de la canción
                // Se actualiza la imagen del ImageView en función del nuevo estado de favorito
                if (!isFavorita) {
                    ivFavorita.setImageResource(R.drawable.corazon_con);

                    // Editor de SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Se agrega la canción a la lista de favoritos
                    editor.putBoolean(cancion.getNombre(), true); // Utiliza el nombre de la canción como clave y true como valor

                    // Guardar los cambios
                    editor.apply();
                    Toast.makeText(context, R.string.favoritos, Toast.LENGTH_SHORT).show();

                } else {
                    ivFavorita.setImageResource(R.drawable.corazon_sin);
                    // Editor de SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Se quita la canción de la lista de favoritos
                    editor.remove(cancion.getNombre()); // Utiliza el nombre de la canción como clave

                    // Guardar los cambios
                    editor.apply();
                    Toast.makeText(context, R.string.noFavoritos, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Inserto en cada componente el valor correspondiente de cada atributo de la clase CancionesPOJO
        String nombre = cancion.getNombre();
        String artista = cancion.getArtista();
        String rutaPortada = cancion.getPortada();
        int resId = context.getResources().getIdentifier(rutaPortada, "drawable", context.getPackageName());
        ivPortada.setImageResource(resId);

        tvNombre.setText(nombre.substring(0, 1).toUpperCase() + nombre.substring(1));
        tvArtista.setText(artista.substring(0, 1).toUpperCase() + artista.substring(1));

        return fila;
    }
}
