# S3 remote state backend
terraform {
  backend "s3" {
    bucket       = "your-terraform-state-bucket"
    key          = "production/eks-gitops-deploy.tfstate"
    region       = "ap-south-2"
    encrypt      = true
    use_lockfile = true
  }
}
