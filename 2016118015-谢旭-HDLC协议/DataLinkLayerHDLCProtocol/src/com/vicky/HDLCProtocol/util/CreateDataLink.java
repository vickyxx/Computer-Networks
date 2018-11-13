package com.vicky.HDLCProtocol.util;

import com.vicky.HDLCProtocol.view.ShowProtocolController;

import javafx.application.Platform;

public class CreateDataLink extends Thread{
	private ShowProtocolController showProtocolController;
	
	public CreateDataLink(ShowProtocolController showProtocolController){
		this.setShowProtocolController(showProtocolController);
	}
	
	@Override
	public void run() {
		StringBuffer  createDataLinkBuffer = new StringBuffer();
		Platform.runLater(() -> {
			showProtocolController.resultTextArea.appendText("建立数据链路：\n");
			showProtocolController.resultTextArea.appendText("    主站向从站请求建立数据链路连接（U,SNRM,P=1）\n");		
		});
		
		//假定从站同意建立连接
		createDataLinkBuffer.append("    从站同意建立数据链路连接（U,UA,F=1）\n");
		Platform.runLater(() -> {
			showProtocolController.resultTextArea.appendText(createDataLinkBuffer.toString());
		});
		showProtocolController.setStep(showProtocolController.getStep() + 1);
	}
	
	public ShowProtocolController getShowProtocolController() {
		return showProtocolController;
	}
	
	public void setShowProtocolController(ShowProtocolController showProtocolController) {
		this.showProtocolController = showProtocolController;
	}


}
