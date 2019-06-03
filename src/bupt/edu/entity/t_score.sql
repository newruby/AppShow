create table t_score(
	    scoreid int auto_increment primary key,
	    cellid varchar(50) not null,
	    omctime varchar(50) not null,
	    cellidname varchar(100) not null,
	    access double not null,
	    maintain double not null,
	    mobility double not null,
	    integri double not null,
	    capacity double not null,
	    comprehensive double ,
	    access_n double ,
	    maintain_n double ,
	    mobility_n double ,
	    integri_n double ,
	    capacity_n double 
);