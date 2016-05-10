package co.com.ingeneo.backgroundlocation;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by crestrepo on 5/5/2016.
 */
public class BackgroundLocationPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            Map<String, String> params = new HashMap<String, String>();
            if (args != null && args.length() > 0) {
                JSONObject obj = args.getJSONObject(0);

                Iterator<String> keysItr = obj.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) obj.get(key);
                    params.put(key, value);
                }
            }

            ServiceLocation sl = new ServiceLocation(params);

            Intent servicio = new Intent(cordova.getActivity(), sl.getClass());
            cordova.getActivity().startService(servicio);
            callbackContext.success();
            return true;
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Intent servicio = new Intent(cordova.getActivity(), ServiceLocation.class);
        cordova.getActivity().stopService(servicio);
    }
}
