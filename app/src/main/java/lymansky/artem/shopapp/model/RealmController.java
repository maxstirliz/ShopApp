package lymansky.artem.shopapp.model;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmController {

    private static RealmController instance;
    private final Realm realm;

    private RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance() {
        if(instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    public RealmResults<Product> getProducts() {
        return realm.where(Product.class)
                .equalTo(Product.INCLUDED, true)
                .findAll();
    }

    public RealmResults<Product> getItems() {
        return realm.where(Product.class)
                .equalTo(Product.LISTED, true)
                .findAll();
    }

    public void clearCart() {
        realm.beginTransaction();
        realm.where(Product.class)
                .equalTo(Product.INCLUDED, true)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void clearList() {
        realm.beginTransaction();
        realm.where(Product.class)
                .equalTo(Product.LISTED, true)
                .findAll()
                .deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void clearAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public void refresh() {
        realm.refresh();
    }

    public void close() {
        realm.close();
    }
}
