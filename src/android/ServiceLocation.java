package co.com.ingeneo.backgroundlocation;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
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

    private static final int SERVICE_FRECUENCY = 1000 * 10;

    private static final String WIFI_TYPE_NAME = "wifi";
    private static final String MOBILE_TYPE_NAME = "mobile";

    private static String url = "";

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

    public ServiceLocation(String url) {
        super();
        this.url = url;
    }

    public ServiceLocation() {
        super();
    }

    @Override
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
            stopService(new Intent(mContext, this.getClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTask() {
        mHandler.post(new Runnable() {
            public void run() {
                Location location = getLocation();
                if (location != null) {
                    String latitude = String.valueOf(location.getLatitude());
                    String longitude = String.valueOf(location.getLongitude());
                    Log.i("PLUGIN", latitude);
                    Log.i("PLUGIN", longitude);

                    if (isOnline(mContext)) {
                        String imei = Util.getPhoneIMEI(mContext);
                        // Sincronización de localización
                        new ConsumirWS(imei, latitude, longitude).execute();
                    }
                }
            }
        });
    }

    class ConsumirWS extends AsyncTask<Void, Void, String> {

        String imei;
        String latitude;
        String longitude;

        public ConsumirWS(String imei, String latitude, String longitude) {
            this.imei = imei;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Map<String, String> requestParams = new HashMap<String, String>();
                //requestParams.put("imei", imei);
                requestParams.put("latitud", latitude);
                requestParams.put("longitud", longitude);

                requestParams.put("cedula", imei);
                requestParams.put("ruta", "5");
                requestParams.put("distancia", "25");

                return RestFullRequest.httpGetData(requestParams, url);
            } catch (Exception e) {}
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(null);
            Log.i("PLUGIN: RESPONSE", res);
        }
    }

    private boolean isOnline(Context contex) {
        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) contex
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase(WIFI_TYPE_NAME)) {
                if (ni.isConnected()) {
                    hasConnectedWifi = true;
                }
            } else if (ni.getTypeName().equalsIgnoreCase(MOBILE_TYPE_NAME)) {
                if (ni.isConnected()) {
                    hasConnectedMobile = true;
                }
            }
        }
        return hasConnectedWifi || hasConnectedMobile;
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