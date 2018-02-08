/**
 * 
 */
package com.vtb.domain;

import java.util.Vector;

/**
 * Страница организаций для выгрузки в систему Рейтинги.
 * Сделан класс по просьбе Лисовского в письме Жогину от 12.05.2009 14:49
 * @author Andrey Pavlenko
 */
public class OrganisationPartnerPage extends VtbObject {
	 private Long startRow;

	 private Long rows;

	 private Long totalRows;

	 private Vector<OrganisationPartner> partners = new Vector<OrganisationPartner>();

	 public Vector<OrganisationPartner> getPartners() {
	 		 return partners;
	 }

	 public void setPartners(Vector<OrganisationPartner> partners) {
	 		 this.partners = partners;
	 }

	 public Long getRows() {
	 		 return rows;
	 }

	 public void setRows(Long rows) {
	 		 this.rows = rows;
	 }

	 public void setRows(Integer rows) {
 		 this.rows = Long.valueOf(rows.toString());
	 }
	 
	 public Long getStartRow() {
	 		 return startRow;
	 }

	 public void setStartRow(Long startRow) {
	 		 this.startRow = startRow;
	 }

	 public Long getTotalRows() {
	 		 return totalRows;
	 }

	 public void setTotalRows(Long totalRows) {
	 		 this.totalRows = totalRows;
	 }
	 
}
