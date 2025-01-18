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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


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


        IVPreviewImage = findViewById(R.id.imageView);
        ImageButton buttonCamera = findViewById(R.id.buttonCamera);
        ImageButton buttonImport = findViewById(R.id.buttonImport);
        TextView statusText = findViewById(R.id.statusText);

        // Sprawdzanie połączenia internetowego i ustawianie tekstu oraz koloru
        if (isNetworkAvailable()) {
            statusText.setText("Jesteś online");
            statusText.setTextColor(getResources().getColor(R.color.colorOnline));
        } else {
            statusText.setText("Brak połączenia z internetem");
            statusText.setTextColor(getResources().getColor(R.color.colorOffline));
        }


        // Ikona aparatu
        buttonCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Poproś o pozwolenie na aparat
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

            } else {
                // Przejdź do ekranu aparatu
                openCameraActivity();
            }
        });


        // Przycisk do wyboru zdjęcia z galerii
        buttonImport.setOnClickListener(v -> {
            // Sprawdzamy wersję Androida
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Sprawdzamy, czy aplikacja ma uprawnienia do dostępu do wszystkich plików (od Androida 11)
                if (!Environment.isExternalStorageManager()) {
                    // Jeśli nie mamy uprawnień, przekierowujemy do ustawień, aby użytkownik mógł je przyznać
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                } else {
                    // Jeśli mamy uprawnienia, otwieramy galerię
                    openGallery();
                }
            } else {
                // Dla starszych wersji Androida, sprawdzamy uprawnienia do pamięci
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Jeśli mamy uprawnienia, otwieramy galerię
                    openGallery();
                } else {
                    // Jeśli nie mamy uprawnień, prosimy o nie
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            GALLERY_REQUEST_CODE);
                }
            }
        });
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
                openCameraActivity();
            } else {
                Toast.makeText(this, "Odmówiono dostępu do aparatu.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Funkcja przejścia do CameraActivity
    private void openCameraActivity() {
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
                //Toast.makeText(this, "Wybrano obraz: " + selectedImage.toString(), Toast.LENGTH_SHORT).show();
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
                if (selectedImageUri != null) {
                    openCameraActivity();
                    // CameraActivity.onActivityResult();
                    // update the preview image in the layout
                    //IVPreviewImage.setImageURI(selectedImageUri);
                }
            }
        }
    }


    // Funkcja sprawdzająca połączenie z internetem
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }


    protected void imageChooser() {
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






