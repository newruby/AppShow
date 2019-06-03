package bupt.edu.entity;


public class OmcBean {
	private Integer scoreid;
	private Double sendrate;
	private Double receiverate;
	private String rsrp;
	private String rsrq;
	private String sinr;
	private Double access;
	private Double maintain ;
	private Double mobility ;
	private Double integri ;
	private Double capacity ;
	private Double comprehensive ;

	public OmcBean(){}

	public String getRsrp() {
		return rsrp;
	}

	public void setRsrp(String rsrp) {
		this.rsrp = rsrp;
	}


	public String getRsrq() {
		return rsrq;
	}

	public void setRsrq(String rsrq) {
		this.rsrq = rsrq;
	}

	public String getSinr() {
		return sinr;
	}

	public void setSinr(String sinr) {
		this.sinr = sinr;
	}

	public Integer getScoreid() {
		return scoreid;
	}

	public void setScoreid(Integer scoreid) {
		this.scoreid = scoreid;
	}

	public Double getAccess () {
		return access ;
	}

	public void setAccess (Double access ) {
		this.access  = access ;
	}

	public Double getmaintain () {
		return maintain ;
	}

	public void setmaintain (Double maintain ) {
		this.maintain  = maintain ;
	}

	public Double getMobility () {
		return mobility ;
	}

	public void setMobility (Double mobility ) {
		this.mobility  = mobility ;
	}

	public Double getCapacity () {
		return capacity ;
	}

	public void setCapacity (Double capacity ) {
		this.capacity  = capacity ;
	}

	public Double getComprehensive () {
		return comprehensive ;
	}

	public void setComprehensive (Double comprehensive ) {
		this.comprehensive  = comprehensive ;
	}

	public Double getSendrate() {
		return sendrate;
	}

	public void setSendrate(Double sendrate) {
		this.sendrate = sendrate;
	}

	public Double getReceiverate() {
		return receiverate;
	}

	public void setReceiverate(Double receiverate) {
		this.receiverate = receiverate;
	}

	public Double getMaintain() {
		return maintain;
	}

	public void setMaintain(Double maintain) {
		this.maintain = maintain;
	}

	public Double getIntegri() {
		return integri;
	}

	public void setIntegri(Double integri) {
		this.integri = integri;
	}

	@Override
	public String toString() {
		return this.scoreid+"\t"+this.sendrate+"\t"+this.receiverate+"\t"+this.rsrp+"\t"+this.rsrp+"\t"+this.sinr+"\t"+this.access 
	+"\t"+this.capacity +"\t"+this.mobility+"\t"+this.maintain +"\t"+this.integri+"\t"+this.comprehensive;
	}
	
	
}
