package com.example.mvvm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int  ADD_NOTE_REQUEST=1;
    public static final int  EDIT_NOTE_REQUEST=2;

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_notes);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNotes.class);
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update RecycleView
                adapter.setNotes(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        //editing
        adapter.setOnItemListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNotes.class);
                intent.putExtra(AddEditNotes.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNotes.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNotes.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNotes.EXTRA_PRIORITY, note.getPriority());
                startActivityForResult(intent,EDIT_NOTE_REQUEST);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_NOTE_REQUEST && resultCode==RESULT_OK){
            String title = data.getStringExtra(AddEditNotes.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNotes.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNotes.EXTRA_PRIORITY,1);


            Note note = new Note(title, description, priority);
            noteViewModel.insert(note);

            Toast.makeText(this, "note saved", Toast.LENGTH_SHORT).show();

        }else if (requestCode == EDIT_NOTE_REQUEST && resultCode==RESULT_OK) {
            int id = data.getIntExtra(AddEditNotes.EXTRA_ID,-1);
            
            if(id==-1){

                Toast.makeText(this, "Note cannot be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditNotes.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNotes.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNotes.EXTRA_PRIORITY,1);


            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);

            Toast.makeText(this, "note updated", Toast.LENGTH_SHORT).show();

            

        }else
         {
            Toast.makeText(this, "note not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "all notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}