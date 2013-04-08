@echo off

if "X%COG_INSTALL_PATH%" == "X" goto nocogpath
goto cogpath

:nocogpath

    echo Error: COG_INSTALL_PATH not set
    goto end

:cogpath

set COG_CMD_LINE_ARGS=

:setupArgs
if %1a==a goto doneStart
set COG_CMD_LINE_ARGS=%COG_CMD_LINE_ARGS% %1
shift
goto setupArgs

:doneStart

call "%COG_INSTALL_PATH%\etc\java.cfg.bat"

set LOCAL_OPTS=

if "X%GLOBUS_LOCATION%" == "X" goto option1
set LOCAL_OPTS=-DGLOBUS_LOCATION="%GLOBUS_LOCATION%"

:option1
if "X%X509_USER_PROXY%" == "X" goto option2
set LOCAL_OPTS=-DX509_USER_PROXY="%X509_USER_PROXY%" %LOCAL_OPTS%

:option2
if "X%GLOBUS_TCP_PORT_RANGE%" == "X" goto option3
set LOCAL_OPTS=-DGLOBUS_TCP_PORT_RANGE=%GLOBUS_TCP_PORT_RANGE% %LOCAL_OPTS%

:option3
if "X%GLOBUS_TCP_SOURCE_PORT_RANGE%" == "X" goto option4
set LOCAL_OPTS=-DGLOBUS_TCP_SOURCE_PORT_RANGE=%GLOBUS_TCP_SOURCE_PORT_RANGE% %LOCAL_OPTS%

:option4
if "%X509_CERT_DIR%" == "" goto option5
set LOCAL_OPTS=-DX509_CERT_DIR="%X509_CERT_DIR%" %LOCAL_OPTS%

:option5
if "X%GLOBUS_HOSTNAME%" == "X" goto ibmvm
set LOCAL_OPTS=-DGLOBUS_HOSTNAME=%GLOBUS_HOSTNAME% %LOCAL_OPTS%

:ibmvm
if not "X%IBM_JAVA_OPTIONS%" == "X" goto run 
set IBM_JAVA_OPTIONS=-Xquickstart

:run
set LDAP_JARS=-Xbootclasspath/p:"%COG_INSTALL_PATH%\lib\providerutil.jar";"%COG_INSTALL_PATH%\lib\ldap.jar"

java %LOCAL_OPTS% %COG_OPTS% %LDAP_JARS% -classpath %LOCALCLASSPATH% org.globus.tools.MyProxy %COG_CMD_LINE_ARGS%

:end
