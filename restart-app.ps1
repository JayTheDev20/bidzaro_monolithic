# Restart Spring Boot Application Script

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Spring Boot Application Restart" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

# Stop any running Java processes
Write-Host "Stopping existing Java processes..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Navigate to project directory
Set-Location "C:\Users\dhanu\bidzaro\bidzaro_monolithic"

# Clean and compile
Write-Host "Compiling project..." -ForegroundColor Yellow
mvn clean compile -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Compilation successful!" -ForegroundColor Green
    Write-Host ""

    # Start application
    Write-Host "Starting Spring Boot application..." -ForegroundColor Yellow
    Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Gray
    Write-Host ""

    mvn spring-boot:run -DskipTests
} else {
    Write-Host "✗ Compilation failed!" -ForegroundColor Red
    Write-Host "Please check the errors above" -ForegroundColor Red
    exit 1
}
