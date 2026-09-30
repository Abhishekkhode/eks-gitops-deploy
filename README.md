# 🚀 eks-gitops-deploy: Cloud-Native Spring Boot SRE & IaC Blueprint

A production-grade, containerized Spring Boot 3 microservice engineered for automated deployment, telemetry verification, and incident alerting with **Terraform (IaC)**, **Kestra Orchestrator**, **AWS EKS**, and **Discord**.

---

## 📌 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Service overview & discovery map |
| `GET` | `/actuator/health` | Spring Boot Actuator probe endpoint (Kubernetes Liveness & Readiness) |
| `GET` | `/api/hello` | Hello World & baseline connectivity check |
| `GET` | `/api/info` | Application metadata, author information, and blueprint specs |
| `GET` | `/api/system` | Live server uptime, JVM telemetry, UTC timestamp, and AWS EKS host resource metadata |
| `GET` | `/api/status` | SRE status summary |

---

## 🛠️ Project Structure

```text
├── terraform/                           # Infrastructure as Code (AWS EKS, ECR, VPC)
│   ├── versions.tf                      # AWS Provider and version constraints
│   ├── variables.tf                     # Configurable cluster & network inputs
│   ├── vpc.tf                           # Multi-AZ VPC with Public & Private Subnets + NAT
│   ├── ecr.tf                           # Encrypted ECR repository + lifecycle policy
│   ├── eks.tf                           # EKS Control plane, Managed Node Group & Addons
│   ├── outputs.tf                       # Formatted outputs for Kestra inputs & CLI helpers
│   └── terraform.tfvars.example         # Example configuration file
├── src/
│   ├── main/java/com/kestra/blueprint/
│   │   ├── Application.java             # Spring Boot Application Entrypoint
│   │   └── controller/
│   │       ├── HomeController.java      # Root route discovery
│   │       └── AppController.java       # Hello, Info, System, and Status endpoints
│   └── main/resources/
│       └── application.yml              # Actuator & probe configurations
├── k8s/
│   ├── deployment.yaml                  # EKS Deployment with Liveness & Readiness Probes
│   └── service.yaml                     # Kubernetes LoadBalancer Service
├── Dockerfile                           # Hardened Multi-Stage Container Build
├── docker-compose.yaml                  # Local Kestra Orchestrator Service
├── kestra-workflow.yaml                 # Complete Kestra SRE Blueprint Workflow
└── pom.xml                              # Maven Configuration (Java 17, Spring Boot 3.3.4)
```

---

## 🏗️ 1. Provision AWS Infrastructure with Terraform (IaC)

### Step A: (Optional) Bootstrap S3 Remote State & DynamoDB Locks
If you want to store your Terraform state remotely in S3 with state locking:

```bash
cd terraform/bootstrap
terraform init
terraform apply -auto-approve
# Note the generated S3 bucket name and DynamoDB table from outputs
cd ..
```

### Step B: Initialize & Deploy Main Infrastructure
Navigate to the `terraform/` directory:

```bash
cd terraform

# 1. Initialize Terraform with Remote S3 Backend
# (Pass the bucket name created in Step A or your existing bucket)
terraform init -backend-config="bucket=<your-s3-bucket-name>" -reconfigure

# 2. Review Execution Plan
terraform plan

# 3. Provision Infrastructure (VPC, ECR, EKS Cluster & Node Groups)
terraform apply -auto-approve
```

### Terraform Outputs

Upon completion, Terraform prints the exact commands and parameters:
* `ecr_repository_url`: `123456789012.dkr.ecr.ap-south-1.amazonaws.com/eks-gitops-deploy`
* `eks_cluster_name`: `eks-gitops-deploy-cluster`
* `kubectl_configure_command`: `aws eks update-kubeconfig --region ap-south-2 --name eks-gitops-deploy-cluster`
* `kestra_pipeline_inputs`: Formatted map ready to paste directly into Kestra!

---

## 🐳 2. Local Container Verification

Test the multi-stage build locally using Docker:

```bash
# Build the container image
docker build -t eks-gitops-deploy:local .

# Run the container
docker run -p 8081:8080 -e AWS_REGION=ap-south-2 eks-gitops-deploy:local
```

Test the endpoints:
```bash
curl http://localhost:8081/
curl http://localhost:8081/actuator/health
curl http://localhost:8081/api/info
curl http://localhost:8081/api/system
```

---

## ⚙️ 3. Kestra Secret Configuration

Configure the following secrets in your Kestra instance (`http://localhost:8080`):

1. `AWS_ACCESS_KEY_ID`: Programmatic IAM Access Key with ECR & EKS permissions.
2. `AWS_SECRET_ACCESS_KEY`: Programmatic IAM Secret Key.
3. `DISCORD_WEBHOOK_URL`: Discord webhook URL for deployment & incident alerting.
