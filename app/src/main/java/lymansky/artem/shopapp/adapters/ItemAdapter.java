package lymansky.artem.shopapp.adapters;

import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import lymansky.artem.shopapp.R;

/**
 * Created by artem on 11/25/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {


    public ItemAdapter() {
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_card, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

//  INNER CLASS

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox isBought;
        private TextView name;
        private AppCompatImageButton delete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            isBought = itemView.findViewById(R.id.item_checkbox);
            name = itemView.findViewById(R.id.item_name);
            delete = itemView.findViewById(R.id.item_delete);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.item_delete:
                    break;
                case R.id.item_background:
                    break;
            }
        }
    }
}

