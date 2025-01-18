package edu.mob.notescan;

import static android.provider.Telephony.Mms.Part.FILENAME;

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
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.core.MatOfFloat;



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
        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonSave = findViewById(R.id.buttonSave);


        // Przycisk powrotu do ekranu głównego
        buttonBack.setOnClickListener(v ->  finish());


        // Przycisk do skopipowania tekstu po zakończonej obróbce
        button_copy.setOnClickListener(v -> {
            String textToCopy = textViewResult.getText().toString();
            if (!textToCopy.isEmpty() && !textToCopy.equals("Wynik OCR pojawi się tutaj")) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("OCR Text", textToCopy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Skopiowano tekst do schowka", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Brak tekstu do skopiowania", Toast.LENGTH_SHORT).show();
            }
        });


        // Przycisk do zapisu tekstu jak .txt w pamięci urządznia
        buttonSave.setOnClickListener(v -> {
            String textToSave = textViewResult.getText().toString();

            if (!textToSave.isEmpty() && !textToSave.equals("Wynik OCR pojawi się tutaj")) {
                saveTextToFile(textToSave);
            } else {
                Toast.makeText(this, "Brak tekstu do zapisania", Toast.LENGTH_SHORT).show();
            }
        });


        // Przycisk otwierający kamere
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
        // metoda do rozpoznania tekstu ze zdjęcia i wyswietlenia go w textViewResult
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


    // Funkcja zapisująca tekst do pliku w pamięci wewnętrznej
    private void saveTextToFile(String text) {
        FileOutputStream fos = null;
        BufferedWriter writer = null;
        try {
            // Otwórz strumień do zapisu pliku
            fos = openFileOutput(FILENAME, MODE_PRIVATE);  // Zapisywanie w trybie prywatnym
            writer = new BufferedWriter(new OutputStreamWriter(fos));

            // Zapisz tekst do pliku
            writer.write(text);
            writer.newLine();  // Dodaj nową linię po zapisaniu tekstu

            Toast.makeText(this, "Tekst został zapisany w pliku", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Błąd zapisu do pliku", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}




