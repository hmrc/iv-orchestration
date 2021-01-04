
# iv-orchestration

## Business Context
A new solution was required to enable DWP to utilise HMRC’s Identity Verification solution. The primary driver was to remove DWP's dependency on Verify for identity verification (IV) by leveraging HMRC IV capabilities. IV itself is required as one of the conditions for migrating users to Universal Credit.
The main business benefits of the new proposed solution were defined as follows:
* Reduced on-line cost per IV (assuming the HMRC charge-out model is less than the current Verify cost)
* Reduced risk of cost increases and/or service interruptions from Verify going private in March 2020
* Reduced face-to-face IV costs (assuming increased success rates)

Note that whilst the initial user of this solution is DWP, the intent is that this service can be utilised for any OGD (‘Other Government Department’).

## User Goal
The target user set is individuals only and it will provide the following user benefits:
* Avoids having to complete IV again if they've already successfully passed HMRC IV
* Improved on-line IV success rates means the user can avoid doing IV face to face in a job centre

## Architectural Flow
The high level journey is provided as follows:
* The user starts in DWP's Universal Credit (UC) service
* If they select to prove their identity with HMRC they are directed to sign in to government gateway or create a gg account
* They are then redirected to iv-orchestration-frontend  that will facilitate IV and enable the collection of IV data 
    * If the user has already successfully IV’d then the relevant info will be collected and stored in a session record in this service
    * If the user has not been IV’d before or does not have the required confidence level then they are redirected into the main IV frontend to go through IV - once complete then they’ll be returned to iv-orchestration-frontend and the relevant info collected and stored in a session record in this service
* When completed (either pass or fail), the user is returned to DWP's UC service along with a journey ID so that DWP can request the session record data
* DWP call this service via the API platform to get the session record data
DWP apply their business logic to the returned values to finalise setting up the user’s UC account

# Service Overview
A service to store an iv orchestration journey and enable an OGD to retrieve the outcome of an IV uplift journey via the API platform (currently only used by DWP).

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

# Api endpoints

| URI                              | Http Method |Description                  |Request Body                                                           | Response Body                                                             | Statuses       |
|:---------------------------------|:------------|:----------------------------|:----------------------------------------------------------------------|---------------------------------------------------------------------------|----------------|
|/iv-orchestration/iv-sessiondata  |POST         |Create a new session data    |[IvSessionData](#user-content-ivsessiondata)                           |                                                                           |201,401,500     |
|/iv-orchestration/session/search  |POST         |Return session data response |[IvSessionDataSearchRequest](#user-content-ivsessiondatasearchrequest) |[IvSessionDataSerchDataResponse](#user-content-ivsessiondatasearchresponse)|201,401,500     |

## JSON

Note that `nino`, `loginTimes`, `credentialStrength`, `postCode`, `firstName`, `lastName`, `dateOfBirth`, `evidencesPassedCount` and `ivFailureReason` are optional fields.

### IvSessionData

```json
[
    {
      "credId": "444",
      "nino": "123455",
      "confidenceLevel": 200,
      "loginTimes": "2019-08-27",
      "credentialStrength": "123",
      "postCode": "AA12 3BB",
      "firstName": "Jim",
      "lastName": "Smith",
      "dateOfbirth": "2019-08-27",
      "affinityGroup": "Individual",
      "ivFailureReason": "User failed IV",
      "evidencesPassedCount": 1
    }
]
```

### IvSessionDataSearchRequest

```json
[
    {
      "journeyId": "85de3050-9d43-4363-a92f-258d2657ea8b",
      "credId": "333"
    }
]
```

### IvSessionDataSerchDataResponse

```json
[
    {
      "nino": "123455",
      "confidenceLevel": 200,
      "loginTimes": "2019-09-12",
      "credentialStrength": "123",
      "postCode": "AB12 3BC",
      "firstName": "Jim",
      "lastName": "Smith",
      "dateOfbirth": "2019-09-12",
      "affinityGroup": "Individual",
      "ivFailureReason": "User failed IV",
      "evidencesPassedCount": 1
    }
]
```

## Test Repositories

The iv orchestration service is tested by the [dwp iv ui tests](https://github.com/hmrc/dwp-iv-ui-tests). If any changes are made to this service please run those tests before raising a PR. Information on how to run the tests are located in the respective repository readme.
