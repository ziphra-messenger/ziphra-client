package com.privacity.cliente.singleton.observers;

import android.util.Log;

import com.privacity.cliente.common.constants.DeveloperConstant;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoChangeUserRoleDTO;
import com.privacity.common.dto.response.SaveGrupoGralConfLockResponseDTO;
import com.privacity.common.enumeration.GrupoRolesEnum;
import com.privacity.common.enumeration.MessageState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ObserverGrupo  implements SingletonReset {
    private static final String TAG = "ObserverGrupo";
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<ObservadoresGrupos> o = new ArrayList<ObservadoresGrupos>();
    private boolean grupoOnTop=false;
    private LinkedHashMap<String, Grupo> misGrupoPorId = new LinkedHashMap<String, Grupo>();
    private static ObserverGrupo instance;
    //private Map<String, GrupoUserConfDTO> grupoUserConfPorId = new HashMap<String, GrupoUserConfDTO>();

    public boolean amIReadOnly(String idGrupo){
        if (DeveloperConstant.validateReadOnly == false) return false;
        Grupo g = this.getGrupoById(idGrupo);
        return g.iAmReadOnly();
    }

    public GrupoRolesEnum whichIsMyRole(String idGrupo){
        if (DeveloperConstant.validateAdmin == false) return GrupoRolesEnum.ADMIN;
        Grupo g = this.getGrupoById(idGrupo);
        return g.getUserForGrupoDTO().getRole();
    }

    public boolean amIAdmin(String idGrupo){
        if (DeveloperConstant.validateAdmin == false) return true;
        Grupo g = this.getGrupoById(idGrupo);
        return g.iAmAdmin();
    }





    public Grupo getGrupoById(String idGrupo){

        return this.misGrupoPorId.get(idGrupo);
    }
    public void avisarCambioUnread(MessageDetail m){
        if (m.getEstado().equals(MessageState.MY_MESSAGE_SENDING))
        for( ObservadoresGrupos e : o) {
            e.cambioUnread(m.getIdGrupo());
        }
    }
    public void dessuscribirse( ObservadoresGrupos n) {
        o.remove(n);
    }

    public void avisarCambioUnread(String idGrupo){
        for( ObservadoresGrupos e : o) {
            e.cambioUnread(idGrupo);
        }
    }



    public static ObserverGrupo getInstance() {
        if (instance == null){
            instance = new ObserverGrupo();
        }
        return instance;
    }
        private ObserverGrupo() { }



    public void suscribirse( ObservadoresGrupos n) {
            o.add(n);/*
            for ( int i = o.size()-1 ; i  > 0 ; i--){
                if ( o.get(i) instanceof GrupoActivity){
                    o.remove(i);
                }
           }*/

        }

    public void moverGrupo(String idGrupo) {

        Grupo g = this.misGrupoPorId.get(idGrupo);
        this.misGrupoPorId.remove(idGrupo);
        this.misGrupoPorId.put(g.getIdGrupo(),g);
    }
    public void addGrupos(Protocolo protocolo) {
        Grupo[] gs = UtilsStringSingleton.getInstance().gson().fromJson(protocolo.getObjectDTO(), Grupo[].class);

        for (Grupo g : gs) {
            addGrupo(g, true);
        }
    }
    public void addGrupo(Protocolo protocolo) {
        GrupoDTO g = UtilsStringSingleton.getInstance().gson().fromJson(protocolo.getObjectDTO(), GrupoDTO.class);
        addGrupo(new Grupo(g),true);
    }

    public void addGrupo(Grupo grupo, boolean index0) {
        //System.out.println("*********************************************");
        //System.out.println(UtilsStringSingleton.getInstance().gsonToSend(grupo.getDto()));
        if ( misGrupoPorId.containsKey(grupo.getIdGrupo())){

            //agregar el name del invitation
//            System.out.println("VIEJO GRUPO");
  //          System.out.println(UtilsStringSingleton.getInstance().gsonToSend(misGrupoPorId.get(grupo.getIdGrupo())));
            //viejoGrupo =misGrupoPorId.get(grupo.getIdGrupo());
        }else{
            misGrupoPorId.put(grupo.getIdGrupo(), grupo);
        }
    //    System.out.println("*********************************************");
        avisar(grupo);
    }

    @Override
    public void reset() {
        Iterator<Grupo> i = getMisGrupoList().stream().iterator();
        while (i.hasNext()){
            Grupo g = i.next();

                if (g.getCountDownTimer().isPasswordCountDownTimerRunning()){
                   g.getCountDownTimer().cancel();


            }
            misGrupoPorId.remove(g.getIdGrupo());
            ToolsUtil.forceGarbageCollector(g);
        }
        misGrupoPorId.clear();
        ToolsUtil.forceGarbageCollector(TAG);
        ToolsUtil.forceGarbageCollector(grupoOnTop);
        ToolsUtil.forceGarbageCollector(misGrupoPorId);
        ToolsUtil.forceGarbageCollector(instance);

    }

    private void avisar(Grupo grupoDTO) {
            for(ObservadoresGrupos e : o) {
                e.nuevoGrupo(grupoDTO);
            }

    }

    public Set<Grupo> getMisGrupoList(){
            return new HashSet<>(this.misGrupoPorId.values());
    }

    public void GrupoRemove(String idGrupo) {
        if (misGrupoPorId.get(idGrupo) != null){
            if (misGrupoPorId.get(idGrupo).getCountDownTimer().isPasswordCountDownTimerRunning()){
                misGrupoPorId.get(idGrupo).getCountDownTimer().cancel();
            }
        }
        misGrupoPorId.remove(idGrupo);
        avisarGrupoRemove(idGrupo);
    }

    public void avisarGrupoRemove(String idGrupo) {
        for(ObservadoresGrupos e : o) {
            e.removeGrupo(idGrupo);
        }
    }

    public void updateGrupo(GrupoDTO grupoDTO) {
        misGrupoPorId.get(grupoDTO.getIdGrupo()).setDto(grupoDTO);

    }

    public void updateGrupoAcceptInvitation(GrupoDTO l) {
        if (misGrupoPorId.get(l.getIdGrupo()) != null){
            Objects.requireNonNull(misGrupoPorId.get(l.getIdGrupo())).setDto(l);
        }else{
            misGrupoPorId.put(l.getIdGrupo(),new Grupo(l));
        }


        avisarActualizarLista();
    }
    /*
    public void addGrupoInvitation(Grupo l) {
        Grupo n = misGrupoPorId.get(l.getIdGrupo());
        n.setGrupoInvitationDTO(null);
        n.setUserConfDTO(l.getUserConfDTO());
        n.setUserForGrupoDTO(l.getUserForGrupoDTO());
        n.setGralConfDTO(l.getGralConfDTO());
        n.setAlias(l.getAlias());
        n.setNicknameForGrupo(l.getNicknameForGrupo());
        n.setName(l.getName());

        misGrupoPorId.remove(l.getIdGrupo());
        misGrupoPorId.put(l.getIdGrupo(), n );
        avisarActualizarLista();
    }
*/
    public void avisarActualizarLista(){
        for( ObservadoresGrupos e : o) {
            e.actualizarLista();
        }
    }
    public void avisarLock(Grupo grupo) {
        for( ObservadoresGrupos e : o) {
            e.avisarLock(grupo);
        }
    }

    public void avisarRoleChange(Grupo grupo) {
        for( ObservadoresGrupos e : o) {
            e.avisarRoleChange(grupo);
        }
    }

    public void updateOnline(Protocolo p) {
        GrupoDTO gs = UtilsStringSingleton.getInstance().gson().fromJson(p.getObjectDTO(), GrupoDTO.class);
        System.out.println(" " +gs.toString());

        if (misGrupoPorId.get(gs.getIdGrupo()) == null){

           misGrupoPorId.put(gs.getIdGrupo(),new Grupo(gs));
        }

        misGrupoPorId.get(gs.getIdGrupo()).getMembersQuantityDTO().setQuantityOnline(gs.getMembersQuantityDTO().getQuantityOnline());
        avisarActualizarLista();


    }

    public void resetOnLineMember() {
        misGrupoPorId.forEach((k,v) -> v.getMembersQuantityDTO().setQuantityOnline(0));
        avisarActualizarLista();
    }

    public void resetWriting() {

        misGrupoPorId.forEach((k,v) ->
                resetWriting(v)
        );
        avisarActualizarLista();
    }

    private void resetWriting (Grupo grupo){
        grupo.setOtherAreWritting(false);
        grupo.writtingCountDownTimerStop();
    }

    public void updateGrupoLock(SaveGrupoGralConfLockResponseDTO r) {
        if(misGrupoPorId.get(r.getIdGrupo()) == null){
            Log.e(TAG,"updateGrupoLock: " + "no deberia recibir un idGrupo inexistente");
        }
        Objects.requireNonNull(misGrupoPorId.get(r.getIdGrupo())).setLock(r.getLock());
        Objects.requireNonNull(misGrupoPorId.get(r.getIdGrupo())).setPassword(r.getPassword());
        avisarLock(misGrupoPorId.get(r.getIdGrupo()));
        if ( r.getLock().isEnabled()){
            if ( misGrupoPorId.get(r.getIdGrupo()).getCountDownTimer().isPasswordCountDownTimerRunning()){
                misGrupoPorId.get(r.getIdGrupo()).getCountDownTimer().restart();
            }

        }else{
            misGrupoPorId.get(r.getIdGrupo()).getCountDownTimer().cancel();

        }



    }
    public void avisarCambioGrupoGralConf(Grupo grupo) {
        for( ObservadoresGrupos e : o) {
            e.avisarCambioGrupoGralConf(grupo);
        }
    }
    public void updateGrupoGralConf(GrupoGralConfDTO sgglr) {
        misGrupoPorId.get(sgglr.getIdGrupo()).setGralConfDTO(sgglr);
        avisarCambioGrupoGralConf(misGrupoPorId.get(sgglr.getIdGrupo()));
    }

    public void updateGrupoUserRole(GrupoChangeUserRoleDTO sgglr) {
        misGrupoPorId.get(sgglr.getIdGrupo()).getUserForGrupoDTO().setRole(sgglr.getRole());
        avisarRoleChange(misGrupoPorId.get(sgglr.getIdGrupo()));
    }
}
