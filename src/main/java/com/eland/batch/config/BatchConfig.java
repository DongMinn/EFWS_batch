package com.eland.batch.config;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;


import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.scheduling.annotation.Scheduled;

import com.eland.batch.dto.DailyPlantTotalData;
import com.eland.batch.utils.CurrentDate;
import com.eland.batch.implement.BatchJobCompletionListener;
import com.eland.batch.processor.CreateProcessor;
import com.eland.batch.processor.Update2Processor;
import com.eland.batch.processor.UpdateProcessor;




@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job createJob;
	
	
	@Scheduled(fixedRate=100000)
	public void createDailyTable(){
		try{
			System.out.println("batch 시작");
			
			JobParameters jobparameters = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis())
					.toJobParameters();
			
			jobLauncher.run(createJob, jobparameters);
			
			System.out.println("i have been scheduled with spring batch!!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@Bean
	public Job createJob(JobBuilderFactory jobs , Step createStep,Step updateStep , JobExecutionListener listener){
		return jobs.get("createJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.start(createStep)
				.next(updateStep)
				.build();
	}	
	
	@Bean
	public Step createStep(StepBuilderFactory stepBuilderFactory 
			, ItemReader<DailyPlantTotalData> createReader 
			, ItemWriter<DailyPlantTotalData> createWriter
			, ItemProcessor<DailyPlantTotalData, DailyPlantTotalData> createProcessor){
			
		return stepBuilderFactory.get("createStep")
			.<DailyPlantTotalData, DailyPlantTotalData>chunk(1)
			.reader(createReader)
			.processor(createProcessor)
			.writer(createWriter)
			.build();
	}
	
	@Bean
	public ItemReader<DailyPlantTotalData> createReader(DataSource dataSource){
		String query = "SELECT left(a.plant_code , 2) as brand_code  , a.plant_code , b.plant_name , a.sale_date  , count(*) total_customer  , 'batch' , NOW()  , 'batch' , NOW() "
				+ "FROM table_reservation_store as a "
				+ "join plant as b "
				+ "on a.plant_code = b.plant_code "
				+ "where 1=1 "
				+ "and sale_date = ? "
				+ "group by left(a.plant_code , 2)  , a.plant_code , b.plant_name , a.sale_date ";
		
		JdbcCursorItemReader<DailyPlantTotalData> reader = new JdbcCursorItemReader<>();
		reader.setDataSource(dataSource);
		reader.setPreparedStatementSetter(new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				
				LocalDate today= LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
						
//				String yesterDay = today.minusDays(1).format( formatter );
				String yesterDay = today.format( formatter );
				ps.setString(1, yesterDay);
				
			}
		});
		
		reader.setSql(query);
		reader.setRowMapper(new BeanPropertyRowMapper<>(DailyPlantTotalData.class));
		return reader;
		
	}
	
	@Bean
	public ItemWriter<DailyPlantTotalData> createWriter(DataSource dataSource
			, ItemPreparedStatementSetter<DailyPlantTotalData> createSetter){
		JdbcBatchItemWriter<DailyPlantTotalData> writer = new JdbcBatchItemWriter<>();
		
		
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<DailyPlantTotalData>());
		writer.setItemPreparedStatementSetter(createSetter);
		writer.setSql("insert into daily_plant_total_data"
				+ "(brand_code , plant_code , plant_name , sale_date , total_customer , creator , creation_time , updater , update_time) "
				+ "values (?,?,?,?,?,?,now(),?,now())");
		
		writer.setDataSource(dataSource);
		return writer;
	}
	
	@Bean
	public ItemPreparedStatementSetter<DailyPlantTotalData> createSetter(){
		return (item, ps)->{
		
			ps.setString(1, item.getBrandCode());
			ps.setString(2, item.getPlantCode());
			ps.setString(3, item.getPlantName());
			ps.setString(4, item.getSaleDate());
			ps.setInt(5, item.getTotalCustomer());
			ps.setString(6, "batch");
//			ps.setDate(7, CurrentDate.getCurrentDate());
			ps.setString(7, "batch");
//			ps.setDate(9, CurrentDate.getCurrentDate());
			
		};
	}
	
	@Bean
	public ItemProcessor<DailyPlantTotalData, DailyPlantTotalData> createProcessor(){
		return new CreateProcessor();
	}
	
	@Bean
	public Step updateStep(StepBuilderFactory stepBuilderFactory 
			, ItemReader<DailyPlantTotalData> updateReader 
			, ItemWriter<DailyPlantTotalData> updateWriter
			, ItemProcessor<DailyPlantTotalData, DailyPlantTotalData> updateProcessor){
			
		return stepBuilderFactory.get("updateStep")
			.<DailyPlantTotalData, DailyPlantTotalData>chunk(1)
			.reader(updateReader)
			.processor(updateProcessor)
			.writer(updateWriter)
			.build();
	}

	
	@Bean
	public ItemReader<DailyPlantTotalData> updateReader(DataSource dataSource){
		String query = 
			"SELECT a.sale_date , a.plant_code , a.total_customer ,b.total_entrance , b.avg_wait_time , max.max_wait_time, DATE_FORMAT(max.max_wait_order_time , '%H:%i:%s') as max_wait_order_time "
			+ ", min.min_wait_time  , DATE_FORMAT(min.min_wait_order_time , '%H:%i:%s') as min_wait_order_time  "
			+", f.total_kakao , g.total_print  , h.total_noshow , i.first_wait_order_time , i.last_wait_order_time "
			+"FROM daily_plant_total_data as a " 
			+"LEFT JOIN ( "
					+"SELECT  a.plant_code , a.sale_date  , count(*) as total_entrance " 
					+", ROUND((sum(total_turnaround_time))/60) as avg_wait_time " 
					+"FROM table_reservation_store as a " 
					+"WHERE 1=1 "
					+"AND sale_date = ? "
				    +"AND waiting_state = 'ENTRANCE' "
				    +"GROUP BY a.plant_code ,a.sale_date "
				    +") as b " 
			+"ON a.plant_code = b.plant_code " 
			+"AND a.sale_date = b.sale_date "
			+"LEFT JOIN ( "
					+ "SELECT max.plant_code , max.sale_date , max.max_wait_time , a.reservation_order_time as max_wait_order_time "
					+ "FROM table_reservation_store as a "
					+ "JOIN( "
							+"SELECT  a.plant_code , a.sale_date " 
							+", ROUND((max(total_turnaround_time)/60)) as max_wait_time " 
							+"FROM table_reservation_store as a " 
							+"WHERE 1=1 "
							+"AND sale_date = ? "
						    +"AND waiting_state = 'ENTRANCE' "
						    +"GROUP BY a.plant_code ,a.sale_date "
						    +") as max "
					+ "ON a.sale_date = max.sale_date "
					+ "AND a.plant_code = max.plant_code "
					+ "AND ROUND((a.total_turnaround_time)/60) = max.max_wait_time "
					+ ")as max " 
			+"ON a.plant_code = max.plant_code " 
			+"AND a.sale_date = max.sale_date "
			+"LEFT JOIN ( "
					+ "SELECT min.plant_code , min.sale_date , min.min_wait_time , a.reservation_order_time as min_wait_order_time "
					+ "FROM table_reservation_store as a "
					+ "JOIN( "
							+"SELECT  a.plant_code , a.sale_date " 
							+", ROUND((min(total_turnaround_time)/60)) as min_wait_time " 
							+"FROM table_reservation_store as a " 
							+"WHERE 1=1 "
							+"AND sale_date = ? "
						    +"AND waiting_state = 'ENTRANCE' "
						    +"GROUP BY a.plant_code ,a.sale_date "
						    +") as min "
					+ "ON a.sale_date = min.sale_date "
					+ "AND a.plant_code = min.plant_code "
					+ "AND ROUND((a.total_turnaround_time)/60) = min.min_wait_time "
					+ ") as min " 
			+"ON a.plant_code = min.plant_code " 
			+"AND a.sale_date = min.sale_date "
			+"LEFT JOIN( "
					+"SELECT a.plant_code , a.sale_date , count(*) as total_kakao "
					+"FROM table_reservation_store as a " 
					+"WHERE 1=1 "
					+"AND sale_date = ? "
					+"AND customer_cellphone<>'000' "
					+"GROUP BY a.plant_code ,a.sale_date "
					+")as f "
			+"ON a.plant_code = f.plant_code "
			+"AND a.sale_date = f.sale_date "
			+"LEFT JOIN( "
					+"SELECT a.plant_code , a.sale_date , count(*) as total_print "
					+"FROM table_reservation_store as a  "
					+"WHERE 1=1  "
					+"AND sale_date = ? "
					+"AND customer_cellphone='000' "
					+"GROUP BY a.plant_code ,a.sale_date "
					+")as g "
			+"ON a.plant_code = g.plant_code "
			+"AND a.sale_date = g.sale_date "
			+"LEFT JOIN ( "
					+"SELECT  a.plant_code , a.sale_date  , count(*) as total_noshow "
					+"FROM table_reservation_store as a  "
					+"WHERE 1=1 "
					+"AND sale_date = ? "
					+"AND waiting_state = 'NOSHOW' "
					+"GROUP BY a.plant_code ,a.sale_date "
					+") as h "
			+"ON a.plant_code = h.plant_code  "
			+"AND a.sale_date = h.sale_date "
			+"LEFT JOIN ( "
					+"SELECT  a.plant_code , a.sale_date , min(reservation_order_time) as first_wait_order_time  "
					+", max(reservation_order_time) as last_wait_order_time "
					+"FROM table_reservation_store as a  "
					+"WHERE 1=1 "
					+"AND sale_date = ? "
					+"GROUP BY a.plant_code ,a.sale_date "
					+")as i "
			+"ON a.plant_code= i.plant_code "
			+"AND a.sale_date = i.sale_date	 "
			+"WHERE a.sale_date = ? ";
		JdbcCursorItemReader<DailyPlantTotalData> reader = new JdbcCursorItemReader<>();
		
		
		reader.setDataSource(dataSource);
		reader.setPreparedStatementSetter(new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				
				LocalDate today= LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//				String yesterDay = today.minusDays(1).format( formatter );
				String yesterDay = today.format( formatter );
				ps.setString(1, yesterDay);
				ps.setString(2, yesterDay);
				ps.setString(3, yesterDay);
				ps.setString(4, yesterDay);
				ps.setString(5, yesterDay);
				ps.setString(6, yesterDay);
				ps.setString(7, yesterDay);
				ps.setString(8, yesterDay);
			}
		});
		
		reader.setSql(query);
		reader.setRowMapper(new BeanPropertyRowMapper<>(DailyPlantTotalData.class));
		return reader;
		
	}
	
	@Bean
	public ItemWriter<DailyPlantTotalData> updateWriter(DataSource dataSource
			, ItemPreparedStatementSetter<DailyPlantTotalData> updateSetter){
		JdbcBatchItemWriter<DailyPlantTotalData> writer = new JdbcBatchItemWriter<>();
		
		String query = "update daily_plant_total_data "
				+ "Set total_entrance =? , avg_wait_time =?  , max_wait_time=? "
				+ ", min_wait_time=? , total_kakao=? , total_print=? "
				+ ", total_noshow  =? , first_wait_order_time = ? , last_wait_order_time = ? "
				+ ", max_wait_order_time  = ? , min_wait_order_time = ? , leave_rate = ? "
				+ "where plant_code = ? "
				+ "and sale_date = ? ";
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<DailyPlantTotalData>());
		writer.setItemPreparedStatementSetter(updateSetter);
		writer.setSql(query);
		writer.setDataSource(dataSource);
		return writer;
	}
	@Bean
	public ItemPreparedStatementSetter<DailyPlantTotalData> updateSetter(){
		return (item, ps)->{
		
			
			ps.setInt(1, item.getTotalEntrance());
			ps.setInt(2, item.getAvgWaitTime());
			ps.setInt(3, item.getMaxWaitTime());
			ps.setInt(4, item.getMinWaitTime());
			ps.setInt(5, item.getTotalKakao());
			ps.setInt(6, item.getTotalPrint());
			ps.setInt(7, item.getTotalNoshow());
			ps.setTime(8, java.sql.Time.valueOf(item.getFirstWaitOrderTime()));
			ps.setTime(9, java.sql.Time.valueOf(item.getLastWaitOrderTime()));
			ps.setTime(10, java.sql.Time.valueOf(item.getMaxWaitOrderTime()));
			ps.setTime(11, java.sql.Time.valueOf(item.getMinWaitOrderTime()));
			ps.setDouble(12, item.getLeaveRate());
			ps.setString(13, item.getPlantCode());
			ps.setString(14, item.getSaleDate());
				
		};
	}
	@Bean
	public ItemProcessor<DailyPlantTotalData, DailyPlantTotalData> updateProcessor(){
		return new UpdateProcessor();
	}
	
	
	@Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
	@Bean
	public JobExecutionListener listener(){
		return new BatchJobCompletionListener();
	}
	@Bean
	public ResourcelessTransactionManager transactionManager(){
		return new ResourcelessTransactionManager();
	}
}
