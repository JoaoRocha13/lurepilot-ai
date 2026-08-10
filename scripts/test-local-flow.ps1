param(
    [string]$BaseUrl = 'http://localhost:8080',
    [long]$SpotId = 3,
    [long[]]$LureIds = @(3, 4),
    [long]$LureLibraryItemId = 12,
    [string]$Species = 'Black Bass'
)

$ErrorActionPreference = 'Stop'

function Invoke-Api([string]$Method, [string]$Path, [object]$Body = $null) {
    $parameters = @{
        Method = $Method
        Uri = "$BaseUrl$Path"
        ContentType = 'application/json'
    }
    if ($null -ne $Body) {
        $parameters.Body = $Body | ConvertTo-Json -Depth 20
    }

    try {
        return Invoke-RestMethod @parameters
    } catch {
        $detail = $_.ErrorDetails.Message
        if ([string]::IsNullOrWhiteSpace($detail) -and $_.Exception.Response) {
            try {
                $reader = [System.IO.StreamReader]::new($_.Exception.Response.GetResponseStream())
                $detail = $reader.ReadToEnd()
                $reader.Dispose()
            } catch {
                $detail = $_.Exception.Message
            }
        }
        throw "API flow failed at ${Method} ${Path}: $detail"
    }
}

$date = Get-Date -Format 'yyyy-MM-dd'
$plan = Invoke-Api 'POST' '/api/plans' @{
    spotId = $SpotId
    plannedDate = $date
    plannedTime = '07:00'
    targetSpecies = $Species
    waterClarity = 'CLEAR'
    waterLevel = 'NORMAL'
    notes = 'Local end-to-end verification'
}
Write-Host "1/10 Plan created: $($plan.id)"

foreach ($lureId in $LureIds) {
    Invoke-Api 'POST' "/api/plans/$($plan.id)/lures" @{ lureId = $lureId } | Out-Null
}
Write-Host "2/10 Selected lures linked: $($LureIds -join ', ')"

$weather = Invoke-Api 'POST' "/api/weather-snapshots/plans/$($plan.id)"
Write-Host "3/10 Weather snapshot: $($weather.sourceLocationName)"

$solunar = Invoke-Api 'GET' "/api/solunar/spots/${SpotId}?date=${date}"
Write-Host "4/10 Solunar forecast: $($solunar.activityLevel)"

$recommendation = Invoke-Api 'POST' '/api/recommendations/plan' @{ planId = $plan.id }
Write-Host "5/10 AI recommendation: $($recommendation.id) (confidence $($recommendation.confidence))"

$session = Invoke-Api 'POST' '/api/sessions' @{
    spotId = $SpotId
    planId = $plan.id
    date = $date
    targetSpecies = $Species
    waterClarity = 'CLEAR'
    waterLevel = 'NORMAL'
}
Write-Host "6/10 Session created: $($session.id)"

$startedSession = Invoke-Api 'POST' "/api/sessions/$($session.id)/start" @{ startTime = '07:00' }
$adjustment = Invoke-Api 'POST' '/api/recommendations/session-adjustment' @{
    sessionId = $session.id
    situation = 'Sem toques ha 20 minutos'
    currentConditions = 'Agua clara e vento fraco'
}
Write-Host "7/10 Session started and adjusted: $($adjustment.id)"

$catch = Invoke-Api 'POST' "/api/sessions/$($session.id)/catches" @{
    species = $Species
    lureLibraryItemId = $LureLibraryItemId
    quantity = 1
    sizeCm = 35
    weightKg = 0.8
    released = $true
    photoUrl = '/demo/images/fish/freshwater/black-bass.png'
    photoThumbnailUrl = '/demo/images/fish/freshwater/black-bass.png'
    photoCaption = 'Local flow verification'
}
Write-Host "8/10 Catch registered: $($catch.id)"

$finishedSession = Invoke-Api 'POST' "/api/sessions/$($session.id)/finish" @{
    endTime = '08:15'
    success = $true
    resultSummary = 'Local end-to-end verification catch'
    rating = 4
}
$execution = Invoke-Api 'POST' "/api/recommendations/$($recommendation.id)/executions" @{
    sessionId = $session.id
    recommendationStep = 'PLAN_A'
    followed = $true
    result = 'CATCH'
    success = $true
    rating = 4
    startedAt = '07:00'
    endedAt = '07:20'
    notes = 'Plan A followed during local flow verification'
}
Write-Host "9/10 Session finished ($($finishedSession.durationMinutes) min) and recommendation evaluated: $($execution.id)"

$review = Invoke-Api 'POST' '/api/recommendations/session-review' @{ sessionId = $session.id }
$gallery = Invoke-Api 'GET' "/api/gallery/catches?sessionId=$($session.id)&withPhotoOnly=true"
$analytics = Invoke-Api 'GET' '/api/analytics/summary'
Write-Host "10/10 Review $($review.id), gallery items $($gallery.totalItems), analytics sessions $($analytics.totalSessions)"
Write-Host "Completed local flow for plan $($plan.id) and session $($session.id)."
