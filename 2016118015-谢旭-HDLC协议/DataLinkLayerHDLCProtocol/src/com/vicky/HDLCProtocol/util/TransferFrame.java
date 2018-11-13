package com.vicky.HDLCProtocol.util;

import com.vicky.HDLCProtocol.model.HDLCFrame;
import com.vicky.HDLCProtocol.view.ShowProtocolController;

import javafx.application.Platform;

public class TransferFrame extends Thread{
	
	private ShowProtocolController showProtocolController;
	private HDLCFrame frame;
	
	public TransferFrame(ShowProtocolController showProtocolController){
		this.setShowProtocolController(showProtocolController);
	}
	
	@Override
	public void run() {
		if(ShowProtocolController.number == 0){
			Platform.runLater(() -> {
				showProtocolController.resultTextArea.appendText("发送帧：\n");
			});
		}
		ShowProtocolController.number++;
		
		//如果上一次传输错误，应该从备份属性恢复信息
		if(!frame.getBackupInfo().equals(frame.getInformation())){
			frame.setInformation(frame.getBackupInfo());
			frame.setFcs(showProtocolController.calcFCS(frame.getInformation(),"10011010"));
		}
		
		StringBuffer control = new StringBuffer(frame.getControl());
		control.setCharAt(4, '1');//P/F位置一
		frame.setControl(control.toString());
		frame.setFrameState("开始传输");
		
		Platform.runLater(() -> {
			showProtocolController.resultTextArea.appendText("    主站向从站发送第" + (showProtocolController.getStep() - 1)
					+ "帧(I,N(S)=" + (showProtocolController.getStep() - 1) + "，N(R)=0，P=1)，等待从站接收并返回确认帧\n");
		});
		
		//随机决定是否传输错误，传输错误则随机数据有几位传错和哪几位传错，进行相应提示
				boolean transferFalse = false;
				int x = (int)(Math.random() * 2);
				if(x == 1)
					transferFalse = true;
				String oldInformation = frame.getInformation();
				StringBuffer information = new StringBuffer(oldInformation);
				int len = information.length();
				if(transferFalse) {
					int wrongBitNum = (int) (Math.random() * Math.min(len, 5));
					for(int i = 0;i < wrongBitNum;i++) {
						int bit = (int) (Math.random() * len);
						char ch = information.charAt(bit);
						if(ch == '0'){
							ch = '1';
						}else{
							ch = '0';
						}	
						information.setCharAt(bit, ch);
					}
					String newFCS = showProtocolController.calcFCS(information.toString(), "10011010");
					frame.setInformation(information.toString());
					frame.setFcs(newFCS);
					frame.setFrameState("出错了");
					
					Platform.runLater(() -> {
						showProtocolController.resultTextArea.appendText("    传输出错，信息由" + oldInformation + "变成" + information
								+ ",从站发送NAK帧到主站，请主站重新发送！\n");
					});
					
				}else {
					frame.setFrameState("传输完成");
					Platform.runLater(() -> {
						showProtocolController.resultTextArea.appendText("    传输正确，从站发送ACK帧到主站！\n");
					});
					showProtocolController.setStep(showProtocolController.getStep() + 1);
				}				
	}
	

	public ShowProtocolController getShowProtocolController() {
		return showProtocolController;
	}

	public void setShowProtocolController(ShowProtocolController showProtocolController) {
		this.showProtocolController = showProtocolController;
	}

	public HDLCFrame getFrame() {
		return frame;
	}

	public void setFrame(HDLCFrame frame) {
		this.frame = frame;
	}
	

}
