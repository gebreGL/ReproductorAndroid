package com.example.reproductor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.reproductor.POJO.CancionesPOJO;

import java.util.ArrayList;

public class ModificarCancionActivity extends AppCompatActivity {

    private Button btnModificar;
    private EditText etNombre;
    private EditText etArtista;
    private ImageView ivPortadaModif;
    ArrayList<CancionesPOJO> cancionesArray;

    int posicion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_cancion);

        setTitle(R.string.modificarCancion);

        btnModificar = findViewById(R.id.btnModificar);
        etNombre = findViewById(R.id.etNombreModif);
        etArtista = findViewById(R.id.etArtistaModif);
        ivPortadaModif = findViewById(R.id.ivPortadaModif);

        Intent i = getIntent();
        cancionesArray = i.getParcelableArrayListExtra("arrayCanciones");
        posicion = i.getIntExtra("posicion", 0);

        etNombre.setHint(cancionesArray.get(posicion).getNombre());
        etArtista.setHint(cancionesArray.get(posicion).getArtista());
        int resId = getResources().getIdentifier(cancionesArray.get(posicion).getPortada(), "drawable", getPackageName());
        ivPortadaModif.setImageResource(resId);

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNombre.getText().toString().isEmpty()) {
                    Toast.makeText(ModificarCancionActivity.this, R.string.avisoNombre, Toast.LENGTH_SHORT).show();
                } else if (etArtista.getText().toString().isEmpty()) {
                    Toast.makeText(ModificarCancionActivity.this, R.string.avisoArtista, Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ModificarCancionActivity.this);
                    builder.setTitle(getString(R.string.modificar));
                    builder.setIcon(R.drawable.modificar);
                    builder.setMessage(getString(R.string.avisoModificar) + "\n" +
                            "\n" + getString(R.string.nombreCancion) + etNombre.getText() +
                            "\n" + getString(R.string.artistaCancion) + etArtista.getText());
                    builder.setPositiveButton(getString(R.string.aceptar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(0);
                            if (etNombre.getText().toString().isEmpty()) {
                                Toast.makeText(ModificarCancionActivity.this, R.string.avisoNombre, Toast.LENGTH_SHORT).show();
                            } else if (etArtista.getText().toString().isEmpty()) {
                                Toast.makeText(ModificarCancionActivity.this, R.string.avisoArtista, Toast.LENGTH_SHORT).show();
                            } else {
                                Intent resultadoIntent = new Intent();
                                resultadoIntent.putExtra("nombreAntiguo", cancionesArray.get(posicion).getNombre());
                                resultadoIntent.putExtra("artistaAntiguo", cancionesArray.get(posicion).getArtista());
                                resultadoIntent.putExtra("nombreCancion", etNombre.getText().toString());
                                resultadoIntent.putExtra("nombreArtista", etArtista.getText().toString());
                                setResult(RESULT_OK, resultadoIntent);
                                finish();
                            }
                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancelar), null);
                    builder.show();
                }
            }
        });
    }
}