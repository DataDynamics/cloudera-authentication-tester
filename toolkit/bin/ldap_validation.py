from ldap3 import Server, Connection, ALL, MODIFY_REPLACE, Tls, SUBTREE, ALL_ATTRIBUTES, NTLM
import ssl

# LDAP 서버 정보
server_uri = "ldaps://10.0.1.79:636"
bind_dn = r"DATALAKE\Administrator"
bind_password = "Dd98969321$9"
user_base_dn = "OU=Users,OU=IBK,DC=DATALAKE,DC=NET"
domain_name = "DATALAKE.NET"
user_default_password = "@123qwe"

# 추가할 사용자 정보
new_username = "testuser"
new_user_cn = "Test User"
new_user_dn = f"CN={new_user_cn},{user_base_dn}"
new_user_samaccountname = new_username


def add_user_and_set_password():
    # 1. LDAP 서버 연결 (TLS: 인증서 검증 off, 테스트 목적)
    tls = Tls(validate=ssl.CERT_NONE)
    server = Server(server_uri, use_ssl=True, get_info=ALL, tls=tls)
    conn = Connection(server, user=bind_dn, password=bind_password, authentication=NTLM, auto_bind=True)

    # 2. 사용자 추가 (objectClass 등 AD 필수 속성 포함)
    attributes = {
        'objectClass': ['top', 'person', 'organizationalPerson', 'user'],
        'cn': new_user_cn,
        'sAMAccountName': new_user_samaccountname,
        'userPrincipalName': f"{new_user_samaccountname}@{domain_name}",
        'displayName': new_user_cn,
        'givenName': "Test",
        'sn': "User",
        'mail': f"{new_user_samaccountname}@{domain_name}",
        'accountExpires': '0',  # Never expires
    }
    conn.add(new_user_dn, attributes=attributes)
    if not conn.result["description"] == "success":
        print(f"User add failed: {conn.result}")
        return False

    print("User created.")

    # 3. 패스워드 설정 (AD는 패스워드 반드시 unicodePwd로, 특수 규칙)
    unicode_pass = f'"{user_default_password}"'.encode('utf-16-le')
    conn.modify(new_user_dn, {'unicodePwd': [(MODIFY_REPLACE, [unicode_pass])]})

    if not conn.result["description"] == "success":
        print(f"Password set failed: {conn.result}")
        return False

    print("Password set.")

    # 4. 계정 활성화 (userAccountControl에서 512: 정상 계정)
    conn.modify(new_user_dn, {'userAccountControl': [(MODIFY_REPLACE, [512])]})
    print("Account enabled.")

    conn.unbind()
    return True


def authenticate_user(username, password):
    # 인증: 새로 생성한 계정으로 바인드 시도
    user = f"{domain_name}\\{username}"
    tls = Tls(validate=ssl.CERT_NONE)
    server = Server(server_uri, use_ssl=True, get_info=ALL, tls=tls)
    try:
        conn = Connection(server, user=user, password=password, authentication=NTLM, auto_bind=True)
        print("Authentication succeeded.")
        conn.unbind()
        return True
    except Exception as e:
        print(f"Authentication failed: {e}")
        return False


if __name__ == "__main__":
    result = add_user_and_set_password()
    if result:
        auth_ok = authenticate_user("testuser", user_default_password)
        print("Final authentication test:", "OK" if auth_ok else "FAILED")
