@echo off
setlocal
echo =======================================================
echo   FLINDERS COFFEE - PUSH KE GITHUB REPOSITORY
echo =======================================================
echo.
set "GIT_EXE=C:\Users\RAZER\AppData\Local\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe\cmd\git.exe"

set /p REPO_URL="Masukkan URL GitHub Repository Anda (contoh: https://github.com/username/flinders-coffee.git): "

if "%REPO_URL%"=="" (
    echo Error: URL tidak boleh kosong!
    pause
    exit /b 1
)

echo.
echo -> Menghubungkan remote origin ke: %REPO_URL%
"%GIT_EXE%" remote remove origin 2>nul
"%GIT_EXE%" remote add origin %REPO_URL%
"%GIT_EXE%" branch -M main

echo -> Mengirim file ke GitHub...
"%GIT_EXE%" push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo =======================================================
    echo BERHASIL! Seluruh file project sudah terhubung ke GitHub!
    echo =======================================================
) else (
    echo.
    echo [Catatan] Jika push meminta autentikasi / gagal:
    echo 1. Pastikan repository di GitHub sudah dibuat (kosong / tanpa README).
    echo 2. Gunakan Personal Access Token (PAT) jika login dengan password ditolak.
)

echo.
pause
