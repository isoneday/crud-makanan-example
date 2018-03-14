package com.iswandi.crudmakanan.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.iswandi.crudmakanan.R;
import com.iswandi.crudmakanan.adapter.ListMakananAdapter;
import com.iswandi.crudmakanan.helper.MyConstant;
import com.iswandi.crudmakanan.helper.SessionManager;
import com.iswandi.crudmakanan.model.DataKategori;
import com.iswandi.crudmakanan.model.DataMakananItem;
import com.iswandi.crudmakanan.model.ModelKategori;
import com.iswandi.crudmakanan.model.ModelMakanan;
import com.iswandi.crudmakanan.network.RestAPI;
import com.iswandi.crudmakanan.network.RetrofitClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iswandi.crudmakanan.helper.MyConstant.STORAGE_PERMISSION_CODE;

public class MakananActivity extends SessionManager implements ListMakananAdapter.OnItemClicked, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.spincarimakanan)
    Spinner spincarimakanan;
    @BindView(R.id.listmakanan)
    RecyclerView listmakanan;
    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshlayout;
    List<DataKategori>  listdatakategori;
    List<DataMakananItem>  listdatamakanan;
    String strkategorimakanan,striduser;
    Dialog dialog;
    TextInputEditText edtnamamakanan;
    EditText edtidmakanan;
    Button btnuploadmakanan, btninsert, btnreset, btnupdate, btndelete;
    ImageView imgpreview;
    Spinner spinnercarikategori;
    String strnamamakan;

    Bitmap bitmap;
    Uri filepath;
    private String strpath;
    private String strtime;
    private Dialog dialog2;
    private Spinner spincariupdatekategori;
    private Target mTarget;
    private String stridmakanan;
    private String strpath1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makanan);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        listmakanan.setLayoutManager(new LinearLayoutManager(c));
        requeststoragepermission();
        refreshlayout.setOnRefreshListener(this);
//        RecyclerView.LayoutManager manager = new LinearLayoutManager(c);
//        listmakanan.setLayoutManager(manager);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(MakananActivity.this);

                dialog.setContentView(R.layout.tambahmakanan);
                dialog.setTitle("data makanan");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                //inisialisasi
                edtnamamakanan = (TextInputEditText) dialog.findViewById(R.id.edtnamamakanan);
                btnuploadmakanan = (Button) dialog.findViewById(R.id.btnuploadmakanan);
                imgpreview = (ImageView) dialog.findViewById(R.id.imgupload);
                btninsert = (Button) dialog.findViewById(R.id.btninsert);
                btnreset = (Button) dialog.findViewById(R.id.btnreset);
                spinnercarikategori = (Spinner) dialog.findViewById(R.id.spincarikategori);
                //aksi

                btnuploadmakanan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showfilechooser(MyConstant.REQ_FILE_CHOOSE);
                    }
                });
                getdatakategori(spinnercarikategori);

                btninsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        strnamamakan = edtnamamakanan.getText().toString();
                        if (TextUtils.isEmpty(strnamamakan)) {
                            edtnamamakanan.setError("nama makanan tidak boleh kosong");
                            edtnamamakanan.requestFocus();
                            myanimation(edtnamamakanan);
                        } else if (imgpreview.getDrawable() == null) {
                            myToast("gambar harus dipilih");
                        } else {
                            insertdatamakanan(strkategorimakanan);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();

            }
        });
        //method unntuk menampilkan kategori makanan di spinner
        getdatakategori(spincarimakanan);
    }

    private void insertdatamakanan(String strkategorimakanan) {
        //mengambil path dari gmbar yang d i upload
        try {
            strpath = getPath(filepath);
            striduser = sessionManager.getIdUser();
//            MaxSizeImage(strpath);

        } catch (Exception e) {
            myToast("gambar terlalu besar \n silahkan pilih gambar yang lebih kecil");
            e.printStackTrace();
        }
        /**
         * Sets the maximum time to wait in milliseconds between two upload attempts.
         * This is useful because every time an upload fails, the wait time gets multiplied by
         * {@link UploadService#BACKOFF_MULTIPLIER} and it's not convenient that the value grows
         * indefinitely.
         */
        strtime = currentDate();
        try {
            new MultipartUploadRequest(c, MyConstant.UPLOAD_URL)
                    .addFileToUpload(strpath, "image")
                    .addParameter("vsiduser", striduser)
                    .addParameter("vsnamamakanan", strnamamakan)
                    .addParameter("vstimeinsert", strtime)
                    .addParameter("vskategori", strkategorimakanan)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

            getDataMakanan(strkategorimakanan);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            myToast(e.getMessage());
        } catch (FileNotFoundException e) {
            myToast(e.getMessage());
            e.printStackTrace();
        }
    }

    private String getPath(Uri filepath) {
        Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void showfilechooser(int reqFileChoose) {
        Intent intentgalery = new Intent();
        intentgalery.setType("image/*");
        intentgalery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentgalery, "select Pictures"), reqFileChoose);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstant.REQ_FILE_CHOOSE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filepath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imgpreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    private void requeststoragepermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                myToast("Permission granted now you can read the storage");
            } else {
                //Displaying another toast if permission is not granted
                myToast("Oops you just denied the permission");
            }
        }
    }

    private void getdatakategori(final Spinner namaspinner) {
       // final ProgressDialog dialog =ProgressDialog.show(MakananActivity.this,"proses getdata user","loading....");
        RestAPI api = RetrofitClient.getInstaceRetrofit();
        Call<ModelKategori> m =api.getkategorimakanan();
        m.enqueue(new Callback<ModelKategori>() {
            @Override
            public void onResponse(Call<ModelKategori> call, Response<ModelKategori> response) {
                if (response.isSuccessful()){
            //    dialog.dismiss();
                    listdatakategori= new ArrayList<DataKategori>();
                listdatakategori =response.body().getDataKategori();
                final String[] idmakanan =new String[listdatakategori.size()];
                String[] namamakanan =new String[listdatakategori.size()];
                for (int i =0;i<listdatakategori.size();i++){
                    idmakanan[i] =listdatakategori.get(i).getIdKategori();
                    namamakanan[i] =listdatakategori.get(i).getNamaKategori();
                }
                    ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_spinner_item, namamakanan);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    namaspinner.setAdapter(adapter);
                    namaspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //cara 1 untuk menampilakan brdasarkan nama
                            strkategorimakanan = idmakanan[0].toString();
                            strkategorimakanan = parent.getItemAtPosition(position).toString();

                            getDataMakanan(strkategorimakanan);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<ModelKategori> call, Throwable t) {

            }
        });

    }

    private void getDataMakanan(String strkategorimakanan) {
        final ProgressDialog dialog = ProgressDialog.show(MakananActivity.this, "process register user", "harap bersabar");
        String iduser = sessionManager.getIdUser();
        RestAPI api = RetrofitClient.getInstaceRetrofit();
        Call<ModelMakanan> modelUserCall = api.getdatamakanan(
                iduser, strkategorimakanan);
        myToast("iduser" + iduser);
        modelUserCall.enqueue(new Callback<ModelMakanan>() {
            @Override
            public void onResponse(Call<ModelMakanan> call, Response<ModelMakanan> response) {
                dialog.dismiss();
                //hideProgressDialog();
                listdatamakanan=new ArrayList<>();
                listdatamakanan = response.body().getDataMakanan();
                String[] id_makanan = new String[listdatamakanan.size()];
                String[] namamakanan = new String[listdatamakanan.size()];
                String[] fotomakanan = new String[listdatamakanan.size()];
                for (int i = 0; i < listdatamakanan.size(); i++) {
                    namamakanan[i] = listdatamakanan.get(i).getMakanan().toString();
                    fotomakanan[i] = listdatamakanan.get(i).getFotoMakanan().toString();
                    id_makanan[i] = listdatamakanan.get(i).getIdMakanan().toString();
                    striduser = id_makanan[i];
                }
                ListMakananAdapter adapter = new ListMakananAdapter(c, listdatamakanan);
                listmakanan.setAdapter(adapter);
               adapter.setOnClick(MakananActivity.this);
            }

            @Override
            public void onFailure(Call<ModelMakanan> call, Throwable t) {
                dialog.dismiss();
                myToast(t.getMessage());
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        dialog2 = new Dialog(MakananActivity.this);
        dialog2.setTitle("Update data makanan");
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setContentView(R.layout.updatemakanan);
        dialog2.show();
        //inisialisasi
        edtnamamakanan = (TextInputEditText) dialog2.findViewById(R.id.edtnamamakanan);
        edtidmakanan = (EditText) dialog2.findViewById(R.id.edtidmakanan);
        btnuploadmakanan = (Button) dialog2.findViewById(R.id.btnuploadmakanan);
        imgpreview = (ImageView) dialog2.findViewById(R.id.imgupload);
        btnupdate = (Button) dialog2.findViewById(R.id.btnupdate);
        btndelete = (Button) dialog2.findViewById(R.id.btndelete);
        spincariupdatekategori = (Spinner) dialog2.findViewById(R.id.spincarikategori);

        mTarget = new Target() {
            @Override
            public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from){
                //Do something
//            ...

                imgpreview.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        Picasso.with(c)
                .load(MyConstant.IMAGE_URL+listdatamakanan.get(position).getFotoMakanan().toString())
                .into(mTarget);
        //  imgpreview.setImageBitmap();

        getdatakategori(spincariupdatekategori);
        //isidata
        edtnamamakanan.setText(listdatamakanan.get(position).getMakanan());
        edtidmakanan.setText(listdatamakanan.get(position).getIdMakanan());
        btnuploadmakanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showfilechooser(MyConstant.REQ_FILE_CHOOSE);
            }
        });
        spincariupdatekategori.setPrompt(listdatakategori.get(position).getNamaKategori());
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stridmakanan = edtidmakanan.getText().toString();
                hapusdatamakanan();
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    strpath1 = getPath(filepath);
                    striduser = sessionManager.getIdUser();

                } catch (Exception e) {
//                    myToast("gambar terlalu besar \n silahkan pilih gambar yang lebih kecil");
                    e.printStackTrace();
                }

                strnamamakan = edtnamamakanan.getText().toString();
                stridmakanan = edtidmakanan.getText().toString();
                if (TextUtils.isEmpty(strnamamakan)) {
                    edtnamamakanan.setError("nama makanan tidak boleh kosong");
                    edtnamamakanan.requestFocus();
                    myanimation(edtnamamakanan);
                } else if (imgpreview.getDrawable() == null) {
                    myToast("gambar harus dipilih");
                }
                else {
                    /**
                     * Sets the maximum time to wait in milliseconds between two upload attempts.
                     * This is useful because every time an upload fails, the wait time gets multiplied by
                     * {@link UploadService#BACKOFF_MULTIPLIER} and it's not convenient that the value grows
                     * indefinitely.
                     */

                    try {
                        new MultipartUploadRequest(c, MyConstant.UPLOAD_UPDATE_URL)
                                .addFileToUpload(strpath1, "image")
                                .addParameter("vsidmakanan", stridmakanan)
                                .addParameter("vsnamamakanan", strnamamakan)
                                .addParameter("vsidkategori", strkategorimakanan)
                                .setNotificationConfig(new UploadNotificationConfig())
                                .setMaxRetries(2)

                                .startUpload();


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        myToast(e.getMessage());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        myToast(e.getMessage());
                    }

                    dialog2.dismiss();
                }
            }


        });

    }

    private void hapusdatamakanan() {
        RestAPI api = RetrofitClient.getInstaceRetrofit();
        Call<ModelMakanan> result = api.deletemakanan(stridmakanan);
        result.enqueue(new Callback<ModelMakanan>() {
            @Override
            public void onResponse(Call<ModelMakanan> call, Response<ModelMakanan> response) {
                String result = response.body().getResult();
                String msg = response.body().getMsg();
                if (result.equals("1")) {
                    myToast(msg);
                    dialog2.dismiss();
                    getDataMakanan(strkategorimakanan);
                } else {
                    myToast(msg);
                    dialog2.setCancelable(false);
                }
            }

            @Override
            public void onFailure(Call<ModelMakanan> call, Throwable t) {
                myToast("kesalahan koneksi data" + t.getMessage());
                dialog2.setCancelable(false);

            }
        });

    }

    @Override
    public void onRefresh() {
        getDataMakanan(strkategorimakanan);
        refreshlayout.setRefreshing(false);
    }
}
