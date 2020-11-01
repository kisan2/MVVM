package com.example.mvvm;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;

import java.util.List;

public class NoteRepository {
    private Notedao notedao;
    private LiveData<List<Note>> allnotes;

    public NoteRepository(Application application){
        NoteDatabase database = NoteDatabase.getInstance(application);
        notedao = database.noteDao();
        allnotes = notedao.getAllNotes();

    }

    public void insert(Note note){
        new InsertNoteAsyncTask(notedao).execute(note);

    }
    public void update(Note note){
        new UpdateNoteAsyncTask(notedao).execute(note);

    }
    public void delete(Note note){
        new DeleteNoteAsyncTask(notedao).execute(note);

    }
    public void deleteAllNotes(){
        new DeleteAllNoteAsyncTask(notedao).execute();

    }

    public LiveData<List<Note>> getAllnotes() {
        return allnotes;
    }
    //insert
    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void> {
        private Notedao notedao;
        private InsertNoteAsyncTask(Notedao notedao){
            this.notedao = notedao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            notedao.insert(notes[0]);

            return null;
        }
    }
//delete
private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void> {
    private Notedao notedao;
    private UpdateNoteAsyncTask(Notedao notedao){
        this.notedao = notedao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        notedao.update(notes[0]);

        return null;
    }
}
//update
private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void> {
    private Notedao notedao;
    private DeleteNoteAsyncTask(Notedao notedao){
        this.notedao = notedao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        notedao.delete(notes[0]);

        return null;
    }
}
//deleteallnotes
private static class DeleteAllNoteAsyncTask extends AsyncTask<Void,Void,Void> {
    private Notedao notedao;
    private DeleteAllNoteAsyncTask(Notedao notedao){
        this.notedao = notedao;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        notedao.deleteAllNotes();
        return null;
    }
}

}
