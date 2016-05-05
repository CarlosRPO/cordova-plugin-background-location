package co.com.ingeneo.backgroundlocation;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by crestrepo on 5/5/2016.
 */
public class ServiceLocation extends Service implements LocationListener {

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    private Context mContext;
    protected LocationManager locationManager;
    private Timer timer = null;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private static final int SERVICE_FRECUENCY = 1000 * 60 * 2; // 2 MINUTOS

    public Location getLocation() {
        Location location = null;
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.removeUpdates(ServiceLocation.this);
            }
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                //location = getLocation(LocationManager.GPS_PROVIDER);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }

                if (location == null) {
                    // getting network status
                    isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    if (isNetworkEnabled) {
                        //location = getLocation(LocationManager.NETWORK_PROVIDER);
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                }
            } else {
                // getting network status
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (isNetworkEnabled) {
                    //location = getLocation(LocationManager.NETWORK_PROVIDER);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
/*
    private Location getLocation(String provider) {
        Location _location = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    showSettingsAlert();
                }
            }
        }
        locationManager.requestLocationUpdates(provider, 1, 1, this);
        if (locationManager != null) {
            _location = locationManager.getLastKnownLocation(provider);
        }
        return _location;
    }
*/
    /**
     * Muestra el dialogo con el mensajes de solicitud de prender el gps
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("¿Desea activar el GPS?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void onCreate() {
        super.onCreate();
        if (mContext == null) {
            mContext = this;
        }
        startService();
    }

    public void onDestroy() {
        super.onDestroy();
        // Detenemos el servicio
        stopService();
    }

    public void startService() {
        try {
            // Creamos el timer
            timer = new Timer();
            // Configuramos lo que tiene que hacer
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    executeTask();
                }
            }, 0, SERVICE_FRECUENCY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopService() {
        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTask() {
        mHandler.post(new Runnable() {
            public void run() {
                Location location = getLocation();
                if (location != null) {
                    Log.i("PLUGIN", String.valueOf(location.getLatitude()));
                    Log.i("PLUGIN", String.valueOf(location.getLongitude()));
                    // TODO: Implementación de envío de localización hacia el servidor
                }
            }
        });
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(0, null);
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location arg0) {
    }

    @Override
    public void onProviderDisabled(String arg0) {
    }

    @Override
    public void onProviderEnabled(String arg0) {
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }
}