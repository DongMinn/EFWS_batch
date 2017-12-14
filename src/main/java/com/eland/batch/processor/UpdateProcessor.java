package com.eland.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.eland.batch.dto.DailyPlantTotalData;

public class UpdateProcessor implements ItemProcessor<DailyPlantTotalData, DailyPlantTotalData>{

	@Override
	public DailyPlantTotalData process(DailyPlantTotalData item) throws Exception {
		// TODO Auto-generated method stub
		
		if(item.getTotalCustomer()==null) item.setTotalCustomer(0);
		if(item.getTotalEntrance()==null) item.setTotalEntrance(0);
		if(item.getTotalKakao()==null) item.setTotalKakao(0);
		if(item.getTotalNoshow()==null) item.setTotalNoshow(0);
		if(item.getTotalPrint()==null) item.setTotalPrint(0);
		
		
		double rate = 1-((double)item.getTotalEntrance()/(double)item.getTotalCustomer());
	
		item.setLeaveRate(rate);
		
		
		
		return item;
	}

}
