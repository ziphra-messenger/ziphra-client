package com.privacity.cliente.frame.help;

public class HelpFrame {
    private HelpView view;

    public HelpFrame() {
        view = new HelpView();
    }

    public void show(String s){
        view.show(s);
    }

}
