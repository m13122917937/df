# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a RuoYi-based management system (若依管理系统) - a Spring Boot application for e-commerce order and financial management. The system handles orders, IMEI tracking, express logistics, billing/payments, and user/company management.

**Key Technologies:**
- Java 11, Spring Boot 2.6.15, MyBatis-Plus 3.5.3
- Redis for caching, JWT for authentication
- RocketMQ/Alibaba ONS for messaging
- Knife4j/Swagger for API documentation
- Hutool, EasyExcel, MapStruct for utilities

## Project Structure

This is a multi-module Maven project with the following key modules:

### Core Modules
- **ruoyi-admin** - Backend admin application (port 7772)
  - Main class: `com.ruoyi.AdminApplication`
  - Entry point for backend management system
- **ruoyi-api** - API application for external integrations
  - Main class: `com.ruoyi.ApiApplication`
  - Supports async operations
- **ruoyi-framework** - Core framework configurations
  - Security config (JWT + Spring Security)
  - Redis config, MyBatis-Plus config
  - Global exception handler, interceptors
  - AOP aspects for logging and data scope
- **ruoyi-common** - Shared utilities and constants
- **ruoyi-system** - System management (users, roles, permissions)
- **ruoyi-quartz** - Scheduled task management

### Business Modules
- **ruoyi-order** - Order management
  - Orders, IMEI tracking, hanging orders, trade orders
  - Express logistics integration (route subscriptions)
  - Rule engine for order processing
  - Pattern: `facade/impl` → `manager` → `mapper` → `domain`
- **ruoyi-finance** - Financial management
  - Bills, payment plans, deductions, transactions
  - Payer management and configurations
- **ruoyi-user** - User and company management
  - Users, companies, company banks
  - Capital management, payments
  - Product SKU management
- **ruoyi-generator** - Code generation tools
- **ruoyi-starter** - Custom Spring Boot starters
  - `spring-boot-starter-kuaidi100` - Express 100 API integration
  - `spring-boot-starter-oss` - Aliyun OSS integration
  - `spring-boot-starter-wangdian` - WangDian API integration
  - `spring-boot-starter-sn` - Shopee integration

### Frontend
- **ruoyi-ui** - Vue.js frontend (separate directory)
  - Run with: `npm run dev` (default port 80)
  - Build with: `npm run build:prod` or `npm run build:stage`

## Architecture Patterns

### Layered Architecture (Business Modules)
```
Controller (if present) → Facade → Manager → Mapper → Domain
```

- **Facade** - Service interface layer (in `/facade/` and `/facade/impl/`)
- **Manager** - Business logic layer (handles transactions, complex operations)
- **Mapper** - MyBatis-Plus data access layer
- **Domain** - Entity classes mapped to database tables
- **BO** - Business Objects (data transfer between layers)
- **Param** - Request parameters
- **Query** - Query criteria objects
- **DTO** - Data Transfer Objects (often for aggregation queries)
- **Convert** - MapStruct converters (e.g., `OrderCov`, `BillCov`)

### Naming Conventions
- Domain entities: `Order`, `Bill`, `User`
- Managers: `OrderManager`, `BillManager`
- Facades: `IOrderFacade` / `OrderFacadeService`
- Mappers: `OrderMapper`
- Converters: `{Entity}Cov` (e.g., `OrderCov`)
- Constants: `{Entity}Consts` or `{Category}Consts`

## Build and Deployment

### Maven Build
```bash
# Build all modules
mvn clean install

# Build without tests
mvn clean install -Dmaven.test.skip=true

# Build with specific local repository
mvn clean install -B -Dmaven.repo.local="D:\software\maven\rep" -Dmaven.test.skip=true
```

### Build and Deploy to Docker (Windows Scripts)
Located in `bin/` directory:

- **bin/ruoyi-admin.bat** - Builds and deploys admin application to Huawei Cloud SWR
- **bin/ruoyi-api.bat** - Builds and deploys API application to Huawei Cloud SWR

These scripts:
1. Run Maven build
2. Build Docker image (requires Dockerfile in respective module directory)
3. Tag image for Huawei Cloud SWR registry
4. Push to registry
5. Clean up Docker system

Configuration parameters are at the top of each `.bat` file (REGION, PROJECT_NAMESPACE, IMAGE_TAG).

### Running Applications
```bash
# Run admin application (from ruoyi-admin directory)
mvn spring-boot:run

# Run API application (from ruoyi-api directory)
mvn spring-boot:run
```

## Configuration

### Application Profiles
- **application.yml** - Main configuration
- **application-dev.yml** - Development environment
- **application-test.yml** - Test environment
- **application-prod.yml** - Production environment (if exists)

Active profile is set via `spring.profiles.active` in application.yml (default: `dev`)

### Key Configuration Locations
- **ruoyi-admin/src/main/resources/application.yml** - Admin app config
- **ruoyi-api/src/main/resources/application.yml** - API app config
- Database: MyBatis-Plus with Druid connection pool
- Redis: Configured for session/token management
- Server port: 7772 (admin)

### External Integrations
The system integrates with multiple external services configured in properties:
- **Express 100** (kuaidi100) - Logistics tracking
- **Aliyun OSS** - Object storage
- **WangDian** - ERP system
- **PDD (Pinduoduo)** - E-commerce platform
- **FaDaDa** - Electronic signature service
- **WeChat** - Mini programs, payments

## MyBatis-Plus Configuration

Mapper XML files are located at: `classpath*:mapper/**/*Mapper.xml`

Key settings:
- ID generation: AUTO (database auto-increment)
- Logical delete: 1 (deleted), 0 (active)
- Camel case mapping enabled
- Cache disabled

## API Documentation

Knife4j (enhanced Swagger) is available at the application's `/doc.html` endpoint when running.

## Code Generation

The `ruoyi-generator` module provides code generation capabilities using Velocity templates.

## Security

- JWT-based authentication with custom header: `Authorization`
- Token expiration: 1440 minutes (24 hours)
- Spring Security integration with custom filters
- XSS protection enabled for `/system/*`, `/monitor/*`, `/tool/*` paths

## Development Notes

- The project uses Lombok extensively for reducing boilerplate
- MapStruct is used for object mapping between layers
- The system supports multiple profiles (dev, test, prod)
- AOP is used for logging and data scope control
- Async operations are supported in the API application