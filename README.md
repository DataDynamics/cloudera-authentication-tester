# Cloudera CDP Authentication Tester

Cloudera CDP 에 적용된 LDAP & Kerberos Authentication Tester를 위한 프로젝트입니다.

이 프로젝트는 Impala, Hive, HDFS 등의 인증 테스트를 지원합니다.

## Build

```
mvn clean package
```

## Cloudera CDP의 인증

다양한 인증 방식을 지원하지만 인증을 적용하는 경우 다음의 인증 방법을 적용합니다.

* LDAP 인증
  * Open LDAP 등의 LDAP 서버 사용 가능
  * Active Directory 권고
* Kerberos 인증
  * Active Directory 권고

Cloduera CDP에서는 LDAP 인증 및 Kerberos 인증을 동시에 적용할 수 있습니다. 예를 들어 Kerberos 인증 적용시 AD Join이 되어 있지 않은 PC 또는 Server에서 Kerberos 인증 사용이 어려울 수 있으므로 이 경우 LDAP 인증을 적용할 수 있습니다. 예를 들어 다음과 서비스가 대표적인 사례입니다.

* Apache Hive
* Apache Impala

## Kerberos Keytab 파일 생성

Keytab 파일이 있으면 패스워드 없이 principal을 사용할 수 있습니다. 다만 보안에 취약하므로 관리를 철저하게 해야 합니다.

Windows AD Server에서 Keytab 파일을 생성하는 방법은 다음과 같습니다.

```
ktpass [-?] | [/out filename] [/princ SPN] [/mapuser target_user] \
              [/mapop {add|set|delete}] [/pass {password|*|+rndpass}] \
              [/minlen length] [/maxlen length] [/oldpass old_password] \
              [/setpass] [/in filename] [/crypto {type}] \
              [/ptype {KRB5_NT_PRINCIPAL | KRB5_NT_SRV_INST | KRB5_NT_SRV_HST}] \
              [/kvno KeyVersionNumber] [/target server_name] \
              [/rndcrypt] [/nocopy] [/answer]
```

각 옵션은 다음과 같습니다.

* `/out <filename>` (필수):
  * 생성될 Keytab 파일의 경로와 이름을 지정합니다.
  * 예: `/out C:\keytabs\myservice.keytab`
* `/princ <SPN>` (필수):
  * Kerberos 주체 이름(Service Principal Name)을 지정합니다. 이 SPN은 클라이언트가 서비스를 식별하는 데 사용됩니다.
  * 형식: `<서비스_클래스>/<FQDN>@<REALM>` (REALM은 대문자여야 합니다.)
  * 예: `/princ HTTP/myserver.example.com@EXAMPLE.COM`
  * 예: `/princ host/myserver.example.com@EXAMPLE.COM`
* `/mapuser <target_user>` (필수):
  * SPN을 매핑할 Active Directory 사용자 계정 또는 컴퓨터 계정을 지정합니다.
  * 형식: `<user_name>@<domain>` 또는 `<domain>\<user_name>`
  * 예: `/mapuser svc_myweb@example.com`
  * 예: `/mapuser EXAMPLE\svc_myweb`
* `/mapop {add|set|delete}`:
  * `mapuser`와 `princ` 간의 매핑 작업 유형을 지정합니다.
    * `add`: 지정된 SPN을 사용자 계정에 추가합니다. (기존 SPN이 있어도 추가)
    * `set`: 지정된 SPN을 사용자 계정의 유일한 SPN으로 설정합니다. 이전에 설정된 모든 SPN을 제거하고 새로 지정된 SPN만 남깁니다. (가장 일반적으로 사용)
    * `delete`: 지정된 SPN을 사용자 계정에서 제거합니다.
  * 주의: `set` 옵션은 강력하므로, 사용 시 해당 계정에 연결된 다른 서비스가 없는지 확인해야 합니다.
* `/pass {password|*|+rndpass}`:
  * `mapuser`로 지정된 Active Directory 계정의 비밀번호를 지정합니다.
    * `<password>`: 명시적으로 비밀번호를 입력합니다. 경고: 명령 히스토리에 노출되므로 보안에 취약합니다.
    * `*`: 명령 실행 시 비밀번호를 직접 입력하라는 프롬프트를 띄웁니다. (권장)
    * `+rndpass`: 무작위 비밀번호를 생성하고 Active Directory 계정에 적용합니다. 이 비밀번호는 사용자에게 표시되지 않습니다. 이 옵션을 사용할 경우, 해당 계정으로 다른 서비스가 실행 중이라면 중단될 수 있습니다.
* `/crypto {type}`:
  * Keytab 파일에 포함될 암호화 유형(들)을 지정합니다. 여러 유형을 쉼표로 구분하여 지정할 수 있습니다.
  * 사용 가능한 유형: `RC4-HMAC-NT`, `AES256-SHA1`, `AES128-SHA1`, `DES-CBC-MD5`, `DES-CBC-CRC`
  * `all`: 지원되는 모든 암호화 유형을 포함합니다.
  * 권장: `AES256-SHA1` 또는 `all`을 사용하는 것이 좋습니다. 해당 Active Directory 계정이 선택된 암호화 유형을 지원하도록 설정되어 있어야 합니다 (예: "This account supports Kerberos AES 256 bit encryption" 옵션).
  * 예: `/crypto AES256-SHA1,AES128-SHA1`
  * 예: `/crypto all`
* `/ptype {KRB5_NT_PRINCIPAL | KRB5_NT_SRV_INST | KRB5_NT_SRV_HST}`:
  * 생성할 주체(principal)의 유형을 지정합니다.
    * `KRB5_NT_PRINCIPAL`: 일반적인 사용자 주체 유형입니다. (가장 일반적)
    * `KRB5_NT_SRV_INST`: 서비스 인스턴스 주체 유형입니다. (SPN에 사용)
    * `KRB5_NT_SRV_HST`: 호스트 서비스 주체 유형입니다. (컴퓨터 계정에 사용될 수 있음)
  * 대부분의 서비스 SPN에는 KRB5_NT_PRINCIPAL을 사용해도 무방합니다.
* `/kvno <KeyVersionNumber>`:
  * Keytab에 포함될 키 버전 번호(Key Version Number)를 수동으로 지정합니다.
  * 일반적으로 `ktpass`가 자동으로 가장 최신 키 버전을 사용하도록 하는 것이 좋습니다. 이 옵션은 특정 문제 해결 시에만 사용됩니다.
  * `+kvno`를 사용하여 기존 키 버전을 증가시킬 수도 있습니다.
* `/in <filename>`:
  * 새로운 항목을 추가할 기존 Keytab 파일을 지정합니다. 이 옵션을 사용하면 기존 Keytab에 SPN을 추가할 수 있습니다. (`-append`와 유사하게 작동)
  * 예: `/in C:\keytabs\existing.keytab`
* `/target <server_name>`:
  * Kerberos 주체를 찾을 도메인 컨트롤러를 지정합니다. 이 옵션은 특정 DC에 연결해야 할 때 사용됩니다.
  * 예: `/target DC01.example.com`
* `/rndcrypt`:
  * (제거됨 또는 더 이상 사용되지 않음) `+rndpass`와 유사하게, 암호화 키를 무작위로 생성하는 옵션으로 사용되었으나, 최신 버전에서는 `+rndpass`가 이 기능을 대체합니다.
* `/nocopy`:
  * (드물게 사용) 주체 정보를 Keytab에 복사하지 않도록 합니다. 일반적으로 사용하지 않습니다.
* `/answer`:
  * (숨겨진 옵션) 모든 프롬프트에 자동으로 '예'라고 응답합니다. 스크립트에서 자동화할 때 유용할 수 있지만, 주의해서 사용해야 합니다.
* `/?` 또는 `-?`:
  * `ktpass` 명령어의 사용법 및 옵션 목록을 표시합니다.

`ktpas` 커맨드의 사용법에 따라서 다음과 같이 Keytab 파일을 생성할 수 있습니다.

```
ktpass -out webservice.keytab \
       -princ HTTP/myservice.example.com@EXAMPLE.COM \
       -mapuser svc_webservice@example.com \
       -pass YourComplexPassword! \
       -ptype KRB5_NT_PRINCIPAL \
       -crypto AES256-SHA1 -mapop set
```

다음은 실제 적용 사례입니다.

```
ktpass -out cloudera.keytab -princ cloudera@DATALAKE.NET \
                            -mapuser DATALAKE_PROD\cloudera \
                            -pass Password!! \
                            -ptype KRB5_NT_PRINCIPAL -crypto all -mapop set
```

### Cloudera의 Kerbeors Keytab Format

* 기본 형식 : `service[/host]@REALM`
 * 예 : `hdfs/data-node-1.example.com@EXAMPLE.COM`

### Cloudera의 Hadoop Users (user:group) and Kerberos Principals

https://docs.cloudera.com/cdp-private-cloud-base/7.3.1/security-kerberos-authentication/topics/cm_sg_cm_users_principals.html

| Component (Version)      | Unix User ID | Groups            |
| ------------------------ | ------------ | ----------------- |
| Cloudera Manager         | cloudera-scm | cloudera-scm      |
| HDFS                     | hdfs         | hdfs, hadoop      |
| YARN                     | yarn         | yarn, hadoop      |
| HBase                    | hbase        | hbase, hadoop     |
| Hive                     | hive         | hive              |
| Impala                   | impala       | impala, hive      |
| Spark                    | spark        | spark             |
| Kafka                    | kafka        | kafka             |
| Kudu                     | kudu         | kudu              |
| ZooKeeper                | zookeeper    | zookeeper         |
| NiFi                     | nifi         | nifi              |

## Keytab 파일의 Pricipal 확인

```
# klist -e -k -t hdfs.keytab
Keytab name: WRFILE:hdfs.keytab
slot KVNO Principal
---- ---- ---------------------------------------------------------------------
   1    7    HTTP/fully.qualified.domain.name@YOUR-REALM.COM (DES cbc mode with CRC-32)
   2    7    HTTP/fully.qualified.domain.name@YOUR-REALM.COM (Triple DES cbc mode with HMAC/sha1)
   3    7    hdfs/fully.qualified.domain.name@YOUR-REALM.COM (DES cbc mode with CRC-32)
   4    7    hdfs/fully.qualified.domain.name@YOUR-REALM.COM (Triple DES cbc mode with HMAC/sha1)
```

## Keytab 파일의 다수의 Principal 적용

1개의 Keytab 파일에 다수의 principal을 적용하려면 우선 개별 principal의 keytab 파일이 있어야 하며, Linux 상에서 `ktutil` 커맨드로 병합이 가능합니다.

```
# ktutil
ktutil:  rkt hdfs.keytab
ktutil:  rkt yarn.keytab
ktutil:  wkt merged.keytab
ktutil:  quit
```

## Keytab 파일로 로그인

```
kinit -k -t cloudera.keytab cloudera@DATALAKE
```
