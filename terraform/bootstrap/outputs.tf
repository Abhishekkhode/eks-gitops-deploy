output "s3_bucket_name" {
  description = "Created S3 Bucket Name for Remote State"
  value       = aws_s3_bucket.terraform_state.id
}

output "aws_region" {
  description = "AWS Region for Backend"
  value       = var.aws_region
}

output "init_command" {
  description = "Command to initialize the main Terraform module with this S3 bucket"
  value       = "terraform init -backend-config=\"bucket=${aws_s3_bucket.terraform_state.id}\" -reconfigure"
}
