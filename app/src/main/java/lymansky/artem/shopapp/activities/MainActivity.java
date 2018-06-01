package lymansky.artem.shopapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import lymansky.artem.shopapp.R;
import lymansky.artem.shopapp.model.Product;
import lymansky.artem.shopapp.utils.RealmHelper;
import lymansky.artem.shopapp.utils.TextUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final long ANIMATION_DURATION = 100L;

    private BottomSheetBehavior mBottomSheetBehavior;

    private TextView mTotal;
    private TextInputEditText mName;
    private TextInputEditText mPrice;
    private TextInputEditText mNumber;
    private Button mAddButton;
    private Button mClearButton;
    private TextView mGoToCart;
    private Switch mSwitch;
    private TextView mSwitchText;

    RealmHelper realmHelper;
    private Realm realmInstance;
    private RealmResults<Product> productsBought;
    private RealmResults<Product> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Initializations
        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        realmHelper = RealmHelper.getHelper();
        realmInstance = realmHelper.getRealm();
        productsBought = realmHelper.getProducts();
        items = realmHelper.getShoppingList();
        mSwitch = findViewById(R.id.modeSwitch);
        mSwitch.setChecked(true);
        mSwitchText = findViewById(R.id.modeText);
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mAddButton = findViewById(R.id.add_button);
        mClearButton = findViewById(R.id.clear_button);
        mGoToCart = findViewById(R.id.go_to_cart_button);
        mTotal = findViewById(R.id.total);
        mName = findViewById(R.id.input_name);
        mPrice = findViewById(R.id.input_price);
        mNumber = findViewById(R.id.input_number);
        mTotal.setText(getTotal());

        setUpMode();

//        Buttons and Listeners setup
        mNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handled = true;
                }
                return handled;
            }
        });

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString();
                final double price = TextUtils.getDouble(mPrice);
                final double number = TextUtils.getDouble(mNumber);
                if (mSwitch.isChecked() || price == 0 || number == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_wrong_number_format), Toast.LENGTH_SHORT)
                            .show();
                    setFocusShowKeyboard(mPrice);
                } else {
                    realmInstance.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Product product = realmInstance.createObject(Product.class, System.currentTimeMillis());
                            product.setName(TextUtils.checkName(name));
                            product.setPrice(price);
                            product.setNumber(number);


                            if (mSwitch.isChecked()) {
                                product.setBought(true);
                                product.setIncluded(true);
                                product.setListed(false);
                            } else {
                                product.setBought(false);
                                product.setIncluded(false);
                                product.setListed(true);
                            }
                        }
                    });
                    clearFields();
                    setFocusShowKeyboard(mName);
                    mTotal.setText(getTotal());
                }
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                setFocusShowKeyboard(mName);
            }
        });

        mGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (realmInstance.isEmpty()) {
                    Toast.makeText(MainActivity.this, "The Cart is empty", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Intent intent = new Intent(MainActivity.this, CartManagerActivity.class);
                    startActivity(intent);
                }
            }
        });

        mNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!getTotal().equals(TextUtils.getCurrencyValue(0))) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        mAddButton.setText(getString(R.string.add_button) + " " + getTotal());
                        return true;
                    } else {
                        Toast.makeText(MainActivity.this, "Do not use null values!", Toast.LENGTH_SHORT)
                                .show();
                        mPrice.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void clearFields() {
        mName.setText("");
        mPrice.setText("");
        mNumber.setText("");
        mAddButton.setText(getString(R.string.add_button));
        setFocusShowKeyboard(mName);
    }

    private void setUpMode() {
        if (mSwitch.isChecked()) {
            mNumber.setEnabled(true);
            mPrice.setEnabled(true);
            mSwitchText.setText(getString(R.string.switchOn));
        } else {
            mNumber.setEnabled(false);
            mPrice.setEnabled(false);
            mSwitchText.setText(getString(R.string.switchOff));
        }
    }

    private void setFocusShowKeyboard(EditText field) {
        field.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmInstance.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTotal.setText(getTotal());
        setUpMode();
    }

//    Adapter stuff

    protected class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        CheckBox bought;
        AppCompatImageButton delete;

        Product boundItem;

        public ItemViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            bought = itemView.findViewById(R.id.item_checkbox);
            delete = itemView.findViewById(R.id.item_delete);

            name.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_delete:
                    break;
                case R.id.item_name:
                    break;
            }
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
            return new ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            Product item = items.get(position);
            holder.boundItem = item;
            holder.name.setText(item.getName());
            holder.bought.setChecked(item.isListed());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }
}
