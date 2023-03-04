# Create security group for EC2 instance


variable "vpc_id" {
  type = string
}

resource "aws_security_group" "app_security_group" {
  name_prefix = "app_security_group"

  ingress {
    from_port = var.app_port[0]
    to_port   = var.app_port[0]
    protocol  = var.protocol[0]
    cidr_blocks = var.route_table_cidr
  }

  ingress {
    from_port = var.app_port[1]
    to_port   = var.app_port[1]
    protocol  = var.protocol[0]
    cidr_blocks = var.route_table_cidr
  }

  ingress {
    from_port = var.app_port[2]
    to_port   = var.app_port[2]
    protocol  = var.protocol[0]
    cidr_blocks = var.route_table_cidr
  }

  ingress {
    from_port = var.app_port[3]
    to_port   = var.app_port[3]
    protocol  = var.protocol[0]
    cidr_blocks = var.route_table_cidr
  }

  ingress {
    from_port = var.app_port[5]
    to_port   = var.app_port[5]
    protocol  = var.protocol[0]
    cidr_blocks = var.route_table_cidr
  }

  egress {
    from_port = var.app_port[4]
    to_port   = var.app_port[4]
    protocol  = var.protocol[1]
    cidr_blocks = var.route_table_cidr
  }

  vpc_id = var.vpc_id

}

output "security_group_id" {
  value = aws_security_group.app_security_group.id
}
