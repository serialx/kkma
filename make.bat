SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_19

mkdir build
cd build
xcopy /E /I ..\bin\classes\org org
@REM xcopy /E /I ..\dic dic
@REM rmdir /S /Q dic\CVS dic\ecat\CVS dic\prob\CVS

"%JAVA_HOME%\bin\jar" -cvfm ..\org.snu.ids.ha.1.0.jar ..\Manifest.txt org
cd ..
rmdir /S /Q build
copy org.snu.ids.ha.1.0.jar ..\org.snu.ids.kkma\web\WEB-INF\lib\org.snu.ids.ha.1.0.jar
copy org.snu.ids.ha.1.0.jar ..\org.snu.ids.sejong\web\WEB-INF\lib\org.snu.ids.ha.1.0.jar