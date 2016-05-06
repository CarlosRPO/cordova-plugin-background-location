package co.com.ingeneo.backgroundlocation;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by crestrepo on 5/5/2016.
 */
public class BackgroundLocationPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            ServiceLocation sl = null;
            if (args != null) {
                JSONObject obj = args.getJSONObject(0);
                if (obj.has("url")) {
                    String url = obj.getString("url");
                    if (!url.isEmpty()) {
                        sl = new ServiceLocation(url);
                    }
                }
            } else {
                sl = new ServiceLocation();
            }

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
