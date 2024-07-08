package com.privacity.cliente.activity.messagedetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CheckBox;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.WrittingDTO;
import com.privacity.common.enumeration.GrupoUserConfEnum;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailActivity extends CustomAppCompatActivity
        implements ObservadoresMensajes, ObservadoresPasswordGrupo, RecyclerMessageDetailAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {
    private RecyclerView rvLista;
    private RecyclerMessageDetailAdapter adapter;
    private List<ItemListMessageDetail> items;


    @Override
    public void passwordExpired(Grupo g) {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())){
            this.finishMessageDetailActivity();
        }
    }

    @Override
    public void passwordSet(Grupo g) {

    }

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PrivaCity - Mensaje Detalles");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Observers.message().suscribirse(this);
        Observers.passwordGrupo().suscribirse(this);
        initViews();
        initValues();

        MessageDTO m = Observers.message().getMensajesPorId(
                SingletonValues.getInstance().getMessageDetailSeleccionado().getMessage().buildIdMessageToMap());

        GrupoUserConfDTO conf = Observers.grupo().getGrupoById(m.getIdGrupo()).getUserConfDTO();

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    myFinish();
                }else if (action.equals("finish_all_activities")) {
                    myFinish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

    }

    public void myFinish() {

                SingletonValues.getInstance().setMessageDetailSeleccionado(null);
        Observers.passwordGrupo().remove(this);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        finishMessageDetailActivity();
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        rvLista = findViewById(R.id.rv_message_detail_list);
    }

    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);

        if (items != null)  items.clear();
        items = getItems();
        adapter = new RecyclerMessageDetailAdapter(items);
        rvLista.setAdapter(adapter);
    }
    private List<ItemListMessageDetail> getItems() {

        MessageDetailDTO selected = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO();

        MessageDetailDTO[] list = Observers.message().getMensajesPorId(selected.buildIdMessageToMap()).getMessagesDetailDTO();

        ArrayList<ItemListMessageDetail> r = new ArrayList<ItemListMessageDetail>();

        GrupoUserConfDTO conf = Observers.grupo().getGrupoById(selected.getIdGrupo()).getUserConfDTO();
/*
        if ( conf.getAnonimoRecived() != null && GrupoUserConfEnum.GRUPO_TRUE.equals(conf.getAnonimoRecived())){
            r.add(new ItemListMessageDetail(selected));
        }else{*/
            for (int i = 0 ; i < list.length ; i++){
                ItemListMessageDetail add = new ItemListMessageDetail(list[i]);


                r.add(add);
            }

  //      }
        return r;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishMessageDetailActivity();
    }


    private void finishMessageDetailActivity(){
        Observers.passwordGrupo().remove(this);
        Observers.message().dessuscribirse(this);
        SingletonValues.getInstance().setMessageDetailSeleccionado(null);
        this.finish();
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void itemClick(ItemListGrupo item) {

    }


    @Override
    public void nuevoMensaje(ProtocoloDTO protocoloDTO) {

    }

    @Override
    public void cambioEstado(MessageDetailDTO m) {
        initValues();
        adapter.notifyDataSetChanged();
/*
        for (ItemListMessageDetail item : items){
            if (item.getMessageDetailDTO().buildIdMessageDetailToMap().equals(m.buildIdMessageDetailToMap())){
                item.getMessageDetailDTO().setEstado(m.getEstado());
                adapter.notifyDataSetChanged();
            }
        }*/

    }

    @Override
    public void emptyList() {
        items.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void mensajeAddItem(MessageDTO miMensaje, String asyncId) {

    }

    @Override
    public void borrarMensaje(MessageDetailDTO detail) {

    }

    @Override
    public void writting(WrittingDTO w) {

    }

    @Override
    public void writtingStop(WrittingDTO w) {

    }

    @Override
    public void deleteExtraEncrypt(Grupo g) {

    }

    @Override
    public void lock(Grupo g) {

    }
}
