
# iv-orchestration

This is a placeholder README.md for a new repository

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").


To run the application locally, the following parameter is needed to allow for the generation of absolute urls.

sbt run

# api endpoints

## API Endpoints
| URI                              | Http Method |Description                  |Request Body                                                           | Response Body                                                             | Statuses       |
|:---------------------------------|:------------|:----------------------------|:----------------------------------------------------------------------|---------------------------------------------------------------------------|----------------|
|/iv-orchestration/iv-sessiondata  |POST         |Create a new session data    |[IvSessionData](#user-content-ivsessiondata)                           |                                                                           |201,401,500
|/iv-orchestration/session/search  |POST         |Return session data response |[IvSessionDataSearchRequest](#user-content-ivsessiondatasearchrequest) |[IvSessionDataSerchDataResponse](#user-content-ivsessiondatasearchresponse)|201,401,500

## JSON

### IvSessionData

```json
[
    {
      "credId": "444",
      "nino": "123455",
      "confidenceLevel": 200,
      "loginTimes": "2019-08-27",
      "credentialStrength": "123",
      "itmpAddress": {
        "line1": "5 Street",
        "line2": "Worthing",
        "line3": "West Sussex",
        "postCode": "BN13 3AS",
        "countryName": "England",
        "countryCode": "44"
      },
      "postCode": "BN13 3AS",
      "firstName": "Matt",
      "lastName": "Groom",
      "dateOfbirth": "2019-08-27"
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
      "itmpAddress": {
        "line1": "5 Street",
        "line2": "Worthing",
        "line3": "West Sussex",
        "postCode": "BN13 3AS",
        "countryName": "England",
        "countryCode": "44"
      },
      "postCode": "BN13 3AS",
      "firstName": "Matt",
      "lastName": "Groom",
      "dateOfbirth": "2019-09-12"
    }
]
```