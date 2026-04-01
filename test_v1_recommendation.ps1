$headers = @{"X-User-Id" = "2"}
$response = Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/recommendations/meal?mealType=MORNING_FUEL' -Method GET -ContentType 'application/json' -Headers $headers -ErrorAction Stop
$response | ConvertTo-Json -Depth 5
