# DevOps POC - Complete Setup Guide

This guide provides detailed step-by-step instructions to set up the complete CI/CD pipeline using Terraform, Jenkins, and Java Spring Boot.

## Table of Contents

1. [Terraform Infrastructure Setup](#section-1-terraform-infrastructure-setup)
2. [Jenkins Server Setup](#section-2-jenkins-server-setup)
3. [Application Server Setup](#section-3-application-server-setup)
4. [SSH Setup for Jenkins Deployment](#section-4-ssh-setup-for-jenkins-deployment)
5. [Jenkins Job Setup](#section-5-jenkins-job-setup)
6. [Deployment Flow](#section-6-deployment-flow)

---

## SECTION 1: Terraform Infrastructure Setup

### Prerequisites

1. AWS Account with appropriate permissions
2. AWS CLI installed
3. Terraform installed
4. SSH key pair created in AWS

### Step 1.1: Install Terraform

**On Ubuntu/Debian:**

```bash
wget -O- https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list
sudo apt update && sudo apt install terraform
```

**Verify installation:**

```bash
terraform --version
```

### Step 1.2: Configure AWS CLI

**Install AWS CLI:**

```bash
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

**Configure AWS credentials:**

```bash
aws configure
```

Enter:
- AWS Access Key ID
- AWS Secret Access Key
- Default region (e.g., us-east-1)
- Default output format (json)

**Verify configuration:**

```bash
aws sts get-caller-identity
```

### Step 1.3: Create AWS Key Pair

```bash
aws ec2 create-key-pair --key-name devops-key --query 'KeyMaterial' --output text > devops-key.pem
chmod 400 devops-key.pem
```

### Step 1.4: Prepare Terraform Configuration

Navigate to the terraform directory:

```bash
cd terraform
```

Create `terraform.tfvars` file:

```bash
cat > terraform.tfvars <<EOF
aws_region        = "us-east-1"
key_pair_name     = "devops-key"
allowed_ssh_cidr  = "0.0.0.0/0"
EOF
```

### Step 1.5: Initialize Terraform

```bash
terraform init
```

This command:
- Downloads required provider plugins
- Initializes the backend
- Prepares the working directory

### Step 1.6: Run Terraform Plan

```bash
terraform plan
```

Review the execution plan. It should show:
- 2 EC2 instances to be created
- 2 Security groups to be created
- Various outputs defined

### Step 1.7: Apply Terraform Configuration

```bash
terraform apply
```

Type `yes` when prompted.

Wait for resources to be created (approximately 2-3 minutes).

### Step 1.8: Capture Outputs

```bash
terraform output
```

Save the following information:
- Jenkins server public IP
- Application server public IP
- SSH commands
- URLs

Example output:

```
jenkins_server_public_ip = "54.123.45.67"
application_server_public_ip = "54.123.45.68"
jenkins_ssh_command = "ssh -i devops-key.pem ubuntu@54.123.45.67"
application_ssh_command = "ssh -i devops-key.pem ubuntu@54.123.45.68"
jenkins_url = "http://54.123.45.67:8080"
```

### Step 1.9: Verify Server Accessibility

Test SSH connection to Jenkins server:

```bash
ssh -i devops-key.pem ubuntu@<JENKINS_SERVER_IP>
```

Test SSH connection to Application server:

```bash
ssh -i devops-key.pem ubuntu@<APP_SERVER_IP>
```

---

## SECTION 2: Jenkins Server Setup

### Step 2.1: Connect to Jenkins Server

```bash
ssh -i devops-key.pem ubuntu@<JENKINS_SERVER_IP>
```

### Step 2.2: Update System

```bash
sudo apt update
sudo apt upgrade -y
```

### Step 2.3: Install Java 21

```bash
sudo apt install -y openjdk-21-jdk
```

**Verify installation:**

```bash
java -version
```

Expected output: `openjdk version "21.x.x"`

**Set JAVA_HOME:**

```bash
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' | sudo tee -a /etc/environment
echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/environment
source /etc/environment
```

### Step 2.4: Install Jenkins

**Add Jenkins repository:**

```bash
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
  
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
```

**Install Jenkins:**

```bash
sudo apt update
sudo apt install -y jenkins
```

**Start Jenkins service:**

```bash
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

**Check Jenkins status:**

```bash
sudo systemctl status jenkins
```

### Step 2.5: Install Maven

```bash
sudo apt install -y maven
```

**Verify Maven installation:**

```bash
mvn -version
```

### Step 2.6: Install Git

```bash
sudo apt install -y git
```

**Verify Git installation:**

```bash
git --version
```

### Step 2.7: Create Jenkins User

The Jenkins user is already created during installation, but we need to set it up for SSH:

```bash
sudo su - jenkins
```

Generate SSH key for Jenkins user:

```bash
ssh-keygen -t rsa -b 4096 -C "jenkins@devops"
```

Press Enter for all prompts (default location, no passphrase).

Exit Jenkins user:

```bash
exit
```

### Step 2.8: Unlock Jenkins

Get the initial admin password:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Copy the password.

Open browser and navigate to:

```
http://<JENKINS_SERVER_IP>:8080
```

Paste the initial admin password.

### Step 2.9: Install Jenkins Plugins

**Option 1:** Select "Install suggested plugins"

**Option 2:** Select "Select plugins to install" and choose:
- Git plugin
- Pipeline plugin
- Maven Integration plugin
- SSH Agent plugin
- Workspace Cleanup plugin
- JUnit plugin

Wait for plugins to install.

### Step 2.10: Create Admin User

Create the first admin user:
- Username: `admin`
- Password: `<choose secure password>`
- Full name: `Jenkins Admin`
- Email: `admin@example.com`

Click "Save and Continue"

### Step 2.11: Configure Jenkins URL

Confirm the Jenkins URL:

```
http://<JENKINS_SERVER_IP>:8080
```

Click "Save and Finish"

### Step 2.12: Configure JDK in Jenkins

1. Go to: **Manage Jenkins** → **Tools**
2. Scroll to **JDK installations**
3. Click **Add JDK**
4. Name: `JDK-21`
5. Uncheck "Install automatically"
6. JAVA_HOME: `/usr/lib/jvm/java-21-openjdk-amd64`
7. Click **Save**

### Step 2.13: Configure Maven in Jenkins

1. Go to: **Manage Jenkins** → **Tools**
2. Scroll to **Maven installations**
3. Click **Add Maven**
4. Name: `Maven-3.9`
5. Uncheck "Install automatically"
6. MAVEN_HOME: `/usr/share/maven`
7. Click **Save**

---

## SECTION 3: Application Server Setup

### Step 3.1: Connect to Application Server

Open a new terminal and connect:

```bash
ssh -i devops-key.pem ubuntu@<APP_SERVER_IP>
```

### Step 3.2: Update System

```bash
sudo apt update
sudo apt upgrade -y
```

### Step 3.3: Install Java 21

```bash
sudo apt install -y openjdk-21-jdk
```

**Verify installation:**

```bash
java -version
```

### Step 3.4: Install Maven (Optional)

```bash
sudo apt install -y maven
```

### Step 3.5: Install Git (Optional)

```bash
sudo apt install -y git
```

### Step 3.6: Create msservice User

```bash
sudo useradd -m -s /bin/bash msservice
```

Set password for msservice:

```bash
sudo passwd msservice
```

### Step 3.7: Create Application Directory

```bash
sudo mkdir -p /home/msservice/app
sudo chown -R msservice:msservice /home/msservice/app
```

### Step 3.8: Configure Firewall (if enabled)

```bash
sudo ufw allow 8081/tcp
sudo ufw allow 22/tcp
```

---

## SECTION 4: SSH Setup for Jenkins Deployment

### Step 4.1: Copy Jenkins SSH Public Key

On Jenkins server:

```bash
sudo cat /var/lib/jenkins/.ssh/id_rsa.pub
```

Copy the entire public key.

### Step 4.2: Add Key to Application Server

On Application server, switch to msservice user:

```bash
sudo su - msservice
```

Create .ssh directory:

```bash
mkdir -p ~/.ssh
chmod 700 ~/.ssh
```

Add Jenkins public key to authorized_keys:

```bash
nano ~/.ssh/authorized_keys
```

Paste the Jenkins public key, save and exit (Ctrl+X, Y, Enter).

Set permissions:

```bash
chmod 600 ~/.ssh/authorized_keys
```

Exit msservice user:

```bash
exit
```

### Step 4.3: Test SSH Connection from Jenkins

On Jenkins server:

```bash
sudo su - jenkins
ssh msservice@<APP_SERVER_IP>
```

If successful, type `exit` to return to Jenkins server.

Exit Jenkins user:

```bash
exit
```

### Step 4.4: Add SSH Credentials in Jenkins

1. Go to: **Manage Jenkins** → **Credentials**
2. Click on **(global)** domain
3. Click **Add Credentials**
4. Kind: `SSH Username with private key`
5. ID: `app-server-ssh-key`
6. Username: `msservice`
7. Private Key: Click **Enter directly**
8. Copy Jenkins private key:

```bash
sudo cat /var/lib/jenkins/.ssh/id_rsa
```

Paste the private key content (including BEGIN and END lines).

9. Click **Create**

---

## SECTION 5: Jenkins Job Setup

### Step 5.1: Create Git Repository

Push your project to a Git repository (GitHub, GitLab, Bitbucket, etc.).

Example GitHub:

```bash
cd /path/to/project
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/yourusername/devops-poc.git
git push -u origin main
```

### Step 5.2: Create Pipeline Job in Jenkins

1. Click **New Item**
2. Enter name: `devops-app-pipeline`
3. Select **Pipeline**
4. Click **OK**

### Step 5.3: Configure Pipeline

**General Section:**
- Description: `CI/CD pipeline for DevOps POC application`

**Build Triggers:**
- Optional: Check **Poll SCM** with schedule: `H/5 * * * *` (every 5 minutes)

**Pipeline Section:**
- Definition: `Pipeline script from SCM`
- SCM: `Git`
- Repository URL: `https://github.com/yourusername/devops-poc.git`
- Credentials: Add your Git credentials if repository is private
- Branch: `*/main`
- Script Path: `Jenkinsfile`

Click **Save**

### Step 5.4: Update Jenkinsfile with Server IP

Edit the Jenkinsfile in your repository and update the `APP_SERVER_HOST` variable:

```groovy
environment {
    APP_NAME = 'devops-app'
    APP_VERSION = '1.0.0'
    APP_SERVER_USER = 'msservice'
    APP_SERVER_HOST = '<APP_SERVER_IP>'  // Update this
    APP_DEPLOY_DIR = '/home/msservice/app'
    JAR_NAME = "${APP_NAME}-${APP_VERSION}.jar"
}
```

Commit and push the changes:

```bash
git add Jenkinsfile
git commit -m "Update application server IP"
git push
```

### Step 5.5: Run Pipeline

1. Go to the pipeline job
2. Click **Build Now**
3. Watch the build progress in **Build History**
4. Click on the build number to see console output

### Step 5.6: Monitor Build Stages

The pipeline will execute the following stages:

1. **Checkout** - Clone the repository
2. **Build** - Compile Java code with Maven
3. **Unit Tests** - Run JUnit tests
4. **Package** - Create JAR file
5. **Deploy to Application Server** - Copy JAR via SSH and start application
6. **Health Check** - Verify application is running

### Step 5.7: Verify Deployment

After successful pipeline execution, verify the application:

**Health Check:**

```bash
curl http://<APP_SERVER_IP>:8081/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

**Swagger UI:**

Open browser:

```
http://<APP_SERVER_IP>:8081/swagger-ui.html
```

**GraphQL UI:**

```
http://<APP_SERVER_IP>:8081/graphiql
```

**H2 Console:**

```
http://<APP_SERVER_IP>:8081/h2-console
```

- JDBC URL: `jdbc:h2:mem:devopsdb`
- Username: `sa`
- Password: (leave empty)

---

## SECTION 6: Deployment Flow

### Overview

The complete CI/CD flow follows this sequence:

```
Developer → Git Push → Jenkins Trigger → Build → Test → Package → Deploy → Health Check
```

### Detailed Flow

1. **Developer Commits Code**
   - Developer makes changes to Java application
   - Commits changes to Git repository
   - Pushes to remote repository

2. **Jenkins Detects Changes**
   - Jenkins polls Git repository (every 5 minutes)
   - OR manually triggers build via "Build Now"
   - Jenkins pulls latest code

3. **Checkout Stage**
   - Jenkins clones the repository
   - Switches to specified branch (main)

4. **Build Stage**
   - Maven compiles Java source code
   - Resolves dependencies from Maven Central
   - Generates .class files in target directory

5. **Unit Tests Stage**
   - Maven runs JUnit tests
   - Generates test reports in XML format
   - Jenkins publishes test results
   - Pipeline fails if tests fail

6. **Package Stage**
   - Maven packages compiled code into JAR
   - JAR file created: `devops-app-1.0.0.jar`
   - Located in `java-app/target/` directory

7. **Deploy to Application Server Stage**
   - Jenkins uses SSH agent with credentials
   - Creates deployment directory on application server
   - Copies JAR file via SCP
   - Stops running application (if exists)
   - Starts new application using `nohup java -jar`
   - Waits 30 seconds for application startup

8. **Health Check Stage**
   - Jenkins calls: `http://<APP_SERVER_IP>:8081/actuator/health`
   - Parses JSON response
   - Checks if status is "UP"
   - Pipeline fails if health check fails
   - Pipeline succeeds if health check passes

9. **Post Actions**
   - On success: Log success message with application URL
   - On failure: Log failure message
   - Always: Clean workspace

### API Endpoints Available

**REST APIs:**

```
GET  http://<APP_SERVER_IP>:8081/api/customers
GET  http://<APP_SERVER_IP>:8081/api/customers/{id}
POST http://<APP_SERVER_IP>:8081/api/customers
```

**SOAP APIs:**

WSDL Available at:
```
http://<APP_SERVER_IP>:8081/ws/employees.wsdl
```

Operations:
- getEmployee
- createEmployee
- listEmployees

**GraphQL APIs:**

Endpoint:
```
http://<APP_SERVER_IP>:8081/graphql
```

Queries:
- getProducts
- getProductById(id: ID!)
- getOrders

Mutations:
- createProduct(name, description, price, stock)

### Testing the Complete Flow

1. **Make a code change:**

Edit `java-app/src/main/java/com/devops/app/controller/CustomerController.java`

Add a simple log statement:

```java
@GetMapping
@Operation(summary = "Get all customers")
public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
    System.out.println("Fetching all customers - v2.0");
    return ResponseEntity.ok(customerService.getAllCustomers());
}
```

2. **Commit and push:**

```bash
git add .
git commit -m "Add logging to customer endpoint"
git push
```

3. **Watch Jenkins:**
   - Jenkins will detect the change within 5 minutes
   - OR click "Build Now" manually
   - Monitor the build stages
   - Check console output

4. **Verify deployment:**

```bash
curl http://<APP_SERVER_IP>:8081/api/customers
```

5. **Check application logs:**

On application server:

```bash
sudo su - msservice
cd /home/msservice/app
tail -f app.log
```

### Troubleshooting

**Pipeline fails at Build stage:**
- Check Java version: `java -version`
- Check Maven version: `mvn -version`
- Review console output for compilation errors

**Pipeline fails at Unit Tests stage:**
- Review test failures in Jenkins test results
- Check `target/surefire-reports/` for detailed logs

**Pipeline fails at Deploy stage:**
- Verify SSH credentials in Jenkins
- Test SSH connection manually
- Check application server disk space

**Pipeline fails at Health Check stage:**
- Check if application is running: `ps aux | grep devops-app`
- Review application logs: `tail -f /home/msservice/app/app.log`
- Verify port 8081 is not blocked
- Check application startup errors

### Monitoring

**Jenkins:**
- Build history: Shows all pipeline executions
- Console output: Detailed logs for each build
- Test results: JUnit test reports

**Application Server:**

Check running processes:
```bash
ps aux | grep java
```

Check application logs:
```bash
tail -f /home/msservice/app/app.log
```

Check port usage:
```bash
sudo netstat -tulpn | grep 8081
```

### Cleanup

To destroy all resources:

```bash
cd terraform
terraform destroy
```

Type `yes` when prompted.

This will terminate both EC2 instances and delete all resources.

---

## Summary

You have successfully set up:

✅ Terraform infrastructure with 2 EC2 instances
✅ Jenkins CI/CD server with Java 21 and Maven
✅ Application server configured for Java deployment
✅ SSH-based deployment mechanism
✅ Automated pipeline with build, test, and deployment
✅ Health check verification
✅ Java Spring Boot application with REST, SOAP, and GraphQL APIs

The complete DevOps POC is now operational!
