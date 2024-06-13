package com.privacity.cliente.activity.grupoinfo;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.SingletonValues;

import com.privacity.cliente.util.DialogsToShow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerGrupoInfoAdapter extends RecyclerView.Adapter<RecyclerGrupoInfoAdapter.RecyclerHolder> {
    private List<ItemListGrupoInfo> items;
    public List<ItemListGrupoInfo> originalItems;
    private RecyclerItemClick itemClick;
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

    @Override
    public void onBindViewHolder(@NonNull final RecyclerHolder holder, final int position) {
        final ItemListGrupoInfo item = items.get(position);
        holder.nickname.setText(item.getUsersForGrupoDTO().getUsuario().getNickname());
        holder.roles.setSelection(item.getUsersForGrupoDTO().role.ordinal());

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
        private final ImageButton addMember;
        //private ImageButton save;
        private TextView nickname;
        private Spinner roles;

        public RecyclerHolder(@NonNull View itemView_1) {
            super(itemView_1);
            DialogsToShow.noAdminDialog(grupoInfoActivity, SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),itemView);
//            boolean isAdmin = );
           // save = itemView.findViewById(R.id.ib_grupo_info_itemlist_save_role);


            nickname = itemView.findViewById(R.id.tv_grupo_info_itemlist_nickname);
            roles = itemView.findViewById(R.id.sp_grupo_info_itemlist_roles);
            addMember = (ImageButton)itemView.findViewById(R.id.ib_grupo_info_itemlist_add_member);

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
