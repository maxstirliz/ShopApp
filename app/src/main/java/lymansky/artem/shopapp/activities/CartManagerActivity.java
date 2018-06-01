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
import lymansky.artem.shopapp.utils.RealmHelper;
import lymansky.artem.shopapp.utils.TextUtils;

public class CartManagerActivity extends AppCompatActivity {

    private static final long ANIMATION_DURATION = 150L;

    private RecyclerView rv;
    private ProductAdapter adapter;
    private TextView total;

    private RealmHelper realmHelper;
    private Realm realmInstance;
    private RealmResults<Product> productsBought;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_manager);

//        Initializations
        realmHelper = RealmHelper.getHelper();
        realmInstance = realmHelper.getRealm();
        productsBought = realmHelper.getProducts();
        total = findViewById(R.id.cart_manager_total);
        rv = findViewById(R.id.cart_manager_rv);
        adapter = new ProductAdapter();

//        Animation settings
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setRemoveDuration(ANIMATION_DURATION);

//        RV setup
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.setItemAnimator(animator);
        total.setText(getTotal());

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
        realmHelper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        total.setText(getTotal());
    }

    private String getTotal() {
        double value = 0;
        for (Product product : productsBought) {
            if (product.isIncluded()) {
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
                    boolean isIncluded = ((CheckBox) view).isChecked();
                    realmHelper.setIncludedById(boundProduct.getId(), isIncluded);
                    total.setText(getTotal());
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.card_delete:
                    if (boundProduct.isListed()) {
                        realmHelper.removeBoughtById(boundProduct.getId());
                        adapter.notifyItemChanged(getAdapterPosition());
                        total.setText(getTotal());
                    } else {
                        realmHelper.removeById(boundProduct.getId());
                        adapter.notifyDataSetChanged();
                        total.setText(getTotal());
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
