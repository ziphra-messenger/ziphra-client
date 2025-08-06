package com.privacity.cliente.activity.grupoinfo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.util.DialogsToShow;
import com.privacity.cliente.util.RoleUtil;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoChangeUserRoleDTO;
import com.privacity.common.dto.request.GrupoRemoveUserDTO;
import com.privacity.common.enumeration.GrupoRolesEnum;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerGrupoInfoAdapter extends RecyclerView.Adapter<RecyclerGrupoInfoAdapter.RecyclerHolder> {
    private final List<ItemListGrupoInfo> items;
    public List<ItemListGrupoInfo> originalItems;
    private final RecyclerItemClick itemClick;
    GrupoInfoActivity grupoInfoActivity;
    private View viewInvitation;
    public RecyclerGrupoInfoAdapter(List<ItemListGrupoInfo> items, RecyclerItemClick itemClick, GrupoInfoActivity grupoInfoActivity) {
        this.items = items;
        this.itemClick = itemClick;
        this.originalItems = new ArrayList<>();
        originalItems.addAll(items);
        this.grupoInfoActivity = grupoInfoActivity;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view_grupo_info, parent, false);

        return new RecyclerHolder(view);
    }
    public void removeOtherRest(String idUsuario) {
    Protocolo p = new Protocolo();
                p.setComponent(ProtocoloComponentsEnum.GRUPO);
                p.setAction(ProtocoloActionsEnum.GRUPO_REMOVE_OTHER);

        GrupoRemoveUserDTO o = new GrupoRemoveUserDTO();
                o.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
                o.setIdUsuario(idUsuario);
                p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

                RestExecute.doit(grupoInfoActivity, p,
            new CallbackRest() {

        @Override
        public void response(ResponseEntity<Protocolo> response) {

            if (response.getBody().getCodigoRespuesta() != null) {
                Toast.makeText(grupoInfoActivity, response.getBody().getCodigoRespuesta(), Toast.LENGTH_SHORT).show();

            }


        }

        @Override
        public void onError(ResponseEntity<Protocolo> response) {

        }

        @Override
        public void beforeShowErrorMessage(String msg) {

        }
    });

}
    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final ItemListGrupoInfo item = items.get(position);

        boolean isMyUser = (item.getUsersForGrupoDTO().getUsuario().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario()));
        boolean iAmAdmin = (SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.ADMIN));
        holder.nickname.setText(item.getUsersForGrupoDTO().getUsuario().getNickname());
        holder.roles.setSelection(item.getUsersForGrupoDTO().getRole().ordinal());


        if (isMyUser) {
            //holder.roles.setEnabled(false);
            //holder.removeMember.setEnabled(false);

            holder.layout.setVisibility(View.VISIBLE);
            //*holder.layoutValidation.setVisibility(View.GONE);
            //holder.layout.setBackgroundColor(Color.parseColor("#E0ACF3"));
        }else{
            holder.layout.setVisibility(View.GONE);

        }


        if (iAmAdmin) {
                holder.layoutValidation.setVisibility(View.GONE);
                holder.removeMember.setVisibility(View.VISIBLE);
        }else{
            if (!isMyUser) {
                holder.layoutValidation.setVisibility(View.VISIBLE);
            }
            holder.removeMember.setVisibility(View.GONE);
        }
        setListeners(holder, item);
    }

    private void setListeners(@NonNull RecyclerHolder holder, ItemListGrupoInfo item) {
        holder.removeMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleErrorDialog.confirmAction(grupoInfoActivity, new SimpleErrorDialog.OkI() {
                            @Override
                            public void action() {

                                try {
                                    removeOtherRest(item.getUsersForGrupoDTO().getUsuario().getIdUsuario());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        },
                        new SimpleErrorDialog.CancelI() {
                            @Override
                            public void action() {

                            }
                        });
            }

    });
        holder.roles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SimpleErrorDialog.passwordValidation(grupoInfoActivity, "jpña", new
                        SimpleErrorDialog.PasswordValidationI() {
                            @Override
                            public void action() {
                                if (holder.roles.getSelectedItemPosition() != item.getUsersForGrupoDTO().getRole().ordinal()) {
                                    SimpleErrorDialog.confirmAction(grupoInfoActivity, new SimpleErrorDialog.OkI() {
                                                @Override
                                                public void action() {

                                                    try {
                                                        changeUserRoleRest(grupoInfoActivity, holder.roles, SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),

                                                                item.getUsersForGrupoDTO().getUsuario().getIdUsuario(),
                                                                holder, item);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            },
                                            new SimpleErrorDialog.CancelI() {
                                                @Override
                                                public void action() {
                                                    holder.roles.setSelection(item.getUsersForGrupoDTO().getRole().ordinal());
                                                }
                                            });
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void changeUserRoleRest(Activity activity, Spinner roles, String idGrupo, String idUsuario,RecyclerHolder holder,ItemListGrupoInfo item ) throws Exception {





        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_CHANGE_USER_ROLE);

        GrupoChangeUserRoleDTO dto = new GrupoChangeUserRoleDTO();
        dto.setIdGrupo(idGrupo);
        dto.setIdUsuarioToChange(idUsuario);

        dto.setRole(RoleUtil.transformRole(roles));
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));

        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        item.getUsersForGrupoDTO().setRole(dto.getRole());
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        holder.roles.setSelection(item.getUsersForGrupoDTO().getRole().ordinal());
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }



    public void filter(final String strSearch) {
/*        if (strSearch.length() == 0) {
            items.clear();
            items.addAll(originalItems);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                items.clear();
                List<ItemListGrupoInfo> collect = originalItems.stream()
                        .filter(i -> i.getUsersForGrupoDTO().getUsuario().getNickname().toLowerCase().contains(strSearch.toLowerCase()))
                        .collect(Collectors.toList());

                items.addAll(collect);
            }
            else {
                items.clear();
                for (ItemListGrupoInfo i : originalItems) {
                    if (i.getUsersForGrupoDTO().getUsuario().getNickname().toLowerCase().contains(strSearch.toLowerCase())) {
                        items.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();*/
    }



    public class RecyclerHolder extends RecyclerView.ViewHolder {
        private final ImageButton removeMember;
        //private ImageButton save;
        private final TextView nickname;
        private final Spinner roles;
        private final LinearLayout layout;
        private final LinearLayout layoutValidation;
        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            DialogsToShow.noAdminDialog(grupoInfoActivity, SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),itemView);



//            boolean isAdmin = );
           // save = itemView.findViewById(R.id.ib_grupo_info_itemlist_save_role);

            layoutValidation = (LinearLayout)itemView.findViewById(R.id.validation_item_list_view_grupo_info_layout);
            layout = (LinearLayout)itemView.findViewById(R.id.propio_item_list_view_grupo_info_layout);
            nickname = itemView.findViewById(R.id.tv_grupo_info_itemlist_nickname);
            roles = itemView.findViewById(R.id.sp_grupo_info_itemlist_roles);
            removeMember = (ImageButton)itemView.findViewById(R.id.ib_grupo_info_itemlist_remove_member);


//            if (!isAdmin) DialogsToShow.noAdminDialog(grupoActivity, addMember);
//            if (!isAdmin) DialogsToShow.noAdminDialog(grupoActivity, save);
//            if (!isAdmin)DialogsToShow.noAdminDialog(grupoActivity, roles);
        }
    }

    public interface RecyclerItemClick {
        void itemClick(ItemListGrupoInfo item);
    }
    @Override
    public int getItemCount() {
        return items.size();
    }
}
