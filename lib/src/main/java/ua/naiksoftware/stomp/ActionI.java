package ua.naiksoftware.stomp;

public interface ActionI {

    public void actionSucess(String msg);

    public void actionFail (String msg);

    public void sendInfoMessage (String msg);

    public void isNotOnline ();

}
