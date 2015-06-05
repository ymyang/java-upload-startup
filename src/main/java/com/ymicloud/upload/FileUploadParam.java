package com.ymicloud.upload;


/**
 * 文件上传参数
 * 
 * @author yang
 * 
 */
public class FileUploadParam implements IBaseDTO {

	/**
	 * 文件类型<br>
	 * 分段上传支持：企业文件，个人文件，发送文件，发送语音
	 * {@link com.conlect.oatos.dto.status.FileType}<br>
	 */
	private String fileType;

	/**
	 * 文件夹id<br>
	 * 上传会议文件时为会议id
	 */
	private Long folderId;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 文件大小<br>
	 * 单位：byte
	 */
	private long fileSize;

	/**
	 * 文件MD5校验码
	 */
	private String fileMd5;


	/**
	 * 外链id（上传外链id时使用）
	 */
	private Long linkId;
	
	/**
	 * token,web上传时需要
	 */
	private String token;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 接收者id，发送文件时使用
	 */
	private Long receiverId;

	/**
	 * 语音时长<br>
	 * 单位：秒
	 */
	private Integer length;

	/**
	 * 忽略重名<br>
	 * 出现重名，则返回重名的文件夹信息
	 */
	private boolean ignoreSame;

	public FileUploadParam() {
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public boolean isIgnoreSame() {
		return ignoreSame;
	}

	public void setIgnoreSame(boolean ignoreSame) {
		this.ignoreSame = ignoreSame;
	}

}
