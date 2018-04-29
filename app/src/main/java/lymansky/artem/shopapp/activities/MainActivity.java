package lymansky.artem.shopapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import lymansky.artem.shopapp.R;
import lymansky.artem.shopapp.model.Product;
import lymansky.artem.shopapp.model.RealmController;
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

    private RealmController controller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Initializations
        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        mAddButton = findViewById(R.id.add_button);
        mClearButton = findViewById(R.id.clear_button);
        mGoToCart = findViewById(R.id.go_to_cart_button);
        mTotal = findViewById(R.id.total);
        mName = findViewById(R.id.input_name);
        mPrice = findViewById(R.id.input_price);
        mNumber = findViewById(R.id.input_number);
        controller = RealmController.getInstance();
        mTotal.setText(getTotal());

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
                Log.v(TAG, "addButton is clicked");
                final String name = mName.getText().toString();
                final double price = TextUtils.getDouble(mPrice);
                final double number = TextUtils.getDouble(mNumber);
                if (price == 0 || number == 0) {
                    Toast.makeText(MainActivity.this, getString(R.string.toast_wrong_number_format), Toast.LENGTH_SHORT)
                            .show();
                    setFocusShowKeyboard(mPrice);
                } else {
                    controller.getRealm().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Product product = realm.createObject(Product.class, UUID.randomUUID().toString());
                            product.setName(TextUtils.checkName(name));
                            product.setPrice(price);
                            product.setNumber(number);
                            product.setBought(true);
                            product.setIncluded(true);
                            product.setListed(false);
                            clearFields();
                            setFocusShowKeyboard(mName);
                            mTotal.setText(getTotal());
                        }
                    });
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
                if(controller.getProducts().isEmpty()) {
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
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    if(!getTotal().equals(TextUtils.getCurrencyValue(0))) {
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

    private void setFocusShowKeyboard(EditText field) {
        field.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT);
    }

    private String getTotal() {
        double value = 0;
        for(Product product : controller.getProducts()) {
            value += product.getPrice() * product.getNumber();
        }
        return TextUtils.getCurrencyValue(value);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTotal.setText(getTotal());
    }
}
