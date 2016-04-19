package in.arnavgupta.badootransactionapp.models;

import android.content.Context;

import java.util.Objects;

import in.arnavgupta.badootransactionapp.utils.CurrencyConverter;
import in.arnavgupta.badootransactionapp.utils.DataUtils;

/**
 * Created by championswimmer on 19/4/16.
 */
public class Transaction {
    public String SKU;
    public double amount;
    public String currency;
    public double amountGBP = -1;

    public double calcGBP (Context c) {

        if (currency.equals("GBP")) {
            amountGBP = amount;
            return amountGBP;
        }

        if (amountGBP == -1) {
            try {
                amountGBP = DataUtils.convertToGBP(currency, amount, c);
            } catch (ArithmeticException e) {
                amountGBP = 0;
            }
        }
        return amountGBP;
    }


}
