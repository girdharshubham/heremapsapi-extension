# heremapsapi-extension

# How to run
```
docker-compose -f docker-compose.yml up -d
mongo "mongodb://root:changeme@localhost:27017"
use heremaps
db.links.createIndex({"coordinate": "2d"})
sbt impl/run
```