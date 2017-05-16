# products-v1 - Restful service for MyRetail

Rest based api that gets the product & pricing information /updates the pricing information for a given product


## Tech Stack

JDK 1.8
Spring boot
Mongo DB
Mockito
Docker
Maven
Eclipse IDE

## Prerequisites

### Installing Mongodb & data setup

1. Install MongoDB to your local machine
```
curl -O https://fastdl.mongodb.org/osx/mongodb-osx-x86_64-3.4.4.tgz
tar -zxvf mongodb-osx-x86_64-3.4.4.tgz
mkdir -p mongodb
cp -R -n mongodb-osx-x86_64-3.4.4/ mongodb
export PATH=<mongodb-install-directory>/bin:$PATH
```
2. Provide executable permissions to /data/db directory
```
sudo chmod 777 /data/db
```
3. Start the mongo service and open the mongo shell in a different terminal
```
sudo mongod
mongo
```
4.Insert data for datasetup
```
db.createCollection("PRICING)
db.PRICING.insert({ productId: '13860428', currentPrice : 19.99, currencyCode :'USD' })
db.PRICING.insert({ productId: '16696652', currentPrice : 33.75, currencyCode :'USD' })

```


### Installing products-v1 RESTFUl service

1. Clone this repo in your local and import the project in eclipse
2. Build and run the test cases
```
mvn clean package
```
3. To run the RESTful service 
```
mvn spring-boot:run
```

### Swagger Spec

The swagger spec can be found in 

```
http://localhost:8080/swagger-ui.html
```
Alternatively, it is also available in the path (/products-v1/specs/products-v1.json)

### Execution and validation

1.Open POSTMAN or Chrome
2.Invoke GET Service
	
```
GET /products/v1/13860428?key=1234 HTTP/1.1

```

3.Invoke POST Service

```
PUT /products/13860428?key=1234 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
{
  "name": "The Big Lebowski (Blu-ray)",
  "id": "13860428",
  "current_price": {
    "value": 19.92,
    "currency_code": "USD"
  }
}
```

### Reports

#### Unit test results

The unit test results can be found in the path
```
/products-v1/target/site/pmd.html
```
#### Static analysis report

The PMD analysis report can be found in the path
```
/products-v1/target/site/pmd.html
```

#### Author

Navaneethan S