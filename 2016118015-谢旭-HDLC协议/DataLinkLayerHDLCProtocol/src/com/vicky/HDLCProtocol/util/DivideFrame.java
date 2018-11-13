package com.vicky.HDLCProtocol.util;

import com.vicky.HDLCProtocol.view.ShowProtocolController;

import javafx.application.Platform;

import com.vicky.HDLCProtocol.model.HDLCFrame;;

public class DivideFrame extends Thread{
	
	private ShowProtocolController showProtocolController;
	private int flag;
	
	@Override
	public void run() {
		divideFrame(showProtocolController.inputDataField.getText());
		Platform.runLater(() -> {
			showProtocolController.resultTextArea.clear();
			if(flag == 1){
				showProtocolController.resultTextArea.setText("输入的数据可以划分为上表所示的数据帧。\n");
			}else if(flag == 2) {
				showProtocolController.resultTextArea.setText("已将数据重新划分为上表所示的数据帧。\n");
			}
			showProtocolController.setStep(0);
			ShowProtocolController.number = 0;
		});	
	}
	
	private void divideFrame(String inputInfo){
		
		int frameSize = HDLCFrame.getFrameSize();
		int len = inputInfo.length();
		//计算成帧的数目
		int num = (int) Math.ceil((len * 1.0) / frameSize);
		
		//生成一个随机的地址字段
		String address = "";
		if(showProtocolController.frameInfo.size() == 0) {
			for (int i = 0; i < 8; i++){
				int a = (int) (Math.random() * 2);
				address += a;
			}
		 }else {
				address = showProtocolController.frameInfo.get(0).getAddress();
			}
		showProtocolController.frameInfo.clear();
		
		for(int i = 1; i <= num; i++){
			HDLCFrame frame = new HDLCFrame();
			frame.setFrameId(String.valueOf(i - 1));
			frame.setFrameState("未发送");
			frame.setAddress(address);
			frame.setControl(calcControl((i - 1) % 8));
			int endIndex = i * frameSize;
			if(i == num ){
				endIndex = Math.min(len, endIndex);			
			}
			String information = inputInfo.substring(( i - 1) * frameSize,endIndex);
			frame.setInformation(information);
			frame.setBackupInfo(information);
			frame.setFcs(calcFCS(information, "10011010"));
			showProtocolController.frameInfo.add(frame);
		}
	}
	
	/**
	 * CRC校验过程
	 * @param fx
	 * @param gx
	 * @return
	 */
	public String calcFCS(String fx,String gx){
		//计算发送数据需要左移的位数（即fx后加的0）
		int num_0 = gx.length() - 1;
		for(int i = 1; i <= num_0; i++) {
			fx += "0";
		}
		//商
		StringBuilder qx = new StringBuilder();
		StringBuilder lastStatus = new StringBuilder(fx.substring(0,num_0));
		
		//模拟除法过程
		for(int i = num_0; i < fx.length(); i++) {
			lastStatus.append(fx.charAt(i));
			if(lastStatus.length() < gx.length()){
				qx.append(0);
			} else {
				qx.append(1);
				// t为异或后的结果
				StringBuilder t = new StringBuilder();
				for (int j = 0; j < gx.length(); j++){
					if (lastStatus.charAt(j) == gx.charAt(j)){
						t.append('0');
					}else{
						t.append('1');
					}		
				}
				// 找最后一位前导0并判断是否全为0，根据情况更新lastStatus
				int lastLeadZeroPosition = -1;
				boolean isAllZero = true;
				for (int j = 0; j < t.length(); j++){
					if (t.charAt(j) != '0') {
						lastLeadZeroPosition = j - 1;
						isAllZero = false;
						break;
					}
				}
				if (isAllZero){
					lastStatus.replace(0, lastStatus.length(), "");
				}else{
					lastStatus.replace(0, lastStatus.length(), t.substring(lastLeadZeroPosition + 1));
				}		
			}
		}	
		return String.format("%016d", Long.parseLong(lastStatus.toString()));
	}
	
	private String calcControl(int x) {
		String NS = String.format("%03d", Integer.parseInt(Integer.toBinaryString(x)));
		String informationFrameControl = "0" + NS + "0000";
		return informationFrameControl;
	}
	
	public DivideFrame(ShowProtocolController showProtocolController, int flag){
		this.showProtocolController = showProtocolController;
		this.flag = flag;
	}
	
	public ShowProtocolController getShowProtocolController() {
		return showProtocolController;
	}
	
	public void setShowProtocolController(ShowProtocolController showProtocolController) {
		this.showProtocolController = showProtocolController;
	}

}
