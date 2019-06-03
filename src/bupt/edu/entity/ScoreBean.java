package bupt.edu.entity;

public class ScoreBean {
	private Integer scoreid ;
    private Double access ;
    private Double maintain ;
    private Double mobility  ;
    private Double integri ;
    private Double  capacity  ;
    private Double  comprehensive  ; 
    
    public ScoreBean(){}

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
    	return this.scoreid +"\t"+this.access+"\t"+this.mobility+"\t"+this.maintain+"\t"
    			+this.integri+"\t"+this.capacity+"\t"+this.comprehensive;
    }
}
