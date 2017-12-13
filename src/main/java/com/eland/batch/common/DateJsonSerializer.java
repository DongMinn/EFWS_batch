package com.eland.batch.common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

// json으로 return할 때, Date형이 long 숫자로 나오는 것을 'yyyy-MM-dd HH:mm:ss'형으로 변경한다.
public class DateJsonSerializer extends JsonSerializer<Date> {
	private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		String formatedDate = DATEFORMAT.format(value);
		gen.writeString(formatedDate);
	}
}
