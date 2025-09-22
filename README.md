# JobPilot.ai

🚀 **JobPilot.ai** is an AI-powered job application assistant that helps you stand out in the hiring process.  
Upload your resume, and JobPilot.ai will:

- 📄 Generate tailored **cover letters** for each job posting  
- ✨ Adapt and refine your **resume to match the job description**  
- ☁️ Store your resumes and cover letters securely on AWS (S3)  

---

## 🛠️ Tech Stack

### Backend
- **Java Spring Boot** – API and business logic  
- **PostgreSQL** – Relational database for persistent storage  

### Frontend
- **React (TypeScript + Vite)** – Fast, modern, and type-safe UI  

### Infrastructure
- **Terraform** – Infrastructure as Code  
- **AWS** – Cloud hosting  
  - **EC2** – Application hosting  
  - **RDS (PostgreSQL)** – Managed relational database  
  - **S3** – File storage for resumes and cover letters  

---

## ⚙️ Features
- Resume upload and parsing  
- AI-assisted cover letter generation  
- Resume tailoring to specific job descriptions  
- Secure storage of user documents  
- Deployed on scalable AWS infrastructure  

---

## 🚀 Getting Started

### Prerequisites
- [Java 17+](https://adoptium.net/)  
- [Node.js 18+](https://nodejs.org/) & npm  
- [PostgreSQL](https://www.postgresql.org/)  
- [Terraform](https://developer.hashicorp.com/terraform/downloads)  
- AWS account & configured credentials  

### Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Infra Setup
```bash
cd terraform
cd infra
terraform init
terraform plan
terraform apply
```
