import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CopiaMultimedia extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 345;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            // Manifest.permission.MANAGE_EXTERNAL_STORAGE // Requiere una configuración especial en Android 11+
    };

    // ... (resto de tu actividad)

    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!tienePermisos()) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                copiarArchivosMultimedia();
            }
        } else {
            // En versiones anteriores a Marshmallow, los permisos se otorgan en la instalación
            copiarArchivosMultimedia();
        }
    }

    private boolean tienePermisos() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                copiarArchivosMultimedia();
            } else {
                // El usuario denegó los permisos
                Log.e("CopiaMultimedia", "Permisos denegados");
                // Aquí puedes mostrar un mensaje al usuario o tomar alguna acción
            }
        }
    }

    private void copiarArchivosMultimedia() {
        // Obtén la ruta de la carpeta de almacenamiento externo
        File carpetaAlmacenamiento = Environment.getExternalStorageDirectory();

        // Crea una carpeta para almacenar las copias
        File carpetaCopia = new File(carpetaAlmacenamiento, "CopiaMultimedia");
        if (!carpetaCopia.exists()) {
            carpetaCopia.mkdirs();
        }

        // Realiza la copia de los archivos multimedia (fotos, videos, audios)
        copiarArchivosDesdeCarpeta(carpetaAlmacenamiento, carpetaCopia);

        // Aquí puedes implementar la lógica para enviar el correo electrónico con los archivos copiados
        // enviarCorreoElectronico(carpetaCopia);
    }

    private void copiarArchivosDesdeCarpeta(File origen, File destino) {
        File[] archivos = origen.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    // Si es una carpeta, crea una carpeta correspondiente en el destino y copia recursivamente
                    File nuevaCarpeta = new File(destino, archivo.getName());
                    nuevaCarpeta.mkdirs();
                    copiarArchivosDesdeCarpeta(archivo, nuevaCarpeta);
                } else if (esArchivoMultimedia(archivo)) {
                    // Si es un archivo multimedia, cópialo al destino
                    copiarArchivo(archivo, new File(destino, archivo.getName()));
                }
            }
        }
    }

    private boolean esArchivoMultimedia(File archivo) {
        String mimeType = obtenerMimeType(archivo);
        return mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/") || mimeType.startsWith("audio/"));
    }

    private String obtenerMimeType(File archivo) {
        Uri uri = Uri.fromFile(archivo);
        String mimeType = getContentResolver().getType(uri);
        return mimeType;
    }

    private void copiarArchivo(File origen, File destino) {
        try {
            FileInputStream inStream = new FileInputStream(origen);
            FileOutputStream outStream = new FileOutputStream(destino);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            Log.e("CopiaMultimedia", "Error al copiar archivo: " + e.getMessage());
        }
    }

    // ... (Métodos para enviar el correo electrónico)
}
