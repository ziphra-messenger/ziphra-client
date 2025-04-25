package com.privacity.cliente.activity.message;

import android.widget.Button;

import com.privacity.cliente.R;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
@Data
@NoArgsConstructor
public class StateIcons {

    private Button isHideReadState;
    private Button isExtraEncrypt;
    private Button isBlack;
    private Button isRandomNickname;
    private Button isAnonimo;
    private Button isResend;
    private Button isBlockDownload;
    private Button isTime;
    private Button isAudioMessage;
    private Button isBlockResend;

}
