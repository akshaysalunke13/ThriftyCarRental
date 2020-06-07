package model;

import javafx.beans.property.SimpleStringProperty;

public class RecordBean {

	private final SimpleStringProperty KEY;
	private final SimpleStringProperty VALUE;

	//Constructor 
	public RecordBean(String KEY, String VALUE) {
		this.KEY = new SimpleStringProperty(KEY);
		this.VALUE = new SimpleStringProperty(VALUE);
	}

	public String getKEY() {
		return KEY.get();
	}

	public void setKEY(String KEY) {
		this.KEY.set(KEY);
	}

	public String getVALUE() {
		return VALUE.get();
	}

	public void setVALUE(String VALUE) {
		this.VALUE.set(VALUE);
	}

}
