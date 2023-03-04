terraform {
  backend "s3" {
    bucket         = "csye6225webappbucket"
    key            = "terraform/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    profile = "demo"
  }
}
