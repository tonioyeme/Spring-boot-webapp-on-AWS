#!/bin/bash

# Install AWS CLI
yum update -y
yum install -y aws-cli


# Set environment variables
export DB_USERNAME=csye6225
export DB_PASSWORD=strong-password
export DB_HOSTNAME=$${aws_db_instance.rds_instance.endpoint}
export S3_BUCKET=$${aws_s3_bucket.private_bucket.bucket}


# Download and run the setup script
aws s3 cp s3://$${S3_BUCKET}/setup.sh /tmp/setup.sh
chmod +x /tmp/setup.sh
/tmp/setup.sh
