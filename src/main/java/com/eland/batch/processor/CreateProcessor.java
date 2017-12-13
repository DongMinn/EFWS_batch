package com.eland.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.eland.batch.dto.DailyPlantTotalData;

public class CreateProcessor implements ItemProcessor<DailyPlantTotalData, DailyPlantTotalData>{

	@Override
	public DailyPlantTotalData process(DailyPlantTotalData item) throws Exception {
		// TODO Auto-generated method stub
		return item;
	}

}
