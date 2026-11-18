# Script Sinkronisasi Otomatis Semua Komponen Flinders Coffee ke Git Repo
Write-Host ">>> Memulai sinkronisasi file project ke Git Repository..." -ForegroundColor Cyan

# 1. Sync Android App
Write-Host "-> Menyinkronkan Android App..." -ForegroundColor Yellow
robocopy "C:\Users\RAZER\AndroidStudioProjects\flinders\flinders" "$PSScriptRoot\android-app" /E /XD .gradle .idea build .kotlin /XF local.properties /NJH /NJS /NDL /NC /NS

# 2. Sync Web POS
Write-Host "-> Menyinkronkan Web POS Kasir..." -ForegroundColor Yellow
Copy-Item "C:\Users\RAZER\Documents\flinders-pos\*" "$PSScriptRoot\web-pos\" -Recurse -Force

# 3. Sync Backend API & Database
Write-Host "-> Menyinkronkan REST API & MySQL Database..." -ForegroundColor Yellow
Copy-Item "C:\xampp\htdocs\flinders-api\api.php" "$PSScriptRoot\backend-api\" -Force
Copy-Item "C:\xampp\htdocs\flinders-api\database.sql" "$PSScriptRoot\backend-api\" -Force
Copy-Item "C:\xampp\htdocs\flinders-api\Flinders_Coffee_API.postman_collection.json" "$PSScriptRoot\postman\" -Force

# 4. Cek Git Status
Write-Host "-> Status Git saat ini:" -ForegroundColor Green
$git = "C:\Users\RAZER\AppData\Local\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\cmd\git.exe"
if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    & $git -C $PSScriptRoot status
} else {
    git -C $PSScriptRoot status
}

Write-Host "`n>>> Selesai! File project berhasil disinkronkan." -ForegroundColor Cyan
Write-Host "Untuk mengirim perubahan ke GitHub, jalankan:" -ForegroundColor White
Write-Host "git add . ; git commit -m 'update pesan' ; git push" -ForegroundColor Gray
