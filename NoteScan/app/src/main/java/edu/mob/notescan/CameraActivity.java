package edu.mob.notescan;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



        // Przycisk wstecz
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> {
            // Powrót do ekranu głównego
            finish();
        });

        // Obsługa innych funkcjonalności (np. zapis zdjęcia)
        ImageButton buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> Toast.makeText(this, "Funkcja zapisu nie została jeszcze zaimplementowana.", Toast.LENGTH_SHORT).show());
        // Zapis zdjęcia (do zaimplementowania)

        // aparat
        button2

    }
}
