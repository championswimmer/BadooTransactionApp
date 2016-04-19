package in.arnavgupta.badootransactionapp;

import org.junit.Before;
import org.junit.Test;

import in.arnavgupta.badootransactionapp.utils.CurrencyConverter;

import static org.junit.Assert.assertEquals;

/**
 * Created by championswimmer on 19/4/16.
 */
public class CurrencyConverterTest {

    private CurrencyConverter mCurrencyConverter;

    @Before
    public void prepareCurrencyConverter () {
        mCurrencyConverter = new CurrencyConverter();

        mCurrencyConverter.setExchangeRate("USD", "GBP", 0.5);
        mCurrencyConverter.setExchangeRate("CAD", "USD", 0.5);
        mCurrencyConverter.setExchangeRate("AUD", "CAD", 0.5);

    }

    @Test
    public void testCurrencyConverter () {
        // Using a delta of 0.006 to pad for ceil and floor errors
        assertEquals(0.25, mCurrencyConverter.convertCurrency("CAD", "GBP", 1.0), 0.006); // Should be 0.5 * 0.5 = 0.25
        assertEquals(0.12, mCurrencyConverter.convertCurrency("AUD", "GBP", 1.0), 0.006); // Should be 0.125 ~ 0.12
    }

}
