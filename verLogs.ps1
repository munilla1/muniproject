param(
    [string]$service = "muniproject"
)

Write-Host "Mostrando logs del servicio: $service (filtrando ERROR y Exception)" -ForegroundColor Green

# Muestra los logs en tiempo real y filtra ERROR o Exception
railway logs --service $service | Select-String "ERROR|Exception"
