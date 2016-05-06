package co.com.ingeneo.backgroundlocation;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by crestrepo on 5/6/2016.
 */
public class Util {

    public static String getPhoneIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }
}
