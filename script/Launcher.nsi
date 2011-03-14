Name "${PRODUCT}"
Icon "${DIR_FILES}\${FILE_ICON}"
OutFile "${PATH_OUT}"

!include "Version.nsh"

RequestExecutionLevel user
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow
SetCompress force
SetCompressor /SOLID /FINAL lzma
 
!include "FileFunc.nsh"
!insertmacro GetFileVersion
!insertmacro GetParameters
!include "WordFunc.nsh"
!insertmacro VersionCompare
 
Section ""
  Call CheckJRE
  Pop $R0
 
  ${GetParameters} $1
  StrCpy $0 '"$R0" -jar ${FILE_JAR} $1'
 
  SetOutPath $EXEDIR
  Exec $0
SectionEnd
 
Function CheckJRE
    Push $R0
    Push $R1
    Push $2
    
  CheckLocal:
    ClearErrors
    StrCpy $R0 "$EXEDIR\jre\bin\${FILE_JAVA}"
    IfFileExists $R0 JreFound
 
  CheckJavaHome:
    ClearErrors
    ReadEnvStr $R0 "JAVA_HOME"
    StrCpy $R0 "$R0\bin\${FILE_JAVA}"
    IfErrors CheckRegistry     
    IfFileExists $R0 0 CheckRegistry
    Call CheckJREVersion
    IfErrors CheckRegistry JreFound
 
  CheckRegistry:
    ClearErrors
    ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
    StrCpy $R0 "$R0\bin\${FILE_JAVA}"
    IfErrors DownloadJRE
    IfFileExists $R0 0 DownloadJRE
    Call CheckJREVersion
    IfErrors DownloadJRE JreFound
 
  DownloadJRE:
    MessageBox MB_YESNO|MB_ICONQUESTION "${PRODUCT} requires Java ${JAVA_VERSION} or later to run.$\n\
    Would you like to visit official Java download site?" IDNO GoodLuck
    ExecShell "open" "${JAVA_URL}"
    Abort
 
  GoodLuck:
    StrCpy $R0 "${FILE_JAVA}"
    MessageBox MB_ICONSTOP "Java not found, program aborted.$\nPlease provide Java ${JAVA_VERSION} or later."
    Abort
 
  JreFound:
    Pop $2
    Pop $R1
    Exch $R0
FunctionEnd
 
; Pass the "javaw.exe" path by $R0
Function CheckJREVersion
    Push $R1
 
    ; Get the file version of javaw.exe
    ${GetFileVersion} $R0 $R1
;    MessageBox MB_ICONSTOP $R1
    ${VersionCompare} ${JAVA_NUMBER} $R1 $R1
 
    ; Check whether $R1 != "1"
    ClearErrors
    StrCmp $R1 "1" 0 CheckDone
    SetErrors
 
  CheckDone:
    Pop $R1
FunctionEnd
 