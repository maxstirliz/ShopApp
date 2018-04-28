package lymansky.artem.shopapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.realm.Realm;
import io.realm.RealmResults;
import lymansky.artem.shopapp.R;
import lymansky.artem.shopapp.model.Product;
import lymansky.artem.shopapp.model.Utils;

/**
 * Created by artem on 3/14/2018.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private TextView mTotal;
    private RealmResults<Product> products;
    private Realm mRealm;


    public ProductAdapter(Realm realm, TextView total) {
        mRealm = realm;
        products = realm.where(Product.class)
                .equalTo("included", true)
                .findAll();
        mTotal = total;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.mName.setText(product.getName());
        holder.mPrice.setText(Utils.getCurrencyValue(product.getPrice() * product.getNumber()));
        holder.mIsIncluded.setChecked(product.isIncluded());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

//    INNER CLASS

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private CheckBox mIsIncluded;
        private TextView mName;
        private TextView mPrice;
        private ImageButton mDeleteButton;

        public ProductViewHolder(View v) {
            super(v);
            mIsIncluded = v.findViewById(R.id.card_checkbox);
            mName = v.findViewById(R.id.card_name);
            mPrice = v.findViewById(R.id.card_price);
            mDeleteButton = v.findViewById(R.id.card_delete);

            mIsIncluded.setOnClickListener(this);
            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.card_checkbox:
                    String uuid = products.get(getAdapterPosition()).getId();
                    CheckBox checkBox = (CheckBox) v;
                    final boolean check = checkBox.isChecked();
                    final Product product = mRealm.where(Product.class).equalTo(Product.ID, uuid).findFirst();
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            product.setIncluded(check);
                            refreshTotal();
                        }
                    });
                    break;
                case R.id.card_delete:
                    final String id = products.get(getAdapterPosition()).getId();
                    mRealm.executeTransaction(new Realm.Transaction(){

                        @Override
                        public void execute(Realm realm) {
                            realm.where(Product.class).equalTo(Product.ID, id).findFirst().deleteFromRealm();
                            refreshTotal();
                        }
                    });
                    break;
            }
        }
    }

    private void refreshTotal() {
        double value = 0;
        for(Product product : products) {
            if(product.isIncluded()) {
                value += product.getPrice() * product.getNumber();
            }
        }
        mTotal.setText(Utils.getCurrencyValue(value));
    }
}
