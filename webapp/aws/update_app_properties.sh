#!/bin/bash

PUBLIC_IP=$1
sed -i '' "1s|{mysql_url}|jdbc:mysql://$PUBLIC_IP:3306/mydb|" application.properties


chmod +x /Users/toni/csye6225/webapp/webapp/aws/update_app_properties.sh
