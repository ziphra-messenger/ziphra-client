package com.privacity.cliente.activity.messagedetail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.activity.main.TextHighlighter;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.WrittingDTO;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaTest224Activity extends AppCompatActivity
        implements ObservadoresMensajes, ObservadoresPasswordGrupo, RecyclerMessageDetailAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {
    private RecyclerView rvLista;
    private TestRecyclerMessageDetailAdapter adapter;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail_listtest);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.message_detail_activity__title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initViews();
        initValues();


        ((TextView)findViewById(R.id.message_detail__hide_message_details_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new TextHighlighter()
                        .setBackgroundColor(Color.parseColor("#FFFF00"))
                        .setForegroundColor(Color.GREEN)
                        .addTarget(items.get(0).holder.tvMessageDetailItemUsername)
                        .addTarget(items.get(1).holder.tvMessageDetailItemUsername)
                        .highlight(((TextView)findViewById(R.id.message_detail__hide_message_details_txt)).getText().toString(), TextHighlighter.BASE_MATCHER);

                items.get(1).holder.tvMessageDetailItemUsername.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public void myFinish() {


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
        adapter = new TestRecyclerMessageDetailAdapter(this,items);
        rvLista.setAdapter(adapter);
    }
    private List<ItemListMessageDetail> getItems() {



        List<MessageDetailDTO> list = new ArrayList<>();

        MessageDetailDTO m1 = new MessageDetailDTO();


        list.add(new MessageDetailDTO());
        list.add(new MessageDetailDTO());


        ArrayList<ItemListMessageDetail> r = new ArrayList<ItemListMessageDetail>();


/*
        if ( conf.getAnonimoRecived() != null && RulesConfEnum.ON.equals(conf.getAnonimoRecived())){
            r.add(new ItemListMessageDetail(selected));
        }else{*/
        for (int i = 0, listLength = list.size(); i < listLength; i++) {
            MessageDetail messageDetail = new MessageDetail(list.get(i));
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
