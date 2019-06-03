package bupt.edu.entity;

public class IndexScore {

	private Integer scoreid;
	private Double access ;
	private Double maintain ;
	private Double mobility ;
	private Double integrity ;
	private Double capacity ;
	private Double comprehensive ;
	
	public IndexScore(){}

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

	public Double getIntegrity () {
		return integrity ;
	}

	public void setIntegrity (Double integrity ) {
		this.integrity  = integrity ;
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


}
