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


public class RecyclerMessageDetailAdapter extends RecyclerView.Adapter<RecyclerMessageDetailAdapter.RecyclerHolder> {
    private final List<ItemListMessageDetail> items;
    private final List<ItemListMessageDetail> originalItems;
    private RecyclerItemClick itemClick;
    private final Activity activity;
    public RecyclerMessageDetailAdapter(Activity activity,List<ItemListMessageDetail> items) {
        this.items = items;
        this.activity = activity;
        if (items.size() != 0 ){
        if (ObserverMessage.getInstance().getMensajesPorId(items.get(0).messageDetail.buildIdMessageToMap()).getShowingAnonimo()) {
            Comparator comparator = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return ((ItemListMessageDetail)o1).getMessageDetail().getEstado().compareTo(
                            ((ItemListMessageDetail)o2).getMessageDetail().getEstado());

                }
            };

            this.items.sort(comparator);
        }else {
            Comparator comparator = new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String nn1=((ItemListMessageDetail) o1).getMessageDetail().getUsuarioDestino().getNickname();
                    if (((ItemListMessageDetail) o1).getMessageDetail().getUsuarioDestino().getNickname() == null){
                        nn1="";
                    }
                    return (nn1.compareTo(
                            ((ItemListMessageDetail) o2).getMessageDetail().getUsuarioDestino().getNickname()));

                }
            };

            this.items.sort(comparator);
            }
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

       /* if (item.getMessageDetail().buildIdMessageDetailToMap().equals(SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail().buildIdMessageDetailToMap())
        && !ObservatorMensajes.getInstance().getMensajesPorId(item.getMessageDetail().buildIdMessageToMap()).isSystemMessage()){
            holder.tvMessageDetailItemUsername.setText(SingletonValues.getInstance().getMessageDetailSeleccionado().getNombreMostrado());
        }else{*/
        if (ObserverMessage.getInstance().getMensajesPorId(item.messageDetail.buildIdMessageToMap()).getShowingAnonimo()) {
            holder.tvMessageDetailItemUsername.setText(activity.getString(R.string.general__message_type__anonimous));
        } else {
            holder.tvMessageDetailItemUsername.setText(item.getMessageDetail().getUsuarioDestino().getNickname());
        }

        //}

        holder.tvMessageDetailItemState.setText(item.messageDetail.getEstado().name());



        //}

        holder.tvMessageDetailItemState.setText(item.messageDetail.getEstado().name());

        holder.tvMessageDetailItemStateTilde.setTextColor(Color.BLACK);
        if (item.messageDetail.getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)) {
            holder.tvMessageDetailItemStateTilde.setTextColor(Color.RED);
            holder.tvMessageDetailItemStateTilde.setText("❌");
            holder.tvMessageDetailItemUsername.setText(ErrorDialog.getErrorDescription(activity,item.messageDetail.getError()));
            setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__my_message_error_not_send");
        }
    else if (item.messageDetail.getEstado().equals(MessageState.MY_MESSAGE_SENT)) {
            holder.tvMessageDetailItemStateTilde.setTextColor(Color.GREEN);
            setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__message_sent");
                holder.tvMessageDetailItemStateTilde.setText( "✉️".trim());
            } else if (item.messageDetail.getEstado().equals(MessageState.MY_MESSAGE_SENDING)) {
            setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__message_sending");
                holder.tvMessageDetailItemStateTilde.setText("\uD83D\uDCE8");
            } else if (item.messageDetail.getEstado().equals(MessageState.DESTINY_SERVER)) {
            setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__destiny_server");
                holder.tvMessageDetailItemStateTilde.setText("✓");
            } else if (item.messageDetail.getEstado().equals(MessageState.DESTINY_DELIVERED)) {
                holder.tvMessageDetailItemStateTilde.setText("✓✓");
            setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__destiny_delivered");
            } else if (item.messageDetail.getEstado().equals(MessageState.DESTINY_READ)) {
                if (item.getMessageDetail().isHideRead()){
                    setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__destiny_read_hidden");
                    holder.tvMessageDetailItemStateTilde.setTextColor(Color.parseColor("#2A2A6FB8"));
                    holder.tvMessageDetailItemStateTilde.setText("✓✓✓");
                }else {
                    holder.tvMessageDetailItemStateTilde.setTextColor(Color.BLUE);
                    setIconStateListener(holder.tvMessageDetailItemStateTilde, "item_list_view_message_detail__state__destiny_read");
                    holder.tvMessageDetailItemStateTilde.setText("✓✓✓");

                }
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
        private final TextView tvMessageDetailItemUsername;
        private final TextView tvMessageDetailItemState;
        private final TextView tvMessageDetailItemStateTilde;
        private final View itemContent;
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
