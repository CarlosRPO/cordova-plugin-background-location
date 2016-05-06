package co.com.ingeneo.backgroundlocation;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by crestrepo on 5/6/2016.
 */
public class RestFullRequest {

    public static String httpGetData(Map<String, String> params, String mURL) {
        String response = "";
        HttpURLConnection urlConnection = null;
        try {
            mURL = mURL.replace(" ", "%20");
            StringBuilder sb = new StringBuilder(mURL);

            if (params != null && !params.isEmpty()) {
                sb.append("?");

                int cont = 0;
                Iterator entries = params.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) entries.next();
                    String key = entry.getKey();
                    String value = entry.getValue();

                    if (cont > 0) {
                        sb.append("&");
                    }

                    String query = key + "=" + value;
                    sb.append(query);

                    cont++;
                }
            }

            URL url = new URL(sb.toString());

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            // Acciones a realizar con el flujo de datos
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sbResponse = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sbResponse.append(line);
            }
            in.close();

            response = sbResponse.toString();

        } catch (IllegalStateException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }finally {
            urlConnection.disconnect();
        }

        return response;

    }
}
