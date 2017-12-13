package com.eland.batch.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.*;

public class DailyPlantTotalData {

	
	private String brandCode;
	private String plantCode;
	private String plantName;
	private String saleDate;
	private Integer totalCustomer;
	private Integer totalKakao;
	private Integer totalPrint;
	private Integer totalEntrance;
	private Integer totalNoshow;
	private Double leaveRate;
	private Integer avgWaitTime;
	private Integer maxWaitTime;
	private LocalTime maxWaitOrderTime;
	private Integer minWaitTime;
	private LocalTime minWaitOrderTime;
	private LocalTime firstWaitOrderTime;
	private LocalTime lastWaitOrderTime;
	private String creator;
	
	
	public String getBrandCode() {
		return brandCode;
	}
	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}
	public String getPlantCode() {
		return plantCode;
	}
	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}
	public String getPlantName() {
		return plantName;
	}
	public void setPlantName(String plantName) {
		this.plantName = plantName;
	}
	public String getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}
	public Integer getTotalCustomer() {
		return totalCustomer;
	}
	public void setTotalCustomer(Integer totalCustomer) {
		this.totalCustomer = totalCustomer;
	}
	public Integer getTotalKakao() {
		return totalKakao;
	}
	public void setTotalKakao(Integer totalKakao) {
		this.totalKakao = totalKakao;
	}
	public Integer getTotalPrint() {
		return totalPrint;
	}
	public void setTotalPrint(Integer totalPrint) {
		this.totalPrint = totalPrint;
	}
	public Integer getTotalEntrance() {
		return totalEntrance;
	}
	public void setTotalEntrance(Integer totalEntrance) {
		this.totalEntrance = totalEntrance;
	}
	public Integer getTotalNoshow() {
		return totalNoshow;
	}
	public void setTotalNoshow(Integer totalNoshow) {
		this.totalNoshow = totalNoshow;
	}
	public Double getLeaveRate() {
		return leaveRate;
	}
	public void setLeaveRate(Double leaveRate) {
		this.leaveRate = leaveRate;
	}
	public Integer getAvgWaitTime() {
		return avgWaitTime;
	}
	public void setAvgWaitTime(Integer avgWaitTime) {
		this.avgWaitTime = avgWaitTime;
	}
	public Integer getMaxWaitTime() {
		return maxWaitTime;
	}
	public void setMaxWaitTime(Integer maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}
	public LocalTime getMaxWaitOrderTime() {
		return maxWaitOrderTime;
	}
	public void setMaxWaitOrderTime(LocalTime maxWaitOrderTime) {
		this.maxWaitOrderTime = maxWaitOrderTime;
	}
	public Integer getMinWaitTime() {
		return minWaitTime;
	}
	public void setMinWaitTime(Integer minWaitTime) {
		this.minWaitTime = minWaitTime;
	}
	public LocalTime getMinWaitOrderTime() {
		return minWaitOrderTime;
	}
	public void setMinWaitOrderTime(LocalTime minWaitOrderTime) {
		this.minWaitOrderTime = minWaitOrderTime;
	}
	public LocalTime getFirstWaitOrderTime() {
		return firstWaitOrderTime;
	}
	public void setFirstWaitOrderTime(LocalTime firstWaitOrderTime) {
		this.firstWaitOrderTime = firstWaitOrderTime;
	}
	public LocalTime getLastWaitOrderTime() {
		return lastWaitOrderTime;
	}
	public void setLastWaitOrderTime(LocalTime lastWaitOrderTime) {
		this.lastWaitOrderTime = lastWaitOrderTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public String getUpdater() {
		return updater;
	}
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	private Date creationTime;
	private String updater;
	private Date updateTime;
}
