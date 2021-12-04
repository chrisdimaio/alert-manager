#! /usr/bin/sh

API_TOKEN=12323123123
AWS_MM_SMS_OriginationNumber="+18445811227"

export API_TOKEN
export AWS_MM_SMS_OriginationNumber

java -jar /home/chris/workspace/alert-manager/target/alert-manager-1.0-SNAPSHOT-fat.jar