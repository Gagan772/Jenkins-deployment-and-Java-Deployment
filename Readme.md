# DevOps POC Project Structure

```
.
├── README.md
├── skills.md
├── Jenkinsfile
├── terraform/
│   ├── provider.tf
│   ├── variables.tf
│   ├── main.tf
│   ├── outputs.tf
│   └── terraform.tfvars.example
├── java-app/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── devops/
│   │   │   │           └── app/
│   │   │   │               ├── DevOpsApplication.java
│   │   │   │               ├── controller/
│   │   │   │               │   └── CustomerController.java
│   │   │   │               ├── service/
│   │   │   │               │   ├── CustomerService.java
│   │   │   │               │   ├── EmployeeService.java
│   │   │   │               │   ├── ProductService.java
│   │   │   │               │   └── OrderService.java
│   │   │   │               ├── repository/
│   │   │   │               │   ├── CustomerRepository.java
│   │   │   │               │   ├── EmployeeRepository.java
│   │   │   │               │   ├── ProductRepository.java
│   │   │   │               │   └── OrderRepository.java
│   │   │   │               ├── entity/
│   │   │   │               │   ├── Customer.java
│   │   │   │               │   ├── Employee.java
│   │   │   │               │   ├── Product.java
│   │   │   │               │   └── OrderEntity.java
│   │   │   │               ├── dto/
│   │   │   │               │   ├── CustomerDTO.java
│   │   │   │               │   ├── ProductDTO.java
│   │   │   │               │   └── OrderDTO.java
│   │   │   │               ├── config/
│   │   │   │               │   └── WebServiceConfig.java
│   │   │   │               ├── exception/
│   │   │   │               │   ├── ResourceNotFoundException.java
│   │   │   │               │   └── GlobalExceptionHandler.java
│   │   │   │               ├── soap/
│   │   │   │               │   └── EmployeeEndpoint.java
│   │   │   │               └── graphql/
│   │   │   │                   └── GraphQLController.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── data.sql
│   │   │       ├── xsd/
│   │   │       │   └── employees.xsd
│   │   │       └── graphql/
│   │   │           └── schema.graphqls
│   │   └── test/
│   │       └── java/
│   │           └── com/
│   │               └── devops/
│   │                   └── app/
│   │                       ├── CustomerServiceTest.java
│   │                       └── DevOpsApplicationTests.java
└── docs/
    └── setup.md
```

## Project Components

### Terraform (`terraform/`)
- **provider.tf**: AWS provider configuration
- **variables.tf**: Infrastructure variables
- **main.tf**: EC2 instances and security groups
- **outputs.tf**: Server IPs and connection commands

### Java Application (`java-app/`)
- **Spring Boot 3.2.3** with Java 21
- **H2 Database** for in-memory storage
- **Maven** for build management
- **Swagger/OpenAPI** for API documentation
- **Spring Actuator** for health monitoring

### APIs Implemented

#### REST APIs (3)
- GET `/api/customers` - Get all customers
- GET `/api/customers/{id}` - Get customer by ID
- POST `/api/customers` - Create new customer

#### SOAP APIs (3)
- `getEmployee` - Get employee by ID
- `createEmployee` - Create new employee
- `listEmployees` - Get all employees

#### GraphQL APIs (4)
- `getProducts` - Get all products
- `getProductById` - Get product by ID
- `createProduct` - Create new product
- `getOrders` - Get all orders

### Jenkins Pipeline (`Jenkinsfile`)
Automated CI/CD pipeline with stages:
1. Checkout code from Git
2. Build with Maven
3. Run unit tests
4. Package JAR
5. Deploy via SSH
6. Health check validation

### Documentation (`docs/`)
Comprehensive setup guide covering:
- Terraform infrastructure provisioning
- Jenkins server configuration
- Application server setup
- SSH deployment configuration
- Pipeline setup and execution
- Complete deployment flow

## Quick Start

1. **Provision Infrastructure:**
   ```bash
   cd terraform
   terraform init
   terraform apply
   ```

2. **Setup Jenkins Server:**
   - Install Java 21, Jenkins, Maven, Git
   - Configure JDK and Maven in Jenkins
   - Create SSH keys for deployment

3. **Setup Application Server:**
   - Install Java 21
   - Create `msservice` user
   - Configure SSH access from Jenkins

4. **Deploy Application:**
   - Create Jenkins pipeline job
   - Configure Git repository
   - Run pipeline build

5. **Verify:**
   ```bash
   curl http://<APP_SERVER_IP>:8081/actuator/health
   ```

For detailed instructions, see [docs/setup.md](docs/setup.md)
