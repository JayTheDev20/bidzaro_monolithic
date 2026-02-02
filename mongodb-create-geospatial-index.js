g// MongoDB Index Creation Script
// Run this in MongoDB Compass or mongosh if automatic index creation fails

// Connect to your database
use bidzaro_catering_platform_db;

// Create 2dsphere index on vendors collection for geospatial queries
db.vendors.createIndex(
  { "business_address.gps_coordinates": "2dsphere" },
  { name: "business_address_gps_coordinates_2dsphere" }
);

// Verify the index was created
db.vendors.getIndexes();

// Test the index with a sample query (replace coordinates with actual values)
db.vendors.find({
  "status": "ACTIVE",
  "approval_status": "APPROVED",
  "business_address.gps_coordinates": {
    $nearSphere: {
      $geometry: {
        type: "Point",
        coordinates: [78.348915, 17.440081] // [longitude, latitude]
      },
      $maxDistance: 50000 // 50km in meters
    }
  }
});

console.log("Geospatial index created successfully!");
