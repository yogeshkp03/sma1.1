$response = Invoke-RestMethod -Uri 'http://localhost:8080/api/restaurants/1/menu' -Method GET -ContentType 'application/json' -ErrorAction Stop
$response | ConvertTo-Json -Depth 3
