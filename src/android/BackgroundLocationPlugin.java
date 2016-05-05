package co.com.ingeneo.backgroundlocation;

import android.content.Intent;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by crestrepo on 5/5/2016.
 */
public class BackgroundLocationPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Intent servicio = new Intent(cordova.getActivity(), ServiceLocation.class);
        cordova.getActivity().startService(servicio);
        return true;
    }
}
