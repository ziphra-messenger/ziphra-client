package com.privacity.cliente.activity.loading;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.IdDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.stomp.StateProcess;

public class LoadingActivityGrupoRestDelegate {
    private final LoadingActivity loadingActivity;

    public LoadingActivityGrupoRestDelegate(LoadingActivity loadingActivity) {
        this.loadingActivity = loadingActivity;
    }

    void getIdsMyGrupos() {
        Observers.grupo().suscribirse(loadingActivity);
        loadingActivity.addTextConsole("Getting Ids My Grupos");
        SingletonValues.getInstance().setGrupoSeleccionado(null);

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_GET_IDS_MY_GRUPOS
        );
        RestExecute.doit(loadingActivity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        try {
                            IdDTO[] l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), IdDTO[].class);

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
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
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

    void getGruposThread(IdDTO[] g) {

        final List<List<IdDTO>> toProcess = new ArrayList<List<IdDTO>>();
        int j = 0;
        for (int i = 1; i < g.length + 1; i++) {
            if (toProcess.size() < j + 1) {
                toProcess.add(j, new ArrayList<IdDTO>());
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

    public void getGrupoById(List<IdDTO> g) {
        loadingActivity.addTextConsole("Getting Grupo by Ids");
        g.stream().forEach(grupo ->
                loadingActivity.addTextConsole("Getting Grupo id : " + grupo.getId())
        );
        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_GET_GRUPO_BY_IDS);

        p.setObjectDTO(GsonFormated.get().toJson(g));

        RestExecute.doit(loadingActivity, p,
                new CallbackRest() {

                    private int countGruposPROCESSEDInterno=0;
                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        Grupo[] l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), Grupo[].class);

                        for (int i = 0; i < l.length; i++) {
                            Observers.grupo().addGrupo(l[i], false);
                            countGruposPROCESSEDInterno++;
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
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