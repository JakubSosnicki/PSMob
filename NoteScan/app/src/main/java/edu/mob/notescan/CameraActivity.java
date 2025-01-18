package edu.mob.notescan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.cast.framework.media.ImagePicker;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
//import com.google.mlkit.vision.text.TextRecognizerOptions;

import CropImageView.Guidelines;


public class CameraActivity extends AppCompatActivity {

    Button button_capture, button_copy;
    private TextView textViewResult;
    private static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        button_capture = findViewById(R.id.button_capture);
        button_copy = findViewById(R.id.button_copy);
        textViewResult = findViewById(R.id.textViewResult);

        button_copy.setOnClickListener(v -> {
            String textToCopy = textViewResult.getText().toString();
            if (!textToCopy.isEmpty()) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("OCR Text", textToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Skopiowano tekst do schowka", Toast.LENGTH_SHORT).show();
            }
        });


        // Przycisk powrotu do ekranu głównego
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v ->  finish());

        // Obsługa innych funkcjonalności (np. zapis zdjęcia)
        ImageButton buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> Toast.makeText(this, "Funkcja zapisu nie została jeszcze zaimplementowana.", Toast.LENGTH_SHORT).show());


        button_capture.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Pobieranie obrazu z kamery
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Rozpocznij proces OCR
            recognizeTextFromImage(imageBitmap);
        }
    }

    private void recognizeTextFromImage(Bitmap imageBitmap) {
        // Konwersja obrazu na InputImage
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);

        TextRecognizerOptions options = new TextRecognizerOptions.Builder().build();

        // Utworzenie instancji rozpoznawacza tekstu z domyślnymi opcjami
        TextRecognizer recognizer = TextRecognition.getClient(options);

        // Rozpoczęcie procesu OCR
        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    String recognizedText = result.getText();  // Pobranie rozpoznanego tekstu
                    textViewResult.setText(recognizedText);   // Wyświetlenie tekstu w TextView
                })
                .addOnFailureListener(e -> {
                    textViewResult.setText("Błąd podczas rozpoznawania tekstu: " + e.getMessage());
                });
    }


}






// TO - DO:
// Zapis zdjęcia (do zaimplementowania)