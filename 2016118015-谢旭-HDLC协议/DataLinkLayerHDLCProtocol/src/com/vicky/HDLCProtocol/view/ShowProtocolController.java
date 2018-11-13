package com.vicky.HDLCProtocol.view;

import com.vicky.HDLCProtocol.model.HDLCFrame;
import com.vicky.HDLCProtocol.util.CreateDataLink;
import com.vicky.HDLCProtocol.util.DivideFrame;
import com.vicky.HDLCProtocol.util.ReleaseDataLink;
import com.vicky.HDLCProtocol.util.TransferFrame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class ShowProtocolController {
	
	@FXML
	private TableView<HDLCFrame> frameTable;
	@FXML
	private TableColumn<HDLCFrame, String> frameIdColumn;
	@FXML
	private TableColumn<HDLCFrame, String> frameStateColumn;
	@FXML
	private TableColumn<HDLCFrame, String> startColumn;
	@FXML
	private TableColumn<HDLCFrame, String> addressColumn;
	@FXML
	private TableColumn<HDLCFrame, String> controlColumn;
	@FXML
	private TableColumn<HDLCFrame, String> informationColumn;
	@FXML
	private TableColumn<HDLCFrame, String> fcsColumn;
	@FXML
	private TableColumn<HDLCFrame, String> endColumn;
	@FXML
	public TextField inputDataField;
	@FXML
	public TextArea resultTextArea;
	@FXML
	private Button frameButton;
	@FXML
	private Button nextButton;
	@FXML
	private TextField frameSizeField;

	//信息帧列表
	public ObservableList<HDLCFrame> frameInfo = FXCollections.observableArrayList();
	//标记传输步骤
	private int step = -1;
	//传输信息帧的次数
	public static int number = 0;
	
	private CreateDataLink createDataLink = new CreateDataLink(this);
	private TransferFrame transferFrame = new TransferFrame(this);
	private ReleaseDataLink releaseDataLink = new ReleaseDataLink(this);
	
	/**
	 * 初始化
	 */
	@FXML
	public void initialize(){
		// 把表中各列与Frame类各个属性对应，表与observableList对应
		frameTable.setItems(frameInfo);
		frameIdColumn.setCellValueFactory(cellData -> cellData.getValue().frameIdProperty());
		frameStateColumn.setCellValueFactory(cellData -> cellData.getValue().frameStateProperty());
		startColumn.setCellValueFactory(cellData -> cellData.getValue().STARTProperty());
		addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
		controlColumn.setCellValueFactory(cellData -> cellData.getValue().controlProperty());
		informationColumn.setCellValueFactory(cellData -> cellData.getValue().informationProperty());
		fcsColumn.setCellValueFactory(cellData -> cellData.getValue().fcsProperty());
		endColumn.setCellValueFactory(cellData -> cellData.getValue().ENDProperty());	
	}
	
	@FXML
	private void checkInputTexField() {
		String inputInformation = inputDataField.getText();
		int len = inputInformation.length();
		// 输入为空
		if (len == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("输入不能为空！");
			alert.show();
			return;
		}

		// 检查文本框中是否各位都为0或1
		boolean sign = true;
		for (int i = 0; i < len; i++) {
			char c = inputInformation.charAt(i);
			if (!(c == '0' || c == '1')) {
				sign = false;
				break;
			}
		}

		// 有问题给出相应错误提示
		if (!sign) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("输入各位不能含有其他字符，只能为0/1");
			alert.show();
			return;
		}

		DivideFrame divideFrame = new DivideFrame(this,1);
		divideFrame.run();
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
	
	/**
	 * 设置帧的长度
	 */
	@FXML
	private void changeFrameSize() {
		String frameSize = frameSizeField.getText().trim();
		try {
			int inputFrameSize = Integer.parseInt(frameSize);
			if (!(inputFrameSize >= HDLCFrame.frameSize)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("帧的值不在范围内！(最少16位)");
				alert.show();
			} else {
				if (inputFrameSize == HDLCFrame.getFrameSize()) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setContentText("值与原值相等！");
					alert.show();
					return;
				}

				HDLCFrame.setFrameSize(inputFrameSize);

				// 修改完还应该根据是否有帧进行重新划分
				if (frameInfo.size() != 0) {
					DivideFrame divideFrame = new DivideFrame(this,2);
					divideFrame.run();
				}
			}

		} catch (NumberFormatException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("帧的大小必须为整数！");
			alert.show();
		}
	}
	
	@FXML
	private void nextStep() {
		if (step == -1) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("整个数据链路层传输已结束或还未开始！");
			alert.show();
		} else if (step == 0) {
			// 在已经分好帧的情况下进入建立数据链路阶段
			createDataLink = new CreateDataLink(this);
			createDataLink.run();
		} else if (step >= 1 && step <= frameInfo.size()) {
			// 数据链路已建立，进行帧传输
			transferFrame = new TransferFrame(this);
			transferFrame.setFrame(frameInfo.get(step - 1));
			transferFrame.run();
		} else if (step == frameInfo.size() + 1) {
			// 数据帧传输完毕，进入释放数据链路阶段
			releaseDataLink = new ReleaseDataLink(this);
			releaseDataLink.run();
		}
	}
	
	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
}
