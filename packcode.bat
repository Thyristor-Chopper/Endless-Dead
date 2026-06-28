del /q src_packed.txt >nul 2>&1

for /r %%i in (*.kt *.java *.gradle *.kts) do (
    echo ---- file: %%i ---->>src_packed.txt
    echo(>>src_packed.txt
    type "%%i">>src_packed.txt
    echo(>>src_packed.txt
    echo(>>src_packed.txt
    echo(>>src_packed.txt
)
