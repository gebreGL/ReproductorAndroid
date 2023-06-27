package com.example.reproductor.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class CancionesPOJO implements Comparable<CancionesPOJO>, Parcelable {

    private int id;
    private String nombre;
    private String artista;
    private String portada;
    private String ruta;

    public CancionesPOJO(String nombre, String artista, String portada, String ruta) {
        this.nombre = nombre;
        this.artista = artista;
        this.portada = portada;
        this.ruta = ruta;
    }

    public CancionesPOJO() {}

    protected CancionesPOJO(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        artista = in.readString();
        portada = in.readString();
        ruta = in.readString();
    }

    public static final Creator<CancionesPOJO> CREATOR = new Creator<CancionesPOJO>() {
        @Override
        public CancionesPOJO createFromParcel(Parcel in) {
            return new CancionesPOJO(in);
        }

        @Override
        public CancionesPOJO[] newArray(int size) {
            return new CancionesPOJO[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    @Override
    public int compareTo(CancionesPOJO o) {
        return this.nombre.compareTo(o.nombre);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre);
        dest.writeString(artista);
        dest.writeString(portada);
        dest.writeString(ruta);
    }
}
