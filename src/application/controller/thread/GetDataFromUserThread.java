package application.controller.thread;

import java.io.File;

import application.protocol.vo.ObjectVO;

public class GetDataFromUserThread {
	private static ObjectVO addFriendVo;
	private static ObjectVO getMessageVo;
	private static ObjectVO getComparePwVo;
	private static ObjectVO fileCheckVo;
	
	private static File tempFile;
	
	//친구 추가
	public static ObjectVO getAddFriendVo() {
		return addFriendVo;
	}

	public static void setAddFriendVo(ObjectVO addFriendVo) {
		GetDataFromUserThread.addFriendVo = addFriendVo;
	}

	//메세지
	public static ObjectVO getGetMessageVo() {
		return getMessageVo;
	}

	public static void setGetMessageVo(ObjectVO getMessageVo) {
		GetDataFromUserThread.getMessageVo = getMessageVo;
	}

	public static ObjectVO getGetComparePwVo() {
		return getComparePwVo;
	}

	public static void setGetComparePwVo(ObjectVO getComparePwVo) {
		GetDataFromUserThread.getComparePwVo = getComparePwVo;
	}

	public static ObjectVO getFileCheckVo() {
		return fileCheckVo;
	}

	public static void setFileCheckVo(ObjectVO fileCheckVo) {
		GetDataFromUserThread.fileCheckVo = fileCheckVo;
	}

	public static File getTempFile() {
		return tempFile;
	}

	public static void setTempFile(File tempFile) {
		GetDataFromUserThread.tempFile = tempFile;
	}
	
	
}
