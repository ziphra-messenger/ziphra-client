package com.privacity.cliente.activity.grupoinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.util.DialogsToShow;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.IdDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.UserForGrupoDTO;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

public class GrupoInfoActivity extends CustomAppCompatActivity implements
        ObservadoresPasswordGrupo, RecyclerGrupoInfoAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {

    private GrupoInfoNicknameFrame nickname;
    private Button btGrupoInfoSalir;
    private RecyclerView rvLista;
    private List<ItemListGrupoInfo> items;
    private RecyclerGrupoInfoAdapter adapter;

    public ProgressBar progressBar;

    private Button btGrupoInfoMenuAcciones;
    private LinearLayout tlGrupoInfoMenuAccionesContent;

    private Button btGrupoInfoMenuName;
    private TableLayout tlGrupoInfoMenuNameContent;

    private Button btGrupoInfoMenuMessage;
    private TableLayout tlGrupoInfoMenuMessageContent;

    private Button btGrupoInfoMenuLista;
    private TableLayout tlGrupoInfoMenuListaContent;
    private Button btGrupoInfoAddMember;


    private Grupo grupoSeleccionado;
    private Button btGrupoInfoDeleteGrupo;
    private TextView nameGrupoName;

    private ConfiguracionGeneral configuracionGeneral;

    private GrupoInfoLock lock;

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
        grupoSeleccionado= SingletonValues.getInstance().getGrupoSeleccionado();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Informacion Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Observers.passwordGrupo().suscribirse(this);
        progressBar = (ProgressBar) findViewById(R.id.gral_progress_bar);
        progressBar.setVisibility(View.GONE);
        try{
             rvLista = findViewById(R.id.rv_grupoinfo_menu_lista);
            iniciarNickname();
             iniciarLock();
            initValues();
            iniciarNombreGrupo();
            iniciarAcciones();
            iniciarConfiguracion();

            DialogsToShow.noAdminDialog(this,grupoSeleccionado.getIdGrupo());
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(
                    this,
                    "ERROR A CARGAR GRUPO INFO",
                    e.getMessage(),
                    () -> GrupoInfoActivity.this.finish()
            );

        }

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity")) {
                    myFinish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));
    }

    public void myFinish() {
        SingletonValues.getInstance().setImagenFull(null);
        Observers.passwordGrupo().remove(this);
        finish();
    }

    private void iniciarNickname() {
        nickname = new GrupoInfoNicknameFrame(this, progressBar);
        nickname.setListener();
        nickname.loadValues();
    }


    private void iniciarLock() {
        lock = new GrupoInfoLock(this, progressBar);
        lock.setListener();
        lock.loadValues();
    }

    private void iniciarConfiguracion() {

        configuracionGeneral = new ConfiguracionGeneral(this);
        configuracionGeneral.setReenviar((CheckBox) findViewById(R.id.grupo_info_conf_resend));

        configuracionGeneral.setAnonimo(new MultipleOption(
                (TextView) findViewById(R.id.grupo_info_conf_anonimo_titulo),
                (RadioGroup) findViewById(R.id.grupo_info_conf_anonimo_grupo),
                (RadioButton)findViewById(R.id.grupo_info_conf_anonimo_permitir),
                (RadioButton)findViewById(R.id.grupo_info_conf_anonimo_bloquear),
                (RadioButton)findViewById(R.id.grupo_info_conf_anonimo_obligatorio))
        );
        configuracionGeneral.setTemporal(new ConfiguracionGeneralTemporal(
                (CheckBox) findViewById(R.id.grupo_info_conf_temporal_obligatorio),
                (Spinner) findViewById(R.id.grupo_info_conf_temporal_maximo_tiempo_permitido)
        ));
        configuracionGeneral.setConfBlackObligatorioAdjunto((CheckBox) findViewById(R.id.grupo_info_conf_black_obligatorio_adjunto));

        configuracionGeneral.setAudioChat(new ConfiguracionGeneralAudioChat(
                (Spinner) findViewById(R.id.grupo_info_conf_audiochat_maximo_tiempo),
                        new MultipleOption(
                                (TextView) findViewById(R.id.grupo_info_conf_audiochat_titulo),
                                (RadioGroup) findViewById(R.id.grupo_info_conf_audiochat_grupo),
                                (RadioButton)findViewById(R.id.grupo_info_conf_audiochat_permitir),
                                (RadioButton)findViewById(R.id.grupo_info_conf_audiochat_bloquear), null)
                )
        );

        configuracionGeneral.setDescarga(new ConfiguracionGeneralDescarga(
                (CheckBox) findViewById(R.id.grupo_info_conf_descarga_imagen)
//                (CheckBox) findViewById(R.id.grupo_info_conf_descarga_audio),
//                (CheckBox) findViewById(R.id.grupo_info_conf_descarga_video)
        ));
        configuracionGeneral.setOtras(new ConfiguracionGeneralOtras(
                (CheckBox) findViewById(R.id.grupo_info_conf_gen_cambiar_nickname),
                (CheckBox) findViewById(R.id.grupo_info_conf_gen_ocultar_detalles),
                (CheckBox) findViewById(R.id.grupo_info_conf_gen_ocultar_estado),
                (CheckBox) findViewById(R.id.grupo_info_conf_gen_ocultar_lista_integrantes)
                ));


        configuracionGeneral.setExtraEncript(new MultipleOption(
                (TextView) findViewById(R.id.grupo_info_conf_extra_encrip_titulo),
                (RadioGroup) findViewById(R.id.grupo_info_conf_extra_encrip_grupo),
                (RadioButton)findViewById(R.id.grupo_info_conf_extra_encrip_permitir),
                (RadioButton)findViewById(R.id.grupo_info_conf_extra_encrip_bloquear),
                (RadioButton)findViewById(R.id.grupo_info_conf_extra_encrip_obligatorio))
        );


        configuracionGeneral.setReset((Button) findViewById(R.id.grupo_info_conf_reset));
        configuracionGeneral.setSave((Button) findViewById(R.id.grupo_info_conf_save));

        configuracionGeneral.setListener();

        configuracionGeneral.loadValues(grupoSeleccionado);
    }

    private void iniciarNombreGrupo() {
        nameGrupoName = (TextView)findViewById(R.id.grupoinfo_name_grupo_name);
        nameGrupoName.setText(grupoSeleccionado.getName());

    }


    @NotNull
    private View.OnClickListener getListenerGrupoInfoDeleteGrupo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProtocoloDTO p = new ProtocoloDTO();
                p.setComponent("/grupo");
                p.setAction("/grupo/removeMe");

                IdDTO o = new IdDTO();
                o.setId(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
                p.setObjectDTO(GsonFormated.get().toJson(o));

                RestExecute.doit(GrupoInfoActivity.this, p,
                        new CallbackRest() {

                            @Override
                            public void response(ResponseEntity<ProtocoloDTO> response) {

                                if (response.getBody().getCodigoRespuesta() != null) {
                                    Toast.makeText(GrupoInfoActivity.this, response.getBody().getCodigoRespuesta(), Toast.LENGTH_SHORT).show();
                                } else {

                                    Observers.grupo().GrupoRemove(o.getId());
                                    Observers.message().removeAllMessageFromUser(o.getId(), SingletonValues.getInstance().getUsuario().getIdUsuario());
                                    Intent intent = new Intent("finish_message_activity");
                                    GrupoInfoActivity.this.sendBroadcast(intent);
                                    Toast.makeText(GrupoInfoActivity.this, "Ha dejado el grupo", Toast.LENGTH_SHORT).show();
                                    GrupoInfoActivity.this.finish();
                                }


                            }

                            @Override
                            public void onError(ResponseEntity<ProtocoloDTO> response) {

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

                ProtocoloDTO p = new ProtocoloDTO();
                p.setComponent("/grupo");
                p.setAction("/grupo/removeMe");

                IdDTO o = new IdDTO();
                o.setId(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
                p.setObjectDTO(GsonFormated.get().toJson(o));

                RestExecute.doit(GrupoInfoActivity.this, p,
                        new CallbackRest() {

                            @Override
                            public void response(ResponseEntity<ProtocoloDTO> response) {

                                if (response.getBody().getCodigoRespuesta() != null) {
                                    Toast.makeText(GrupoInfoActivity.this, response.getBody().getCodigoRespuesta(), Toast.LENGTH_SHORT).show();
                                } else {

                                    Observers.grupo().GrupoRemove(o.getId());
                                    Observers.message().removeAllMessageFromUser(o.getId(), SingletonValues.getInstance().getUsuario().getIdUsuario());
                                    Intent intent = new Intent("finish_message_activity");
                                    GrupoInfoActivity.this.sendBroadcast(intent);
                                    Toast.makeText(GrupoInfoActivity.this, "Ha dejado el grupo", Toast.LENGTH_SHORT).show();
                                    GrupoInfoActivity.this.finish();
                                }


                            }

                            @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                        });

            }
        };
    }




    private void iniciarAcciones(){
        btGrupoInfoSalir = (Button) findViewById(R.id.bt_grupoinfo_salir);
        btGrupoInfoSalir.setOnClickListener(
                getListenerGrupoInfoSalir()
        );

        btGrupoInfoAddMember = (Button) findViewById(R.id.bt_grupoinfo_add_member);
        btGrupoInfoAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GrupoInfoActivity.this, AddMembersToGrupoActivity.class);
                startActivity(intent);
            }
        });
        btGrupoInfoDeleteGrupo = (Button) findViewById(R.id.bt_grupoinfo_delete_grupo);
        btGrupoInfoDeleteGrupo.setOnClickListener(
                getListenerGrupoInfoDeleteGrupo()
        );

    }
    private void ocultarViewToNotAdmin(View v){
        v.setBackgroundColor(Color.LTGRAY);
        v.setEnabled(false);
        v.setClickable(false);
    }
    private void iniciarMenu(Button button, final TableLayout tableLayout){
        PorterDuff.Mode t = button.getCompoundDrawableTintMode();
        if (tableLayout.getVisibility() != View.VISIBLE){
            button.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_arrow_down,0);
            button.setCompoundDrawableTintMode(t);
        }else{
            button.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_arrow_up, 0);
            button.setCompoundDrawableTintMode(t);
        }
    }


    private void initValues() {


        btGrupoInfoMenuAcciones = (Button)findViewById(R.id.bt_grupoinfo_menu_acciones);
        tlGrupoInfoMenuAccionesContent = (LinearLayout)findViewById(R.id.tl_grupoinfo_menu_acciones_content);

        btGrupoInfoMenuName = (Button)findViewById(R.id.bt_grupoinfo_menu_name);
        tlGrupoInfoMenuNameContent = (TableLayout)findViewById(R.id.tl_grupoinfo_menu_name_content);

        btGrupoInfoMenuMessage = (Button)findViewById(R.id.bt_grupoinfo_menu_message);
        tlGrupoInfoMenuMessageContent = (TableLayout)findViewById(R.id.tl_grupoinfo_menu_message_content);




        btGrupoInfoMenuLista = (Button)findViewById(R.id.bt_grupoinfo_menu_lista);
        tlGrupoInfoMenuListaContent = (TableLayout)findViewById(R.id.tl_grupoinfo_menu_lista_content);

        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuMessage, tlGrupoInfoMenuMessageContent);
        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuLista, tlGrupoInfoMenuListaContent);
        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuName, tlGrupoInfoMenuNameContent);
        MenuAcordeonUtil.setActionMenu(btGrupoInfoMenuAcciones, tlGrupoInfoMenuAccionesContent);



        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);

        if (items != null)  items.clear();
getIdsMyGrupos();
    }
    @Override
    public void finish() {
        super.finish();
        Observers.passwordGrupo().remove(this);
    }
    private List<ItemListGrupoInfo> getItems() {

        UserForGrupoDTO[] list = ObserverGrupo.getInstance().getGrupoById(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()).getUsersForGrupoDTO();
        ArrayList<ItemListGrupoInfo> r = new ArrayList<ItemListGrupoInfo>();

        for (int k = 0; k < list.length ; k++){
            ItemListGrupoInfo i = new ItemListGrupoInfo();
            UserForGrupoDTO e = list[k];
            i.setUsersForGrupoDTO(e);
            r.add(i);
        }


        return new ArrayList<ItemListGrupoInfo>();
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
        if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())){
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


        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_GRUPO);
        p.setAction("/grupo/list/members"
        );
        GrupoDTO dto = new GrupoDTO().setIdGrupo(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        p.setObjectDTO(GsonFormated.get().toJson(dto));
        RestExecute.doit(GrupoInfoActivity.this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        try {
                            UserForGrupoDTO[] list = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), UserForGrupoDTO[].class);
                            ArrayList<ItemListGrupoInfo> r = new ArrayList<ItemListGrupoInfo>();

                            for (int k = 0; k < list.length ; k++){
                                ItemListGrupoInfo i = new ItemListGrupoInfo();
                                UserForGrupoDTO e = list[k];
                                i.setUsersForGrupoDTO(e);
                                r.add(i);
                            }

                            items=r;
                            adapter = new RecyclerGrupoInfoAdapter(items, GrupoInfoActivity.this, GrupoInfoActivity.this);
                            rvLista.setAdapter(adapter);
                            adapter.notifyDataSetChanged();




                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                    }
                });


    }
}