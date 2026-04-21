#!/usr/bin/env powershell
<#
Script para probar la API E-Commerce mediante Postman
#>

$baseUrl = "http://127.0.0.1:8080"
$results = @()

function Test-Endpoint {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body,
        [string]$Token,
        [int]$ExpectedStatus = 200
    )

    $headers = @{"Content-Type" = "application/json"}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    try {
        if ($Body) {
            $resp = Invoke-WebRequest -Uri $Url -Method $Method -Headers $headers -Body ($Body | ConvertTo-Json) -UseBasicParsing -ErrorAction Stop
        } else {
            $resp = Invoke-WebRequest -Uri $Url -Method $Method -Headers $headers -UseBasicParsing -ErrorAction Stop
        }
        
        $success = $resp.StatusCode -eq $ExpectedStatus
        $status = $resp.StatusCode
    }
    catch {
        $status = $_.Exception.Response.StatusCode.Value__
        $success = $status -eq $ExpectedStatus
    }

    $results += [PSCustomObject]@{
        Name = $Name
        Method = $Method
        Status = $status
        Expected = $ExpectedStatus
        Success = $success
        URL = $Url
    }

    Write-Host "[$([datetime]::Now.ToString('HH:mm:ss'))] $Name - Status: $status - $(if($success) {'✓'} else {'✗'})"
    return $success
}

Write-Host "====== PRUEBAS DE API E-COMMERCE ======" -ForegroundColor Cyan
Write-Host "Base URL: $baseUrl" -ForegroundColor Cyan
Write-Host ""

# 1. Health Check
Write-Host "1. Health Check" -ForegroundColor Yellow
Test-Endpoint -Name "GET /api/health" -Method GET -Url "$baseUrl/api/health" -ExpectedStatus 200 | Out-Null

# 2. Registro
Write-Host "`n2. Autenticación - Registro" -ForegroundColor Yellow
$usuario1 = @{
    nombre = "Test User 1"
    email = "user1@example.com"
    telefono = "1111111111"
    password = "password123"
}
$regResult = Test-Endpoint -Name "POST /api/auth/register" -Method POST -Url "$baseUrl/api/auth/register" -Body $usuario1 -ExpectedStatus 200

# Obtener token
$tokenResp = try {
    $resp = Invoke-WebRequest -Uri "$baseUrl/api/auth/register" -Method POST -ContentType "application/json" -Body ($usuario1 | ConvertTo-Json) -UseBasicParsing
    $resp.Content | ConvertFrom-Json
} catch {
    $null
}
$token1 = $tokenResp.accessToken

# 3. Categorías
Write-Host "`n3. Categorías" -ForegroundColor Yellow
$categoria = @{
    nombre = "Electrónica"
    descripcion = "Productos electrónicos"
}
Test-Endpoint -Name "GET /api/categorias (autenticado)" -Method GET -Url "$baseUrl/api/categorias" -Token $token1 -ExpectedStatus 200 | Out-Null

# 4. Estados
Write-Host "`n4. Estados" -ForegroundColor Yellow
Test-Endpoint -Name "GET /api/estados (autenticado)" -Method GET -Url "$baseUrl/api/estados" -Token $token1 -ExpectedStatus 200 | Out-Null

# 5. Métodos de Pago
Write-Host "`n5. Métodos de Pago" -ForegroundColor Yellow
Test-Endpoint -Name "GET /api/metodos-pago (autenticado)" -Method GET -Url "$baseUrl/api/metodos-pago" -Token $token1 -ExpectedStatus 200 | Out-Null

# 6. Productos
Write-Host "`n6. Productos" -ForegroundColor Yellow
Test-Endpoint -Name "GET /api/productos (autenticado)" -Method GET -Url "$baseUrl/api/productos" -Token $token1 -ExpectedStatus 200 | Out-Null

# 7. Usuarios
Write-Host "`n7. Usuarios" -ForegroundColor Yellow
Test-Endpoint -Name "GET /api/users/me (perfil actual)" -Method GET -Url "$baseUrl/api/users/me" -Token $token1 -ExpectedStatus 200 | Out-Null

# 8. Direcciones de envío
Write-Host "`n8. Direcciones de Envío" -ForegroundColor Yellow
$direccion = @{
    direccion = "Calle Principal 123"
    ciudad = "Buenos Aires"
    codigoPostal = "1425"
    esPrincipal = $true
}
Test-Endpoint -Name "POST /api/direcciones" -Method POST -Url "$baseUrl/api/direcciones" -Body $direccion -Token $token1 -ExpectedStatus 201 | Out-Null

# 9. Carrito
Write-Host "`n9. Carrito" -ForegroundColor Yellow
Test-Endpoint -Name "GET /api/carrito (listar)" -Method GET -Url "$baseUrl/api/carrito" -Token $token1 -ExpectedStatus 200 | Out-Null

# Resumen
Write-Host "`n====== RESUMEN DE PRUEBAS ======" -ForegroundColor Cyan
$totalTests = $results.Count
$successTests = ($results | Where-Object {$_.Success}).Count
$failedTests = $totalTests - $successTests

Write-Host "Total de pruebas: $totalTests" -ForegroundColor White
Write-Host "Exitosas: $successTests" -ForegroundColor Green
Write-Host "Fallidas: $failedTests" -ForegroundColor $(if($failedTests -gt 0) {'Red'} else {'Green'})
Write-Host ""

# Detalle de resultados
Write-Host "====== DETALLE DE RESULTADOS ======" -ForegroundColor Cyan
$results | Format-Table -AutoSize

# Guardar resultados
$timestamp = Get-Date -Format "yyyy-MM-dd_HH-mm-ss"
$results | Export-Csv -Path ".\test_results_$timestamp.csv" -NoTypeInformation -Encoding UTF8
Write-Host "`nResultados guardados en: test_results_$timestamp.csv" -ForegroundColor Yellow
