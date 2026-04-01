$body = @{
    email = "testvalid456@test.com"
    password = "password123"
    fullName = "Test User"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/auth/register' -Method POST -ContentType 'application/json' -Body $body -ErrorAction Stop
    $response | ConvertTo-Json
} catch {
    Write-Host "Error: $($_.Exception.Response.StatusCode.Value__)"
    Write-Host "Details: $_"
}
