package org.springframework.social.linkedin.api.impl;

import static org.springframework.social.linkedin.api.impl.LinkedInTemplate.BASE_URL;

import java.net.URI;

import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
import org.springframework.social.linkedin.api.ProfileField;
import org.springframework.social.linkedin.api.ProfileOperations;
import org.springframework.social.linkedin.api.SearchParameters;
import org.springframework.social.linkedin.api.SearchResult;
import org.springframework.social.linkedin.api.SearchResultWrapper;
import org.springframework.web.client.RestTemplate;

public class ProfileTemplate extends AbstractTemplate implements ProfileOperations {
	static {
		StringBuffer b = new StringBuffer();
		b.append(BASE_URL).append("{id}:(");
		boolean first = true;
		for (ProfileField f : ProfileField.values()) {
			switch (f) {
			case CONNECTIONS:
				break;
			default:
				if (first) {
					first = false;
				}
				else {
					b.append(',');
				}
				b.append(f);
			}
		}
		b.append(")?format=json");
		
		PROFILE_URL_FULL = b.toString();
	}
	private RestTemplate restTemplate;
	
	public ProfileTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	public String getProfileId() {
		return getUserProfile().getId();
	}

	public String getProfileUrl() {
		return getUserProfile().getPublicProfileUrl();
	}

	public LinkedInProfile getUserProfile() {
		return restTemplate.getForObject(PROFILE_URL, LinkedInProfile.class, "~");
	}
	
	public LinkedInProfileFull getUserProfileFull() {
		return restTemplate.getForObject(PROFILE_URL_FULL, LinkedInProfileFull.class, "~");
	}
	
	public LinkedInProfile getProfileById(String id) {
		return restTemplate.getForObject(PROFILE_URL, LinkedInProfile.class, "id=" + id);
	}
	
	public LinkedInProfile getProfileByPublicUrl(String url) {
		return restTemplate.getForObject(PROFILE_URL, LinkedInProfile.class, "url=" + url);
	}
	
	public LinkedInProfileFull getProfileFullById(String id) {
		return restTemplate.getForObject(PROFILE_URL_FULL, LinkedInProfileFull.class, "id=" + id);
	}
	
	public LinkedInProfileFull getProfileFullByPublicUrl(String url) {
		return restTemplate.getForObject(PROFILE_URL_FULL, LinkedInProfileFull.class, "url=" + url);
	}
	
	public SearchResult search(SearchParameters parameters) {
		SearchResultWrapper wrapper =  restTemplate.getForObject(expand(PEOPLE_SEARCH_URL, parameters), SearchResultWrapper.class);
		
		if (wrapper != null) {
			return wrapper.getResult();
		}
		return null;
	}
	
	private URI expand(String url, SearchParameters parameters) {
		Object[] variables = new Object[] {
				parameters.getKeywords(),
				parameters.getFirstName(),
				parameters.getLastName(),
				parameters.getCompanyName(),
				parameters.getCurrentCompany(),
				parameters.getTitle(),
				parameters.getCurrentTitle(),
				parameters.getSchoolName(),
				parameters.getCurrentSchool(),
				parameters.getCountryCode(),
				parameters.getPostalCode(),
				parameters.getDistance(),
				parameters.getStart(),
				parameters.getCount(),
				parameters.getSort()
		};
		return expand(url, variables, true);
	}
	
	static final String PROFILE_URL = BASE_URL + "{id}:(id,first-name,last-name,headline,industry,site-standard-profile-request,public-profile-url,picture-url,summary)?format=json";
	
	static final String PROFILE_URL_FULL;
	
	static final String PEOPLE_SEARCH_URL = "https://api.linkedin.com/v1/people-search:(people:(id,first-name,last-name,headline,industry,site-standard-profile-request,public-profile-url,picture-url,summary))?{&keywords}{&first-name}{&last-name}{&company-name}&{current-company}{&title}{&current-title}{&school-name}{&current-school}{&country-code}{&postal-code}{&distance}{&start}{&count}{&sort}";

}
