$response = Invoke-RestMethod -Uri 'http://localhost:8080/api/restaurants' -Method GET -ContentType 'application/json' -ErrorAction Stop
$response | ConvertTo-Json -Depth 5
