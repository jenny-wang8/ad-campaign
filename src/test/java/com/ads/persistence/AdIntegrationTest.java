package com.ads.persistence;

import com.ads.Resource;
import com.ads.domain.AdCampaign;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class AdIntegrationTest {
	
	private static final Gson GSON = new Gson();

	public static void main(String[] args) {
		Resource.main(args);

		AdIntegrationTest testClass = new AdIntegrationTest();
		testClass.process();
		
		System.exit(0);
	}
	
	public void process() {
		String[] partnerIds = { "partner1", "parnter2", "partner3", "partner4", "partner5" };

		for (int i = 1; i <= 10; i++) {
			for (String partnerId : partnerIds) {
				AdCampaign ad = new AdCampaign();
				ad.setAd_content("content " + i);
				ad.setDuration(i);
				ad.setPartner_id(partnerId);
				
				addAdCampaign(ad);
				getAdCampaign(ad);
			}
		}
	}
	
	protected void addAdCampaign(final AdCampaign ad) {
		Client client = Client.create();

		WebResource webResource = client.resource("http://localhost:4567/ad");

		String requestString = GSON.toJson(ad);

		ClientResponse response = webResource.accept("application/json")
				.post(ClientResponse.class, requestString);

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}

		String output = response.getEntity(String.class);

		/*System.out.println("request : " + requestString);
		System.out.println("response: " + output);*/
		if (!requestString.equals(output)) {
			throw new RuntimeException();
		}
	}

	protected void getAdCampaign(final AdCampaign ad) {
		try {
			Client client = Client.create();

			WebResource webResource = client.resource("http://localhost:4567/ad/" + ad.getPartner_id());

			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			String output = response.getEntity(String.class);

			/*System.out.println("Output from Server .... \n");
			System.out.println(output);*/
			String requestString = GSON.toJson(ad);
			if (!requestString.equals(output)) {
				throw new RuntimeException();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}