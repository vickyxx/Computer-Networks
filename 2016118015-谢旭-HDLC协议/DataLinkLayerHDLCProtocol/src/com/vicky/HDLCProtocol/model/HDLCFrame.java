package com.vicky.HDLCProtocol.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HDLCFrame {
	
	private StringProperty frameId;
	private StringProperty frameState;
	private final StringProperty START = new SimpleStringProperty("01111110");
	private StringProperty address;
	private StringProperty control;
	private StringProperty information;
	private StringProperty backupInfo;
	private StringProperty fcs;
	private final StringProperty END = new SimpleStringProperty("01111110");
	public static int frameSize = 16; //设置信息帧长度默认为16位（最小16位）
	
	public HDLCFrame () {
		this.frameId = new SimpleStringProperty();
		this.frameState = new SimpleStringProperty();
		this.address = new SimpleStringProperty();
		this.control = new SimpleStringProperty();
		this.information = new SimpleStringProperty();
		this.backupInfo = new SimpleStringProperty();
		this.fcs = new SimpleStringProperty();
	}
	
	public HDLCFrame(String frameId,String frameState,String address,String control,String information,String fcs) {
		this.frameId = new SimpleStringProperty(frameId);
		this.frameState = new SimpleStringProperty(frameState);
		this.address = new SimpleStringProperty(address);
		this.control = new SimpleStringProperty(control);
		this.information = new SimpleStringProperty(information);
		this.backupInfo = new SimpleStringProperty(information);
		this.fcs = new SimpleStringProperty(fcs);
	}
	
	public StringProperty frameIdProperty() {
		return this.frameId;
	}
	

	public String getFrameId() {
		return this.frameIdProperty().get();
	}
	

	public void setFrameId(String id) {
		this.frameIdProperty().set(id);
	}
	
	public String getFrameState() {
		return frameStateProperty().get();
	}
	
	public void setFrameState(String frameState) {
		this.frameStateProperty().set(frameState);
	}
	
	public StringProperty frameStateProperty() {
		return this.frameState;
	}
	
	public String getStart() {
		return STARTProperty().get();
	}
	
	public StringProperty STARTProperty() {
		return START;
	}
	
	public String getAddress() {
		return addressProperty().get();
	}
	
	public void setAddress(String address) {
		this.addressProperty().set(address);
	}
	
	public StringProperty addressProperty() {
		return address;
	}
	
	public String getControl() {
		return controlProperty().get();
	}
	
	public void setControl(String control) {
		this.controlProperty().set(control);
	}
	
	public StringProperty controlProperty() {
		return control;
	}
	
	public String getInformation() {
		return informationProperty().get();
	}
	
	public void setInformation(String information) {
		this.informationProperty().set(information);
	}
	
	public StringProperty informationProperty() {
		return information;
	}
	
	public String getFcs() {
		return fcsProperty().get();
	}
	
	public void setFcs(String fcs) {
		this.fcsProperty().set(fcs);
	}
	
	public StringProperty fcsProperty() {
		return fcs;
	}
	
	public String getEND() {
		return ENDProperty().get();
	}
	
	public StringProperty ENDProperty() {
		return END;
	}
	
	public String getBackupInfo() {
		return backupInfoProperty().get();
	}
	
	public void setBackupInfo(String backupInfo) {
		this.backupInfoProperty().set(backupInfo);
	}
	
	public StringProperty backupInfoProperty() {
		return backupInfo;
	}
	
	public static int getFrameSize() {
		return frameSize;
	}

	public static void setFrameSize(int frameSize) {
		HDLCFrame.frameSize = frameSize;
	}
	

}
