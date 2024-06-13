package com.privacity.cliente.activity.messageresend;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.util.CallbackAction;
import com.privacity.cliente.util.DialogsToShow;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;

public class RecyclerMessageResendAdapter extends RecyclerView.Adapter<RecyclerMessageResendAdapter.RecyclerHolder> {
    private final Activity activity;
    private List<ItemListMessageResend> items;
    private List<ItemListMessageResend> originalItems;
    private RecyclerItemClick itemClick;
    private HashMap<String,Boolean> gruposChequeados = new HashMap<>();
    public RecyclerMessageResendAdapter(List<ItemListMessageResend> items, RecyclerItemClick itemClick, Activity activity) {
        this.items = items;
        this.itemClick = itemClick;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(items);
        this.activity =activity;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view_message_resend, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final ItemListMessageResend item = items.get(position);
        holder.tvMssageResendGrupoName.setText(item.getGrupo().getName());

        if (this.gruposChequeados.containsKey(item.getGrupo().getIdGrupo())) {
            item.setChecked(true);
            holder.cbMessageResendGrupoSelect.setChecked(true);
        }else{
            item.setChecked(false);
            holder.cbMessageResendGrupoSelect.setChecked(false);
        }



        if (Observers.grupo().amIReadOnly(item.getGrupo().getIdGrupo())){
            //holder.cbMessageResendGrupoSelect.setEnabled(false);
            {
                DialogsToShow.MyListener listener = DialogsToShow.noAdminDialog(activity, holder.fondo,item.getGrupo().getIdGrupo());
                listener.setActionBeforeMessage(new CallbackActionCb(holder));
            }
            {
                DialogsToShow.MyListener listener = DialogsToShow.noAdminDialog(activity, holder.cbMessageResendGrupoSelect,item.getGrupo().getIdGrupo());
                listener.setActionBeforeMessage(new CallbackActionCb(holder));
            }
        }else{
            holder.cbMessageResendGrupoSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {ListenerNotReadOnly(v, holder, item);}
            });

            holder.fondo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {ListenerNotReadOnly(v, holder, item);}
            });

        }

    }
    @AllArgsConstructor
    private class CallbackActionCb implements CallbackAction{

        private RecyclerHolder holder;

        @Override
        public void action() {
            holder.cbMessageResendGrupoSelect.setChecked(false);
        }
    }
    private void ListenerNotReadOnly(View v, @NotNull RecyclerHolder holder, ItemListMessageResend item) {
        if (v instanceof LinearLayout){
            if (holder.cbMessageResendGrupoSelect.isChecked()){
                holder.cbMessageResendGrupoSelect.setChecked(false);
            }else{
                holder.cbMessageResendGrupoSelect.setChecked(true);
            }
        }
        if (holder.cbMessageResendGrupoSelect.isChecked()){
            gruposChequeados.put(item.getGrupo().getIdGrupo(), true);
            item.setChecked(true);
            holder.cbMessageResendGrupoSelect.setChecked(true);

        }else{
            gruposChequeados.remove(item.getGrupo().getIdGrupo());
            item.setChecked(false);
            holder.cbMessageResendGrupoSelect.setChecked(false);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            items.clear();
            for (ItemListMessageResend i : originalItems) {
                    if (this.gruposChequeados.containsKey(i.getGrupo().getIdGrupo())) {
                        i.setChecked(true);
                    }else{
                        i.setChecked(false);
                    }
                    items.add(i);
            }
        }
        else {

            items.clear();
            for (ItemListMessageResend i : originalItems) {
                if (i.getGrupo().getName().toLowerCase().contains(strSearch.toLowerCase())) {
                    if (this.gruposChequeados.containsKey(i.getGrupo().getIdGrupo())) {
                        i.setChecked(true);
                    }else{
                        i.setChecked(false);
                    }

                    items.add(i);
                }
            }

        }
        notifyDataSetChanged();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private final View fondo;
        private TextView tvMssageResendGrupoName;
        private CheckBox cbMessageResendGrupoSelect;
        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            fondo = itemView.findViewById(R.id.resend_item_list_layout);
            tvMssageResendGrupoName = itemView.findViewById(R.id.tv_message_resend_grupo_name);
            cbMessageResendGrupoSelect = itemView.findViewById(R.id.cb_message_resend_grupo_select);
        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListMessageResend item);
    }


}
