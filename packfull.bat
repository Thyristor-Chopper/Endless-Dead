@echo off

del /q src_packed.txt >nul 2>&1

for /r %%i in (*.kt *.java *.gradle *.kts *.cmd *.sh *.bat *.gitmodules *.gitignore *.md *.txt) do (
    if /i not "%%~nxi"=="gradlew.bat" (
		if /i not "%%~nxi"=="src_packed.txt" (
			echo Packing: %%i
			echo ---- file: %%i ---->>src_packed.txt
			echo(>>src_packed.txt
			type "%%i">>src_packed.txt
			echo(>>src_packed.txt
			echo(>>src_packed.txt
			echo(>>src_packed.txt
		)
    )
)

echo(
echo Saved to src_packed.txt.
pause
