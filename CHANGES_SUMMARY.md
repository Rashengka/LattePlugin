# Changes Summary

## Security Update: Replaced json-simple with Gson

### Issue
The project was using the `com.googlecode.json-simple:json-simple:1.1.1` library, which has known security vulnerabilities. This library is outdated (last updated in 2012) and is no longer maintained.

### Solution
Replaced json-simple with Google's Gson library (`com.google.code.gson:gson:2.10.1`), which is actively maintained, more secure, and provides similar functionality.

### Changes Made
1. Updated `build.gradle` to replace the json-simple dependency with Gson:
   ```gradle
   // Old dependency
   implementation 'com.googlecode.json-simple:json-simple:1.1.1'
   
   // New dependency
   implementation 'com.google.code.gson:gson:2.10.1'
   ```

2. Updated `NettePackageDetector.java`:
   - Changed imports from json-simple to Gson
   - Updated JSON parsing code to use Gson's API
   - Updated exception handling to catch JsonSyntaxException instead of ParseException

3. Updated `LatteVersionDetector.java`:
   - Changed imports from json-simple to Gson
   - Updated JSON parsing code to use Gson's API
   - Updated exception handling to catch JsonSyntaxException instead of ParseException

### Key API Differences
- **Parsing JSON**: 
  - json-simple: `JSONParser parser = new JSONParser(); JSONObject json = (JSONObject) parser.parse(reader);`
  - Gson: `JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();`

- **Accessing Properties**:
  - json-simple: `JSONObject obj = (JSONObject) json.get("property"); if (obj != null) {...}`
  - Gson: `if (json.has("property")) { JsonObject obj = json.getAsJsonObject("property"); ... }`

- **Getting Values**:
  - json-simple: `Object value = json.get("key"); if (value != null) { String str = value.toString(); }`
  - Gson: `if (json.has("key")) { JsonElement value = json.get("key"); String str = value.getAsString(); }`

### Testing
All tests were run and passed successfully, confirming that the functionality is preserved with the new Gson dependency.

### Benefits
1. **Security**: Removed a dependency with known security vulnerabilities
2. **Maintenance**: Using an actively maintained library
3. **Performance**: Gson is generally more performant than json-simple
4. **Features**: Gson provides more features and better type handling