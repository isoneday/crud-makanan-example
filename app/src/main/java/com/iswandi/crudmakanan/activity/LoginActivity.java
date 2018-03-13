package com.iswandi.crudmakanan.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.iswandi.crudmakanan.R;
import com.iswandi.crudmakanan.helper.SessionManager;
import com.iswandi.crudmakanan.model.ModelUser;
import com.iswandi.crudmakanan.network.RestAPI;
import com.iswandi.crudmakanan.network.RetrofitClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SessionManager {

    @BindView(R.id.regUsername)
    EditText regUsername;
    @BindView(R.id.regPass)
    EditText regPass;
    @BindView(R.id.regAdmin)
    RadioButton regAdmin;
    @BindView(R.id.regUserbiasa)
    RadioButton regUserbiasa;
    @BindView(R.id.regBtnLogin)
    Button regBtnLogin;
    @BindView(R.id.regBtnRegister)
    Button regBtnRegister;
String strusername,strpassword,strlevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (regAdmin.isChecked()) {
            strlevel = "admin";
        } else {
            strlevel = "user biasa";
        }
    }

    @OnClick({R.id.regAdmin, R.id.regUserbiasa, R.id.regBtnLogin, R.id.regBtnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.regAdmin:
                strlevel = "admin";

                break;
            case R.id.regUserbiasa:
                strlevel = "user biasa";

                break;
            case R.id.regBtnLogin:
                strusername =regUsername.getText().toString();
                strpassword =regPass.getText().toString();
                if(TextUtils.isEmpty(strusername)){
                    regUsername.requestFocus();
                    myanimation(regUsername);
                    regUsername.setError("username tidak boleh kosong");
                }else if (TextUtils.isEmpty(strpassword)){
                    regPass.requestFocus();
                    myanimation(regPass);
                    regPass.setError("password tidak boleh kosong");
                }else{
                    loginuser();
                }
                break;
            case R.id.regBtnRegister:
                myIntent(RegisterActivity.class);
                break;
        }
    }

    private void loginuser() {
        final ProgressDialog dialog =ProgressDialog.show(LoginActivity.this,"proses register user","loading....");
        RestAPI api = RetrofitClient.getInstaceRetrofit();
        Call<ModelUser> m =api.loginuser(strusername,strpassword,strlevel);
        m.enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                    String status = response.body().getResult();
                    String pesan =response.body().getMsg();
                    if (status.equals("1")){
                        myToast(pesan);
                        myIntent(MakananActivity.class);
                        finish();
                        sessionManager.createSession(strusername);
                        String iduser=response.body().getUser().getIdUser();
                        sessionManager.setIdUser(iduser);
                    }else{
                        myToast(pesan);
                    }

                }
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable t) {
            myToast("gagal koneksi"+t.getMessage());
            dialog.dismiss();
            }
        });

    }
}
