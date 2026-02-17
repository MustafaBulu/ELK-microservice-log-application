# Kibana Dashboard Setup

## 1) Create Data View

1. Open Kibana: `http://localhost:5601`
2. Go to `Stack Management -> Data Views`
3. Create data view:
   - Name: `microservices-logs`
   - Index pattern: `microservices-logs-*`
   - Time field: `@timestamp`

## 2) Useful KQL Queries

- All auth logs:
  - `service_name:"auth-service"`
- Failed auth attempts:
  - `service_name:"api-gateway" and message:"Unauthorized"`
- User registered events (consumer side):
  - `service_name:"user-service" and message:"Consumed user registered event"`
- Dead-letter events:
  - `service_name:"user-service" and message:"Event moved to DLT"`

## 3) Suggested Dashboard Panels

1. `Logs over time`:
   - Visualization: Line
   - X-axis: `@timestamp` (date histogram)
   - Breakdown: `service_name`
2. `Unauthorized count`:
   - Visualization: Metric
   - Filter: `message:"Unauthorized"`
3. `User registration events`:
   - Visualization: Metric
   - Filter: `message:"Published user registration event" or message:"Consumed user registered event"`
4. `DLT events`:
   - Visualization: Data table
   - Filter: `message:"Event moved to DLT"`
   - Columns: `@timestamp`, `service_name`, `message`

## 4) Quick Verification

1. Register a user from `auth-service`.
2. Trigger protected endpoint with and without JWT.
3. Confirm logs arrive in Discover and panels update.
