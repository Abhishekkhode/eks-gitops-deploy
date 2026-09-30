variable "aws_region" {
  description = "AWS region for remote state storage"
  type        = string
  default     = "ap-south-2"
}

variable "project_name" {
  description = "Project name prefix"
  type        = string
  default     = "eks-gitops-deploy"
}
