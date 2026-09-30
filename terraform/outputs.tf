# AWS and EKS outputs
output "aws_region" {
  description = "Target AWS Region"
  value       = var.aws_region
}

output "ecr_repository_url" {
  description = "Full ECR Repository URI"
  value       = aws_ecr_repository.app_repo.repository_url
}

output "ecr_registry_host" {
  description = "ECR Registry host"
  value       = split("/", aws_ecr_repository.app_repo.repository_url)[0]
}

output "eks_cluster_name" {
  description = "EKS Cluster identifier"
  value       = aws_eks_cluster.eks.name
}

output "eks_cluster_endpoint" {
  description = "EKS API Server Endpoint"
  value       = aws_eks_cluster.eks.endpoint
}

output "kubectl_configure_command" {
  description = "Command to connect local kubectl to EKS"
  value       = "aws eks update-kubeconfig --region ${var.aws_region} --name ${aws_eks_cluster.eks.name}"
}

output "ecr_login_command" {
  description = "Command to authenticate Docker CLI with ECR"
  value       = "aws ecr get-login-password --region ${var.aws_region} | docker login --username AWS --password-stdin ${split("/", aws_ecr_repository.app_repo.repository_url)[0]}"
}

output "kestra_pipeline_inputs" {
  description = "Inputs for Kestra Workflow Execution"
  value = {
    aws_region       = var.aws_region
    ecr_registry     = split("/", aws_ecr_repository.app_repo.repository_url)[0]
    eks_cluster_name = aws_eks_cluster.eks.name
    git_repo         = "https://github.com/your-username/eks-gitops-deploy.git"
    git_branch       = "main"
  }
}
