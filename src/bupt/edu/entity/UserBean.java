package bupt.edu.entity;

public class UserBean {
	
	private Integer appid;
	private String recorddate;
	private String androidVersion;
	private String appName;
	private String memoryrate;
	private String cpuRate;
	private String networkType;
	private String longitude;	
	private String latitude;
	private Integer omcid;
	public UserBean(){}
	
	public Integer getAppid() {
		return appid;
	}

	public void setAppid(Integer appid) {
		this.appid = appid;
	}

	public String getRecorddate() {
		return recorddate;
	}

	public void setRecorddate(String recorddate) {
		this.recorddate = recorddate;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getMemoryrate() {
		return memoryrate;
	}

	public void setMemoryrate(String memoryrate) {
		this.memoryrate = memoryrate;
	}

	

	public String getCpuRate() {
		return cpuRate;
	}

	public void setCpuRate(String cpuRate) {
		this.cpuRate = cpuRate;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public Integer getOmcid() {
		return omcid;
	}

	public void setOmcid(Integer omcid) {
		this.omcid = omcid;
	}

	@Override
	public String toString() {
		return this.appid+"\t"+this.recorddate.toString()+"\t"+this.androidVersion+"\t"+this.appName+"\t"+
	this.cpuRate+"\t"+this.memoryrate+"\t"+this.networkType+"\t"+this.longitude+"\t"+this.latitude;
	}
}
