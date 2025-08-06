package com.privacity.cliente.common.component.selectview;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.ItemListGrupo;

import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.cliente.singleton.toast.SingletonToastManager;
import com.privacity.common.enumeration.MessageState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class RecyclerSelectViewAdapter extends RecyclerView.Adapter<RecyclerSelectViewAdapter.RecyclerHolder> {
    private final List<Object> items;

    private RecyclerItemClick itemClick;
    private final Activity activity;
    public RecyclerSelectViewAdapter(Activity activity, List<Object> items) {
        this.items = items;
        this.activity = activity;

    }


    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_select_viewl, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final Object item = items.get(position);


    }


    @Override
    public int getItemCount() {
        return items.size();
    }




    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private final TextView name;

        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            name = itemView.findViewById(R.id.common__select__view__name);



        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListGrupo item);
    }
}
