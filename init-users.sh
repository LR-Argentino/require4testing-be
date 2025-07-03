#!/bin/zsh

echo "Initializing users..."
RESPONSE=$(http :8080/api/auth/register username="luca" password="test123" email="luca@icloud.com" role="REQUIREMENTS_ENGINEER")
RESPONSE=$(http :8080/api/auth/register username="max" password="test123" email="max@icloud.com" role="REQUIREMENTS_ENGINEER")
STATUS=$?

echo "Antwort vom Server:"

echo "$RESPONSE"

if [ $STATUS -ne 0 ]; then
    echo "Error during user initialization. Status code: $STATUS"
    exit $STATUS
else
    echo "Users initialization successful"
fi