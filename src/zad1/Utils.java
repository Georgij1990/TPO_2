package zad1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    static String apiKey = "32ff572c7e78c1a7255af9ac0425dd10";

    static List<Double> getLatitudeAndLongitude(String countryCode, String cityName) throws UnsupportedEncodingException {
        List<Double> latLon;
        Map<String, String> parameters = new HashMap<>();
        String cityNameCountryCode = String.format("%s,%s",cityName,  countryCode);
        parameters.put("q", cityNameCountryCode);
        parameters.put("appid", apiKey);
        String endPoint = "http://api.openweathermap.org/geo/1.0/direct?" + ParameterStringBuilder.getParamsString(parameters);
        String content = makeHttpRequest(endPoint);
        latLon = returnLatitudeAndLongitude(String.valueOf(content));
        return latLon;
    }

    static String makeHttpRequest(String endPoint) {
        StringBuffer content = new StringBuffer();
        try {
            URL url = new URL(endPoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<Double> returnLatitudeAndLongitude(String text) {
        JSONArray jsonArray = returnJSONArray(text);
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        Double latitude = (Double) jsonObject.get("lat");
        Double longitude = (Double) jsonObject.get("lon");
        return Arrays.asList(latitude, longitude);
    }

    static JSONArray returnJSONArray(String text) {
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) parser.parse(text);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return jsonArray;
    }

    static JSONObject returnJSONObject(String response) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    static String returnTable(String currencyCode) {
        for (String table : Arrays.asList("a", "b", "c")) {
            String nbpRateURL = String.format("http://api.nbp.pl/api/exchangerates/tables/%s/", table);
            String response = Utils.makeHttpRequest(nbpRateURL);
            JSONArray jsonArray = Utils.returnJSONArray(response);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JSONArray ratesArray = (JSONArray) jsonObject.get("rates");
            if (checkTable(ratesArray, currencyCode)) {
                return table;
            }
        }
        return null;
    }

    static boolean checkTable(JSONArray ratesArray, String currencyCode) {
        for (Object obj : ratesArray) {
            JSONObject currencyRateJson =(JSONObject) obj;
            if (currencyRateJson.get("code").equals(currencyCode)) {
                return true;
            }
        }
        return false;
    }
}
