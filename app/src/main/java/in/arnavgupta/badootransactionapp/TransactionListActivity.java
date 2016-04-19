package in.arnavgupta.badootransactionapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import in.arnavgupta.badootransactionapp.models.Rate;
import in.arnavgupta.badootransactionapp.models.SkuTransactions;
import in.arnavgupta.badootransactionapp.models.Transaction;
import in.arnavgupta.badootransactionapp.utils.CurrencyConverter;
import in.arnavgupta.badootransactionapp.utils.DataLoaders;
import in.arnavgupta.badootransactionapp.utils.DataUtils;

public class TransactionListActivity extends AppCompatActivity {

    public static final String TAG = "TrListAct";

    String skuName;
    ListView transListView;
    double totalGBP = 0;

    TextView textGbpTotal;

    CurrencyConverter currencyConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        transListView = (ListView) findViewById(R.id.transactions_list);
        skuName = getIntent().getStringExtra(ProductListActivity.EXTRA_SKU_TRANSACTIONS);

        SkuTransactions skuTrans = ProductListActivity.transactionsMap.get(skuName);
        final List<Transaction> transList = skuTrans.transactions;
        final TransactionListAdapter trListAdapter = new TransactionListAdapter(transList);


        DataLoaders.loadCurrencies(this, new DataLoaders.OnRatesLoadedListener() {
            @Override
            public void onRatesLoaded(List<Rate> rateList) {
                currencyConverter = DataUtils.getCurrencyConverter(getApplicationContext(), rateList);

                for (Transaction trans : transList) {
                    totalGBP += trans.calcGBP(currencyConverter);
                }
                textGbpTotal = (TextView) findViewById(R.id.text_total_gbp);

                if (textGbpTotal != null) {
                    textGbpTotal.setText(String.format(getString(R.string.total_gbp), totalGBP));
                }

                transListView.setAdapter(trListAdapter);
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(String.format(getString(R.string.trans_detail_title), skuName));
        }
    }

    class TransactionListAdapter extends BaseAdapter {
        private final List<Transaction> trList;

        public TransactionListAdapter(List<Transaction> transactionList) {
            trList = transactionList;
        }

        @Override
        public int getCount() {
            return trList.size();
        }

        @Override
        public Transaction getItem(int position) {
            return trList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO implement you own logic with ID
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View result;

            if (convertView == null) {
                result = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            } else {
                result = convertView;
            }

            Transaction trans = getItem(position);

            // TODO replace findViewById by ViewHolder pattern

            //FIXME: The values could be null
            String actualAmt = trans.currency + " " + trans.amount;

            String poundAmt = String.format(getString(R.string.gbp_trans_amount), trans.calcGBP(currencyConverter));
            ((TextView) result.findViewById(android.R.id.text1)).setText(actualAmt);
            ((TextView) result.findViewById(android.R.id.text2)).setText(poundAmt);


            return result;
        }
    }

}
