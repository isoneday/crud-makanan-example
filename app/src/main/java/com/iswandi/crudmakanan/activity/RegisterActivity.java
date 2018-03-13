package com.iswandi.crudmakanan.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

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


public class RegisterActivity extends SessionManager {

    @BindView(R.id.edtnama)
    EditText edtnama;
    @BindView(R.id.edtalamat)
    EditText edtalamat;
    @BindView(R.id.edtnotelp)
    EditText edtnotelp;
    @BindView(R.id.spinjenkel)
    Spinner spinjenkel;
    @BindView(R.id.edtusername)
    EditText edtusername;
    @BindView(R.id.edtpassword)
    TextInputEditText edtpassword;
    @BindView(R.id.edtpasswordconfirm)
    TextInputEditText edtpasswordconfirm;
    @BindView(R.id.regAdmin)
    RadioButton regAdmin;
    @BindView(R.id.regUserbiasa)
    RadioButton regUserbiasa;
    @BindView(R.id.btnregister)
    Button btnregister;
    String jenkel[]={"laki-laki","perempuan"};

    String strnama,stralamat,strnotlp,strusername,strpassword,strlevel,strconpass,strjenkel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        if (regAdmin.isChecked()) {
            strlevel = "admin";
        } else {
            strlevel = "user biasa";
        }
        //mengisi spinner jenis kelamin
        ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, jenkel);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinjenkel.setAdapter(adapter);
        spinjenkel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strjenkel = jenkel[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }




    @OnClick({R.id.regAdmin, R.id.regUserbiasa, R.id.btnregister})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.regAdmin:
                strlevel ="admin";
                break;
            case R.id.regUserbiasa:
                strlevel ="user biasa";
                break;
            case R.id.btnregister:
                strnama =edtnama.getText().toString();
                stralamat =edtalamat.getText().toString();
                strnotlp =edtnotelp.getText().toString();
                strusername =edtusername.getText().toString();
                strpassword =edtpassword.getText().toString();
                strconpass =edtpasswordconfirm.getText().toString();
                if (TextUtils.isEmpty(strnama)){
                    edtnama.setError("nama tidak boleh kosong");
                    edtnama.requestFocus();
                    myanimation(edtnama);
                }else if(TextUtils.isEmpty(stralamat)){
                    edtalamat.requestFocus();
                    edtalamat.setError("alamt tidak boleh kosong");
                    myanimation(edtalamat);
                }else if (TextUtils.isEmpty(strnotlp)){
                    edtnotelp.requestFocus();
                    myanimation(edtnotelp);
                    edtnotelp.setError("no hp tidak boleh kosong");
                }else if(TextUtils.isEmpty(strusername)){
                    edtusername.requestFocus();
                    myanimation(edtusername);
                    edtusername.setError("username tidak boleh kosong");
                }else if (TextUtils.isEmpty(strpassword)){
                    edtpassword.requestFocus();
                    myanimation(edtpassword);
                    edtpassword.setError("password tidak boleh kosong");
                }else if (strpassword.length()<6){
                    myanimation(edtpassword);
                    edtpassword.setError("password minimal 6 karakter");
                }else if (TextUtils.isEmpty(strconpass)){
                    edtpasswordconfirm.requestFocus();
                    myanimation(edtpasswordconfirm);
                    edtpasswordconfirm.setError("password confirm tidak boleh kosong");
                }else if (!strpassword.equals(strconpass)){
                    edtpasswordconfirm.requestFocus();
                    myanimation(edtpasswordconfirm);
                    edtpasswordconfirm.setError("password tidak sama");
                }else{
                    registeruser();
                }
                break;
        }
    }

    private void registeruser() {
         final ProgressDialog dialog =ProgressDialog.show(RegisterActivity.this,"proses register user","loading....");
        RestAPI api = RetrofitClient.getInstaceRetrofit();
        Call<ModelUser> ModelLoginCall =api.registerUser(
                strnama,stralamat,strnotlp,strjenkel,strusername,strpassword,strlevel);
        ModelLoginCall.enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                if (response.isSuccessful()){
                    dialog.dismiss();
                    String status = response.body().getResult();
                    String pesan =response.body().getMsg();
                    if (status.equals("1")){
                        myToast(pesan);
                        myIntent(LoginActivity.class);
                        finish();
                    }else{
                        myToast(pesan);
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable t) {
            myToast("gagal koneksi");
            dialog.dismiss();
            }
        });

    }

}
