package com.ads.domain;

import java.io.Serializable;
import java.util.Objects;

public class AdCampaign implements Serializable {

	private static final long serialVersionUID = 1l;

	private String partner_id;
	private Integer duration;
	private String ad_content;
	private transient Long expirationDate;

	public String getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(String partner_id) {
		this.partner_id = partner_id;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getAd_content() {
		return ad_content;
	}

	public void setAd_content(String ad_content) {
		this.ad_content = ad_content;
	}

	public Long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Long expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(ad_content, duration, partner_id, expirationDate);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AdCampaign)) {
			return false;
		}
		AdCampaign ad = (AdCampaign) o;
		return Objects.equals(ad_content, ad.getAd_content()) &&
				Objects.equals(duration, ad.getDuration()) && 
				Objects.equals(expirationDate, ad.getExpirationDate()) && 
				Objects.equals(partner_id, ad.getPartner_id());
	}

}