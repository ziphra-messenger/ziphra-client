package com.privacity.cliente.activity.messagedetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.common.enumeration.MessageState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class RecyclerMessageDetailAdapter extends RecyclerView.Adapter<RecyclerMessageDetailAdapter.RecyclerHolder> {
    private List<ItemListMessageDetail> items;
    private List<ItemListMessageDetail> originalItems;
    private RecyclerItemClick itemClick;

    public RecyclerMessageDetailAdapter(List<ItemListMessageDetail> items) {
        this.items = items;

        if (ObserverMessage.getInstance().getMensajesPorId(items.get(0).messageDetailDTO.buildIdMessageToMap()).isAnonimo()) {
            Comparator comparator = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return ((ItemListMessageDetail)o1).getMessageDetailDTO().getEstado().compareTo(
                            ((ItemListMessageDetail)o2).getMessageDetailDTO().getEstado());

                }
            };

            this.items.sort(comparator);
        }else{
            Comparator comparator = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return ((ItemListMessageDetail)o1).getMessageDetailDTO().getUsuarioDestino().getNickname().compareTo(
                    ((ItemListMessageDetail)o2).getMessageDetailDTO().getUsuarioDestino().getNickname());

                }
            };

            this.items.sort(comparator);
        }
        this.itemClick = itemClick;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(items);
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


        //holder.tvGruposUnreadCount.setText();

       /* if (item.getMessageDetailDTO().buildIdMessageDetailToMap().equals(SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO().buildIdMessageDetailToMap())
        && !ObservatorMensajes.getInstance().getMensajesPorId(item.getMessageDetailDTO().buildIdMessageToMap()).isSystemMessage()){
            holder.tvMessageDetailItemUsername.setText(SingletonValues.getInstance().getMessageDetailSeleccionado().getNombreMostrado());
        }else{*/
        if (ObserverMessage.getInstance().getMensajesPorId(item.messageDetailDTO.buildIdMessageToMap()).isAnonimo()) {
            holder.tvMessageDetailItemUsername.setText("Anonimo");
        } else {
            holder.tvMessageDetailItemUsername.setText(item.getMessageDetailDTO().getUsuarioDestino().getNickname());
        }

        //}

        holder.tvMessageDetailItemState.setText(item.messageDetailDTO.getEstado().name());



        //}

        holder.tvMessageDetailItemState.setText(item.messageDetailDTO.getEstado().name());


        if (item.messageDetailDTO.getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)) {
            holder.tvMessageDetailItemStateTilde.setText("✘");
        }
    else if (item.messageDetailDTO.getEstado().equals(MessageState.MY_MESSAGE_SENT)) {
                holder.tvMessageDetailItemStateTilde.setText("");
            } else if (item.messageDetailDTO.getEstado().equals(MessageState.MY_MESSAGE_SENDING)) {
                holder.tvMessageDetailItemStateTilde.setText("");
            } else if (item.messageDetailDTO.getEstado().equals(MessageState.DESTINY_SERVER)) {
                holder.tvMessageDetailItemStateTilde.setText("✓");
            } else if (item.messageDetailDTO.getEstado().equals(MessageState.DESTINY_DELIVERED)) {
                holder.tvMessageDetailItemStateTilde.setText("✓✓");
            } else if (item.messageDetailDTO.getEstado().equals(MessageState.DESTINY_READED)) {
                holder.tvMessageDetailItemStateTilde.setText("✓✓✓");
            }

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                intent.putExtra("itemDetail", item);
                holder.itemView.getContext().startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return items.size();
    }




    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageDetailItemUsername;
        private TextView tvMessageDetailItemState;
        private TextView tvMessageDetailItemStateTilde;
        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            tvMessageDetailItemState = itemView.findViewById(R.id.bt_message_detail_item_state);
            tvMessageDetailItemStateTilde = itemView.findViewById(R.id.bt_message_detail_item_state_tilde);

            tvMessageDetailItemUsername = itemView.findViewById(R.id.tv_message_list_text);
        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListGrupo item);
    }
}
