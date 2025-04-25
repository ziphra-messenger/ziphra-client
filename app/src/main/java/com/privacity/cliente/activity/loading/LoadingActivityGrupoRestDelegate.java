package com.privacity.cliente.activity.loading;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.stomp.StateProcess;

public class LoadingActivityGrupoRestDelegate {

    private static final String TAG = "LoadingActivityGrupoRestDelegate";
    private final LoadingActivity loadingActivity;

    public LoadingActivityGrupoRestDelegate(LoadingActivity loadingActivity) {
        this.loadingActivity = loadingActivity;
    }

    void getIdsMyGrupos() {
        Observers.grupo().suscribirse(loadingActivity);
        loadingActivity.addTextConsole("Getting Ids My Grupos");
        SingletonValues.getInstance().setGrupoSeleccionado(null);

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_GET_IDS_MY_GRUPOS
        );
        RestExecute.doit(loadingActivity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        GrupoDTO[] l;
                        try {
                            if (response.getBody().getObjectDTO() == null){
                                l = new GrupoDTO[0];
                            }else{
                                l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), GrupoDTO[].class);
                            }


//                            if (l==null){
//                                loadingActivity.addTextConsole("My Ids Grupos Count:" + 0);
//                                loadingActivity.setCountGruposOK(0);
//                                loadingActivity.setCountGruposPROCESSED(0);
//                                loadingActivity.setGetGrupos(StateProcess.SUCESS);
//                                loadingActivity.endProcess();
//                                return;
//                            }
                            loadingActivity.setCountGruposTOTAL(l.length);

                            loadingActivity.addTextConsole("My Ids Grupos Count:" + l.length);


                            if (l.length == 0) {
                                loadingActivity.setCountGruposOK(l.length);
                                loadingActivity.setCountGruposPROCESSED(l.length);
                                loadingActivity.setGetGrupos(StateProcess.SUCESS);

                                loadingActivity.endProcess();
                            } else {
                                loadingActivity.setGetGrupos(StateProcess.WORKING);
                                getGruposThread(l);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            loadingActivity.setGetGrupos(StateProcess.WORKING);

                            loadingActivity.endProcess();
                        }


                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        loadingActivity.setGetGrupos(StateProcess.FAIL);

                        loadingActivity.addTextConsole("get Grupos onError STATUS: " + response.getStatusCode());

                        if (response.getBody() != null) {
                            loadingActivity.addTextConsole("get Grupos onError getCodigoRespuesta: " + response.getBody().getCodigoRespuesta());
                        }

                        loadingActivity.endProcess();
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                    }
                });


    }

    void getGruposThread(GrupoDTO[] g) {

        final List<List<GrupoDTO>> toProcess = new ArrayList<List<GrupoDTO>>();
        int j = 0;
        for (int i = 1; i < g.length + 1; i++) {
            if (toProcess.size() < j + 1) {
                toProcess.add(j, new ArrayList<GrupoDTO>());
            }
            toProcess.get(j).add(g[i - 1]);

            if (i % 5 == 0 || i == g.length) {
                j++;
            }
        }

        toProcess.stream().forEach(listaIds ->
                getGrupoById(listaIds)
        );

    }

    public void getGrupoById(List<GrupoDTO> g) {
        loadingActivity.addTextConsole("Getting Grupo by Ids");
        g.stream().forEach(grupo ->
                loadingActivity.addTextConsole("Getting Grupo id : " + grupo.getIdGrupo())
        );
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_GET_GRUPO_BY_IDS);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(g));

        RestExecute.doit(loadingActivity, p,
                new CallbackRest() {

                    private int countGruposPROCESSEDInterno=0;
                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        //Log.d(TAG,"response.getBody(): " + response.getBody());
                        //Log.d(TAG,"response.getBody().getObjectDTO(): " + response.getBody().getObjectDTO());
                        GrupoDTO[] l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), GrupoDTO[].class);

                        for (GrupoDTO grupoDTO : l) {
                            Observers.grupo().addGrupo(new Grupo(grupoDTO), false);
                            //Log.d(TAG,"l[i]: " + UtilsStringSingleton.getInstance().gson().toJson(l[i]));
                            countGruposPROCESSEDInterno++;
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        loadingActivity.addTextConsole("Group onError: " + response.getStatusCode() + " Codigo Error: " + response.getBody().getCodigoRespuesta());
                        loadingActivity.setCountGruposPROCESSED(loadingActivity.getCountGruposPROCESSED() +  g.size() - countGruposPROCESSEDInterno);
                        loadingActivity.addTextConsole("Grupo Procesados : " + loadingActivity.getCountGruposPROCESSED() + "/" + loadingActivity.getCountGruposTOTAL());

                        loadingActivity.endProcess();
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        loadingActivity.addTextConsole("Group beforeShowErrorMessage: " + msg);
                    }

                });


    }
}