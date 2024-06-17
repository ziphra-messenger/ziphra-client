package com.privacity.cliente.activity.grupo;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.GrupoBloqueoRemoto;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.about.AboutActivity;
import com.privacity.cliente.activity.addgrupo.AddGrupoActivity;
import com.privacity.cliente.activity.codigoinvitacion.CodigoInvitacionActivity;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.activity.main.MainActivity;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RestCalls;
import com.privacity.cliente.activity.myaccount.MyAccountActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.notificacion.Notificacion;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.IdDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.WrittingDTO;


import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;


public class GrupoActivity extends CustomAppCompatActivity implements
        ObservadoresPasswordGrupo,
        ObservadoresGrupos, ObservadoresMensajes, RecyclerGrupoAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {
    private RecyclerView rvLista;
    private SearchView svSearch;
    private RecyclerGrupoAdapter adapter;
    private List<ItemListGrupo> items;
    public ProgressBar progressBar;


    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    void sortOnline()
    {

        items.sort(new Comparator<ItemListGrupo>() {
            @Override
            public int compare(ItemListGrupo o1, ItemListGrupo o2) {
                if (o1.getGrupo().getMembersOnLine() > o2.getGrupo().getMembersOnLine()) return -1;
                if (o1.getGrupo().getMembersOnLine() < o2.getGrupo().getMembersOnLine()) return 1;
                return 0;
            }
        });

        adapter.notifyDataSetChanged();

    }

    void sortName()
    {

        items.sort(new Comparator<ItemListGrupo>() {
            @Override
            public int compare(ItemListGrupo o1, ItemListGrupo o2) {
             try{
                int value =  o1.getGrupo().getName().compareTo(o2.getGrupo().getName());
                return value;
            }catch (Exception e){
                e.printStackTrace();

            }
             return 1;
            }
        });

        adapter.notifyDataSetChanged();

    }

    void sortMsg()
    {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        Observers.grupo().suscribirse(this);
        Observers.message().suscribirse(this);
        Observers.passwordGrupo().suscribirse(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PrivaCity - Grupos");

        //sortOnline
        Observers.grupo().setGrupoOnTop(true);
//
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_all_activities")) {
                    finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_all_activities"));
        //

        Spinner sort = (Spinner) findViewById(R.id.grupo_sort_spinner);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final TextView childAt = (TextView) parent.getChildAt(0);
                ((TextView) parent.getChildAt(0)).setTypeface(((TextView) parent.getChildAt(0)).getTypeface(), Typeface.BOLD);
                if (!sort.getSelectedItem().toString().trim().equals("")){

                    if (sort.getSelectedItem().toString().trim().equals("OL")){
                        sortOnline();
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#23B34C"));
                    }
                    if (sort.getSelectedItem().toString().trim().equals("Msg")){
                        sortMsg();
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.parseColor("#3DA1CF"));
                    }
                    if (sort.getSelectedItem().toString().trim().equals("Name")){
                        sortName();
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);


                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Notificacion.getInstance().init(this);
        //SingletonOnPauseTime.getInstance().startClock();
        progressBar = (ProgressBar) findViewById(R.id.gral_progress_bar);
        progressBar.setVisibility(View.GONE);


        //if (Observers.grupo().getMisGrupoList().size() > 0 ){
            //GetMessageById.loadMessagesContador(this);
        //}
        {
            Intent intent = new Intent("finish_activity_loading");
            this.sendBroadcast(intent);
        }
        ((Button)(findViewById(R.id.ws_disconnect))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingletonValues.getInstance().getWebSocket().disconnectStomp();
                Intent intent = new Intent("connection_closed");
                GrupoActivity.this.sendBroadcast(intent);
            }
        });

        SingletonValues.getInstance().setGrupoSeleccionado(null);
        initViews();
        initValues();
        initListener();



        String packageName = "com.privacity.cliente";
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {

            SimpleErrorDialog.errorDialog(this, "SOLICITUD DE PERMISO",
                    "En la siguiente pantalla se le solicitara permisos para poder continuar " +
                            "recibiendo mensajes cuando el movil entre en modo reposo. " +
                            "La aplicacion utiliza su propia recepcion de mensajes, " +
                            "por seguridad descarta soluciones de terceros como las " +
                            "proporcionadas por Google. Garantizamos que no afectará " +
                            "en consumo de la bateria, ni del consumo de datos mas de lo " +
                            "estrictamente necesario. \n" +
                            "Si acepta puede cambiar la configuracion en cualquier momento. " +
                            "Si no acepta seguira funcionando pero en Modo Reposo " +
                            "no recibira notificacion alguna.", new SimpleErrorDialog.PasswordValidationI(){

                        @Override
                        public void action() {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:"+packageName));
                            startActivity(intent);
                        }
                    });


        }

        RestCalls.loadMessagesContador(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Observers.grupo().setGrupoOnTop(true);
        actualizarLista();
        SingletonValues.getInstance().setGrupoSeleccionado(null);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Notificacion.ID);

    }

    private void alertClose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Cerrar App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GrupoActivity.super.onBackPressed();
                Observers.message().dessuscribirse(GrupoActivity.this);
                SingletonValues.getInstance().setLogout(false);
                Intent intent = new Intent("finish_all_activities");
                GrupoActivity.this.sendBroadcast(intent);
            }
        });

        builder.setNegativeButton("Cerrar Session", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GrupoActivity.super.onBackPressed();
                Observers.message().dessuscribirse(GrupoActivity.this);
                SharedPreferencesUtil.deleteSharedPreferencesUserPass(GrupoActivity.this);
                SingletonValues.getInstance().setLogout(true);
                {
                    Intent intent = new Intent("finish_all_activities");
                    GrupoActivity.this.sendBroadcast(intent);
                }
                {
                    Intent intent = new Intent(GrupoActivity.this, MainActivity.class);
                    GrupoActivity.this.startActivity(intent);
                }

            }
        });


        builder.setNeutralButton("Minimizar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                GrupoActivity.this.moveTaskToBack(true);
                //SharedPreferencesUtil.deleteSharedPreferencesUserPass(GrupoActivity.this);
            }
        });
        //builder.setTitle("Si presiona OK cerrará la aplicacion");

        AlertDialog dialog = builder.create();

        dialog.show();

    }
    @Override
    public void onBackPressed() {
        Observers.grupo().setGrupoOnTop(false);
        this.moveTaskToBack(true);

    }



    @Override
    public void finish() {

        super.finish();
        Observers.grupo().setGrupoOnTop(false);
        SingletonValues.getInstance().getWebSocket().disconnectStomp();
        //Intent intent = new Intent(this, MainActivity.class);
        //this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        int id = itemMenu.getItemId();

        if ( id ==R.id.boton_add_grupo){
            Intent intent = new Intent(GrupoActivity.this, AddGrupoActivity.class);
            startActivity(intent);
        }else if ( id == R.id.menu_grupo_mi_cuenta){
            Intent intent = new Intent(GrupoActivity.this, MyAccountActivity.class);
            startActivity(intent);
        }else if ( id == R.id.menu_grupo_server_gral_conf){
            Intent intent = new Intent(GrupoActivity.this, AboutActivity.class);
            startActivity(intent);
        }else if ( id == R.id.menu_item_grupo_logout){
            alertClose();
        }else if ( id == R.id.menu_grupo_codigo_invitacion){


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

    private void initViews(){
        rvLista = findViewById(R.id.rv_message_detail_list);
        svSearch = findViewById(R.id.svSearchGrupo2);
    }

    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);

        if (items != null)  items.clear();
        items = getItems();
        adapter = new RecyclerGrupoAdapter(items, this, GrupoActivity.this);
        rvLista.setAdapter(adapter);
    }

    private void initListener() {
        svSearch.setOnQueryTextListener(this);
    }

    private List<ItemListGrupo> getItems() {

        Set<Grupo> list = Observers.grupo().getMisGrupoList();
        ArrayList<ItemListGrupo> r = new ArrayList<ItemListGrupo>();
        for (Grupo g : list){
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

        items.add(0,i);
        adapter.originalItems.add(0,i);
        //rvLista.selec.setSelection(adapter.getCount()-1);
        rvLista.scrollToPosition(0);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void nuevoMensaje(ProtocoloDTO protocoloDTO) {
        refrescarUnread(protocoloDTO.getMessageDTO().getIdGrupo());
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
            if (posicion != 0){
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
            if (posicion != 0){
                ItemListGrupo item = adapter.originalItems.get(posicion);
                adapter.originalItems.remove(posicion);
                adapter.originalItems.add(0, item);
                Observers.grupo().moverGrupo(item.getGrupo().getIdGrupo());
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void cambioEstado(MessageDetailDTO m) {
        refrescarUnread(m.getIdGrupo());
    }

    @Override
    public void cambioUnread(String idGrupo) {
        refrescarUnread(idGrupo);

    }


    @Override
    public void removeGrupo(String idGrupo) {
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
    public void avisarLock(GrupoDTO g) {

        if (SingletonValues.getInstance().getGrupoSeleccionado() != null
                && SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.idGrupo)
        ) {

            if ( SingletonValues.getInstance().getGrupoSeleccionado().getLock().isEnabled() ) {
                {
                    Intent intent = new Intent("finish_message_activity");
                    this.sendBroadcast(intent);
                }

                {
                    Intent intent = new Intent("finish_activity");
                    this.sendBroadcast(intent);
                }
            }
        }

    }

    @Override
    public void emptyList() {
        refrescarUnread(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
    }

    @Override
    public void mensajeAddItem(MessageDTO miMensaje, String asyncId) {
        refrescarUnread(miMensaje.getIdGrupo());
    }

    @Override
    public void borrarMensaje(MessageDetailDTO detail) {
        refrescarUnread(detail.getIdGrupo());
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
        && SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.idGrupo)
        ) {

            {
                Intent intent = new Intent("finish_message_activity");
                this.sendBroadcast(intent);
            }

            {
                Intent intent = new Intent("finish_activity");
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
