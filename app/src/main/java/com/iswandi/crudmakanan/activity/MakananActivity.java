package com.iswandi.crudmakanan.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.iswandi.crudmakanan.R;
import com.iswandi.crudmakanan.helper.SessionManager;
import com.iswandi.crudmakanan.model.DataKategori;
import com.iswandi.crudmakanan.model.ModelKategori;
import com.iswandi.crudmakanan.model.ModelUser;
import com.iswandi.crudmakanan.network.RestAPI;
import com.iswandi.crudmakanan.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakananActivity extends SessionManager {

    @BindView(R.id.spincarimakanan)
    Spinner spincarimakanan;
    @BindView(R.id.listmakanan)
    RecyclerView listmakanan;
    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshlayout;
    List<DataKategori>  listdatakategori;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makanan);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //method unntuk menampilkan kategori makanan di spinner
        getdatakategori();
    }

    private void getdatakategori() {
        final ProgressDialog dialog =ProgressDialog.show(MakananActivity.this,"proses register user","loading....");
        RestAPI api = RetrofitClient.getInstaceRetrofit();
        Call<ModelKategori> m =api.getkategorimakanan();
        m.enqueue(new Callback<ModelKategori>() {
            @Override
            public void onResponse(Call<ModelKategori> call, Response<ModelKategori> response) {
                if (response.isSuccessful()){
                dialog.dismiss();
                    listdatakategori= new ArrayList<DataKategori>();
                listdatakategori =response.body().getDataKategori();
                String[] idmakanan =new String[listdatakategori.size()];
                String[] namamakanan =new String[listdatakategori.size()];
                for (int i =0;i<listdatakategori.size();i++){
                    idmakanan[i] =listdatakategori.get(i).getIdKategori();
                    namamakanan[i] =listdatakategori.get(i).getNamaKategori();
                }
                    ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, namamakanan);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spincarimakanan.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<ModelKategori> call, Throwable t) {

            }
        });

    }

}
