# MongoDB Geospatial Index Fix for Vendor Matching

## Problem
The error `NoQueryExecutionPlans: unable to find index for $geoNear query` occurs because MongoDB requires a `2dsphere` index on the `business_address.gps_coordinates` field for geospatial queries using `$nearSphere`.

## Solution Options

### Option 1: Create Index via MongoDB Compass (RECOMMENDED)
1. Open **MongoDB Compass**
2. Connect to your database: `mongodb+srv://ac-4erzjiy-shard-00-01.1ot9fox.mongodb.net`
3. Select database: `bidzaro_catering_platform_db`
4. Select collection: `vendors`
5. Go to **Indexes** tab
6. Click **Create Index**
7. Enter this JSON:
   ```json
   {
     "business_address.gps_coordinates": "2dsphere"
   }
   ```
8. Click **Create Index**

### Option 2: Create Index via mongosh
Run this command in your MongoDB shell:
```bash
mongosh "mongodb+srv://your-connection-string"

use bidzaro_catering_platform_db

db.vendors.createIndex(
  { "business_address.gps_coordinates": "2dsphere" },
  { name: "business_address_gps_coordinates_2dsphere" }
)
```

### Option 3: Create Index via MongoDB Atlas UI
1. Log in to **MongoDB Atlas** (https://cloud.mongodb.com/)
2. Navigate to your cluster
3. Click **Collections**
4. Select `bidzaro_catering_platform_db` database
5. Select `vendors` collection
6. Click **Indexes** tab
7. Click **CREATE INDEX**
8. In the Fields section, add:
   - Field: `business_address.gps_coordinates`
   - Type: `2dsphere`
9. Click **Review**
10. Click **Create Index**

### Option 4: Automated Index Creation (Already Implemented)
The application now has `MongoIndexConfig.java` that automatically creates this index on startup.
However, this requires write permissions on the MongoDB database.

## Verification
After creating the index, verify it exists:

### Via MongoDB Compass:
- Go to Indexes tab in vendors collection
- You should see an index named `business_address_gps_coordinates_2dsphere` or similar

### Via mongosh:
```javascript
db.vendors.getIndexes()
```

Look for an entry like:
```json
{
  "v": 2,
  "key": {
    "business_address.gps_coordinates": "2dsphere"
  },
  "name": "business_address_gps_coordinates_2dsphere"
}
```

## Test the Fix
After creating the index, test the vendor matching endpoint:

```bash
POST http://localhost:8080/api/v1/cart/match
Content-Type: application/json

{
  "masterItemIds": ["item-id-1", "item-id-2"],
  "latitude": 17.440081,
  "longitude": 78.348915,
  "radiusKm": 50
}
```

The error should be resolved and you should get vendor matches.

## Why This Happens
- MongoDB's `$nearSphere` operator requires a geospatial index to optimize proximity searches
- The `@GeoSpatialIndexed` annotation in Spring Data MongoDB doesn't always automatically create indexes on embedded fields
- Manual index creation or programmatic creation at startup ensures the index exists

## Note
If you're using MongoDB Atlas Free Tier (M0), there might be limitations on:
- Number of indexes (max 5 for free tier)
- Index creation permissions

If you hit these limits, consider upgrading your cluster or removing unused indexes.
