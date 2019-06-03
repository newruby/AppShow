package bupt.edu.entity;

public class CellDataBean {
	
	private String imsi;
	private String deviceBrand;
	private String deviceModel;
	private Integer recordnums;
	public CellDataBean(){}
	
	
	public String getImsi() {
		return imsi;
	}


	public void setImsi(String imsi) {
		this.imsi = imsi;
	}


	public String getDeviceBrand() {
		return deviceBrand;
	}


	public void setDeviceBrand(String deviceBrand) {
		this.deviceBrand = deviceBrand;
	}


	public String getDeviceModel() {
		return deviceModel;
	}


	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}


	public Integer getRecordnums() {
		return recordnums;
	}


	public void setRecordnums(Integer recordnums) {
		this.recordnums = recordnums;
	}


	@Override
	public String toString() {
		return this.imsi+"\t"+this.deviceBrand+"\t"+this.deviceModel+"\t"+this.recordnums;
	}
}
