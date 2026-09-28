package com.example.appalmi.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface UsuarioDao {
    @Query("SELECT * FROM usuarios ORDER BY id")
    LiveData<List<Usuario>> loadAllUsuarios();

    @Insert
    void insertUsuario(Usuario usuario);

    @Update
    void updateUsuario(Usuario usuario);

    @Delete
    void delete(Usuario usuario);

    @Query("DELETE FROM usuarios")
    void deleteAll();

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario loadUsuarioById(int id);

    @Insert
    void insertar(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE usuario = :user AND password = :pass LIMIT 1")
    Usuario login(String user, String pass);
}