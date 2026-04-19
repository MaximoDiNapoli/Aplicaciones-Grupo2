#!/bin/bash
set -e

for sql_file in $(find /docker-initdb -maxdepth 1 -type f -name 'v*.sql' | sort -V); do
	echo "Applying ${sql_file}"
	mysql --protocol=tcp -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < "${sql_file}"
done