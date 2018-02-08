package ru.md.domain;

import java.util.Date;

/**
 * Организация. Юридическое лицо.
 * @author Andrey Pavlenko
 */
public class ClientInfo {
	private String id;
	private String pub;
	private String status;
	private Date securityLast;
	private Date securityValidto;
	private String securityText;
	private String corpBlock;

	private String groupRating;
	private String groupRatingReview;
	private String ratingMethod;
	private String rating;
	private String ratingScale;
	private String ratingScaleGroup;
	private String ratingReview;

	private String suspendLimitLoan;
	private String suspendLimitInvest;

	private Long groupDecision;
	private Long groupDecisionReview;
	private Long clientDecision;
	private Long clientDecisionReview;
	private Long limitDecision;
	private Long suspendLimitInvestDecision;
	private Long suspendLimitLoanDecision;

	private String sublimit;
	private Date validtoDate;
	private Date suspendLimitLoanDate;
	private Date suspendLimitInvestDate;

	/**
	 * Returns .
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets .
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getPub() {
		return pub;
	}

	/**
	 * Sets .
	 * @param pub
	 */
	public void setPub(String pub) {
		this.pub = pub;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets .
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getSecurityLast() {
		return securityLast;
	}

	/**
	 * Sets .
	 * @param securityLast
	 */
	public void setSecurityLast(Date securityLast) {
		this.securityLast = securityLast;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getSecurityValidto() {
		return securityValidto;
	}

	/**
	 * Sets .
	 * @param securityValidto
	 */
	public void setSecurityValidto(Date securityValidto) {
		this.securityValidto = securityValidto;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getSecurityText() {
		return securityText;
	}

	/**
	 * Sets .
	 * @param securityText
	 */
	public void setSecurityText(String securityText) {
		this.securityText = securityText;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getGroupRating() {
		return groupRating;
	}

	/**
	 * Sets .
	 * @param groupRating
	 */
	public void setGroupRating(String groupRating) {
		this.groupRating = groupRating;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getGroupRatingReview() {
		return groupRatingReview;
	}

	/**
	 * Sets .
	 * @param groupRatingReview
	 */
	public void setGroupRatingReview(String groupRatingReview) {
		this.groupRatingReview = groupRatingReview;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getRatingMethod() {
		return ratingMethod;
	}

	/**
	 * Sets .
	 * @param ratingMethod
	 */
	public void setRatingMethod(String ratingMethod) {
		this.ratingMethod = ratingMethod;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * Sets .
	 * @param rating
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getRatingScale() {
		return ratingScale;
	}

	/**
	 * Sets .
	 * @param ratingScale
	 */
	public void setRatingScale(String ratingScale) {
		this.ratingScale = ratingScale;
	}


	/**
	 * Returns .
	 * @return
	 */
	public String getRatingReview() {
		return ratingReview;
	}

	/**
	 * Sets .
	 * @param ratingReview
	 */
	public void setRatingReview(String ratingReview) {
		this.ratingReview = ratingReview;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getSuspendLimitLoan() {
		return suspendLimitLoan;
	}

	/**
	 * Sets .
	 * @param suspendLimitLoan
	 */
	public void setSuspendLimitLoan(String suspendLimitLoan) {
		this.suspendLimitLoan = suspendLimitLoan;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getSuspendLimitInvest() {
		return suspendLimitInvest;
	}

	/**
	 * Sets .
	 * @param suspendLimitInvest
	 */
	public void setSuspendLimitInvest(String suspendLimitInvest) {
		this.suspendLimitInvest = suspendLimitInvest;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getCorpBlock() {
		return corpBlock;
	}

	/**
	 * Sets .
	 * @param corpBlock
	 */
	public void setCorpBlock(String corpBlock) {
		this.corpBlock = corpBlock;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getGroupDecision() {
		return groupDecision;
	}

	/**
	 * Sets .
	 * @param groupDecision
	 */
	public void setGroupDecision(Long groupDecision) {
		this.groupDecision = groupDecision;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getClientDecision() {
		return clientDecision;
	}

	/**
	 * Sets .
	 * @param clientDecision
	 */
	public void setClientDecision(Long clientDecision) {
		this.clientDecision = clientDecision;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getGroupDecisionReview() {
		return groupDecisionReview;
	}

	/**
	 * Sets .
	 * @param groupDecisionReview
	 */
	public void setGroupDecisionReview(Long groupDecisionReview) {
		this.groupDecisionReview = groupDecisionReview;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getClientDecisionReview() {
		return clientDecisionReview;
	}

	/**
	 * Sets .
	 * @param clientDecisionReview
	 */
	public void setClientDecisionReview(Long clientDecisionReview) {
		this.clientDecisionReview = clientDecisionReview;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getRatingScaleGroup() {
		return ratingScaleGroup;
	}

	/**
	 * Sets .
	 * @param ratingScaleGroup
	 */
	public void setRatingScaleGroup(String ratingScaleGroup) {
		this.ratingScaleGroup = ratingScaleGroup;
	}

	/**
	 * Returns .
	 * @return
	 */
	public String getSublimit() {
		return sublimit;
	}

	/**
	 * Sets .
	 * @param sublimit
	 */
	public void setSublimit(String sublimit) {
		this.sublimit = sublimit;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getValidtoDate() {
		return validtoDate;
	}

	/**
	 * Sets .
	 * @param validtoDate
	 */
	public void setValidtoDate(Date validtoDate) {
		this.validtoDate = validtoDate;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getLimitDecision() {
		return limitDecision;
	}

	/**
	 * Sets .
	 * @param limitDecision
	 */
	public void setLimitDecision(Long limitDecision) {
		this.limitDecision = limitDecision;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getSuspendLimitInvestDecision() {
		return suspendLimitInvestDecision;
	}

	/**
	 * Sets .
	 * @param suspendLimitInvestDecision
	 */
	public void setSuspendLimitInvestDecision(Long suspendLimitInvestDecision) {
		this.suspendLimitInvestDecision = suspendLimitInvestDecision;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Long getSuspendLimitLoanDecision() {
		return suspendLimitLoanDecision;
	}

	/**
	 * Sets .
	 * @param suspendLimitLoanDecision
	 */
	public void setSuspendLimitLoanDecision(Long suspendLimitLoanDecision) {
		this.suspendLimitLoanDecision = suspendLimitLoanDecision;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getSuspendLimitLoanDate() {
		return suspendLimitLoanDate;
	}

	/**
	 * Sets .
	 * @param suspendLimitLoanDate
	 */
	public void setSuspendLimitLoanDate(Date suspendLimitLoanDate) {
		this.suspendLimitLoanDate = suspendLimitLoanDate;
	}

	/**
	 * Returns .
	 * @return
	 */
	public Date getSuspendLimitInvestDate() {
		return suspendLimitInvestDate;
	}

	/**
	 * Sets .
	 * @param suspendLimitInvestDate
	 */
	public void setSuspendLimitInvestDate(Date suspendLimitInvestDate) {
		this.suspendLimitInvestDate = suspendLimitInvestDate;
	}
}
