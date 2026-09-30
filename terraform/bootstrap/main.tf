terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Generate random suffix to guarantee global uniqueness for the S3 bucket
resource "random_id" "suffix" {
  byte_length = 4
}

locals {
  bucket_name = "${var.project_name}-tfstate-${random_id.suffix.hex}"
}

# ==========================================
# S3 Bucket for Remote State Storage
# ==========================================

resource "aws_s3_bucket" "terraform_state" {
  bucket        = local.bucket_name
  force_destroy = true

  tags = {
    Name        = "Terraform State Storage"
    Project     = var.project_name
    Environment = "Management"
  }
}

# Enable versioning so all state revisions are preserved
resource "aws_s3_bucket_versioning" "state_versioning" {
  bucket = aws_s3_bucket.terraform_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

# Enable AES256 server-side encryption
resource "aws_s3_bucket_server_side_encryption_configuration" "state_encryption" {
  bucket = aws_s3_bucket.terraform_state.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# Block all public access
resource "aws_s3_bucket_public_access_block" "state_public_block" {
  bucket = aws_s3_bucket.terraform_state.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}
