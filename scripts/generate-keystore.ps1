param(
    [string]$Alias = "memoria",
    [string]$OutFile = "release.jks",
    [string]$Password = ""
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($Password)) {
    $secure = Read-Host "Elegi una contrasena para el keystore (no se muestra)" -AsSecureString
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try {
        $Password = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

if (Test-Path -LiteralPath $OutFile) {
    throw "Ya existe $OutFile. No lo sobrescribas: perdes la posibilidad de actualizar la app."
}

$keytool = Get-Command keytool -ErrorAction SilentlyContinue
if (-not $keytool) {
    throw "No se encontro keytool. Instala un JDK 17 o usa la terminal de Android Studio."
}

& keytool -genkeypair -v `
    -keystore $OutFile `
    -alias $Alias `
    -keyalg RSA `
    -keysize 2048 `
    -validity 10000 `
    -storepass $Password `
    -keypass $Password `
    -dname "CN=Memoria Activa, O=Memoria Activa, C=AR"

if ($LASTEXITCODE -ne 0) {
    throw "keytool fallo con codigo $LASTEXITCODE"
}

$bytes = [IO.File]::ReadAllBytes((Resolve-Path -LiteralPath $OutFile))
$base64File = "$OutFile.base64"
[IO.File]::WriteAllText($base64File, [Convert]::ToBase64String($bytes), [Text.Encoding]::ASCII)

Write-Host ""
Write-Host "Keystore creado: $OutFile" -ForegroundColor Green
Write-Host "Copia Base64 para el secret SIGNING_KEYSTORE_BASE64: $base64File"
Write-Host ""
Write-Host "Guarda el .jks y su contrasena en un lugar seguro y privado."
Write-Host "Si perdes este archivo no vas a poder publicar actualizaciones sobre la app instalada."
Write-Host ""
Write-Host "Secrets que tenes que cargar en GitHub:"
Write-Host "  SIGNING_KEYSTORE_BASE64 = contenido de $base64File"
Write-Host "  KEYSTORE_PASSWORD       = la contrasena que elegiste"
Write-Host "  KEY_ALIAS               = $Alias"
Write-Host "  KEY_PASSWORD            = la contrasena que elegiste"
