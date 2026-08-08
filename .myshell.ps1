$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

Write-Host "Building My Java Shell..."

mvn -q -B package

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed."
    exit $LASTEXITCODE
}

$jar = Get-ChildItem "$projectRoot\target\*.jar" |
       Where-Object { $_.Name -notmatch "(sources|javadoc|original)" } |
       Select-Object -First 1

if (-not $jar) {
    Write-Host "Could not find JAR in target/"
    exit 1
}

Write-Host "Launching shell..."
Write-Host

java --enable-native-access=ALL-UNNAMED `
     --enable-preview `
     -jar $jar.FullName