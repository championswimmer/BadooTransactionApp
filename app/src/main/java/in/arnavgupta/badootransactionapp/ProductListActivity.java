package in.arnavgupta.badootransactionapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import in.arnavgupta.badootransactionapp.models.SkuTransactions;
import in.arnavgupta.badootransactionapp.utils.DataLoaders;


public class ProductListActivity extends AppCompatActivity {

    public static final String TAG = "ProdListAct";

    public static final String EXTRA_SKU_TRANSACTIONS = "sku_transactions";
    //A Map with each object containing a list of all transactions of a particular SKU
    //Ideally this should go into a database, and then we can query WHERE SKU = "xyz"
    static Map<String, SkuTransactions> transactionsMap;
    ListView productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productList = (ListView) findViewById(R.id.product_list);

        DataLoaders.loadTransactions(this, new DataLoaders.OnTransactionsLoadedListener() {
            @Override
            public void onTransactionsLoaded(Map<String, SkuTransactions> skuTransactionsMap) {
                transactionsMap = skuTransactionsMap;
                ProductListAdapter productListAdapter = new ProductListAdapter(transactionsMap);

                productList.setAdapter(productListAdapter);
            }
        });


        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getBaseContext(), TransactionListActivity.class);
                String sku = (String) ((TextView) view.findViewById(R.id.text_product_sku)).getText();
                i.putExtra(EXTRA_SKU_TRANSACTIONS, sku);
                startActivity(i);
            }
        });


    }

    class ProductListAdapter extends BaseAdapter {
        private final ArrayList skuList;

        public ProductListAdapter(Map<String, SkuTransactions> map) {
            skuList = new ArrayList();
            skuList.addAll(map.entrySet());
        }

        @Override
        public int getCount() {
            return skuList.size();
        }

        @Override
        public Map.Entry<String, SkuTransactions> getItem(int position) {
            return (Map.Entry) skuList.get(position);
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
                result = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_products, parent, false);
            } else {
                result = convertView;
            }

            Map.Entry<String, SkuTransactions> item = getItem(position);

            // TODO replace findViewById by ViewHolder

            ((TextView) result.findViewById(R.id.text_product_sku)).setText(item.getKey());
            int totalTrans;
            try {
                totalTrans = item.getValue().transactions.size();
            } catch (Exception e) {
                totalTrans = 0;
            }
            ((TextView) result.findViewById(R.id.text_num_trans)).setText(String.format(getString(R.string.num_transactions), totalTrans));


            return result;
        }
    }


}
