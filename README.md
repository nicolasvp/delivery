# Delivery service

Delivery service for phrases project, this microservice do the work of matching phrases to any user and delivery to each one.

### Prerequisites

To run this microservice use the following maven environment variables:

```
-DconfigUrl
-Dspring.profiles.active
```

Examples:

```
Localhost url
-DconfigUrl=http://localhost:1111

Docker container url
-DconfigUrl=http://config-server:1111

Spring active profile for DEV
-Dspring.profiles.active=dev

Sring active profile for PROD
-Dspring.profiles.active=prod
```