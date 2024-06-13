package com.privacity.cliente.activity.errorconsole;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.userhelper.UserHelperPagesContant;

public class ErrorConsoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_console);

        Bundle bundle = getIntent().getExtras();
        String errores = bundle.getString(UserHelperPagesContant.CONSOLE_ERROR_INTENT);

        Button compBack = (Button)findViewById(R.id.error_console_back);
        TextView compConsole = (TextView) findViewById(R.id.error_console_console);

        compConsole.setText(errores);

        compBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorConsoleActivity.this.onBackPressed();
            }
        });


    }
}