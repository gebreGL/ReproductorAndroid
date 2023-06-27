package com.example.reproductor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductor.POJO.CancionesPOJO;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnPlayPause, btnRepetir;
    private MediaPlayer mp;
    private SeekBar sbProgreso;
    private ImageView ivPortada;
    private TextView tvCancion, tvArtista;
    private Handler mHandler = new Handler();
    ArrayList<CancionesPOJO> cancionesArray;

    int repetir = 2, posicion = 0;

    MediaPlayer vectormp[];

    private final int CONSTANTE_NOTIFICACION = 0;
    private static final String CHANNEL_ID = "a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name);

        btnPlayPause = findViewById(R.id.btnPlay);
        btnRepetir = findViewById(R.id.btnBucle);
        ivPortada = findViewById(R.id.ivPortada);
        tvCancion = findViewById(R.id.tvCancion);
        tvArtista = findViewById(R.id.tvArtista);
        sbProgreso = findViewById(R.id.sbProgreso);

        Intent i = getIntent();
        cancionesArray = i.getParcelableArrayListExtra("arrayCanciones");
        posicion = i.getIntExtra("posicion", 0);

        vectormp = new MediaPlayer[cancionesArray.size()];

        for (int j = 0; j < cancionesArray.size(); j++) {
            String rutaCancion = cancionesArray.get(j).getRuta();
            String nombreArchivo = rutaCancion.substring(rutaCancion.lastIndexOf(".") + 1);
            int resourceId = getResources().getIdentifier(nombreArchivo, "raw", getPackageName());
            vectormp[j] = MediaPlayer.create(this, resourceId);
        }

        asociar();

        mp = vectormp[posicion];

        sbProgreso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progreso = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    progreso = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mActualizarProgreso);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(progreso);
                mHandler.post(mActualizarProgreso);
            }
        });

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                sbProgreso.setMax(mp.getDuration());
                mHandler.post(mActualizarProgreso);
            }
        });

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (posicion < vectormp.length - 1) {

                    // Detener la reproducción actual
                    if (vectormp[posicion].isPlaying()) {
                        vectormp[posicion].stop();
                        vectormp[posicion].release(); // Liberar el MediaPlayer actual
                    }
                    // Reproducir la siguiente canción
                    posicion++;
                    mp = vectormp[posicion];
                    asociar();
                    mp.setOnCompletionListener(this);  // Agregar nuevamente el listener para la siguiente canción
                    mp.start();
                    btnPlayPause.setBackgroundResource(R.drawable.pausa);
                    sbProgreso.setMax(mp.getDuration());
                    sbProgreso.setProgress(0);
                    mHandler.post(mActualizarProgreso);
                    notificacion();
                } else {
                    Toast.makeText(MainActivity.this, R.string.avisoCanciones, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Runnable mActualizarProgreso = new Runnable() {
        @Override
        public void run() {
            if (mp != null && mp.isPlaying()) { // Verificar si el reproductor está en un estado válido
                sbProgreso.setProgress(mp.getCurrentPosition());
            }
            mHandler.postDelayed(this, 1000);
        }
    };


    public void playPause(View view) {
        if (vectormp[posicion].isPlaying()) {
            vectormp[posicion].pause();
            btnPlayPause.setBackgroundResource(R.drawable.play);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
            Toast.makeText(this, R.string.pausa, Toast.LENGTH_SHORT).show();
        } else {
            vectormp[posicion].start();
            btnPlayPause.setBackground(getResources().getDrawable(R.drawable.pausa));
            notificacion();
            Toast.makeText(this, R.string.play, Toast.LENGTH_SHORT).show();
        }
    }

    public void repetir(View view) {
        if (repetir == 1) {
            btnRepetir.setBackgroundResource(R.drawable.flecha);
            Toast.makeText(this, R.string.noRepetir, Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(false);
            repetir = 2;
        } else {
            btnRepetir.setBackgroundResource(R.drawable.bucle);
            Toast.makeText(this, R.string.repetir, Toast.LENGTH_SHORT).show();
            vectormp[posicion].setLooping(true);
            sbProgreso.setProgress(0);
            repetir = 1;
        }
    }

    public void siguiente(View view) throws IOException {
        if (posicion < vectormp.length - 1) {
            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop();
                vectormp[posicion].prepare();
            }
            posicion++;
            asociar();
            sbProgreso.setProgress(0);
            mHandler.removeCallbacks(mActualizarProgreso);
            mp = vectormp[posicion];
            sbProgreso.setMax(mp.getDuration());
            mp.start();
            btnPlayPause.setBackgroundResource(R.drawable.pausa);
            mHandler.post(mActualizarProgreso);
            notificacion();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayPause.setBackgroundResource(R.drawable.play);
                }
            });
        } else {
            Toast.makeText(this, R.string.avisoCanciones, Toast.LENGTH_SHORT).show();
        }
    }

    public void anterior(View view) throws IOException {
        if (posicion > 0) {
            if (vectormp[posicion].isPlaying()) {
                vectormp[posicion].stop();
                vectormp[posicion].prepare();
            }
            posicion--;
            asociar();
            sbProgreso.setProgress(0);
            mHandler.removeCallbacks(mActualizarProgreso);
            mp = vectormp[posicion];
            sbProgreso.setMax(mp.getDuration());
            mp.start();
            btnPlayPause.setBackgroundResource(R.drawable.pausa);
            mHandler.post(mActualizarProgreso);
            notificacion();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btnPlayPause.setBackgroundResource(R.drawable.play);
                }
            });
        } else {
            Toast.makeText(this, R.string.avisoCanciones, Toast.LENGTH_SHORT).show();
        }
    }


    public void asociar() {
        ArrayList<CancionesPOJO> cancionesList = new ArrayList<>();
        for (int i = 0; i < cancionesArray.size(); i++) {
            // Aquí asumo que cancion.getPortada() devuelve la ruta o URL de la imagen
            CancionesPOJO cancionCompleta = new CancionesPOJO();
            cancionCompleta.setNombre(cancionesArray.get(i).getNombre());
            cancionCompleta.setArtista(cancionesArray.get(i).getArtista());
            cancionCompleta.setPortada(cancionesArray.get(i).getPortada());
            cancionesList.add(cancionCompleta);
        }
        int resId = getResources().getIdentifier(cancionesList.get(posicion).getPortada(), "drawable", getPackageName());
        ivPortada.setImageResource(resId);
        tvCancion.setText(cancionesList.get(posicion).getNombre());
        tvArtista.setText(cancionesList.get(posicion).getArtista());
    }

    public void notificacion() {
        // Verifico la versión de android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear el canal de notificación
            createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.play)
                    .setContentTitle(getString(R.string.reproduciendo))
                    .setSound(null)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(cancionesArray.get(posicion).getNombre()
                            + "\n" + getString(R.string.de) + cancionesArray.get(posicion).getArtista()))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            getResources().getIdentifier(cancionesArray.get(posicion).getPortada(),
                                    "drawable", getPackageName())))
                    .setAutoCancel(false);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(CONSTANTE_NOTIFICACION, builder.build());
        } else {
            // Versiones anteriores a Android 8.0 (API 26)
            int resId = getResources().getIdentifier(cancionesArray.get(posicion).getPortada(), "drawable", getPackageName());
            Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "notificacion")
                    .setSmallIcon(R.drawable.play)
                    .setContentTitle(getString(R.string.reproduciendo))
                    .setContentText(cancionesArray.get(posicion).getNombre()
                            + "\n" + R.string.de + " " + cancionesArray.get(posicion).getArtista())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setSound(null)
                    .setLargeIcon(bm);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notificacion";
            String description = "Canal para notificación de la canción que se está reproduciendo";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.enableVibration(false);
            channel.setDescription(description);
            channel.setSound(null, null);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.salir));
        builder.setIcon(R.drawable.salir);
        builder.setMessage(getString(R.string.avisoSalir1) +
                "\n" + getString(R.string.avisoSalir2));
        builder.setPositiveButton(getString(R.string.opcionSi), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acciones para salir de la actividad o realizar otras acciones necesarias
                vectormp[posicion].stop();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.opcionNo), null);
        builder.show();
    }
}