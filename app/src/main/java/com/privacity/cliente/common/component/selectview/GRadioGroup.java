package com.privacity.cliente.common.component.selectview;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class GRadioGroup {
    RadioCallBack cb;
    List<RadioButton> radios = new ArrayList<RadioButton>();

    /**
     * Constructor, which allows you to pass number of RadioButton instances,
     * making a group.
     *
     * @param radios
     *            One RadioButton or more.
     */
    public GRadioGroup(RadioCallBack cb, RadioButton... radios) {
        super();
        this.cb=cb;
        for (RadioButton rb : radios) {
            this.radios.add(rb);
            rb.setOnClickListener(onClick);
        }
    }

    /**
     * Constructor, which allows you to pass number of RadioButtons
     * represented by resource IDs, making a group.
     *
     * @param activity
     *            Current View (or Activity) to which those RadioButtons
     *            belong.
     * @param radiosIDs
     *            One RadioButton or more.
     */
    public GRadioGroup(View activity, int... radiosIDs) {
        super();

        for (int radioButtonID : radiosIDs) {
            RadioButton rb = (RadioButton)activity.findViewById(radioButtonID);
            if (rb != null) {
                this.radios.add(rb);
                rb.setOnClickListener(onClick);
            }
        }
    }

    public RadioButton getSelected(){
        for (RadioButton rb : radios) {
            // now let's select currently clicked RadioButton
            if (rb.isChecked()) {
               return rb;

            }
        }
        return null;
    }
    /**
     * This occurs everytime when one of RadioButtons is clicked,
     * and deselects all others in the group.
     */
    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            // let's deselect all radios in group
            for (RadioButton rb : radios) {
                if(cb !=null)cb.action();
                // now let's select currently clicked RadioButton
                if (rb.equals(v)) {
                    ((RadioButton)rb).setChecked(true);

               }else {
                    ((RadioButton)rb).setChecked(false);
                }
            }



        }
    };

}