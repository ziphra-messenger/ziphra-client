package com.privacity.cliente.activity.grupoinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.addmember.AddMembersToGrupoActivity;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupoinfo.lock.GrupoInfoLock;
import com.privacity.cliente.activity.grupoinfo.nameframe.NameFrameView;
import com.privacity.cliente.activity.message.delegate.BloqueoRemotoDelegate;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.util.DialogsToShow;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.UserForGrupoDTO;
import com.privacity.common.dto.request.GrupoRemoveUserDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;

public class GrupoInfoActivity extends CustomAppCompatActivity implements
        ObservadoresPasswordGrupo, RecyclerGrupoInfoAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {
    private static final String TAG = "GrupoInfoActivity";
    private static final String CONSTANT__BT_GRUPO_INFO_BLOQUEO_REMOTO = "bt_grupo_info_bloqueo_remoto_";
    private static final String CONSTANT__FIND_BY__ID = "id";
    public ProgressBar progressBar;
    private GrupoInfoNicknameFrame nickname;
    private Button btGrupoInfoSalir;
    private RecyclerView rvLista;
    private List<ItemListGrupoInfo> items;
    private RecyclerGrupoInfoAdapter adapter;
    private Button btGrupoInfoMenuAcciones;
    private LinearLayout tlGrupoInfoMenuAccionesContent;

    private Button btGrupoInfoMenuName;
    private TableLayout tlGrupoInfoMenuNameContent;

    private Button btGrupoInfoMenuMessage;
    private LinearLayout tlGrupoInfoMenuMessageContent;

    private Button btGrupoInfoMenuLista;
    private LinearLayout tlGrupoInfoMenuListaContent;
    private Button btGrupoInfoAddMember;


    private Grupo grupoSeleccionado;
    private Button btGrupoInfoDeleteGrupo;
    private TextView nameGrupoName;

    private ConfiguracionGeneral configuracionGeneral;

    private GrupoInfoLock lock;
    private NameFrameView name;
    private TextView hideMemberAdminMessage;

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_grupoinfo, menu);

        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_info);
        grupoSeleccionado = SingletonValues.getInstance().getGrupoSeleccionado();

        initActionBar();

        Observers.passwordGrupo().suscribirse(this);
        progressBar = (ProgressBar) findViewById(R.id.common__progress_bar);
        progressBar.setVisibility(View.GONE);
        try {
            rvLista = findViewById(R.id.rv_grupoinfo_menu_lista);
            iniciarName();
            iniciarNickname();
            iniciarLock();
            initValues();
            iniciarNombreGrupo();
            iniciarAcciones();
            iniciarConfiguracion();

            DialogsToShow.noAdminDialog(this, grupoSeleccionado.getIdGrupo());
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(
                    this,
                    getString(R.string.general__error_message_ph1, TAG),
                    e.getMessage(),
                    () -> GrupoInfoActivity.this.finish()
            );

        }

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_ACTIVITY)
                ||action.equals(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY)
                || action.equals(BroadcastConstant.BROADCAST__FINISH_GRUPO_INFO_ACTIVITY)) {
                    myFinish();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_GRUPO_INFO_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES));
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(getString(R.string.grupo_info_activity__title, getGrupoSeleccionado().getName()));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        iniciarAcciones();

    }

    public void myFinish() {
        SingletonValues.getInstance().setImagenFull(null);
        Observers.passwordGrupo().remove(this);
        finish();
    }

    private void iniciarNickname() {
        nickname = new GrupoInfoNicknameFrame(this, progressBar);

    }

    private void iniciarName() {
        name = new NameFrameView(this);
        name.setListener();

    }

    private void iniciarLock() {
        lock = new GrupoInfoLock(this, progressBar);

    }

    private void iniciarConfiguracion() {

        configuracionGeneral = new ConfiguracionGeneral(this);

    }

    private void iniciarNombreGrupo() {
        nameGrupoName = (TextView) findViewById(R.id.grupoinfo_name_grupo_name);
        nameGrupoName.setText(grupoSeleccionado.getName());

    }


    @NotNull
    private View.OnClickListener getListenerGrupoInfoDeleteGrupo() {
        Log.e(TAG, "falta probar el delete gurpo");
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Protocolo p = new Protocolo();
                p.setComponent(ProtocoloComponentsEnum.GRUPO);
                p.setAction(ProtocoloActionsEnum.GRUPO_REMOVE_ME);

                GrupoDTO o = new GrupoDTO();
                o.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
                p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

                RestExecute.doit(GrupoInfoActivity.this, p,
                        new CallbackRest() {

                            @Override
                            public void response(ResponseEntity<Protocolo> response) {

                                if (response.getBody().getCodigoRespuesta() != null) {
                                    Toast.makeText(GrupoInfoActivity.this, response.getBody().getCodigoRespuesta(), Toast.LENGTH_SHORT).show();
                                } else {

                                    Observers.grupo().GrupoRemove(o.getIdGrupo());
                                    Observers.message().removeAllMessageFromUser(o.getIdGrupo(), Singletons.usuario().getUsuario().getIdUsuario());
                                    Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY);
                                    GrupoInfoActivity.this.sendBroadcast(intent);
                                    Toast.makeText(GrupoInfoActivity.this, getString(R.string.grupo_info_activity__delete_grupo__success), Toast.LENGTH_SHORT).show();
                                    GrupoInfoActivity.this.finish();
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
        };
    }

    @NotNull
    private View.OnClickListener getListenerGrupoInfoSalir() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Protocolo p = new Protocolo();
                p.setComponent(ProtocoloComponentsEnum.GRUPO);
                p.setAction(ProtocoloActionsEnum.GRUPO_REMOVE_ME);

                GrupoRemoveUserDTO o = new GrupoRemoveUserDTO();
                o.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
                o.setIdUsuario(Singletons.usuario().getUsuario().getIdUsuario());
                p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

                RestExecute.doit(GrupoInfoActivity.this, p,
                        new CallbackRest() {

                            @Override
                            public void response(ResponseEntity<Protocolo> response) {

                                if (response.getBody().getCodigoRespuesta() != null) {
                                    Toast.makeText(GrupoInfoActivity.this, response.getBody().getCodigoRespuesta(), Toast.LENGTH_SHORT).show();
                                } else {

                                    Observers.grupo().GrupoRemove(o.getIdGrupo());
                                    Observers.message().removeAllMessageFromUser(o.getIdGrupo(), Singletons.usuario().getUsuario().getIdUsuario());
                                    Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY);
                                    GrupoInfoActivity.this.sendBroadcast(intent);
                                    Toast.makeText(GrupoInfoActivity.this, getString(R.string.grupo_info_activity__remove_me__success), Toast.LENGTH_SHORT).show();
                                    GrupoInfoActivity.this.finish();
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
        };
    }

    private void initView() {
        btGrupoInfoSalir = GetButtonReady.get(this, R.id.bt_grupoinfo_salir, new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {
                getListenerGrupoInfoSalir();
            }
        });
        btGrupoInfoAddMember = GetButtonReady.get(this,R.id.bt_grupoinfo_add_member);
        hideMemberAdminMessage = (TextView) findViewById(R.id.frame_grupo_info__hide_member__admin_message);
        if (SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isHideMemberList()){
            hideMemberAdminMessage.setVisibility(View.VISIBLE);
        }else{
            hideMemberAdminMessage.setVisibility(View.GONE);
        }

        btGrupoInfoDeleteGrupo = GetButtonReady.get(this, R.id.bt_grupoinfo_delete_grupo, new ViewCallbackActionInterface() {
            @Override
            public void action(View v) {

            }
        });
    }

    private void iniciarAcciones() {
        initView();




        btGrupoInfoAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrupoInfoActivity.this, AddMembersToGrupoActivity.class);
                startActivity(intent);
            }
        });



        Resources res = this.getResources();


        final Counter count = new Counter();

        ArrayList<Button> mylistBotones = new ArrayList<Button>();


        ArrayList<String> mylist = new ArrayList<String>();
        for (int i = 1; i <= 4; i++) {
            mylist.add(i + "");
            mylistBotones.add(
                    GetButtonReady.get(this, res.getIdentifier(CONSTANT__BT_GRUPO_INFO_BLOQUEO_REMOTO + i, CONSTANT__FIND_BY__ID, this.getPackageName())
                            , v -> check(count, mylistBotones)));

        }
        resetBotonos(mylistBotones);

        //System.out.println("Original List : \n" + mylist);

        Collections.shuffle(mylist);

        for (int i = 1; i <= 4; i++) {
            mylistBotones.get(i - 1).setText(mylist.get(i - 1));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //nickname.getView().setMarquee(true);

    }

    @NotNull
    private View.OnClickListener check(final Counter count, ArrayList<Button> mylistBotones) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((count.getCount()) + "").equals(((Button) view).getText())) {
                    view.setBackgroundColor(Color.parseColor("#57AE74"));

                    count.add();
                    if (count.getCount() > mylistBotones.size()) {
                        new BloqueoRemotoDelegate().ejecutarGrupoBloqueoRemoto(GrupoInfoActivity.this);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                    count.reset();
                                    resetBotonos(mylistBotones);
                                } catch (InterruptedException e) {
                                    count.reset();
                                    resetBotonos(mylistBotones);
                                }
                            }
                        }).start();


                    }
                } else {
                    count.reset();
                    resetBotonos(mylistBotones);
                }
            }
        };
    }

    private void resetBotonos(ArrayList<Button> mylistBotones) {

        for (Button b : mylistBotones) {
            b.setBackgroundColor(Color.RED);
        }
    }

    private void ocultarViewToNotAdmin(View v) {
        v.setBackgroundColor(Color.LTGRAY);
        v.setEnabled(false);
        v.setClickable(false);
    }

    private void iniciarMenu(Button button, final TableLayout tableLayout) {
        PorterDuff.Mode t = button.getCompoundDrawableTintMode();
        if (tableLayout.getVisibility() != View.VISIBLE) {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            button.setCompoundDrawableTintMode(t);
        } else {
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            button.setCompoundDrawableTintMode(t);
        }
    }

    private void initValues() {


        btGrupoInfoMenuAcciones = GetButtonReady.get(this,R.id.bt_grupoinfo_menu_acciones);
        tlGrupoInfoMenuAccionesContent = (LinearLayout) findViewById(R.id.tl_grupoinfo_menu_acciones_content);

        btGrupoInfoMenuName = GetButtonReady.get(this,R.id.bt_grupoinfo_menu_name);
        tlGrupoInfoMenuNameContent = (TableLayout) findViewById(R.id.tl_grupoinfo_menu_name_content);

        btGrupoInfoMenuMessage = GetButtonReady.get(this,R.id.bt_grupo_info_conf_gen__title);
        tlGrupoInfoMenuMessageContent = (LinearLayout) findViewById(R.id.tl_grupoinfo_menu_message_content);


        btGrupoInfoMenuLista = GetButtonReady.get(this,R.id.bt_grupoinfo_menu_lista);
        tlGrupoInfoMenuListaContent = (LinearLayout) findViewById(R.id.tl_grupoinfo_menu_lista_content);

        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuMessage, tlGrupoInfoMenuMessageContent);
        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuLista, tlGrupoInfoMenuListaContent);
        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuName, tlGrupoInfoMenuNameContent);
        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuAcciones, tlGrupoInfoMenuAccionesContent);



        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);

        if (items != null) items.clear();
        getIdsMyGrupos();
    }

    @Override
    public void finish() {
        super.finish();
        Observers.passwordGrupo().remove(this);
    }

    private List<ItemListGrupoInfo> getItems() {
        ArrayList<ItemListGrupoInfo> r = new ArrayList<ItemListGrupoInfo>();
        if (ObserverGrupo.getInstance().getGrupoById(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()) != null) {


            UserForGrupoDTO[] list = ObserverGrupo.getInstance().getGrupoById(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getUsersForGrupoDTO();


            for (UserForGrupoDTO userForGrupoDTO : list) {
                ItemListGrupoInfo i = new ItemListGrupoInfo();
                UserForGrupoDTO e = userForGrupoDTO;
                i.setUsersForGrupoDTO(e);
                r.add(i);
            }
        }


        return r;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        int id = itemMenu.getItemId();

        finish();


        return true;
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
    public void itemClick(ItemListGrupoInfo item) {

    }

    @Override
    public void passwordExpired(Grupo g) {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())) {
            this.finish();
        }
    }

    @Override
    public void passwordSet(Grupo g) {

    }

    @Override
    public void deleteExtraEncrypt(Grupo g) {

    }

    @Override
    public void lock(Grupo g) {

    }

    void getIdsMyGrupos() {


        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_LIST_MEMBERS
        );
        GrupoDTO dto = new GrupoDTO();
        dto.setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(dto));
        RestExecute.doit(GrupoInfoActivity.this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        try {
                            UserForGrupoDTO[] list = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), UserForGrupoDTO[].class);
                            ArrayList<ItemListGrupoInfo> r = new ArrayList<ItemListGrupoInfo>();

                            for (UserForGrupoDTO userForGrupoDTO : list) {
                                ItemListGrupoInfo i = new ItemListGrupoInfo();
                                UserForGrupoDTO e = userForGrupoDTO;
                                i.setUsersForGrupoDTO(e);
                                r.add(i);
                            }

                            items = r;
                            adapter = new RecyclerGrupoInfoAdapter(items, GrupoInfoActivity.this, GrupoInfoActivity.this);
                            rvLista.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        } catch (Exception e) {
                            e.printStackTrace();

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

    @Data
    private static class Counter {
        private int count = 1;

        public void add() {
            count++;
        }

        public void reset() {
            count = 1;
        }
    }
}