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
import java.util.Map;

import in.arnavgupta.badootransactionapp.models.SkuTransactions;
import in.arnavgupta.badootransactionapp.models.Transaction;

/**
 * Created by championswimmer on 19/4/16.
 */
public class DataUtils {

    public static final String TAG = "DatUtils";

    private static CurrencyConverter currencyConverter = null;

    private static Map<String, Double> curRateMap = new HashMap<>();

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

    public static Map<String, SkuTransactions> loadTransactions (Context c) throws JSONException {
        Map<String, SkuTransactions> transactionMap = new HashMap<>();
        String transactionJson = loadJSONFromAsset(c, "transactions.json");
        JSONArray transArray = new JSONArray(transactionJson);
        Transaction tmpTrans;
        SkuTransactions transList;
        for (int i = 0; i < transArray.length(); i++) {
            JSONObject transObj = transArray.getJSONObject(i);
            tmpTrans = new Transaction();
            tmpTrans.amount = (double) transObj.getDouble("amount");
            tmpTrans.currency = transObj.getString("currency");
            tmpTrans.SKU = transObj.getString("sku");

            if (transactionMap.containsKey(tmpTrans.SKU)) {
                //Log.d(TAG, "loadTransactions: "+ tmpTrans.SKU + " exists");
                transList = transactionMap.get(tmpTrans.SKU);
                transList.transactions.add(tmpTrans);
                transactionMap.put(tmpTrans.SKU, transList);
            } else {
                transList = new SkuTransactions();
                transList.transactions = new ArrayList<>();
                transList.SKU = tmpTrans.SKU;
                transList.transactions.add(tmpTrans);
                transactionMap.put(tmpTrans.SKU, transList);
            }

        }
        return transactionMap;
    }

    public static double convertToGBP(String currency, double amount, Context c) {
        double reqRate;
        if (curRateMap.containsKey(currency)) {
            reqRate = curRateMap.get(currency);
        } else {
            Log.d(TAG, "convertToGBP: Start Vertex = " + currency);
            reqRate = getCurrencyConverter(c).convertCurrency(currency, "GBP", 1);
            curRateMap.put(currency, reqRate);
        }
        return reqRate * amount;
    }

    public static CurrencyConverter getCurrencyConverter (Context c) {

        if (currencyConverter == null) {
            currencyConverter = new CurrencyConverter();

            String rateJsonString = loadJSONFromAsset(c, "rates.json");
            JSONArray rateArr = null;
            try {
                rateArr = new JSONArray(rateJsonString);
            } catch (JSONException e) {
                Log.e(TAG, "getCurrencyConverter: rates.json is malformed", e);
                return null;
            }

            for (int i = 0; i < rateArr.length(); i++) {
                JSONObject rateObj = null;
                String from, to;
                double rate;
                try {
                    rateObj = rateArr.getJSONObject(i);
                    from = rateObj.getString("from");
                    to = rateObj.getString("to");
                    rate = (double) rateObj.getDouble("rate");
                } catch (JSONException e) {
                    Log.e(TAG, "getCurrencyConverter: This rate is malformed", e);
                    e.printStackTrace();
                    break;
                }

                if (to.equals("GBP")) {
                    curRateMap.put(from, rate);
                }
                if (from.equals("GBP")) {
                    curRateMap.put(to, (1/rate));
                }

                boolean success = currencyConverter.setExchangeRate(
                        from,
                        to,
                        rate
                );
                Log.d(TAG, "getCurrencyConverter: Added rate from " + from + " to " + to + " and reverse = " + success);
            }
        }

        return currencyConverter;
    }
}
