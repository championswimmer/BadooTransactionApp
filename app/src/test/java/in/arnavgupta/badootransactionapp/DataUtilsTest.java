package in.arnavgupta.badootransactionapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import in.arnavgupta.badootransactionapp.models.Rate;
import in.arnavgupta.badootransactionapp.utils.CurrencyConverter;
import in.arnavgupta.badootransactionapp.utils.DataUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by championswimmer on 20/4/16.
 */
public class DataUtilsTest {

    List<Rate> rates = new ArrayList<>();
    CurrencyConverter curConv;

    @Before
    public void loadRates () {
        Rate r = new Rate();
        r.from = "GBP"; r.to = "EUR"; r.rate = 0.91;
        rates.add(r);
        r = new Rate();
        r.from = "CAD"; r.to = "EUR"; r.rate = 0.76;
        rates.add(r);
        r = new Rate();
        r.from = "USD"; r.to = "CAD"; r.rate = 1.21;
        rates.add(r);

        curConv = DataUtils.getCurrencyConverter(rates);

    }

    @Test
    public void convToGBPtest() {
        System.out.println(curConv);
        assertEquals(1.10, DataUtils.convertToGBP("EUR", 1, curConv), 0.006); // 1.1 =  1 / 0.91
        assertEquals(0.83, DataUtils.convertToGBP("CAD", 1, curConv), 0.006); // 0.83 = 0.76 / 0.91
    }
}
