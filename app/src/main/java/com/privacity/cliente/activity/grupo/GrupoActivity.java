package com.privacity.cliente.activity.grupo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.IntentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.about.AboutActivity;
import com.privacity.cliente.activity.addgrupo.AddGrupoActivity;
import com.privacity.cliente.activity.codigoinvitacion.CodigoInvitacionActivity;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.grupo.delegate.ExitPrivacity;
import com.privacity.cliente.activity.grupo.delegate.GrupoActivityListeners;
import com.privacity.cliente.activity.grupo.delegate.UsuarioCloseSessionRest;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RestCalls;
import com.privacity.cliente.activity.myaccount.MyAccountActivity;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.singleton.localconfiguration.SingletonLang;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.cliente.util.notificacion.Notificacion;
import com.privacity.common.BroadcastConstant;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.WrittingDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class GrupoActivity extends CustomAppCompatActivity implements
        ObservadoresPasswordGrupo,
        ObservadoresGrupos, ObservadoresMensajes, RecyclerGrupoAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {
    public ProgressBar progressBar;
    private RecyclerView rvLista;
    private SearchView svSearch;
    private RecyclerGrupoAdapter adapter;
    private List<ItemListGrupo> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_grupo);
        currentLanguage = getIntent().getStringExtra(currentLang);
        Observers.grupo().suscribirse(this);
        Observers.message().suscribirse(this);
        Observers.passwordGrupo().suscribirse(this);

        initActionBar();


        //sortOnline
        Observers.grupo().setGrupoOnTop(true);
//
        initBroadCast();
        //

        Spinner sort = (Spinner) findViewById(R.id.grupo_sort_spinner);
        GrupoActivityListeners.sortGrupos(sort, adapter, items);

        Notificacion.getInstance().init(this);
        //SingletonOnPauseTime.getInstance().startClock();
        progressBar = (ProgressBar) findViewById(R.id.common__progress_bar);
        progressBar.setVisibility(View.GONE);


        //if (Observers.grupo().getMisGrupoList().size() > 0 ){
        //GetMessageById.loadMessagesContador(this);
        //}
        //inishLoadingActivity();
        if (SingletonSessionClosing.getInstance().isClosing())return;

        GrupoActivityListeners.closeMessagingConectionListener(this);

        SingletonValues.getInstance().setGrupoSeleccionado(null);
        initViews();
        initValues();
        initListener();


        powerServiceMessage();

        RestCalls.loadMessagesContador(this);

    }

    private void powerServiceMessage() {/*
        String packageName = "com.privacity.cliente";
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {

            SimpleErrorDialog.errorDialog(this, getString(R.string.grupo_activity__powerservice__title),
                    getString(R.string.grupo_activity__powerservice__detail), () -> {
                        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);
                    });
        }*/
    }


    private void finishLoadingActivity() {
        Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ACTIVITY_LOADING);
        this.sendBroadcast(intent);
    }


    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(getString(R.string.grupo_activity__title));

        if (SingletonServer.getInstance().isDeveloper()) {
            actionBar.setTitle(actionBar.getTitle() + " - " + Singletons.usuario().getUsuario().getNickname());
        }
    }

    private void initBroadCast() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES)) {
                    UsuarioCloseSessionRest.doIt(GrupoActivity.this);
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES));
    }

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    Locale myLocale;
    String currentLanguage = SingletonLang.getInstance().get(), currentLang;
    public void setLocale(String idioma) {
        SingletonLang.getInstance().save(this,idioma);

        myLocale = new Locale(idioma);
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, GrupoActivity.class);
        refresh.putExtra(currentLang, idioma);
        currentLanguage=idioma;
        //this.finish();
        this.startActivity(refresh);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (SingletonSessionClosing.getInstance().isClosing())return;



//        Observers.grupo().setGrupoOnTop(true);
//        actualizarLista();
//        SingletonValues.getInstance().setGrupoSeleccionado(null);
//        if (!SingletonLang.getInstance().get().equals(currentLanguage)){
//            setLocale(SingletonLang.getInstance().get());
//        }else{
//            currentLanguage=SingletonLang.getInstance().get();
//        }
//
//        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(Notificacion.ID);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Observers.grupo().setGrupoOnTop(false);
       // this.moveTaskToBack(true);

    }


    public void superOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
//        try {
            items.clear();
           // items = getItems();
//
//            //adapter.notifyDataSetChanged();
//        } catch (Exception e) {
//
//        }
//
//        try {
//            SingletonSessionClosing.getInstance().setClosing(true);
//            Intent intent = new Intent(this, MainActivity.class);
//            this.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }






    }
    public static void restart(Context context){
        Intent mainIntent = IntentCompat.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_LAUNCHER);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(mainIntent);
        System.exit(0);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        int id = itemMenu.getItemId();

        if (id == R.id.boton_add_grupo) {
            Intent intent = new Intent(GrupoActivity.this, AddGrupoActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_grupo_mi_cuenta) {
            Intent intent = new Intent(GrupoActivity.this, MyAccountActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_grupo_server_gral_conf) {
            Intent intent = new Intent(GrupoActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_item_grupo_logout) {
            ExitPrivacity.alertClose(this);
        } else if (id == R.id.menu_grupo_codigo_invitacion) {


            Intent intent = new Intent(this, CodigoInvitacionActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(itemMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grupo, menu);
        return true;
    }

    private void initViews() {
        rvLista = findViewById(R.id.rv_message_detail_list);
        svSearch = findViewById(R.id.svSearchGrupo2);
    }

    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);

        if (items != null) items.clear();
        items = getItems();
        adapter = new RecyclerGrupoAdapter(items, this, GrupoActivity.this);
        rvLista.setAdapter(adapter);
    }

    private void initListener() {
        svSearch.setOnQueryTextListener(this);
    }

    private List<ItemListGrupo> getItems() {

        Set<Grupo> list = Observers.grupo().getMisGrupoList();
        ArrayList<ItemListGrupo> r = new ArrayList<>();
        for (Grupo g : list) {
            ItemListGrupo i = new ItemListGrupo();
            i.setGrupo(g);
            i.setUnread(Observers.message().getMensajesDetailsPorGrupoUnread(g.getIdGrupo()));
//            if (!g.isGrupoInvitation()){
//                getGrupoUserConfRest(g.getIdGrupo());
//            }
            r.add(i);
        }
        return r;
    }

    @Override
    public void itemClick(ItemListGrupo itemListGrupo) {
        ObserverGrupo.getInstance().getGrupoById(itemListGrupo.getGrupo().getIdGrupo()).setGrupoLocked(false)
        ;
        SingletonValues.getInstance().setGrupoSeleccionado(itemListGrupo.getGrupo());

        Intent intent = new Intent(GrupoActivity.this, MessageActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    @Override
    public void actualizarLista() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void nuevoGrupo(Grupo g) {
        ItemListGrupo i = new ItemListGrupo();
        i.setGrupo(g);
        i.setUnread(Observers.message().getMensajesDetailsPorGrupoUnread(g.getIdGrupo()));

        items.add(0, i);
        adapter.originalItems.add(0, i);
        //rvLista.selec.setSelection(adapter.getCount()-1);
        rvLista.scrollToPosition(0);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void nuevoMensaje(Protocolo protocolo) {
        //refrescarUnread(protocolo.getMessage().getIdGrupo());
    }


    public void refrescarUnread(String idGrupo) {
        {
            int posicion = 0;
            for (int i = 0; i < items.size(); i++) {
                ItemListGrupo item = items.get(i);
                if (item.getGrupo().getIdGrupo().equals(idGrupo)) {
                    item.setUnread(Observers.message().contarEstadoMensajePorGrupo(idGrupo));
                    posicion = i;
                }
            }
            if (posicion != 0) {
                ItemListGrupo item = items.get(posicion);
                items.remove(posicion);
                items.add(0, item);
            }

        }
        {
            int posicion = 0;
            for (int i = 0; i < adapter.originalItems.size(); i++) {
                ItemListGrupo item = adapter.originalItems.get(i);
                if (item.getGrupo().getIdGrupo().equals(idGrupo)) {
                    item.setUnread(Observers.message().contarEstadoMensajePorGrupo(idGrupo));
                    posicion = i;
                }
            }
            if (posicion != 0) {
                ItemListGrupo item = adapter.originalItems.get(posicion);
                adapter.originalItems.remove(posicion);
                adapter.originalItems.add(0, item);
                Observers.grupo().moverGrupo(item.getGrupo().getIdGrupo());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void cambioEstado(MessageDetail m) {

    }

    @Override
    public void emptyList(String idGrupo) {

    }

    @Override
    public void cambioUnread(String idGrupo) {


    }


    @Override
    public void removeGrupo(String idGrupo) {
        if (SingletonValues.getInstance().getGrupoSeleccionado() != null){
            if (idGrupo.equals(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo() )){
                Observers.grupo().GrupoRemove(idGrupo);
                Observers.message().removeAllMessageFromUser(idGrupo, Singletons.usuario().getUsuario().getIdUsuario());
                Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY);
                this.sendBroadcast(intent);
                Toast.makeText(this, getString(R.string.grupo_info_activity__remove_me__success), Toast.LENGTH_SHORT).show();

            }
        }
        initValues();
        /*
        for (int i = 0 ; i <  items.size() ; i++) {
            ItemListGrupo item = items.get(i);
            if (item.getGrupo().getIdGrupo().equals(idGrupo)) {
                items.remove(item);
                adapter.notifyDataSetChanged();
            }
        }
        */
    }

    @Override
    public void avisarLock(Grupo g) {

        if (SingletonValues.getInstance().getGrupoSeleccionado() != null
                && SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())
        ) {

            if (SingletonValues.getInstance().getGrupoSeleccionado().getLock().isEnabled()) {
                {
                    Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY);
                    this.sendBroadcast(intent);
                }

                {
                    Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ACTIVITY);
                    this.sendBroadcast(intent);
                }
            }
        }

    }

    @Override
    public void avisarRoleChange(Grupo g) {

    }

    @Override
    public void avisarCambioGrupoGralConf(Grupo g) {

    }



    @Override
    public void mensajeAddItem(Message miMensaje, String asyncId) {
       // refrescarUnread(miMensaje.getIdGrupo());
    }

    @Override
    public void borrarMessageDetail(MessageDetail detail) {
       // refrescarUnread(detail.getIdGrupo());
    }

    @Override
    public void borrarMessage(String idMessageToMap) {
     //   refrescarUnread(detail.getIdGrupo());
    }

    @Override
    public void writting(WrittingDTO w) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void writtingStop(WrittingDTO w) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void passwordExpired(Grupo g) {

        actualizarLista();
    }

    @Override
    public void passwordSet(Grupo g) {
        actualizarLista();
    }

    @Override
    public void deleteExtraEncrypt(Grupo g) {

    }

    @Override
    public void lock(Grupo g) {

        if (SingletonValues.getInstance().getGrupoSeleccionado() != null
                && SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())
        ) {

            {
                Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY);
                this.sendBroadcast(intent);
            }

            {
                Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ACTIVITY);
                this.sendBroadcast(intent);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Observers.grupo().setGrupoOnTop(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Observers.grupo().setGrupoOnTop(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Observers.grupo().setGrupoOnTop(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Observers.grupo().setGrupoOnTop(false);
    }


}
