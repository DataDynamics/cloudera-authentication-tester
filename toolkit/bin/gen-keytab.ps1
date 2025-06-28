param(
    [string]$principal,
    [string]$mapuser
)

# 인자 체크
if (-not $principal -or -not $mapuser) {
    Write-Host "Usage: gen-keytab.ps1 -principal <VALUE> -mapuser <VALUE>"
    Write-Host "Usage: gen-keytab.ps1 -principal cloudera@DATALAKE.NET -mapuser DATALAKE_PROD\cloudera"
    exit 1
}

ktpass -out $principal.keytab -princ $principal -mapuser $mapuser -pass * -ptype KRB5_NT_PRINCIPAL -crypto all -mapop set
