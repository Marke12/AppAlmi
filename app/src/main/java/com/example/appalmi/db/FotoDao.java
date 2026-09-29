package com.example.appalmi.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.appalmi.modelos.FotoEntity;

import java.util.List;

@Dao
public interface FotoDao {
    @Insert
    void insertar(FotoEntity foto);

    @Query("SELECT * FROM fotos WHERE ubicacion = :ubicacion")
    LiveData<List<FotoEntity>> obtenerFotosPorUbicacion(String ubicacion);
}