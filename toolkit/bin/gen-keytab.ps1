param(
    [string]$principal,
    [string]$mapuser,
    [string]$password
)

# 인자 체크
if (-not $principal -or -not $mapuser -or -not $password) {
    Write-Host "사용법: gen-keytab.ps1 --principal <값> --mapuser <값> --password <값>"
    Write-Host "사용법: gen-keytab.ps1 --principal cloudera@DATALAKE.NET --mapuser DATALAKE_PROD\cloudera --password @123qwe"
    exit 1
}

ktpass -out "$principal".keytab -princ "$principal" \
                            -mapuser "$mapuser" \
                            -pass "$principal" \
                            -ptype KRB5_NT_PRINCIPAL -crypto all -mapop set
