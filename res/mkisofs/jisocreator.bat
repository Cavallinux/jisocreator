set "JISOCREATOR_LOGS=%LOCALAPPDATA%\jisocreator\logs"
set "JAVA_HOME=%ProgramFiles%\jisocreator\jre"
if not exist "%JISOCREATOR_LOGS%" mkdir "%JISOCREATOR_LOGS%"
start /b "%JAVA_HOME%"\bin\javaw -Dpath.logs="%JISOCREATOR_LOGS%" --add-opens java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED -jar jisocreator.jar %*