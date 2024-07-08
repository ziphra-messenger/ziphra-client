package com.privacity.cliente.activity.messageresend;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageResendActivity extends CustomAppCompatActivity implements RecyclerMessageResendAdapter.RecyclerItemClick, SearchView.OnQueryTextListener {

    private RecyclerView rvMessageResendList;
    private Button btMessageResendResend;
    private RecyclerMessageResendAdapter adapter;
    private List<ItemListMessageResend> items;
    private Switch black;
    private Switch anonimo;
    private Switch time;
    private Switch personalKey;
    private TextView personalKeyValue;
    private Spinner spinnerTime;
    private Button tbMessageResendAvanzada;
    private View resendAvanzada;
    private SearchView svSearch;

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_resend);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("ReEnviar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //SingletonValues.getInstance().setGrupoSeleccionado(null);
        initViews();
        initValues();
        initListener();

        black = (Switch) findViewById(R.id.resend_sw_message_always_black);
        anonimo= (Switch) findViewById(R.id.resend_sw_message_always_anonimo);
        time= (Switch) findViewById(R.id.resend_sw_message_always_time);
        personalKey= (Switch) findViewById(R.id.resend_sw_message_always_personal_key);
        personalKeyValue = (TextView) findViewById(R.id.resend_tv_message_secret_key);
        spinnerTime = (Spinner) findViewById(R.id.resend_spinner_time);
        tbMessageResendAvanzada = (Button) findViewById(R.id.bt_message_resend_avanzada);

        resendAvanzada = (View) findViewById(R.id.resend_avanzada);

        tbMessageResendAvanzada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resendAvanzada.getVisibility() == View.VISIBLE){
                    resendAvanzada.setVisibility(View.GONE);
                }else{
                    resendAvanzada.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        myFinish();
        return true;
    }

    @Override
    public void onBackPressed() {
        myFinish();
    }
    private void myFinish(){
//        SingletonValues.getInstance().setMessageDetailSeleccionado(null);
        finish();
    }

    private void initListener() {
        svSearch.setOnQueryTextListener(this);
        btMessageResendResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    myFinish();


                    for (ItemListMessage item : Observers.message().getMessageSelected()){
                        Message mensajeNuevo = item.getMessage();
                        for ( int i = 0 ; i < items.size() ; i++){
                            if ( items.get(i).isChecked()){

                                String mediaData=null;
                                String mediaType=null;

                                (new MessageUtil()).sendMessage(MessageResendActivity.this,
                                        mensajeNuevo.getParentReply(), null, items.get(i).getGrupo().getIdGrupo(),
                                        mensajeNuevo.getText(),
                                        mensajeNuevo.getMediaDTO(),
                                        mensajeNuevo.isBlackMessage(),
                                        mensajeNuevo.isTimeMessage(),
                                        mensajeNuevo.isAnonimo(),
                                        mensajeNuevo.isSecretKeyPersonal(),
                                        true,
                                        false, mensajeNuevo.getTimeMessage());
                            }
                           // Observers.message().getMessageSelected().remove(mensajeNuevo);
                        }


                        Toast toast= Toast. makeText(getApplicationContext(),"Mensaje/s Reenviado",Toast. LENGTH_SHORT);
                        toast.show();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(MessageResendActivity.this, "Error Resend", e.getMessage());
                    return;
                }
            }
        });
    }

/*    private MessageDTO getMensaje(Message mensajeBase) throws Exception {

        //MessageDetailDTO detail = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO();
        //MessageDTO mensajeBase = Observers.message().getMensajesPorId(detail.buildIdMessageToMap());
        MessageDTO mensajeNuevo = new MessageDTO();


        String txtStr= new String(mensajeBase.getText());

        if (txtStr == null || txtStr.equals("")) return null;

        EditText tvMessageSecretKey = (EditText) findViewById(R.id.tv_message_secret_key);

        AEStoUse secretKeyPersonal=null;
        if ( mensajeBase.isSecretKeyPersonal()){

            secretKeyPersonal = AEStoUseFactory.getAEStoUseExtra
                    (tvMessageSecretKey.getText().toString(), tvMessageSecretKey.getText().toString());
            txtStr = secretKeyPersonal.getAESDecrypt(txtStr);
        }

        //txtStr = ObservatorGrupos.getInstance().getGrupoAESToUseById(idGrupo).getAESDecrypt(txtStr);
        mensajeNuevo.setText(txtStr);
        byte[] mediaData=null;
        if (mensajeBase.getMediaDTO() != null){


            mediaData = Observers.grupo().getGrupoById(mensajeBase.getIdGrupo()).getAESToUse().getAESDecrypt(mensajeBase.getMediaDTO().getData());
            if ( mensajeBase.isSecretKeyPersonal()){

                mediaData = secretKeyPersonal.getAESDecrypt(mediaData);

            }

            mensajeNuevo.setMediaDTO(new MediaDTO());
            mensajeNuevo.getMediaDTO().setMediaType(mensajeBase.getMediaDTO().getMediaType());
            mensajeNuevo.getMediaDTO().setData(mediaData);


        }

        return mensajeNuevo;
    }*/

    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvMessageResendList.setLayoutManager(manager);

        items = getItems();
        adapter = new RecyclerMessageResendAdapter(items, this, MessageResendActivity.this);
        rvMessageResendList.setAdapter(adapter);
    }

    private List<ItemListMessageResend> getItems() {

        Set<Grupo> list = Observers.grupo().getMisGrupoList();
        ArrayList<ItemListMessageResend> r = new ArrayList<ItemListMessageResend>();
        for (Grupo g : list){

            if (!g.isGrupoInvitation()){
                ItemListMessageResend i = new ItemListMessageResend();
                i.setGrupo(g);
                i.setChecked(false);

                r.add(i);

            }
        }
        return r;
    }
    private void initViews(){
        rvMessageResendList = findViewById(R.id.rv_message_resend_list);
        btMessageResendResend = findViewById(R.id.bt_message_resend_send);
        svSearch = findViewById(R.id.svSearchResend);
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
    public void itemClick(ItemListMessageResend item) {

    }
}