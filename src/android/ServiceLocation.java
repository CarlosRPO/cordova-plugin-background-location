package co.com.ingeneo.backgroundlocation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
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

    private static int SERVICE_FREQUENCY = 1000 * 60 * 3; // 3 minutos, frecuencia por defecto
    private static final String FREQUENCY_KEY = "frequency";
    private static final String URL_KEY = "url";

    private static Map<String, String> args;

    public ServiceLocation(Map<String, String> args) {
        super();
        this.args = args;

        // Recuperamos y establecemos la frecuencia de recuperación de localización
        if (args != null && !args.isEmpty()) {
            if (args.containsKey(FREQUENCY_KEY)) {
                SERVICE_FREQUENCY = Integer.parseInt(args.get(FREQUENCY_KEY));
            }
        }
    }

    // Instancia del Servicio
    public void onCreate() {
        super.onCreate();
        if (mContext == null) {
            mContext = this;
        }
        // Iniciamos tarea de recupación de posiciones
        startService();
    }

    // Creación de hilo que ejecuta el proceso
    public void startService() {
        try {
            // Creamos el timer
            timer = new Timer();
            // Configuramos la tarea con la respectiva frecuencia
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    executeTask();
                }
            }, 0, SERVICE_FREQUENCY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Detenemos el servicio
        stopService();
    }

    // Método que se encuarga de detener proceso de localización
    public void stopService() {
        try {
            timer.cancel();
            stopService(new Intent(mContext, this.getClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ejecución de la tarea
    private void executeTask() {
        mHandler.post(new Runnable() {
            public void run() {
                // Obtenemos localización
                Location location = getLocation();
                if (location != null) {
                    // Recuperamos latitud
                    String latitude = String.valueOf(location.getLatitude());
                    // Recuperamos longitud
                    String longitude = String.valueOf(location.getLongitude());

                    // Verificación de conectividad a internet
                    if (Util.isOnline(mContext)) {
                        Map<String, String> params = new HashMap<String, String>();
                        if (args != null && !args.isEmpty() && args.containsKey(URL_KEY)) {
                            Iterator entries = args.entrySet().iterator();
                            while (entries.hasNext()) {
                                Map.Entry<String, String> thisEntry = (Map.Entry<String, String>) entries.next();
                                String key = thisEntry.getKey();
                                if (!key.equalsIgnoreCase(FREQUENCY_KEY) || !key.equalsIgnoreCase(URL_KEY)) {
                                    String value = thisEntry.getValue();
                                    params.put(key, value);
                                }
                            }

                            // Además de los parámetros recibidos se sincroniza la Latitud y Longitud
                            params.put("latitud", latitude);
                            params.put("longitud", longitude);

                            // Sincronización de localización
                            new ConsumirWS(params, args.get(URL_KEY)).execute();
                        }
                    }
                }
            }
        });
    }

    // Método que se encarga de obtener la posición a través del GPS o a través de su conexión a internet
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

    // Tarea asincrona encargada de sincronizar las posiciones obtenidad a través del Web Service (URL) recibida como parámetro
    class ConsumirWS extends AsyncTask<Void, Void, String> {

        private Map<String, String> parametros;
        private String url;

        public ConsumirWS(Map<String, String> parametros, String url) {
            this.parametros = parametros;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                // Llamado al Web Service
                return RestFullRequest.httpGetData(parametros, url);
            } catch (Exception e) {}
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(null);
            Log.i("PLUGIN: RESPONSE", res);
        }
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