package in.arnavgupta.badootransactionapp.models;

import java.util.Objects;

import in.arnavgupta.badootransactionapp.utils.CurrencyConverter;

/**
 * Created by championswimmer on 19/4/16.
 */
public class Transaction {
    public String SKU;
    public double amount;
    public String currency;
    public double amountGBP = -1;

    public double calcGBP (CurrencyConverter curCov) {

        if (currency.equals("GBP")) {
            amountGBP = amount;
            return amountGBP;
        }

        if (amountGBP == -1) {
            try {
                amountGBP = curCov.convertCurrency(currency, "GBP", amount);
            } catch (ArithmeticException e) {
                amountGBP = 0;
            }
        }
        return amountGBP;
    }


}
