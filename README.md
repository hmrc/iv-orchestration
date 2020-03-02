
# iv-orchestration

This service checks if users are authorised and allowed to create and retrieve session data.
To run the application locally on port 9276 - endpoints are documented below.

sbt run

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").

# Api endpoints

| URI                              | Http Method |Description                  |Request Body                                                           | Response Body                                                             | Statuses       |
|:---------------------------------|:------------|:----------------------------|:----------------------------------------------------------------------|---------------------------------------------------------------------------|----------------|
|/iv-orchestration/iv-sessiondata  |POST         |Create a new session data    |[IvSessionData](#user-content-ivsessiondata)                           |                                                                           |201,401,500     |
|/iv-orchestration/session/search  |POST         |Return session data response |[IvSessionDataSearchRequest](#user-content-ivsessiondatasearchrequest) |[IvSessionDataSerchDataResponse](#user-content-ivsessiondatasearchresponse)|201,401,500     |

## JSON

Note that `nino`, `loginTimes`, `credentialStrength`, `postCode`, `firstName`, `lastName`, `dateOfBirth` and `ivFailureReason` are optional fields.

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
      "ivFailureReason": "User failed IV"
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
      "ivFailureReason": "User failed IV"
    }
]
```
