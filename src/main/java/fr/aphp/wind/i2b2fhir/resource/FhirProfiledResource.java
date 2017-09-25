package fr.aphp.wind.i2b2fhir.resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import fr.aphp.wind.i2b2fhir.config.ConfigFhirResource;
import fr.aphp.wind.i2b2fhir.i2b2set.DateSetImpl;
import fr.aphp.wind.i2b2fhir.i2b2set.EncounterSetImpl;
import fr.aphp.wind.i2b2fhir.i2b2set.InstanceSetImpl;
import fr.aphp.wind.i2b2fhir.i2b2set.PatientSetImpl;
import fr.aphp.wind.i2b2fhir.i2b2setlist.I2b2SetList;
import net.minidev.json.JSONArray;

public class FhirProfiledResource {
	final static Logger logger = Logger.getLogger(FhirProfiledResource.class);
	private String resourceName;
	private String profileResourceName;
	private String fhirApiHost;
	private Integer fhirApiPagination;
	private Integer entriesNumber = 0;
	private String fhirProxyHost;
	private int fhirProxyPort;
	private boolean hasProxy;
	private String i2b2SetType;
	private String fhirSearchQuery;
	private ConfigFhirResource configFhirResource = new ConfigFhirResource();
	private I2b2SetList i2b2SetList = new I2b2SetList();
	private Integer fhirDstuVersion;
	private Date happensBeforeDate;
	private Date happensAfterDate;
	private String  fhirTerminologyHost;
	private ArrayList<String> codes;

	public String getFhirTerminologyHost() {
		return fhirTerminologyHost;
	}

	public void setFhirTerminologyHost(String fhirTerminologyHost) {
		this.fhirTerminologyHost = fhirTerminologyHost;
	}

	public boolean isHasProxy() {
		return hasProxy;
	}

	public void setHasProxy(boolean hasProxy) {
		this.hasProxy = hasProxy;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getProfileResourceName() {
		return profileResourceName;
	}

	public void setProfileResourceName(String profileResourceName) {
		this.profileResourceName = profileResourceName;
	}

	public String toString() {
		return this.resourceName + this.profileResourceName;
	}

	public String getFhirApiHost() {
		return fhirApiHost;
	}

	public void setFhirApiHost(String fhirApiHost) {
		this.fhirApiHost = fhirApiHost;
	}

	public Integer getFhirApiPagination() {
		return fhirApiPagination;
	}

	public void setFhirApiPagination(Integer fhirApiPagination) {
		this.fhirApiPagination = fhirApiPagination;
	}

	public String getFhirProxyHost() {
		return fhirProxyHost;
	}

	public void setFhirProxyHost(String fhirProxyHost) {
		this.fhirProxyHost = fhirProxyHost;
	}

	public int getFhirProxyPort() {
		return fhirProxyPort;
	}

	public void setFhirProxyPort(int fhirProxyPort) {
		this.fhirProxyPort = fhirProxyPort;
	}

	public String getI2b2SetType() {
		return i2b2SetType;
	}

	public void setI2b2SetType(String i2b2SetType) {
		this.i2b2SetType = i2b2SetType;
	}

	public String getFhirSearchQuery() {
		return fhirSearchQuery;
	}

	public void setFhirSearchQuery(String fhirSearchQuery) {
		this.fhirSearchQuery = fhirSearchQuery;
	}

	public I2b2SetList getI2b2SetList() {
		return this.i2b2SetList;
	}

	public void setI2b2SetList(I2b2SetList i2b2SetList) {
		this.i2b2SetList = i2b2SetList;
	}

	public Integer getFhirDstuVersion() {
		return fhirDstuVersion;
	}

	public void setFhirDstuVersion(Integer fhirDstuVersion) {
		this.fhirDstuVersion = fhirDstuVersion;
	}

	public void setHappensBeforeDate(Date happensBeforeDate) {
		this.happensBeforeDate = happensBeforeDate;
	}

	public void setHappensAfterDate(Date happensAfterDate) {
		this.happensAfterDate = happensAfterDate;
	}
	
	public ArrayList<String> getCodes() {
		return codes;
	}

	public void setCodes(ArrayList<String> codes) {
		this.codes = codes;
	}

	private String createFhirSearchQuery() {
		String tmp = "";
		if(this.fhirSearchQuery != null){
		tmp += "&" + this.fhirSearchQuery ;  	
		}
		return tmp;
	}

	private String createDateFilterString() {
		String tmp = "";
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		;
		if (this.happensBeforeDate != null) {
			tmp += "&date=" + "lt" + fmt.print(new DateTime(this.happensBeforeDate));
		}
		if (this.happensAfterDate != null) {
			tmp += "&date=" + "gt" + fmt.print(new DateTime(this.happensAfterDate));
		}
		return tmp;
	}
	

	private String getElementQuery() {
		String str = "_elements=";
		if (this.i2b2SetType.equals("patientSet")) {
			str += this.configFhirResource.getPatientUriField();
		} else if (this.i2b2SetType.equals("encounterSet")) {
			str += this.configFhirResource.getPatientUriField();
			str += ",";
			str += this.configFhirResource.getEncounterUriField();

		} else if (this.i2b2SetType.equals("instanceSet")) {
			str += this.configFhirResource.getPatientUriField();
			str += ",";
			str += this.configFhirResource.getEncounterUriField();
			str += ",";
			str += this.configFhirResource.getInstanceUriField();
		} else if (this.i2b2SetType.equals("dateSet")) {
			str += this.configFhirResource.getPatientUriField();
			str += ",";
			str += this.configFhirResource.getEncounterUriField();
			str += ",";
			str += this.configFhirResource.getInstanceUriField();
			str += ",";
			str += this.configFhirResource.getDateField();
		}
		return str;
	}


	private ArrayList<String> getCodesExpanded() {
		ArrayList<String> tmp = new ArrayList<String>();
		for (String str : this.codes) {
			tmp.add(str); // we keep the original code
			tmp.addAll(getEquivalentCode()); //add all expanded equivalent
		}
		return tmp;
	}

	private ArrayList<String> getEquivalentCode() {
		ArrayList<String> tmp = new ArrayList<String>();
		//TODO: Make a call to ConceptMapping resource
		//getHttpCallAsString(query)
		return tmp;

	}
	
	private String createFhirCodeSearchQuery() {
		String tmp="";
		if(this.codes!=null){
			tmp+= String.format("&code=%s", String.join(",", this.codes));
		}
		return tmp;
	}
	
	private String getHttpCallAsString(String query){
		logger.error(query);
		String tmp = "";
		if (this.hasProxy) {
			Unirest.setProxy(new HttpHost(this.fhirProxyHost, this.fhirProxyPort));
		}
		try {
			tmp = Unirest.get(query)
			.header("accept", "application/json")
			.header("Content-Type", "application/json")
			.asString()
			.getBody()
			.toString();
			logger.error(tmp);
		} catch (UnirestException e) {
			logger.error(String.format("Query \"%s\" has failed: %s",query, e.getMessage() ));
			e.printStackTrace();

		}
		return tmp;
		
	}
	
	public void collectResult() throws UnirestException {
		configFhirPath();
	
		boolean hasResult = true;

		String query = this.fhirApiHost + this.profileResourceName + "?" 
		+ this.getElementQuery() 
		+ this.createFhirSearchQuery()
		+ this.createFhirCodeSearchQuery()
		+ this.createDateFilterString() 
		+ "&_count=" + this.fhirApiPagination 
		+ "&_pretty=false";
		
		long startTime = System.nanoTime();
		long endTime ;
		long duration ;
		
		while (hasResult) {
			logger.info(String.format("FHIR query: %s", query));
			

			String jsonResultString = getHttpCallAsString(query);

			endTime = System.nanoTime();
			duration = (endTime - startTime)/1000000; //divide by 1000000 to get milliseconds
			logger.error(String.format("got the string %d", duration));
			
			try{
			JSONArray entries = JsonPath.read(jsonResultString, "$.entry");
			for (Object entry : entries) {
				entriesNumber++;
				if (this.i2b2SetType.equals("patientSet")) {
					i2b2SetList.addResult(collectPatientList(entry));
				} else if (this.i2b2SetType.equals("encounterSet")) {
					i2b2SetList.addResult(collectEncounterList(entry));
				} else if (this.i2b2SetType.equals("instanceSet")) {
					i2b2SetList.addResult(collectInstanceList(entry));
				} else if (this.i2b2SetType.equals("dateSet")) {
					i2b2SetList.addResult(collectDateList(entry));
				}
			}
			}catch(Exception e){
				logger.info("no entries in the result");
			}
			

			query = getNextPagination(jsonResultString);
			if (query == null) {
				hasResult = false;
			}
			
		}
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000; //divide by 1000000 to get milliseconds
		logger.error(String.format("total fetch %d", duration));
	}



	private void configFhirPath() {

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = new ObjectMapper().readTree(new File("/opt/wildfly-10.0.0.Final/standalone/configuration/crcapp/fhir.conf"));
			JsonNode dstu = json.get("dstu" + this.fhirDstuVersion);
			JsonNode resource = dstu.get(this.resourceName);
			mapper.readerForUpdating(configFhirResource).readValue(resource);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private PatientSetImpl collectPatientList(Object entry) {
		PatientSetImpl r = new PatientSetImpl();
		try {
			String entryId = JsonPath.read(entry, this.configFhirResource.getPatientUriPath());
			r.setPatientUri(entryId.replaceAll("^.*/", ""));
		} catch (Exception e) {
			logger.error(String.format("Does not have any Patient id: [%s]", entry));
		}
		return r;
	}

	private EncounterSetImpl collectEncounterList(Object entry) {
		EncounterSetImpl r = new EncounterSetImpl();
		PatientSetImpl p = collectPatientList(entry);
		try {
			String encounterId = JsonPath.read(entry, this.configFhirResource.getEncounterUriPath());
			r.setPatientUri(p.getPatientUri());
			r.setEncounterUri(encounterId.replaceAll("^.*/", ""));
		} catch (Exception e) {
			logger.info(String.format("Does not have any Encounter id: [%s]", entry));
		}
		return r;
	}

	private InstanceSetImpl collectInstanceList(Object entry) {
		InstanceSetImpl r = new InstanceSetImpl();
		EncounterSetImpl en = collectEncounterList(entry);
		try {
			String instanceId = JsonPath.read(entry, this.configFhirResource.getInstanceUriPath());
			r.setPatientUri(en.getPatientUri());
			r.setEncounterUri(en.getEncounterUri());
			r.setInstanceUri(instanceId.replaceAll("^.*/", ""));
		} catch (Exception e) {
			logger.info(String.format("Does not have any Instance id: [%s]", entry));
		}
		return r;
	}

	private DateSetImpl collectDateList(Object entry) {
		InstanceSetImpl i = collectInstanceList(entry);
		DateSetImpl r = new DateSetImpl();
		String instanceId = null;
		try {
			instanceId = JsonPath.read(entry, this.configFhirResource.getDatePath());
			r.setPatientUri(i.getPatientUri());
			r.setEncounterUri(i.getEncounterUri());
			r.setInstanceUri(i.getInstanceUri());
			// workaround datetime: problem of date encoding
			r.setDate(instanceId.substring(0, 19));
		} catch (Exception e) {
			logger.error(String.format("Does not have any Instance id: [%s]", instanceId.toString()));
		}
		return r;
	}

	private String getNextPagination(String jsonResultString) {
		String result = null;
		JSONArray links = JsonPath.read(jsonResultString, "$.link");
		for (Object link : links) {
			String relType = JsonPath.read(link, "$.relation");
			if (relType.equals("next")) {
				result = JsonPath.read(link, "$.url");
				break;
			}
		}

		return result;
	}

	public Integer getEntriesNumber() {
		return this.entriesNumber;
	}

	public String toCsv() {
		return this.i2b2SetList.toCsv();
	}

}