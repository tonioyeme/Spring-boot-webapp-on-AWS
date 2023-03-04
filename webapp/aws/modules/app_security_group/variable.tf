variable "cidr_block_addresses" {
  type = list(string)
  default = [ "10.1.0.0/16", "10.2.0.0/16" ]
}

variable "public_subnet_cidr_addresses" {
    type = list(string)
    default = [ "10.1.1.0/24", "10.1.2.0/24", "10.1.3.0/24", "10.2.1.0/24", "10.2.2.0/24", "10.2.3.0/24"]
}

variable "private_subnet_cidr_addresses" {
    type = list(string)
    default = [ "10.1.4.0/24", "10.1.5.0/24", "10.1.6.0/24", "10.2.4.0/24", "10.2.5.0/24", "10.2.6.0/24" ]
}

variable "route_table_cidr" {
    type = list(string)
    default = [ "0.0.0.0/0" ]
  
}

variable "availability_zone_names" {
    type = list(any)
    default = ["us-east-1a", "us-east-1b","us-east-1c", "us-west-2a", "us-west-2b", "us-west-2c"]
}

variable ami {
    type = list(string)
    default = ["ami-0e44f7e1770a7812e"]

}

variable instance_type {
    type = list(string)
    default = ["t2.micro"]
}

variable "volume_type" {
    type = list(string)
    default = [ "gp2" ]
  
}

variable root_volume_size {
    type = list(string)
    default = ["50"]
}

variable "app_port" {
  type = list(number)
  default = [ 22, 80, 443, 8080, 0, 3306 ]
}

variable protocol {
    type = list(string)
    default = ["tcp", "-1"]
}


variable "ami_id" {
  type = string
  description = "The ID of the AMI to use for the EC2 instance."
  default = ""
}

variable "ssh_user" {
  type = string
  default = "ec2-user"
}

variable "ssh_private_key" {
  type = string
  default = ""
}

variable "SSH_PRIVATE_KEY" {
  type    = string
  default = ""
}

variable "ssh_private_key_file" {
  description = "Path to the SSH private key file"
}

