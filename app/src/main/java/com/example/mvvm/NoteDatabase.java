package com.example.mvvm;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;

    public abstract Notedao noteDao();
    public static synchronized NoteDatabase getInstance(Context context){
        if(instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note database").fallbackToDestructiveMigration().addCallback(roomCallback).build();

        }
        return instance;
    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private Notedao notedao;

        private PopulateDbAsyncTask(NoteDatabase Db){
            notedao =Db.noteDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            notedao.insert(new Note(1,"tittle1","description1",1));
            notedao.insert(new Note(2,"tittle2","description2",2));
            notedao.insert(new Note(3,"tittle3","description3",3));
            return null;
        }
    }
}
