package com.iswandi.crudmakanan.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
public class ModelKategori{

	@SerializedName("DataKategori")
	private List<DataKategori> dataKategori;

	public void setDataKategori(List<DataKategori> dataKategori){
		this.dataKategori = dataKategori;
	}

	public List<DataKategori> getDataKategori(){
		return dataKategori;
	}
}