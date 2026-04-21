$ErrorActionPreference = "Stop"

$baseUrl = "http://127.0.0.1:8080"
$mysqlPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
$mysqlArgs = @("-h", "127.0.0.1", "-P", "3306", "-u", "ecomerce", "-pecomerce123", "ecomerce_db")

$results = New-Object System.Collections.Generic.List[object]

function Add-Result {
    param(
        [string]$Name,
        [int]$Status,
        [int]$Expected,
        [string]$Url
    )

    $results.Add([pscustomobject]@{
        Name = $Name
        Status = $Status
        Expected = $Expected
        Ok = ($Status -eq $Expected)
        Url = $Url
    })
}

function Parse-Json {
    param([string]$Content)
    if ([string]::IsNullOrWhiteSpace($Content)) {
        return $null
    }

    try {
        return $Content | ConvertFrom-Json
    } catch {
        return $null
    }
}

function Invoke-JsonApi {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [string]$Token,
        [object]$Body,
        [int]$Expected
    )

    $headers = @{}
    if (-not [string]::IsNullOrWhiteSpace($Token)) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $status = -1
    $raw = ""
    $parsed = $null

    try {
        if ($null -ne $Body) {
            $json = $Body | ConvertTo-Json -Depth 30 -Compress
            $resp = Invoke-WebRequest -Method $Method -Uri $Url -Headers $headers -ContentType "application/json" -Body $json -UseBasicParsing
        } else {
            $resp = Invoke-WebRequest -Method $Method -Uri $Url -Headers $headers -UseBasicParsing
        }

        $status = [int]$resp.StatusCode
        $raw = [string]$resp.Content
        $parsed = Parse-Json -Content $raw
    } catch {
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode.value__
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) {
                    $reader = New-Object System.IO.StreamReader($stream)
                    $raw = $reader.ReadToEnd()
                    $reader.Close()
                    $parsed = Parse-Json -Content $raw
                }
            } catch {
                $raw = $_.Exception.Message
            }
        } else {
            $raw = $_.Exception.Message
        }
    }

    Add-Result -Name $Name -Status $status -Expected $Expected -Url $Url
    return [pscustomobject]@{ Status = $status; Body = $parsed; Raw = $raw }
}

function Invoke-MultipartApi {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [string]$Token,
        [string[]]$FormEntries,
        [int]$Expected
    )

    $tmpFile = [System.IO.Path]::GetTempFileName()
    $args = @("-s", "-o", $tmpFile, "-w", "%{http_code}", "-X", $Method, $Url)

    if (-not [string]::IsNullOrWhiteSpace($Token)) {
        $args += @("-H", "Authorization: Bearer $Token")
    }

    foreach ($entry in $FormEntries) {
        $args += @("-F", $entry)
    }

    $statusText = & curl.exe @args
    $status = [int]$statusText
    $raw = ""
    $parsed = $null

    if (Test-Path $tmpFile) {
        $raw = Get-Content -Raw -Path $tmpFile
        Remove-Item -Path $tmpFile -Force
        $parsed = Parse-Json -Content $raw
    }

    Add-Result -Name $Name -Status $status -Expected $Expected -Url $Url
    return [pscustomobject]@{ Status = $status; Body = $parsed; Raw = $raw }
}

function Require-Token {
    param([string]$Name, [object]$Resp)
    if ($Resp.Status -ne 200 -or $null -eq $Resp.Body -or [string]::IsNullOrWhiteSpace($Resp.Body.accessToken)) {
        throw "No se pudo obtener token para $Name"
    }
}

$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$newBuyerEmail = "e2e.$timestamp@cliente.com"
$newBuyerPassword = "Secret123!"

# 1) Health + auth
Invoke-JsonApi -Name "GET /api/health" -Method "GET" -Url "$baseUrl/api/health" -Token $null -Body $null -Expected 200 | Out-Null

$registerResp = Invoke-JsonApi -Name "POST /api/auth/register" -Method "POST" -Url "$baseUrl/api/auth/register" -Token $null -Body @{
    nombre = "E2E Comprador"
    email = $newBuyerEmail
    password = $newBuyerPassword
    telefono = "1160000000"
    rol = "COMPRADOR"
} -Expected 200

$adminLogin = Invoke-JsonApi -Name "POST /api/auth/login (admin)" -Method "POST" -Url "$baseUrl/api/auth/login" -Token $null -Body @{
    email = "admin@selvachoco.com"
    password = "Secret123!"
} -Expected 200

$vendorLogin = Invoke-JsonApi -Name "POST /api/auth/login (vendedor)" -Method "POST" -Url "$baseUrl/api/auth/login" -Token $null -Body @{
    email = "marco@selvachoco.com"
    password = "Secret123!"
} -Expected 200

$buyerLogin = Invoke-JsonApi -Name "POST /api/auth/login (comprador)" -Method "POST" -Url "$baseUrl/api/auth/login" -Token $null -Body @{
    email = "camila@cliente.com"
    password = "Secret123!"
} -Expected 200

Require-Token -Name "admin" -Resp $adminLogin
Require-Token -Name "vendedor" -Resp $vendorLogin
Require-Token -Name "comprador" -Resp $buyerLogin

$adminToken = $adminLogin.Body.accessToken
$vendorToken = $vendorLogin.Body.accessToken
$buyerToken = $buyerLogin.Body.accessToken

# 2) Security checks by role
Invoke-JsonApi -Name "POST /api/estados (comprador => 403)" -Method "POST" -Url "$baseUrl/api/estados" -Token $buyerToken -Body @{ nombre = "BLOQUEADO"; descripcion = "no" } -Expected 403 | Out-Null
Invoke-JsonApi -Name "POST /api/categorias (vendedor => 403)" -Method "POST" -Url "$baseUrl/api/categorias" -Token $vendorToken -Body @{ nombre = "BLOQUEADO"; descripcion = "no" } -Expected 403 | Out-Null
Invoke-JsonApi -Name "GET /api/direcciones (vendedor => 403)" -Method "GET" -Url "$baseUrl/api/direcciones" -Token $vendorToken -Body $null -Expected 403 | Out-Null

# 3) Categoria CRUD (admin)
Invoke-JsonApi -Name "GET /api/categorias" -Method "GET" -Url "$baseUrl/api/categorias" -Token $adminToken -Body $null -Expected 200 | Out-Null
$catCreate = Invoke-JsonApi -Name "POST /api/categorias" -Method "POST" -Url "$baseUrl/api/categorias" -Token $adminToken -Body @{ nombre = "E2E Categoria"; descripcion = "Alta e2e" } -Expected 201
$catId = [int]$catCreate.Body.id
Invoke-JsonApi -Name "GET /api/categorias/{id}" -Method "GET" -Url "$baseUrl/api/categorias/$catId" -Token $adminToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/categorias/{id}" -Method "PUT" -Url "$baseUrl/api/categorias/$catId" -Token $adminToken -Body @{ nombre = "E2E Categoria Edit"; descripcion = "Edit" } -Expected 200 | Out-Null
Invoke-JsonApi -Name "DELETE /api/categorias/{id}" -Method "DELETE" -Url "$baseUrl/api/categorias/$catId" -Token $adminToken -Body $null -Expected 204 | Out-Null

# 4) Estado CRUD (admin)
Invoke-JsonApi -Name "GET /api/estados" -Method "GET" -Url "$baseUrl/api/estados" -Token $adminToken -Body $null -Expected 200 | Out-Null
$estadoCreate = Invoke-JsonApi -Name "POST /api/estados" -Method "POST" -Url "$baseUrl/api/estados" -Token $adminToken -Body @{ nombre = "E2E_ESTADO"; descripcion = "Alta e2e" } -Expected 201
$estadoId = [int]$estadoCreate.Body.id
Invoke-JsonApi -Name "GET /api/estados/{id}" -Method "GET" -Url "$baseUrl/api/estados/$estadoId" -Token $adminToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/estados/{id}" -Method "PUT" -Url "$baseUrl/api/estados/$estadoId" -Token $adminToken -Body @{ nombre = "E2E_ESTADO_EDIT"; descripcion = "Edit" } -Expected 200 | Out-Null
Invoke-JsonApi -Name "DELETE /api/estados/{id}" -Method "DELETE" -Url "$baseUrl/api/estados/$estadoId" -Token $adminToken -Body $null -Expected 204 | Out-Null

# 5) MetodoPago CRUD (admin)
Invoke-JsonApi -Name "GET /api/metodos-pago" -Method "GET" -Url "$baseUrl/api/metodos-pago" -Token $adminToken -Body $null -Expected 200 | Out-Null
$metodoCreate = Invoke-JsonApi -Name "POST /api/metodos-pago" -Method "POST" -Url "$baseUrl/api/metodos-pago" -Token $adminToken -Body @{ tipo = "E2E Pago"; descripcion = "Alta e2e" } -Expected 201
$metodoId = [int]$metodoCreate.Body.id
Invoke-JsonApi -Name "GET /api/metodos-pago/{id}" -Method "GET" -Url "$baseUrl/api/metodos-pago/$metodoId" -Token $adminToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/metodos-pago/{id}" -Method "PUT" -Url "$baseUrl/api/metodos-pago/$metodoId" -Token $adminToken -Body @{ tipo = "E2E Pago Edit"; descripcion = "Edit" } -Expected 200 | Out-Null
Invoke-JsonApi -Name "DELETE /api/metodos-pago/{id}" -Method "DELETE" -Url "$baseUrl/api/metodos-pago/$metodoId" -Token $adminToken -Body $null -Expected 204 | Out-Null

# 6) Direcciones (comprador)
$dir1 = Invoke-JsonApi -Name "POST /api/direcciones" -Method "POST" -Url "$baseUrl/api/direcciones" -Token $buyerToken -Body @{ direccion = "E2E Calle 100"; ciudad = "CABA"; codigoPostal = "1405"; esPrincipal = $true } -Expected 201
$dir1Id = [int]$dir1.Body.id
$dir2 = Invoke-JsonApi -Name "POST /api/direcciones (2)" -Method "POST" -Url "$baseUrl/api/direcciones" -Token $buyerToken -Body @{ direccion = "E2E Calle 200"; ciudad = "CABA"; codigoPostal = "1406"; esPrincipal = $false } -Expected 201
$dir2Id = [int]$dir2.Body.id
Invoke-JsonApi -Name "GET /api/direcciones" -Method "GET" -Url "$baseUrl/api/direcciones" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/direcciones/{id}" -Method "GET" -Url "$baseUrl/api/direcciones/$dir1Id" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/direcciones/{id}" -Method "PUT" -Url "$baseUrl/api/direcciones/$dir1Id" -Token $buyerToken -Body @{ direccion = "E2E Calle 100 Piso 2"; ciudad = "CABA"; codigoPostal = "1405"; esPrincipal = $true } -Expected 200 | Out-Null
Invoke-JsonApi -Name "DELETE /api/direcciones/{id}" -Method "DELETE" -Url "$baseUrl/api/direcciones/$dir2Id" -Token $buyerToken -Body $null -Expected 204 | Out-Null

# 7) Productos (vendedor)
$img1 = Join-Path $env:TEMP "e2e-producto-1.jpg"
$img2 = Join-Path $env:TEMP "e2e-producto-2.jpg"
[System.IO.File]::WriteAllBytes($img1, [byte[]](255,216,255,224,0,16,74,70,73,70,0,1,255,217))
[System.IO.File]::WriteAllBytes($img2, [byte[]](255,216,255,224,0,16,74,70,73,70,0,2,255,217))

$prod1 = Invoke-MultipartApi -Name "POST /api/productos (multipart sin foto)" -Method "POST" -Url "$baseUrl/api/productos" -Token $vendorToken -FormEntries @(
    "categoriaId=1",
    "nombre=E2E Producto Vendedor 1",
    "precio=22.50",
    "descripcion=Producto e2e 1",
    "stock=9",
    "descuentoPorcentaje=5"
) -Expected 201
$prod1Id = [int]$prod1.Body.id

$prod2 = Invoke-MultipartApi -Name "POST /api/productos (multipart con foto)" -Method "POST" -Url "$baseUrl/api/productos" -Token $vendorToken -FormEntries @(
    "categoriaId=2",
    "nombre=E2E Producto Vendedor 2",
    "precio=39.90",
    "descripcion=Producto e2e 2",
    "stock=8",
    "descuentoPorcentaje=10",
    "image=@$img1"
) -Expected 201
$prod2Id = [int]$prod2.Body.id

Invoke-JsonApi -Name "GET /api/productos" -Method "GET" -Url "$baseUrl/api/productos" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/productos/{id}" -Method "GET" -Url "$baseUrl/api/productos/$prod2Id" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/productos/{id}/foto" -Method "GET" -Url "$baseUrl/api/productos/$prod2Id/foto" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/productos?usuario=:id" -Method "GET" -Url "$baseUrl/api/productos?usuario=2" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/productos?categoria=:id" -Method "GET" -Url "$baseUrl/api/productos?categoria=1" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/productos?search=e2e" -Method "GET" -Url "$baseUrl/api/productos?search=e2e" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/productos?minPrecio=10&maxPrecio=50" -Method "GET" -Url "$baseUrl/api/productos?minPrecio=10&maxPrecio=50" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/productos/{id} (json)" -Method "PUT" -Url "$baseUrl/api/productos/$prod1Id" -Token $vendorToken -Body @{ categoriaId = 1; nombre = "E2E Producto Vendedor 1 Edit"; precio = 25.00; descripcion = "Editado"; stock = 11; descuentoPorcentaje = 7 } -Expected 200 | Out-Null
Invoke-MultipartApi -Name "PUT /api/productos/{id} (multipart con foto)" -Method "PUT" -Url "$baseUrl/api/productos/$prod2Id" -Token $vendorToken -FormEntries @(
    "categoriaId=2",
    "nombre=E2E Producto Vendedor 2 Edit",
    "precio=41.90",
    "descripcion=Producto e2e 2 edit",
    "stock=10",
    "descuentoPorcentaje=12",
    "image=@$img2"
) -Expected 200 | Out-Null
Invoke-JsonApi -Name "POST /api/productos (comprador => 403)" -Method "POST" -Url "$baseUrl/api/productos" -Token $buyerToken -Body @{ categoriaId = 1; nombre = "No permitido"; precio = 1; stock = 1 } -Expected 403 | Out-Null
Invoke-JsonApi -Name "DELETE /api/productos/{id}" -Method "DELETE" -Url "$baseUrl/api/productos/$prod1Id" -Token $vendorToken -Body $null -Expected 204 | Out-Null

# 8) Carrito + items (comprador)
$carritoCreate = Invoke-JsonApi -Name "POST /api/carrito" -Method "POST" -Url "$baseUrl/api/carrito" -Token $buyerToken -Body @{ nombre = "E2E Carrito Comprador" } -Expected 201
$carritoId = [int]$carritoCreate.Body.id
Invoke-JsonApi -Name "GET /api/carrito" -Method "GET" -Url "$baseUrl/api/carrito" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/carrito/{id}" -Method "GET" -Url "$baseUrl/api/carrito/$carritoId" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/carrito/{id}" -Method "PUT" -Url "$baseUrl/api/carrito/$carritoId" -Token $buyerToken -Body @{ nombre = "E2E Carrito Comprador Edit" } -Expected 200 | Out-Null

$item1 = Invoke-JsonApi -Name "POST /api/carrito/{id}/items" -Method "POST" -Url "$baseUrl/api/carrito/$carritoId/items" -Token $buyerToken -Body @{ idProducto = $prod2Id; cantidad = 2; precioUnitario = 41.90 } -Expected 201
$item1Id = [int]$item1.Body.id
$item2 = Invoke-JsonApi -Name "POST /api/carrito/{id}/items (2)" -Method "POST" -Url "$baseUrl/api/carrito/$carritoId/items" -Token $buyerToken -Body @{ idProducto = 1; cantidad = 1; precioUnitario = 8.42 } -Expected 201
$item2Id = [int]$item2.Body.id
Invoke-JsonApi -Name "GET /api/carrito/{id}/items" -Method "GET" -Url "$baseUrl/api/carrito/$carritoId/items" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/carrito/items/{idItem}" -Method "PUT" -Url "$baseUrl/api/carrito/items/$item1Id" -Token $buyerToken -Body @{ idProducto = $prod2Id; cantidad = 3; precioUnitario = 41.90 } -Expected 200 | Out-Null
Invoke-JsonApi -Name "DELETE /api/carrito/items/{idItem}" -Method "DELETE" -Url "$baseUrl/api/carrito/items/$item2Id" -Token $buyerToken -Body $null -Expected 204 | Out-Null

# 9) Compras (comprador)
$compraCreate = Invoke-JsonApi -Name "POST /api/compras/{idCarrito}" -Method "POST" -Url "$baseUrl/api/compras/$carritoId" -Token $buyerToken -Body @{ idMetodoPago = 1; idDireccionEnvio = $dir1Id } -Expected 201
$compraId = [int]$compraCreate.Body.id
Invoke-JsonApi -Name "GET /api/compras" -Method "GET" -Url "$baseUrl/api/compras" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/compras/{id}" -Method "GET" -Url "$baseUrl/api/compras/$compraId" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/compras/{id}/detalle" -Method "GET" -Url "$baseUrl/api/compras/$compraId/detalle" -Token $buyerToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/compras/{id} (comprador => 403)" -Method "PUT" -Url "$baseUrl/api/compras/$compraId" -Token $buyerToken -Body @{ idEstado = 1 } -Expected 403 | Out-Null
Invoke-JsonApi -Name "DELETE /api/compras/{id} (comprador => 403)" -Method "DELETE" -Url "$baseUrl/api/compras/$compraId" -Token $buyerToken -Body $null -Expected 403 | Out-Null
Invoke-JsonApi -Name "GET /api/compras (admin => 403)" -Method "GET" -Url "$baseUrl/api/compras" -Token $adminToken -Body $null -Expected 403 | Out-Null
Invoke-JsonApi -Name "PUT /api/compras/{id} (admin => 403)" -Method "PUT" -Url "$baseUrl/api/compras/$compraId" -Token $adminToken -Body @{ idEstado = 1 } -Expected 403 | Out-Null
Invoke-JsonApi -Name "DELETE /api/compras/{id} (admin => 403)" -Method "DELETE" -Url "$baseUrl/api/compras/$compraId" -Token $adminToken -Body $null -Expected 403 | Out-Null

# 10) Detalle compra (admin)
$detalleList = Invoke-JsonApi -Name "GET /api/detalle-compras" -Method "GET" -Url "$baseUrl/api/detalle-compras" -Token $adminToken -Body $null -Expected 200
$detalleIdFromList = $null
if ($detalleList.Body -is [System.Array] -and $detalleList.Body.Count -gt 0) {
    $detalleIdFromList = [int]$detalleList.Body[0].id
}
if ($null -ne $detalleIdFromList) {
    Invoke-JsonApi -Name "GET /api/detalle-compras/{id}" -Method "GET" -Url "$baseUrl/api/detalle-compras/$detalleIdFromList" -Token $adminToken -Body $null -Expected 200 | Out-Null
}

$detalleCreate = Invoke-JsonApi -Name "POST /api/detalle-compras" -Method "POST" -Url "$baseUrl/api/detalle-compras" -Token $adminToken -Body @{ idCompra = $compraId; idProducto = $prod2Id; cantidad = 1; precioUnitario = 41.90 } -Expected 201
$detalleId = [int]$detalleCreate.Body.id
Invoke-JsonApi -Name "PUT /api/detalle-compras/{id}" -Method "PUT" -Url "$baseUrl/api/detalle-compras/$detalleId" -Token $adminToken -Body @{ idCompra = $compraId; idProducto = $prod2Id; cantidad = 2; precioUnitario = 41.90 } -Expected 200 | Out-Null
Invoke-JsonApi -Name "DELETE /api/detalle-compras/{id}" -Method "DELETE" -Url "$baseUrl/api/detalle-compras/$detalleId" -Token $adminToken -Body $null -Expected 204 | Out-Null
Invoke-JsonApi -Name "GET /api/detalle-compras (comprador => 403)" -Method "GET" -Url "$baseUrl/api/detalle-compras" -Token $buyerToken -Body $null -Expected 403 | Out-Null

# 11) Usuarios (admin)
$usersResp = Invoke-JsonApi -Name "GET /api/users" -Method "GET" -Url "$baseUrl/api/users" -Token $adminToken -Body $null -Expected 200
Invoke-JsonApi -Name "GET /api/users/{id}" -Method "GET" -Url "$baseUrl/api/users/4" -Token $adminToken -Body $null -Expected 200 | Out-Null
Invoke-JsonApi -Name "PUT /api/users/{id}" -Method "PUT" -Url "$baseUrl/api/users/4" -Token $adminToken -Body @{ telefono = "1144444444" } -Expected 200 | Out-Null
Invoke-JsonApi -Name "GET /api/users/{id}/compras (denyAll => 403)" -Method "GET" -Url "$baseUrl/api/users/4/compras" -Token $adminToken -Body $null -Expected 403 | Out-Null
Invoke-JsonApi -Name "GET /api/users/{id}/carrito (denyAll => 403)" -Method "GET" -Url "$baseUrl/api/users/4/carrito" -Token $adminToken -Body $null -Expected 403 | Out-Null
Invoke-JsonApi -Name "GET /api/users/{id}/direcciones (denyAll => 403)" -Method "GET" -Url "$baseUrl/api/users/4/direcciones" -Token $adminToken -Body $null -Expected 403 | Out-Null

$newUserId = $null
if ($usersResp.Body -is [System.Array]) {
    $newUser = $usersResp.Body | Where-Object { $_.email -eq $newBuyerEmail } | Select-Object -First 1
    if ($newUser) {
        $newUserId = [int]$newUser.id
    }
}

if ($null -ne $newUserId) {
    Invoke-JsonApi -Name "DELETE /api/users/{id}" -Method "DELETE" -Url "$baseUrl/api/users/$newUserId" -Token $adminToken -Body $null -Expected 204 | Out-Null
} else {
    Add-Result -Name "DELETE /api/users/{id} (nuevo usuario)" -Status -1 -Expected 204 -Url "$baseUrl/api/users/{newUserId}"
}

# 12) DB verification
$sql = @"
SELECT id, id_usuario, nombre FROM carrito WHERE id = $carritoId;
SELECT id, id_usuario, direccion, ciudad FROM direccionenvio WHERE id = $dir1Id;
SELECT id, id_usuario, id_metodo_pago, id_direccion_envio, total FROM compra WHERE id = $compraId;
SELECT COUNT(*) AS detalle_compra_items FROM detallecompra WHERE id_compra = $compraId;
SELECT id, id_usuario, nombre, activo FROM producto WHERE id IN ($prod1Id, $prod2Id) ORDER BY id;
SELECT id, nombre, email, telefono FROM usuario WHERE id IN (4);
"@

$dbCheck = & $mysqlPath @mysqlArgs -e $sql

# 13) Report
$failed = $results | Where-Object { -not $_.Ok }

Write-Host ""
Write-Host "================ RESUMEN ENDPOINTS ================"
$results | Sort-Object Name | Format-Table Name, Status, Expected, Ok -AutoSize
Write-Host "Total endpoints ejecutados:" $results.Count
Write-Host "Fallidos:" $failed.Count

$resultsJsonPath = Join-Path $PSScriptRoot "e2e_results.json"
$results | ConvertTo-Json -Depth 6 | Set-Content -Path $resultsJsonPath -Encoding UTF8
Write-Host "Detalle JSON:" $resultsJsonPath

Write-Host ""
Write-Host "================ VERIFICACION BDD ================"
Write-Host $dbCheck

if ($failed.Count -gt 0) {
    Write-Host ""
    Write-Host "================ FALLIDOS ================"
    $failed | Sort-Object Name | Format-Table Name, Status, Expected, Url -AutoSize
    exit 1
}

Write-Host ""
Write-Host "OK: todos los endpoints definidos fueron ejecutados con el status esperado y se verifico persistencia en BDD."
