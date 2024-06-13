package com.privacity.cliente.activity.grupo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.restcalls.invitation.AcceptInvitationCallRest;
import com.privacity.cliente.singleton.Observers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerGrupoAdapter extends RecyclerView.Adapter<RecyclerGrupoAdapter.RecyclerHolder> {
    private List<ItemListGrupo> items;
    public List<ItemListGrupo> originalItems;
    private RecyclerItemClick itemClick;
    private GrupoActivity grupoActivity;
    private View viewInvitation;



    public RecyclerGrupoAdapter(List<ItemListGrupo> items, RecyclerItemClick itemClick, GrupoActivity grupoActivity) {
        this.items = items;
        this.itemClick = itemClick;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(items);
        this.grupoActivity = grupoActivity;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view_grupo, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final ItemListGrupo item = items.get(position);
        holder.tvTitulo.setText(item.getGrupo().getName());

        if ( item.getGrupo().getMembersOnLine() > 1 ){
            holder.grupoListItemOnline.setText((item.getGrupo().getMembersOnLine()-1)+"");
            holder.grupoListItemOnline.setVisibility(View.VISIBLE);
        }else{
            holder.grupoListItemOnline.setVisibility(View.GONE);
        }


        if (item.getGrupo().isOtherAreWritting()){
            holder.tvGrupoListWritting.setVisibility(View.VISIBLE);
        }else{
            holder.tvGrupoListWritting.setVisibility(View.GONE);
        }

        if (item.getGrupo().isGrupoInvitation()){
            holder.btGrupoInvitacion.setVisibility(View.VISIBLE);
            holder.tvGruposUnreadCount.setVisibility(View.GONE);
            holder.btGrupoInvitacion.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(grupoActivity);
                    builder.setTitle("Responder a la solicitud");

                    viewInvitation = LayoutInflater.from(grupoActivity).inflate(R.layout.custom_comp_grupo_invitation_message, null);

                    builder.setView(viewInvitation);
                    builder.setIcon(R.drawable.ic_launcher_background);

                    TextView invitationMessage = (TextView) viewInvitation.findViewById(R.id.tv_grupo_invitation_message);
                    invitationMessage.setText("Ha sido invitado por " + item.getGrupo().getGrupoInvitationDTO().getUsuarioInvitante().getNickname()
                            + " Con el rol de " + item.getGrupo().getGrupoInvitationDTO().getRole());

//        ((LinearLayout)findViewById(R.id.linear_layout_add_grupo)).getChildAt(0)

                    //builder.itsetView(invitationCode);
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                ProgressBarUtil.show(grupoActivity, grupoActivity.progressBar);
                                AcceptInvitationCallRest.call(grupoActivity, grupoActivity.progressBar,item);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ProgressBarUtil.hide(grupoActivity, grupoActivity.progressBar);
                                SimpleErrorDialog.errorDialog(grupoActivity, "ERROR ACEPTANDO INVITACION", e.getMessage()                                );
                                return;
                            }


                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNeutralButton("Ignorar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.show();
                }
            });
        }else{
            holder.btGrupoInvitacion.setVisibility(View.GONE);
            holder.tvGruposUnreadCount.setVisibility(View.VISIBLE);
        }


        //holder.tvGruposUnreadCount.setText();
        String unread = item.getUnread()+"";
        if (unread.equals("0")){
            unread ="";
            holder.tvGruposUnreadCount.setVisibility(View.INVISIBLE);
        }else {

            holder.tvGruposUnreadCount.setVisibility(View.VISIBLE);
        }

        holder.tvGruposUnreadCount.setText(unread);
        if (!item.getGrupo().isGrupoInvitation()){

            Grupo grupo = Observers.grupo().getGrupoById(item.getGrupo().getIdGrupo());

            //boolean grupogetPasswordisEnabled=false;
            //if (grupo.getPassword()!=null){
            //    grupogetPasswordisEnabled = grupo.getPassword().isEnabled();
            //}


            if (
                    grupo.getCountDownTimer().isPasswordCountDownTimerRunning()
            ) {
                if (grupo.getPassword().isEnabled()){
                    holder.candadoAbierto.setVisibility(View.VISIBLE);
                    holder.candadoAbierto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            grupo.setGrupoLocked(true);
                            grupo.getCountDownTimer().finish();
                        }
                    });
                    holder.candadoCerrado.setVisibility(View.GONE);
                }else{
                    //holder.candadoAbierto.setVisibility(View.GONE);
                    //holder.candadoCerrado.setVisibility(View.INVISIBLE);
                }
                holder.itemView.setOnClickListener(v -> grupoActivity.itemClick(item));
                holder.tvGruposUnreadCount.setOnClickListener(v -> grupoActivity.itemClick(item));
            }else{

                if ( (grupo.getCountDownTimer() == null ||
                        !grupo.getCountDownTimer().isPasswordCountDownTimerRunning()) &&
                        grupo.getPassword().isEnabled()
                ) {
                    holder.candadoAbierto.setVisibility(View.GONE);
                    holder.candadoCerrado.setVisibility(View.VISIBLE);
                }else{
                    holder.candadoAbierto.setVisibility(View.GONE);
                    holder.candadoCerrado.setVisibility(View.INVISIBLE);
                }

            }
        }else{
            holder.candadoAbierto.setVisibility(View.GONE);
            holder.candadoCerrado.setVisibility(View.INVISIBLE);
        }

        if (holder.candadoCerrado.getVisibility() == View.VISIBLE){
            holder.itemView.setOnClickListener(getAction(item));
        }else{
            holder.itemView.setOnClickListener(getActionUnlock(item));
        }
    }

    private View.OnClickListener getActionUnlock(ItemListGrupo item ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                grupoActivity.itemClick(item);
            }

        };
    }

    private View.OnClickListener getAction(ItemListGrupo item ) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleErrorDialog.passwordGrupoValidation(RecyclerGrupoAdapter.this.grupoActivity, grupoActivity.progressBar, item.getGrupo());
            }

        };
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void filter(final String strSearch) {
        if (strSearch.length() == 0) {
            items.clear();
            items.addAll(originalItems);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                items.clear();
                List<ItemListGrupo> collect = originalItems.stream()
                        .filter(i -> i.getGrupo().getName().toLowerCase().contains(strSearch.toLowerCase()))
                        .collect(Collectors.toList());

                items.addAll(collect);
            }
            else {
                items.clear();
                for (ItemListGrupo i : originalItems) {
                    if (i.getGrupo().getName().toLowerCase().contains(strSearch.toLowerCase())) {
                        items.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private TextView grupoListItemOnline;
        private ImageButton btGrupoInvitacion;
        private TextView tvTitulo;
        private ImageButton tvGrupoListWritting;
        private TextView tvGruposUnreadCount;
        private ImageView candadoCerrado;
        private ImageView candadoAbierto;
        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            tvGruposUnreadCount = itemView.findViewById(R.id.bt_message_detail_item_state);
            tvTitulo = itemView.findViewById(R.id.tv_message_list_text);
            btGrupoInvitacion = itemView.findViewById(R.id.bt_grupo_invitation);
            grupoListItemOnline = itemView.findViewById(R.id.bt_grupo_list_item_online);
            candadoCerrado = itemView.findViewById(R.id.grupo_candado_cerrado);
            candadoAbierto = itemView.findViewById(R.id.grupo_candado_abierto);
            tvGrupoListWritting = itemView.findViewById(R.id.tv_message_list_writting);


        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListGrupo item);
    }

}
