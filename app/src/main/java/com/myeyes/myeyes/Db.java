package com.myeyes.myeyes;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db extends SQLiteOpenHelper {

    static final String dbName="MyEyes";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase dbs;


    public Db(Context context) {
        super(context, dbName, null,33);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try{

            //Tabla que contiene la información básica del usuario
            db.execSQL("CREATE TABLE usuario (id INTEGER PRIMARY KEY , nombre_usuario TEXT)");

            //Tabla de obstaculos
            db.execSQL("CREATE TABLE obstaculo (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " tipo integer , longitud Double, latitud double);");
        }catch (SQLException ex){
            System.out.println("Salida "+ex.getMessage());
        }catch (Exception ex){
            System.out.println("Salida "+ex.getMessage());
        }
    }
 @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
