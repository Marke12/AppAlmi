package com.example.appalmi.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Usuario.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "UsuariosBD";
    private static volatile AppDatabase sInstance;

    public abstract UsuarioDao usuarioDao();

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration() // Si la versión de la app cambia o es menor que la del móvil, destruye y recrea limpia
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return sInstance;
    }

    // onCreate solo se ejecuta UNA vez cuando la base de datos se crea por primera vez en el dispositivo
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            AppExecutors.getInstance().getDiskIO().execute(() -> {
                if (sInstance != null) {
                    UsuarioDao dao = sInstance.usuarioDao();
                    dao.insertar(new Usuario("Almi", "Almi123"));
                }
            });
        }
    };
}