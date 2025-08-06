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
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.restcalls.invitation.AcceptInvitationCallRest;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ObservadoresUnread;
import com.privacity.common.exceptions.PrivacityException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerGrupoAdapter extends RecyclerView.Adapter<RecyclerGrupoAdapter.RecyclerHolder> {

    private static final String TAG = "RecyclerGrupoAdapter";

    private final List<ItemListGrupo> items;
    public List<ItemListGrupo> originalItems;
    private final RecyclerItemClick itemClick;
    private final GrupoActivity grupoActivity;
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
        Singletons.unread().suscribe(holder, item.getGrupo().getIdGrupo());
        holder.avisar(item.getGrupo().getIdGrupo(), Singletons.unread().get(item.getGrupo().getIdGrupo()));



        if (  item.getGrupo().getMembersQuantityDTO() != null && item.getGrupo().getMembersQuantityDTO().getQuantityOnline() > 1 ){
            holder.grupoListItemOnline.setText((item.getGrupo().getMembersQuantityDTO().getQuantityOnline()-1)+"");
            holder.grupoListItemOnline.setVisibility(View.VISIBLE);
        }else{
            holder.grupoListItemOnline.setVisibility(View.GONE);
        }

        holder.grupoListItemOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleErrorDialog.errorDialog(grupoActivity, grupoActivity.getString(R.string.grupo_activity__members__online__title),
                        grupoActivity.getString(R.string.grupo_activity__members__online__detail,item.getGrupo().getMembersQuantityDTO().getQuantityOnline()+""
                        ,item.getGrupo().getMembersQuantityDTO().getTotalQuantity()+"" ));
            }
        });


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
                    builder.setTitle(grupoActivity.getString(R.string.grupo_activity__invitation__response));

                    viewInvitation = LayoutInflater.from(grupoActivity).inflate(R.layout.custom_comp_grupo_invitation_message, null);

                    builder.setView(viewInvitation);
                    builder.setIcon(R.drawable.ic_launcher_background);
                    String invitacionMensaje="";
                    if (item.getGrupo().getGrupoInvitationDTO().getInvitationMessage() != null){


                    RSA t = new RSA();

                        byte[] mensajeEncr = Base64.getDecoder().decode(item.getGrupo().getGrupoInvitationDTO().getInvitationMessage());
                        try {
                            invitacionMensaje = UtilsStringSingleton.getInstance().convertBytesToString(t.decryptFilePrivate(mensajeEncr, SingletonValues.getInstance().getEncryptKeysToUse().getPrivateKey()));

                        } catch (IOException | PrivacityException | GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    }
                    TextView invitationRole = (TextView) viewInvitation.findViewById(R.id.tv_grupo_invitation_role);
                    TextView invitationMessage = (TextView) viewInvitation.findViewById(R.id.tv_grupo_invitation_message);
                    TextView invitationMessageDetail = (TextView) viewInvitation.findViewById(R.id.tv_grupo_invitation_message_detail);
                    invitationMessage.setText(grupoActivity.getString(R.string.grupo_activity__invitation__response_details1, item.getGrupo().getGrupoInvitationDTO().getUsuarioInvitante().getNickname()));
                    invitationRole.setText((grupoActivity.getString(R.string.grupo_activity__invitation__response_details1, GrupoUtil.transformGrupoRoleEnumToCompleteString(grupoActivity, item.getGrupo().getGrupoInvitationDTO().getRole())) ));

                    invitationMessageDetail.setText(invitacionMensaje);
//        ((LinearLayout)findViewById(R.id.linear_layout_add_grupo)).getChildAt(0)

                    //builder.itsetView(invitationCode);
                    builder.setPositiveButton(grupoActivity.getString(R.string.general__accept), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                ProgressBarUtil.show(grupoActivity, grupoActivity.progressBar);
                                AcceptInvitationCallRest.call(grupoActivity, grupoActivity.progressBar,item);
                            } catch (Exception e) {
                                e.printStackTrace();
                                ProgressBarUtil.hide(grupoActivity, grupoActivity.progressBar);
                                SimpleErrorDialog.errorDialog(grupoActivity,
                                        grupoActivity.getString(R.string.general__error_message_ph1,TAG)
                                        , e.getMessage()                                );
                                return;
                            }


                        }
                    }).setNegativeButton(grupoActivity.getString(R.string.general__cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setNeutralButton(grupoActivity.getString(R.string.general__ignore), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.show();
                }
            });
        }else{
            holder.btGrupoInvitacion.setVisibility(View.GONE);
        }


        //holder.tvGruposUnreadCount.setText();



        //holder.tvGruposUnreadCount.setText(unread);
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

                if ( (grupo.getCountDownTimer() == null || (grupo.getPassword() != null &&
                        !grupo.getCountDownTimer().isPasswordCountDownTimerRunning()) &&
                        grupo.getPassword().isEnabled())
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


    public static class RecyclerHolder extends RecyclerView.ViewHolder implements ObservadoresUnread {
        private final TextView grupoListItemOnline;
        private final ImageButton btGrupoInvitacion;
        private final TextView tvTitulo;
        private final ImageButton tvGrupoListWritting;
        private final TextView tvGruposUnreadCount;
        private final ImageView candadoCerrado;
        private final ImageView candadoAbierto;


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

        @Override
        public void avisar(String idGrupo, int unread) {
            if (unread==0){
                tvGruposUnreadCount.setVisibility(View.INVISIBLE);
            }else {

                tvGruposUnreadCount.setVisibility(View.VISIBLE);
                tvGruposUnreadCount.setText(unread+"");

            }

        }

        @Override
        public void suscribeByGrupo(String idGrupo) {

        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListGrupo item);
    }

}
