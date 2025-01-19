package edu.mob.notescan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
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

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.BreakIterator;


public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    // One Button
    Button BSelectImage;
    ImageButton Photo;
    // One Preview Image
    ImageView IVPreviewImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton buttonCamera = findViewById(R.id.buttonCamera);
        ImageButton buttonImport = findViewById(R.id.buttonImport);
        TextView statusText = findViewById(R.id.statusText);

        // Sprawdzanie połączenia internetowego i ustawianie tekstu oraz jego koloru
        if (isNetworkAvailable()) {
            statusText.setText("Jesteś online");
            statusText.setTextColor(getResources().getColor(R.color.colorOnline));
        } else {
            statusText.setText("Brak połączenia z internetem");
            statusText.setTextColor(getResources().getColor(R.color.colorOffline));
        }


        // Przycisk do odpalenia kamery
        buttonCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Poproś o pozwolenie na aparat
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

            }
            else {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        });


        // Przycisk do wyboru zdjęcia z galerii
        buttonImport.setOnClickListener(v -> {
            // Sprawdzamy wersję Androida
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    // Jeśli nie mamy uprawnień, przekierowujemy do ustawień, aby użytkownik mógł je przyznać
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                } else {
                    openGallery();
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                } else {
                    // Jeśli nie mamy uprawnień prosimy o nie
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            GALLERY_REQUEST_CODE);
                }
            }
        });
    }


    // Funkcja otwierająca galerie
    private void openGallery() {
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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);

            } else {
                Toast.makeText(this, "Odmówiono dostępu do aparatu.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Funkcja przejścia do CameraActivity z plikiem
    private void openCameraActivity2(String imagePath) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("imagePath", imagePath);
        startActivity(intent);
    }


    // Funkcja do przejścia do CameraActivity z bitmapą z zrobionym zdjęciem
    private void openCameraActivity(Bitmap bitmap) {
        Intent intent = new Intent(this, CameraActivity.class);
        // Przekazywanie bitmapy przez Intent (tutaj używamy putExtra)
        Bundle bundle = new Bundle();
        bundle.putParcelable("imageBitmap", bitmap);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    // Obsługa wyniku wybrania pliku z galerii
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData(); // Uzyskanie URI wybranego obrazu
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Pobieranie obrazu z kamery
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Po wykonaniu zdjęcia przechodzimy do openCameraActivity i aktywujemy ocr dla zdjęcia
            openCameraActivity(imageBitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                        // Zapis bitmapy do pliku
                        File cacheDir = getCacheDir();
                        File file = new File(cacheDir, "temp_image.jpg");
                        FileOutputStream fos = new FileOutputStream(file);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.close();
                        openCameraActivity2(file.getAbsolutePath());
                    } catch (IOException e) {
                        Toast.makeText(this, "Błąd ładowania obrazu", Toast.LENGTH_SHORT).show();
                    }
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



}






