package edu.mob.notescan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;


    // One Button
    Button BSelectImage;
    ImageButton Photo;
    // One Preview Image
    ImageView IVPreviewImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // register the UI widgets with their appropriate IDs
        //BSelectImage = findViewById(R.id.button);
        IVPreviewImage = findViewById(R.id.imageView);

        // handle the Choose Image button to trigger
        // the image chooser function
//        BSelectImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageChooser();
//            }
//        });

        // Ikona aparatu
        ImageButton buttonCamera = findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Poproś o pozwolenie na aparat
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

            } else {
                // Przejdź do ekranu aparatu
                openCamera();
            }
        });

        // Ikona importu (opcjonalnie możesz obsłużyć import tutaj)
        ImageButton buttonImport = findViewById(R.id.buttonImport);
        buttonImport.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                } else {
                   openGallery();
                }
            }

        });
        //"Funkcja importu nie została jeszcze zaimplementowana.", Toast.LENGTH_SHORT).show());

    }

    private void openGallery() {
        // Tworzenie intencji do otwierania galerii
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }


    // Obsługa wyniku żądania uprawnień
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Uprawnienia do galerii przyznane.", Toast.LENGTH_SHORT).show();
                // Implementacja otwierania galerii
            } else {
                openGallery();
            }
        }
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Odmówiono dostępu do aparatu.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Funkcja przejścia do ekranu aparatu
    private void openCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }


    // Obsługa wyniku wybrania pliku z galerii
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData(); // Uzyskanie URI wybranego obrazu
                // Możesz teraz wyświetlić obraz lub przesłać go dalej
                Toast.makeText(this, "Wybrano obraz: " + selectedImage.toString(), Toast.LENGTH_SHORT).show();
                // Na przykład: wyślij URI do innej aktywności lub pokaż w ImageView
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == GALLERY_REQUEST_CODE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), GALLERY_REQUEST_CODE);
    }
}






