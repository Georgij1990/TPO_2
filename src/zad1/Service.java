package zad1;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *
 *  @author Kocsarjan Georgij S24171
 *
 */

public class Service {

    private static Map<String, String> countries = new HashMap<>();

    String country;
    String city;
    Locale locale;
    String currencyCode;
    public Service(String country) {
        this.country = country;
        generateCountryCodes();
        this.locale = new Locale("", countries.get(this.country));
        Currency currency = Currency.getInstance(this.locale);
        this.currencyCode = currency.getCurrencyCode();
    }

    String getWeather(String city) {
        this.city = city;
        try {
            String countryCode = countries.get(this.country);
            List<Double> latLon = Utils.getLatitudeAndLongitude(countryCode, city);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("lat", String.valueOf(latLon.get(0)));
            parameters.put("lon", String.valueOf(latLon.get(1)));
            parameters.put("appid", Utils.apiKey);
            String weatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?" + ParameterStringBuilder.getParamsString(parameters);
            String response = Utils.makeHttpRequest(weatherApiUrl);
            JSONObject jsonObject = Utils.returnJSONObject(response);
            JSONObject weather = (JSONObject) jsonObject.get("main");
            return String.valueOf(weather);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    Double getRateFor(String currCode) {
        if (currencyCode.equals(currCode)) {
            return 1.0;
        }
        String exchangeRateUrl = String.format("https://v6.exchangerate-api.com/v6/88c75a74e502dbdbc7c8a94d/latest/%s", currencyCode);
        String response = Utils.makeHttpRequest(exchangeRateUrl);
        JSONObject jsonObject = Utils.returnJSONObject(response);
        JSONObject rates = (JSONObject) jsonObject.get("conversion_rates");
        return (Double) rates.get(currCode);
    }

    Double getNBPRate() {
        if (currencyCode.equals("PLN")) {
            return 1.0;
        }
        Double mid = null;
        String table = Utils.returnTable(currencyCode);
        String rateURL = String.format("http://api.nbp.pl/api/exchangerates/rates/%s/%s/", table, currencyCode);
        String response = Utils.makeHttpRequest(rateURL);
        JSONArray jsonArray = (JSONArray) Utils.returnJSONObject(response).get("rates");
        JSONObject jsonObject = (JSONObject) jsonArray.get(0);
        mid = (Double) jsonObject.get("mid");
        return 1 / mid;
    }

    public static void generateCountryCodes() {
        for (String iso: Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
    }
}
