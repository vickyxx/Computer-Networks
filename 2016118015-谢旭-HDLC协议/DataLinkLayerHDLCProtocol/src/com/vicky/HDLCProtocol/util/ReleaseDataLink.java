package com.vicky.HDLCProtocol.util;

import com.vicky.HDLCProtocol.view.ShowProtocolController;

import javafx.application.Platform;

public class ReleaseDataLink extends Thread{
	
	private ShowProtocolController showProtocolController;

	public ReleaseDataLink(ShowProtocolController showProtocolController){
		this.setShowProtocolController(showProtocolController);
	}
	
	@Override
	public void run() {
		StringBuffer createDataLinkBuffer = new StringBuffer();
		Platform.runLater(() -> {
			showProtocolController.resultTextArea.appendText("\n");
			showProtocolController.resultTextArea.appendText("释放数据链路：\n");
			showProtocolController.resultTextArea.appendText("    主站发送拆链命令（U,DISC,P=1）\n");
		});
		
		//假定从站同意拆链
		createDataLinkBuffer.append("    从站同意释放数据链路连接，发送无编号确认帧向主站应答(U,UA,F=1)\n");
		Platform.runLater(() -> {
			showProtocolController.resultTextArea.appendText(createDataLinkBuffer.toString());
		});
		showProtocolController.setStep(-1);
	}
	
	public ShowProtocolController getShowProtocolController() {
		return showProtocolController;
	}

	public void setShowProtocolController(ShowProtocolController showProtocolController) {
		this.showProtocolController = showProtocolController;
	}
	
	
	

}
