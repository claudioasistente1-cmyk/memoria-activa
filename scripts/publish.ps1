param(
    [Parameter(Mandatory = $true)]
    [string]$Version
)

$ErrorActionPreference = "Stop"

if ($Version -notmatch '^\d+\.\d+\.\d+$') {
    throw "Usa el formato X.Y.Z, por ejemplo: 1.0.1"
}

$parts = $Version.Split('.')
$code = [int]$parts[0] * 10000 + [int]$parts[1] * 100 + [int]$parts[2]

if ($code -lt 1) {
    throw "VersionCode invalido"
}

$tracked = git status --porcelain
if ($tracked) {
    throw "Hay cambios sin commitear. Commitealos o descartalos antes de publicar."
}

"versionCode=$code`nversionName=$Version" | Set-Content -LiteralPath "version.properties" -Encoding ASCII

git add version.properties
git commit -m "Release $Version"

if ($LASTEXITCODE -ne 0) { throw "Fallo el commit" }

git tag "v$Version"
if ($LASTEXITCODE -ne 0) { throw "Fallo la creacion del tag" }

git push origin HEAD
if ($LASTEXITCODE -ne 0) { throw "Fallo el push de la rama" }

git push origin "v$Version"
if ($LASTEXITCODE -ne 0) { throw "Fallo el push del tag" }

Write-Host ""
Write-Host "Tag v$Version publicado." -ForegroundColor Green
Write-Host "GitHub Actions va a compilar, firmar y publicar la APK y el update.json."
