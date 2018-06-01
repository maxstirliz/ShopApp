package lymansky.artem.shopapp.utils;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import lymansky.artem.shopapp.model.Product;

public class RealmHelper {

    private static RealmHelper helper;
    private Realm realm;
    private RealmResults<Product> productsBought;
    private RealmResults<Product> shoppingList;

    private RealmHelper() {
        realm = Realm.getDefaultInstance();
        productsBought = realm.where(Product.class)
                .equalTo(Product.BOUGHT, true)
                .findAll()
                .sort(Product.ID, Sort.DESCENDING);
        shoppingList = realm.where(Product.class)
                .equalTo(Product.LISTED, true)
                .findAll()
                .sort(Product.ID, Sort.DESCENDING);
    }

    public static RealmHelper getHelper() {
        if (helper == null) {
            helper = new RealmHelper();
        }
        return helper;
    }

    public RealmResults<Product> getProducts() {
        return productsBought;
    }

    public RealmResults<Product> getShoppingList() {
        return shoppingList;
    }

    public Realm getRealm() {
        return realm;
    }

    public void close() {
        realm.close();
    }

    public void removeById(long id) {
        final long finalId = id;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Product.class)
                        .equalTo(Product.ID, finalId)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }

    public void removeBoughtById(long id) {
        final long finalId = id;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Product product = realm
                        .where(Product.class)
                        .equalTo(Product.ID, finalId)
                        .findFirst();
                product.setBought(false);
                product.setIncluded(false);
            }
        });
    }

    public void setIncludedById(long id, boolean included) {
        final long finalId = id;
        final boolean finalIncluded = included;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Product product = realm.where(Product.class)
                        .equalTo(Product.ID, finalId)
                        .findFirst();
                product.setIncluded(finalIncluded);
            }
        });
    }
}
