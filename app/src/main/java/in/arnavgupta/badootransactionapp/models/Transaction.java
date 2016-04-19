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

    /**
     * A singleton-wrap over amountGBP, so that the calculation
     * need not take again if already calculated.
     *
     * @return
     */
    public double calcGBP (CurrencyConverter currConv) {

        if (currency.equals("GBP")) {
            amountGBP = amount;
            return amountGBP;
        }

        if (amountGBP == -1) {
            try {
                amountGBP = DataUtils.convertToGBP(currency, amount, currConv);
            } catch (ArithmeticException e) {
                amountGBP = 0;
            }
        }
        return amountGBP;
    }


}
