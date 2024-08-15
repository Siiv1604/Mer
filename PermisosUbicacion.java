import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermisosUbicacion extends AppCompatActivity implements LocationListener {

    private static final int PERMISSION_REQUEST_CODE = 456;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION // Solo si necesitas rastrear en segundo plano
    };

    private static final String PREFS_NAME = "MisPreferencias";
    private static final String PREF_PERMISOS_OTORGADOS = "permisosOtorgados";

    private LocationManager locationManager;

    // ... (resto de tu actividad)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... (tu código de inicialización)

        // Verifica si los permisos ya fueron otorgados en ejecuciones anteriores
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean permisosOtorgados = prefs.getBoolean(PREF_PERMISOS_OTORGADOS, false);

        if (!permisosOtorgados) {
            // Si es la primera vez, solicita los permisos al usuario
            solicitarPermisos();
        } else {
            // Si los permisos ya fueron otorgados, inicia el seguimiento de la ubicación
            iniciarSeguimientoUbicacion();
        }
    }

    private void solicitarPermisos() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Explica al usuario por qué necesitas los permisos
            new AlertDialog.Builder(this)
                    .setTitle("Permisos de ubicación")
                    .setMessage("Esta aplicación necesita acceder a tu ubicación para [explica el propósito]")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PermisosUbicacion.this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            // Solicita los permisos directamente
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisos otorgados, guarda la preferencia y inicia el seguimiento
                guardarPreferenciaPermisos(true);
                iniciarSeguimientoUbicacion();
            } else {
                // Permisos denegados, muestra un mensaje o redirige a la configuración
                mostrarMensajePermisosDenegados();
            }
        }
    }

    private void guardarPreferenciaPermisos(boolean otorgados) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_PERMISOS_OTORGADOS, otorgados);
        editor.apply();
    }

    private void iniciarSeguimientoUbicacion() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // Envía la ubicación a tu servicio de Google Maps
        enviarUbicacionAGoogleMaps(location);
    }

    // ... (Otros métodos de LocationListener que puedes implementar si es necesario)

    private void enviarUbicacionAGoogleMaps(Location location) {
        // Implementa la lógica para enviar la ubicación a tu servicio de Google Maps
        // Puedes utilizar la API de Google Maps o Firebase para esto
        Log.d("PermisosUbicacion", "Ubicación: " + location.getLatitude() + ", " + location.getLongitude());
    }

    private void mostrarMensajePermisosDenegados() {
        new AlertDialog.Builder(this)
                .setTitle("Permisos denegados")
                .setMessage("La aplicación necesita permisos de ubicación para funcionar correctamente. Por favor, habilítalos en la configuración.")
                .setPositiveButton("Ir a configuración", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Abre la pantalla de configuración de la aplicación
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
