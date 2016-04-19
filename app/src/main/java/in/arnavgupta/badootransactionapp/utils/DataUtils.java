package in.arnavgupta.badootransactionapp.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.arnavgupta.badootransactionapp.models.Rate;
import in.arnavgupta.badootransactionapp.models.SkuTransactions;
import in.arnavgupta.badootransactionapp.models.Transaction;

/**
 * Created by championswimmer on 19/4/16.
 */
public class DataUtils {

    public static final String TAG = "DatUtils";

    /**
     * A private static CurrencyConverter singleton
     */
    private static CurrencyConverter currencyConverter = null;

    /**
     * curRateMap: A static map of each currency and it's multiplier for GBP
     *
     * This will prevent calculating Dijkstra everytime we want to go from
     * currency XXX to GBP. We can just lookup the map, and find the multiplier
     * if the conversion for this currency XXX to GBP has already once been calculated.
     */
    private static Map<String, Double> curRateMap = new HashMap<>();


    /**
     * A method to load x.json files from assets folder and convert them
     * into a string.
     *
     * @param c A contect
     * @param fileName The json file in assets folder which to load json data from
     * @return
     */
    public static String loadJSONFromAsset(Context c, String fileName) {
        String json = null;
        try {
            InputStream is = c.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    /**
     * Whenever we need to convert a currency, we call this method.
     * If it's a new conversion, it calculates shortest path. And saves to the
     * currency-GBP map.
     *
     * If an already-calculated conversion, it'll simple lookup from the map.
     *
     * @param currency
     * @param amount
     * @return
     */
    public static double convertToGBP(String currency, double amount,CurrencyConverter currConv) {
        double reqRate;
        if (curRateMap.containsKey(currency)) {
            reqRate = curRateMap.get(currency);
        } else {
            Log.d(TAG, "convertToGBP: Start Vertex = " + currency);
            reqRate = currConv.convertCurrency(currency, "GBP", 1);
            curRateMap.put(currency, reqRate);
        }
        return reqRate * amount;
    }

    /**
     * A helper to implement a Singleton-like pattern for the CurrencyConverter
     * instance.
     *
     * @param c Context
     * @return Singleton instance of CurrencyConverter
     */
    public static CurrencyConverter getCurrencyConverter (Context c, List<Rate> currencyRates) {

        if (currencyConverter == null) {
            currencyConverter = new CurrencyConverter();


            for (Rate rate : currencyRates){
                if (rate.to.equals("GBP")) {
                    curRateMap.put(rate.from, rate.rate);
                }
                if (rate.from.equals("GBP")) {
                    curRateMap.put(rate.to, (1/rate.rate));
                }

                boolean success = currencyConverter.setExchangeRate(
                        rate.from,
                        rate.to,
                        rate.rate
                );
            }
        }

        return currencyConverter;
    }
}
