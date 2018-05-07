package lymansky.artem.shopapp.model;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by artem on 3/20/2018.
 */

public class Product extends RealmObject {

    private static final String TAG = Product.class.getSimpleName();

    public static final String ID = "id";
    public static final String INCLUDED = "included";
    public static final String LISTED = "listed";
    public static final String BOUGHT = "bought";

    @PrimaryKey
    private long id;
    private String name;
    private double price;
    private double number;
    private boolean included;
    private boolean listed;
    private boolean bought;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }

    public double getTotal() {
        return price * number;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public boolean isListed() {
        return listed;
    }

    public void setListed(boolean listed) {
        this.listed = listed;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }
}
