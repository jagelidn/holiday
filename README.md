## Holiday Service
Service for retrieval of holiday information. Running Java 21 with Micronaut & Gradle.

See overview of supported countries here https://date.nager.at/Country/Coverage. 

## Run application
#### Build

```
./gradlew clean build
```

#### Unit Testing

```
./gradlew clean test
```

#### Run
Application will run at http://localhost:8080
```
MICRONAUT_ENVIRONMENTS=application.yaml ./gradlew run 
```

## Endpoints

### /holiday/country/{countryName}/recent

---
##### ***GET***
**Description:** Given a country name, return the last celebrated 3 holidays (date and english name).

**Parameters**

| Name | Located in | Description | Required | Schema |
| --- | ---------- | ----------- | -------- | ---- |
| countryName | path |  | Yes | string |


**Responses**

| Code | Description               | Schema              |
|------|---------------------------|---------------------|
| 200  | Successful response       | List < HolidayDto > |
| 400  | wrong input data          |                     |
| 404  | no result found for input |                     |


### /holiday/year/{year}/weekdayholidays

---
##### ***GET***
**Description:** Given a year and country codes, for each country code returns public holidays not falling on weekends.
Weekend is defined as saturday or sunday. Sorted in descending order by date.

**Parameters**

| Name      | Located in | Description                                                     | Required | Schema       |
|-----------|------------|-----------------------------------------------------------------| -------- |--------------|
| year      | path       | Year under examination  (1974 - 2074)                                         | Yes | int          |
| countries | query      | List of ISO-3166-2 country codes. Empty list gives empty result | Yes | List<String> |

**Responses**

| Code | Description               | Schema                        |
|------|---------------------------|-------------------------------|
| 200  | Successful response       | List< CountryHolidayListDto > |
| 400  | wrong input data          |                               |

### /holiday/year/{year}/commonholidays/{countrycode1}/{countrycode2}

---
##### ***GET***
**Description:** Given a year and 2 country codes, returns the deduplicated list of dates celebrated in both countries.
Country codes 1 & 2 cannot be the same.

**Parameters**

| Name         | Located in | Description                                                   | Required | Schema |
|--------------|------------|---------------------------------------------------------------| -------- |--------|
| year         | path       | Year under examination   (1974 - 2074)                                      | Yes | int    |
| countrycode1 | path       | ISO-3166-2 country code | Yes | String |
| countrycode2 | path       | ISO-3166-2 country code | Yes | String |


**Responses**

| Code | Description               | Schema                      |
|------|---------------------------|-----------------------------|
| 200  | Successful response       | List < DateHolidayListDto > |
| 400  | wrong input data          |                             |


### Models

---

#### HolidayDto

| Name | Type   | Description          |
|------|--------|----------------------|
| name | string | english holiday name | 
| date | string | yyyy-mm-dd           | 


==============================

#### DateHolidayListDto

| Name       | Type           | Description |
|------------|----------------| ----------- |
| date       | string         |  yyyy-mm-dd |
| localNames | List< string > | country specific name |

==============================

#### CountryHolidayListDto

| Name          | Type               | Description | 
|---------------|--------------------| ----------- |
| countryCode   | string             |  ISO-3166-2 country code|
| holidays      | List< HolidayDto > | 

==============================