package com.privacity.cliente.singleton.observers;

import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.singleton.interfaces.SingletonReset;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.response.SaveGrupoGralConfLockResponseDTO;
import com.privacity.common.enumeration.GrupoRolesEnum;
import com.privacity.common.enumeration.MessageState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ObserverGrupo implements SingletonReset {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<ObservadoresGrupos> o = new ArrayList<ObservadoresGrupos>();

    private boolean grupoOnTop=false;

    private LinkedHashMap<String, Grupo> misGrupoPorId = new LinkedHashMap<String, Grupo>();

    //private Map<String, GrupoUserConfDTO> grupoUserConfPorId = new HashMap<String, GrupoUserConfDTO>();

    public boolean amIReadOnly(String idGrupo){
        Grupo g = this.getGrupoById(idGrupo);
        boolean r = true;
            if ( !g.getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.READONLY)){
                r=false;
        }
        return r;
    }

    public GrupoRolesEnum whichIsMyRole(String idGrupo){
        Grupo g = this.getGrupoById(idGrupo);
        return g.getUserForGrupoDTO().getRole();
    }

    public boolean amIAdmin(String idGrupo){
        Grupo g = this.getGrupoById(idGrupo);
        boolean r = true;
        if ( !g.getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.ADMIN)){
            r=false;
        }
        return r;
    }





    public Grupo getGrupoById(String idGrupo){

        return this.misGrupoPorId.get(idGrupo);
    }
    public void avisarCambioUnread(MessageDetailDTO m){
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


        private static ObserverGrupo instance;
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
    public void addGrupos(ProtocoloDTO protocoloDTO) {
        Grupo[] gs = GsonFormated.get().fromJson(protocoloDTO.getObjectDTO(), Grupo[].class);

        for (int i = 0 ; i < gs.length ; i++){
            addGrupo(gs[i],true);
        }
    }
    public void addGrupo(ProtocoloDTO protocoloDTO) {
        addGrupo(GsonFormated.get().fromJson(protocoloDTO.getObjectDTO(), Grupo.class),true);
    }

    public void addGrupo(Grupo grupo, boolean index0) {
        System.out.println("*********************************************");
        System.out.println(GsonFormated.get().toJson(grupo));
        if ( misGrupoPorId.containsKey(grupo.getIdGrupo())){
            System.out.println("VIEJO GRUPO");
            System.out.println(GsonFormated.get().toJson(misGrupoPorId.get(grupo.getIdGrupo())));
            //viejoGrupo =misGrupoPorId.get(grupo.getIdGrupo());
        }else{
            misGrupoPorId.put(grupo.getIdGrupo(), grupo);
        }
        System.out.println("*********************************************");
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
            misGrupoPorId.remove(g.idGrupo);
        }
        instance = null;
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

    public void updateGrupo(Grupo grupoDTO) {
        misGrupoPorId.put(grupoDTO.getIdGrupo(), grupoDTO);
    }

    public void updateGrupoAcceptInvitation(Grupo l) {
        Grupo grupoActual = misGrupoPorId.get(l.getIdGrupo());
        grupoActual.setGrupoInvitationDTO(null);
        grupoActual.setUserConfDTO(l.getUserConfDTO());

        grupoActual.setUserForGrupoDTO(l.getUserForGrupoDTO());
        grupoActual.setGralConfDTO(l.getGralConfDTO());
        grupoActual.setAlias(l.getAlias());
        //grupoActual.setNicknameForGrupo(l.getNicknameForGrupo());
        grupoActual.setName(l.getName());

        grupoActual.setLock(l.getLock());
        grupoActual.setPassword(l.getPassword());

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
    public void avisarLock(Grupo grupoDTO) {
        for( ObservadoresGrupos e : o) {
            e.avisarLock(grupoDTO);
        }
    }
    public void updateOnline(ProtocoloDTO p) {
        Grupo gs = GsonFormated.get().fromJson(p.getObjectDTO(), Grupo.class);
        System.out.println("update -> " +gs.toString());

        if (misGrupoPorId.get(gs.getIdGrupo()) == null){
            misGrupoPorId.put(gs.getIdGrupo(),gs);
        }
        misGrupoPorId.get(gs.getIdGrupo()).setMembersOnLine(gs.getMembersOnLine());
        avisarActualizarLista();


    }

    public void resetOnLineMember() {
        misGrupoPorId.forEach((k,v) -> v.setMembersOnLine(0));
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
        misGrupoPorId.get(r.getIdGrupo()).setLock(r.getLock());
        misGrupoPorId.get(r.getIdGrupo()).setPassword(r.getPassword());
        avisarLock(misGrupoPorId.get(r.getIdGrupo()));
        if ( r.lock.isEnabled()){
            if ( misGrupoPorId.get(r.getIdGrupo()).getCountDownTimer().isPasswordCountDownTimerRunning()){
                misGrupoPorId.get(r.getIdGrupo()).getCountDownTimer().restart();
            }

        }else{
            misGrupoPorId.get(r.getIdGrupo()).getCountDownTimer().cancel();

        }



    }
}
