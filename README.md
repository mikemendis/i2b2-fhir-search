# i2b2 fhir search

## Goal

This demo retrieves patientSet, encounterSet, instanceSet or dateSet from a remote FHIR API. Here the HAPI FHIR demo web service is used for testing. In the future, an hapi FHIR instance is supposed to be plugged on top of any other datawarehouse, live EMR and so on

## Feature

- can configure the fhir API HOST
- can configure the resource fields
- can configure the number of pagination
- loop over the pagination to get all results
- Fact represents Observation, Medication, ... and so on
- does not store the full result in memory; but each pagination instead
- the query needs to be pass as a string
- the way to retrieve the fields (patient/encounter/fact ids), is done by jsonPath, and is configurable by the client.
- warns when a field (eg: enconter in Observation) is not present in a resource
- compatible FHIR v3; easy to make it compatible with future version
- compatible with standard resources & profiled resources
- only retrieves fields for sets (not the full resource)

## Use

This uses the Builder design pattern :

```java
import fr.aphp.wind.i2b2fhir.*

FhirProfiledResource prf = new FhirProfiledResourceBuilder()
.withFhirApiHost("http://fhirtest.uhn.ca/baseDstu3/")//Required ; from i2b2 hive configuration?
.withFhirDstuVersion(3)//Optionnal, 3 is default
.withFhirProxyHost("my.proxy")//Optionnal
.withFhirProxyPort(myport)//Optionnal
.withResourceName("Observation")// Required
.withHappensBeforeDate(java.util.Date) // Optionnal; filter based on date
.withHappensAfterDate(java.util.Date) // Optionnal; filter based on date
.withProfileResourceName("ObservationAphp")//Optionnal - if using a profiled resource
.withFhirSearchQuery("code=29463-7&value-quantity=21")//OPTIONNAL
.withI2b2SetType("dateSet")//one of [patientSet, encounterSet, instanceSet, dateSet]
.build();
prf.collectResult();

System.out.println(String.format("[Observation] %s", prf.toCsv()));//produce a csv to be loaded into i2b2 temporary table on the fly

I2b2SetList myResultSet = prf.getI2b2SetList(); //produce an array list
for( I2b2Set encSet : myResultSet){//iterate over the result set
	System.out.println(encSet.getPatientUri());
	System.out.println(encSet.getEncounterUri());

}
```

## Example

```java
Date date = null;
DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
try {
	date =  formatter.parse("2014-05-28");
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
//2017-09-09 09:16:25 INFO FhirProfiledResource:207 - FHIR query: http://fhirtest.uhn.ca/baseDstu3/Encounter?&_element=subject,identifier&status=finished&date=lt2014-05-28T00:00:00.000+02:00&date=gt2014-05-28T00:00:00.000+02:00&_count=500&_pretty=false
//77274
//77302
//189211
//189239
//187309
//187354
//174603
//174665
//155637
//155711
//155009
//155072
//a9dcaa46-91eb-4882-a915-276af2280c46
//f3d51fc2-063d-46b9-a43d-ae1db2a83a86
//152090
//152167
//5ad64836-8a7f-4484-a8a1-9e3b2e875ac2
//fda84825-44d1-4aba-8011-9b2ff3bb6e30
//d2280b9b-7e4d-4995-a321-9dfdf7810dbc
//f4ac6a8b-5a28-4b02-8864-7c5b8ec11aa0
//522fb4c3-ec64-4b17-8736-817bd9fd72d8
//590d92c6-e29f-435a-a90f-b0dd99ccce93
```


## Pakage:

- mvn assembly:single -DdescriptorId=jar-with-dependencies

## Install

- mvn clean install
- import in eclipse as maven project
- run within eclipse

## test

- mvn test


## TODO

- Exceptions
- date formating
