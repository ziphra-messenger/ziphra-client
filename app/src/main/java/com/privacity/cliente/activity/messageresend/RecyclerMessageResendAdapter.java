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
import com.privacity.cliente.singleton.toast.SingletonToastManager;
import com.privacity.cliente.util.CallbackAction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.AllArgsConstructor;

public class RecyclerMessageResendAdapter extends RecyclerView.Adapter<RecyclerMessageResendAdapter.RecyclerHolder> {
    private final Activity activity;
    private final List<ItemListMessageResend> items;
    private final List<ItemListMessageResend> originalItems;
    private final RecyclerItemClick itemClick;
    private final HashMap<String,Boolean> gruposChequeados = new HashMap<>();
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
            holder.cbMessageResendGrupoSelect.setEnabled(false);
            holder.fondo.setClickable(true);
            holder.fondo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SingletonToastManager.getInstance().showToadShort(activity, "tu rol es solo lectura");
                }
            });
/*            {
                DialogsToShow.MyListener listener = DialogsToShow.noAdminDialog(activity, holder.fondo,item.getGrupo().getIdGrupo());
                listener.setActionBeforeMessage(new CallbackActionCb(holder));
            }
            {
                DialogsToShow.MyListener listener = DialogsToShow.noAdminDialog(activity, holder.cbMessageResendGrupoSelect,item.getGrupo().getIdGrupo());
                listener.setActionBeforeMessage(new CallbackActionCb(holder));
            }*/
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

        private final RecyclerHolder holder;

        @Override
        public void action() {
            holder.cbMessageResendGrupoSelect.setChecked(false);
        }
    }
    private void ListenerNotReadOnly(View v, @NotNull RecyclerHolder holder, ItemListMessageResend item) {
        if (v instanceof LinearLayout){
            holder.cbMessageResendGrupoSelect.setChecked(!holder.cbMessageResendGrupoSelect.isChecked());
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
                i.setChecked(this.gruposChequeados.containsKey(i.getGrupo().getIdGrupo()));
                items.add(i);
            }
        }
        else {

            items.clear();
            for (ItemListMessageResend i : originalItems) {
                if (i.getGrupo().getName().toLowerCase().contains(strSearch.toLowerCase())) {
                    i.setChecked(this.gruposChequeados.containsKey(i.getGrupo().getIdGrupo()));

                    items.add(i);
                }
            }

        }
        notifyDataSetChanged();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private final View fondo;
        private final TextView tvMssageResendGrupoName;
        private final CheckBox cbMessageResendGrupoSelect;
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
