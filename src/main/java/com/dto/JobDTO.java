package com.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobDTO {
  
	
	private int eid;
	private String jobDescription;
	private String industry;
	private String location;
	
	@NotNull
	private float salaryPackage;
  
  
}