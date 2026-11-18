@echo off
setlocal
title Push Flinders Coffee ke GitHub
echo ================================================================
echo   FLINDERS COFFEE - PUSH KE GITHUB REPOSITORY
echo   Target: https://github.com/hanifatkj/FlindersCoffee
echo ================================================================
echo.

set "GIT_DIR=C:\Users\RAZER\AppData\Local\Microsoft\WinGet\Packages\Git.MinGit_Microsoft.Winget.Source_8wekyb3d8bbwe"
set "PATH=%GIT_DIR%\cmd;%GIT_DIR%\ucrt64\bin;%PATH%"

cd /d "%~dp0"

echo [1/3] Memeriksa status Git lokal...
git status --short

echo.
echo [2/3] Memastikan remote origin terhubung...
git remote remove origin 2>nul
git remote add origin https://github.com/hanifatkj/FlindersCoffee.git
git remote -v

echo.
echo [3/3] Mengirim (push) commit ke GitHub branch 'main'...
echo (Jendela browser atau popup GitHub mungkin akan muncul untuk otorisasi login)
echo.

git push -u origin main

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================================
    echo   BERHASIL! Seluruh proyek Flinders Coffee telah terunggah ke:
    echo   https://github.com/hanifatkj/FlindersCoffee
    echo ================================================================
) else (
    echo.
    echo ================================================================
    echo   [PERHATIAN] Push belum selesai.
    echo   Jika diminta autentikasi:
    echo   1. Klik 'Sign in with your browser' saat jendela GitHub muncul.
    echo   2. Atau gunakan Personal Access Token (PAT) sebagai password.
    echo ================================================================
)

echo.
pause
