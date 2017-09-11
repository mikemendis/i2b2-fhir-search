package fr.aphp.wind.fhir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

import com.mashape.unirest.http.exceptions.UnirestException;

import fr.aphp.wind.i2b2fhir.i2b2set.I2b2Set;
import fr.aphp.wind.i2b2fhir.i2b2setlist.I2b2SetList;
import fr.aphp.wind.i2b2fhir.resource.FhirProfiledResource;
import fr.aphp.wind.i2b2fhir.resource.FhirProfiledResourceBuilder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ProfiledFhirResourceTest extends TestCase {
	final static Logger logger = Logger.getLogger(TestCase.class);

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public ProfiledFhirResourceTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ProfiledFhirResourceTest.class);
	}

	public void testPatient() throws UnirestException, FileNotFoundException, IOException {
		FhirProfiledResource prf = new FhirProfiledResourceBuilder()
				.withFhirApiHost("http://fhirtest.uhn.ca/baseDstu3/")
				.withFhirDstuVersion(3)
				.withProfileResourceName("Patient")
				.withResourceName("Patient")
				.withFhirSearchQuery("birthdate=1974-12-24")
				.withI2b2SetType("patientSet")
				.build();
		prf.collectResult();

		logger.info(String.format("[Patient] %s", prf.toCsv()));
		I2b2SetList a = prf.getI2b2SetList();
		for( I2b2Set t : a){
			logger.error(t.getPatientUri());
			
		}
	}
	
	public void testEncounter() throws UnirestException, FileNotFoundException, IOException {
		Date date = null;
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
		   date =  formatter.parse("2014-05-28 11:22:37");
		} catch (ParseException e) {
		  e.printStackTrace();
		}
		FhirProfiledResource prf = new FhirProfiledResourceBuilder()
				.withFhirApiHost("http://fhirtest.uhn.ca/baseDstu3/")
				.withFhirDstuVersion(3)
				.withProfileResourceName("Encounter")
				.withResourceName("Encounter")
			    .withFhirSearchQuery("status=finished")
				.withHappensBeforeDate(date)
				.withHappensAfterDate(date)
				.withI2b2SetType("encounterSet")
				.build();
		prf.collectResult();

		logger.info(String.format("[Encounter] %s", prf.toCsv()));
		for( I2b2Set encSet : prf.getI2b2SetList()){
            System.out.println(encSet.getPatientUri());
            System.out.println(encSet.getEncounterUri());

    }

	}
	
	public void testObservation() throws UnirestException, FileNotFoundException, IOException {
		FhirProfiledResource prf = new FhirProfiledResourceBuilder()
				.withFhirApiHost("http://fhirtest.uhn.ca/baseDstu3/")
				.withFhirDstuVersion(3)
				//.withFhirProxyHost("xxx")
				//.withFhirProxyPort(9090)
				//.withProfileResourceName("Observation")
				.withResourceName("Observation")
				.withFhirSearchQuery("code=29463-7&value-quantity=21")
				.withI2b2SetType("dateSet") .build();
		prf.collectResult();

		logger.error(String.format("[Observation] %s", prf.toCsv()));
	}
}
