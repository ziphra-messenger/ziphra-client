package com.privacity.cliente.activity.grupo.delegate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.activity.grupo.RecyclerGrupoAdapter;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.common.BroadcastConstant;

import java.util.Comparator;
import java.util.List;

public class GrupoActivityListeners {
    public static final String TAG = "GrupoActivityListeners";
    public static void closeMessagingConectionListener(GrupoActivity activity) {
        ((Button) (activity.findViewById(R.id.ws_disconnect))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingletonValues.getInstance().getWebSocket().disconnectStomp();
                Intent intent = new Intent(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED);
                activity.sendBroadcast(intent);
            }
        });
    }

    public static void sortGrupos(Spinner sort, RecyclerGrupoAdapter adapter,
             List<ItemListGrupo>items) {
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String TagMethod="sortGrupos";
                try {


                final TextView childAt = (TextView) parent.getChildAt(0);
                ((TextView) parent.getChildAt(0)).setTypeface(((TextView) parent.getChildAt(0)).getTypeface(), Typeface.BOLD);
                if (sort.getSelectedItem() != null && !sort.getSelectedItem().toString().trim().equals("")) {

                    if (sort.getSelectedItemPosition() == 1) { // OL
                        sortOnline(adapter, items);
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#23B34C"));
                    }
                    if (sort.getSelectedItemPosition() == 2) { //MSG
                        sortMsg(adapter, items);
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#3DA1CF"));
                    }
                    if (sort.getSelectedItemPosition() == 3){ // NAME{
                        sortName(adapter, items);
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);


                    }

                }
                }catch (Exception e){
                   // e.printStackTrace();
                    Log.e(GrupoActivityListeners.TAG + "-" + TagMethod, e.getMessage());                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    static void sortOnline(RecyclerGrupoAdapter adapter, List<ItemListGrupo>items) {

        items.sort(new Comparator<ItemListGrupo>() {
            @Override
            public int compare(ItemListGrupo o1, ItemListGrupo o2) {
                if (o1.getGrupo().getMembersQuantityDTO().getQuantityOnline() > o2.getGrupo().getMembersQuantityDTO().getQuantityOnline()) {

                    return -1;
                }
                if (o1.getGrupo().getMembersQuantityDTO().getQuantityOnline() < o2.getGrupo().getMembersQuantityDTO().getQuantityOnline())
                    return 1;
                return 0;
            }
        });

        adapter.notifyDataSetChanged();

    }

    static void sortName(RecyclerGrupoAdapter adapter, List<ItemListGrupo>items) {

        items.sort(new Comparator<ItemListGrupo>() {
            @Override
            public int compare(ItemListGrupo o1, ItemListGrupo o2) {
                try {
                    int value = o1.getGrupo().getName().compareTo(o2.getGrupo().getName());
                    return value;
                } catch (Exception e) {
                    e.printStackTrace();

                }
                return 1;
            }
        });

        adapter.notifyDataSetChanged();

    }

    static void sortMsg(RecyclerGrupoAdapter adapter, List<ItemListGrupo>items) {

        items.sort(new Comparator<ItemListGrupo>() {
            @Override
            public int compare(ItemListGrupo o1, ItemListGrupo o2) {
                if (o1.getUnread() > o2.getUnread()) return -1;
                if (o1.getUnread() < o2.getUnread()) return 1;
                return 0;
            }
        });

        adapter.notifyDataSetChanged();

    }
}
