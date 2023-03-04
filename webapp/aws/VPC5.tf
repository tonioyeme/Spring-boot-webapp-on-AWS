# Create VPC 5
resource "aws_vpc" "vpc5" {
  cidr_block = var.cidr_block_addresses[0]
  tags = {
  Name = "vpc5"
}
}

# Create public subnets in vpc5
resource "aws_subnet" "public_vpc5a" {
  vpc_id = aws_vpc.vpc5.id
  cidr_block = var.public_subnet_cidr_addresses[0]
  availability_zone = var.availability_zone_names[0]
  tags = {
    Name = "public-subnet-vpc5a"
  }
}

resource "aws_subnet" "public_vpc5b" {
  vpc_id = aws_vpc.vpc5.id
  cidr_block = var.public_subnet_cidr_addresses[1]
  availability_zone = var.availability_zone_names[1]
  tags = {
    Name = "public-subnet-vpc5b"
  }
}

resource "aws_subnet" "public_vpc5c" {
  vpc_id = aws_vpc.vpc5.id
  cidr_block = var.public_subnet_cidr_addresses[2]
  availability_zone = var.availability_zone_names[2]
  tags = {
    Name = "public-subnet-vpc5c"
  }
}

# Create private subnets in vpc5
resource "aws_subnet" "private_subnet_vpc5a" {
  vpc_id = aws_vpc.vpc5.id
  cidr_block = var.private_subnet_cidr_addresses[0]
  availability_zone = var.availability_zone_names[0]
  tags = {
    Name = "private-subnet-vpc5a"
  }
}

resource "aws_subnet" "private_subnet_vpc5b" {
  vpc_id = aws_vpc.vpc5.id
  cidr_block = var.private_subnet_cidr_addresses[1]
  availability_zone = var.availability_zone_names[1]
  tags = {
    Name = "private-subnet-vpc5b"
  }
}

resource "aws_subnet" "private_subnet_vpc5c" {
  vpc_id = aws_vpc.vpc5.id
  cidr_block = var.private_subnet_cidr_addresses[2]
  availability_zone = var.availability_zone_names[2]
  tags = {
    Name = "private-subnet-vpc5c"
  }
}


#Create internet gateway in vpc5

resource "aws_internet_gateway" "igw_vpc5" {
vpc_id = aws_vpc.vpc5.id
tags = {
Name = "igw-vpc5"
}
}

#Create a public route table in vpc5

resource "aws_route_table" "public_vpc5" {
vpc_id = aws_vpc.vpc5.id
route {
cidr_block = var.route_table_cidr[0]
gateway_id = aws_internet_gateway.igw_vpc5.id
}
tags = {
Name = "public-route-table-vpc5"
}
}

# Create a public route in the public route table in vpc5
resource "aws_route" "public_route_vpc5" {
  route_table_id            = aws_route_table.public_vpc5.id
  destination_cidr_block    = var.route_table_cidr[0]
  gateway_id                = aws_internet_gateway.igw_vpc5.id
}


#Create a private route table in vpc5

resource "aws_route_table" "private_vpc5" {
vpc_id = aws_vpc.vpc5.id
tags = {
Name = "private-route-table-vpc5"
}
}

# Associate public route table with public subnets in vpc5
resource "aws_route_table_association" "public_vpc5a" {
  subnet_id = aws_subnet.public_vpc5a.id
  route_table_id = aws_route_table.public_vpc5.id
}

resource "aws_route_table_association" "public_vpc5b" {
  subnet_id = aws_subnet.public_vpc5b.id
  route_table_id = aws_route_table.public_vpc5.id
}

resource "aws_route_table_association" "public_vpc5c" {
  subnet_id = aws_subnet.public_vpc5c.id
  route_table_id = aws_route_table.public_vpc5.id
}

# Associate private route table with private subnets in vpc5
resource "aws_route_table_association" "private_subnet_vpc5a" {
  subnet_id = aws_subnet.private_subnet_vpc5a.id
  route_table_id = aws_route_table.private_vpc5.id
}

resource "aws_route_table_association" "private_subnet_vpc5b" {
  subnet_id = aws_subnet.private_subnet_vpc5b.id
  route_table_id = aws_route_table.private_vpc5.id
}

resource "aws_route_table_association" "private_subnet_vpc5c" {
  subnet_id = aws_subnet.private_subnet_vpc5c.id
  route_table_id = aws_route_table.private_vpc5.id
}


# Security group for EC2
module "app_security_group" {
  source = "./modules/app_security_group"
  ssh_private_key_file = var.ssh_private_key_file
  vpc_id = aws_vpc.vpc5.id
}


# Create EC2 instance in public subnet of vpc5
resource "aws_instance" "example_vpc5" {
  ami = var.ami_id
  key_name = "csye6225_demo"
  instance_type = var.instance_type[0]
  subnet_id = aws_subnet.public_vpc5a.id
  associate_public_ip_address = true
  #user data
  user_data = templatefile(
                            "${path.module}/userdata.sh", 
                            {
                              db_username = "root"
                              db_password = "zzyTnl2023228!!!"
                              db_hostname = aws_db_instance.rds_instance.endpoint
                              s3_bucket   = aws_s3_bucket.private_bucket.id
                              
                            }
                          )
  root_block_device {
    volume_type = var.volume_type[0]
    volume_size = var.root_volume_size[0]
    delete_on_termination = true
  }
  vpc_security_group_ids = [module.app_security_group.security_group_id]


  tags = {
    Name = "example-instance-vpc5"
  }

}



# Create an EC2 security group for RDS instances
resource "aws_security_group" "database_security_group" {
  name_prefix = "database_security_group"

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [module.app_security_group.security_group_id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  vpc_id = aws_vpc.vpc5.id
}



# Create private S3 bucket

resource "random_id" "bucket_id" {
  byte_length = 4
}

resource "aws_s3_bucket" "private_bucket" {
  bucket = "csye6225-${random_id.bucket_id.hex}-s3"
  acl    = "private"

  versioning {
    enabled = true
  }

  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm     = "AES256"
      }
    }
  }

  lifecycle_rule {
    enabled = true
    id      = "transition-to-ia"
    prefix  = ""

    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
}

# RDS Parameter Group
resource "aws_db_parameter_group" "db_parameter_group" {
  name_prefix = "db-parameter-group"
  family      = "mysql8.0"

  parameter {
    name  = "character_set_client"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_connection"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_database"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_results"
    value = "utf8mb4"
  }

  parameter {
    name  = "character_set_server"
    value = "utf8mb4"
  }
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name        = "my-db-subnet-group"
  description = "Subnet group for RDS instance"

  subnet_ids  = [
    aws_subnet.private_subnet_vpc5a.id,
    aws_subnet.private_subnet_vpc5b.id,
    aws_subnet.private_subnet_vpc5c.id
  ]

  tags = {
    Name = "my-db-subnet-group"
  }
}


# RDS Instance
resource "aws_db_instance" "rds_instance" {
  identifier            = "csye6225"
  allocated_storage     = 20
  engine                = "mysql"
  engine_version        = "8.0.25"
  instance_class        = "db.t3.micro"
  name                  = "csye6225"
  username              = "csye6225"
  password              = "strong-password"
  parameter_group_name  = aws_db_parameter_group.db_parameter_group.name
  skip_final_snapshot   = true
  publicly_accessible  = false
  db_subnet_group_name  = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [aws_security_group.database_security_group.id]

  tags = {
    Name = "rds_instance"
  }
}



# Create Launch Configuration
resource "aws_launch_configuration" "launch_configuration" {
  name_prefix                 = "launch_configuration"
  image_id                    = var.ami_id
  instance_type               = "t2.micro"
  key_name                    = "csye6225_demo"
  security_groups             = [module.app_security_group.security_group_id]
  

  lifecycle {
    create_before_destroy = true
  }

  depends_on = [
    aws_db_instance.rds_instance,
  ]
}


# Create IAM policy
resource "aws_iam_policy" "webapp_s3_policy" {
  name_prefix = "webapp_s3_policy"
  policy      = jsonencode({
    Version: "2012-10-17",
    Statement: [
      {
        Action: [
          "s3:*"
        ],
        Effect: "Allow",
        Resource: [
          aws_s3_bucket.private_bucket.arn,
          "${aws_s3_bucket.private_bucket.arn}/*"
        ]
      }
    ]
  })
}

#IAM policy to grant permissions for your Terraform configuration to create and manage resources in AWS account 
resource "aws_iam_policy" "terraform_policy" {
  name = "terraform_policy"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "ec2:*",
          "vpc:*"
        ],
        Resource = "*"
      }
    ]
  })
}


# Create IAM role
resource "aws_iam_role" "ec2_csye6225" {
  name = "EC2-CSYE6225"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

# Attach IAM policy to IAM role
resource "aws_iam_policy_attachment" "webapp_s3" {
  name       = "webapp_s3_attachment"
  policy_arn = aws_iam_policy.webapp_s3_policy.arn
  roles      = [aws_iam_role.ec2_csye6225.name]
}

resource "aws_iam_role_policy_attachment" "terraform_policy_attachment" {
  policy_arn = aws_iam_policy.terraform_policy.arn
  role       = aws_iam_role.ec2_csye6225.name
}

# Attach IAM policy to EC2 instance profile
resource "aws_iam_instance_profile" "instance_profile" {
  name_prefix = "instance_profile"
  role        = aws_iam_role.ec2_csye6225.name
}

resource "aws_iam_role_policy_attachment" "webapp_s3_policy_attachment" {
  policy_arn = aws_iam_policy.webapp_s3_policy.arn
  role       = aws_iam_instance_profile.instance_profile.name
}

