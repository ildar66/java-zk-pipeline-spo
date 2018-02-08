package ru.md.spo.report;

public class Statistic {
    private Long product;
    private Long limit;
    private Long closed;
    private Long active;
    private Long paused;
	public Long getAll() {
		return closed+paused+active;
	}
	public Long getProduct() {
		return product;
	}
	public void setProduct(Long product) {
		this.product = product;
	}
	public Long getLimit() {
		return limit;
	}
	public void setLimit(Long limit) {
		this.limit = limit;
	}
	public Long getClosed() {
		return closed;
	}
	public void setClosed(Long closed) {
		this.closed = closed;
	}
	public Long getActive() {
		return active;
	}
	public void setActive(Long active) {
		this.active = active;
	}
	public Long getPaused() {
		return paused;
	}
	public void setPaused(Long paused) {
		this.paused = paused;
	}
    
    
}
