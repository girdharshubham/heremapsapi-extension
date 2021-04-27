# heremapsapi-extension

# How to run
```
docker-compose -f docker-compose.yml up -d
mongo "mongodb://root:changeme@localhost:27017"
use heremaps
db.links.createIndex({"location": "2dsphere"})
sbt impl/run
curl --location --request GET 'localhost:9000/route' \
--header 'Content-Type: application/json' \
--data-raw '{
    "start": {
        "latitude": -35.817900,
        "longitude": 145.0271960
    },
        "end": {
        "latitude": -35.817900,
        "longitude": 146.0271960
    }
}'
```
