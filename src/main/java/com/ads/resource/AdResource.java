package com.ads.resource;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;

import org.eclipse.jetty.http.HttpStatus;

import com.ads.domain.AdCampaign;
import com.ads.persistence.AdDataSource;
import com.google.gson.Gson;

import spark.Response;

public class AdResource {
	
	public static final String NO_CAMPAIGN_DETAILS = "No ad campaign details provided";
	public static final String NO_PARTNER_ID = "No partner id provided";
	public static final String NO_DURATION = "No/Invalid duration provided";
	public static final String NO_ACTIVE_CAMPAIGNS = "No active ad campaigns exists for the specified partner";
	
	private static final Gson GSON = new Gson();

	public AdResource() {
		init();
	}
	
	private void init() {
		post("/ad", (request, response) -> 
			saveAd(request.body(), response));
		
		get("/ad/:partnerId", (request, response) -> 
			getAd(request.params("partnerId"), response));
		
		get("/ad", (request, response) ->
			getActiveAds(response));
	}
	
	protected String saveAd(final String requestBody, final Response response) {
		/*
		 * Monitoring considerations:  
		 * I would add metrics for number of saves, how long the full save took,
		 * as well as any save errors thrown.
		 * I would also add logging of the request body & subsequent response for
		 * any troubleshooting
		 */
		if (requestBody == null || requestBody.trim().length() == 0) {
			response.status(HttpStatus.BAD_REQUEST_400);
			return NO_CAMPAIGN_DETAILS;
		}
		
		AdCampaign ad = GSON.fromJson(requestBody, AdCampaign.class);
		if (ad.getPartner_id() == null || ad.getPartner_id().length() == 0) {
			response.status(HttpStatus.BAD_REQUEST_400);
			return NO_PARTNER_ID;
		}
		
		if (ad.getDuration() == null || ad.getDuration() <= 0) {
			response.status(HttpStatus.BAD_REQUEST_400);
			return NO_DURATION;
		}
		
		//Duration is in seconds, current time is in millis
		ad.setExpirationDate(System.currentTimeMillis() + (ad.getDuration() * 1000));
		AdCampaign saved = AdDataSource.getInstance().setAdCampaign(ad);
		
		response.status(HttpStatus.OK_200);		
		return GSON.toJson(saved);
	}
	
	protected String getAd(final String partnerId, final Response response) {
		/*
		 * Monitoring considerations:  
		 * I would add metrics for number of retrievals, how long the full retrieval took,
		 * as well as any retrieval errors thrown.
		 * I would also add logging for the partnerId to assist with any 
		 * troubleshooting
		 */
		AdCampaign ad = AdDataSource.getInstance().getAdCampaignFor(partnerId);
		
		if (ad == null) {
			response.status(HttpStatus.NOT_FOUND_404);
			return NO_ACTIVE_CAMPAIGNS;
		}
		
		response.status(HttpStatus.OK_200);
		return GSON.toJson(ad);
	}
	
	protected String getActiveAds(final Response response) {
		List<AdCampaign> ads = AdDataSource.getInstance().getAllAdCampaigns();
		response.status(HttpStatus.OK_200);
		return GSON.toJson(ads);
	}
	
}