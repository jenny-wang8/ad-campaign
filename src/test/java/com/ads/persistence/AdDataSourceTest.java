package com.ads.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ads.domain.AdCampaign;

public class AdDataSourceTest {

	@Test
	public void testPut() {
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 0);
		
		AdCampaign a1 = new AdCampaign();
		a1.setPartner_id("partner1");
		a1.setDuration(10);
		AdDataSource.getInstance().setAdCampaign(a1);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 1);
		
		AdCampaign a2 = new AdCampaign();
		a2.setPartner_id("partner2");
		a2.setDuration(20);
		AdDataSource.getInstance().setAdCampaign(a2);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 2);
		
		AdCampaign a3 = new AdCampaign();
		a3.setPartner_id("partner3");
		a3.setDuration(30);
		AdDataSource.getInstance().setAdCampaign(a3);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 3);
		
		AdCampaign a10 = new AdCampaign();
		a10.setPartner_id("partner1");
		a10.setDuration(100);
		AdDataSource.getInstance().setAdCampaign(a10);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 3);
	}
	
	@Before
	public void resetAdCampaigns() {
		AdDataSource.getInstance().resetData();
	}
	
	@Test
	public void testGet() {
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 0);
		//null partnerId
		assertNull(AdDataSource.getInstance().getAdCampaignFor(null));
		
		//no string
		assertNull(AdDataSource.getInstance().getAdCampaignFor(""));
		
		//empty string
		assertNull(AdDataSource.getInstance().getAdCampaignFor("  "));
		
		//partnerId with no campaign
		assertNull(AdDataSource.getInstance().getAdCampaignFor("partner1"));
		
		//partnerId with expired campaign
		AdCampaign a1 = new AdCampaign();
		a1.setPartner_id("partner1");
		a1.setExpirationDate(System.currentTimeMillis() - 10000);
		AdDataSource.getInstance().setAdCampaign(a1);
		assertNull(AdDataSource.getInstance().getAdCampaignFor("partner1"));
		
		//partnerId with active campaign
		AdCampaign a2 = new AdCampaign();
		a2.setPartner_id("partner2");
		a2.setExpirationDate(System.currentTimeMillis() + 10000);
		AdDataSource.getInstance().setAdCampaign(a2);
		
		AdCampaign saved = AdDataSource.getInstance().getAdCampaignFor("partner2");
		assertNotNull(saved);
		assertEquals(saved, a2);
	}
	
	@Test
	public void testGetAll() {
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 0);
		assertNotNull(AdDataSource.getInstance().getAllAdCampaigns());
		assertTrue(AdDataSource.getInstance().getAllAdCampaigns().isEmpty());
		
		AdCampaign a1 = new AdCampaign();
		a1.setPartner_id("partner1");
		a1.setExpirationDate(System.currentTimeMillis() + 10000);
		AdDataSource.getInstance().setAdCampaign(a1);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 1);
		
		AdCampaign a10 = new AdCampaign();
		a10.setPartner_id("partner1");
		a10.setExpirationDate(System.currentTimeMillis() + 12000);
		AdDataSource.getInstance().setAdCampaign(a10);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 1);
		
		AdCampaign a2 = new AdCampaign();
		a2.setPartner_id("partner2");
		a2.setExpirationDate(System.currentTimeMillis() + 10000);
		AdDataSource.getInstance().setAdCampaign(a2);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 2);
		
		AdCampaign a3 = new AdCampaign();
		a3.setPartner_id("partner3");
		a3.setExpirationDate(System.currentTimeMillis() + 10000);
		AdDataSource.getInstance().setAdCampaign(a3);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 3);
		
		AdCampaign a30 = new AdCampaign();
		a30.setPartner_id("partner3");
		a30.setExpirationDate(System.currentTimeMillis() + 12000);
		AdDataSource.getInstance().setAdCampaign(a30);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 3);
		
		AdCampaign a4 = new AdCampaign();
		a4.setPartner_id("partner4");
		a4.setExpirationDate(System.currentTimeMillis() - 10000);
		AdDataSource.getInstance().setAdCampaign(a4);
		assertTrue(AdDataSource.getInstance().getAdCampaigns().size() == 4);
		
		List<AdCampaign> active = AdDataSource.getInstance().getAllAdCampaigns();
		System.out.println(active.size());
		assertTrue(active.size() == 6);
	}
	
}