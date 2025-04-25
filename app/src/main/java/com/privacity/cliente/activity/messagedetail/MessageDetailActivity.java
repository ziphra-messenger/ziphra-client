package com.privacity.cliente.activity.messagedetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.WrittingDTO;

import java.util.ArrayList;
import java.util.List;

public class MessageDetailActivity extends CustomAppCompatActivity
        implements ObservadoresMensajes, ObservadoresPasswordGrupo, RecyclerMessageDetailAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {
    private RecyclerView rvLista;
    private RecyclerMessageDetailAdapter adapter;
    private List<ItemListMessageDetail> items;
    private TextView hideRead;
    private TextView hideMessageDetails;
    private Button grupoName;
    private String messageId;
    private Message message;

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
        actionBar.setTitle(getString(R.string.message_detail_activity__title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Observers.message().suscribirse(this);
        Observers.passwordGrupo().suscribirse(this);
        initViews();
        initValues();

        this.messageId= SingletonValues.getInstance().getMessageDetailSeleccionado().getMessage().buildIdMessageToMap();
        message = Observers.message().getMensajesPorId(messageId);

        hideRead = (TextView) findViewById(R.id.message_detail__hide_read);
        if (SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail().isHideRead()){
            hideRead.setVisibility(View.VISIBLE);
        }else{
            hideRead.setVisibility(View.GONE);
        }
        hideMessageDetails = (TextView) findViewById(R.id.message_detail__hide_message_details_txt);
        if (SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isHideMessageDetails()){
            hideMessageDetails.setVisibility(View.VISIBLE);
        }else{
            hideMessageDetails.setVisibility(View.GONE);
        }

        grupoName = GetButtonReady.get(this, R.id.message_detail__grupo_name,SingletonValues.getInstance().getGrupoSeleccionado().getName());


        GrupoUserConfDTO conf = Observers.grupo().getGrupoById(message.getIdGrupo()).getUserConfDTO();

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY)
                        ||action.equals(BroadcastConstant.BROADCAST__FINISH_MESSAGE_DETAIL_ACTIVITY)
                        ||action.equals(BroadcastConstant.BROADCAST__FINISH_ACTIVITY)
                        || action.equals(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES)) {
                    myFinish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_MESSAGE_DETAIL_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES));

        new IconView();
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
        adapter = new RecyclerMessageDetailAdapter(this,items);
        rvLista.setAdapter(adapter);
    }
    private List<ItemListMessageDetail> getItems() {

        if (SingletonValues.getInstance().getMessageDetailSeleccionado() == null){
            return new ArrayList<>();
        }
        MessageDetail selected = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail();

        MessageDetail[] list = Observers.message().getMensajesPorId(selected.buildIdMessageToMap()).getMessagesDetail();

        ArrayList<ItemListMessageDetail> r = new ArrayList<ItemListMessageDetail>();

        GrupoUserConfDTO conf = Observers.grupo().getGrupoById(selected.getIdGrupo()).getUserConfDTO();
/*
        if ( conf.getAnonimoRecived() != null && RulesConfEnum.ON.equals(conf.getAnonimoRecived())){
            r.add(new ItemListMessageDetail(selected));
        }else{*/
        for (int i = 0, listLength = list.length; i < listLength; i++) {
            MessageDetail messageDetail = list[i];
            ItemListMessageDetail add = new ItemListMessageDetail(messageDetail);


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
    public void nuevoMensaje(Protocolo protocolo) {

    }

    @Override
    public void cambioEstado(MessageDetail m) {
        initValues();
        adapter.notifyDataSetChanged();
/*
        for (ItemListMessageDetail item : items){
            if (item.getMessageDetail().buildIdMessageDetailToMap().equals(m.buildIdMessageDetailToMap())){
                item.getMessageDetail().setEstado(m.getEstado());
                adapter.notifyDataSetChanged();
            }
        }*/

    }

    @Override
    public void emptyList(String idGrupo) {
        items.clear();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void mensajeAddItem(Message miMensaje, String asyncId) {

    }

    @Override
    public void borrarMessageDetail(MessageDetail detail) {
        if (this.message.isDeleted() || this.messageId.equals(detail.buildIdMessageToMap())){
            myFinish();
        };


    }

    @Override
    public void borrarMessage(String idMessageToMap) {
        if (this.message.isDeleted() || messageId.equals(idMessageToMap)){
            myFinish();
        }
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
