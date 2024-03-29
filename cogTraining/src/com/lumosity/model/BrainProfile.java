package com.lumosity.model;

import java.util.List;

import com.lumosity.model.base.BaseBrainProfile;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class BrainProfile extends BaseBrainProfile<BrainProfile> {
	public static final BrainProfile dao = new BrainProfile();
	
	public List<BrainProfile> findAll(){
		return find("select * from brain_profile");
	}
	
	public List<BrainProfile> findBySort(int LPI){
		return find("select * from brain_profile where overall <="+LPI);
	}
}
