# S3 remote state backend
terraform {
  backend "s3" {
    bucket       = "eks-gitops-deploy-tfstate-abhishekkhode-2088975711"
    key          = "production/eks-gitops-deploy.tfstate"
    region       = "ap-south-2"
    encrypt      = true
    use_lockfile = true
  }
}
