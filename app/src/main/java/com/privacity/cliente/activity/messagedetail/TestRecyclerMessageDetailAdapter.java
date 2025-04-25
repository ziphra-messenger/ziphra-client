package com.privacity.cliente.activity.messagedetail;

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

import lombok.Data;


public class TestRecyclerMessageDetailAdapter extends RecyclerView.Adapter<TestRecyclerMessageDetailAdapter.RecyclerHolder> {
    private final List<ItemListMessageDetail> items;

    private RecyclerItemClick itemClick;
    private final Activity activity;
    public TestRecyclerMessageDetailAdapter(Activity activity, List<ItemListMessageDetail> items) {
        this.items = items;
        this.activity = activity;


    }


    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view_message_detail, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final ItemListMessageDetail item = items.get(position);
        item.holder = holder;


        //holder.tvGruposUnreadCount.setText();

       /* if (item.getMessageDetail().buildIdMessageDetailToMap().equals(SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail().buildIdMessageDetailToMap())
        && !ObservatorMensajes.getInstance().getMensajesPorId(item.getMessageDetail().buildIdMessageToMap()).isSystemMessage()){
            holder.tvMessageDetailItemUsername.setText(SingletonValues.getInstance().getMessageDetailSeleccionado().getNombreMostrado());
        }else{*/

            holder.tvMessageDetailItemUsername.setText("fewfwef");


        //}

        holder.tvMessageDetailItemState.setText("RecyclerMessageDetailAdapter");



        //}



        holder.tvMessageDetailItemStateTilde.setTextColor(Color.BLACK);



    }

    private void setIconStateListener(View v, String strid){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonToastManager.getInstance().showToadShort(
                        SingletonCurrentActivity.getInstance().get(),
                        SingletonCurrentActivity.getInstance().get().getString(

                                SingletonCurrentActivity.getInstance().get().getResources().getIdentifier(strid, "string", SingletonCurrentActivity.getInstance().get().getPackageName())));


            }});

    }
    @Override
    public int getItemCount() {
        return items.size();
    }




    public class RecyclerHolder extends RecyclerView.ViewHolder {
        public final TextView tvMessageDetailItemUsername;
        public final TextView tvMessageDetailItemState;
        public final TextView tvMessageDetailItemStateTilde;
        public final View itemContent;
        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            tvMessageDetailItemState = itemView.findViewById(R.id.bt_message_detail_item_state);
            tvMessageDetailItemStateTilde = itemView.findViewById(R.id.bt_message_detail_item_state_tilde);

            tvMessageDetailItemUsername = itemView.findViewById(R.id.tv_message_list_text);
            itemContent = itemView.findViewById(R.id.item_list_view_message_detail__item_content);

            tvMessageDetailItemUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvMessageDetailItemUsername.setSingleLine(!tvMessageDetailItemUsername.isSingleLine());
                }
            });
            tvMessageDetailItemUsername.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    tvMessageDetailItemUsername.setSingleLine(!tvMessageDetailItemUsername.isSingleLine());
                    return true;
                }
            });

        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListGrupo item);
    }
}
