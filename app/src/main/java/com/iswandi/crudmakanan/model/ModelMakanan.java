package com.iswandi.crudmakanan.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ModelMakanan{

	@SerializedName("DataMakanan")
	private List<DataMakananItem> dataMakanan;

	public void setDataMakanan(List<DataMakananItem> dataMakanan){
		this.dataMakanan = dataMakanan;
	}

	public List<DataMakananItem> getDataMakanan(){
		return dataMakanan;
	}
}