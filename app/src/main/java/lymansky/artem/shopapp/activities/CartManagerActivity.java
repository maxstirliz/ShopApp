package lymansky.artem.shopapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import lymansky.artem.shopapp.R;
import lymansky.artem.shopapp.model.Product;
import lymansky.artem.shopapp.utils.TextUtils;

public class CartManagerActivity extends AppCompatActivity {

    private static final long ANIMATION_DURATION = 150L;

    private RecyclerView mRv;
    private ProductAdapter adapter;
    private TextView mTotal;

    private Realm realmInstance;
    private RealmResults<Product> productsBought;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_manager);

//        Initializations
        realmInstance = Realm.getDefaultInstance();
        productsBought = realmInstance.where(Product.class)
                .equalTo(Product.BOUGHT, true)
                .findAll().sort(Product.ID);
        mTotal = findViewById(R.id.cart_manager_total);
        mRv = findViewById(R.id.cart_manager_rv);
        adapter = new ProductAdapter();

//        Animation settings
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setRemoveDuration(ANIMATION_DURATION);

//        RV setup
        mRv.setHasFixedSize(true);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(adapter);
        mRv.setItemAnimator(animator);
        mTotal.setText(getTotal());

        productsBought.addChangeListener(new RealmChangeListener<RealmResults<Product>>() {
            @Override
            public void onChange(RealmResults<Product> products) {
                productsBought = products;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmInstance.close();
    }

    private String getTotal() {
        double value = 0;
        for(Product product : productsBought) {
            if(product.isIncluded()) {
                value += product.getTotal();
            }
        }
        return TextUtils.getCurrencyValue(value);
    }

    private class ProductViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        private CheckBox included;
        private TextView name;
        private TextView price;
        private ImageButton delete;

        private Product boundProduct;

        public ProductViewHolder(View itemView) {
            super(itemView);
            included = itemView.findViewById(R.id.card_checkbox);
            name = itemView.findViewById(R.id.card_name);
            price = itemView.findViewById(R.id.card_price);
            delete = itemView.findViewById(R.id.card_delete);

            included.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.card_checkbox:
                    final boolean isIncluded = ((CheckBox) view).isChecked();
                    realmInstance.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Product product = realmInstance.where(Product.class)
                                    .equalTo(Product.ID, boundProduct.getId())
                                    .findFirst();
                            product.setIncluded(isIncluded);
                        }
                    });
                    mTotal.setText(getTotal());
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.card_delete:
                    if(boundProduct.isListed()) {
                        realmInstance.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Product product = realmInstance.where(Product.class)
                                        .equalTo(Product.ID, boundProduct.getId())
                                        .findFirst();
                                product.setBought(false);
                                product.setIncluded(false);
                            }
                        });
                        adapter.notifyItemChanged(getAdapterPosition());
                        mTotal.setText(getTotal());
                    } else {
                        realmInstance.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmInstance.where(Product.class)
                                        .equalTo(Product.ID, boundProduct.getId())
                                        .findFirst().deleteFromRealm();
                            }
                        });
                        adapter.notifyDataSetChanged();
                        mTotal.setText(getTotal());
                    }
                    break;
            }
        }
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
            return new ProductViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            Product product = productsBought.get(position);
            holder.name.setText(product.getName());
            holder.price.setText(TextUtils.getCurrencyValue(product.getTotal()));
            holder.included.setChecked(product.isIncluded());
            holder.boundProduct = product;
        }

        @Override
        public int getItemCount() {
            return productsBought.size();
        }
    }
}
