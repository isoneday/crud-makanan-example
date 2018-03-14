package com.iswandi.crudmakanan.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ModelMakanan{

	@SerializedName("DataMakanan")
	private List<DataMakananItem> dataMakanan;

	@SerializedName("result")
	private String result;

	@SerializedName("msg")
	private String msg;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setDataMakanan(List<DataMakananItem> dataMakanan){
		this.dataMakanan = dataMakanan;
	}

	public List<DataMakananItem> getDataMakanan(){
		return dataMakanan;
	}
}