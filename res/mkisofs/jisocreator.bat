set "JISOCREATOR_LOGS=%LOCALAPPDATA%\jisocreator\logs"
if not exist "%JISOCREATOR_LOGS%" mkdir "%JISOCREATOR_LOGS%"
start /b javaw -Dpath.logs="%JISOCREATOR_LOGS%" --add-opens java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -jar jisocreator.jar %*