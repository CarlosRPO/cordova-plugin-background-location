package co.com.ingeneo.backgroundlocation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by crestrepo on 5/6/2016.
 */
public class Util {

    private static final String WIFI_TYPE_NAME = "wifi";
    private static final String MOBILE_TYPE_NAME = "mobile";

    /**
     * Método encargado de obtener el IMEI del dispositivo
     * @param context
     * @return String IMEI
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * Método encargado de verificar si el dispositivo tiene conexión a Internet
     *
     * @param contex
     * @return boolean Tiene conexión
     */
    public static boolean isOnline(Context contex) {
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
}