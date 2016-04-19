package in.arnavgupta.badootransactionapp.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import in.arnavgupta.badootransactionapp.models.Rate;
import in.arnavgupta.badootransactionapp.models.SkuTransactions;
import in.arnavgupta.badootransactionapp.models.Transaction;

/**
 * Created by championswimmer on 20/4/16.
 */
public class DataLoaders {

    public static final String TAG = "DataLoaders";

    public static void loadTransactions(Context c, OnTransactionsLoadedListener onTrLoadListener) {
        Map<String, SkuTransactions> transactionMap = new TreeMap<>();
        String transactionJson = DataUtils.loadJSONFromAsset(c, "transactions.json");

        try {
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
            onTrLoadListener.onTransactionsLoaded(transactionMap);
        } catch (JSONException je) {
            Log.e(TAG, "loadTransactions: Could not load JSON", je);
        }
    }

    public static void loadCurrencies(Context c, OnRatesLoadedListener onRatesLoadedListener) {
        List<Rate> rates = new ArrayList<>();
        String rateJsonString = DataUtils.loadJSONFromAsset(c, "rates.json");
        JSONArray rateArr = null;
        try {
            rateArr = new JSONArray(rateJsonString);
            Rate tmpRate;

            for (int i = 0; i < rateArr.length(); i++) {
                JSONObject rateObj = null;
                tmpRate = new Rate();
                rateObj = rateArr.getJSONObject(i);
                tmpRate.from = rateObj.getString("from");
                tmpRate.to = rateObj.getString("to");
                tmpRate.rate = (double) rateObj.getDouble("rate");

                rates.add(tmpRate);

            }
            onRatesLoadedListener.onRatesLoaded(rates);
        } catch (JSONException je) {
            Log.e(TAG, "loadCurrencies: Could not load rates JSON", je);
        }
    }

    /**
     * Turning the transactions loader into a background function,
     * implementing a callback which notifies when all transactions
     * are loaded from the JSON
     */
    public interface OnTransactionsLoadedListener {
        public void onTransactionsLoaded(Map<String, SkuTransactions> skuTransactionsMap);
    }

    /**
     * Turning rates loader into a background function, and implementing
     * a callback to notify when all rates are loaded.
     */
    public interface OnRatesLoadedListener {
        public void onRatesLoaded(List<Rate> rateList);
    }
}
