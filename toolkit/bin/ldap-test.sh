#!/bin/sh

python3 ldap_validation.py --server ldaps://localhost:636
	--bind-dn 'DATALAKE\Administrator'  --bind-password '11111'
	--user-base-dn 'OU=Users,DC=datalake,DC=net' \
	--domain-name 'DATALAKE.NET' \
	--new-username 'test' --new-username-password '11111'