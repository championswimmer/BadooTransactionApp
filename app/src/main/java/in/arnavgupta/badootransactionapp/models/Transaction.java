package in.arnavgupta.badootransactionapp.models;

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
        if (amountGBP == -1) {
            amountGBP = curCov.convertCurrency(currency, "GBP", amount);
        }
        return amountGBP;
    }


}
