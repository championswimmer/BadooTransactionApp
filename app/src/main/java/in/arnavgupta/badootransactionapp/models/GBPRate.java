package in.arnavgupta.badootransactionapp.models;

/**
 * Created by championswimmer on 19/4/16.
 */
public class GBPRate {

    /*
    This is specifically used for conversion to and from GBP as the base.

    Basically to convert to GBP, getGBPRate(foreignCurrency).rate * foreignAmount;
     */

    String currency; //The foreign currency, for eg, USD
    float rate; // Here rate stands for: 1 GBP = rate * USD, or 1 USD = 1 GBP / rate
}
