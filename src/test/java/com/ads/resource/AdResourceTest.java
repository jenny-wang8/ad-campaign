package com.ads.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.ads.domain.AdCampaign;
import com.ads.persistence.AdDataSource;
import com.google.gson.Gson;

import spark.RequestResponseFactory;
import spark.Response;

public class AdResourceTest {

	private static final Gson GSON = new Gson();
	
	@Test
	public void saveAdTest() {
		AdResource resource = new AdResource();
		Response response = RequestResponseFactory.create(new TestHttpServletResponse());
		
		//no request passed
		assertEquals(AdResource.NO_CAMPAIGN_DETAILS, resource.saveAd(null, response));
		assertTrue(response.status() == HttpStatus.BAD_REQUEST_400);
		
		//empty String
		response = RequestResponseFactory.create(new TestHttpServletResponse());
		assertEquals(AdResource.NO_CAMPAIGN_DETAILS, resource.saveAd("", response));
		assertTrue(response.status() == HttpStatus.BAD_REQUEST_400);
		
		//spaces string
		response = RequestResponseFactory.create(new TestHttpServletResponse());
		assertEquals(AdResource.NO_CAMPAIGN_DETAILS, resource.saveAd("  ", response));
		assertTrue(response.status() == HttpStatus.BAD_REQUEST_400);
		
		//no partner id
		response = RequestResponseFactory.create(new TestHttpServletResponse());
		assertEquals(AdResource.NO_PARTNER_ID, resource.saveAd("{}", response));
		assertTrue(response.status() == HttpStatus.BAD_REQUEST_400);
		
		//no duration
		response = RequestResponseFactory.create(new TestHttpServletResponse());
		assertEquals(AdResource.NO_DURATION, resource.saveAd("{\"partner_id\": \"p1\"}", response));
		assertTrue(response.status() == HttpStatus.BAD_REQUEST_400);
		
		//negative duration
		response = RequestResponseFactory.create(new TestHttpServletResponse());
		assertEquals(AdResource.NO_DURATION, 
				resource.saveAd("{\"partner_id\": \"p1\", \"duration\":-10}", response));
		assertTrue(response.status() == HttpStatus.BAD_REQUEST_400);
		
		//happy case
		response = RequestResponseFactory.create(new TestHttpServletResponse());
		String resourceResponse = resource.saveAd("{\"partner_id\": \"p2\", \"duration\":10}", response);
		assertTrue(response.status() == HttpStatus.OK_200);
		assertNotNull(resourceResponse);
	}
	
	@Test
	public void getAdTest() {
		String partnerId = "partner1";
		Response response = RequestResponseFactory.create(new TestHttpServletResponse());
		
		AdResource resource = new AdResource();
		assertEquals(AdResource.NO_ACTIVE_CAMPAIGNS, resource.getAd(partnerId, response));
		
		AdCampaign a1 = new AdCampaign();
		a1.setPartner_id(partnerId);
		a1.setExpirationDate(System.currentTimeMillis() + 10000);
		a1.setDuration(10);
		AdDataSource.getInstance().setAdCampaign(a1);
		
		assertEquals(GSON.toJson(a1), resource.getAd(partnerId, response));
	}
	
	@Before
	public void reset() {
		AdDataSource.getInstance().resetData();
	}
	
}