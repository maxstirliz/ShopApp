package lymansky.artem.shopapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import lymansky.artem.shopapp.R;
import lymansky.artem.shopapp.adapters.ProductAdapter;
import lymansky.artem.shopapp.model.Product;
import lymansky.artem.shopapp.model.RealmController;
import lymansky.artem.shopapp.utils.TextUtils;

public class CartManagerActivity extends AppCompatActivity {

    private static final long ANIMATION_DURATION = 150L;

    private RecyclerView mRv;
    private ProductAdapter adapter;
    private TextView mTotal;
    private RealmController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_manager);

//        Initializations
        controller = RealmController.getInstance();
        mTotal = findViewById(R.id.cart_manager_total);
        mRv = findViewById(R.id.cart_manager_rv);
        adapter = new ProductAdapter(mTotal);

//        Animation settings
        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setRemoveDuration(ANIMATION_DURATION);

//        RV setup
        mRv.setHasFixedSize(true);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(adapter);
        mRv.setItemAnimator(animator);
        mTotal.setText(getTotal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }

    private String getTotal() {
        double value = 0;
        for(Product product : controller.getProducts()) {
            value += product.getPrice() * product.getNumber();
        }
        return TextUtils.getCurrencyValue(value);
    }
}
