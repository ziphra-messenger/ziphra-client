package com.privacity.cliente.util;

import android.view.View;
import android.widget.Button;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuAcordeonObject {

    private Button title;
    private View content;
}
