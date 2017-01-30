package com.ads.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.ads.domain.AdCampaign;

public class AdDataSource {

	private static final AdDataSource INSTANCE = new AdDataSource(); 
	
	/*
	 * Issues with using a concurrent hash maps:
	 * If the service is stopped/restarted, the data is lost (no data retention)
	 * If there are multiple instances of the service, there is no data
	 * shared between the instances.
	 * 
	 * To scale this, you could use any type of no-sql solution (like
	 * ES, cassandra, DynamoDB if in Amazon, etc.)
	 * The problem with that type of solution, you run the issue with
	 * concurrency.  Adding different ads for the same partnerId at the
	 * same time will cause a data issue.
	 */
	private ConcurrentHashMap<String, AdCampaign> adCampaigns = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Set<AdCampaign>> allCampaigns = 
			new ConcurrentHashMap<>();
	
	private AdDataSource() {}
	
	public static AdDataSource getInstance() {
		return INSTANCE;
	}
	
	public AdCampaign getAdCampaignFor(final String partnerId) {
		AdCampaign ad = null;
		if (partnerId == null || partnerId.trim().length() == 0)
			return ad;
		
		ad = getAdCampaigns().get(partnerId);
		
		if (ad != null) {
			if (ad.getExpirationDate() < System.currentTimeMillis()) {
				ad = null;
				getAdCampaigns().remove(partnerId);
			}
		}
		
		return ad;
	}
	
	public AdCampaign setAdCampaign(final AdCampaign ad) {
		getAdCampaigns().put(ad.getPartner_id(), ad);
		
		Set<AdCampaign> ads = getAllCampaigns().putIfAbsent(ad.getPartner_id(), 
				new HashSet<AdCampaign>());
		if (ads == null)
		   ads = getAllCampaigns().get(ad.getPartner_id());
		synchronized (ads) {
		   ads.add(ad);
		}
		
		return getAdCampaigns().get(ad.getPartner_id());
	}
	
	protected ConcurrentHashMap<String, AdCampaign> getAdCampaigns() {
		return adCampaigns;
	}
	
	protected ConcurrentHashMap<String, Set<AdCampaign>> getAllCampaigns() {
		return allCampaigns;
	}
	
	public List<AdCampaign> getAllAdCampaigns() {
			return getAllCampaigns().values()
				.parallelStream()
				.flatMap(v -> v.stream())
				.collect(Collectors.toList());
	}
	
	/*
	 * This is used for testing
	 */
	public void resetData() {
		adCampaigns.clear();
		allCampaigns.clear();
	}
	
}