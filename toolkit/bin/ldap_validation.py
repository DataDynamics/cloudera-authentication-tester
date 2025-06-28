import argparse
import ssl
from ldap3 import Server, Connection, ALL, MODIFY_REPLACE, Tls, ALL_ATTRIBUTES, NTLM


# LDAP 서버 정보
# args.server = "ldaps://10.0.1.79:636"
# bind_dn = r"DATALAKE\Administrator"
# bind_password = "@123qwe"
# user_base_dn = "OU=Users,DC=DATALAKE,DC=NET"
# domain_name = "DATALAKE.NET"
# new_username = "honggildong"
# new_username_password = "@123qwe"


def add_user_and_set_password(args):
    # 1. LDAP 서버 연결 (TLS: 인증서 검증 off, 테스트 목적)
    tls = Tls(validate=ssl.CERT_NONE)
    server = Server(args.server, use_ssl=is_ldaps_url(args.server), get_info=ALL, tls=tls)
    conn = Connection(server, user=args.bind_dn, password=args.bind_password, authentication=NTLM, auto_bind=True)

    # 2. 사용자 추가 (objectClass 등 AD 필수 속성 포함)
    attributes = {
        'objectClass': ['top', 'person', 'organizationalPerson', 'user'],
        'cn': f"{args.new_username}",
        'sAMAccountName': f"{args.new_username}",
        'userPrincipalName': f"{args.new_username}@{args.domain_name.lower()}",
        'displayName': f"{args.new_username}",
        'givenName': "Test",
        'sn': "User",
        'accountExpires': '0',  # Never expires
    }
    
    if user_dn_exists(conn, f"CN={args.new_username},{args.user_base_dn}"):
        if delete_user_dn(conn, f"CN={args.new_username},{args.user_base_dn}"):
            print("User deleted.")

    conn.add(f"CN={args.new_username},{args.user_base_dn}", attributes=attributes)
    if not conn.result["description"] == "success":
        print(f"User add failed: {conn.result}")
        return False

    print("User created.")

    # 3. 패스워드 설정 (AD는 패스워드 반드시 unicodePwd로, 특수 규칙)
    unicode_pass = f'"{args.new_username_password}"'.encode('utf-16-le')
    conn.modify(f"CN={args.new_username},{args.user_base_dn}", {'unicodePwd': [(MODIFY_REPLACE, [unicode_pass])]})

    if not conn.result["description"] == "success":
        print(f"Password set failed: {conn.result}")
        return False

    print("Password set.")

    # 4. 계정 활성화 (userAccountControl에서 512: 정상 계정)
    conn.modify(f"CN={args.new_username},{args.user_base_dn}", {'userAccountControl': [(MODIFY_REPLACE, [512])]})
    print("Account enabled.")

    conn.unbind()
    return True


def delete_user_dn(conn, user_dn) -> bool:
    try:
        return conn.delete(user_dn)
    except Exception:
        return False


def user_dn_exists(conn, user_dn) -> bool:
    try:
        conn.search(
            search_base=user_dn,
            search_filter='(objectClass=user)',
            search_scope='BASE',  # DN 하나만 탐색
            attributes=ALL_ATTRIBUTES
        )
        # 검색 결과가 있으면 존재하는 것
        return len(conn.entries) > 0
    except Exception:
        return False


def authenticate_user(args):
    user = f"{args.domain_name}\\{args.new_username}"
    tls = Tls(validate=ssl.CERT_NONE)
    server = Server(args.server, use_ssl=is_ldaps_url(args.server), get_info=ALL, tls=tls)
    try:
        conn = Connection(server, user=user, password=args.new_username_password, authentication=NTLM, auto_bind=True)
        print("Authentication succeeded.")
        conn.unbind()
        return True
    except Exception as e:
        print(f"Authentication failed: {e}")
        return False


def is_ldaps_url(url: str) -> bool:
    """
    Determines if a given URL starts with the "ldaps://" scheme.

    This function checks whether the input URL is using the "ldaps://"
    protocol. It performs a case-insensitive comparison by converting the
    input string to lowercase and verifying the prefix.

    :param url: The URL string to check.
    :type url: str
    :return: True if the URL starts with "ldaps://", False otherwise.
    :rtype: bool
    """
    return url.lower().startswith("ldaps://")


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='AD LDAP Connection Validator')
    parser.add_argument('--server', required=True, help='LDAP URL (예; ldaps://10.0.1.79:636)')
    parser.add_argument('--bind-dn', required=True, help='인증을 위한 Bind DN (AD의 경우; DATALAKE\Administrator)')
    parser.add_argument('--bind-password', required=True, help='인증을 위한 Bind Password')
    parser.add_argument('--user-base-dn', required=True, help='사용자가 등록되어 있는 User DN (예; OU=Users,DC=DATALAKE,DC=NET)')
    parser.add_argument('--domain-name', required=True, help='Active Directory의 Domain Name (예; DATALAKE.NET)')
    parser.add_argument('--new-username', required=True, help='신규로 생성할 사용자명')
    parser.add_argument('--new-username-password', required=True, help='신규로 생성할 사용자의 기본 패스워드')

    args = parser.parse_args()
    
    result = add_user_and_set_password(args)

    if result:
        auth_ok = authenticate_user(args)
        print("Final authentication test:", "OK" if auth_ok else "FAILED")
