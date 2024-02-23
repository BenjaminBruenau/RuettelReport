
### Right now the Analysis service only works for geojson Features for Earthquake Events like this one:
```json
{
 "geometry": {
     "coordinates": [
         178.0089,
         51.9332,
         123.1
     ],
     "type": "Point"
 },
 "id": "ak023bmf0kuh",
 "properties": {
     "alert": null,
     "cdi": null,
     "code": "023bmf0kuh",
     "detail": "https://earthquake.usgs.gov/fdsnws/event/1/query?eventid=ak023bmf0kuh&format=geojson",
     "dmin": null,
     "felt": null,
     "gap": null,
     "ids": ",ak023bmf0kuh,",
     "mag": 2.3,
     "magType": "ml",
     "mmi": null,
     "net": "ak",
     "nst": null,
     "place": "Rat Islands, Aleutian Islands, Alaska",
     "rms": 0.82,
     "sig": 81,
     "sources": ",ak,",
     "status": "reviewed",
     "time": 1694304508259,
     "title": "M 2.3 - Rat Islands, Aleutian Islands, Alaska",
     "tsunami": 0,
     "type": "earthquake",
     "types": ",origin,phase-data,",
     "tz": null,
     "updated": 1695422986077,
     "url": "https://earthquake.usgs.gov/earthquakes/eventpage/ak023bmf0kuh"
 },
 "type": "Feature"
}
```
 

# DataFrames are grouped by tenants and users (to only take their aggregations for queried data into account for e.g. visualization)

+------------+----------+--------------------+
|    tenantId|    userId|               value|
+------------+----------+--------------------+
|test-tenant2|marcooo123|{"geometry":{"coo...|
|test-tenant2|marcooo123|{"geometry":{"coo...|
|test-tenant2|marcooo123|{"geometry":{"coo...|
|test-tenant2| andiii123|{"geometry":{"coo...|
|test-tenant2| andiii123|{"geometry":{"coo...|
|test-tenant2| andiii123|{"geometry":{"coo...|
| test-tenant|  benni123|{"geometry":{"coo...|
| test-tenant|  benni123|{"geometry":{"coo...|
| test-tenant|  benni123|{"geometry":{"coo...|
| test-tenant|   joel123|{"geometry":{"coo...|
| test-tenant|   joel123|{"geometry":{"coo...|
| test-tenant|   joel123|{"geometry":{"coo...|
| test-tenant|   joel123|{"geometry":{"coo...|
| test-tenant|   joel123|{"geometry":{"coo...|
| test-tenant|   joel123|{"geometry":{"coo...|
| test-tenant|  maggo123|{"geometry":{"coo...|
| test-tenant|  maggo123|{"geometry":{"coo...|
| test-tenant|  maggo123|{"geometry":{"coo...|
| test-tenant|  maggo123|{"geometry":{"coo...|
| test-tenant|  maggo123|{"geometry":{"coo...|
+------------+----------+--------------------+


+------------+----------+-----+------------------+--------------------+
|    tenantId|    userId|count|     avg_magnitude|high_magnitude_count|
+------------+----------+-----+------------------+--------------------+
| test-tenant|  benni123|    6|1.6433333433333335|                   0|
| test-tenant|  maggo123|    6|1.6433333433333335|                   0|
| test-tenant|   joel123|    6|1.6433333433333335|                   0|
|test-tenant2|marcooo123|    3|1.6433333433333333|                   0|
|test-tenant2| andiii123|    3|1.6433333433333333|                   0|
+------------+----------+-----+------------------+--------------------+


+------------+----------+---+---+---+---+----+
|    tenantId|    userId|0-2|2-4|4-6|6-8|8-10|
+------------+----------+---+---+---+---+----+
| test-tenant|  benni123|  6|  0|  0|  0|   0|
| test-tenant|  maggo123|  6|  0|  0|  0|   0|
| test-tenant|   joel123|  6|  0|  0|  0|   0|
|test-tenant2|marcooo123|  3|  0|  0|  0|   0|
|test-tenant2| andiii123|  3|  0|  0|  0|   0|
+------------+----------+---+---+---+---+----+